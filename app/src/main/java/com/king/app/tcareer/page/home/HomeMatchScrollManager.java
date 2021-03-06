package com.king.app.tcareer.page.home;

import android.content.Context;

import com.king.app.tcareer.page.match.gallery.ScrollManager;
import com.king.app.tcareer.view.widget.GradientBkView;

/**
 * Created by Administrator on 2017/4/4 0004.
 * 主页执行的赛事切换行为变化
 */

public class HomeMatchScrollManager extends ScrollManager {

    private GradientBkView vMatchBk;

    public HomeMatchScrollManager(Context context) {
        super(context);
    }

    /**
     * 绑定待变化的view
     *
     * @param vMatchBk
     */
    public void bindBehaviorView(GradientBkView vMatchBk) {
        this.vMatchBk = vMatchBk;
    }

    @Override
    protected void updateBehaviors(int[] color) {
        // 渐变背景
        vMatchBk.updateGradientValues(color);
    }
}
