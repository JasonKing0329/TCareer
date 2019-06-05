package com.king.app.tcareer.page.score;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityScoreBinding;
import com.king.app.tcareer.page.rank.ScoreEditDialog;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/2/20 16:21
 */
public class ScoreActivity extends MvvmActivity<ActivityScoreBinding, ScoreHolderViewModel> implements IScoreHolder {

    public static final String KEY_USER_ID = "key_user_id";

    private ScoreFragment ftYear;
    private ScoreFragment ft52Week;

    private ScoreEditDialog editDialog;

    @Override
    protected int getContentView() {
        return R.layout.activity_score;
    }

    @Override
    protected ScoreHolderViewModel createViewModel() {
        return ViewModelProviders.of(this).get(ScoreHolderViewModel.class);
    }

    @Override
    protected void initView() {
        setFocusTab(mBinding.tvWeek);
        mBinding.ivBack.setOnClickListener(v -> onBackPressed());
        mBinding.tvYear.setOnClickListener(v -> showYearScores());
        mBinding.tvWeek.setOnClickListener(v -> show52WeekScores());
        mBinding.ivEdit.setOnClickListener(v -> showScoreEditDialog());
        mBinding.ivDate.setOnClickListener(v -> showDateGroup());
    }

    @Override
    protected void initData() {
        mModel.userObserver.observe(this, user -> show52WeekScores());
        mModel.loadData(getIntent().getLongExtra(KEY_USER_ID, -1));
    }

    private void showDateGroup() {
        ftYear.showDateGroup();
    }

    private void showScoreEditDialog() {
        editDialog = new ScoreEditDialog();
        editDialog.setMode(ScoreEditDialog.MODE_COUNT_RANK);
        editDialog.setUser(mModel.getUser());
        editDialog.setOnRankListener(rank -> {
        });
        editDialog.show(getSupportFragmentManager(), "ScoreEditDialog");
    }

    private void show52WeekScores() {
        setFocusTab(mBinding.tvWeek);
        show52WeekFragment();
    }

    private void showYearScores() {
        setFocusTab(mBinding.tvYear);
        showYearFragment();
    }

    private void show52WeekFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (ft52Week == null) {
            ft52Week = ScoreFragment.newInstance(getIntent().getLongExtra(KEY_USER_ID, -1), ScoreFragment.FLAG_52WEEK);
            ft.add(R.id.rl_ft_container, ft52Week, "ScoreFragment_52Week");
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
            ft.add(R.id.rl_ft_container, ftYear, "ScoreFragment_Year").hide(ft52Week);
        }
        else {
            ft.show(ftYear).hide(ft52Week);
        }
        ft.commit();
    }

    private ScoreFragment getFocusFragment() {
        if (mBinding.tvYear.isSelected()) {
            return ftYear;
        }
        else {
            return ft52Week;
        }
    }

    public void setFocusTab(TextView focusTab) {
        if (focusTab == mBinding.tvYear) {
            mBinding.tvYear.setSelected(true);
            mBinding.tvWeek.setSelected(false);
            mBinding.dividerYear.setVisibility(View.VISIBLE);
            mBinding.dividerWeek.setVisibility(View.INVISIBLE);
            mBinding.ivDate.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.tvYear.setSelected(false);
            mBinding.tvWeek.setSelected(true);
            mBinding.dividerYear.setVisibility(View.INVISIBLE);
            mBinding.dividerWeek.setVisibility(View.VISIBLE);
            mBinding.ivDate.setVisibility(View.GONE);
        }
    }

}
