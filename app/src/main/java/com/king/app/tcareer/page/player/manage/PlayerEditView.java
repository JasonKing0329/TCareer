package com.king.app.tcareer.page.player.manage;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/24 9:23
 */
public interface PlayerEditView extends BaseView {
    void onUpdateAtpCompleted(PlayerAtpBean bean);
}
