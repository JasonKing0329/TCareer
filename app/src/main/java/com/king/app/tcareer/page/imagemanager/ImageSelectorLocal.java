package com.king.app.tcareer.page.imagemanager;

import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.model.http.bean.ImageItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/3 0003 00:12
 */

public class ImageSelectorLocal extends ImageSelector {

    @Override
    protected void initItemBean(ItemBean item, ImageItemBean bean) {
        item.setBean(bean);
        item.setUrl(bean.getUrl());
        item.setNew(false);
        item.setCheck(false);
    }

    @Override
    protected View customToolbar() {
        requestOkAction(R.drawable.ic_delete_white_36dp);
        requestCloseAction();
        return null;
    }

    @Override
    protected boolean onClickOk() {
        List<ImageItemBean> itemList = mAdapter.getSelectedKey();

        List<String> selectedList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i ++) {
            String path = itemList.get(i).getUrl();
            selectedList.add(path);
        }
        onSelectorListener.onSelectDone(selectedList);
        return true;
    }
}
