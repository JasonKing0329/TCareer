package com.king.app.tcareer.page.score;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.model.db.entity.User;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/5 10:24
 */
public class ScoreHolderViewModel extends BaseViewModel {

    public MutableLiveData<User> userObserver = new MutableLiveData<>();

    public ScoreHolderViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadData(final long userId) {
        queryUser(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(User user) {
                        userObserver.setValue(user);
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
