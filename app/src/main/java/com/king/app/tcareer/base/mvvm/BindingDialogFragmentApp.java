package com.king.app.tcareer.base.mvvm;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.tcareer.view.dialog.BaseDialogFragmentApp;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/15 17:35
 */
public abstract class BindingDialogFragmentApp<T extends ViewDataBinding> extends BaseDialogFragmentApp {

    protected T mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false);

        View view = mBinding.getRoot();
        initView(view);
        return view;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        if (isAdded()) {
            ft.show(this);
        }
        else {
            ft.add(this, tag);
        }
        ft.commitAllowingStateLoss();
    }
}
