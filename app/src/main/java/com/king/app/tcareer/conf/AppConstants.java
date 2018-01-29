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
}
