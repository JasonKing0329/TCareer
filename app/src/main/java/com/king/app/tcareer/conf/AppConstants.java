package com.king.app.tcareer.conf;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 17:28
 */
public class AppConstants {

    public static final String SCORE_RETIRE = "W/O";

    public static final String SCORE_RETIRE_NORMAL = "(对手退赛)";

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

}
