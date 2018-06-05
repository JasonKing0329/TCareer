package com.king.app.tcareer.page.player.h2hlist;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.glory.chart.ChartManager;
import com.king.app.tcareer.page.player.list.RichPlayerFilterDialog;
import com.king.app.tcareer.page.player.list.RichPlayerFragment;
import com.king.app.tcareer.page.player.list.RichPlayerHolder;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.ListUtil;
import com.king.app.tcareer.utils.ScreenUtils;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceAlignmentEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class H2hListActivity extends BaseMvpActivity<H2hPresenter> implements IH2hListView
        , OnBMClickListener, RichPlayerHolder {

    public static final String KEY_USER_ID = "key_user_id";

    @BindView(R.id.group_root)
    ViewGroup groupRoot;
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_total_player)
    TextView tvTotalPlayer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bmb_menu)
    BoomMenuButton bmbMenu;
    @BindView(R.id.piechart)
    PieChart pieChart;
    @BindView(R.id.tv_career)
    TextView tvCareer;
    @BindView(R.id.tv_win)
    TextView tvWin;
    @BindView(R.id.tv_season)
    TextView tvSeason;
    @BindView(R.id.tv_lose)
    TextView tvLose;
    @BindView(R.id.tv_win_lose)
    TextView tvWinLose;
    @BindView(R.id.tv_sort)
    TextView tvSort;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.ctl_toolbar)
    CollapsingToolbarLayout ctlToolbar;
    @BindView(R.id.iv_collapse_all)
    ImageView ivCollapseAll;
    @BindView(R.id.iv_expand_all)
    ImageView ivExpandAll;
    @BindView(R.id.iv_side)
    ImageView ivSide;

    private SortDialog sortDialog;

    private ChartManager chartManager;
    private H2hListPageData pageData;

    private RichPlayerFragment ftRich;

    @Override
    protected int getContentView() {
        return R.layout.activity_h2h_list;
    }

    @Override
    protected void initView() {
        initToolbar();
        initBoomButton();

        ftRich = RichPlayerFragment.newInstance(getIntent().getLongExtra(KEY_USER_ID, -1), true);
        // 底部栏会遮挡，为了保留遮挡效果又不让最后一个内容被遮挡，设置最后一个item的bottom margin即刻
        ftRich.setBottomMargin(ScreenUtils.dp2px(36));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_ft, ftRich, "RichPlayerFragment")
                .commit();

        ivSide.setOnClickListener(v -> ftRich.toggleSidebar());
        ivCollapseAll.setOnClickListener(v -> ftRich.setExpandAll(false));
        ivExpandAll.setOnClickListener(v -> ftRich.setExpandAll(true));

        ivSide.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        ivCollapseAll.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        ivExpandAll.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected H2hPresenter createPresenter() {
        return new H2hPresenter();
    }

    @Override
    protected void initData() {
        chartManager = new ChartManager(this);
        presenter.loadPlayers(getIntent().getLongExtra(KEY_USER_ID, -1));
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

    }

    private void initBoomButton() {
        int radius = getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_radius);
        bmbMenu.setButtonEnum(ButtonEnum.SimpleCircle);
        bmbMenu.setButtonRadius(radius);
        bmbMenu.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
        bmbMenu.setButtonPlaceEnum(ButtonPlaceEnum.Vertical);
        bmbMenu.setButtonPlaceAlignmentEnum(ButtonPlaceAlignmentEnum.BL);
        bmbMenu.setButtonLeftMargin(getResources().getDimensionPixelSize(R.dimen.home_pop_menu_right));
        bmbMenu.setButtonBottomMargin(getResources().getDimensionPixelSize(R.dimen.home_pop_menu_bottom));
        bmbMenu.setButtonVerticalMargin(getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_margin_ver));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_sort_white_24dp)
                .buttonRadius(radius)
                .listener(this));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_filter_list_white_24dp)
                .buttonRadius(radius)
                .listener(this));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_refresh_white_24dp)
                .buttonRadius(radius)
                .listener(this));

    }

    @Override
    public void postShowUser(final User user) {
        runOnUiThread(() -> {
            ctlToolbar.setTitle(user.getNameChn());
            String imagePath = ImageProvider.getDetailPlayerPath(user.getNameChn());
            Glide.with(this)
                    .asBitmap()
                    .load(imagePath)
                    .apply(GlideOptions.getEditorPlayerOptions())
                    .into(ivHead);
        });
    }

    @Override
    public void onDataLoaded(H2hListPageData data) {

        this.pageData = data;

        tvCareer.setSelected(true);
        tvWin.setSelected(true);
        showChart();
    }

    @Override
    public void onSortFinished(List<H2hBean> list) {

    }

    @Override
    public void onFilterFinished(List<H2hBean> list) {

    }

    @Override
    public void onBoomButtonClick(int index) {

        switch (index) {
            case 0:
                showSortDialog();
                break;
            case 1:
                showFilterDialog();
                break;
            case 2:
                presenter.loadPlayers(getIntent().getLongExtra(KEY_USER_ID, -1));
                ftRich.reload();
                break;
        }
    }

    private void showSortDialog() {
        sortDialog = new SortDialog();
        sortDialog.setOnSortListener((type, order) -> ftRich.sortPlayer(type));
        sortDialog.show(getSupportFragmentManager(), "SortDialog");
    }

    private void showFilterDialog() {
        RichPlayerFilterDialog dialog = new RichPlayerFilterDialog();
        dialog.setOnFilterListener(bean -> ftRich.filterPlayer(bean));
        dialog.show(getSupportFragmentManager(), "RichPlayerFilterDialog");
    }

    @OnClick({R.id.tv_career, R.id.tv_win, R.id.tv_season, R.id.tv_lose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_career:
                tvCareer.setSelected(true);
                tvSeason.setSelected(false);
                showChart();
                break;
            case R.id.tv_win:
                tvWin.setSelected(true);
                tvLose.setSelected(false);
                showChart();
                break;
            case R.id.tv_season:
                tvCareer.setSelected(false);
                tvSeason.setSelected(true);
                showChart();
                break;
            case R.id.tv_lose:
                tvWin.setSelected(false);
                tvLose.setSelected(true);
                showChart();
                break;
        }
    }

    private void showChart() {
        float[] values = new float[pageData.getChartContents().length];
        Integer[] targetValues;
        if (tvCareer.isSelected()) {
            if (tvWin.isSelected()) {
                targetValues = pageData.getCareerChartWinValues();
            } else {
                targetValues = pageData.getCareerChartLoseValues();
            }
        } else {
            if (tvWin.isSelected()) {
                targetValues = pageData.getSeasonChartWinValues();
            } else {
                targetValues = pageData.getSeasonChartLoseValues();
            }
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = targetValues[i];
        }
        chartManager.showH2hChart(pieChart, pageData.getChartContents(), values);
    }

    @Override
    public void onSelectPlayer(CompetitorBean bean) {
        Intent intent = new Intent();
        intent.setClass(this, PlayerPageActivity.class);
        intent.putExtra(PlayerPageActivity.KEY_USER_ID, presenter.getUser().getId());
        if (bean instanceof User) {
            intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_IS_USER, true);
        }
        intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, bean.getId());
        startActivity(intent);
    }

    @Override
    public void onSortFinished(int sortType) {
        updateSortText(sortType);
        // 此页面默认全部收起
        ftRich.setExpandAll(false);
    }

    @Override
    public void updateFirstIndex(String index) {

    }

    private void updateSortText(int sortType) {
        String sort;
        switch (sortType) {
            case SettingProperty.VALUE_SORT_PLAYER_NAME_ENG:
                sort = getString(R.string.menu_sort_name_eng);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_COUNTRY:
                sort = getString(R.string.menu_sort_country);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_AGE:
                sort = getString(R.string.menu_sort_age);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION:
                sort = getString(R.string.menu_sort_constellation);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_RECORD:
                sort = getString(R.string.menu_sort_record);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_HEIGHT:
                sort = getString(R.string.menu_sort_height);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_WEIGHT:
                sort = getString(R.string.menu_sort_weight);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_HIGH:
                sort = getString(R.string.menu_sort_career_high);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_TITLES:
                sort = getString(R.string.menu_sort_career_titles);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_WIN:
                sort = getString(R.string.menu_sort_career_win);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_TURNEDPRO:
                sort = getString(R.string.menu_sort_turned_pro);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_LAST_UPDATE:
                sort = getString(R.string.menu_sort_last_update);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_RECORD_WIN:
                sort = getString(R.string.menu_sort_win);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_RECORD_LOSE:
                sort = getString(R.string.menu_sort_lose);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_RECORD_ODDS_WIN:
                sort = getString(R.string.menu_sort_odd_win);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_RECORD_ODDS_LOSE:
                sort = getString(R.string.menu_sort_odd_lose);
                break;
            default:
                sort = getString(R.string.menu_sort_name);
                break;
        }
        tvSort.setText(sort + "排序");

        int[] winLose = ftRich.getWinLoseOfList();
        StringBuffer buffer = new StringBuffer();
        buffer.append(winLose[0]).append("胜").append(winLose[1]).append("负");
        tvWinLose.setText(buffer.toString());

        buffer = new StringBuffer("过滤条件：");
        if (ListUtil.isEmpty(ftRich.getFilterTexts())) {
            tvFilter.setVisibility(View.GONE);
        }
        else {
            for (int i = 0; i < ftRich.getFilterTexts().size(); i ++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(ftRich.getFilterTexts().get(i));
            }
            tvFilter.setText(buffer.toString());
            tvFilter.setVisibility(View.VISIBLE);
        }

        tvTotalPlayer.setText(String.valueOf(ftRich.getTotalPlayers()));
    }

}
