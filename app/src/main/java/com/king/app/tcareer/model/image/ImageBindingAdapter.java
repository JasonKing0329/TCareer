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
     * match image
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

    /**
     * player detail image
     * @param view
     * @param url
     */
    @BindingAdapter({"playerDetailUrl"})
    public static void setPlayerDetailUrl(ImageView view, String url) {
        DebugLog.e(url);
        AppGlide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.ic_def_player)
                .error(R.drawable.ic_def_player)
                .into(view);
    }

    /**
     * player detail image
     * @param view
     * @param url
     */
    @BindingAdapter({"playerHeadUrl"})
    public static void setPlayerHeadUrl(ImageView view, String url) {
        DebugLog.e(url);
        AppGlide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.ic_def_player_head)
                .error(R.drawable.ic_def_player_head)
                .into(view);
    }

}
