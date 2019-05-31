package com.king.app.tcareer.base.mvvm;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/15 17:35
 */
public abstract class BaseDialogFragment<T extends ViewDataBinding> extends DialogFragment {

    protected T mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBinding = DataBindingUtil.inflate(inflater, getContentView(), container, false);

        View view = mBinding.getRoot();
        onBindView(view);
        return view;
    }

    protected abstract int getContentView();

    protected abstract void onBindView(View view);


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
