package com.king.app.tcareer.view.widget.scoreboard;

import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.Score;

import java.util.List;

public class ScoreBoardParam {

    private String matchName;

    private String round;

    private String player1;

    private String player2;

    private String playerUrl1;

    private String playerUrl2;

    private int winnerIndex;

    private Record record;

    private List<Score> scoreList;

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getPlayerUrl1() {
        return playerUrl1;
    }

    public void setPlayerUrl1(String playerUrl1) {
        this.playerUrl1 = playerUrl1;
    }

    public String getPlayerUrl2() {
        return playerUrl2;
    }

    public void setPlayerUrl2(String playerUrl2) {
        this.playerUrl2 = playerUrl2;
    }

    public List<Score> getScoreList() {
        return scoreList;
    }

    public void setScoreList(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    public int getWinnerIndex() {
        return winnerIndex;
    }

    public void setWinnerIndex(int winnerIndex) {
        this.winnerIndex = winnerIndex;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
