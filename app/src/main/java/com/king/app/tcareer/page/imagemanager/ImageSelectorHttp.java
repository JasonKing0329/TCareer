package com.king.app.tcareer.page.imagemanager;

import android.view.View;

import com.king.app.tcareer.model.http.bean.ImageItemBean;
import com.king.app.tcareer.page.download.DownloadItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/3 0003 00:20
 */

public class ImageSelectorHttp extends ImageSelector {

    protected ImageSelectorAdapter mAdapter;

    @Override
    public ImageSelectorAdapter initAdapter() {
        mAdapter = new HttpSelectorAdapter(getContext(), imageUrlBean);
        return mAdapter;
    }

    @Override
    protected View customToolbar() {
        requestOkAction();
        requestCloseAction();
        return null;
    }

    @Override
    protected boolean onClickOk() {
        List<ImageItemBean> itemList = mAdapter.getSelectedKey();
        List<DownloadItem> list = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i ++) {
            ImageItemBean itemBean = itemList.get(i);
            DownloadItem item = new DownloadItem();
            item.setKey(itemBean.getUrl());
            item.setFlag(itemBean.getKey());
            item.setSize(itemBean.getSize());

            String url = itemBean.getUrl();
            if (url.contains("/")) {
                String[] array = url.split("/");
                url = array[array.length - 1];
            }
            item.setName(url);

            list.add(item);
        }
        onSelectorListener.onSelectDone(list);
        return true;
    }
}
