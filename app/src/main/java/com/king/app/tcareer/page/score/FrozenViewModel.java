package com.king.app.tcareer.page.score;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.model.db.entity.FrozenScore;
import com.king.app.tcareer.model.db.entity.FrozenScoreDao;
import com.king.app.tcareer.model.observer.SimpleObserver;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/4/15 8:48
 */
public class FrozenViewModel extends BaseViewModel {

    private long mUserId;
    
    public MutableLiveData<List<FrozenItem>> itemsObserver = new MutableLiveData<>();

    public FrozenViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadScores(long userId) {
        mUserId = userId;
        getScores()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SimpleObserver<List<FrozenItem>>(getCompositeDisposable()) {
                    @Override
                    public void onNext(List<FrozenItem> frozenItems) {
                        itemsObserver.setValue(frozenItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private Observable<List<FrozenItem>> getScores() {
        return Observable.create(e -> {
            List<FrozenScore> scores = getDaoSession().getFrozenScoreDao().queryBuilder()
                    .where(FrozenScoreDao.Properties.UserId.eq(mUserId))
                    .build().list();
            List<FrozenItem> list = new ArrayList<>();
            for (FrozenScore score:scores) {
                FrozenItem item = new FrozenItem();
                item.setBean(score);
                item.setMatch(score.getMatchNameBean().getName());
                list.add(item);
            }
            e.onNext(list);
        });
    }

    public void deleteScore(FrozenItem bean) {
        getDaoSession().getFrozenScoreDao().delete(bean.getBean());
        getDaoSession().getFrozenScoreDao().detachAll();
        loadScores(mUserId);
    }

    public void insertOrUpdateScore(FrozenScore bean) {
        bean.setUserId(mUserId);
        getDaoSession().getFrozenScoreDao().insertOrReplace(bean);
        getDaoSession().getFrozenScoreDao().detachAll();
        loadScores(mUserId);
    }
}
