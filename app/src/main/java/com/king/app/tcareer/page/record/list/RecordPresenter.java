package com.king.app.tcareer.page.record.list;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.ScoreDao;
import com.king.app.tcareer.model.db.entity.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * <p/>创建时间: 2017/4/21 17:05
 */
public class RecordPresenter extends BasePresenter<IRecordView> {

    private List<Record> recordList;

    @Override
    protected void onCreate() {

    }

    /**
     * 按照当前的recordList组装3级数据
     * @param recordList
     */
    public void loadRecordDatas(ArrayList<Record> recordList) {
        this.recordList = recordList;
        queryRecords()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RecordPageData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(RecordPageData recordPageData) {
                        view.onRecordDataLoaded(recordPageData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Load records failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 加载全部的record，并组装3级数据
     */
    public void loadRecordDatas(long userId) {
        recordList = null;
        view.showLoading();
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<RecordPageData>>() {
                    @Override
                    public ObservableSource<RecordPageData> apply(User user) throws Exception {
                        view.postShowUser();
                        return queryRecords();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RecordPageData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(RecordPageData recordPageData) {
                        view.dismissLoading();
                        view.onRecordDataLoaded(recordPageData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Load records failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * query all records desc for user
     * @return
     */
    private Observable<RecordPageData> queryRecords() {
        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                if (recordList == null) {
                    recordList = TApplication.getInstance().getDaoSession().getRecordDao()
                            .queryBuilder()
                            .where(RecordDao.Properties.UserId.eq(mUser.getId()))
                            .build().list();
                    Collections.reverse(recordList);
                }
                e.onNext(recordList);
            }
        }).flatMap(new Function<List<Record>, ObservableSource<RecordPageData>>() {
            @Override
            public ObservableSource<RecordPageData> apply(List<Record> records) throws Exception {
                return parseRecords();
            }
        });
    }

    /**
     * parse records to data matched for views
     * @return
     */
    private Observable parseRecords() {
        return Observable.create(new ObservableOnSubscribe<RecordPageData>() {
            @Override
            public void subscribe(ObservableEmitter<RecordPageData> e) throws Exception {
                RecordPageData headerList = createHeaderList();
                e.onNext(headerList);
            }
        });
    }

    private RecordPageData createHeaderList() {
        RecordPageData data = new RecordPageData();

        data.setRecordList(recordList);

        List<YearItem> yearList = new ArrayList<>();
        data.setYearList(yearList);
        Map<String, YearItem> yearMap = new HashMap<>();
        Map<String, HeaderItem> headerMap = new HashMap<>();

        int nCareer = 0;
        int nCareerWin = 0;
        int nYear = 0;
        int nYearWin = 0;
        for (int i = 0; i < recordList.size(); i ++) {
            Record record = recordList.get(i);
            // format Year and Header and Item
            String keyYear = record.getDateStr().split("-")[0];
            YearItem yearItem = yearMap.get(keyYear);
            if (yearItem == null) {
                yearItem = new YearItem();
                yearItem.setYear(keyYear);
                yearItem.setChildItemList(new ArrayList<HeaderItem>());
                yearMap.put(keyYear, yearItem);
                yearList.add(yearItem);
            }
            List<HeaderItem> headerList = yearItem.getChildItemList();

            String keyHeader = record.getMatch() + "-" + record.getDateStr();
            HeaderItem item = headerMap.get(keyHeader);
            if (item == null) {
                item = new HeaderItem();
                item.setRecord(record);
                item.setChildItemList(new ArrayList<RecordItem>());
                headerMap.put(keyHeader, item);
                headerList.add(item);
            }
            RecordItem rItem = new RecordItem();
            rItem.setRecord(record);
            item.getChildItemList().add(rItem);

            int year = Calendar.getInstance().get(Calendar.YEAR);
            // count win lose
            // W/0不算作胜负场
            if (AppConstants.RETIRE_WO != record.getRetireFlag()) {
                nCareer ++;
                if (AppConstants.WINNER_USER == record.getWinnerFlag()) {
                    nCareerWin ++;
                    yearItem.setWin(yearItem.getWin() + 1);
                }
                else {
                    yearItem.setLose(yearItem.getLose() + 1);
                }

                if (year == Integer.parseInt(record.getDateStr().split("-")[0])) {
                    nYear ++;
                    if (AppConstants.WINNER_USER == record.getWinnerFlag()) {
                        nYearWin ++;
                    }
                }
            }
        }
        data.setCareerWin(nCareerWin);
        data.setCareerLose(nCareer - nCareerWin);
        data.setYearWin(nYearWin);
        data.setYearLose(nYear - nYearWin);
        DecimalFormat format = new DecimalFormat("##0.0");
        data.setCareerRate(format.format((float) nCareerWin/ (float) nCareer * 100) + "%");
        data.setYearRate(format.format((float) nYearWin/ (float) nYear * 100) + "%");
        return  data;
    }

    public void delete(final Record record, final int viewPosition) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                // control in transaction
                TApplication.getInstance().getDaoSession().runInTx(new Runnable() {
                    @Override
                    public void run() {
                        // delete from match_records
                        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
                        dao.queryBuilder()
                                .where(RecordDao.Properties.Id.eq(record.getId()))
                                .buildDelete()
                                .executeDeleteWithoutDetachingEntities();
                        // delete from scores
                        ScoreDao scoreDao = TApplication.getInstance().getDaoSession().getScoreDao();
                        scoreDao.queryBuilder()
                                .where(ScoreDao.Properties.RecordId.eq(record.getId()))
                                .buildDelete()
                                .executeDeleteWithoutDetachingEntities();
                    }
                });
                e.onNext(new Object());
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object object) {
                        view.deleteSuccess(viewPosition);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Delete record failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
