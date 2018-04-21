package com.king.app.tcareer.page.player.page;

/**
 * 描述: tab entity in PlayerPageActivity
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 16:18
 */
public abstract class TabBean {
    public int win;
    public int lose;
    /**
     * total = win + lose + W/O
     */
    public int total;
    public String id;

    public abstract String getTitle();
}
