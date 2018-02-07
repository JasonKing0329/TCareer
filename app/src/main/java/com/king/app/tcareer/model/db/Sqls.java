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
    
    public static String getAgainstTopCount(long userId, int winnerFlag, int year) {
        return "select sum(case when rank_cpt between 1 and 10 and winner_flag=" + winnerFlag + " then 1 else 0 end) as top10\n" +
                " ,sum(case when rank_cpt between 11 and 20 and winner_flag=" + winnerFlag + " then 1 else 0 end) as top20\n" +
                " ,sum(case when rank_cpt between 21 and 50 and winner_flag=" + winnerFlag + " then 1 else 0 end) as top50\n" +
                " ,sum(case when rank_cpt between 51 and 100 and winner_flag=" + winnerFlag + " then 1 else 0 end) as top100\n" +
                " ,sum(case when (rank_cpt > 100 or rank_cpt=0) and winner_flag=" + winnerFlag + " then 1 else 0 end) as outof100\n" +
                " FROM match_records where user_id=" + userId
                + (year == 0 ? "" : " and date_str LIKE '" + year + "%'");
    }
}
