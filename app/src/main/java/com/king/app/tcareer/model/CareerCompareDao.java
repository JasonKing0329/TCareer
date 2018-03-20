package com.king.app.tcareer.model;

import android.database.Cursor;

import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.KeyValueCountBean;
import com.king.app.tcareer.page.record.complex.CompareItem;
import com.king.app.tcareer.utils.FormatUtil;

import java.util.ArrayList;
import java.util.List;

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

}
