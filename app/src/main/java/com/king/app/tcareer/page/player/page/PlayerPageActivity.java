package com.king.app.tcareer.page.player.page;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.view.widget.CircleImageView;

import java.util.List;

import butterknife.BindView;

/**
 * 描述: collapse toolbar + viewpager style
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 14:11
 */
public class PlayerPageActivity extends BaseMvpActivity<PagePresenter> implements IPageView, IPageHolder {

    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_COMPETITOR_ID = "key_competitor_id";
    public static final String KEY_COMPETITOR_IS_USER = "key_competitor_is_user";


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_player)
    CircleImageView ivPlayer;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.tv_country)
    TextView tvCountry;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    private PageAdapter pageAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_player_page;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected PagePresenter createPresenter() {
        return new PagePresenter();
    }

    @Override
    protected void initData() {
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        tabLayout.removeAllTabs();
        init();
    }

    private void init() {

        initViews();

        initPlayerAndUser();
    }

    @Override
    public User getUser() {
        return presenter.getUser();
    }

    private void initViews() {
        Drawable[] drawables = tvCountry.getCompoundDrawables();
        if (drawables[0] != null) {
            drawables[0].setColorFilter(getResources().getColor(R.color.icon_grey), PorterDuff.Mode.SRC_IN);
        }
        // 不用公共的icon，这样会使其他界面引用该资源颜色也被下面的代码修改
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_filterrable);
        toolbar.getNavigationIcon().setColorFilter(
                getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initPlayerAndUser() {
        long playerId = getIntent().getLongExtra(KEY_COMPETITOR_ID, -1);
        boolean playerIsUser = getIntent().getBooleanExtra(KEY_COMPETITOR_IS_USER, false);
        long userId = getIntent().getLongExtra(KEY_USER_ID, -1);
        presenter.loadPlayerAndUser(playerId, userId, playerIsUser);
    }

    @Override
    public void showPlayerInfo(String engName, String info, String imagePath, String country) {
        collapsingToolbar.setTitle(engName);
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.grey));
        tvInfo.setText(info);
        Glide.with(this)
                .load(imagePath)
                .apply(GlideOptions.getDefaultPlayerOptions())
                .into(ivPlayer);
        tvCountry.setText(country);

        initFragments();
    }

    @Override
    public void showError(String s) {
        showConfirmMessage(s, null);
    }

    private void initFragments() {
        presenter.loadRecords();
    }

    @Override
    public void onTabLoaded(List<TabBean> list) {
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        for (TabBean bean:list) {
            TabLayout.Tab shotsTab = tabLayout.newTab();
            TabCustomView shotsTabCustomView = new TabCustomView(this);
            shotsTab.setCustomView(shotsTabCustomView);
            shotsTabCustomView.setCount(bean.win + "-" + bean.lose);
            shotsTabCustomView.setContentCategory(bean.name);
            tabLayout.addTab(shotsTab);

            PageFragment fragment = PageFragment.newInstance(bean.name);
            pageAdapter.addFragment(fragment);
        }
        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewpager));

        viewpager.setAdapter(pageAdapter);
    }

    @Override
    public PagePresenter getPresenter() {
        return presenter;
    }
}
