package com.king.app.tcareer.conf;

import android.text.TextUtils;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 17:28
 */
public class AppConstants {

    public static final long USER_ID_KING = 1;
    public static final long USER_ID_FLAMENCO = 2;
    public static final long USER_ID_HENRY = 3;
    public static final long USER_ID_QI = 4;

    public static final String SCORE_RETIRE = "W/O";

    public static final String SCORE_RETIRE_NORMAL = "(对手退赛)";

    public static final String CHAMPOION = "冠军";

    public static final String RUNNERUP = "亚军";

    public static final String MATCH_CONST_MONTECARLO = "蒙特卡洛大师赛";

    /**
     * 0: no retire
     * 1: retire with score
     * 2: retire before match(W/0)
     */
    public static final int RETIRE_NONE = 0;
    public static final int RETIRE_WITH_SCORE = 1;
    public static final int RETIRE_WO = 2;

    /**
     * 0: user
     * 1: competitor
     */
    public static final int WINNER_USER = 0;
    public static final int WINNER_COMPETITOR = 1;

    /**
     * 0: challenge
     * 1: qualify
     */
    public static final int ACHIEVE_CHALLENGE = 0;
    public static final int ACHIEVE_QUALIFY = 1;

    /**
     * 0: normal player
     * 1: virtual player
     */
    public static final int COMPETITOR_NORMAL = 0;
    public static final int COMPETITOR_VIRTUAL = 1;

    public static final String[] RECORD_MATCH_LEVELS = new String[] {

            "Grand Slam",
            "Master Cup",
            "ATP1000",
            "ATP500",
            "ATP250",
            "Davi\'s Cup",
            "Olympic Games"
    };

    public static final String[] RECORD_MATCH_COURTS = new String[] {

            "硬地",
            "红土",
            "草地",
            "室内硬地",
    };

    public static final String[] RECORD_MATCH_ROUNDS = new String[] {

            "Final",
            "Semi Final",
            "1/4 Final",
            "Round 16",
            "Round 32",
            "Round 64",
            "Round 128",
            "Group",
            "Bronze medal"
    };

    public static final String[] RECORD_MATCH_ROUNDS_SHORT = new String[] {

            "F",
            "SF",
            "QF",
            "R16",
            "R32",
            "R64",
            "R128",
            "RR",
            "BR"
    };

    public static final String[] RECORD_GS_ROUNDS_GLORY = new String[] {

            "冠军",
            "亚军",
            "四强",
            "八强",
            "第四轮",
            "第三轮",
            "第二轮",
            "第一轮",
            "",
            ""
    };

    /**
     * RECORD_MATCH_ROUNDS对照的RECORD_GS_ROUNDS_GLORY
     * @param round
     * @return
     */
    public static final String getMasterGloryForRound(String round) {
        if (TextUtils.isEmpty(round)) {
            return "--";
        }
        if (round.equals("Winner")) {
            return "W";
        }

        String glory = "--";
        for (int i = 0; i < RECORD_MATCH_ROUNDS.length; i ++) {
            if (RECORD_MATCH_ROUNDS[i].equals(round)) {
                glory = RECORD_MATCH_ROUNDS_SHORT[i];
            }
        }
        return glory;
    }

    /**
     * RECORD_MATCH_ROUNDS对照的RECORD_GS_ROUNDS_GLORY
     * @param round
     * @return
     */
    public static final String getRoundShortName(String round) {
        if (TextUtils.isEmpty(round)) {
            return "--";
        }
        String glory = "--";
        for (int i = 0; i < RECORD_MATCH_ROUNDS.length; i ++) {
            if (RECORD_MATCH_ROUNDS[i].equals(round)) {
                glory = RECORD_MATCH_ROUNDS_SHORT[i];
            }
        }
        return glory;
    }

    /**
     * RECORD_MATCH_ROUNDS对照的RECORD_GS_ROUNDS_GLORY
     * @param round
     * @param isWinner
     * @return
     */
    public static final String getGsGloryForRound(String round, boolean isWinner) {
        if (TextUtils.isEmpty(round)) {
            return "--";
        }
        if (round.equals(RECORD_MATCH_ROUNDS[0]) && isWinner) {
            return RECORD_GS_ROUNDS_GLORY[0];
        }

        String glory = "--";
        for (int i = 0; i < RECORD_MATCH_ROUNDS.length; i ++) {
            if (RECORD_MATCH_ROUNDS[i].equals(round)) {
                glory = RECORD_GS_ROUNDS_GLORY[i + 1];
            }
        }
        return glory;
    }

    public static final String[] MONTH_ENG = new String[] {

            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    };

    public static final String[] MONTH_CHN = new String[] {

            "1月",
            "2月",
            "3月",
            "4月",
            "5月",
            "6月",
            "7月",
            "8月",
            "9月",
            "10月",
            "11月",
            "12月"
    };

    public static final int GROUP_BY_ALL = 0;
    public static final int GROUP_BY_LEVEL = 1;
    public static final int GROUP_BY_YEAR = 2;
    public static final int GROUP_BY_COURT = 3;

    /**
     * 统计整百场的记录
     */
    public static final int GLORY_TARGET_FACTOR = 100;

    public static final String[] MATCH_GS = new String[] {

            "澳大利亚网球公开赛",
            "法国网球公开赛",
            "温布尔顿网球公开赛",
            "美国网球公开赛"
    };

    public static final long[] ATP_1000_MATCH_ID = new long[] {
            //iw,miami,mc,madrid,roma,rc,cicinati,sh,paris
            15, //iw
            16, //miami
            18, //mc
            21, //madrid
            22, //roma
            34, //rc
            36, //cicinati
            44, //sh
            49, //paris

    };

    public static final String[] USER_COUNTRY = new String[] {
            "China",
            "France",
            "America"
    };

    public static final String FILTER_ALL = "All";
}
