package com.king.app.tcareer.page.rank;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.Rank;
import com.king.app.tcareer.model.db.entity.RankDao;
import com.king.app.tcareer.model.db.entity.User;

import java.util.List;

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
 * <p/>创建时间: 2017/4/5 11:20
 */
public class RankPresenter extends BasePresenter<RankView> {

    @Override
    protected void onCreate() {

    }

    public void loadYearRanks(final long userId) {
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<List<Rank>>>() {
                    @Override
                    public ObservableSource<List<Rank>> apply(User user) throws Exception {
                        view.postShowUser(user.getNameEng());
                        return queryYearRank(userId);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Rank>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Rank> ranks) {
                        view.showRanks(ranks);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Load rank error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * query rank of year
     * @param userId
     * @return
     */
    private Observable<List<Rank>> queryYearRank(final long userId) {
        return Observable.create(new ObservableOnSubscribe<List<Rank>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Rank>> e) throws Exception {
                RankDao dao = TApplication.getInstance().getDaoSession().getRankDao();
                List<Rank> list = dao.queryBuilder()
                        .where(RankDao.Properties.UserId.eq(userId))
                        .build().list();
                e.onNext(list);
            }
        });
    }

    public void saveRankFinal(Rank bean) {
        RankDao dao = TApplication.getInstance().getDaoSession().getRankDao();
        if (bean.getId() == null || bean.getId() == 0) {
            dao.insert(bean);
        }
        else {
            dao.update(bean);
        }
    }

    public void deleteRank(Rank bean) {
        RankDao dao = TApplication.getInstance().getDaoSession().getRankDao();
        dao.queryBuilder()
                .where(RankDao.Properties.Id.eq(bean.getId()))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }
}
