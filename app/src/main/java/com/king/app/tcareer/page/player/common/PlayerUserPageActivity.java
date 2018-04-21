package com.king.app.tcareer.page.player.common;

import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.page.PagePresenter;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;

/**
 * @desc player主页，继承自PlayerPageActivity，重写presenter方法以及adapter适用的user
 * 使tab以user区分
 * @auth 景阳
 * @time 2018/4/21 0021 18:45
 */

public class PlayerUserPageActivity extends PlayerPageActivity {

    @Override
    protected PagePresenter createPresenter() {
        return new PageUserPresenter();
    }

    @Override
    public User getUser(String tabId) {
        return presenter.getUser(tabId);
    }
}
