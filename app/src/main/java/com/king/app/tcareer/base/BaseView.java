package com.king.app.tcareer.base;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 13:14
 */
public interface BaseView {

    void showLoading();

    void dismissLoading();

    void showConfirm(String message);

    void showMessage(String message);
}
