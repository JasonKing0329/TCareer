package com.king.app.tcareer.page.player.manage;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConfig;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.model.html.RankParser;
import com.king.app.tcareer.model.http.AtpWorldTourClient;
import com.king.app.tcareer.model.http.AtpWorldTourParams;
import com.king.app.tcareer.utils.FileUtil;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 11:48
 */

public class PlayerManagePresenter extends BasePresenter<PlayerManageView> {

    private List<User> userList;

    @Override
    protected void onCreate() {
    }

    /**
     * 获取排名数据
     */
    public void fetchData() {
        view.showLoading();
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
                        view.dismissLoading();
                        view.showMessage("下载完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("下载失败" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static Observable<File> saveFile(final ResponseBody responseBody, final String path) {
        return Observable.create(e -> e.onNext(FileUtil.saveFile(responseBody.byteStream(), path)));
    }

    public String[] getUserSelector() {
        if (userList == null) {
            UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
            userList = dao.loadAll();
        }
        String[] users = new String[userList.size() + 1];
        users[0] = "All users";
        for (int i = 0; i < userList.size(); i ++) {
            users[i + 1] = userList.get(i).getNameEng();
        }
        return users;
    }

    public User getUser(int position) {
        if (position == 0) {
            return null;
        }
        else {
            return userList.get(position - 1);
        }
    }
}
