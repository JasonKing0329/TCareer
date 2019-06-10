package com.king.app.tcareer.page.rank;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.mvvm.BaseViewModel;
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
public class RankWeekViewModel extends BaseViewModel {

    public MutableLiveData<LineChartData> chartObserver = new MutableLiveData<>();

    private RankRepository repository;

    public RankWeekViewModel(@NonNull Application application) {
        super(application);
        repository = new RankRepository();
    }

    public void loadRanks(long userId) {
        queryUser(userId)
                .flatMap(user -> repository.loadUserWeekRankChart(userId))
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
                        messageObserver.setValue("Load error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
