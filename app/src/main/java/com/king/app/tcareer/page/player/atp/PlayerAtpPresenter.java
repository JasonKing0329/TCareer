package com.king.app.tcareer.page.player.atp;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerAtpBeanDao;
import com.king.app.tcareer.model.html.PlayerParser;
import com.king.app.tcareer.model.http.AtpWorldTourParams;

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
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/16 11:01
 */
public class PlayerAtpPresenter<T extends BaseView> extends BasePresenter<T> {

    @Override
    protected void onCreate() {

    }

    /**
     * 更新player的atp数据
     * @param atpId
     */
    public void updateAtpData(final String atpId) {
        if (atpId == null) {
            view.showMessage("atpId不存在");
            return;
        }
        view.showLoading();
        getPlayerAtpUrl(atpId)
                .flatMap(new Function<String, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(String url) throws Exception {
                        return new PlayerParser().parse(atpId, url);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        view.dismissLoading();
                        onUpdateAtpCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("更新失败：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 子类选择覆盖
    protected void onUpdateAtpCompleted() {
        view.showMessage("更新完成");
    }

    private Observable<String> getPlayerAtpUrl(final String atpId) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                PlayerAtpBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerAtpBeanDao();
                PlayerAtpBean bean = dao.queryBuilder()
                        .where(PlayerAtpBeanDao.Properties.Id.eq(atpId))
                        .build().unique();
                String url = AtpWorldTourParams.BASE_URL;
                if (bean.getOverViewUrl().startsWith("/")) {
                    url = url + bean.getOverViewUrl().substring(1);
                }
                else {
                    url = url + bean.getOverViewUrl();
                }
                e.onNext(url);
            }
        });
    }
}
