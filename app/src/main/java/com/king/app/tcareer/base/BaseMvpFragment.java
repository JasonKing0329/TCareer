package com.king.app.tcareer.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 描述: mvp架构的base fragment
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 13:28
 */
public abstract class BaseMvpFragment<T extends BasePresenter> extends BaseFragment implements BaseView {

    protected T presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        presenter = createPresenter();
        presenter.onAttach(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCreateData();
    }

    protected abstract T createPresenter();

    protected abstract void onCreateData();

    @Override
    public void onDestroyView() {
        if (presenter != null) {
            presenter.onDestroy();
        }
        super.onDestroyView();
    }

    @Override
    public void showLoading() {
        showProgress("loading...");
    }

    @Override
    public void dismissLoading() {
        dismissProgress();
    }

    @Override
    public void showConfirm(String message) {
        showConfirmMessage(message, null);
    }

    @Override
    public void showMessage(String message) {
        showMessageShort(message);
    }

}
