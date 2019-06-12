package com.king.app.tcareer.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BindingDialogFragment;
import com.king.app.tcareer.databinding.DialogLoadingBinding;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 13:37
 */
public class ProgressDialogFragment extends BindingDialogFragment<DialogLoadingBinding> {

    private String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setCancelable(true);
        setStyle(android.app.DialogFragment.STYLE_NORMAL, R.style.LoadingDialog);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_loading;
    }

    @Override
    protected void initView(View view) {
        mBinding.tvMessage.setText(message);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        if (isAdded()) {
            ft.show(this);
        } else {
            ft.add(this, tag);
        }
        ft.commitAllowingStateLoss();
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
