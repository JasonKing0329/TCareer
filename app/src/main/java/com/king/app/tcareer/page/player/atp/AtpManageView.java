package com.king.app.tcareer.page.player.atp;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.view.widget.SideBar;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/18 9:21
 */
public interface AtpManageView extends BaseView {

    void postShowPlayers(List<PlayerAtpBean> list);
    SideBar getSideBar();
}
