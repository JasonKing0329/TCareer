package com.king.app.tcareer.base;

import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 13:14
 */
public abstract class BasePresenter<T extends BaseView> {

    protected T view;

    protected User mUser;

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
}
