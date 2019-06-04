package com.king.app.tcareer.page.match.common;

import com.king.app.tcareer.model.db.entity.User;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/4 15:25
 */
public class UserItem {
    private String name;
    private String years;
    private String h2h;
    private User user;
    private String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYears() {
        return years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    public String getH2h() {
        return h2h;
    }

    public void setH2h(String h2h) {
        this.h2h = h2h;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
