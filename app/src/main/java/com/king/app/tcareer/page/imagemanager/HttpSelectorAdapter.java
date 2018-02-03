package com.king.app.tcareer.page.imagemanager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.tcareer.conf.AppConfig;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.http.bean.ImageItemBean;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;
import com.king.app.tcareer.page.setting.SettingProperty;

import java.io.File;

/**
 * Created by Administrator on 2016/10/11.
 */
public class HttpSelectorAdapter extends ImageSelectorAdapter {

    private RequestOptions imageOptions;

    public HttpSelectorAdapter(Context context, ImageUrlBean imageUrlBean) {
        super(context, imageUrlBean);
        imageOptions = GlideOptions.getCommonOptions();
    }

    @Override
    public void onBindItemImage(ImageView imageView, ImageItemBean bean) {

        Glide.with(imageView.getContext())
                .load("http://" + SettingProperty.getServerBaseUrl() + bean.getUrl())
                .apply(imageOptions)
                .into(imageView);
    }

    @Override
    public void onBindItemMark(ImageView markNew, ImageItemBean bean) {

        markNew.setVisibility(View.VISIBLE);

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
                    markNew.setVisibility(View.GONE);
                    break;
                }
            }
        }
    }
}
