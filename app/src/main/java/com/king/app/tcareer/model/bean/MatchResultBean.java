package com.king.app.tcareer.model.bean;

/**
 * Created by Administrator on 2017/6/18 0018.
 */

public class MatchResultBean {
    private long matchNameId;
    private long matchId;
    private String match;
    private String date;
    private String result;

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getMatchNameId() {
        return matchNameId;
    }

    public void setMatchNameId(long matchNameId) {
        this.matchNameId = matchNameId;
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }
}
