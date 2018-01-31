package com.king.app.tcareer.page.player.manage;

import com.king.app.tcareer.base.BaseView;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 11:48
 */

public interface PlayerManageView extends BaseView {
    void showPlayers(List<PlayerViewBean> list);

    void clearSideBar();

    void addSideBarIndex(String index);

    void showSideBar(boolean show);

    void deleteSuccess();

    void sortFinished(List<PlayerViewBean> list);
}
