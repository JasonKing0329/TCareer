package com.king.app.tcareer.page.rank;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.jactionbar.JActionbar;
import com.king.app.jactionbar.OnBackListener;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.db.entity.Rank;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/22 19:07
 */
public class RankManageActivity extends BaseMvpActivity<RankPresenter> implements RankView, View.OnClickListener {

    public static final String KEY_USER_ID = "key_user_id";

    private static final int REQUEST_DETAIL = 100;

    @BindView(R.id.actionbar)
    JActionbar actionbar;
    @BindView(R.id.group_chart_container)
    ViewGroup groupChartContainer;
    @BindView(R.id.rank_manage_list)
    RecyclerView rvRankList;

    private RankYearEndFragment ftChart;

    private RankItemAdapter rankItemAdapter;

    private long userId;

    @Override
    protected int getContentView() {
        return R.layout.activity_rank_manage;
    }

    @Override
    protected void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRankList.setLayoutManager(manager);
        rvRankList.setItemAnimator(new DefaultItemAnimator());

        actionbar.setOnBackListener(new OnBackListener() {
            @Override
            public void onBack() {
                onBackPressed();
            }
        });
    }

    @Override
    protected RankPresenter createPresenter() {
        return new RankPresenter();
    }

    @Override
    public void initData() {
        userId = getIntent().getLongExtra(KEY_USER_ID, -1);
        presenter.loadYearRanks(userId);
    }

    @Override
    public void postShowUser(final String nameEng) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                actionbar.setTitle(nameEng);
            }
        });
    }

    @Override
    public void showRanks(final List<Rank> rankList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showYearRanks(rankList);
            }
        });
    }

    private void showYearRanks(final List<Rank> rankList) {
        rankItemAdapter = new RankItemAdapter(rankList);
        rankItemAdapter.setOnRankActionListener(new RankItemAdapter.OnRankActionListener() {
            @Override
            public void onDeleteRank(int position) {
                presenter.deleteRank(rankList.get(position));
                tagUpdated();
                refreshRanks();
            }

            @Override
            public void onEditRank(int position) {
                updateRank(rankList.get(position));
            }
        });
        rvRankList.setAdapter(rankItemAdapter);

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

    @OnClick({R.id.iv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                addRank();
                break;
        }
    }

    private void addRank() {
        ScoreEditDialog dialog = new ScoreEditDialog();
        dialog.setMode(ScoreEditDialog.MODE_YEAR_RANK);
        dialog.setUser(presenter.getUser());
        dialog.setOnRankListener(new ScoreEditDialog.OnRankListener() {
            @Override
            public void onSaveYearRank(Rank rank) {
                rank.setUserId(userId);
                presenter.saveRankFinal(rank);
                tagUpdated();
                refreshRanks();
            }
        });
        dialog.show(getSupportFragmentManager(), "ScoreEditDialog");
    }

    private void updateRank(Rank rank) {
        ScoreEditDialog dialog = new ScoreEditDialog();
        dialog.setMode(ScoreEditDialog.MODE_YEAR_RANK);
        dialog.setRank(rank);
        dialog.setUser(presenter.getUser());
        dialog.setOnRankListener(new ScoreEditDialog.OnRankListener() {
            @Override
            public void onSaveYearRank(Rank rank) {
                rank.setUserId(userId);
                presenter.saveRankFinal(rank);
                tagUpdated();
                refreshRanks();
            }
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
