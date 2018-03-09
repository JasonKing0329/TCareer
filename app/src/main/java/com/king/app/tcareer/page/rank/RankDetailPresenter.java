package com.king.app.tcareer.page.rank;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.model.db.entity.User;

import org.greenrobot.greendao.query.QueryBuilder;

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
 * <p/>创建时间: 2018/3/8 14:03
 */
public class RankDetailPresenter extends BasePresenter<RankDetailView> {
    @Override
    protected void onCreate() {

    }

    /**
     * query rank of week
     * @param userId
     * @return
     */
    private Observable<List<RankWeek>> queryWeekRank(final long userId, final boolean desc) {
        return Observable.create(new ObservableOnSubscribe<List<RankWeek>>() {
            @Override
            public void subscribe(ObservableEmitter<List<RankWeek>> e) throws Exception {
                RankWeekDao dao = TApplication.getInstance().getDaoSession().getRankWeekDao();
                QueryBuilder<RankWeek> builder = dao.queryBuilder()
                        .where(RankWeekDao.Properties.UserId.eq(userId));
                if (desc) {
                    builder.orderDesc(RankWeekDao.Properties.Date);
                }
                else {
                    builder.orderAsc(RankWeekDao.Properties.Date);
                }
                List<RankWeek> list = builder.build().list();
                e.onNext(list);
            }
        });
    }

    public void loadRanks(final long userId, final boolean desc) {
        view.showLoading();
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<List<RankWeek>>>() {
                    @Override
                    public ObservableSource<List<RankWeek>> apply(User user) throws Exception {
                        return queryWeekRank(userId, desc);
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
