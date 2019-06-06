package com.king.app.tcareer.page.player.h2hlist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityH2hListBinding;
import com.king.app.tcareer.model.bean.CompetitorBean;
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
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class H2hListActivity extends MvvmActivity<ActivityH2hListBinding, H2hViewModel> implements 
        OnBMClickListener, RichPlayerHolder {

    public static final String KEY_USER_ID = "key_user_id";

    private SortDialog sortDialog;

    private ChartManager chartManager;

    private RichPlayerFragment ftRich;

    @Override
    protected int getContentView() {
        return R.layout.activity_h2h_list;
    }

    @Override
    protected H2hViewModel createViewModel() {
        return ViewModelProviders.of(this).get(H2hViewModel.class);
    }

    @Override
    protected void initView() {
        mBinding.setModel(mModel);

        initToolbar();
        initBoomButton();

        ftRich = RichPlayerFragment.newInstance(getIntent().getLongExtra(KEY_USER_ID, -1), true);
        // 底部栏会遮挡，为了保留遮挡效果又不让最后一个内容被遮挡，设置最后一个item的bottom margin即刻
        ftRich.setBottomMargin(ScreenUtils.dp2px(36));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_ft, ftRich, "RichPlayerFragment")
                .commit();

        mBinding.tvCareer.setOnClickListener(v -> {
            mBinding.tvCareer.setSelected(true);
            mBinding.tvSeason.setSelected(false);
            showChart();
        });
        mBinding.tvWin.setOnClickListener(v -> {
            mBinding.tvWin.setSelected(true);
            mBinding.tvLose.setSelected(false);
            showChart();
        });
        mBinding.tvSeason.setOnClickListener(v -> {
            mBinding.tvCareer.setSelected(false);
            mBinding.tvSeason.setSelected(true);
            showChart();
        });
        mBinding.tvLose.setOnClickListener(v -> {
            mBinding.tvWin.setSelected(false);
            mBinding.tvLose.setSelected(true);
            showChart();
        });
        mBinding.tvCareer.setSelected(true);
        mBinding.tvWin.setSelected(true);

        mBinding.ivSide.setOnClickListener(v -> ftRich.toggleSidebar());
        mBinding.ivCollapseAll.setOnClickListener(v -> ftRich.setExpandAll(false));
        mBinding.ivExpandAll.setOnClickListener(v -> ftRich.setExpandAll(true));

        mBinding.ivSide.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        mBinding.ivCollapseAll.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        mBinding.ivExpandAll.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
    }

    private void initToolbar() {
        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setNavigationOnClickListener(view -> finish());

    }

    private void initBoomButton() {
        int radius = getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_radius);
        mBinding.bmbMenu.setButtonEnum(ButtonEnum.SimpleCircle);
        mBinding.bmbMenu.setButtonRadius(radius);
        mBinding.bmbMenu.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
        mBinding.bmbMenu.setButtonPlaceEnum(ButtonPlaceEnum.Vertical);
        mBinding.bmbMenu.setButtonPlaceAlignmentEnum(ButtonPlaceAlignmentEnum.BL);
        mBinding.bmbMenu.setButtonLeftMargin(getResources().getDimensionPixelSize(R.dimen.home_pop_menu_right));
        mBinding.bmbMenu.setButtonBottomMargin(getResources().getDimensionPixelSize(R.dimen.home_pop_menu_bottom));
        mBinding.bmbMenu.setButtonVerticalMargin(getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_margin_ver));
        mBinding.bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_sort_white_24dp)
                .buttonRadius(radius)
                .listener(this));
        mBinding.bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_filter_list_white_24dp)
                .buttonRadius(radius)
                .listener(this));
        mBinding.bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_refresh_white_24dp)
                .buttonRadius(radius)
                .listener(this));

    }

    @Override
    protected void initData() {
        chartManager = new ChartManager(this);

        mModel.pageDataObserver.observe(this, data -> showChart());
        mModel.loadPlayers(getIntent().getLongExtra(KEY_USER_ID, -1));
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
                mModel.loadPlayers(getIntent().getLongExtra(KEY_USER_ID, -1));
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

    private void showChart() {
        String[] contentValues = mModel.getChartContents();
        float[] values = mModel.getTargetValues(mBinding.tvCareer.isSelected(), mBinding.tvWin.isSelected());
        chartManager.showH2hChart(mBinding.piechart, contentValues, values);
    }

    @Override
    public void onSelectPlayer(CompetitorBean bean) {
        Intent intent = new Intent();
        intent.setClass(this, PlayerPageActivity.class);
        intent.putExtra(PlayerPageActivity.KEY_USER_ID, mModel.getUser().getId());
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
        mBinding.tvSort.setText(sort + "排序");

        int[] winLose = ftRich.getWinLoseOfList();
        StringBuffer buffer = new StringBuffer();
        buffer.append(winLose[0]).append("胜").append(winLose[1]).append("负");
        mBinding.tvWinLose.setText(buffer.toString());

        buffer = new StringBuffer("过滤条件：");
        if (ListUtil.isEmpty(ftRich.getFilterTexts())) {
            mBinding.tvFilter.setVisibility(View.GONE);
        }
        else {
            for (int i = 0; i < ftRich.getFilterTexts().size(); i ++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(ftRich.getFilterTexts().get(i));
            }
            mBinding.tvFilter.setText(buffer.toString());
            mBinding.tvFilter.setVisibility(View.VISIBLE);
        }

        mBinding.tvTotalPlayer.setText(String.valueOf(ftRich.getTotalPlayers()));
    }

}
