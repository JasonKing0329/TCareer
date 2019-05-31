package com.king.app.tcareer.page.login;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityLoginBinding;
import com.king.app.tcareer.model.fingerprint.samsung.SamsungFingerPrint;
import com.king.app.tcareer.page.home.main.MainHomeActivity;
import com.king.app.tcareer.page.setting.SettingActivity;
import com.king.app.tcareer.utils.AppUtil;
import com.king.app.tcareer.utils.DBExportor;
import com.king.app.tcareer.view.dialog.FingerprintDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * login page
 */
public class LoginActivity extends MvvmActivity<ActivityLoginBinding, LoginViewModel> {

    private SamsungFingerPrint fingerPrint;

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        mBinding.groupLogin.setVisibility(View.INVISIBLE);
        mBinding.btnLogin.setOnClickListener(v -> mModel.checkPassword(mBinding.etPwd.getText().toString()));
    }

    @Override
    protected LoginViewModel createViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    protected void initData() {

        if (AppUtil.isAndroidP()) {
            closeAndroidPDialog();
        }

        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isGranted -> {
                    if (isGranted) {
                        initCreate();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    finish();
                });

    }

    private void initCreate() {
        mModel.showFingerPrint.observe(this, show -> showFingerPrint());
        mModel.showLoginFrame.observe(this, show -> showLoginFrame());
        mModel.loginSuccess.observe(this, success -> {
            if (success) {
                permitLogin();
            }
        });

        // 每次进入导出一次数据库
        DBExportor.execute();
        mModel.prepare();
    }

    /**
     * android 9.0开始，部分机型如小米，Android P 后谷歌限制了开发者调用非官方公开API 方法或接口，也就是说，
     * 用反射直接调用源码就会有这样的提示弹窗出现，非 SDK 接口指的是 Android 系统内部使用、
     * 并未提供在 SDK 中的接口，开发者可能通过 Java 反射、JNI 等技术来调用这些接口
     * 用此方法去掉该弹框
     */
    private void closeAndroidPDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoginFrame() {
        mBinding.groupLogin.setVisibility(View.VISIBLE);
    }

    private void showFingerPrint() {
        checkFingerprint();
    }

    private void checkFingerprint() {
        FingerprintManagerCompat compat = FingerprintManagerCompat.from(this);
        if (compat.isHardwareDetected()) {
            if (compat.hasEnrolledFingerprints()) {
                startFingerPrintDialog();
            }
            else {
                showMessageLong("设备未注册指纹");
            }
        }
        else {
            // 三星Tab S(Android6.0)有指纹识别，但是系统方法判断为没有，继续用三星的sdk操作
            checkSamsungFingerprint();
        }
    }

    private void checkSamsungFingerprint() {
        fingerPrint = new SamsungFingerPrint(LoginActivity.this);
        if (fingerPrint.isSupported()) {
            if (fingerPrint.hasRegistered()) {
                startSamsungFingerPrintDialog();
            } else {
                showMessageLong("设备未注册指纹");
            }
            return;
        } else {
            showMessageLong("设备不支持指纹识别");
        }
    }

    /**
     * 通用指纹识别对话框
     */
    private void startFingerPrintDialog() {
        FingerprintDialog dialog = new FingerprintDialog();
        dialog.setOnFingerPrintListener(() -> permitLogin());
        dialog.show(getSupportFragmentManager(), "FingerprintDialog");
    }

    /**
     * 三星指纹识别对话框
     */
    private void startSamsungFingerPrintDialog() {
        boolean withPW = false;
        fingerPrint.showIdentifyDialog(withPW, new SamsungFingerPrint.SimpleIdentifyListener() {

            @Override
            public void onSuccess() {
                permitLogin();
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onCancel() {
                finish();
            }
        });
    }

    public void permitLogin() {
        // 弃用
//        showYesNoMessage("是否打开设置页面？",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        startSetting();
//                    }
//                }, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        startHome();
//                    }
//                });

        startHome();
    }

    private void startSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void startHome() {
        Intent intent = new Intent(this, MainHomeActivity.class);
        startActivity(intent);
        finish();
    }
}
