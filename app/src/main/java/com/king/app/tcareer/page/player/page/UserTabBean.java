package com.king.app.tcareer.page.player.page;

import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.page.TabBean;

/**
 * @desc
 * @auth 景阳
 * @time 2018/4/21 0021 19:02
 */

public class UserTabBean extends TabBean {

    public User user;

    @Override
    public String getTitle() {
        return user.getNameShort();
    }
}
