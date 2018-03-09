package com.king.app.tcareer.page.score;

import com.king.app.tcareer.base.BasePresenter;
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
public class ScoreHolderPresenter extends BasePresenter<ScoreView> {

    @Override
    protected void onCreate() {

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
                    public void onNext(User rankCareer) {
                        view.showRanks();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Load error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
