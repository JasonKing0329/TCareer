package com.king.app.tcareer.page.record.page;

import android.graphics.PorterDuff;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.bean.RecordWinFlagBean;
import com.king.app.tcareer.model.dao.H2HDao;
import com.king.app.tcareer.model.dao.RecordExtendDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.Score;
import com.king.app.tcareer.model.palette.PaletteResponse;
import com.king.app.tcareer.model.palette.ViewColorBound;
import com.king.app.tcareer.utils.ListUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/22 13:38
 */
public class RecordPagePresenter extends BasePresenter<RecordPageView> {

    private Record mRecord;

    private Palette.Swatch mSwatch;

    private PaletteResponse mPaletteResponse;

    @Override
    protected void onCreate() {

    }

    public Record getRecord() {
        return mRecord;
    }

    public void loadRecord(long recordId) {
        view.showLoading();
        queryRecord(recordId)
                .flatMap(new Function<Record, ObservableSource<List<Record>>>() {
                    @Override
                    public ObservableSource<List<Record>> apply(Record record) throws Exception {
                        mRecord = record;
                        mUser = record.getUser();
                        view.postShowRecord(record);
                        return queryMatchRecords(record);
                    }
                })
                .flatMap(new Function<List<Record>, ObservableSource<Details>>() {
                    @Override
                    public ObservableSource<Details> apply(List<Record> records) throws Exception {
                        view.postShowMatchRecords(records);
                        return queryDetails();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Details>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Details details) {
                        view.dismissLoading();
                        view.showDetails(details.scoreSet, details.levelStr, details.courtStr, details.h2h);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Load error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Record> queryRecord(final long recordId) {
        return Observable.create(new ObservableOnSubscribe<Record>() {
            @Override
            public void subscribe(ObservableEmitter<Record> e) throws Exception {
                RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
                Record record = dao.queryBuilder()
                        .where(RecordDao.Properties.Id.eq(recordId))
                        .build().unique();
                record.getUser();
                record.getCompetitor();
                record.getMatch().getMatchBean();
                record.getScoreList();
                e.onNext(record);
            }
        });
    }

    private Observable<List<Record>> queryMatchRecords(final Record record) {
        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
                List<Record> list = dao.queryBuilder()
                        .where(RecordDao.Properties.MatchNameId.eq(record.getMatchNameId())
                            , RecordDao.Properties.DateLong.eq(record.getDateLong())
                            , RecordDao.Properties.UserId.eq(record.getUserId()))
                        .build().list();
                // 最近的排在前面
                Collections.reverse(list);
                e.onNext(list);
            }
        });
    }

    private class Details {
        String scoreSet;
        String levelStr;
        String courtStr;
        String h2h;
    }

    private Observable<Details> queryDetails() {
        return Observable.create(new ObservableOnSubscribe<Details>() {
            @Override
            public void subscribe(ObservableEmitter<Details> e) throws Exception {

                Details details = new Details();
                // 盘分
                List<Score> scores = mRecord.getScoreList();
                int win = 0, lose = 0;
                for (Score score:scores) {
                    if (score.getUserPoint() > score.getCompetitorPoint()) {
                        win ++;
                    }
                    else {
                        lose ++;
                    }
                }
                details.scoreSet = win + "：" + lose;

                boolean isWin = mRecord.getWinnerFlag() == AppConstants.WINNER_USER;
                // 级别胜绩
                RecordExtendDao extendDao = new RecordExtendDao();
                List<RecordWinFlagBean> flagList = extendDao.queryRecordWinnerFlagsByLevel(
                        mRecord.getUserId(), mRecord.getMatch().getMatchBean().getLevel(), false);
                int careerIndex = 0;
                for (int i = 0; i < flagList.size(); i ++) {
                    if (mRecord.getWinnerFlag() == flagList.get(i).getWinnerFlag()) {
                        careerIndex ++;
                    }
                    if (flagList.get(i).getRecordId() == mRecord.getId()) {
                        break;
                    }
                }
                StringBuffer buffer = new StringBuffer();
                buffer.append("生涯第").append(careerIndex)
                        .append(isWin ? "胜":"败");
                int year = Integer.parseInt(mRecord.getDateStr().split("-")[0]);
                if (year == Calendar.getInstance().get(Calendar.YEAR)) {
                    int yearIndex = 0;
                    flagList = extendDao.queryRecordWinnerFlagsByLevel(
                            mRecord.getUserId(), mRecord.getMatch().getMatchBean().getLevel(), true);
                    for (int i = 0; i < flagList.size(); i ++) {
                        if (mRecord.getWinnerFlag() == flagList.get(i).getWinnerFlag()) {
                            yearIndex ++;
                        }
                        if (flagList.get(i).getRecordId() == mRecord.getId()) {
                            break;
                        }
                    }
                    buffer.append("，赛季第").append(yearIndex)
                            .append(isWin ? "胜":"败");
                }
                details.levelStr = buffer.toString();

                // 场地胜绩
                flagList = extendDao.queryRecordWinnerFlagsByCourt(
                        mRecord.getUserId(), mRecord.getMatch().getMatchBean().getCourt(), false);
                careerIndex = 0;
                for (int i = 0; i < flagList.size(); i ++) {
                    if (mRecord.getWinnerFlag() == flagList.get(i).getWinnerFlag()) {
                        careerIndex ++;
                    }
                    if (flagList.get(i).getRecordId() == mRecord.getId()) {
                        break;
                    }
                }
                buffer = new StringBuffer();
                buffer.append("生涯第").append(careerIndex)
                        .append(isWin ? "胜":"败");
                if (year == Calendar.getInstance().get(Calendar.YEAR)) {
                    int yearIndex = 0;
                    flagList = extendDao.queryRecordWinnerFlagsByCourt(
                            mRecord.getUserId(), mRecord.getMatch().getMatchBean().getCourt(), true);
                    for (int i = 0; i < flagList.size(); i ++) {
                        if (mRecord.getWinnerFlag() == flagList.get(i).getWinnerFlag()) {
                            yearIndex ++;
                        }
                        if (flagList.get(i).getRecordId() == mRecord.getId()) {
                            break;
                        }
                    }
                    buffer.append("，赛季第").append(yearIndex)
                            .append(isWin ? "胜":"败");
                }
                details.courtStr = buffer.toString();

                H2HDao dao = new H2HDao();
                H2hBean bean = dao.getH2h(mRecord.getUserId(), mRecord.getPlayerId(), mRecord.getPlayerFlag() == AppConstants.COMPETITOR_VIRTUAL);
                details.h2h = "H2H(" + bean.getWin() + " - " + bean.getLose() + ")";

                e.onNext(details);
            }
        });
    }

    /**
     * 根据背景图片为各个UI空间设置合适的颜色
     * @param response
     */
    public void handlePalette(final PaletteResponse response) {
        mPaletteResponse = response;
        if (response != null && response.palette != null) {
            mSwatch = getTitlebarSwatch(response.palette);
            dispatchTitleBar(mSwatch);
        }
    }

    private void dispatchTitleBar(Palette.Swatch swatch) {
        // 修改titlebar的主色调（背景，文字颜色，图标颜色）
        view.getCollapsingToolbar().setContentScrimColor(swatch.getRgb());
        view.getCollapsingToolbar().setCollapsedTitleTextColor(swatch.getTitleTextColor());
        // toolbar上的图标需要根据展开/折叠状态确定颜色，初始是展开状态
        handleCollapseScrimChanged(false);
    }

    /**
     * 处理appBar滑动过程scrim的出现和消失引起的图标颜色变化
     * @param isCollapsing
     */
    public void handleCollapseScrimChanged(boolean isCollapsing) {
        // 折叠状态运用main swatch的getTitleTextColor
        if (isCollapsing) {
            // PorterDuff.Mode作用参考https://www.jianshu.com/p/d11892bbe055
            // 这里用SRC_IN，第一个参数是source，表示source与原图像叠加后相交的部分运用source的颜色，如果是SRC_TOP则是叠加的情况，SRC则是source color充满整个view区域
            view.getToolbar().getNavigationIcon().setColorFilter(mSwatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
            // 替换原图颜色
            view.getEditMenuItem().getIcon().setColorFilter(mSwatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
        }
        // 展开状态运用图片区域颜色分析法得到的颜色
        else {
            if (!ListUtil.isEmpty(mPaletteResponse.viewColorBounds)) {
                for (ViewColorBound bound:mPaletteResponse.viewColorBounds) {
                    if (bound.view == view.getToolbar()) {
                        view.getToolbar().getNavigationIcon().setColorFilter(bound.color, PorterDuff.Mode.SRC_IN);
                    }
                    else if (bound.view == view.getEditMenuItem().getActionView()) {
                        // 替换原图颜色
                        view.getEditMenuItem().getIcon().setColorFilter(bound.color, PorterDuff.Mode.SRC_IN);
                    }
                }
            }
        }
    }

    /**
     * title bar运用的颜色
     * vibrant优先，其次muted，再其次任意
     * @param palette
     * @return
     */
    public Palette.Swatch getTitlebarSwatch(Palette palette) {
        if (palette == null) {
            return null;
        }
        Palette.Swatch swatch = palette.getVibrantSwatch();
        if (swatch == null) {
            swatch = palette.getMutedSwatch();
            if (swatch == null) {
                List<Palette.Swatch> swatches = palette.getSwatches();
                if (!ListUtil.isEmpty(swatches)) {
                    swatch = swatches.get(0);
                }
            }
        }
        return swatch;
    }

}
