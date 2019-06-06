package com.king.app.tcareer.page.player.page;

import android.animation.Animator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityPlayerPageBinding;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.palette.PaletteCallback;
import com.king.app.tcareer.model.palette.PaletteRequestListener;
import com.king.app.tcareer.model.palette.PaletteResponse;
import com.king.app.tcareer.model.palette.ViewColorBound;
import com.king.app.tcareer.page.imagemanager.ImageManager;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.dialog.AlertDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 描述: collapse mBinding.toolbar + mBinding.viewpager style
 * player主页，相对于单个user，tab以court区分
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 14:11
 */
public class PlayerPageActivity extends MvvmActivity<ActivityPlayerPageBinding, PageViewModel> {

    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_COMPETITOR_ID = "key_competitor_id";
    public static final String KEY_COMPETITOR_IS_USER = "key_competitor_is_user";

    private PageAdapter pageAdapter;

    private boolean disableReloadFragments;

    @Override
    protected int getContentView() {
        return R.layout.activity_player_page;
    }

    @Override
    protected PageViewModel createViewModel() {
        return ViewModelProviders.of(this).get(PageViewModel.class);
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
        mBinding.tabLayout.removeAllTabs();
        init();
    }

    private void init() {
        initViews();
        initObserver();
        initPlayerAndUser();
    }

    private void initViews() {
        setSupportActionBar(mBinding.toolbar);
        // 不用公共的icon，这样会使其他界面引用该资源颜色也被下面的代码修改
//        mBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_filterrable);
        mBinding.toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_white_24dp));
//        mBinding.toolbar.getNavigationIcon().setColorFilter(
//                getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
        mBinding.toolbar.setNavigationOnClickListener(v -> finish());
        mBinding.collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));
        mBinding.collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));

        mBinding.collapsingToolbar.post(() -> {
            // getScrimVisibleHeightTrigger里面用到了getHeight，要在控件布局完成后才有数值
            int trigger = mBinding.collapsingToolbar.getScrimVisibleHeightTrigger();
            int total = getResources().getDimensionPixelSize(R.dimen.player_page_head_height);
            mBinding.appbarLayout.addOnOffsetChangedListener(new AppBarListener(total, trigger) {
                @Override
                protected void onCollapseStateChanged(boolean isCollapsing) {
                    mModel.handleCollapseScrimChanged(isCollapsing);
                }
            });
        });

        mBinding.groupAtp.setVisibility(View.GONE);
        mBinding.groupAtpCover.setVisibility(View.GONE);

        mBinding.tvNameEng.setOnClickListener(tagListener);
        mBinding.tvNameChn.setOnClickListener(tagListener);
        mBinding.tvCountry.setOnClickListener(tagListener);
        mBinding.tvBirthday.setOnClickListener(tagListener);

        mBinding.ivPlayerBg.setOnClickListener(v -> showImageManager());
    }

    private View.OnClickListener tagListener = v -> {
        if (mModel.getCompetitor().getAtpBean() != null) {
            if (mBinding.groupAtp.getVisibility() == View.VISIBLE) {
                mModel.dismissAtpInfo();
            }
            else {
                mModel.playAtpInfo();
            }
        }
    };

    protected void initObserver() {
        mModel.setViewProvider(new PageViewProvider());
        mModel.playerImageUrl.observe(this, url -> showCompetitor(url));
        mModel.atpInfo.observe(this, bean -> showAtpInfo(bean));
        mModel.atpInfoUpdated.observe(this, bean -> {
            disableReloadFragments = true;
            initPlayerAndUser();
        });
        mModel.tabsObserver.observe(this, list -> onTabLoaded(list));
        mModel.animWithRightFace.observe(this, isRightFace -> animTags(isRightFace));
    }

    protected void initPlayerAndUser() {
        long playerId = getIntent().getLongExtra(KEY_COMPETITOR_ID, -1);
        boolean playerIsUser = getIntent().getBooleanExtra(KEY_COMPETITOR_IS_USER, false);
        long bindUserId = getIntent().getLongExtra(KEY_USER_ID, -1);
        mBinding.appbarLayout.setExpanded(true, true);

        // 先全部隐藏，等到调色板相关参数都加载完再动画渐入
        mBinding.tvNameEng.setVisibility(View.GONE);
        mBinding.tvNameChn.setVisibility(View.GONE);
        mBinding.tvBirthday.setVisibility(View.GONE);
        mBinding.tvCountry.setVisibility(View.GONE);

        mModel.preparePage(bindUserId, playerId, playerIsUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player_page, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String text = mModel.getTargetViewTypeString();
        menu.findItem(R.id.menu_view_type).setTitle(text);

        if (mModel.getUser() == null) {
            menu.findItem(R.id.menu_tab_type).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_view_type:
                mModel.changeSubViewType();
                initPlayerAndUser();
                break;
            case R.id.menu_tab_type:
                String[] array = getResources().getStringArray(R.array.player_tab_type);
                new AlertDialogFragment()
                        .setItems(array, (dialogInterface, i) -> {
                            boolean isTypeChanged = false;
                            switch (i) {
                                case 0:
                                    isTypeChanged = mModel.updateTabType(PageViewModel.TAB_USER);
                                    break;
                                case 1:
                                    isTypeChanged = mModel.updateTabType(PageViewModel.TAB_COURT);
                                    break;
                                case 2:
                                    isTypeChanged = mModel.updateTabType(PageViewModel.TAB_LEVEL);
                                    break;
                                case 3:
                                    isTypeChanged = mModel.updateTabType(PageViewModel.TAB_YEAR);
                                    break;
                            }
                            if (isTypeChanged) {
                                initPlayerAndUser();
                            }
                        })
                        .show(getSupportFragmentManager(), "AlertDialogFragment");
                break;
            case R.id.menu_update_atp:
                mModel.updateAtpData(mModel.getCompetitor().getAtpId());
                break;
        }

        return super.onOptionsItemSelected(item);
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
        imageManager.setDataProvider(mModel.getImageProvider());
        imageManager.showOptions(mModel.getCompetitor().getNameEng(), 0
                , Command.TYPE_IMG_PLAYER, mModel.getCompetitor().getNameChn());
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
        public void onOffsetChanged(AppBarLayout layout, int verticalOffset) {
            int offset = collapseHeight + verticalOffset;
            boolean collapsing = (offset <= scrimTrigger);
            if (collapsing != isCollapsing) {
                isCollapsing = collapsing;
                onCollapseStateChanged(isCollapsing);
            }
        }

        protected abstract void onCollapseStateChanged(boolean isCollapsing);
    }

    private void showCompetitor(String bgPath) {
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
                        bound.view = mBinding.toolbar;
                        bound.rect = mBinding.toolbar.getNavigationIcon().getBounds();
                        list.add(bound);

                        // 菜单图标用与返回键一样的颜色
//                        Rect rect = mBinding.toolbar.getOverflowIcon().getBounds();
//                        int width = rect.right - rect.left;
//                        int left = ScreenUtils.getScreenWidth() - rect.right;
//                        rect.left = left;
//                        rect.right = left + width;
//                        bound = new ViewColorBound();
//                        bound.view = mBinding.toolbar;
//                        bound.rect = rect;
//                        list.add(bound);
                        return list;
                    }

                    @Override
                    public void noPaletteResponseLoaded(int position) {
                        // 没有图片或加载失败
                        mModel.handlePalette(null);
                    }

                    @Override
                    public void onPaletteResponse(int position, PaletteResponse response) {
                        mModel.handlePalette(response);
                    }
                }))
                .into(mBinding.ivPlayerBg);

        mBinding.ivPlayerBg.post(() -> startRevealView(500));

        if (!disableReloadFragments) {
            initFragments();
        }

    }

    private void showAtpInfo(PlayerAtpBean atp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mBinding.tvAtpTime.setText("上次更新时间： " + sdf.format(new Date(atp.getLastUpdateDate())));
        mBinding.tvTurnedPro.setText("Turned Pro    " + atp.getTurnedPro());
        mBinding.tvCoach.setText(atp.getCoach());
        if (TextUtils.isEmpty(atp.getResidenceCity())) {
            mBinding.tvResidence.setText(atp.getResidenceCountry());
        }
        else {
            mBinding.tvResidence.setText(atp.getResidenceCity() + ", " + atp.getResidenceCountry());
        }
        mBinding.tvPlays.setText(atp.getPlays());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        calendar.setTime(new Date(atp.getLastUpdateDate()));
        int updateYear = calendar.get(Calendar.YEAR);
        if (year == updateYear) {
            mBinding.tvTitlesYear.setText(year + " (" + atp.getYearSingles() + "/" + atp.getYearDoubles() + ")");
            mBinding.tvWinLosePrizeYear.setText(year + "  " + atp.getYearWin() + "-" + atp.getYearLose() + "  " + atp.getYearPrize());
        }
        else {
            mBinding.tvTitlesYear.setText(year + " Unknown");
            mBinding.tvWinLosePrizeYear.setText(year + " Unknown");
        }
        mBinding.tvTitlesCareer.setText("Career (" + atp.getCareerSingles() + "/" + atp.getCareerDoubles() + ")");
        mBinding.tvWinLosePrizeCareer.setText("Career  " + atp.getCareerWin() + "-" + atp.getCareerLose() + "  " + atp.getCareerPrize());
    }

    private void initFragments() {
        mModel.loadTabs();
    }

    private class PageViewProvider implements ViewProvider {
        public CollapsingToolbarLayout getCollapsingToolbar() {
            return mBinding.collapsingToolbar;
        }

        public Toolbar getToolbar() {
            return mBinding.toolbar;
        }

        public TextView getChnNameTextView() {
            return mBinding.tvNameChn;
        }

        public TextView getEngNameTextView() {
            return mBinding.tvNameEng;
        }

        public TextView getCountryTextView() {
            return mBinding.tvCountry;
        }

        public TextView getBirthdayTextView() {
            return mBinding.tvBirthday;
        }

        public ViewGroup getGroupAtp() {
            return mBinding.groupAtp;
        }

        public TextView getTvAtpTime() {
            return mBinding.tvAtpTime;
        }

        public LinearLayout getGroupAtpCover() {
            return mBinding.groupAtpCover;
        }

        public Resources getResources() {
            return PlayerPageActivity.this.getResources();
        }

        public TabLayout getTabLayout() {
            return mBinding.tabLayout;
        }

        public ViewPager getViewpager() {
            return mBinding.viewpager;
        }
    }

    private void onTabLoaded(List<TabBean> list) {
        mBinding.tabLayout.removeAllTabs();
        if (list.size() > 5) {
            mBinding.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            mBinding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        for (TabBean bean : list) {
            TabLayout.Tab shotsTab = mBinding.tabLayout.newTab();
            TabCustomView shotsTabCustomView = new TabCustomView(this);
            shotsTab.setCustomView(shotsTabCustomView);
            shotsTabCustomView.setCount(bean.win + "-" + bean.lose);
            shotsTabCustomView.setContentCategory(bean.getTitle());
            mBinding.tabLayout.addTab(shotsTab);

            // 非page user模式，取当前user
            if (bean.userId == -1) {
                bean.userId = mModel.getUser().getId();
            }
            PageFragment fragment = PageFragment.newInstance(bean.userId, bean.court, bean.level, bean.year);
            pageAdapter.addFragment(fragment);
        }
        mBinding.viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mBinding.tabLayout));
        mBinding.tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mBinding.viewpager));

        mBinding.viewpager.setAdapter(pageAdapter);
    }

    private void animTags(boolean isRight) {
        int time = 0;
        appearTag(mBinding.tvNameEng, isRight, time);
        time += 100;
        if (!TextUtils.isEmpty(mBinding.tvNameChn.getText().toString())) {
            appearTag(mBinding.tvNameChn, isRight, time);
            time += 100;
        }
        appearTag(mBinding.tvCountry, isRight, time);
        time += 100;
        appearTag(mBinding.tvBirthday, isRight, time);
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
            new Handler().postDelayed(() -> {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(set);
            }, delay);
        } else {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(set);
        }
    }

    private void startRevealView(int animTime) {
        // centerX和centerY实是相对于view的
        Animator anim = ViewAnimationUtils.createCircularReveal(mBinding.ivPlayerBg, mBinding.ivPlayerBg.getWidth() / 2
                , 0, 0, (float) mBinding.ivPlayerBg.getHeight());
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
