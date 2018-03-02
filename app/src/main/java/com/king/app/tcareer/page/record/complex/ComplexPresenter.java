package com.king.app.tcareer.page.record.complex;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
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
 * <p/>创建时间: 2018/3/2 14:23
 */
public class ComplexPresenter extends BasePresenter<ComplexView> {

    @Override
    protected void onCreate() {

    }

    public void loadRecords() {
        view.showLoading();
        queryHead()
                .flatMap(new Function<Record, ObservableSource<List<YearItem>>>() {
                    @Override
                    public ObservableSource<List<YearItem>> apply(Record record) throws Exception {
                        view.postShowHeadRecord(record);
                        return queryRecords();
                    }
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
                        view.dismissLoading();
                        view.showItems(yearItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Load items error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Record> queryHead() {
        return Observable.create(new ObservableOnSubscribe<Record>() {
            @Override
            public void subscribe(ObservableEmitter<Record> e) throws Exception {
                RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
                Record record = dao.queryBuilder().orderDesc(RecordDao.Properties.Id).limit(1).build().unique();
                e.onNext(record);
            }
        });
    }
    /**
     * query all records desc for user
     * @return
     */
    private Observable<List<YearItem>> queryRecords() {
        return Observable.create(new ObservableOnSubscribe<List<YearItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<YearItem>> e) throws Exception {
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
            }
        });
    }
}
