package com.king.app.tcareer.page.match.manage;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchBeanDao;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/30 17:33
 */
public class EditPresenter {

    public void insertFullMatch(MatchNameBean bean, MatchBean matchBean) {
        MatchBeanDao matchDao = TApplication.getInstance().getDaoSession().getMatchBeanDao();
        matchDao.insert(matchBean);

        bean.setMatchId(matchBean.getId());
        MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
        dao.insert(bean);
    }

    public void insertMatchName(MatchNameBean bean) {
        MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
        dao.insert(bean);
    }

    public void updateMatch(MatchNameBean bean, MatchBean matchBean) {
        MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
        dao.update(bean);

        MatchBeanDao matchDao = TApplication.getInstance().getDaoSession().getMatchBeanDao();
        matchDao.update(matchBean);
    }
}
