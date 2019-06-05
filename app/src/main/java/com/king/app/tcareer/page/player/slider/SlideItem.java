package com.king.app.tcareer.page.player.slider;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/5 16:29
 */
public class SlideItem<T> {

    private T bean;

    private String imageUrl;

    public T getBean() {
        return bean;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
