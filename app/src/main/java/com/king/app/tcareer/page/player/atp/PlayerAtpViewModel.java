package com.king.app.tcareer.page.player.atp;

import android.app.Application;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConfig;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerAtpBeanDao;
import com.king.app.tcareer.model.html.PlayerParser;
import com.king.app.tcareer.model.html.RankParser;
import com.king.app.tcareer.model.http.AtpWorldTourClient;
import com.king.app.tcareer.model.http.AtpWorldTourParams;
import com.king.app.tcareer.utils.FileUtil;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/16 11:01
 */
public class PlayerAtpViewModel extends BaseViewModel {

    public PlayerAtpViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 更新player的atp数据
     * @param atpId
     */
    public void updateAtpData(final String atpId) {
        if (atpId == null) {
            messageObserver.setValue("atpId不存在");
            return;
        }
        loadingObserver.setValue(true);
        getPlayerAtpUrl(atpId)
                .flatMap(url -> new PlayerParser().parse(atpId, url))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PlayerAtpBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(PlayerAtpBean bean) {
                        loadingObserver.setValue(false);
                        onUpdateAtpCompleted(bean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("更新失败：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 子类选择覆盖
    protected void onUpdateAtpCompleted(PlayerAtpBean bean) {
        messageObserver.setValue("更新完成");
    }

    private Observable<String> getPlayerAtpUrl(final String atpId) {
        return Observable.create(e -> {
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
        });
    }

    /**
     * 获取排名1-1000数据，添加新增，缓存网页
     */
    public void fetchData() {
        loadingObserver.setValue(true);
        AtpWorldTourClient.getInstance().getService().getRankList(AtpWorldTourParams.URL_RANK)
                .flatMap(responseBody -> saveFile(responseBody, AppConfig.FILE_HTML_RANK))
                .flatMap(file -> new RankParser().parse(file))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean result) {
                        loadingObserver.setValue(false);
                        onFetchCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("下载失败" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static Observable<File> saveFile(final ResponseBody responseBody, final String path) {
        return Observable.create(e -> e.onNext(FileUtil.saveFile(responseBody.byteStream(), path)));
    }

    // 子类选择覆盖
    protected void onFetchCompleted() {
        messageObserver.setValue("下载并更新完成");
    }

}
