package com.king.app.tcareer.base.mvvm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.DaoSession;

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

    protected DaoSession getDaoSession() {
        return TApplication.getInstance().getDaoSession();
    }
}
