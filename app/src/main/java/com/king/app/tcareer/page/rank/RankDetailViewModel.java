package com.king.app.tcareer.page.rank;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.model.db.entity.User;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/8 14:03
 */
public class RankDetailViewModel extends BaseViewModel {

    public MutableLiveData<User> userObserver = new MutableLiveData<>();
    public MutableLiveData<List<RankWeek>> ranksObserver = new MutableLiveData<>();

    public RankDetailViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * query rank of week
     * @param userId
     * @return
     */
    private Observable<List<RankWeek>> queryWeekRank(final long userId, final boolean desc) {
        return Observable.create(e -> {
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
        });
    }

    public void loadRanks(final long userId, final boolean desc) {
        queryUser(userId)
                .flatMap(user -> {
                    userObserver.postValue(user);
                    return queryWeekRank(userId, desc);
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
                        ranksObserver.setValue(list);
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

    public void deleteRank(RankWeek item) {
        RankWeekDao dao = TApplication.getInstance().getDaoSession().getRankWeekDao();
        dao.queryBuilder()
                .where(RankWeekDao.Properties.Id.eq(item.getId()))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }
}
