package com.king.app.tcareer.base;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.king.app.tcareer.R;
import com.king.app.tcareer.view.dialog.ProgressDialogFragmentApp;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 13:26
 */
public abstract class BaseFragmentApp extends Fragment {

    private ProgressDialogFragmentApp progressDialogFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentHolder) {
            bindFragmentHolder((IFragmentHolder) context);
        }
    }

    protected abstract void bindFragmentHolder(IFragmentHolder holder);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentLayoutRes(), container, false);
        onCreate(view);
        return view;
    }

    protected abstract int getContentLayoutRes();

    protected abstract void onCreate(View view);

    public void showProgress(String msg) {
        progressDialogFragment = new ProgressDialogFragmentApp();
        if (TextUtils.isEmpty(msg)) {
            msg = getResources().getString(R.string.loading);
        }
        progressDialogFragment.setMessage(msg);
        progressDialogFragment.show(getChildFragmentManager(), "ProgressDialogFragment");
    }

    public void dismissProgress() {
        if (progressDialogFragment != null) {
            progressDialogFragment.dismiss();
        }
    }

    public void showMessageShort(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void showMessageLong(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    public void showConfirmMessage(String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(getActivity())
                .setTitle(null)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.ok), listener)
                .show();
    }

}
