package com.king.app.tcareer.model.observer;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @description:
 * @author：Jing
 * @date: 2020/3/4 16:28
 */
public abstract class SimpleObserver<T> implements Observer<T> {

    private CompositeDisposable compositeDisposable;

    public SimpleObserver(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void onSubscribe(Disposable d) {
        compositeDisposable.add(d);
    }

    @Override
    public void onComplete() {

    }
}
