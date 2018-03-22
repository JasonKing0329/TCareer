package com.king.app.tcareer.page.record.complex;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.model.dao.CareerCompareDao;

import java.util.ArrayList;
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
 * <p/>创建时间: 2018/3/20 13:49
 */
public class CareerComparePresenter extends BasePresenter<CareerCompareView> {

    private CareerCompareDao dao;

    @Override
    protected void onCreate() {
        dao = new CareerCompareDao();
    }

    public void loadData() {
        view.showLoading();
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
                        view.dismissLoading();
                        view.showData(compareItems);
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

    private Observable<List<CompareItem>> queryData() {
        return Observable.create(new ObservableOnSubscribe<List<CompareItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<CompareItem>> e) throws Exception {
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
            }
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

}
