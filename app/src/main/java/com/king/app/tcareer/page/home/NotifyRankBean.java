package com.king.app.tcareer.page.home;

import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.User;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/8 17:44
 */
public class NotifyRankBean {

    private User user;

    private RankWeek lastRank;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RankWeek getLastRank() {
        return lastRank;
    }

    public void setLastRank(RankWeek lastRank) {
        this.lastRank = lastRank;
    }
}
