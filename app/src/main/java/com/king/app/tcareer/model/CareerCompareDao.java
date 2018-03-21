package com.king.app.tcareer.model;

import android.database.Cursor;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.KeyValueCountBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.page.record.complex.CompareItem;
import com.king.app.tcareer.utils.FormatUtil;
import com.king.app.tcareer.utils.ListUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/20 14:24
 */
public class CareerCompareDao extends GloryDao {

    public CompareItem getTotalChampions() {
        CompareItem item = new CompareItem();
        item.setTitle("总冠军数");
        item.setValueK(String.valueOf(getChampionCount(AppConstants.USER_ID_KING)));
        item.setValueF(String.valueOf(getChampionCount(AppConstants.USER_ID_FLAMENCO)));
        item.setValueH(String.valueOf(getChampionCount(AppConstants.USER_ID_HENRY)));
        item.setValueQ(String.valueOf(getChampionCount(AppConstants.USER_ID_QI)));
        return item;
    }

    /**
     * champion number group by match court
     * @return
     */
    public List<CompareItem> getChampionsByCourt() {

        List<CompareItem> items = new ArrayList<>();
        String[] array = AppConstants.RECORD_MATCH_COURTS;
        for (int i = 0; i < array.length; i ++) {
            CompareItem item = new CompareItem();
            item.setTitle(array[i]);
            items.add(item);
        }

        List<KeyValueCountBean> beans = getTitleCountByCourt(AppConstants.USER_ID_KING, false);
        for (KeyValueCountBean bean:beans) {
            for (int i = 0; i < array.length; i ++) {
                if (bean.getKey().equals(array[i])) {
                    items.get(i).setValueK(String.valueOf(bean.getValue()));
                }
            }
        }
        beans = getTitleCountByCourt(AppConstants.USER_ID_FLAMENCO, false);
        for (KeyValueCountBean bean:beans) {
            for (int i = 0; i < array.length; i ++) {
                if (bean.getKey().equals(array[i])) {
                    items.get(i).setValueF(String.valueOf(bean.getValue()));
                }
            }
        }
        beans = getTitleCountByCourt(AppConstants.USER_ID_HENRY, false);
        for (KeyValueCountBean bean:beans) {
            for (int i = 0; i < array.length; i ++) {
                if (bean.getKey().equals(array[i])) {
                    items.get(i).setValueH(String.valueOf(bean.getValue()));
                }
            }
        }
        beans = getTitleCountByCourt(AppConstants.USER_ID_QI, false);
        for (KeyValueCountBean bean:beans) {
            for (int i = 0; i < array.length; i ++) {
                if (bean.getKey().equals(array[i])) {
                    items.get(i).setValueQ(String.valueOf(bean.getValue()));
                }
            }
        }
        return items;
    }

    /**
     * champion number group by match level
     * @return
     */
    public List<CompareItem> getChampionsByLevel() {

        List<CompareItem> items = new ArrayList<>();
        String[] array = AppConstants.RECORD_MATCH_LEVELS;
        for (int i = 0; i < array.length; i ++) {
            CompareItem item = new CompareItem();
            item.setTitle(array[i]);
            items.add(item);
        }

        List<KeyValueCountBean> beans = getTitleCountByLevel(AppConstants.USER_ID_KING, false);
        for (KeyValueCountBean bean:beans) {
            for (int i = 0; i < array.length; i ++) {
                if (bean.getKey().equals(array[i])) {
                    items.get(i).setValueK(String.valueOf(bean.getValue()));
                }
            }
        }
        beans = getTitleCountByLevel(AppConstants.USER_ID_FLAMENCO, false);
        for (KeyValueCountBean bean:beans) {
            for (int i = 0; i < array.length; i ++) {
                if (bean.getKey().equals(array[i])) {
                    items.get(i).setValueF(String.valueOf(bean.getValue()));
                }
            }
        }
        beans = getTitleCountByLevel(AppConstants.USER_ID_HENRY, false);
        for (KeyValueCountBean bean:beans) {
            for (int i = 0; i < array.length; i ++) {
                if (bean.getKey().equals(array[i])) {
                    items.get(i).setValueH(String.valueOf(bean.getValue()));
                }
            }
        }
        beans = getTitleCountByLevel(AppConstants.USER_ID_QI, false);
        for (KeyValueCountBean bean:beans) {
            for (int i = 0; i < array.length; i ++) {
                if (bean.getKey().equals(array[i])) {
                    items.get(i).setValueQ(String.valueOf(bean.getValue()));
                }
            }
        }
        return items;
    }

    private int getChampionCount(long userId) {
        String sql = "SELECT count(_id) FROM match_records\n" +
                " WHERE user_id=? AND round=? AND winner_flag=?";
        String[] args = new String[] {
                String.valueOf(userId), AppConstants.RECORD_MATCH_ROUNDS[0], String.valueOf(AppConstants.WINNER_USER)
        };
        return parseCount(sql, args);
    }

    private int getRunnerupCount(long userId) {
        String sql = "SELECT count(_id) FROM match_records\n" +
                " WHERE user_id=? AND round=? AND winner_flag=?";
        String[] args = new String[] {
                String.valueOf(userId), AppConstants.RECORD_MATCH_ROUNDS[0], String.valueOf(AppConstants.WINNER_COMPETITOR)
        };
        return parseCount(sql, args);
    }

    private int getFinalCount(long userId) {
        String sql = "SELECT count(_id) FROM match_records\n" +
                " WHERE user_id=? AND round=?";
        String[] args = new String[] {
                String.valueOf(userId), AppConstants.RECORD_MATCH_ROUNDS[0]
        };
        return parseCount(sql, args);
    }

    private int getFinalCount(long userId, String level) {
        String sql = "SELECT count(*) FROM match_records\n" +
                " JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                " JOIN matches ON match_names.match_id=matches._id" +
                " WHERE user_id=? AND round=? AND matches.level=?";
        String[] args = new String[] {
                String.valueOf(userId), AppConstants.RECORD_MATCH_ROUNDS[0], level
        };
        return parseCount(sql, args);
    }

    private int getRunnerupCount(long userId, String level) {
        String sql = "SELECT count(*) FROM match_records\n" +
                " JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                " JOIN matches ON match_names.match_id=matches._id" +
                " WHERE user_id=? AND round=? AND winner_flag=? AND matches.level=?";
        String[] args = new String[] {
                String.valueOf(userId), AppConstants.RECORD_MATCH_ROUNDS[0], String.valueOf(AppConstants.WINNER_COMPETITOR), level
        };
        return parseCount(sql, args);
    }

    private int getSfCount(long userId, String level) {
        String sql = "SELECT count(*) FROM match_records\n" +
                " JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                " JOIN matches ON match_names.match_id=matches._id" +
                " WHERE user_id=? AND round=? AND winner_flag=? AND matches.level=?";
        String[] args = new String[] {
                String.valueOf(userId), AppConstants.RECORD_MATCH_ROUNDS[1], String.valueOf(AppConstants.WINNER_COMPETITOR), level
        };
        return parseCount(sql, args);
    }

    /**
     *
     * @param sql
     * @param args
     * @return
     */
    private int parseCount(String sql, String[] args) {
        int result = 0;
        Cursor cursor = getCursor(sql, args);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    /**
     * 大满贯/大师杯/大师赛/奥运会
     * @return
     */
    public List<CompareItem> getImportantFinals() {
        List<CompareItem> items = new ArrayList<>();
        CompareItem item = new CompareItem();
        item.setTitle("决赛总数");
        item.setValueK(String.valueOf(getFinalCount(AppConstants.USER_ID_KING)));
        item.setValueF(String.valueOf(getFinalCount(AppConstants.USER_ID_FLAMENCO)));
        item.setValueH(String.valueOf(getFinalCount(AppConstants.USER_ID_HENRY)));
        item.setValueQ(String.valueOf(getFinalCount(AppConstants.USER_ID_QI)));
        items.add(item);
        // gs
        item = new CompareItem();
        item.setTitle("大满贯决赛");
        String level = AppConstants.RECORD_MATCH_LEVELS[0];
        item.setValueK(String.valueOf(getFinalCount(AppConstants.USER_ID_KING, level)));
        item.setValueF(String.valueOf(getFinalCount(AppConstants.USER_ID_FLAMENCO, level)));
        item.setValueH(String.valueOf(getFinalCount(AppConstants.USER_ID_HENRY, level)));
        item.setValueQ(String.valueOf(getFinalCount(AppConstants.USER_ID_QI, level)));
        items.add(item);
        // mc
        item = new CompareItem();
        item.setTitle("大师杯决赛");
        level = AppConstants.RECORD_MATCH_LEVELS[1];
        item.setValueK(String.valueOf(getFinalCount(AppConstants.USER_ID_KING, level)));
        item.setValueF(String.valueOf(getFinalCount(AppConstants.USER_ID_FLAMENCO, level)));
        item.setValueH(String.valueOf(getFinalCount(AppConstants.USER_ID_HENRY, level)));
        item.setValueQ(String.valueOf(getFinalCount(AppConstants.USER_ID_QI, level)));
        items.add(item);
        // 1000
        item = new CompareItem();
        item.setTitle("大师赛决赛");
        level = AppConstants.RECORD_MATCH_LEVELS[2];
        item.setValueK(String.valueOf(getFinalCount(AppConstants.USER_ID_KING, level)));
        item.setValueF(String.valueOf(getFinalCount(AppConstants.USER_ID_FLAMENCO, level)));
        item.setValueH(String.valueOf(getFinalCount(AppConstants.USER_ID_HENRY, level)));
        item.setValueQ(String.valueOf(getFinalCount(AppConstants.USER_ID_QI, level)));
        items.add(item);
        // olympic
        item = new CompareItem();
        item.setTitle("奥运会决赛");
        level = AppConstants.RECORD_MATCH_LEVELS[6];
        item.setValueK(String.valueOf(getFinalCount(AppConstants.USER_ID_KING, level)));
        item.setValueF(String.valueOf(getFinalCount(AppConstants.USER_ID_FLAMENCO, level)));
        item.setValueH(String.valueOf(getFinalCount(AppConstants.USER_ID_HENRY, level)));
        item.setValueQ(String.valueOf(getFinalCount(AppConstants.USER_ID_QI, level)));
        items.add(item);

        item = new CompareItem();
        item.setTitle("亚军");
        item.setValueK(String.valueOf(getRunnerupCount(AppConstants.USER_ID_KING)));
        item.setValueF(String.valueOf(getRunnerupCount(AppConstants.USER_ID_FLAMENCO)));
        item.setValueH(String.valueOf(getRunnerupCount(AppConstants.USER_ID_HENRY)));
        item.setValueQ(String.valueOf(getRunnerupCount(AppConstants.USER_ID_QI)));
        items.add(item);
        // gs
        item = new CompareItem();
        item.setTitle("大满贯亚军");
        level = AppConstants.RECORD_MATCH_LEVELS[0];
        item.setValueK(String.valueOf(getRunnerupCount(AppConstants.USER_ID_KING, level)));
        item.setValueF(String.valueOf(getRunnerupCount(AppConstants.USER_ID_FLAMENCO, level)));
        item.setValueH(String.valueOf(getRunnerupCount(AppConstants.USER_ID_HENRY, level)));
        item.setValueQ(String.valueOf(getRunnerupCount(AppConstants.USER_ID_QI, level)));
        items.add(item);
        // mc
        item = new CompareItem();
        item.setTitle("大师杯亚军");
        level = AppConstants.RECORD_MATCH_LEVELS[1];
        item.setValueK(String.valueOf(getRunnerupCount(AppConstants.USER_ID_KING, level)));
        item.setValueF(String.valueOf(getRunnerupCount(AppConstants.USER_ID_FLAMENCO, level)));
        item.setValueH(String.valueOf(getRunnerupCount(AppConstants.USER_ID_HENRY, level)));
        item.setValueQ(String.valueOf(getRunnerupCount(AppConstants.USER_ID_QI, level)));
        items.add(item);
        // atp1000
        item = new CompareItem();
        item.setTitle("大师赛亚军");
        level = AppConstants.RECORD_MATCH_LEVELS[2];
        item.setValueK(String.valueOf(getRunnerupCount(AppConstants.USER_ID_KING, level)));
        item.setValueF(String.valueOf(getRunnerupCount(AppConstants.USER_ID_FLAMENCO, level)));
        item.setValueH(String.valueOf(getRunnerupCount(AppConstants.USER_ID_HENRY, level)));
        item.setValueQ(String.valueOf(getRunnerupCount(AppConstants.USER_ID_QI, level)));
        items.add(item);
        // olympic
        item = new CompareItem();
        item.setTitle("奥运会亚军");
        level = AppConstants.RECORD_MATCH_LEVELS[2];
        item.setValueK(String.valueOf(getRunnerupCount(AppConstants.USER_ID_KING, level)));
        item.setValueF(String.valueOf(getRunnerupCount(AppConstants.USER_ID_FLAMENCO, level)));
        item.setValueH(String.valueOf(getRunnerupCount(AppConstants.USER_ID_HENRY, level)));
        item.setValueQ(String.valueOf(getRunnerupCount(AppConstants.USER_ID_QI, level)));
        items.add(item);
        return items;
    }

    public List<CompareItem> getImportantSF() {
        List<CompareItem> items = new ArrayList<>();
        // gs
        CompareItem item = new CompareItem();
        item.setTitle("大满贯四强");
        String level = AppConstants.RECORD_MATCH_LEVELS[0];
        item.setValueK(String.valueOf(getSfCount(AppConstants.USER_ID_KING, level)));
        item.setValueF(String.valueOf(getSfCount(AppConstants.USER_ID_FLAMENCO, level)));
        item.setValueH(String.valueOf(getSfCount(AppConstants.USER_ID_HENRY, level)));
        item.setValueQ(String.valueOf(getSfCount(AppConstants.USER_ID_QI, level)));
        items.add(item);
        return items;
    }

    /**
     * 抢七盘数、胜率
     * @return
     */
    public List<CompareItem> getTiebreakData() {
        List<CompareItem> items = new ArrayList<>();
        CompareItem item = new CompareItem();
        item.setTitle("抢七数据");
        Object[] data = tiebreak(AppConstants.USER_ID_KING);
        item.setValueK(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = tiebreak(AppConstants.USER_ID_FLAMENCO);
        item.setValueF(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = tiebreak(AppConstants.USER_ID_HENRY);
        item.setValueH(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = tiebreak(AppConstants.USER_ID_QI);
        item.setValueQ(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        items.add(item);
        return items;
    }

    /**
     *
     * @param userId
     * @return 0:win, 1:lose, 2:rate
     */
    private Object[] tiebreak(long userId) {
        Object[] results = new Object[3];
        String sql = "select count(scores._id) as count\n" +
                ", sum(case when user_point>competitor_point then 1 else 0 end) as win\n" +
                ", sum(case when user_point>competitor_point then 0 else 1 end) as lose\n" +
                " from scores join match_records\n" +
                " on scores.record_id=match_records._id\n" +
                " where is_tiebreak=1 and match_records.user_id=?";
        String[] args = new String[] {
                String.valueOf(userId)
        };
        Cursor cursor = getCursor(sql, args);
        if (cursor.moveToNext()) {
            int total = cursor.getInt(0);
            int win = cursor.getInt(1);
            int lose = cursor.getInt(2);
            results[0] = win;
            results[1] = lose;
            results[2] = (double) win / (double) total;
        }
        return results;
    }

    /**
     * 对阵排名胜率
     * @return
     */
    public List<CompareItem> getCompetitorData() {
        List<CompareItem> items = new ArrayList<>();
        CompareItem item = new CompareItem();
        item.setTitle("对阵No.1");
        Object[] data = rankBetween(AppConstants.USER_ID_KING, 1, 1);
        item.setValueK(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_FLAMENCO, 1, 1);
        item.setValueF(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_HENRY, 1, 1);
        item.setValueH(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_QI, 1, 1);
        item.setValueQ(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        items.add(item);

        item = new CompareItem();
        item.setTitle("对阵Top 2");
        data = rankBetween(AppConstants.USER_ID_KING, 1, 2);
        item.setValueK(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_FLAMENCO, 1, 2);
        item.setValueF(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_HENRY, 1, 2);
        item.setValueH(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_QI, 1, 2);
        item.setValueQ(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        items.add(item);

        item = new CompareItem();
        item.setTitle("对阵Top 3");
        data = rankBetween(AppConstants.USER_ID_KING, 1, 3);
        item.setValueK(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_FLAMENCO, 1, 3);
        item.setValueF(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_HENRY, 1, 3);
        item.setValueH(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_QI, 1, 3);
        item.setValueQ(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        items.add(item);

        item = new CompareItem();
        item.setTitle("对阵Top 5");
        data = rankBetween(AppConstants.USER_ID_KING, 1, 5);
        item.setValueK(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_FLAMENCO, 1, 5);
        item.setValueF(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_HENRY, 1, 5);
        item.setValueH(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_QI, 1, 5);
        item.setValueQ(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        items.add(item);

        item = new CompareItem();
        item.setTitle("对阵Top 10");
        data = rankBetween(AppConstants.USER_ID_KING, 1, 10);
        item.setValueK(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_FLAMENCO, 1, 10);
        item.setValueF(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_HENRY, 1, 10);
        item.setValueH(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = rankBetween(AppConstants.USER_ID_QI, 1, 10);
        item.setValueQ(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        items.add(item);
        return items;
    }

    /**
     *
     * @param userId
     * @return 0:win, 1:lose, 2:rate
     */
    private Object[] rankBetween(long userId, int top, int bottom) {
        Object[] results = new Object[3];
        String sql = "select sum(case when winner_flag=0 then 1 else 0 end) as win\n" +
                " ,sum(case when winner_flag=0 then 0 else 1 end) as lose\n" +
                " from match_records\n" +
                " where user_id=? and rank_cpt>=? and rank_cpt<=?";
        String[] args = new String[] {
                String.valueOf(userId), String.valueOf(top), String.valueOf(bottom)
        };
        Cursor cursor = getCursor(sql, args);
        if (cursor.moveToNext()) {
            int win = cursor.getInt(0);
            int lose = cursor.getInt(1);
            results[0] = win;
            results[1] = lose;
            results[2] = (double) win / (double) (win + lose);
        }
        return results;
    }

    /**
     * 赛事级别胜率
     * @return
     */
    public List<CompareItem> getMatchData() {
        List<CompareItem> items = new ArrayList<>();
        CompareItem item = new CompareItem();
        item.setTitle("全部");
        Object[] data = winLoseTotal(AppConstants.USER_ID_KING);
        item.setValueK(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = winLoseTotal(AppConstants.USER_ID_FLAMENCO);
        item.setValueF(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = winLoseTotal(AppConstants.USER_ID_HENRY);
        item.setValueH(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        data = winLoseTotal(AppConstants.USER_ID_QI);
        item.setValueQ(data[0] + "-" + data[1] + "\n" + FormatUtil.round2((Double) data[2] * 100) + "%");
        items.add(item);

        getMatchLevelGroup(items);

        getMatchCourtGroup(items);
        return items;
    }

    /**
     *
     * @param userId
     * @return 0:win, 1:lose, 2:rate
     */
    private Object[] winLoseTotal(long userId) {
        Object[] results = new Object[3];
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        int win = (int) dao.queryBuilder()
                .where(RecordDao.Properties.UserId.eq(userId)
                    , RecordDao.Properties.WinnerFlag.eq(AppConstants.WINNER_USER))
                .buildCount().count();
        int lose = (int) dao.queryBuilder()
                .where(RecordDao.Properties.UserId.eq(userId)
                        , RecordDao.Properties.WinnerFlag.eq(AppConstants.WINNER_COMPETITOR))
                .buildCount().count();
        results[0] = win;
        results[1] = lose;
        results[2] = (double) win / (double) (win + lose);
        return results;
    }

    /**
     *
     * @param userId
     * @return 0:win, 1:lose, 2:rate
     */
    private Cursor winLoseByLevel(long userId) {
        String sql = "SELECT matches.level, sum(CASE WHEN winner_flag=0 THEN 1 ELSE 0 END) AS win , sum(CASE WHEN winner_flag=0 THEN 0 ELSE 1 END) AS lose \n" +
                "                 FROM match_records\n" +
                "                 JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                "                 JOIN matches ON match_names.match_id=matches._id\n" +
                "                 WHERE match_records.user_id=?\n" +
                "                 GROUP BY matches.level";
        String[] args = new String[] {
                String.valueOf(userId)
        };
        return getCursor(sql, args);
    }

    public void getMatchLevelGroup(List<CompareItem> items) {

        String[] levels = AppConstants.RECORD_MATCH_LEVELS;
        Map<String, CompareItem> map = new HashMap<>();
        Cursor cursor = winLoseByLevel(AppConstants.USER_ID_KING);
        while (cursor.moveToNext()) {
            String level = cursor.getString(0);
            CompareItem item = map.get(level);
            if (item == null) {
                item = new CompareItem();
                item.setTitle(level);
                map.put(level, item);
            }
            int win = cursor.getInt(1);
            int lose = cursor.getInt(2);
            double rate = (double) win / (double) (win + lose);
            String value = win + "-" + lose + "\n" + FormatUtil.round2(rate * 100) + "%";
            item.setValueK(value);
        }
        cursor = winLoseByLevel(AppConstants.USER_ID_FLAMENCO);
        while (cursor.moveToNext()) {
            String level = cursor.getString(0);
            CompareItem item = map.get(level);
            if (item == null) {
                item = new CompareItem();
                item.setTitle(level);
                map.put(level, item);
            }
            int win = cursor.getInt(1);
            int lose = cursor.getInt(2);
            double rate = (double) win / (double) (win + lose);
            String value = win + "-" + lose + "\n" + FormatUtil.round2(rate * 100) + "%";
            item.setValueF(value);
        }
        cursor = winLoseByLevel(AppConstants.USER_ID_HENRY);
        while (cursor.moveToNext()) {
            String level = cursor.getString(0);
            CompareItem item = map.get(level);
            if (item == null) {
                item = new CompareItem();
                item.setTitle(level);
                map.put(level, item);
            }
            int win = cursor.getInt(1);
            int lose = cursor.getInt(2);
            double rate = (double) win / (double) (win + lose);
            String value = win + "-" + lose + "\n" + FormatUtil.round2(rate * 100) + "%";
            item.setValueH(value);
        }
        cursor = winLoseByLevel(AppConstants.USER_ID_QI);
        while (cursor.moveToNext()) {
            String level = cursor.getString(0);
            CompareItem item = map.get(level);
            if (item == null) {
                item = new CompareItem();
                item.setTitle(level);
                map.put(level, item);
            }
            int win = cursor.getInt(1);
            int lose = cursor.getInt(2);
            double rate = (double) win / (double) (win + lose);
            String value = win + "-" + lose + "\n" + FormatUtil.round2(rate * 100) + "%";
            item.setValueQ(value);
        }
        for (String level:levels) {
            CompareItem item = map.get(level);
            if (item != null) {
                items.add(item);
            }
        }
    }

    /**
     *
     * @param userId
     * @return 0:win, 1:lose, 2:rate
     */
    private Cursor winLoseByCourt(long userId) {
        String sql = "SELECT matches.court, sum(CASE WHEN winner_flag=0 THEN 1 ELSE 0 END) AS win , sum(CASE WHEN winner_flag=0 THEN 0 ELSE 1 END) AS lose \n" +
                "                 FROM match_records\n" +
                "                 JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                "                 JOIN matches ON match_names.match_id=matches._id\n" +
                "                 WHERE match_records.user_id=?\n" +
                "                 GROUP BY matches.court";
        String[] args = new String[] {
                String.valueOf(userId)
        };
        return getCursor(sql, args);
    }

    public void getMatchCourtGroup(List<CompareItem> items) {

        String[] courts = AppConstants.RECORD_MATCH_COURTS;
        Map<String, CompareItem> map = new HashMap<>();
        Cursor cursor = winLoseByCourt(AppConstants.USER_ID_KING);
        while (cursor.moveToNext()) {
            String level = cursor.getString(0);
            CompareItem item = map.get(level);
            if (item == null) {
                item = new CompareItem();
                item.setTitle(level);
                map.put(level, item);
            }
            int win = cursor.getInt(1);
            int lose = cursor.getInt(2);
            double rate = (double) win / (double) (win + lose);
            String value = win + "-" + lose + "\n" + FormatUtil.round2(rate * 100) + "%";
            item.setValueK(value);
        }
        cursor = winLoseByCourt(AppConstants.USER_ID_FLAMENCO);
        while (cursor.moveToNext()) {
            String level = cursor.getString(0);
            CompareItem item = map.get(level);
            if (item == null) {
                item = new CompareItem();
                item.setTitle(level);
                map.put(level, item);
            }
            int win = cursor.getInt(1);
            int lose = cursor.getInt(2);
            double rate = (double) win / (double) (win + lose);
            String value = win + "-" + lose + "\n" + FormatUtil.round2(rate * 100) + "%";
            item.setValueF(value);
        }
        cursor = winLoseByCourt(AppConstants.USER_ID_HENRY);
        while (cursor.moveToNext()) {
            String level = cursor.getString(0);
            CompareItem item = map.get(level);
            if (item == null) {
                item = new CompareItem();
                item.setTitle(level);
                map.put(level, item);
            }
            int win = cursor.getInt(1);
            int lose = cursor.getInt(2);
            double rate = (double) win / (double) (win + lose);
            String value = win + "-" + lose + "\n" + FormatUtil.round2(rate * 100) + "%";
            item.setValueH(value);
        }
        cursor = winLoseByCourt(AppConstants.USER_ID_QI);
        while (cursor.moveToNext()) {
            String level = cursor.getString(0);
            CompareItem item = map.get(level);
            if (item == null) {
                item = new CompareItem();
                item.setTitle(level);
                map.put(level, item);
            }
            int win = cursor.getInt(1);
            int lose = cursor.getInt(2);
            double rate = (double) win / (double) (win + lose);
            String value = win + "-" + lose + "\n" + FormatUtil.round2(rate * 100) + "%";
            item.setValueQ(value);
        }
        for (String court:courts) {
            CompareItem item = map.get(court);
            if (item != null) {
                items.add(item);
            }
        }
    }

    /**
     * 落后一盘逆转
     * @return
     */
    public CompareItem getReverse() {
        CompareItem item = new CompareItem();
        item.setTitle("落后一盘逆转");
        int number = queryReverse(AppConstants.USER_ID_KING);
        int total = querySet1(AppConstants.USER_ID_KING, false);
        double rate = (double) number / (double) total;
        item.setValueK(number + "/" + total + "\n" + FormatUtil.round2(rate * 100) + "%");

        number = queryReverse(AppConstants.USER_ID_FLAMENCO);
        total = querySet1(AppConstants.USER_ID_FLAMENCO, false);
        rate = (double) number / (double) total;
        item.setValueF(number + "/" + total + "\n" + FormatUtil.round2(rate * 100) + "%");

        number = queryReverse(AppConstants.USER_ID_HENRY);
        total = querySet1(AppConstants.USER_ID_HENRY, false);
        rate = (double) number / (double) total;
        item.setValueH(number + "/" + total + "\n" + FormatUtil.round2(rate * 100) + "%");

        number = queryReverse(AppConstants.USER_ID_QI);
        total = querySet1(AppConstants.USER_ID_QI, false);
        rate = (double) number / (double) total;
        item.setValueQ(number + "/" + total + "\n" + FormatUtil.round2(rate * 100) + "%");
        return item;
    }

    /**
     * 领先一盘被逆转
     * @return
     */
    public CompareItem getBeReversed() {
        CompareItem item = new CompareItem();
        item.setTitle("领先一盘被逆转");
        int number = queryBeReversed(AppConstants.USER_ID_KING);
        int total = querySet1(AppConstants.USER_ID_KING, true);
        double rate = (double) number / (double) total;
        item.setValueK(number + "/" + total + "\n" + FormatUtil.round2(rate * 100) + "%");

        number = queryBeReversed(AppConstants.USER_ID_FLAMENCO);
        total = querySet1(AppConstants.USER_ID_FLAMENCO, true);
        rate = (double) number / (double) total;
        item.setValueF(number + "/" + total + "\n" + FormatUtil.round2(rate * 100) + "%");

        number = queryBeReversed(AppConstants.USER_ID_HENRY);
        total = querySet1(AppConstants.USER_ID_HENRY, true);
        rate = (double) number / (double) total;
        item.setValueH(number + "/" + total + "\n" + FormatUtil.round2(rate * 100) + "%");

        number = queryBeReversed(AppConstants.USER_ID_QI);
        total = querySet1(AppConstants.USER_ID_QI, true);
        rate = (double) number / (double) total;
        item.setValueQ(number + "/" + total + "\n" + FormatUtil.round2(rate * 100) + "%");
        return item;
    }

    public int queryReverse(long userId) {
        int result = 0;
        String sql = "SELECT count(*) from scores\n" +
                " join match_records on scores.record_id = match_records._id\n" +
                " where user_id=?\n" +
                " and set_no = 1 and user_point<competitor_point and match_records.winner_flag=0";
        String[] args = new String[] {
                String.valueOf(userId)
        };
        Cursor cursor = getCursor(sql, args);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    public int queryBeReversed(long userId) {
        int result = 0;
        String sql = "SELECT count(*) from scores\n" +
                " join match_records on scores.record_id = match_records._id\n" +
                " where user_id=?\n" +
                " and set_no = 1 and user_point>competitor_point and match_records.winner_flag=1";
        String[] args = new String[] {
                String.valueOf(userId)
        };
        Cursor cursor = getCursor(sql, args);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    public int querySet1(long userId, boolean isWinner) {
        int result = 0;
        String sql = "SELECT count(*) from scores\n" +
                " join match_records on scores.record_id = match_records._id\n" +
                " where user_id=?\n" +
                " and set_no = 1";
        if (isWinner) {
            sql = sql + " and user_point>competitor_point";
        }
        else {
            sql = sql + " and user_point<competitor_point";
        }
        String[] args = new String[] {
                String.valueOf(userId)
        };
        Cursor cursor = getCursor(sql, args);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    /**
     * 6-0次数(按比赛)
     * @return
     */
    public CompareItem getBagel() {
        CompareItem item = new CompareItem();
        item.setTitle("6-0次数(按比赛)");
        item.setValueK(String.valueOf(queryBagel(AppConstants.USER_ID_KING)));
        item.setValueF(String.valueOf(queryBagel(AppConstants.USER_ID_FLAMENCO)));
        item.setValueH(String.valueOf(queryBagel(AppConstants.USER_ID_HENRY)));
        item.setValueQ(String.valueOf(queryBagel(AppConstants.USER_ID_QI)));
        return item;
    }

    public int queryBagel(long userId) {
        int result = 0;
        String sql = "select count(*) from\n" +
                " (select count(*) from scores\n" +
                " join match_records on scores.record_id = match_records._id\n" +
                " where user_id=?\n" +
                " and user_point=6 and competitor_point=0\n" +
                " group by record_id)";
        String[] args = new String[] {
                String.valueOf(userId)
        };
        Cursor cursor = getCursor(sql, args);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    /**
     * 6-0次数(按比赛)
     * @return
     */
    public CompareItem getBeBagel() {
        CompareItem item = new CompareItem();
        item.setTitle("0-6次数(按比赛)");
        item.setValueK(String.valueOf(queryBeBagel(AppConstants.USER_ID_KING)));
        item.setValueF(String.valueOf(queryBeBagel(AppConstants.USER_ID_FLAMENCO)));
        item.setValueH(String.valueOf(queryBeBagel(AppConstants.USER_ID_HENRY)));
        item.setValueQ(String.valueOf(queryBeBagel(AppConstants.USER_ID_QI)));
        return item;
    }

    public int queryBeBagel(long userId) {
        int result = 0;
        String sql = "select count(*) from\n" +
                " (select count(*) from scores\n" +
                " join match_records on scores.record_id = match_records._id\n" +
                " where user_id=?\n" +
                " and user_point=0 and competitor_point=6\n" +
                " group by record_id)";
        String[] args = new String[] {
                String.valueOf(userId)
        };
        Cursor cursor = getCursor(sql, args);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    /**
     * 6-0次数(按比赛)
     * @return
     */
    public CompareItem getTibreakMatch() {
        CompareItem item = new CompareItem();
        item.setTitle("抢七次数(按比赛)");
        item.setValueK(String.valueOf(queryTiebreakMatch(AppConstants.USER_ID_KING)));
        item.setValueF(String.valueOf(queryTiebreakMatch(AppConstants.USER_ID_FLAMENCO)));
        item.setValueH(String.valueOf(queryTiebreakMatch(AppConstants.USER_ID_HENRY)));
        item.setValueQ(String.valueOf(queryTiebreakMatch(AppConstants.USER_ID_QI)));
        return item;
    }

    public int queryTiebreakMatch(long userId) {
        int result = 0;
        String sql = "select count(*) from\n" +
                " (select count(*) from scores\n" +
                " join match_records on scores.record_id = match_records._id\n" +
                " where user_id=?\n" +
                " and is_tiebreak=1\n" +
                " group by record_id)";
        String[] args = new String[] {
                String.valueOf(userId)
        };
        Cursor cursor = getCursor(sql, args);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    public List<CompareItem> getFinalSet() {
        List<CompareItem> list = new ArrayList<>();
        CompareItem item = new CompareItem();
        item.setTitle("决胜盘");
        list.add(item);
        CompareItem gsItem = new CompareItem();
        gsItem.setTitle("大满贯决胜盘");
        list.add(gsItem);
        CompareItem normalItem = new CompareItem();
        normalItem.setTitle("巡回赛决胜盘");
        list.add(normalItem);

        String[] data = queryFinalSet(AppConstants.USER_ID_KING);
        item.setValueK(data[0]);
        gsItem.setValueK(data[1]);
        normalItem.setValueK(data[2]);

        data = queryFinalSet(AppConstants.USER_ID_FLAMENCO);
        item.setValueF(data[0]);
        gsItem.setValueF(data[1]);
        normalItem.setValueF(data[2]);

        data = queryFinalSet(AppConstants.USER_ID_HENRY);
        item.setValueH(data[0]);
        gsItem.setValueH(data[1]);
        normalItem.setValueH(data[2]);

        data = queryFinalSet(AppConstants.USER_ID_QI);
        item.setValueQ(data[0]);
        gsItem.setValueQ(data[1]);
        normalItem.setValueQ(data[2]);
        return list;
    }

    /**
     *
     * @param userId
     * @return 0:total, 1:gs, 2:normal
     */
    public String[] queryFinalSet(long userId) {
        String[] results = new String[3];
        String sql = "select count(*), sum(case when winner_flag=0 then 1 else 0 end) as win from scores\n" +
                " join match_records on scores.record_id = match_records._id\n" +
                " JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                " JOIN matches ON match_names.match_id=matches._id\n" +
                " where user_id=? and set_no=?";

        // 大满贯是5盘
        String gsSql = sql + " and level='Grand Slam'";
        String[] args = new String[] {
                String.valueOf(userId), "5"
        };
        int gsTotal = 0;
        int gsWin = 0;
        Cursor cursor = getCursor(sql, args);
        if (cursor.moveToNext()) {
            gsTotal = cursor.getInt(0);
            gsWin = cursor.getInt(1);
        }
        double rate = (double) gsWin / (double) gsTotal;
        results[1] = gsWin + "/" + gsTotal + "\n" + FormatUtil.round2(rate * 100) + "%";

        // 巡回赛是3盘
        String normalSql = sql + " and level!='Grand Slam'";
        args = new String[] {
                String.valueOf(userId), "3"
        };
        int normalTotal = 0;
        int normalWin = 0;
        cursor = getCursor(sql, args);
        if (cursor.moveToNext()) {
            normalTotal = cursor.getInt(0);
            normalWin = cursor.getInt(1);
        }
        rate = (double) normalWin / (double) normalTotal;
        results[2] = normalWin + "/" + normalTotal + "\n" + FormatUtil.round2(rate * 100) + "%";

        rate = (double) (gsWin + normalWin) / (double) (gsTotal + normalTotal);
        results[0] = (gsWin + normalWin) + "/" + (gsTotal + normalTotal) + "\n" + FormatUtil.round2(rate * 100) + "%";
        return results;
    }

    public CompareItem getLongestWin() {
        CompareItem item = new CompareItem();
        item.setTitle("最长连胜");
        item.setValueK(String.valueOf(queryLongestWin(AppConstants.USER_ID_KING)));
        item.setValueF(String.valueOf(queryLongestWin(AppConstants.USER_ID_FLAMENCO)));
        item.setValueH(String.valueOf(queryLongestWin(AppConstants.USER_ID_HENRY)));
        item.setValueQ(String.valueOf(queryLongestWin(AppConstants.USER_ID_QI)));
        return item;
    }

    /**
     * 最长连胜纪录
     */
    private class LongestWin {
        // 开始的赛事
        Record startRecord;
        // 结束的赛事
        Record endRecord;
        // 连胜场数
        int count;
    }

    /**
     * 统计最长连胜纪录（支持并列）
     * @param userId
     * @return
     */
    public String queryLongestWin(long userId) {
        StringBuffer buffer = new StringBuffer();
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        List<Record> list = dao.queryBuilder()
                .where(RecordDao.Properties.UserId.eq(userId))
                .build().list();

        int lastFlag = -1;
        int max = 0;
        int count = 0;
        List<LongestWin> winList = new ArrayList<>();
        LongestWin lastLongestWin = null;
        for (int i = 0; i < list.size(); i ++) {
            int winnerFlag = list.get(i).getWinnerFlag();
            if (winnerFlag == AppConstants.WINNER_USER) {
                count ++;
                if (lastLongestWin == null) {
                    lastLongestWin = new LongestWin();
                    lastLongestWin.startRecord = list.get(i);
                }
            }
            else {
                if (winnerFlag != lastFlag && count > 0) {
                    if (count > max) {
                        max = count;
                        winList.clear();
                        lastLongestWin.endRecord = list.get(i - 1);
                        lastLongestWin.count = count;
                        winList.add(lastLongestWin);
                    }
                    else if (count == max) {
                        lastLongestWin.endRecord = list.get(i - 1);
                        lastLongestWin.count = count;
                        winList.add(lastLongestWin);
                    }
                    count = 0;
                    lastLongestWin = null;
                }
            }
            lastFlag = winnerFlag;
        }
        if (!ListUtil.isEmpty(winList)) {
            for (LongestWin win:winList) {
                String year = win.startRecord.getDateStr().split("-")[0];
                buffer.append(year).append(win.startRecord.getMatch().getName());
                buffer.append("-");
                year = win.endRecord.getDateStr().split("-")[0];
                buffer.append(year).append(win.endRecord.getMatch().getName());
                buffer.append(" ");
            }
        }
        return max + "\n" + buffer.toString();
    }
}
