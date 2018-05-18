package com.king.app.tcareer.page.player.manage;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/30 17:33
 */
public class EditPresenter {

    public void insertPlayer(PlayerBean bean) {
        PlayerBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
        dao.insert(bean);
    }

    public void updateUser(User user) {
        UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
        dao.update(user);
        dao.detach(user);
    }

    public void updatePlayer(PlayerBean bean) {
        PlayerBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
        dao.update(bean);
        dao.detach(bean);
    }
}
