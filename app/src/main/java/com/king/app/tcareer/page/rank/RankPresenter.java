package com.king.app.tcareer.page.rank;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.Rank;
import com.king.app.tcareer.model.db.entity.RankDao;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/5 11:20
 */
public class RankPresenter extends BasePresenter<RankView> {

    @Override
    protected void onCreate() {

    }

    public void loadRanks(long userId) {
        RankDao dao = TApplication.getInstance().getDaoSession().getRankDao();
        List<Rank> list = dao.queryBuilder()
                    .where(RankDao.Properties.UserId.eq(userId))
                    .build().list();
        view.showRanks(list);
    }

    public void saveRankFinal(Rank bean) {
        RankDao dao = TApplication.getInstance().getDaoSession().getRankDao();
        if (bean.getId() == null || bean.getId() == 0) {
            dao.insert(bean);
        }
        else {
            dao.update(bean);
        }
    }

    public void deleteRank(Rank bean) {
        RankDao dao = TApplication.getInstance().getDaoSession().getRankDao();
        dao.queryBuilder()
                .where(RankDao.Properties.Id.eq(bean.getId()))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }
}
