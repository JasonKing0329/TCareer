package com.king.app.tcareer.page.player.h2hlist;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.glory.chart.ChartManager;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;
import com.king.app.tcareer.view.widget.SideBar;
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

public class H2hListActivity extends BaseMvpActivity<H2hPresenter> implements IH2hListView, OnItemMenuListener
        , OnBMClickListener, SideBar.OnTouchingLetterChangedListener {

    public static final String KEY_USER_ID = "key_user_id";

    @BindView(R.id.group_root)
    ViewGroup groupRoot;
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_total_player)
    TextView tvTotalPlayer;
    @BindView(R.id.rv_h2h_list)
    RecyclerView rvH2hList;
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
    @BindView(R.id.tv_conclude)
    TextView tvConclude;
    @BindView(R.id.ctl_toolbar)
    CollapsingToolbarLayout ctlToolbar;
    @BindView(R.id.sidebar)
    SideBar sideBar;
    @BindView(R.id.tv_index)
    TextView tvIndex;

    private H2hListAdapter h2hAdapter;

    private SortDialog sortDialog;
    private FilterDialog filterDialog;

    private ChartManager chartManager;
    private H2hListPageData pageData;

    @Override
    protected int getContentView() {
        return R.layout.activity_h2h_list;
    }

    @Override
    protected void initView() {
        initToolbar();
        initBoomButton();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvH2hList.setLayoutManager(manager);

        sideBar.setOnTouchingLetterChangedListener(this);
        sideBar.setTextView(tvIndex);
        // 底部栏控制sidebar显示
        tvConclude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter.getSortType() == SortDialog.SORT_TYPE_NAME) {
                    if (sideBar.getVisibility() == View.VISIBLE) {
                        sideBar.setVisibility(View.GONE);
                    }
                    else {
                        sideBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initBoomButton() {
        int radius = bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_radius);
        bmbMenu.setButtonEnum(ButtonEnum.SimpleCircle);
        bmbMenu.setButtonRadius(radius);
        bmbMenu.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
        bmbMenu.setButtonPlaceEnum(ButtonPlaceEnum.Vertical);
        bmbMenu.setButtonPlaceAlignmentEnum(ButtonPlaceAlignmentEnum.BR);
        bmbMenu.setButtonRightMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.home_pop_menu_right));
        bmbMenu.setButtonBottomMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.home_pop_menu_bottom));
        bmbMenu.setButtonVerticalMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_margin_ver));
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
        tvTotalPlayer.setText(String.valueOf(data.getHeaderList().size()));

        if (h2hAdapter == null) {
            h2hAdapter = new H2hListAdapter(this, data.getHeaderList(), this);
            rvH2hList.setAdapter(h2hAdapter);
        } else {
            h2hAdapter.updateData(data.getHeaderList());
            h2hAdapter.notifyDataSetChanged();
        }
        updateCurrentWinLose();

        tvCareer.setSelected(true);
        tvWin.setSelected(true);
        showChart();

        updateSideBar();
    }

    private void updateSideBar() {
        // 只有按name排序创建并显示side bar
        if (presenter.getSortType() == SortDialog.SORT_TYPE_NAME) {
            sideBar.setVisibility(View.VISIBLE);
            h2hAdapter.updateSideBar(sideBar);
        }
        else {
            sideBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTouchingLetterChanged(String letter) {
        rvH2hList.scrollToPosition(h2hAdapter.getIndexPosition(letter));
    }

    @Override
    public void onSortFinished(List<H2hBean> list) {
        h2hAdapter.updateData(list);
        h2hAdapter.notifyDataSetChanged();
        updateSideBar();
    }

    @Override
    public void onFilterFinished(List<H2hBean> list) {
        h2hAdapter.updateData(list);
        h2hAdapter.notifyDataSetChanged();
        updateCurrentWinLose();
        updateSideBar();
    }

    private void updateCurrentWinLose() {
        int[] winlose = h2hAdapter.getWinLose();
        tvConclude.setText("Win " + winlose[0] + "  Lose " + winlose[1]);
    }

    @Override
    public void onItemClicked(View v, H2hBean item) {

        Intent intent = new Intent();
        intent.setClass(this, PlayerPageActivity.class);
        intent.putExtra(PlayerPageActivity.KEY_USER_ID, presenter.getUser().getId());
        if (item.getCompetitor() instanceof User) {
            intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_IS_USER, true);
        }
        intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, item.getCompetitor().getId());
        startActivity(intent);
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
                break;
        }
    }

    private void showSortDialog() {
        sortDialog = new SortDialog();
        sortDialog.setOnSortListener(new SortDialog.OnSortListener() {
            @Override
            public void onSort(int type, int order) {
                presenter.sortDatas(type, order);
            }
        });
        sortDialog.show(getSupportFragmentManager(), "SortDialog");
    }

    private void showFilterDialog() {
        filterDialog = new FilterDialog();
        filterDialog.setOnFilterListener(new FilterDialog.OnFilterListener() {
            @Override
            public void onFilterNothing() {
                presenter.filterNothing();
            }

            @Override
            public void onFilterCountry(String country) {
                presenter.filterCountry(country);
            }

            @Override
            public void onFilterRank(int min, int max) {

            }

            @Override
            public void onFilterCount(int min, int max) {
                presenter.filterCount(min, max);
            }

            @Override
            public void onFilterWin(int min, int max) {
                presenter.filterWin(min, max);
            }

            @Override
            public void onFilterLose(int min, int max) {
                presenter.filterLose(min, max);
            }

            @Override
            public void onFilterDeltaWin(int min, int max) {
                presenter.filterOdds(min, max);
            }
        });
        filterDialog.show(getSupportFragmentManager(), "FilterDialog");
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
            }
            else {
                targetValues = pageData.getCareerChartLoseValues();
            }
        }
        else {
            if (tvWin.isSelected()) {
                targetValues = pageData.getSeasonChartWinValues();
            }
            else {
                targetValues = pageData.getSeasonChartLoseValues();
            }
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = targetValues[i];
        }
        chartManager.showH2hChart(pieChart, pageData.getChartContents(), values);
    }
}
