package com.king.app.tcareer.model.image;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.utils.DebugLog;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/4 11:37
 */
public class ImageBindingAdapter {

    /**
     * 处于recyclerView中的菜品图片（提前下单、点餐页、购物车）
     * @param view
     * @param url
     */
    @BindingAdapter({"matchUrl"})
    public static void setMatchUrl(ImageView view, String url) {
        DebugLog.e(url);
        AppGlide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(view);
    }

}
