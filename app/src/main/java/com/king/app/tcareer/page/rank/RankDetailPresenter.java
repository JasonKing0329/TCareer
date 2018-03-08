package com.king.app.tcareer.page.rank;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.model.db.entity.User;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/8 14:03
 */
public class RankDetailPresenter extends BasePresenter<RankDetailView> {
    @Override
    protected void onCreate() {

    }

    public void loadRanks(final long userId) {
        view.showLoading();
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<List<RankWeek>>>() {
                    @Override
                    public ObservableSource<List<RankWeek>> apply(User user) throws Exception {
                        return new ObservableSource<List<RankWeek>>() {
                            @Override
                            public void subscribe(Observer<? super List<RankWeek>> observer) {
                                RankWeekDao dao = TApplication.getInstance().getDaoSession().getRankWeekDao();
                                List<RankWeek> list = dao.queryBuilder()
                                        .where(RankWeekDao.Properties.UserId.eq(userId))
                                        .orderDesc(RankWeekDao.Properties.Date)
                                        .build().list();
                                observer.onNext(list);
                            }
                        };
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<RankWeek>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<RankWeek> list) {
                        view.dismissLoading();
                        view.showRanks(list);
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

    public void deleteRank(RankWeek item) {
        RankWeekDao dao = TApplication.getInstance().getDaoSession().getRankWeekDao();
        dao.queryBuilder()
                .where(RankWeekDao.Properties.Id.eq(item.getId()))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }
}
