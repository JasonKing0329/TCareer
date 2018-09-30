package com.king.app.tcareer.view.dialog.frame;

import com.king.app.tcareer.base.BaseMvpFragment;
import com.king.app.tcareer.base.BasePresenter;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/30 9:16
 */
public abstract class FrameContentFragment<T extends BasePresenter> extends BaseMvpFragment<T> {

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
