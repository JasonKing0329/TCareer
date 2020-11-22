package com.king.app.tcareer.model.db;

import android.text.TextUtils;

import com.king.app.tcareer.conf.AppConstants;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/27 0027 00:36
 */

public class Sqls {

    /**
     * 查询第五盘胜负情况
     * user_id total win lose
     *
     */
    public static String getDecidingSetData5() {
        return "select match_records.user_id, count(user_id) as total \n" +
                ", sum(case when user_point>competitor_point then 1 else 0 end) as win \n" +
                ", sum(case when user_point<competitor_point then 1 else 0 end) as lose \n" +
                "from scores, match_records \n" +
                "on scores.record_id=match_records._id where set_no=5 \n" +
                "group by user_id";
    }

    /**
     * 抢七胜负情况
     * user_id total win lose
     *
     * @return
     */
    public static String getTiebreakData() {
        return "select match_records.user_id, count(user_id) as total \n" +
                ", sum(case when user_point>competitor_point then 1 else 0 end) as win \n" +
                ", sum(case when user_point<competitor_point then 1 else 0 end) as lose \n" +
                "from scores, match_records \n" +
                "on scores.record_id=match_records._id where is_tiebreak = 1 \n" +
                "group by user_id";
    }

    /**
     * 送蛋次数
     * user_id total
     *
     * @return
     */
    public static String getLoveSet() {
        return "select match_records.user_id, count(user_id) as total \n" +
                "from scores, match_records \n" +
                "on scores.record_id=match_records._id where competitor_point = 0 and user_point = 6 \n" +
                "group by user_id";
    }

    /**
     * 吞蛋次数
     * user_id total
     *
     * @return
     */
    public static String getBeLovedSet() {
        return "select match_records.user_id, count(user_id) as total \n" +
                "from scores, match_records \n" +
                "on scores.record_id=match_records._id where user_point = 0 and competitor_point = 6 \n" +
                "group by user_id";
    }

    /**
     * 查询user的h2h，按音序排列
     * player_id player_flag total win lose
     * @param userId
     * @param desc
     * @return
     */
    public static String getH2hNoOrderBy(long userId, boolean desc) {
        return getH2h(userId, null, desc);
    }

    /**
     * 查询user的h2h，按总交手次数排列
     * player_id player_flag total win lose
     * @param userId
     * @param desc
     * @return
     */
    public static String getH2hOrderByTotal(long userId, boolean desc) {
        return getH2h(userId, "total ", desc);
    }

    /**
     * 查询user的h2h，按胜场排列
     * player_id player_flag total win lose
     * @param userId
     * @param desc
     * @return
     */
    public static String getH2hOrderByWin(long userId, boolean desc) {
        return getH2h(userId, "win ", desc);
    }

    /**
     * 查询user的h2h，按负场排列
     * player_id player_flag total win lose
     * @param userId
     * @param desc
     * @return
     */
    public static String getH2hOrderByLose(long userId, boolean desc) {
        return getH2h(userId, "lose ", desc);
    }

    /**
     * 查询user的h2h，按净胜场排列
     * player_id player_flag total win lose
     * @param userId
     * @param desc
     * @return
     */
    public static String getH2hOrderByOdds(long userId, boolean desc) {
        return getH2h(userId, "win - lose ", desc);
    }

    /**
     * 查询user的h2h，添加record的顺序
     * player_id player_flag total win lose
     * @param userId
     * @param desc
     * @return
     */
    public static String getH2hOrderByInsert(long userId, boolean desc) {
        return getH2h(userId, "_id ", desc);
    }

    /**
     * user对应的全部h2h
     * player_id player_flag total win lose
     *
     * @return
     */
    public static String getH2h(long userId, String orderBy, boolean desc) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("select player_id, player_flag, count(*) as total\n")
                .append(", sum(case when winner_flag=0 and retire_flag!=2 then 1 else 0 end) as win \n")
                .append(", sum(case when winner_flag=1 and retire_flag!=2 then 1 else 0 end) as lose \n")
                .append(" from match_records\n")
                .append(" where user_id=").append(userId)
                .append(" group by player_id,player_flag \n");
        if (!TextUtils.isEmpty(orderBy)) {
            buffer.append(" order by ").append(orderBy);
            if (desc) {
                buffer.append(" DESC");
            }
        }
        return buffer.toString();
    }

    public static String getH2h(long userId, long playerId, boolean competitorIsUser) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("select player_id, player_flag, count(*) as total\n")
                .append(", sum(case when winner_flag=0 and retire_flag!=2 then 1 else 0 end) as win \n")
                .append(", sum(case when winner_flag=1 and retire_flag!=2 then 1 else 0 end) as lose \n")
                .append(" from match_records\n")
                .append(" where user_id=").append(userId)
                .append(" and player_id=").append(playerId)
                .append(" and player_flag=")
                .append(competitorIsUser ? AppConstants.COMPETITOR_VIRTUAL:AppConstants.COMPETITOR_NORMAL)
                .append(" group by player_id,player_flag \n");
        return buffer.toString();
    }

    /**
     * 统计对阵top N 的胜负量
     * @param userId
     * @param winnerFlag
     * @param year
     * @return
     */
    public static String getAgainstTopCount(long userId, int winnerFlag, int year) {
        return "select sum(case when rank_cpt between 1 and 10 and winner_flag=" + winnerFlag + " then 1 else 0 end) as top10\n" +
                " ,sum(case when rank_cpt between 11 and 20 and winner_flag=" + winnerFlag + " then 1 else 0 end) as top20\n" +
                " ,sum(case when rank_cpt between 21 and 50 and winner_flag=" + winnerFlag + " then 1 else 0 end) as top50\n" +
                " ,sum(case when rank_cpt between 51 and 100 and winner_flag=" + winnerFlag + " then 1 else 0 end) as top100\n" +
                " ,sum(case when (rank_cpt > 100 or rank_cpt=0) and winner_flag=" + winnerFlag + " then 1 else 0 end) as outof100\n" +
                " FROM match_records where user_id=" + userId
                + (year == 0 ? "" : " and date_str LIKE '" + year + "%'");
    }

    /**
     * 统计各个级别赛事的决赛/冠军/亚军数量
     * key value
     * @param userId
     * @param winnerFlag null则决赛，否则冠军或亚军
     * @param year
     * @return
     */
    public static String getCountFinalByLevel(long userId, String winnerFlag, String year) {
        return countFinal(userId, winnerFlag, year, "matches.level");
    }

    /**
     * 统计各个场地赛事的决赛/冠军/亚军数量
     * key value
     * @param userId
     * @param winnerFlag null则决赛，否则冠军或亚军
     * @param year
     * @return
     */
    public static String getCountFinalByCourt(long userId, String winnerFlag, String year) {
        return countFinal(userId, winnerFlag, year, "matches.court");
    }

    /**
     * 统计决赛/冠军/亚军数量
     * @param userId
     * @param winnerFlag null则决赛，否则冠军或亚军
     * @param year
     * @param groupBy
     * @return
     */
    private static String countFinal(long userId, String winnerFlag, String year, String groupBy) {
        StringBuffer where = new StringBuffer();
        where.append(" WHERE match_records.user_id=").append(userId)
                .append(" AND match_records.round='Final'");
        if (!TextUtils.isEmpty(winnerFlag)) {
            where.append(" AND match_records.winner_flag=").append(winnerFlag);
        }
        if (!TextUtils.isEmpty(year)) {
            where.append(" AND match_records.DATE_STR like '").append(year).append("%'");
        }
        return "SELECT " + groupBy + " AS key,COUNT(*) AS value \n" +
                " FROM match_records \n" +
                " JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                " JOIN matches ON match_names.match_id=matches._id\n" +
                where.toString() +
                " GROUP BY " + groupBy;
    }

    public static String getGroupGSWinLose(long userId, String year) {
        return groupWinLose(userId, year, "Grand Slam");
    }

    /**
     * 统计level赛事类型对应的胜负数量
     * _id, name, win, lose
     * @param userId
     * @param year
     * @return
     */
    public static String groupWinLose(long userId, String year, String level) {
        return "SELECT matches._id, match_names.name, sum(CASE WHEN winner_flag=0 THEN 1 ELSE 0 END) AS win , sum(CASE WHEN winner_flag=0 THEN 0 ELSE 1 END) AS lose \n" +
                " FROM match_records\n" +
                " JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                " JOIN matches ON match_names.match_id=matches._id\n" +
                " WHERE match_records.user_id=" + userId + " AND matches.level='" + level + "' \n" +
                (year == null ? "":" AND date_str LIKE '" + year + "%'") +
                " GROUP BY matches._id";
    }

    /**
     * GS胜负数量
     * level, win, lose
     * @param userId
     * @param year
     * @return
     */
    public static String getGSWinLose(long userId, String year) {
        return countWinLose(userId, year, "matches.level", "Grand Slam");
    }

    /**
     * Atp1000胜负数量
     * level, win, lose
     * @param userId
     * @param year
     * @return
     */
    public static String getAtp1000WinLose(long userId, String year) {
        return countWinLose(userId, year, "matches.level", "ATP1000");
    }

    /**
     * 统计类型对应的胜负数量
     * key, win, lose
     * @param userId
     * @param year
     * @return
     */
    public static String countWinLose(long userId, String year, String key, String value) {
        return "SELECT " + key + ", sum(CASE WHEN winner_flag=0 THEN 1 ELSE 0 END) AS win , sum(CASE WHEN winner_flag=0 THEN 0 ELSE 1 END) AS lose \n" +
                " FROM match_records\n" +
                " JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                " JOIN matches ON match_names.match_id=matches._id\n" +
                " WHERE match_records.user_id=" + userId + " AND " + key + "='" + value + "' \n" +
                (year == null ? "":" AND date_str LIKE '" + year + "%'");
    }

    /**
     * 统计GS轮次
     * @param userId
     * @return
     */
    public static String getGsResults(long userId) {
        return getMatchResults(userId, "Grand Slam");
    }

    /**
     * 统计ATP1000轮次
     * @param userId
     * @return
     */
    public static String getATP1000Results(long userId) {
        return getMatchResults(userId, "ATP1000");
    }

    /**
     * 统计GS轮次
     * match_id, name, date_str, result
     * @param userId
     * @return
     */
    public static String getMatchResults(long userId, String level) {
        return "SELECT matches._id AS match_id, match_names._id AS match_name_id, match_names.name, match_records.date_str\n" +
                ",(CASE WHEN winner_flag=0 THEN (CASE WHEN round='Final' THEN 'Winner' END) ELSE round END) AS result \n" +
                " FROM match_records \n" +
                " JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                " JOIN matches ON match_names.match_id=matches._id\n" +
                " WHERE match_records.user_id=" + userId + " AND matches.level='" + level + "' \n" +
                " AND (match_records.winner_flag=1 OR round='Final')";
    }
}
