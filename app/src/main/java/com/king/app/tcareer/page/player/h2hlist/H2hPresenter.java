package com.king.app.tcareer.page.player.h2hlist;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.dao.H2HDao;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class H2hPresenter extends BasePresenter<IH2hListView> {

    private H2hListPageData h2hListPageData;

    private H2HDao h2HDao;

    @Override
    protected void onCreate() {
        h2HDao = new H2HDao();
    }

    public void loadPlayers(final long userId) {
        queryUser(userId)
                .flatMap(user -> {
                    view.postShowUser(user);
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
                        view.onDataLoaded(data);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        view.showMessage("Load h2h beans failed: " + throwable.getMessage());
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
}
