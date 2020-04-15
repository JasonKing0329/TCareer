package com.king.app.tcareer.page.score;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.MvvmFragment;
import com.king.app.tcareer.databinding.FragmentScoreBinding;
import com.king.app.tcareer.model.FlagProvider;
import com.king.app.tcareer.page.match.MatchDialog;
import com.king.app.tcareer.view.dialog.frame.FrameDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/2/21 14:14
 */
public class ScoreFragment extends MvvmFragment<FragmentScoreBinding, ScoreViewModel> {

    public static final int FLAG_52WEEK = 0;
    public static final int FLAG_YEAR = 1;

    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_MODE = "key_mode";

    private int pageMode;

    private IScoreHolder holder;

    private ScoreItemAdapter scoreItemAdapter;

    private ChartHelper chartHelper;

    public static ScoreFragment newInstance(long userId, int mode) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        bundle.putInt(KEY_MODE, mode);
        ScoreFragment fragment = new ScoreFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IScoreHolder) holder;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_score;
    }

    @Override
    protected ScoreViewModel createViewModel() {
        return ViewModelProviders.of(this).get(ScoreViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        mBinding.setModel(mModel);

        mBinding.tvNum.setOnClickListener(v -> showFrozenScore());

        chartHelper = new ChartHelper(getActivity());
        initRecyclerView();
    }

    private void showFrozenScore() {
        FrozenFragment content = new FrozenFragment();
        content.setUserId(getUserId());
        content.setOnDataChangedListener(() -> refreshPage());
        FrameDialogFragment dialog = new FrameDialogFragment();
        dialog.setTitle("Frozen Score");
        dialog.setContentFragment(content);
        dialog.show(getChildFragmentManager(), "FrozenFragment");
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvScoreList.setLayoutManager(manager);
        mBinding.rvScoreList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onCreateData() {
        mBinding.tvYearSelect.setText(String.valueOf(mModel.getCurrentYear()));
        mBinding.ivDateNext.setVisibility(View.INVISIBLE);

        mModel.userObserver.observe(this, user -> mBinding.ivFlagBg.setImageResource(FlagProvider.getFlagRes(user.getCountry())));
        mModel.pageDataObserver.observe(this, data -> onPageDataLoaded(data));

        refreshPage();
    }

    private void refreshPage() {
        pageMode = getArguments().getInt(KEY_MODE);
        if (pageMode == FLAG_YEAR) {
            mModel.queryYearRecords(getUserId());
        } else {
            mModel.query52WeekRecords(getUserId());
        }
    }

    private long getUserId() {
        return getArguments().getLong(KEY_USER_ID);
    }

    private void onMatchClicked(ScoreBean bean) {
        if (bean != null && bean.getMatchBean() != null) {
            String date = bean.getYear() + "-";
            int month = bean.getMatchBean().getMatchBean().getMonth();
            if (month < 10) {
                date = date + "0" + month;
            } else {
                date = date + month;
            }
            MatchDialog dialog = new MatchDialog();
            dialog.setMatch(bean.getMatchBean().getId(), bean.getMatchBean().getName(), date);
            dialog.setUser(mModel.getUser());
            dialog.show(getChildFragmentManager(), "MatchDialog");
        }
    }

    private void onPageDataLoaded(ScorePageData data) {
        mBinding.tvByLevel.setSelected(true);
        showScoreByLevel();

        // 显示场地胜率统计
        showCourtChart(data);
        // 52 week记录才显示去年占比和今年占比
        if (pageMode == FLAG_52WEEK) {
            showYearChart(data);
        } else {
            mBinding.chartYear.setVisibility(View.GONE);
        }
    }

    private void showScoreByLevel() {
        List<Object> scoreList = mModel.getScoresByLevel();

        showScores(scoreList);
    }

    private void showScoreByMonth() {
        List<Object> scoreList = mModel.getScoresByMonth();

        showScores(scoreList);
    }

    private void showScores(List<Object> scoreList) {
        if (scoreItemAdapter == null) {
            scoreItemAdapter = new ScoreItemAdapter();
            scoreItemAdapter.setList(scoreList);
            scoreItemAdapter.setOnItemClickListener((view, position, bean) -> onMatchClicked(bean));
            mBinding.rvScoreList.setAdapter(scoreItemAdapter);
        } else {
            scoreItemAdapter.setList(scoreList);
            scoreItemAdapter.notifyDataSetChanged();
        }
    }

    private void showYearChart(ScorePageData data) {
        // 块对应的颜色
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.colorAccent));
        colors.add(getResources().getColor(R.color.grey));
        int year = mModel.getThisYear();
        String[] contents = new String[]{
                String.valueOf(year), String.valueOf(year - 1)
        };
        float[] percents = new float[]{
                (float) data.getCountScoreYear() / (float) data.getCountScore() * 100,
                (float) data.getCountScoreLastYear() / (float) data.getCountScore() * 100,
        };

        ChartStyle style = new ChartStyle();
        chartHelper.showPieChart(mBinding.chartYear, contents, percents, colors, style);
    }

    private void showCourtChart(ScorePageData data) {
        // 块对应的颜色
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.actionbar_bk_blue));
        colors.add(getResources().getColor(R.color.actionbar_bk_orange));
        colors.add(getResources().getColor(R.color.actionbar_bk_green));
        colors.add(getResources().getColor(R.color.actionbar_bk_deepblue));

        String[] contents = new String[]{
                "硬地", "红土", "草地", "室内硬地"
        };
        float[] percents = new float[]{
                (float) data.getCountScoreHard() / (float) data.getCountScore() * 100,
                (float) data.getCountScoreClay() / (float) data.getCountScore() * 100,
                (float) data.getCountScoreGrass() / (float) data.getCountScore() * 100,
                (float) data.getCountScoreInHard() / (float) data.getCountScore() * 100,
        };

        ChartStyle style = new ChartStyle();
        style.setShowCenterHole(true);
        style.setCenterText("Court");
        style.setShowLegend(true);
        style.setHideEntries(true);
        chartHelper.showPieChart(mBinding.chartCourt, contents, percents, colors, style);

        mBinding.ivDateLast.setOnClickListener(v -> showLastYear());
        mBinding.ivDateNext.setOnClickListener(v -> showNextYear());
        mBinding.tvByLevel.setOnClickListener(v -> {
            if (!mBinding.tvByLevel.isSelected()) {
                mBinding.tvByMonth.setSelected(false);
                mBinding.tvByLevel.setSelected(true);
                showScoreByLevel();
                mBinding.rvScoreList.scrollToPosition(0);
            }
        });
        mBinding.tvByMonth.setOnClickListener(v -> {
            if (!mBinding.tvByMonth.isSelected()) {
                mBinding.tvByLevel.setSelected(false);
                mBinding.tvByMonth.setSelected(true);
                showScoreByMonth();
                mBinding.rvScoreList.scrollToPosition(0);
            }
        });
    }

    public void showDateGroup() {
        if (mBinding.groupDate.getVisibility() == View.VISIBLE) {
            mBinding.groupDate.startAnimation(getDisappearAnim());
            mBinding.groupDate.setVisibility(View.GONE);
        } else {
            mBinding.groupDate.setVisibility(View.VISIBLE);
            mBinding.groupDate.startAnimation(getAppearAnim());
        }
    }

    private void showLastYear() {
        int year = mModel.getCurrentYear() - 1;
        mModel.setCurrentYear(year);
        mBinding.ivDateNext.setVisibility(View.VISIBLE);
        mBinding.tvYearSelect.setText(String.valueOf(year));
        mModel.queryYearRecords(getUserId());
    }

    private void showNextYear() {
        int year = mModel.getCurrentYear() + 1;
        mModel.setCurrentYear(year);
        if (year == Calendar.getInstance().get(Calendar.YEAR)) {
            mBinding.ivDateNext.setVisibility(View.INVISIBLE);
        }
        mBinding.tvYearSelect.setText(String.valueOf(year));
        mModel.queryYearRecords(getUserId());
    }

    public Animation getDisappearAnim() {
        Animation anim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, -1);
        anim.setDuration(500);
        return anim;
    }

    public Animation getAppearAnim() {
        Animation anim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, -1
                , Animation.RELATIVE_TO_SELF, 0);
        anim.setDuration(500);
        return anim;
    }
}
