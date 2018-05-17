package com.king.app.tcareer.page.player.page;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;

import java.util.List;

/**
 * 描述: the view interface of PlayerPageActivity
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 15:49
 */
public interface IPageView extends BaseView {

    void showError(String s);

    void onTabLoaded(List<TabBean> list);

    Context getContext();

    TextView getChnNameTextView();

    TextView getEngNameTextView();

    TextView getCountryTextView();

    TextView getBirthdayTextView();

    CollapsingToolbarLayout getCollapsingToolbar();

    Toolbar getToolbar();

    TabLayout getTabLayout();

    void showCompetitor(String nameEng, String detailPlayerPath);

    void animTags(boolean isRight);

    void showAtpInfo(PlayerAtpBean atpBean);

    ViewGroup getGroupAtp();

    TextView getTvAtpTime();

    LinearLayout getGroupAtpCover();

    ViewPager getViewpager();

    void onUpdateAtpCompleted();
}
