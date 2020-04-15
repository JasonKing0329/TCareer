package com.king.app.tcareer.page.score;

import com.king.app.tcareer.model.db.entity.FrozenScore;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/4/14 17:12
 */
public class FrozenItem {

    private FrozenScore bean;

    private String match;

    public FrozenScore getBean() {
        return bean;
    }

    public void setBean(FrozenScore bean) {
        this.bean = bean;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }
}
