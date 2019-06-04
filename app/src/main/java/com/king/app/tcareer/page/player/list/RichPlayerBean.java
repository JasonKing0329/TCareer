package com.king.app.tcareer.page.player.list;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.app.tcareer.BR;
import com.king.app.tcareer.model.bean.CompetitorBean;

/**
 * @desc
 * @auth 景阳
 * @time 2018/5/19 0019 15:45
 */

public class RichPlayerBean extends BaseObservable {
    private CompetitorBean competitorBean;

    private int win;

    private int lose;

    private String imageUrl;

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

    @Bindable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        notifyPropertyChanged(BR.imageUrl);
    }
}
