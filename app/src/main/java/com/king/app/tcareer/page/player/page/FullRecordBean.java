package com.king.app.tcareer.page.player.page;

import com.king.app.tcareer.model.db.entity.Record;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/4/27 10:43
 */
public class FullRecordBean {
    public Record record;
    boolean isYearFirst;
    public int year;
    public int yearWin;
    public int yearLose;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
