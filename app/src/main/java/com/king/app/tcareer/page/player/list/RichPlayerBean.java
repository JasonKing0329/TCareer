package com.king.app.tcareer.page.player.list;

import com.king.app.tcareer.model.bean.CompetitorBean;

/**
 * @desc
 * @auth 景阳
 * @time 2018/5/19 0019 15:45
 */

public class RichPlayerBean {
    private CompetitorBean competitorBean;

    private int win;

    private int lose;

    public CompetitorBean getCompetitorBean() {
        return competitorBean;
    }

    public void setCompetitorBean(CompetitorBean competitorBean) {
        this.competitorBean = competitorBean;
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
}
