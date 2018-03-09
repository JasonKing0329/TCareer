package com.king.app.tcareer.model.bean;

import com.king.app.tcareer.model.db.entity.RankWeek;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 13:57
 */
public class RankRangeBean {

    private int weeks;

    private int sequences;

    private RankWeek rankStart;

    private RankWeek rankEnd;

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public int getSequences() {
        return sequences;
    }

    public void setSequences(int sequences) {
        this.sequences = sequences;
    }

    public RankWeek getRankStart() {
        return rankStart;
    }

    public void setRankStart(RankWeek rankStart) {
        this.rankStart = rankStart;
    }

    public RankWeek getRankEnd() {
        return rankEnd;
    }

    public void setRankEnd(RankWeek rankEnd) {
        this.rankEnd = rankEnd;
    }
}
