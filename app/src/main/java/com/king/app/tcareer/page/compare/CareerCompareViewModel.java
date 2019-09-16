package com.king.app.tcareer.page.compare;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.model.bean.LineChartData;
import com.king.app.tcareer.model.dao.CareerCompareDao;
import com.king.app.tcareer.repository.RankRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/20 13:49
 */
public class CareerCompareViewModel extends BaseViewModel {

    public MutableLiveData<List<CompareItem>> compareItemsObserver = new MutableLiveData<>();
    public MutableLiveData<LineChartData> chartObserver = new MutableLiveData<>();

    private CareerCompareDao dao;
    private RankRepository repository;

    public CareerCompareViewModel(@NonNull Application application) {
        super(application);
        dao = new CareerCompareDao();
        repository = new RankRepository();
    }

    public void loadData() {
        loadingObserver.setValue(true);
        queryData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<CompareItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<CompareItem> compareItems) {
                        loadingObserver.setValue(false);
                        compareItemsObserver.setValue(compareItems);
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

    private Observable<List<CompareItem>> queryData() {
        return Observable.create(e -> {
            List<CompareItem> list = new ArrayList<>();
            // 冠军（级别，场地）
            queryChampions(list);
            // 重要轮次（级别）
            queryRounds(list);
            // 胜率（级别，场地，对阵排名，抢七）
            queryRate(list);
            // 其他
            queryOther(list);
            e.onNext(list);
        });
    }

    private void queryChampions(List<CompareItem> items) {
        CompareItem item = new CompareItem();
        item.setHead(true);
        item.setTitle("冠军数");
        items.add(item);

        items.add(dao.getTotalChampions());
        items.addAll(dao.getChampionsByLevel());
        items.addAll(dao.getChampionsByCourt());
    }

    /**
     * 重要轮次
     * 决赛（大满贯/大师杯/大师赛/奥运会）,四强（大满贯）
     * @param items
     */
    private void queryRounds(List<CompareItem> items) {
        CompareItem item = new CompareItem();
        item.setHead(true);
        item.setTitle("重要轮次");
        items.add(item);
        items.addAll(dao.getImportantFinals());
        items.addAll(dao.getImportantSF());
    }

    /**
     * 胜率
     * 赛事级别，场地类型，决胜盘，对阵topN，抢七胜率
     * @param items
     */
    private void queryRate(List<CompareItem> items) {
        CompareItem item = new CompareItem();
        item.setHead(true);
        item.setTitle("胜率");
        items.add(item);
        items.addAll(dao.getMatchData());
        items.addAll(dao.getCompetitorData());
        items.addAll(dao.getTiebreakData());
        items.addAll(dao.getFinalSet());
    }

    /**
     * 领先被逆转，落后逆转，送蛋次数，抢七次数，最长连胜
     * @param items
     */
    private void queryOther(List<CompareItem> items) {
        CompareItem item = new CompareItem();
        item.setHead(true);
        item.setTitle("其他");
        items.add(item);
        items.add(dao.getReverse());
        items.add(dao.getBeReversed());
        items.add(dao.getBagel());
        items.add(dao.getBeBagel());
        items.add(dao.getTibreakMatch());
        items.add(dao.getLongestWin());
    }

    public void loadRankCompares() {
        repository.compareUserWeekRankChart()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<LineChartData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(LineChartData data) {
                        chartObserver.setValue(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
