package com.king.app.tcareer.page.rank;

import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.page.score.ScoreCalculator;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/8 14:02
 */
public class RankDetailActivity extends BaseMvpActivity<RankDetailPresenter> implements RankDetailView {

    public static final String KEY_USER_ID = "user_id";

    @BindView(R.id.rv_ranks)
    RecyclerView rvRanks;

    private RankDetailAdapter detailAdapter;

    private RankDetailFragment ftDetail;

    @Override
    protected int getContentView() {
        return R.layout.activity_rank_detail;
    }

    @Override
    protected void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvRanks.setLayoutManager(manager);
    }

    @Override
    protected RankDetailPresenter createPresenter() {
        return new RankDetailPresenter();
    }

    @Override
    protected void initData() {
        long userId = getIntent().getLongExtra(KEY_USER_ID, -1);
        initChart(userId);
        presenter.loadRanks(userId, true);
    }

    private void initChart(long userId) {
        ftDetail = RankDetailFragment.newInstance(userId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_ft_container, ftDetail, "RankDetailFragment")
                .commit();
    }

    @Override
    public void showRanks(List<RankWeek> list) {
        if (detailAdapter == null) {
            detailAdapter = new RankDetailAdapter();
            detailAdapter.setList(list);
            detailAdapter.setOnRankItemListener(new RankDetailAdapter.OnRankItemListener() {
                @Override
                public void onUpdateItem(final int position, RankWeek item) {
                    ScoreCalculator scoreCalculator = new ScoreCalculator();
                    scoreCalculator.setUserId(presenter.getUser().getId());
                    scoreCalculator.setUpdateData(item);
                    scoreCalculator.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            detailAdapter.notifyItemChanged(position);
                            ftDetail.refresh();
                            setResult(RESULT_OK);
                        }
                    });
                    scoreCalculator.show(getSupportFragmentManager(), "ScoreCalculator");
                }

                @Override
                public void onDeleteItem(final int position, final RankWeek item) {
                    showConfirmCancelMessage("Delete data warning, continue?"
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    presenter.deleteRank(item);
                                    detailAdapter.removeItem(position);
                                    detailAdapter.notifyItemRemoved(position);
                                    ftDetail.refresh();
                                    setResult(RESULT_OK);
                                }
                            }, null);
                }
            });
            rvRanks.setAdapter(detailAdapter);
        } else {
            detailAdapter.setList(list);
            detailAdapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_add:
                showScoreCalculator();
                break;
        }
    }

    private void showScoreCalculator() {
        ScoreCalculator calculator = new ScoreCalculator();
        calculator.setUserId(presenter.getUser().getId());
        calculator.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                ftDetail.refresh();
                presenter.loadRanks(presenter.getUser().getId(), true);
                setResult(RESULT_OK);
            }
        });
        calculator.show(getSupportFragmentManager(), "ScoreCalculator");
    }

}
