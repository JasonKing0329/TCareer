package com.king.app.tcareer.page.imagemanager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.http.bean.ImageItemBean;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;

/**
 * Created by Administrator on 2016/10/11.
 */
public class LocalSelectorAdapter extends ImageSelectorAdapter {

    private RequestOptions imageOptions;

    public LocalSelectorAdapter(Context context, ImageUrlBean imageUrlBean) {
        super(context, imageUrlBean);
        imageOptions = GlideOptions.getCommonOptions();
    }

    @Override
    public void onBindItemImage(ImageView imageView, ImageItemBean bean) {
        Glide.with(imageView.getContext())
                .load(bean.getUrl())
                .apply(imageOptions)
                .into(imageView);
    }

    @Override
    public void onBindItemMark(ImageView markNew, ImageItemBean bean) {
        markNew.setVisibility(View.GONE);
    }
}
