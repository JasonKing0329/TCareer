package com.king.app.tcareer.page.score;

import java.util.List;

/**
 * 描述: 计分周期内的积分数据
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/6 14:30
 */
public class ValidScores {

    /**
     * 总积分（有效积分）
     */
    private int validScore;

    /**
     * 起记分（top30有效）
     */
    private int startScore;

    /**
     * 冻结积分（赛事停摆造成的冻结分数，如2020-03月开始疫情影响）
     */
    private int frozenScore;

    /**
     * 周期内的全部赛事
     */
    private List<ScoreBean> allList;

    /**
     * 周期内的参与计分的赛事
     */
    private List<ScoreBean> validList;

    /**
     * 周期内没有参与计分，作为候补积分的赛事
     */
    private List<ScoreBean> replaceList;

    /**
     * 周期内没有积分的赛事（本系统里戴维斯杯不计分）
     */
    private List<ScoreBean> otherList;

    public int getValidScore() {
        return validScore;
    }

    public void setValidScore(int validScore) {
        this.validScore = validScore;
    }

    public int getStartScore() {
        return startScore;
    }

    public void setStartScore(int startScore) {
        this.startScore = startScore;
    }

    public List<ScoreBean> getAllList() {
        return allList;
    }

    public void setAllList(List<ScoreBean> allList) {
        this.allList = allList;
    }

    public List<ScoreBean> getValidList() {
        return validList;
    }

    public void setValidList(List<ScoreBean> validList) {
        this.validList = validList;
    }

    public List<ScoreBean> getReplaceList() {
        return replaceList;
    }

    public void setReplaceList(List<ScoreBean> replaceList) {
        this.replaceList = replaceList;
    }

    public List<ScoreBean> getOtherList() {
        return otherList;
    }

    public void setOtherList(List<ScoreBean> otherList) {
        this.otherList = otherList;
    }

    public int getFrozenScore() {
        return frozenScore;
    }

    public void setFrozenScore(int frozenScore) {
        this.frozenScore = frozenScore;
    }
}
