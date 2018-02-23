package com.king.app.tcareer.model;

import android.database.Cursor;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.KeyValueCountBean;
import com.king.app.tcareer.model.bean.MatchResultBean;
import com.king.app.tcareer.model.db.Sqls;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/8 9:49
 */
public class GloryDao {

    protected Cursor getCursor(String sql, String[] args) {
        if (args == null) {
            args = new String[]{};
        }
        return TApplication.getInstance().getDaoSession().getDatabase()
                .rawQuery(sql, args);
    }

    public List<Record> getChampionRecords(long userId) {
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        List<Record> list = dao.queryBuilder()
                .where(RecordDao.Properties.UserId.eq(userId)
                    , RecordDao.Properties.Round.eq("Final")
                    , RecordDao.Properties.WinnerFlag.eq(0))
                .orderDesc(RecordDao.Properties.Id)
                .build().list();
        return list;
    }

    public List<Record> getRunnerupRecords(long userId) {
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        List<Record> list = dao.queryBuilder()
                .where(RecordDao.Properties.UserId.eq(userId)
                        , RecordDao.Properties.Round.eq("Final")
                        , RecordDao.Properties.WinnerFlag.eq(1))
                .orderDesc(RecordDao.Properties.Id)
                .build().list();
        return list;
    }

    /**
     * 获取第N条的记录（N按照factor从0开始叠加）
     * @param userId
     * @param factor
     * @param isWinner
     * @param earlierWin
     *@param earlierLose @return
     */
    public List<Record> getTargetRecords(long userId, int factor, boolean isWinner, int earlierWin, int earlierLose) {
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        QueryBuilder<Record> builder = dao.queryBuilder();
        builder.where(RecordDao.Properties.UserId.eq(userId));
        long total;
        if (isWinner) {
            builder.where(RecordDao.Properties.WinnerFlag.eq(0));
        }
        total = builder.buildCount().count();

        int count = factor - 1;
        if (isWinner) {
            count -= earlierWin;
        }
        else {
            count -= (earlierLose + earlierWin);
        }
        List<Record> list = new ArrayList<>();
        while (count < total) {
            Record record = builder.offset(count).limit(1)
                    .build().unique();
            list.add(record);

            count += factor;
        }
        return list;
    }

    public int getCareerRecordNumber(long userId, boolean isWinner) {
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        QueryBuilder<Record> builder = dao.queryBuilder();
        if (isWinner) {
            builder.where(RecordDao.Properties.UserId.eq(userId)
                , RecordDao.Properties.WinnerFlag.eq(0));
        }
        else {
            builder.where(RecordDao.Properties.UserId.eq(userId));
        }
        return (int) builder.buildCount().count();
    }

    public int getYearRecordNumber(long userId, boolean isWinner) {
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        QueryBuilder<Record> builder = dao.queryBuilder();
        if (isWinner) {
            builder.where(RecordDao.Properties.UserId.eq(userId)
                    , RecordDao.Properties.WinnerFlag.eq(0)
                    , RecordDao.Properties.DateStr.like(Calendar.getInstance().get(Calendar.YEAR) + "%"));
        }
        else {
            builder.where(RecordDao.Properties.UserId.eq(userId)
                    , RecordDao.Properties.DateStr.like(Calendar.getInstance().get(Calendar.YEAR) + "%"));
        }
        return (int) builder.buildCount().count();
    }

    /**
     * count title by match level
     * @param userId
     * @param isCurrentYear
     * @return
     */
    public List<KeyValueCountBean> getTitleCountByLevel(long userId, boolean isCurrentYear) {
        List<KeyValueCountBean> list = new ArrayList<>();
        String sql = Sqls.getCountFinalByLevel(userId, String.valueOf(AppConstants.WINNER_USER)
                , isCurrentYear ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR)):null);
        Cursor cursor = getCursor(sql, null);
        while (cursor.moveToNext()) {
            KeyValueCountBean bean = parseKeyValueCount(cursor);
            list.add(bean);
        }
        return list;
    }

    /**
     * count runner-up by match level
     * @param userId
     * @param isCurrentYear
     * @return
     */
    public List<KeyValueCountBean> getRunnerUpCountByLevel(long userId, boolean isCurrentYear) {
        List<KeyValueCountBean> list = new ArrayList<>();
        String sql = Sqls.getCountFinalByLevel(userId, String.valueOf(AppConstants.WINNER_COMPETITOR)
                , isCurrentYear ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR)):null);
        Cursor cursor = getCursor(sql, null);
        while (cursor.moveToNext()) {
            KeyValueCountBean bean = parseKeyValueCount(cursor);
            list.add(bean);
        }
        return list;
    }

    private KeyValueCountBean parseKeyValueCount(Cursor cursor) {
        KeyValueCountBean bean = new KeyValueCountBean();
        bean.setKey(cursor.getString(cursor.getColumnIndex("key")));
        bean.setValue(cursor.getInt(cursor.getColumnIndex("value")));
        return bean;
    }

    /**
     * count title by court
     * @param userId
     * @param isCurrentYear
     * @return
     */
    public List<KeyValueCountBean> getTitleCountByCourt(long userId, boolean isCurrentYear) {
        List<KeyValueCountBean> list = new ArrayList<>();
        String sql = Sqls.getCountFinalByCourt(userId, String.valueOf(AppConstants.WINNER_USER)
                , isCurrentYear ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR)):null);
        Cursor cursor = getCursor(sql, null);
        while (cursor.moveToNext()) {
            KeyValueCountBean bean = parseKeyValueCount(cursor);
            list.add(bean);
        }
        return list;
    }

    /**
     * count runner-up by court
     * @param userId
     * @param isCurrentYear
     * @return
     */
    public List<KeyValueCountBean> getRunnerUpCountByCourt(long userId, boolean isCurrentYear) {
        List<KeyValueCountBean> list = new ArrayList<>();
        String sql = Sqls.getCountFinalByCourt(userId, String.valueOf(AppConstants.WINNER_COMPETITOR)
                , isCurrentYear ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR)):null);
        Cursor cursor = getCursor(sql, null);
        while (cursor.moveToNext()) {
            KeyValueCountBean bean = parseKeyValueCount(cursor);
            list.add(bean);
        }
        return list;
    }

    public Map<String,Integer[]> getGsWinLose(long userId) {
        Map<String, Integer[]> map = new HashMap<>();
        String sql = Sqls.getGroupGSWinLose(userId, null);Cursor cursor = getCursor(sql, null);
        while (cursor.moveToNext()) {
            String key = cursor.getString(1);
            Integer[] count = new Integer[2];
            count[0] = cursor.getInt(2);
            count[1] = cursor.getInt(3);
            map.put(key, count);
        }
        return map;
    }

    public Integer[] getGsCount(long userId, boolean isCurrentYear) {
        String sql = Sqls.getGSWinLose(userId, isCurrentYear ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR)):null);

        Integer[] count = new Integer[2];
        Cursor cursor = getCursor(sql, null);
        if (cursor.moveToNext()) {
            count[0] = cursor.getInt(1);
            count[1] = cursor.getInt(2);
        }

        return count;
    }

    public Integer[] getATP1000Count(long userId, boolean isCurrentYear) {
        String sql = Sqls.getAtp1000WinLose(userId, isCurrentYear ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR)):null);

        Integer[] count = new Integer[2];
        Cursor cursor = getCursor(sql, null);
        if (cursor.moveToNext()) {
            count[0] = cursor.getInt(1);
            count[1] = cursor.getInt(2);
        }

        return count;
    }

    public List<MatchResultBean> getGsResultList(long userId) {
        List<MatchResultBean> list = new ArrayList<>();
        String sql = Sqls.getGsResults(userId);
        Cursor cursor = getCursor(sql, null);
        while (cursor.moveToNext()) {
            MatchResultBean bean = parseMatchResult(cursor);
            list.add(bean);
        }
        return list;
    }

    public List<MatchResultBean> getAtp1000ResultList(long userId) {
        List<MatchResultBean> list = new ArrayList<>();
        String sql = Sqls.getATP1000Results(userId);
        Cursor cursor = getCursor(sql, null);
        while (cursor.moveToNext()) {
            MatchResultBean bean = parseMatchResult(cursor);
            list.add(bean);
        }
        return list;
    }

    private MatchResultBean parseMatchResult(Cursor cursor) {
        MatchResultBean bean = new MatchResultBean();
        bean.setMatchId(cursor.getLong(0));
        bean.setMatchNameId(cursor.getLong(1));
        bean.setMatch(cursor.getString(2));
        bean.setDate(cursor.getString(3));
        bean.setResult(cursor.getString(4));
        return bean;
    }

}
