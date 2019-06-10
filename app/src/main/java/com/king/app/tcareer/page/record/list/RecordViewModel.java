package com.king.app.tcareer.page.record.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.EarlierAchieve;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.ScoreDao;
import com.king.app.tcareer.utils.ListUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/21 17:05
 */
public class RecordViewModel extends BaseViewModel {

    public ObservableField<String> userNameText = new ObservableField<>();
    public ObservableInt careerRateVisibility = new ObservableInt();
    public ObservableField<String> careerRateText = new ObservableField<>();
    public ObservableField<String> careerWinLoseText = new ObservableField<>();
    public ObservableInt yearRateVisibility = new ObservableInt();
    public ObservableField<String> yearRateText = new ObservableField<>();
    public ObservableField<String> yearText = new ObservableField<>();
    public ObservableField<String> yearWinLoseText = new ObservableField<>();
    public ObservableField<String> matchImageUrl = new ObservableField<>();

    public MutableLiveData<List<YearItem>> listObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> deletePosition = new MutableLiveData<>();

    private List<Record> recordList;

    public RecordViewModel(@NonNull Application application) {
        super(application);
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    /**
     * 按照当前的recordList组装3级数据
     * @param recordList
     */
    public void loadRecordData(List<Record> recordList) {
        this.recordList = recordList;
        parseRecords()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RecordPageData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(RecordPageData recordPageData) {
                        bindContent(recordPageData);
                        listObserver.setValue(recordPageData.getYearList());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load records failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void bindContent(RecordPageData data) {
        if (TextUtils.isEmpty(data.getCareerRate())) {
            careerRateVisibility.set(View.GONE);
        }
        else {
            careerRateVisibility.set(View.VISIBLE);
            careerRateText.set(data.getCareerRate());
        }
        careerWinLoseText.set("Win " + data.getCareerWin() + "   Lose " + data.getCareerLose());
        yearText.set(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        if (TextUtils.isEmpty(data.getYearRate())) {
            yearRateVisibility.set(View.GONE);
        }
        else {
            yearRateVisibility.set(View.VISIBLE);
            yearRateText.set(data.getYearRate());
        }
        yearWinLoseText.set("Win " + data.getYearWin() + "   Lose " + data.getYearLose());

        if (!ListUtil.isEmpty(data.getYearList())) {
            Record record = data.getYearList().get(0).getChildItemList().get(0).getRecord();
            String path = ImageProvider.getMatchHeadPath(record.getMatch().getName(), record.getMatch().getMatchBean().getCourt());
            matchImageUrl.set(path);
        }
    }

    /**
     * 加载全部的record，并组装3级数据
     */
    public void loadRecordData(long userId) {
        recordList = null;
        loadingObserver.setValue(true);
        queryUser(userId)
                .flatMap(user -> {
                    userNameText.set(user.getNameEng());
                    return queryRecords();
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
                        loadingObserver.setValue(false);
                        bindContent(recordPageData);
                        listObserver.setValue(recordPageData.getYearList());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("Load records failed: " + e.getMessage());
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
        return Observable.create((ObservableOnSubscribe<List<Record>>) e -> {
            if (recordList == null) {
                recordList = TApplication.getInstance().getDaoSession().getRecordDao()
                        .queryBuilder()
                        .where(RecordDao.Properties.UserId.eq(mUser.getId()))
                        .build().list();
                Collections.reverse(recordList);
            }
            e.onNext(recordList);
        }).flatMap(records -> parseRecords());
    }

    /**
     * parse records to data matched for views
     * @return
     */
    private Observable parseRecords() {
        return Observable.create((ObservableOnSubscribe<RecordPageData>) e -> {
            RecordPageData headerList = createHeaderList();
            // year默认展开
            List<YearItem> yearList = headerList.getYearList();
            if (yearList != null) {
                for (YearItem item:yearList) {
                    item.setExpanded(true);
                }
            }
            e.onNext(headerList);
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

            String keyHeader = record.getMatch().getName() + "-" + record.getDateStr();
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
        int earlierWin = 0;
        int earlierLose = 0;
        for (EarlierAchieve achieve:mUser.getEarlierAchieves()) {
            earlierWin += achieve.getWin();
            earlierLose += achieve.getLose();
        }
        data.setCareerWin(nCareerWin + earlierWin);
        data.setCareerLose(nCareer - nCareerWin + earlierLose);
        data.setYearWin(nYearWin);
        data.setYearLose(nYear - nYearWin);
        DecimalFormat format = new DecimalFormat("##0.0");
        if (nCareer > 0) {
            data.setCareerRate(format.format((float) nCareerWin/ (float) nCareer * 100) + "%");
        }
        if (nYear > 0) {
            data.setYearRate(format.format((float) nYearWin/ (float) nYear * 100) + "%");
        }
        return  data;
    }

    public void delete(Record record, int viewPosition) {
        Observable.create(e -> {
            // control in transaction
            TApplication.getInstance().getDaoSession().runInTx(() -> {
                try {
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
                } catch (Exception exp) {
                    exp.printStackTrace();
                } finally {
                    e.onNext(new Object());
                }
            });
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object object) {
                        deletePosition.setValue(viewPosition);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Delete record failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
