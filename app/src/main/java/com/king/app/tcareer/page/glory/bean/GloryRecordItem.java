package com.king.app.tcareer.page.glory.bean;

import com.king.app.tcareer.model.db.entity.Record;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/11 16:58
 */
public class GloryRecordItem {

    private String index;

    private int indexVisibility;

    private String title;

    private int titleVisibility;

    private Record record;

    private String matchImageUrl;

    private String matchName;

    private String playerImageUrl;

    private String playerName;

    private String score;

    private int loseVisibility;

    private String place;

    private String level;

    private String date;

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public String getMatchImageUrl() {
        return matchImageUrl;
    }

    public void setMatchImageUrl(String matchImageUrl) {
        this.matchImageUrl = matchImageUrl;
    }

    public String getPlayerImageUrl() {
        return playerImageUrl;
    }

    public void setPlayerImageUrl(String playerImageUrl) {
        this.playerImageUrl = playerImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public int getIndexVisibility() {
        return indexVisibility;
    }

    public void setIndexVisibility(int indexVisibility) {
        this.indexVisibility = indexVisibility;
    }

    public int getLoseVisibility() {
        return loseVisibility;
    }

    public void setLoseVisibility(int loseVisibility) {
        this.loseVisibility = loseVisibility;
    }

    public int getTitleVisibility() {
        return titleVisibility;
    }

    public void setTitleVisibility(int titleVisibility) {
        this.titleVisibility = titleVisibility;
    }
}
