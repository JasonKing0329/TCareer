package com.king.app.tcareer.page.rank;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityRankDetailBinding;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.page.score.ScoreCalculator;
import com.king.app.tcareer.view.dialog.frame.FrameDialogFragment;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/8 14:02
 */
public class RankDetailActivity extends MvvmActivity<ActivityRankDetailBinding, RankDetailViewModel> {

    public static final String KEY_USER_ID = "user_id";

    private RankDetailAdapter detailAdapter;

    private RankWeekFragment ftRankWeek;

    @Override
    protected int getContentView() {
        return R.layout.activity_rank_detail;
    }

    @Override
    protected RankDetailViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RankDetailViewModel.class);
    }

    @Override
    protected void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mBinding.rvRanks.setLayoutManager(manager);

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_rank_add:
                    showScoreCalculator();
                    break;
                case R.id.menu_rank_count:
                    showRankCount();
                    break;
            }
        });
    }

    @Override
    protected void initData() {
        long userId = getIntent().getLongExtra(KEY_USER_ID, -1);
        initChart(userId);
        mModel.userObserver.observe(this, user -> mBinding.actionbar.setTitle(user.getNameEng()));
        mModel.ranksObserver.observe(this, list -> showRanks(list));
        mModel.loadRanks(userId, true);
    }

    private void initChart(long userId) {
        ftRankWeek = RankWeekFragment.newInstance(userId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_ft_container, ftRankWeek, "RankDetailFragment")
                .commit();
    }

    private void showRanks(List<RankWeek> list) {
        if (detailAdapter == null) {
            detailAdapter = new RankDetailAdapter();
            detailAdapter.setList(list);
            detailAdapter.setOnRankItemListener(new RankDetailAdapter.OnRankItemListener() {
                @Override
                public void onUpdateItem(final int position, RankWeek item) {
                    ScoreCalculator scoreCalculator = new ScoreCalculator();
                    scoreCalculator.setUserId(mModel.getUser().getId());
                    scoreCalculator.setUpdateData(item);
                    FrameDialogFragment dialog = new FrameDialogFragment();
                    dialog.setTitle("Score");
                    dialog.setContentFragment(scoreCalculator);
                    dialog.setOnDismissListener(dialogInterface -> {
                        detailAdapter.notifyItemChanged(position);
                        ftRankWeek.refresh();
                        setResult(RESULT_OK);
                    });
                    dialog.show(getSupportFragmentManager(), "ScoreCalculator");
                }

                @Override
                public void onDeleteItem(final int position, final RankWeek item) {
                    showConfirmCancelMessage("Delete data warning, continue?"
                            , (dialogInterface, i) -> {
                                mModel.deleteRank(item);
                                detailAdapter.removeItem(position);
                                detailAdapter.notifyItemRemoved(position);
                                ftRankWeek.refresh();
                                setResult(RESULT_OK);
                            }, null);
                }
            });
            mBinding.rvRanks.setAdapter(detailAdapter);
        } else {
            detailAdapter.setList(list);
            detailAdapter.notifyDataSetChanged();
        }
    }

    private void showRankCount() {
        RankCountFragment content = RankCountFragment.newInstance(mModel.getUser().getId());
        FrameDialogFragment dialogFragment = new FrameDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Rank count");
        dialogFragment.show(getSupportFragmentManager(), "RankCountFragment");
    }

    private void showScoreCalculator() {
        ScoreCalculator scoreCalculator = new ScoreCalculator();
        scoreCalculator.setUserId(mModel.getUser().getId());
        scoreCalculator.setUpdateData(null);
        FrameDialogFragment dialog = new FrameDialogFragment();
        dialog.setTitle("Score");
        dialog.setContentFragment(scoreCalculator);
        dialog.setOnDismissListener(dialogInterface -> {
            ftRankWeek.refresh();
            mModel.loadRanks(mModel.getUser().getId(), true);
            setResult(RESULT_OK);
        });
        dialog.show(getSupportFragmentManager(), "ScoreCalculator");
    }

}
