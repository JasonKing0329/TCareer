package com.king.app.tcareer.model.bean;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/3 0003 16:08
 */

public class H2hBean {

    private long playerId;

    private int playerFlag;

    private int total;

    private int win;

    private int lose;

    private CompetitorBean competitor;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getPlayerFlag() {
        return playerFlag;
    }

    public void setPlayerFlag(int playerFlag) {
        this.playerFlag = playerFlag;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public CompetitorBean getCompetitor() {
        return competitor;
    }

    public void setCompetitor(CompetitorBean competitor) {
        this.competitor = competitor;
    }
}
