package com.king.app.tcareer.page.player.page;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.king.app.tcareer.base.BaseView;

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

    void showCompetitor(String nameEng, String detailPlayerPath);

    void animTags(boolean isRight);
}
