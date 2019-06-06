package com.king.app.tcareer.page.imagemanager;

import android.view.View;

import com.king.app.tcareer.conf.AppConfig;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.http.bean.ImageItemBean;
import com.king.app.tcareer.page.download.DownloadItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/3 0003 00:20
 */

public class ImageSelectorHttp extends ImageSelector {

    @Override
    protected void initItemBean(ItemBean item, ImageItemBean bean) {
        item.setBean(bean);
        item.setUrl(bean.getUrl());
        item.setCheck(false);

        item.setNew(true);
        File file = null;
        if (bean.getKey().equals(Command.TYPE_IMG_PLAYER)) {
            file = new File(AppConfig.IMG_PLAYER_BASE + imageUrlBean.getKey());
        }
        else if (bean.getKey().equals(Command.TYPE_IMG_MATCH)) {
            file = new File(AppConfig.IMG_MATCH_BASE + imageUrlBean.getKey());
        }
        else if (bean.getKey().equals(Command.TYPE_IMG_PLAYER_HEAD)) {
            file = new File(AppConfig.IMG_PLAYER_HEAD + imageUrlBean.getKey());
        }
        // 如果本地已存在，不显示new角标
        if (file != null && file.exists() && file.isDirectory()) {
            File files[] = file.listFiles();
            for (File f:files) {
                if (bean.getUrl().endsWith(f.getName())) {
                    item.setNew(false);
                    break;
                }
            }
        }
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
