package com.king.app.tcareer.view.dialog.frame;

import android.databinding.ViewDataBinding;

import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.base.mvvm.MvvmFragment;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/30 9:16
 */
public abstract class FrameContentFragment<T extends ViewDataBinding, VM extends BaseViewModel> extends MvvmFragment<T, VM> {

    private FrameHolder dialogHolder;

    public void setDialogHolder(FrameHolder dialogHolder) {
        this.dialogHolder = dialogHolder;
    }

    protected void dismiss() {
        dialogHolder.dismiss();
    }

    protected void dismissAllowingStateLoss() {
        dialogHolder.dismissAllowingStateLoss();
    }

}
