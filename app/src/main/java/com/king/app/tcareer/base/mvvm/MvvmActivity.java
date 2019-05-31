package com.king.app.tcareer.base.mvvm;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.king.app.tcareer.base.BaseActivity;
import com.king.app.tcareer.utils.DebugLog;

/**
 * 描述:MVVM模式的基类activity
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/2 15:50
 */
public abstract class MvvmActivity<T extends ViewDataBinding, VM extends BaseViewModel> extends BaseActivity {

    protected T mBinding;

    protected VM mModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        DebugLog.e();
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getContentView());
        mModel = createViewModel();
        if (mModel != null) {
            mModel.loadingObserver.observe(this, show -> {
                if (show) {
                    showProgress("Loading...");
                }
                else {
                    dismissProgress();
                }
            });
            mModel.messageObserver.observe(this, message -> showMessageShort(message));
        }

        initView();
        initData();
    }

    protected abstract VM createViewModel();

    protected abstract void initData();

    @Override
    protected void onDestroy() {
        if (mModel != null) {
            mModel.onDestroy();
        }
        super.onDestroy();
    }
}
