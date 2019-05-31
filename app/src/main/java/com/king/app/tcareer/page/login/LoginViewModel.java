package com.king.app.tcareer.page.login;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConfig;
import com.king.app.tcareer.model.http.BaseUrl;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.FileUtil;
import com.king.app.tcareer.utils.MD5Util;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: presenter for login page
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 13:57
 */
public class LoginViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> showFingerPrint = new MutableLiveData<>();

    public MutableLiveData<Boolean> showLoginFrame = new MutableLiveData<>();

    public MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void prepare() {
        loadingObserver.setValue(true);
        prepareDatas()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object o) {
                        loadingObserver.setValue(false);

                        TApplication.getInstance().createGreenDao();
                        if (SettingProperty.isEnableFingerPrint()) {
                            showFingerPrint.setValue(true);
                        }
                        else {
                            showLoginFrame.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Object> prepareDatas() {
        return Observable.create(e -> {

            // 创建base目录
            for (String path: AppConfig.DIRS) {
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
            }

            // 检查数据库是否存在
            FileUtil.copyDbFromAssets(AppConfig.DB_NAME);

            // init server url
            BaseUrl.getInstance().setBaseUrl(SettingProperty.getServerBaseUrl());

            e.onNext(new Object());
            e.onComplete();
        });
    }

    public void checkPassword(String pwd) {
        if ("38D08341D686315F".equals(MD5Util.get16MD5Capital(pwd))) {
            loginSuccess.setValue(true);
        }
        else {
            messageObserver.setValue("密码错误");
        }
    }
}
