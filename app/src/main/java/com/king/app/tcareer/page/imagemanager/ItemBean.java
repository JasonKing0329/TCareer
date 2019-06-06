package com.king.app.tcareer.page.imagemanager;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.app.tcareer.BR;
import com.king.app.tcareer.model.http.bean.ImageItemBean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/6 13:43
 */
public class ItemBean extends BaseObservable {

    private String url;

    private boolean check;

    private boolean isNew;

    private ImageItemBean bean;

    @Bindable
    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
        notifyPropertyChanged(BR.check);
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ImageItemBean getBean() {
        return bean;
    }

    public void setBean(ImageItemBean bean) {
        this.bean = bean;
    }
}
