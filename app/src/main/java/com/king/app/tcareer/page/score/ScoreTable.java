package com.king.app.tcareer.page.score;

import com.king.app.tcareer.conf.AppConstants;

/**
 * 描述: 积分对照表
 * <p/>作者：景阳
 * <p/>创建时间: 2017/2/21 9:25
 */
public class ScoreTable {

    /**
     * row refer Constants.RECORD_MATCH_LEVELS
     * col refer Constants.RECORD_MATCH_ROUNDS, the column 0 means winner, others' index + 1
     */
    private static int[][] tables = new int[][]{
            {2000, 1200, 720, 360, 180, 90, 45, 10, 0, 0}, // Grand Slam
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Master cup，计分方式不参照该表，仅占位
            {1000, 600, 360, 180, 90, 45, 10, 10, 0, 0}, // ATP1000
            {500, 300, 180, 90, 45, 20, 0, 0, 0, 0}, // ATP500
            {250, 150, 90, 45, 20, 5, 0, 0, 0, 0}, // ATP250
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Davis Cup，无积分，仅占位
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Olympics，无积分，仅占位
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // ATP cup，计分方式不参照该表，仅占位
    };

    public static int MASTER_CUP_GROUP = 200;
    public static int MASTER_CUP_SF = 400;
    public static int MASTER_CUP_F = 500;

    /**
     * ATP杯积分规则
     * 每获得一场比赛胜利的积分（根据对手排名不同）
     */
    private static int[][] tables_atp_cup = new int[][]{
            // 1-10位， 11-25位， 25-50位，50-100位，100名以外
            {250, 200, 150, 75, 50},// 决赛
            {180, 140, 105, 50, 35},// 半决赛
            {120, 100, 75, 35, 25},// 八强
            {75, 65, 50, 25, 20}// 小组赛
    };

    public static int getScore(String round, String level, boolean isWinner) {
        int indexRound = 0;
        if (AppConstants.RECORD_MATCH_ROUNDS[0].equals(round) && isWinner) {
            indexRound = 0;
        } else {
            for (int i = 0; i < AppConstants.RECORD_MATCH_ROUNDS.length; i++) {
                if (AppConstants.RECORD_MATCH_ROUNDS[i].equals(round)) {
                    indexRound = i + 1;
                    break;
                }
            }
        }
        int indexLevel = 0;
        for (int i = 0; i < AppConstants.RECORD_MATCH_LEVELS.length; i++) {
            if (AppConstants.RECORD_MATCH_LEVELS[i].equals(level)) {
                indexLevel = i;
                break;
            }
        }

        return tables[indexLevel][indexRound];
    }

    public static int getMasterCupScore(String round) {
        if (AppConstants.RECORD_MATCH_ROUNDS[0].equals(round)) {// Final, 胜500
            return 500;
        }
        if (AppConstants.RECORD_MATCH_ROUNDS[1].equals(round)) {// SF, 胜400
            return 400;
        }
        else {// group, 胜200
            return 200;
        }
    }

    public static int getAtpCupScore(String round, int rankCpt) {
        int row = locateRoundToRow(round);
        int col = locateCptToCol(rankCpt);
        return tables_atp_cup[row][col];
    }

    private static int locateCptToCol(int rankCpt) {
        if (rankCpt > 0 && rankCpt <= 10) {
            return 0;
        }
        else if (rankCpt > 10 && rankCpt <= 25) {
            return 1;
        }
        else if (rankCpt > 25 && rankCpt <= 50) {
            return 2;
        }
        else if (rankCpt > 50 && rankCpt <= 100) {
            return 3;
        }
        else {
            return 4;
        }
    }

    private static int locateRoundToRow(String round) {
        if (AppConstants.RECORD_MATCH_ROUNDS[0].equals(round)) {
            return 0;
        }
        else if (AppConstants.RECORD_MATCH_ROUNDS[1].equals(round)) {
            return 1;
        }
        else if (AppConstants.RECORD_MATCH_ROUNDS[2].equals(round)) {
            return 2;
        }
        else {
            return 3;
        }
    }
}