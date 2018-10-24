package com.king.app.tcareer.page.rank;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.model.bean.LineChartData;
import com.king.app.tcareer.repository.RankRepository;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/8 14:03
 */
public class RankWeekPresenter extends BasePresenter<RankWeekView> {

    private RankRepository repository;

    @Override
    protected void onCreate() {
        repository = new RankRepository();
    }

    public void loadRanks(final long userId, final boolean desc) {
        queryUser(userId)
                .flatMap(user -> {
                    view.postShowUser(user.getNameEng());
                    return repository.loadUserWeekRankChart(userId);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<LineChartData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(LineChartData data) {
                        view.showChart(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Load error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
