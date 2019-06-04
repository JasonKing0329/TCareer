package com.king.app.tcareer.model.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.app.tcareer.BR;
import com.king.app.tcareer.model.db.entity.MatchNameBean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/4 16:07
 */
public class MatchImageBean extends BaseObservable {

    private String imageUrl;

    private MatchNameBean bean;

    @Bindable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        notifyPropertyChanged(BR.imageUrl);
    }

    public MatchNameBean getBean() {
        return bean;
    }

    public void setBean(MatchNameBean bean) {
        this.bean = bean;
    }
}
