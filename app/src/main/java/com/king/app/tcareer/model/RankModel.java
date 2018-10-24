package com.king.app.tcareer.model;

import android.database.Cursor;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.bean.RankRangeBean;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.RankWeekDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.database.Database;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 13:50
 */
public class RankModel {

    public int queryCurrentRank(long userId) {
        int rank = 0;
        RankWeekDao weekDao = TApplication.getInstance().getDaoSession().getRankWeekDao();
        try {
            RankWeek week = weekDao.queryBuilder()
                    .where(RankWeekDao.Properties.UserId.eq(userId))
                    .orderDesc(RankWeekDao.Properties.Date)
                    .limit(1)
                    .build().unique();
            rank = week.getRank();
        } catch (DaoException e) {}
        return rank;
    }

    public int queryHighestRank(long userId) {
        int rank = 0;
        Database database = TApplication.getInstance().getDaoSession().getDatabase();
        String sql = "select min(rank) from rank_week where user_id=? and rank != 0";
        String[] args = new String[]{String.valueOf(userId)};
        Cursor cursor = database.rawQuery(sql, args);
        if (cursor.moveToNext()) {
            rank = cursor.getInt(0);
        }
        return rank;
    }

    public RankRangeBean queryRankRange(final long userId, final int min, final int max) {
        RankWeekDao dao = TApplication.getInstance().getDaoSession().getRankWeekDao();

        // count weeks
        RankRangeBean bean = new RankRangeBean();
        long count = dao.queryBuilder()
                .where(RankWeekDao.Properties.UserId.eq(userId)
                        , RankWeekDao.Properties.Rank.ge(min)
                        , RankWeekDao.Properties.Rank.le(max))
                .buildCount().count();
        bean.setWeeks((int) count);

        // count sequences
        List<RankWeek> list = dao.queryBuilder()
                .where(RankWeekDao.Properties.UserId.eq(userId))
                .build().list();
        RankRangeBean maxBean = new RankRangeBean();
        RankRangeBean tempBean = new RankRangeBean();
        for (int i = 0; i < list.size(); i ++) {
            RankWeek week = list.get(i);
            if (week.getRank() >= min && week.getRank() <= max) {
                if (tempBean.getRankStart() == null) {
                    tempBean.setRankStart(week);
                }
                tempBean.setSequences(tempBean.getSequences() + 1);
            }
            else {
                if (tempBean.getRankEnd() == null && i > 0) {
                    tempBean.setRankEnd(list.get(i - 1));
                }
                if (tempBean.getSequences() > maxBean.getSequences()) {
                    maxBean.setSequences(tempBean.getSequences());
                    maxBean.setRankStart(tempBean.getRankStart());
                    maxBean.setRankEnd(tempBean.getRankEnd());
                }
                tempBean.setSequences(0);
                tempBean.setRankStart(null);
                tempBean.setRankEnd(null);
            }
        }
        // 连续子串出现在末尾
        if (tempBean.getRankEnd() == null && list.size() > 0) {
            tempBean.setRankEnd(list.get(list.size() - 1));
        }
        if (tempBean.getSequences() > maxBean.getSequences()) {
            maxBean.setSequences(tempBean.getSequences());
            maxBean.setRankStart(tempBean.getRankStart());
            maxBean.setRankEnd(tempBean.getRankEnd());
        }

        bean.setSequences(maxBean.getSequences());
        bean.setRankStart(maxBean.getRankStart());
        bean.setRankEnd(maxBean.getRankEnd());

        return bean;
    }

}
