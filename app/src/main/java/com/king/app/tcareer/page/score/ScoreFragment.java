package com.king.app.tcareer.page.score;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpFragment;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.FlagProvider;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.match.MatchDialog;
import com.king.app.tcareer.utils.FormatUtil;
import com.king.app.tcareer.utils.RetireUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/2/21 14:14
 */
public class ScoreFragment extends BaseMvpFragment<ScorePresenter> implements IScorePageView {

    public static final int FLAG_52WEEK = 0;
    public static final int FLAG_YEAR = 1;

    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_MODE = "key_mode";

    @BindView(R.id.iv_flag_bg)
    ImageView ivFlagBg;
    @BindView(R.id.tv_country)
    TextView tvCountry;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_height)
    TextView tvHeight;
    @BindView(R.id.tv_match_number)
    TextView tvMatchNumber;
    @BindView(R.id.tv_player)
    TextView tvPlayer;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_rank)
    TextView tvRank;
    @BindView(R.id.group_player_basic)
    RelativeLayout groupPlayerBasic;
    @BindView(R.id.rv_score_list)
    RecyclerView rvScoreList;
    @BindView(R.id.chart_court)
    PieChart chartCourt;
    @BindView(R.id.chart_year)
    PieChart chartYear;
    @BindView(R.id.iv_date_last)
    ImageView ivDateLast;
    @BindView(R.id.tv_year_select)
    TextView tvYearSelect;
    @BindView(R.id.iv_date_next)
    ImageView ivDateNext;
    @BindView(R.id.group_date)
    RelativeLayout groupDate;
    @BindView(R.id.tv_by_level)
    TextView tvByLevel;
    @BindView(R.id.tv_by_month)
    TextView tvByMonth;

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
    protected void onCreate(View view) {
        chartHelper = new ChartHelper(getActivity());
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvScoreList.setLayoutManager(manager);
        rvScoreList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected ScorePresenter createPresenter() {
        return new ScorePresenter();
    }

    @Override
    protected void onCreateData() {
        pageMode = getArguments().getInt(KEY_MODE);
        if (pageMode == FLAG_YEAR) {
            presenter.queryYearRecords(getUserId());
        } else {
            if (RetireUtil.isEffectiveRetiredNow(getUserId())) {
                showUser(presenter.queryUserInstant(getUserId()));
                tvByLevel.setVisibility(View.INVISIBLE);
                tvByMonth.setVisibility(View.INVISIBLE);
            }
            else {
                presenter.query52WeekRecords(getUserId());
            }
        }
    }
    
    private long getUserId(){
        return getArguments().getLong(KEY_USER_ID);
    }

    @Override
    public void showUser(User mUser) {
        ivFlagBg.setImageResource(FlagProvider.getFlagRes(mUser.getCountry()));
        tvPlayer.setText(mUser.getNameEng());
        tvCountry.setText(mUser.getCountry());
        tvBirthday.setText(mUser.getBirthday());
        tvHeight.setText(mUser.getHeight() + "  " + FormatUtil.formatNumber(mUser.getWeight()) + "kg");
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
            dialog.setUser(presenter.getUser());
            dialog.show(getChildFragmentManager(), "MatchDialog");
        }
    }

    @Override
    public void onPageDataLoaded(ScorePageData data) {

        if (RetireUtil.isEffectiveRetiredNow(getUserId())) {
            tvRank.setVisibility(View.INVISIBLE);
        }
        else {
            if (data.getRank() == 0) {
                tvRank.setText("--");
            } else {
                tvRank.setText(String.valueOf(data.getRank()));
            }
        }

        tvByLevel.setSelected(true);
        showScoreByLevel();

        tvTotal.setText(String.valueOf(data.getCountScore()));
        tvMatchNumber.setText("Match count " + String.valueOf(data.getScoreList().size()));

        // 显示场地胜率统计
        showCourtChart(data);
        // 52 week记录才显示去年占比和今年占比
        if (pageMode == FLAG_52WEEK) {
            showYearChart(data);
        } else {
            chartYear.setVisibility(View.GONE);
        }
    }

    private void showScoreByLevel() {
        List<ScoreBean> scoreList = presenter.getScoresByLevel();

        showScores(scoreList);
    }

    private void showScoreByMonth() {
        List<ScoreBean> scoreList = presenter.getScoresByMonth();

        showScores(scoreList);
    }

    private void showScores(List<ScoreBean> scoreList) {
        if (scoreItemAdapter == null) {
            scoreItemAdapter = new ScoreItemAdapter(scoreList);
            scoreItemAdapter.setOnScoreItemClickListener(new ScoreItemAdapter.OnScoreItemClickListener() {
                @Override
                public void onScoreItemClick(ScoreBean bean) {
                    onMatchClicked(bean);
                }
            });
            rvScoreList.setAdapter(scoreItemAdapter);
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
        int year = presenter.getThisYear();
        String[] contents = new String[]{
                String.valueOf(year), String.valueOf(year - 1)
        };
        float[] percents = new float[]{
                (float) data.getCountScoreYear() / (float) data.getCountScore() * 100,
                (float) data.getCountScoreLastYear() / (float) data.getCountScore() * 100,
        };

        ChartStyle style = new ChartStyle();
        chartHelper.showPieChart(chartYear, contents, percents, colors, style);
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
        chartHelper.showPieChart(chartCourt, contents, percents, colors, style);
    }

    @OnClick({R.id.iv_date_last, R.id.iv_date_next, R.id.tv_by_level, R.id.tv_by_month})
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.iv_date_last:
                showLastYear();
                break;
            case R.id.iv_date_next:
                showNextYear();
                break;
            case R.id.tv_by_level:
                if (!tvByLevel.isSelected()) {
                    tvByMonth.setSelected(false);
                    tvByLevel.setSelected(true);
                    showScoreByLevel();
                    rvScoreList.scrollToPosition(0);
                }
                break;
            case R.id.tv_by_month:
                if (!tvByMonth.isSelected()) {
                    tvByLevel.setSelected(false);
                    tvByMonth.setSelected(true);
                    showScoreByMonth();
                    rvScoreList.scrollToPosition(0);
                }
                break;
        }
    }

    public void showDateGroup() {
        if (groupDate.getVisibility() == View.VISIBLE) {
            groupDate.startAnimation(getDisappearAnim());
            groupDate.setVisibility(View.GONE);
        } else {
            groupDate.setVisibility(View.VISIBLE);
            groupDate.startAnimation(getAppearAnim());
        }
    }

    private void showLastYear() {
        int year = presenter.getCurrentYear() - 1;
        presenter.setCurrentYear(year);
        tvYearSelect.setText(String.valueOf(year));
        presenter.queryYearRecords(getUserId());
    }

    private void showNextYear() {
        int year = presenter.getCurrentYear() + 1;
        presenter.setCurrentYear(year);
        tvYearSelect.setText(String.valueOf(year));
        presenter.queryYearRecords(getUserId());
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
