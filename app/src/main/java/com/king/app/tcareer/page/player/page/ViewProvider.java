package com.king.app.tcareer.page.player.page;

import android.content.res.Resources;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/4 10:50
 */
public interface ViewProvider {
    CollapsingToolbarLayout getCollapsingToolbar();

    View getGroupAtp();

    TextView getTvAtpTime();

    TabLayout getTabLayout();

    TextView getEngNameTextView();

    TextView getChnNameTextView();

    TextView getCountryTextView();

    TextView getBirthdayTextView();

    LinearLayout getGroupAtpCover();

    Resources getResources();

    android.support.v7.widget.Toolbar getToolbar();

    ViewPager getViewpager();
}
