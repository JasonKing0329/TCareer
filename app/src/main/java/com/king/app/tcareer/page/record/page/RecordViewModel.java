package com.king.app.tcareer.page.record.page;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.bean.RecordWinFlagBean;
import com.king.app.tcareer.model.dao.H2HDao;
import com.king.app.tcareer.model.dao.RecordExtendDao;
import com.king.app.tcareer.model.db.entity.MatchBean;
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
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/22 13:38
 */
public class RecordViewModel extends BaseViewModel {

    public ObservableField<String> matchNameText = new ObservableField<>();
    public ObservableField<String> matchPlaceText = new ObservableField<>();
    public ObservableField<String> matchCourtText = new ObservableField<>();
    public ObservableField<String> matchLevelText = new ObservableField<>();
    public ObservableField<String> matchDateText = new ObservableField<>();
    public ObservableField<String> recordRoundText = new ObservableField<>();
    public ObservableField<String> recordScoreText = new ObservableField<>();
    public ObservableField<String> levelCountText = new ObservableField<>();
    public ObservableField<String> courtCountText = new ObservableField<>();

    public ObservableField<String> userText = new ObservableField<>();
    public ObservableField<String> competitorText = new ObservableField<>();
    public ObservableField<String> userImageUrl = new ObservableField<>();
    public ObservableField<String> competitorImageUrl = new ObservableField<>();
    public ObservableField<String> setScoreText = new ObservableField<>();
    public ObservableField<String> h2hText = new ObservableField<>();

    public MutableLiveData<String> matchImageUrl = new MutableLiveData<>();

    public MutableLiveData<List<Record>> recordsObserver = new MutableLiveData<>();

    private ViewProvider viewProvider;

    private Record mRecord;

    private Palette.Swatch mSwatch;

    private PaletteResponse mPaletteResponse;

    public RecordViewModel(@NonNull Application application) {
        super(application);
    }

    public Record getRecord() {
        return mRecord;
    }

    public void setViewProvider(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    public void loadRecord(long recordId) {
        loadingObserver.setValue(true);
        queryRecord(recordId)
                .flatMap(record -> {
                    mRecord = record;
                    mUser = record.getUser();
                    updateRecordContent(record);
                    return queryMatchRecords(record);
                })
                .flatMap(records -> {
                    recordsObserver.postValue(records);
                    return queryDetails();
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
                        loadingObserver.setValue(false);
                        setScoreText.set(details.scoreSet);
                        h2hText.set(details.h2h);
                        levelCountText.set(details.levelStr);
                        courtCountText.set(details.courtStr);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("Load error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void updateRecordContent(Record record) {
        MatchBean match = record.getMatch().getMatchBean();
        matchNameText.set(record.getMatch().getName());
        matchPlaceText.set(match.getCountry() + "/" + match.getCity());
        matchCourtText.set(match.getCourt());
        matchLevelText.set(match.getLevel());
        matchDateText.set(record.getDateStr());
        recordRoundText.set(record.getRound());
        recordScoreText.set(ScoreParser.getScoreText(record.getScoreList(), record.getRetireFlag()));
        matchImageUrl.postValue(ImageProvider.getMatchHeadPath(record.getMatch().getName(), match.getCourt()));

        StringBuffer buffer = new StringBuffer();
        buffer.append(record.getUser().getNameChn()).append("(").append(record.getRank());
        if (record.getSeed() > 0) {
            buffer.append("/").append(record.getSeed());
        }
        buffer.append(")");
        userText.set(buffer.toString());
        userImageUrl.set(ImageProvider.getPlayerHeadPath(record.getUser().getNameChn()));

        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);
        buffer = new StringBuffer();
        buffer.append(competitor.getNameChn()).append("(").append(record.getRankCpt());
        if (record.getSeedpCpt() > 0) {
            buffer.append("/").append(record.getSeedpCpt());
        }
        buffer.append(")");
        competitorText.set(buffer.toString());
        competitorImageUrl.set(ImageProvider.getPlayerHeadPath(competitor.getNameChn()));
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

    private Observable<List<Record>> queryMatchRecords(Record record) {
        return Observable.create(e -> {
            RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
            List<Record> list = dao.queryBuilder()
                    .where(RecordDao.Properties.MatchNameId.eq(record.getMatchNameId())
                        , RecordDao.Properties.DateLong.eq(record.getDateLong())
                        , RecordDao.Properties.UserId.eq(record.getUserId()))
                    .build().list();
            // 最近的排在前面
            Collections.reverse(list);

            for (Record rc:list) {
                String name = CompetitorParser.getCompetitorFrom(rc).getNameChn();
                rc.setImageUrl(ImageProvider.getPlayerHeadPath(name));
            }
            e.onNext(list);
        });
    }

    private class Details {
        String scoreSet;
        String levelStr;
        String courtStr;
        String h2h;
    }

    private Observable<Details> queryDetails() {
        return Observable.create(e -> {

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
        viewProvider.getCollapsingToolbar().setContentScrimColor(swatch.getRgb());
        viewProvider.getCollapsingToolbar().setCollapsedTitleTextColor(swatch.getTitleTextColor());
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
            viewProvider.getToolbar().getNavigationIcon().setColorFilter(mSwatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
            // 替换原图颜色
            viewProvider.getEditMenuItem().getIcon().setColorFilter(mSwatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
        }
        // 展开状态运用图片区域颜色分析法得到的颜色
        else {
            if (!ListUtil.isEmpty(mPaletteResponse.viewColorBounds)) {
                for (ViewColorBound bound:mPaletteResponse.viewColorBounds) {
                    if (bound.view == viewProvider.getToolbar()) {
                        viewProvider.getToolbar().getNavigationIcon().setColorFilter(bound.color, PorterDuff.Mode.SRC_IN);
                    }
                    else if (bound.view == viewProvider.getEditMenuItem().getActionView()) {
                        // 替换原图颜色
                        viewProvider.getEditMenuItem().getIcon().setColorFilter(bound.color, PorterDuff.Mode.SRC_IN);
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
