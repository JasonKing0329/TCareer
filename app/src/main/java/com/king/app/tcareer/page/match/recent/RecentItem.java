package com.king.app.tcareer.page.match.recent;

import com.king.app.tcareer.model.db.entity.Record;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/12 11:12
 */
public class RecentItem {

    private Record record;

    private String userImageUrl;

    private String playerImageUrl;

    private String playerName;

    private String rankSeed;

    private String score;

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getPlayerImageUrl() {
        return playerImageUrl;
    }

    public void setPlayerImageUrl(String playerImageUrl) {
        this.playerImageUrl = playerImageUrl;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getRankSeed() {
        return rankSeed;
    }

    public void setRankSeed(String rankSeed) {
        this.rankSeed = rankSeed;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
