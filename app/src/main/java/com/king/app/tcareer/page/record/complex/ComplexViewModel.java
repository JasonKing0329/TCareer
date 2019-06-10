package com.king.app.tcareer.page.record.complex;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.ScoreDao;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/2 14:23
 */
public class ComplexViewModel extends BaseViewModel {

    public ObservableField<String> matchImageUrl = new ObservableField<>();
    public MutableLiveData<List<YearItem>> listObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> deletePosition = new MutableLiveData<>();

    public ComplexViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadRecords() {
        loadingObserver.setValue(true);
        queryHead()
                .flatMap(record -> {
                    String path = ImageProvider.getMatchHeadPath(record.getMatch().getName(), record.getMatch().getMatchBean().getCourt());
                    matchImageUrl.set(path);
                    return queryRecords();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<YearItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<YearItem> yearItems) {
                        loadingObserver.setValue(false);
                        listObserver.setValue(yearItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("Load items error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Record> queryHead() {
        return Observable.create(e -> {
            RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
            Record record = dao.queryBuilder().orderDesc(RecordDao.Properties.Id).limit(1).build().unique();
            e.onNext(record);
        });
    }
    /**
     * query all records desc for user
     * @return
     */
    private Observable<List<YearItem>> queryRecords() {
        return Observable.create(e -> {
            RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
            Query<Record> query = dao.queryRawCreate(
                    " JOIN match_names mn ON T.match_name_id = mn._id\n" +
                            " JOIN matches m ON mn.match_id = m._id\n" +
                            " GROUP BY T.match_name_id, T.date_str\n" +
                            " ORDER BY T.date_str DESC, m.week DESC"
                , new Object[0]);
            List<Record> recordList = query.list();

            List<YearItem> yearList = new ArrayList<>();
            Map<String, YearItem> yearMap = new HashMap<>();
            Map<String, HeaderItem> headerMap = new HashMap<>();
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
                    headerMap.put(keyHeader, item);
                    headerList.add(item);
                }
            }
            e.onNext(yearList);
        });
    }

    public void delete(final Record record, final int viewPosition) {
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
                } catch (Exception exc) {
                    exc.printStackTrace();
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
