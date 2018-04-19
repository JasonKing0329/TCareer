package com.king.app.tcareer.page.player.page;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.palette.PaletteCallback;
import com.king.app.tcareer.model.palette.PaletteRequestListener;
import com.king.app.tcareer.model.palette.PaletteResponse;
import com.king.app.tcareer.model.palette.ViewColorBound;
import com.king.app.tcareer.utils.ScreenUtils;

import java.util.ArrayList;
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
    @BindView(R.id.iv_player_bg)
    ImageView ivPlayerBg;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_country)
    TextView tvCountry;
    @BindView(R.id.tv_name_chn)
    TextView tvNameChn;
    @BindView(R.id.tv_name_eng)
    TextView tvNameEng;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;

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
        // 不用公共的icon，这样会使其他界面引用该资源颜色也被下面的代码修改
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_filterrable);
//        toolbar.getNavigationIcon().setColorFilter(
//                getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));

        collapsingToolbar.post(new Runnable() {
            @Override
            public void run() {
                // getScrimVisibleHeightTrigger里面用到了getHeight，要在控件布局完成后才有数值
                int trigger = collapsingToolbar.getScrimVisibleHeightTrigger();
                int total = getResources().getDimensionPixelSize(R.dimen.player_page_head_height);
                appBarLayout.addOnOffsetChangedListener(new AppBarListener(total, trigger) {
                    @Override
                    protected void onCollapseStateChanged(boolean isCollapsing) {
                        presenter.handleCollapseScrimChanged(isCollapsing);
                    }
                });
            }
        });
    }

    public static abstract class AppBarListener implements AppBarLayout.OnOffsetChangedListener {

        private int collapseHeight;
        private int scrimTrigger;

        private boolean isCollapsing;

        public AppBarListener(int collapseHeight, int scrimTrigger) {
            this.collapseHeight = collapseHeight;
            this.scrimTrigger = scrimTrigger;
        }

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            int offset = collapseHeight + verticalOffset;
            boolean collapsing = (offset <= scrimTrigger);
            if (collapsing != isCollapsing) {
                isCollapsing = collapsing;
                onCollapseStateChanged(isCollapsing);
            }
        }

        protected abstract void onCollapseStateChanged(boolean isCollapsing);
    }

    private void initPlayerAndUser() {
        long playerId = getIntent().getLongExtra(KEY_COMPETITOR_ID, -1);
        boolean playerIsUser = getIntent().getBooleanExtra(KEY_COMPETITOR_IS_USER, false);
        long userId = getIntent().getLongExtra(KEY_USER_ID, -1);
        presenter.loadPlayerAndUser(playerId, userId, playerIsUser);
    }

    @Override
    public void showCompetitor(String nameEng, String bgPath) {
        collapsingToolbar.setTitle(nameEng);

        Glide.with(this)
                .asBitmap()
                .load(bgPath)
                .apply(GlideOptions.getEditorPlayerOptions())
                .listener(new PaletteRequestListener(0, new PaletteCallback() {
                    @Override
                    public List<ViewColorBound> getTargetViews() {
                        List<ViewColorBound> list = new ArrayList<>();
                        ViewColorBound bound = new ViewColorBound();
                        bound.view = toolbar;
                        bound.rect = toolbar.getNavigationIcon().getBounds();
                        list.add(bound);
                        return list;
                    }

                    @Override
                    public void noPaletteResponseLoaded(int position) {

                    }

                    @Override
                    public void onPaletteResponse(int position, PaletteResponse response) {
                        presenter.handlePalette(response);
                    }
                }))
                .into(ivPlayerBg);
        initFragments();
    }

    @Override
    public void showError(String s) {
        showConfirmMessage(s, null);
    }

    private void initFragments() {
        presenter.loadRecords();
    }

    public CollapsingToolbarLayout getCollapsingToolbar() {
        return collapsingToolbar;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public TextView getChnNameTextView() {
        return tvNameChn;
    }

    @Override
    public TextView getEngNameTextView() {
        return tvNameEng;
    }

    @Override
    public TextView getCountryTextView() {
        return tvCountry;
    }

    @Override
    public TextView getBirthdayTextView() {
        return tvBirthday;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public TabLayout getTabLayout() {
        return tabLayout;
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

    @Override
    public void animTags(boolean isRight) {
        int time = 0;
        appearTag(tvNameEng, isRight, time);
        time += 100;
        if (!TextUtils.isEmpty(tvNameChn.getText().toString())) {
            appearTag(tvNameChn, isRight, time);
            time += 100;
        }
        appearTag(tvCountry, isRight, time);
        time += 100;
        appearTag(tvBirthday, isRight, time);
    }

    /**
     * 根据目标位置，从屏幕左侧或右侧alpha+translation渐入
     * @param view
     * @param isRight
     * @param delay
     */
    private void appearTag(final View view, boolean isRight, int delay) {
        final AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(500);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        set.addAnimation(alpha);
        TranslateAnimation translate;
        if (isRight) {
            int locations[] = new int[2];
            view.getLocationOnScreen(locations);
            translate = new TranslateAnimation(-ScreenUtils.getScreenWidth(), 0, 0, 0);
        }
        else {
            translate = new TranslateAnimation(ScreenUtils.getScreenWidth(), 0, 0, 0);
        }
        set.addAnimation(translate);

        if (delay > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.VISIBLE);
                    view.startAnimation(set);
                }
            }, delay);
        }
        else {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(set);
        }
    }
}
