package com.king.app.tcareer.model.dao;

import android.database.Cursor;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.db.Sqls;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/3 0003 16:23
 */

public class H2HDao {

    /**
     * query player list with h2h order by insert sequence desc
     * @param userId
     * @return
     */
    public Observable<List<H2hBean>> queryH2HListOrderByInsert(long userId) {
        return queryH2HList(Sqls.getH2hOrderByInsert(userId, true));
    }

    /**
     * query player list with h2h
     * @param sql
     * @return
     */
    public Observable<List<H2hBean>> queryH2HList(final String sql) {
        return Observable.create(new ObservableOnSubscribe<List<H2hBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<H2hBean>> e) throws Exception {
                List<H2hBean> list = new ArrayList<>();
                Cursor cursor = TApplication.getInstance().getDaoSession().getDatabase()
                        .rawQuery(sql, new String[]{});
                while (cursor.moveToNext()) {
                    H2hBean bean = parseH2hBean(cursor);
                    list.add(bean);
                }
                cursor.close();

                // load player
                Map<Long, User> userMap = new HashMap<>();
                Map<Long, PlayerBean> playerMap = new HashMap<>();
                UserDao userDao = TApplication.getInstance().getDaoSession().getUserDao();
                PlayerBeanDao playerBeanDao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
                for (H2hBean bean:list) {
                    if (bean.getPlayerFlag() == AppConstants.COMPETITOR_VIRTUAL) {
                        User user = userMap.get(bean.getPlayerId());
                        if (user == null) {
                            user = userDao.queryBuilder()
                                    .where(UserDao.Properties.Id.eq(bean.getPlayerId()))
                                    .build().unique();
                            userMap.put(bean.getPlayerId(), user);
                        }
                        bean.setCompetitor(user);
                    }
                    else {
                        PlayerBean playerBean = playerMap.get(bean.getPlayerId());
                        if (playerBean == null) {
                            playerBean = playerBeanDao.queryBuilder()
                                    .where(PlayerBeanDao.Properties.Id.eq(bean.getPlayerId()))
                                    .build().unique();
                            playerMap.put(bean.getPlayerId(), playerBean);
                        }
                        bean.setCompetitor(playerBean);
                    }
                }

                e.onNext(list);
            }
        });
    }

    private H2hBean parseH2hBean(Cursor cursor) {
        H2hBean bean = new H2hBean();
        bean.setPlayerId(cursor.getLong(0));
        bean.setPlayerFlag(cursor.getInt(1));
        bean.setTotal(cursor.getInt(2));
        bean.setWin(cursor.getInt(3));
        bean.setLose(cursor.getInt(4));
        return bean;
    }

    public Observable<List<Record>> queryH2HRecords(final long userId, final H2hBean h2hBean) {
        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                WhereCondition[] conditions = new WhereCondition[2];
                if (h2hBean.getCompetitor() instanceof User) {
                    conditions[0] = RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_VIRTUAL);
                }
                else {
                    conditions[0] = RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_NORMAL);
                }
                conditions[1] = RecordDao.Properties.PlayerId.eq(h2hBean.getCompetitor().getId());

                RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
                List<Record> list = recordDao.queryBuilder()
                        .where(RecordDao.Properties.UserId.eq(userId)
                            , conditions)
                        .build().list();

                e.onNext(list);
            }
        });
    }

    /**
     * query player list with h2h order by insert sequence desc
     * @param userId
     * @return
     */
    public Observable<H2hBean> queryH2H(final long userId, final long playerId, final boolean competitorIsUser) {
        return Observable.create(new ObservableOnSubscribe<H2hBean>() {
            @Override
            public void subscribe(ObservableEmitter<H2hBean> e) throws Exception {
                H2hBean bean = new H2hBean();
                Cursor cursor = TApplication.getInstance().getDaoSession().getDatabase()
                        .rawQuery(Sqls.getH2h(userId, playerId, competitorIsUser), new String[]{});
                if (cursor.moveToNext()) {
                    bean = parseH2hBean(cursor);
                }
                cursor.close();
                e.onNext(bean);
            }
        });
    }

    /**
     * count top10 win lose
     * @return
     */
    public Integer[] getTotalCount(long userId, int winnerFlag, boolean isThisYear) {
        String sql = Sqls.getAgainstTopCount(userId, winnerFlag, isThisYear ? Calendar.getInstance().get(Calendar.YEAR):0);
        Integer[] count = new Integer[5];
        Cursor cursor = TApplication.getInstance().getDaoSession().getDatabase()
                .rawQuery(sql, new String[]{});
        if (cursor.moveToNext()) {
            count[0] = cursor.getInt(0);
            count[1] = cursor.getInt(1);
            count[2] = cursor.getInt(2);
            count[3] = cursor.getInt(3);
            count[4] = cursor.getInt(4);
        }
        return count;
    }

}
