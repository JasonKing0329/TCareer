package com.king.app.tcareer.base.mvvm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.DaoSession;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 描述:base viewmodel
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/2 15:02
 */
public class BaseViewModel extends AndroidViewModel {

    protected CompositeDisposable compositeDisposable;

    public MutableLiveData<Boolean> loadingObserver = new MutableLiveData<>();
    public MutableLiveData<String> messageObserver = new MutableLiveData<>();

    protected User mUser;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
    }

    protected void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    protected void dispatchCommonError(Throwable e) {
        messageObserver.setValue("Load error: " + e.getMessage());
    }

    protected void dispatchCommonError(String errorTitle, Throwable e) {
        messageObserver.setValue(errorTitle + ": " + e.getMessage());
    }

    public void onDestroy() {
        clearComposite();
    }

    protected void clearComposite() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    protected Observable<User> queryUser(final long userId) {
        return Observable.create(e -> e.onNext(queryUserInstant(userId)));
    }

    public User queryUserInstant(long userId) {
        UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
        mUser = dao.queryBuilder()
                .where(UserDao.Properties.Id.eq(userId))
                .build().unique();
        return mUser;
    }

    public User getUser() {
        return mUser;
    }

    protected DaoSession getDaoSession() {
        return TApplication.getInstance().getDaoSession();
    }
}
