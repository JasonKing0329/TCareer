package com.king.app.tcareer.page.player.page;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.dao.H2HDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/4/27 14:08
 */
public class UserPageModel implements SubPageModel {
    @Override
    public List<TabBean> createTabs(User boundUser, CompetitorBean competitor) {
        List<TabBean> list = new ArrayList<>();
        UserDao userDao = TApplication.getInstance().getDaoSession().getUserDao();
        H2HDao h2HDao = new H2HDao();
        List<User> users = userDao.loadAll();
        for (User user:users) {
            H2hBean h2h = h2HDao.getH2h(user.getId(), competitor.getId(), competitor instanceof User);
            UserTabBean bean = new UserTabBean();
            // TAG_SET_USER_ID_AS_TAB_ID
            bean.userId = user.getId();
            // page user tab是以当前mCompetitor为主角的，所以这里的win lose与page court tab是相反的
            bean.win = h2h.getLose();
            bean.lose = h2h.getWin();
            bean.total = h2h.getTotal();
            bean.user = user;
            if (bean.total > 0) {
                list.add(bean);
            }
        }
        return list;
    }
}
