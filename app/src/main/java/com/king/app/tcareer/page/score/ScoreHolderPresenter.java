package com.king.app.tcareer.page.score;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.RankCareer;
import com.king.app.tcareer.model.db.entity.RankCareerDao;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/5 10:24
 */
public class ScoreHolderPresenter extends BasePresenter<ScoreView> {

    private RankCareer mRankCareer;

    @Override
    protected void onCreate() {

    }

    public void loadRank(long userId) {
        mRankCareer = TApplication.getInstance().getDaoSession().getRankCareerDao()
                .queryBuilder()
                .where(RankCareerDao.Properties.UserId.eq(userId))
                .build().unique();
    }

    public RankCareer getRankCareer() {
        return mRankCareer;
    }

    public void updateRankCareer(RankCareer rank) {
        TApplication.getInstance().getDaoSession().getRankCareerDao().update(rank);
    }
}
