package com.king.app.tcareer.page.rank;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityRankManageBinding;
import com.king.app.tcareer.model.db.entity.Rank;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/22 19:07
 */
public class RankManageActivity extends MvvmActivity<ActivityRankManageBinding, RankYearViewModel> {

    public static final String KEY_USER_ID = "key_user_id";

    private static final int REQUEST_DETAIL = 100;

    private RankYearEndFragment ftChart;

    private RankItemAdapter rankItemAdapter;

    private long userId;

    @Override
    protected int getContentView() {
        return R.layout.activity_rank_manage;
    }

    @Override
    protected RankYearViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RankYearViewModel.class);
    }

    @Override
    protected void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvRankList.setLayoutManager(manager);
        mBinding.rvRankList.setItemAnimator(new DefaultItemAnimator());

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.ivAdd.setOnClickListener(v -> addRank());
    }

    @Override
    public void initData() {
        userId = getIntent().getLongExtra(KEY_USER_ID, -1);

        mModel.userObserver.observe(this, user -> mBinding.actionbar.setTitle(user.getNameEng()));
        mModel.ranksObserver.observe(this, list -> showYearRanks(list));
        mModel.loadYearRanks(userId);
    }

    private void showYearRanks(final List<Rank> rankList) {
        rankItemAdapter = new RankItemAdapter();
        rankItemAdapter.setList(rankList);
        rankItemAdapter.setOnRankActionListener(new RankItemAdapter.OnRankActionListener() {
            @Override
            public void onDeleteRank(int position) {
                mModel.deleteRank(rankList.get(position));
                tagUpdated();
                refreshRanks();
            }

            @Override
            public void onEditRank(int position) {
                updateRank(rankList.get(position));
            }
        });
        mBinding.rvRankList.setAdapter(rankItemAdapter);

        initChartFragment();
    }

    private void initChartFragment() {
        ftChart = RankYearEndFragment.newInstance(userId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.group_chart_container, ftChart, "RankYearEndFragment");
        ft.commit();
    }

    /**
     * 标志更新过数据
     */
    private void tagUpdated() {
        setResult(RESULT_OK);
    }

    private void addRank() {
        ScoreEditDialog dialog = new ScoreEditDialog();
        dialog.setMode(ScoreEditDialog.MODE_YEAR_RANK);
        dialog.setUser(mModel.getUser());
        dialog.setOnRankListener(rank -> {
            rank.setUserId(userId);
            mModel.saveRankFinal(rank);
            tagUpdated();
            refreshRanks();
        });
        dialog.show(getSupportFragmentManager(), "ScoreEditDialog");
    }

    private void updateRank(Rank rank) {
        ScoreEditDialog dialog = new ScoreEditDialog();
        dialog.setMode(ScoreEditDialog.MODE_YEAR_RANK);
        dialog.setRank(rank);
        dialog.setUser(mModel.getUser());
        dialog.setOnRankListener(rank1 -> {
            rank1.setUserId(userId);
            mModel.saveRankFinal(rank1);
            tagUpdated();
            refreshRanks();
        });
        dialog.show(getSupportFragmentManager(), "ScoreEditDialog");
    }

    private void refreshRanks() {
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DETAIL) {
            if (resultCode == RESULT_OK) {
                tagUpdated();
                refreshRanks();
            }
        }
    }
}
