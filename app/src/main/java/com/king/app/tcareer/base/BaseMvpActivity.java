package com.king.app.tcareer.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 描述: mvp 模式的base activity
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 13:14
 */
public abstract class BaseMvpActivity<T extends BasePresenter> extends BaseActivity implements BaseView {

    protected T presenter;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        unbinder = ButterKnife.bind(this);

        presenter = createPresenter();
        presenter.onAttach(this);

        initView();
        initData();
    }

    protected abstract T createPresenter();

    protected abstract void initData();

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

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (presenter != null) {
            presenter.onDestroy();
        }
        super.onDestroy();
    }

}
