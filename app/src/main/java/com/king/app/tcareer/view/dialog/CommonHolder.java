package com.king.app.tcareer.view.dialog;

import com.king.app.tcareer.base.IFragmentHolder;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/8 14:45
 */
public interface CommonHolder extends IFragmentHolder {

    void requestOkAction();

    void requestOkAction(int srcRes);

    void requestCloseAction();

    void setTitle(String text);

    void dismiss();

}
