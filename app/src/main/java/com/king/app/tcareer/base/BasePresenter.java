package com.king.app.tcareer.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 13:14
 */
public abstract class BasePresenter<T extends BaseView> {

    protected T view;

    private CompositeDisposable compositeDisposable;

    public BasePresenter() {
        compositeDisposable = new CompositeDisposable();
        onCreate();
    }

    protected abstract void onCreate();

    public void onAttach(T view) {
        this.view = view;
    }

    public void onDestroy() {
        compositeDisposable.clear();
    }

    protected void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }
}
