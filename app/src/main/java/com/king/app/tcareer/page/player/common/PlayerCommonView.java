package com.king.app.tcareer.page.player.common;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.manage.PlayerViewBean;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 11:02
 */

public interface PlayerCommonView extends BaseView {
    void showPlayer(PlayerViewBean playerBean);

    void showH2H(User user, int win, int lose);
}
