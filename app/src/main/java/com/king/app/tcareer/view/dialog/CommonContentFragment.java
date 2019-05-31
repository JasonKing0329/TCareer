package com.king.app.tcareer.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.tcareer.base.BaseMvpFragment;
import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.utils.ScreenUtils;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/8 14:38
 */
public abstract class CommonContentFragment extends BaseMvpFragment<BasePresenter> {

    protected CommonHolder dialogHolder;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getParentFragment() instanceof CommonHolder) {
            dialogHolder = (CommonHolder) getParentFragment();
        }
        customToolbar();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    protected abstract void customToolbar();

    /**
     * 子类选择实现
     * @return
     */
    public boolean onSave() {
        return true;
    }

    /**
     * 子类选择实现
     */
    public void onClose() {

    }

    /**
     * 子类可选择覆盖
     * @return
     */
    public int getMaxHeight() {
        return ScreenUtils.getScreenHeight(getActivity()) * 3 / 5;
    }

    /**
     * 子类可选择覆盖
     * @return
     */
    public int getMinHeight(){
        return ScreenUtils.getScreenHeight(getActivity()) * 2 / 5;
    }
}
