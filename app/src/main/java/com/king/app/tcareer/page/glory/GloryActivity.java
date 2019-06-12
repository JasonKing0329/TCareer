package com.king.app.tcareer.page.glory;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.ActivityGloryBinding;
import com.king.app.tcareer.model.SeasonManager;
import com.king.app.tcareer.page.glory.bean.GloryTitle;
import com.king.app.tcareer.page.glory.chart.ChartManager;
import com.king.app.tcareer.page.glory.gs.GsFragment;
import com.king.app.tcareer.page.glory.gs.MasterFragment;
import com.king.app.tcareer.page.glory.target.TargetFragment;
import com.king.app.tcareer.page.glory.title.ChampionFragment;
import com.king.app.tcareer.page.glory.title.RunnerUpFragment;
import com.king.app.tcareer.page.setting.SettingProperty;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/5/25 16:42
 */
public class GloryActivity extends MvvmActivity<ActivityGloryBinding, GloryViewModel> implements Toolbar.OnMenuItemClickListener {

    public static final String KEY_USER_ID = "key_user_id";

    private final String[] titles = new String[]{
            "Champions", "Runner-ups", "Grand Slam", "ATP1000", "Target"
    };
    private final int PAGE_CHAMPION = 0;
    private final int PAGE_RUNNERUP = 1;
    private final int PAGE_GS = 2;
    private final int PAGE_ATP1000 = 3;
    private final int PAGE_TARGET = 4;

    private GloryPageAdapter pagerAdapter;

    private ChartManager chartManager;

    private boolean isLevelChart;

    @Override
    protected int getContentView() {
        return R.layout.activity_glory;
    }

    @Override
    protected GloryViewModel createViewModel() {
        return ViewModelProviders.of(this).get(GloryViewModel.class);
    }

    @Override
    protected void initView() {
        mBinding.setModel(mModel);
        // top head image
        updateSeasonStyle();

        mBinding.toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_white_24dp));
        mBinding.toolbar.setOnMenuItemClickListener(this);
        mBinding.toolbar.setNavigationOnClickListener(v -> finish());

        mBinding.includeHead.groupCareer.setOnClickListener(v -> {
            if (isLevelChart) {
                chartManager.showLevelChart(mBinding.includeHead.piechart, mModel.gloryTitleObserver.getValue()
                        , pagerAdapter.getItem(mBinding.viewpager.getCurrentItem()) instanceof ChampionFragment
                        , false);
            } else {
                chartManager.showCourtChart(mBinding.includeHead.piechart, mModel.gloryTitleObserver.getValue()
                        , pagerAdapter.getItem(mBinding.viewpager.getCurrentItem()) instanceof ChampionFragment
                        , false);
            }
            setCareerFocus(true);
        });
        mBinding.includeHead.groupSeason.setOnClickListener(v -> {
            if (isLevelChart) {
                chartManager.showLevelChart(mBinding.includeHead.piechart, mModel.gloryTitleObserver.getValue()
                        , pagerAdapter.getItem(mBinding.viewpager.getCurrentItem()) instanceof ChampionFragment
                        , true);
            } else {
                chartManager.showCourtChart(mBinding.includeHead.piechart, mModel.gloryTitleObserver.getValue()
                        , pagerAdapter.getItem(mBinding.viewpager.getCurrentItem()) instanceof ChampionFragment
                        , true);
            }
            setCareerFocus(false);
        });

        setCareerFocus(true);
    }

    @Override
    protected void initData() {
        chartManager = new ChartManager(this);
        mModel.gloryTitleObserver.observe(this, title -> {
            updatePubPage(SettingProperty.getGloryPageIndex(), title);
            initFragments();
        });
        mModel.loadData(getIntent().getLongExtra(KEY_USER_ID, -1));
    }

    private void initFragments() {
        pagerAdapter = new GloryPageAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new ChampionFragment(), titles[PAGE_CHAMPION]);
        pagerAdapter.addFragment(new RunnerUpFragment(), titles[PAGE_RUNNERUP]);
        pagerAdapter.addFragment(new GsFragment(), titles[PAGE_GS]);
        pagerAdapter.addFragment(new MasterFragment(), titles[PAGE_ATP1000]);
        pagerAdapter.addFragment(new TargetFragment(), titles[PAGE_TARGET]);
        mBinding.viewpager.setAdapter(pagerAdapter);

        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[PAGE_CHAMPION]));
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[PAGE_RUNNERUP]));
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[PAGE_GS]));
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[PAGE_ATP1000]));
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[PAGE_TARGET]));
        mBinding.tabLayout.setupWithViewPager(mBinding.viewpager);

        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SettingProperty.setGloryPageIndex(position);
                mModel.bindPageContent(position);
                updatePubPage(position, mModel.gloryTitleObserver.getValue());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        int page = SettingProperty.getGloryPageIndex();
        if (page < pagerAdapter.getCount()) {
            mBinding.viewpager.setCurrentItem(page);
        }
    }

    private void updatePubPage(int position, GloryTitle gloryTitle) {
        switch (position) {
            case PAGE_CHAMPION:
                onShowChampionPage(gloryTitle);
                break;
            case PAGE_RUNNERUP:
                onShowRunnerUpPage(gloryTitle);
                break;
            case PAGE_GS:
                onShowGsPage();
                break;
            case PAGE_ATP1000:
                onShowAtp1000Page();
                break;
            case PAGE_TARGET:
                onShowTargetPage();
                break;
        }
    }

    private void onShowTargetPage() {
        mBinding.toolbar.getMenu().clear();
        mBinding.toolbar.inflateMenu(R.menu.glory_none);
    }

    private void onShowAtp1000Page() {
        mBinding.toolbar.getMenu().clear();
        mBinding.toolbar.inflateMenu(R.menu.glory_none);
    }

    private void onShowGsPage() {
        mBinding.toolbar.getMenu().clear();
        mBinding.toolbar.inflateMenu(R.menu.glory_none);
    }

    private void onShowRunnerUpPage(GloryTitle gloryTitle) {
        mBinding.toolbar.getMenu().clear();
        mBinding.toolbar.inflateMenu(R.menu.glory_list);
        if (AppConstants.GROUP_BY_COURT == SettingProperty.getGloryRunnerupGroupMode()) {
            chartManager.showCourtChart(mBinding.includeHead.piechart, gloryTitle, false, false);
            isLevelChart = false;
        } else {
            chartManager.showLevelChart(mBinding.includeHead.piechart, gloryTitle, false, false);
            isLevelChart = true;
        }
        setCareerFocus(true);
    }

    private void onShowChampionPage(GloryTitle gloryTitle) {
        mBinding.toolbar.getMenu().clear();
        mBinding.toolbar.inflateMenu(R.menu.glory_list);
        if (AppConstants.GROUP_BY_COURT == SettingProperty.getGloryChampionGroupMode()) {
            chartManager.showCourtChart(mBinding.includeHead.piechart, gloryTitle, true, false);
            isLevelChart = false;
        } else {
            chartManager.showLevelChart(mBinding.includeHead.piechart, gloryTitle, true, false);
            isLevelChart = true;
        }
        setCareerFocus(true);
    }

    private void updateSeasonStyle() {
        SeasonManager.SeasonEnum type = SeasonManager.getSeasonType();
        if (type == SeasonManager.SeasonEnum.CLAY) {
            mBinding.includeHead.ivHead.setImageResource(R.drawable.nav_header_mon);
        } else if (type == SeasonManager.SeasonEnum.GRASS) {
            mBinding.includeHead.ivHead.setImageResource(R.drawable.nav_header_win);
        } else if (type == SeasonManager.SeasonEnum.INHARD) {
            mBinding.includeHead.ivHead.setImageResource(R.drawable.nav_header_sydney);
        } else {
            mBinding.includeHead.ivHead.setImageResource(R.drawable.nav_header_iw);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Fragment fragment = pagerAdapter.getItem(mBinding.viewpager.getCurrentItem());
        switch (item.getItemId()) {
            case R.id.menu_group_by_all:
                if (fragment instanceof ChampionFragment) {
                    ((ChampionFragment) fragment).groupBy(AppConstants.GROUP_BY_ALL);
                } else if (fragment instanceof RunnerUpFragment) {
                    ((RunnerUpFragment) fragment).groupBy(AppConstants.GROUP_BY_ALL);
                }
                break;
            case R.id.menu_group_by_court:
                if (fragment instanceof ChampionFragment) {
                    ((ChampionFragment) fragment).groupBy(AppConstants.GROUP_BY_COURT);
                    chartManager.showCourtChart(mBinding.includeHead.piechart, mModel.gloryTitleObserver.getValue(), true, false);
                } else if (fragment instanceof RunnerUpFragment) {
                    ((RunnerUpFragment) fragment).groupBy(AppConstants.GROUP_BY_COURT);
                    chartManager.showCourtChart(mBinding.includeHead.piechart, mModel.gloryTitleObserver.getValue(), false, false);
                }
                isLevelChart = false;
                break;
            case R.id.menu_group_by_level:
                if (fragment instanceof ChampionFragment) {
                    ((ChampionFragment) fragment).groupBy(AppConstants.GROUP_BY_LEVEL);
                    chartManager.showLevelChart(mBinding.includeHead.piechart, mModel.gloryTitleObserver.getValue(), true, false);
                } else if (fragment instanceof RunnerUpFragment) {
                    ((RunnerUpFragment) fragment).groupBy(AppConstants.GROUP_BY_LEVEL);
                    chartManager.showLevelChart(mBinding.includeHead.piechart, mModel.gloryTitleObserver.getValue(), false, false);
                }
                isLevelChart = true;
                break;
            case R.id.menu_group_by_year:
                if (fragment instanceof ChampionFragment) {
                    ((ChampionFragment) fragment).groupBy(AppConstants.GROUP_BY_YEAR);
                } else if (fragment instanceof RunnerUpFragment) {
                    ((RunnerUpFragment) fragment).groupBy(AppConstants.GROUP_BY_YEAR);
                }
                break;
        }
        return true;
    }

    private void setCareerFocus(boolean isFocus) {
        if (isFocus) {
            mBinding.includeHead.tvCareer.setTextColor(getResources().getColor(R.color.tab_actionbar_text_focus));
            mBinding.includeHead.tvCareerTotal.setTextColor(getResources().getColor(R.color.tab_actionbar_text_focus));
            mBinding.includeHead.tvSeason.setTextColor(getResources().getColor(R.color.white));
            mBinding.includeHead.tvSeasonTotal.setTextColor(getResources().getColor(R.color.white));
        } else {
            mBinding.includeHead.tvSeason.setTextColor(getResources().getColor(R.color.tab_actionbar_text_focus));
            mBinding.includeHead.tvSeasonTotal.setTextColor(getResources().getColor(R.color.tab_actionbar_text_focus));
            mBinding.includeHead.tvCareer.setTextColor(getResources().getColor(R.color.white));
            mBinding.includeHead.tvCareerTotal.setTextColor(getResources().getColor(R.color.white));
        }
    }
}
