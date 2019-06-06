package com.king.app.tcareer.page.player.h2hlist;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.dao.H2HDao;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class H2hViewModel extends BaseViewModel {

    public ObservableField<String> userNameText = new ObservableField<>();

    public ObservableField<String> userImageUrl = new ObservableField<>();

    public MutableLiveData<H2hListPageData> pageDataObserver = new MutableLiveData<>();

    private H2hListPageData h2hListPageData;

    private H2HDao h2HDao;

    public H2hViewModel(@NonNull Application application) {
        super(application);
        h2HDao = new H2HDao();
    }

    public void loadPlayers(final long userId) {
        queryUser(userId)
                .flatMap(user -> {
                    userNameText.set(user.getNameChn());
                    userImageUrl.set(ImageProvider.getDetailPlayerPath(user.getNameChn()));
                    return queryPageData();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<H2hListPageData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(H2hListPageData data) {
                        pageDataObserver.setValue(data);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        messageObserver.setValue("Load h2h beans failed: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<H2hListPageData> queryPageData() {
        return Observable.create(e -> {
            h2hListPageData = new H2hListPageData();

            // chart datas
            h2hListPageData.setChartContents(new String[]{
                    "Top10", "Top11-20", "Top21-50", "Top51-100", "OutOf100"
            });
            h2hListPageData.setCareerChartWinValues(h2HDao.getTotalCount(mUser.getId(), AppConstants.WINNER_USER, false));
            h2hListPageData.setCareerChartLoseValues(h2HDao.getTotalCount(mUser.getId(), AppConstants.WINNER_COMPETITOR, false));
            h2hListPageData.setSeasonChartWinValues(h2HDao.getTotalCount(mUser.getId(), AppConstants.WINNER_USER, true));
            h2hListPageData.setSeasonChartLoseValues(h2HDao.getTotalCount(mUser.getId(), AppConstants.WINNER_COMPETITOR, true));

            e.onNext(h2hListPageData);
        });
    }

    public String[] getChartContents() {
        return pageDataObserver.getValue().getChartContents();
    }

    public float[] getTargetValues(boolean isCareer, boolean isWin) {
        float[] values = new float[pageDataObserver.getValue().getChartContents().length];
        Integer[] targetValues;
        if (isCareer) {
            if (isWin) {
                targetValues = pageDataObserver.getValue().getCareerChartWinValues();
            } else {
                targetValues = pageDataObserver.getValue().getCareerChartLoseValues();
            }
        } else {
            if (isWin) {
                targetValues = pageDataObserver.getValue().getSeasonChartWinValues();
            } else {
                targetValues = pageDataObserver.getValue().getSeasonChartLoseValues();
            }
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = targetValues[i];
        }
        return values;
    }
}
