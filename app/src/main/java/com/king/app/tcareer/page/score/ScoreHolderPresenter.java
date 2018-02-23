package com.king.app.tcareer.page.score;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.RankCareer;
import com.king.app.tcareer.model.db.entity.RankCareerDao;
import com.king.app.tcareer.model.db.entity.User;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/5 10:24
 */
public class ScoreHolderPresenter extends BasePresenter<ScoreView> {

    private RankCareer mRankCareer;

    @Override
    protected void onCreate() {

    }

    public void loadRank(final long userId) {
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<RankCareer>>() {
                    @Override
                    public ObservableSource<RankCareer> apply(User user) throws Exception {
                        return new ObservableSource<RankCareer>() {
                            @Override
                            public void subscribe(Observer<? super RankCareer> observer) {
                                mRankCareer = TApplication.getInstance().getDaoSession().getRankCareerDao()
                                        .queryBuilder()
                                        .where(RankCareerDao.Properties.UserId.eq(userId))
                                        .build().unique();
                                observer.onNext(mRankCareer);
                            }
                        };
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RankCareer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(RankCareer rankCareer) {
                        view.showRanks();
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

    public RankCareer getRankCareer() {
        return mRankCareer;
    }

    public void updateRankCareer(RankCareer rank) {
        TApplication.getInstance().getDaoSession().getRankCareerDao().update(rank);
    }
}
