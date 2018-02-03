package com.king.app.tcareer.page.update;

import com.king.app.tcareer.model.http.bean.AppCheckBean;

/**
 * Created by Administrator on 2016/9/6.
 */
public interface IUpdateView {
    void onAppUpdateFound(AppCheckBean bean);
    void onAppIsLatest();
    void onServiceDisConnected();
    void onRequestError();
}
