package com.king.app.tcareer.model.bean;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/6 11:53
 */
public class AutoFillMatchBean {

    private long matchId;
    private String round;
    private int indexYear;
    private int indexMonth;

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public int getIndexYear() {
        return indexYear;
    }

    public void setIndexYear(int indexYear) {
        this.indexYear = indexYear;
    }

    public int getIndexMonth() {
        return indexMonth;
    }

    public void setIndexMonth(int indexMonth) {
        this.indexMonth = indexMonth;
    }
}
