package com.king.app.tcareer.page.player.page;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
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
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.palette.PaletteCallback;
import com.king.app.tcareer.model.palette.PaletteRequestListener;
import com.king.app.tcareer.model.palette.PaletteResponse;
import com.king.app.tcareer.model.palette.ViewColorBound;
import com.king.app.tcareer.page.imagemanager.ImageManager;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述: collapse toolbar + viewpager style
 * player主页，相对于单个user，tab以court区分
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 14:11
 */
public class PlayerPageActivity extends BaseMvpActivity<PagePresenter> implements IPageView, IPageHolder {

    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_COMPETITOR_ID = "key_competitor_id";
    public static final String KEY_COMPETITOR_IS_USER = "key_competitor_is_user";

    public static final String KEY_SUB_PAGE_TYPE = "key_sub_type";
    public static final int SUB_BY_COURT = 1;
    public static final int SUB_BY_USER = 0;

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

    private boolean disableReloadFragments;

    @Override
    protected int getContentView() {
        return R.layout.activity_player_page;
    }

    @Override
    protected void initView() {

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

    private void initViews() {
        setSupportActionBar(toolbar);
        // 不用公共的icon，这样会使其他界面引用该资源颜色也被下面的代码修改
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_filterrable);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_white_24dp));
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

    @Override
    protected PagePresenter createPresenter() {
        return new PagePresenter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player_page, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String text = presenter.getTargetViewTypeString();
        menu.findItem(R.id.menu_view_type).setTitle(text);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_view_type:
                presenter.changeSubViewType();
                initPlayerAndUser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.iv_player_bg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_player_bg:
                showImageManager();
                break;
        }
    }

    private void showImageManager() {
        ImageManager imageManager = new ImageManager(this);
        imageManager.setOnActionListener(new ImageManager.OnActionListener() {
            @Override
            public void onRefresh(int position) {
                disableReloadFragments = true;
                initPlayerAndUser();
            }

            @Override
            public void onManageFinished() {
                disableReloadFragments = true;
                initPlayerAndUser();
            }

            @Override
            public void onDownloadFinished() {
                disableReloadFragments = true;
                initPlayerAndUser();
            }

            @Override
            public FragmentManager getFragmentManager() {
                return getSupportFragmentManager();
            }
        });
        imageManager.setDataProvider(presenter.getImageProvider());
        imageManager.showOptions(presenter.getCompetitor().getNameEng(), 0
                , Command.TYPE_IMG_PLAYER, presenter.getCompetitor().getNameChn());
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

    protected void initPlayerAndUser() {
        long playerId = getIntent().getLongExtra(KEY_COMPETITOR_ID, -1);
        boolean playerIsUser = getIntent().getBooleanExtra(KEY_COMPETITOR_IS_USER, false);
        long bindUserId = getIntent().getLongExtra(KEY_USER_ID, -1);
        int subType = getIntent().getIntExtra(KEY_SUB_PAGE_TYPE, SUB_BY_COURT);
        presenter.preparePage(bindUserId, playerId, playerIsUser, subType);
    }

    @Override
    public void showCompetitor(String nameEng, String bgPath) {
        collapsingToolbar.setTitle(nameEng);
        DebugLog.e(bgPath);
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

                        // 菜单图标用与返回键一样的颜色
//                        Rect rect = toolbar.getOverflowIcon().getBounds();
//                        int width = rect.right - rect.left;
//                        int left = ScreenUtils.getScreenWidth() - rect.right;
//                        rect.left = left;
//                        rect.right = left + width;
//                        bound = new ViewColorBound();
//                        bound.view = toolbar;
//                        bound.rect = rect;
//                        list.add(bound);
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

        ivPlayerBg.post(new Runnable() {
            @Override
            public void run() {
                startRevealView(500);
            }
        });

        if (!disableReloadFragments) {
            initFragments();
        }
    }

    @Override
    public void showError(String s) {
        showConfirmMessage(s, null);
    }

    private void initFragments() {
        presenter.loadTabs();
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
        tabLayout.removeAllTabs();
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        for (TabBean bean : list) {
            TabLayout.Tab shotsTab = tabLayout.newTab();
            TabCustomView shotsTabCustomView = new TabCustomView(this);
            shotsTab.setCustomView(shotsTabCustomView);
            shotsTabCustomView.setCount(bean.win + "-" + bean.lose);
            shotsTabCustomView.setContentCategory(bean.getTitle());
            tabLayout.addTab(shotsTab);

            // 非page user模式，取当前user
            if (bean.userId == -1) {
                bean.userId = presenter.getUser().getId();
            }
            PageFragment fragment = PageFragment.newInstance(bean.userId, bean.court);
            pageAdapter.addFragment(fragment);
        }
        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewpager));

        viewpager.setAdapter(pageAdapter);
    }

    @Override
    public CompetitorBean getCompetitor() {
        return presenter.getCompetitor();
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
     *
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
        } else {
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
        } else {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(set);
        }
    }

    private void startRevealView(int animTime) {
        // centerX和centerY实是相对于view的
        Animator anim = ViewAnimationUtils.createCircularReveal(ivPlayerBg, ivPlayerBg.getWidth() / 2
                , 0, 0, (float) ivPlayerBg.getHeight());
        anim.setDuration(animTime);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

}
