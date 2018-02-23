package com.king.app.tcareer.page.score;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseActivity;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.db.entity.Rank;
import com.king.app.tcareer.model.db.entity.RankCareer;
import com.king.app.tcareer.page.rank.ScoreEditDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/2/20 16:21
 */
public class ScoreActivity extends BaseMvpActivity<ScoreHolderPresenter> implements IScoreHolder, ScoreView {

    public static final String KEY_USER_ID = "key_user_id";

    @BindView(R.id.score_actionbar_year)
    TextView tvYear;
    @BindView(R.id.score_actionbar_week)
    TextView tvWeeks;

    @BindView(R.id.score_actionbar_year_divider)
    View vDividerYear;
    @BindView(R.id.score_actionbar_week_divider)
    View vDividerWeeks;

    @BindView(R.id.score_actionbar_date)
    ImageView ivDate;

    private ScoreFragment ftYear;
    private ScoreFragment ft52Week;

    private ScoreEditDialog editDialog;

    @Override
    protected int getContentView() {
        return R.layout.activity_score;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected ScoreHolderPresenter createPresenter() {
        return new ScoreHolderPresenter();
    }

    @Override
    protected void initData() {
        presenter.loadRank(getIntent().getLongExtra(KEY_USER_ID, -1));
    }

    @Override
    public void showRanks() {
        show52WeekScores();
    }

    @Override
    public RankCareer getRankCareer() {
        return presenter.getRankCareer();
    }

    @OnClick({R.id.score_actionbar_year, R.id.score_actionbar_week, R.id.score_actionbar_edit, R.id.score_actionbar_back, R.id.score_actionbar_date})
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.score_actionbar_year:
                showYearScores();
                break;
            case R.id.score_actionbar_week:
                show52WeekScores();
                break;
            case R.id.score_actionbar_edit:
                showScoreEditDialog();
                break;
            case R.id.score_actionbar_back:
                onBackPressed();
                break;
            case R.id.score_actionbar_date:
                showDateGroup();
                break;
        }
    }

    private void showDateGroup() {
        ftYear.showDateGroup();
    }

    private void showScoreEditDialog() {
        editDialog = new ScoreEditDialog();
        editDialog.setMode(ScoreEditDialog.MODE_COUNT_RANK);
        editDialog.setUser(presenter.getUser());
        editDialog.setRankCareer(getRankCareer());
        editDialog.setOnRankListener(new ScoreEditDialog.OnRankListener() {
            @Override
            public void onSaveYearRank(Rank rank) {
            }

            @Override
            public void onSaveCountRank(RankCareer rank) {
                presenter.updateRankCareer(rank);
                getFocusFragment().onRankChanged(rank);
                setResult(RESULT_OK);
            }
        });
        editDialog.show(getSupportFragmentManager(), "ScoreEditDialog");
    }

    private void show52WeekScores() {
        setFocusTab(tvWeeks);
        show52WeekFragment();
    }

    private void showYearScores() {
        setFocusTab(tvYear);
        showYearFragment();
    }

    private void show52WeekFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (ft52Week == null) {
            ft52Week = ScoreFragment.newInstance(getIntent().getLongExtra(KEY_USER_ID, -1), ScoreFragment.FLAG_52WEEK);
            ft.add(R.id.score_ft_container, ft52Week, "ScoreFragment_52Week");
        }
        else {
            ft.show(ft52Week).hide(ftYear);
        }
        ft.commit();
    }

    private void showYearFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (ftYear == null) {
            ftYear = ScoreFragment.newInstance(getIntent().getLongExtra(KEY_USER_ID, -1), ScoreFragment.FLAG_YEAR);
            ft.add(R.id.score_ft_container, ftYear, "ScoreFragment_Year").hide(ft52Week);
        }
        else {
            ft.show(ftYear).hide(ft52Week);
        }
        ft.commit();
    }

    private ScoreFragment getFocusFragment() {
        if (tvYear.isSelected()) {
            return ftYear;
        }
        else {
            return ft52Week;
        }
    }

    public void setFocusTab(TextView focusTab) {
        if (focusTab == tvYear) {
            tvYear.setSelected(true);
            tvWeeks.setSelected(false);
            vDividerYear.setVisibility(View.VISIBLE);
            vDividerWeeks.setVisibility(View.INVISIBLE);
            ivDate.setVisibility(View.VISIBLE);
        }
        else {
            tvYear.setSelected(false);
            tvWeeks.setSelected(true);
            vDividerYear.setVisibility(View.INVISIBLE);
            vDividerWeeks.setVisibility(View.VISIBLE);
            ivDate.setVisibility(View.GONE);
        }
    }

}
