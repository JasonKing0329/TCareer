package com.king.app.tcareer.page.record.page;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.bean.RecordWinFlagBean;
import com.king.app.tcareer.model.dao.RecordExtendDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.Score;

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
                        view.showDetails(details.scoreSet, details.levelStr, details.courtStr);
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

                // 级别胜绩
                RecordExtendDao extendDao = new RecordExtendDao();
                List<RecordWinFlagBean> flagList = extendDao.queryRecordWinnerFlagsByLevel(
                        mRecord.getUserId(), mRecord.getMatch().getMatchBean().getLevel(), false);
                int careerIndex = 0;
                for (int i = 0; i < flagList.size(); i ++) {
                    if (flagList.get(i).getRecordId() == mRecord.getId()) {
                        careerIndex = i + 1;
                        break;
                    }
                }
                int yearIndex = 0;
                flagList = extendDao.queryRecordWinnerFlagsByLevel(
                        mRecord.getUserId(), mRecord.getMatch().getMatchBean().getLevel(), true);
                for (int i = 0; i < flagList.size(); i ++) {
                    if (flagList.get(i).getRecordId() == mRecord.getId()) {
                        yearIndex = i + 1;
                        break;
                    }
                }
                details.levelStr = "生涯第" + careerIndex + "胜，赛季第" + yearIndex + "胜";

                // 场地胜绩
                flagList = extendDao.queryRecordWinnerFlagsByCourt(
                        mRecord.getUserId(), mRecord.getMatch().getMatchBean().getCourt(), false);
                careerIndex = 0;
                for (int i = 0; i < flagList.size(); i ++) {
                    if (flagList.get(i).getRecordId() == mRecord.getId()) {
                        careerIndex = i + 1;
                        break;
                    }
                }
                yearIndex = 0;
                flagList = extendDao.queryRecordWinnerFlagsByCourt(
                        mRecord.getUserId(), mRecord.getMatch().getMatchBean().getCourt(), true);
                for (int i = 0; i < flagList.size(); i ++) {
                    if (flagList.get(i).getRecordId() == mRecord.getId()) {
                        yearIndex = i + 1;
                        break;
                    }
                }
                details.courtStr = "生涯第" + careerIndex + "胜，赛季第" + yearIndex + "胜";

                e.onNext(details);
            }
        });
    }

}
