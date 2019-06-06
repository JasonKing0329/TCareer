package com.king.app.tcareer.base.mvvm;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.tcareer.base.BaseFragmentApp;

/**
 * 描述:MVVM模式的基类fragment
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/4 10:51
 */
public abstract class MvvmFragmentApp<T extends ViewDataBinding, VM extends BaseViewModel> extends BaseFragmentApp implements LifecycleOwner {

    protected T mBinding;

    protected VM mModel;

    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getContentLayoutRes(), container, false);
        mModel = createViewModel();
        initViewModel();

        View view = mBinding.getRoot();
        onCreate(view);
        onCreateData();
        return view;
    }

    protected void initViewModel() {
        if (mModel != null) {
            mModel.loadingObserver.observe(this, show -> onLoadingChanged(show));
            mModel.messageObserver.observe(this, message -> onMessageObserved(message));
        }
    }

    protected void onMessageObserved(String message) {
        showMessageShort(message);
    }

    protected void onLoadingChanged(Boolean show) {
        if (show) {
            showProgress("Loading...");
        } else {
            dismissProgress();
        }
    }

    protected abstract VM createViewModel();

    protected abstract void onCreateData();

    @Override
    public void onDestroyView() {
        if (mModel != null) {
            mModel.onDestroy();
        }
        super.onDestroyView();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
