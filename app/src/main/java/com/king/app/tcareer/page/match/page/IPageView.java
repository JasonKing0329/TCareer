package com.king.app.tcareer.page.match.page;

import com.king.app.tcareer.base.BaseView;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/21 14:14
 */
public interface IPageView extends BaseView {
    void showMatchInfo(String name, String country, String city, String level, String court);

    void showError(String msg);

    void onRecordsLoaded(List<Object> list, int win, int lose);
}
