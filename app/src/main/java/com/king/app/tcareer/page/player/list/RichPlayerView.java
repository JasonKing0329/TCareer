package com.king.app.tcareer.page.player.list;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.view.widget.SideBar;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/5/19 0019 15:36
 */

public interface RichPlayerView extends BaseView {
    void showPlayers(List<RichPlayerBean> list);

    void onUpdateAtpCompleted(int position);

    void deleteSuccess();

    SideBar getSidebar();

    void sortFinished();
}
