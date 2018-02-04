package com.king.app.tcareer.page.rank;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.bean.RankBean;
import com.king.app.tcareer.model.db.entity.Rank;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/4 0004 13:18
 */

public class ScoreEditDialog extends DraggableDialogFragment {

    /**
     * current rank, top1 week, highest rank
     */
    public static final int MODE_COUNT_RANK = 0;

    /**
     * year rank
     */
    public static final int MODE_YEAR_RANK = 1;

    private EditFragment editFragment;

    private int mode;

    private Rank mRank;

    private RankBean mRankBean;

    private OnRankListener onRankListener;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestOkAction();
        requestCloseAction();
        setTitle("Rank");
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        editFragment = new EditFragment();
        editFragment.setMode(mode);
        editFragment.setRank(mRank);
        editFragment.setRankBean(mRankBean);
        editFragment.setOnRankListener(onRankListener);
        return editFragment;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setRank(Rank mRank) {
        this.mRank = mRank;
    }

    public void setRankBean(RankBean mRankBean) {
        this.mRankBean = mRankBean;
    }

    @Override
    protected boolean onClickOk() {
        return editFragment.onSave();
    }

    public void setOnRankListener(OnRankListener onRankListener) {
        this.onRankListener = onRankListener;
    }

    @Override
    protected int getMaxHeight() {
        return ScreenUtils.getScreenHeight(getActivity()) * 1 / 3;
    }

    public static class EditFragment extends ContentFragment {

        @BindView(R.id.score_manage_rank)
        EditText scoreManageRank;
        @BindView(R.id.score_manage_rank_highest)
        EditText scoreManageRankHighest;
        @BindView(R.id.score_manage_top1_week)
        EditText scoreManageTop1Week;
        @BindView(R.id.score_manage_group_top1_week)
        LinearLayout scoreManageGroupTop1Week;
        @BindView(R.id.group_rank_count)
        LinearLayout groupRankCount;
        @BindView(R.id.et_year)
        EditText etYear;
        @BindView(R.id.et_rank)
        EditText etRank;
        @BindView(R.id.group_rank_year)
        LinearLayout groupRankYear;
        Unbinder unbinder;

        private int mode;

        private Rank mRank;

        private RankBean mRankBean;

        private OnRankListener onRankListener;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_score_manage;
        }

        @Override
        protected void onCreate(View view) {
            unbinder = ButterKnife.bind(this, view);

            if (mode == MODE_YEAR_RANK) {
                if (mRank == null) {
                    mRank = new Rank();
                }
                groupRankCount.setVisibility(View.GONE);
                groupRankYear.setVisibility(View.VISIBLE);
                if (mRank.getYear() != 0) {
                    etYear.setText(String.valueOf(mRank.getYear()));
                }
                if (mRank.getRank() != 0) {
                    etRank.setText(String.valueOf(mRank.getRank()));
                }
            }
            else {
//                mRankBean = getRankData();
                if (mRankBean == null) {
                    mRankBean = new RankBean();
                }
                else {
                    scoreManageRank.setText(String.valueOf(mRankBean.getRank()));
                    scoreManageRankHighest.setText(String.valueOf(mRankBean.getHighestRank()));
                    showTop1Week();
                }
            }
        }

        private void showTop1Week() {
            if (mRankBean.getHighestRank() == 1) {
                scoreManageGroupTop1Week.setVisibility(View.VISIBLE);
                scoreManageTop1Week.setText(String.valueOf(mRankBean.getTop1Week()));
            }
            else {
                scoreManageTop1Week.setVisibility(View.GONE);
            }
        }

        public void setRank(Rank mRank) {
            this.mRank = mRank;
        }

        public void setRankBean(RankBean mRankBean) {
            this.mRankBean = mRankBean;
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public boolean onSave() {
            if (mode == MODE_YEAR_RANK) {
                if (mRank == null) {
                    mRank = new Rank();
                }
                String year = etYear.getText().toString();
                if (TextUtils.isEmpty(year)) {
                    etYear.setError("year can't be null");
                    return false;
                }
                mRank.setYear(Integer.parseInt(year));
                String rank = etRank.getText().toString();
                if (TextUtils.isEmpty(rank)) {
                    etRank.setError("rank can't be null");
                    return false;
                }
                mRank.setRank(Integer.parseInt(rank));
                onRankListener.onSaveYearRank(mRank);
                return true;
            }
            else {
                String rank = scoreManageRank.getText().toString();
                if (TextUtils.isEmpty(rank)) {
                    scoreManageRank.setError("rank can't be null");
                    return false;
                }
                mRankBean.setRank(Integer.parseInt(rank));
                String highest = scoreManageRankHighest.getText().toString();
                if (TextUtils.isEmpty(rank)) {
                    scoreManageRankHighest.setError("highest rank can't be null");
                    return false;
                }
                mRankBean.setHighestRank(Integer.parseInt(highest));
                if (scoreManageTop1Week.getVisibility() == View.VISIBLE) {
                    String top1Week = scoreManageTop1Week.getText().toString();
                    if (TextUtils.isEmpty(rank)) {
                        scoreManageTop1Week.setError("top 1 week can't be null");
                        return false;
                    }
                    mRankBean.setTop1Week(Integer.parseInt(top1Week));
                }

//                new FileIO().saveRankBean(rankBean, MultiUserManager.getInstance().getTargetRankFile(null));
//                actionListener.onSave(rankBean);
                return true;
            }
        }

        public void setOnRankListener(OnRankListener onRankListener) {
            this.onRankListener = onRankListener;
        }
    }

    public interface OnRankListener {
        void onSaveYearRank(Rank rank);
    }
}
