package com.king.app.tcareer.page.rank;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.EarlierAchieve;
import com.king.app.tcareer.model.db.entity.EarlierAchieveDao;
import com.king.app.tcareer.model.db.entity.Rank;
import com.king.app.tcareer.model.db.entity.RankCareer;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.utils.ListUtil;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.util.List;

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

    private RankCareer mRankCareer;

    private User mUser;

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
        editFragment.setRankCareer(mRankCareer);
        editFragment.setUser(mUser);
        editFragment.setOnRankListener(onRankListener);
        return editFragment;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setRank(Rank mRank) {
        this.mRank = mRank;
    }

    public void setRankCareer(RankCareer mRankCareer) {
        this.mRankCareer = mRankCareer;
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
        if (mode == MODE_YEAR_RANK) {
            return ScreenUtils.getScreenHeight(getActivity()) * 1 / 3;
        }
        else {
            return ScreenUtils.getScreenHeight(getActivity()) * 1 / 2;
        }
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
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
        @BindView(R.id.group_earlier)
        LinearLayout groupEarlier;
        @BindView(R.id.et_challenge_win)
        EditText etChallengeWin;
        @BindView(R.id.et_challenge_lose)
        EditText etChallengeLose;
        @BindView(R.id.et_qualify_win)
        EditText etQualifyWin;
        @BindView(R.id.et_qualify_lose)
        EditText etQualifyLose;

        Unbinder unbinder;

        private int mode;

        private Rank mRank;

        private RankCareer mRankCareer;

        private User mUser;

        private OnRankListener onRankListener;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_score_manage;
        }

        public void setUser(User mUser) {
            this.mUser = mUser;
        }

        @Override
        protected void onCreate(View view) {
            unbinder = ButterKnife.bind(this, view);

            if (mode == MODE_YEAR_RANK) {
                showYearRank();
            } else {
                showPlayerRank();
            }
        }

        private void showYearRank() {
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

        private void showPlayerRank() {
            scoreManageRankHighest.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (Integer.parseInt(charSequence.toString()) == 1) {
                            scoreManageTop1Week.setText(String.valueOf(mRankCareer.getTop1Week()));
                            scoreManageGroupTop1Week.setVisibility(View.VISIBLE);
                        } else {
                            mRankCareer.setTop1Week(0);
                            scoreManageGroupTop1Week.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        mRankCareer.setTop1Week(0);
                        scoreManageGroupTop1Week.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            if (mRankCareer == null) {
                mRankCareer = new RankCareer();
            } else {
                scoreManageRank.setText(String.valueOf(mRankCareer.getRankCurrent()));
                scoreManageRankHighest.setText(String.valueOf(mRankCareer.getRankHighest()));
                showTop1Week();
            }

            groupEarlier.setVisibility(View.VISIBLE);
            if (!ListUtil.isEmpty(mUser.getEarlierAchieves())) {
                for (EarlierAchieve achieve:mUser.getEarlierAchieves()) {
                    if (achieve.getType() == AppConstants.ACHIEVE_CHALLENGE) {
                        etChallengeWin.setText(String.valueOf(achieve.getWin()));
                        etChallengeLose.setText(String.valueOf(achieve.getLose()));
                    }
                    else if (achieve.getType() == AppConstants.ACHIEVE_QUALIFY) {
                        etQualifyWin.setText(String.valueOf(achieve.getWin()));
                        etQualifyLose.setText(String.valueOf(achieve.getLose()));
                    }
                }
            }
        }

        private void showTop1Week() {
            if (mRankCareer.getRankHighest() == 1) {
                scoreManageGroupTop1Week.setVisibility(View.VISIBLE);
                scoreManageTop1Week.setText(String.valueOf(mRankCareer.getTop1Week()));
            } else {
                scoreManageGroupTop1Week.setVisibility(View.GONE);
            }
        }

        public void setRank(Rank mRank) {
            this.mRank = mRank;
        }

        public void setRankCareer(RankCareer rankCareer) {
            this.mRankCareer = rankCareer;
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
                return saveYearRank();
            } else {
                return savePlayerRank();
            }
        }

        private boolean savePlayerRank() {
            String rank = scoreManageRank.getText().toString();
            if (TextUtils.isEmpty(rank)) {
                scoreManageRank.setError("rank can't be null");
                return false;
            }
            mRankCareer.setRankCurrent(Integer.parseInt(rank));
            String highest = scoreManageRankHighest.getText().toString();
            if (TextUtils.isEmpty(rank)) {
                scoreManageRankHighest.setError("highest rank can't be null");
                return false;
            }
            mRankCareer.setRankHighest(Integer.parseInt(highest));
            String cWin = etChallengeWin.getText().toString();
            if (TextUtils.isEmpty(cWin)) {
                etChallengeWin.setError("challenge win can't be null");
                return false;
            }
            String cLose = etChallengeLose.getText().toString();
            if (TextUtils.isEmpty(cLose)) {
                etChallengeLose.setError("challenge lose can't be null");
                return false;
            }
            String qWin = etQualifyWin.getText().toString();
            if (TextUtils.isEmpty(qWin)) {
                etQualifyWin.setError("qualify win can't be null");
                return false;
            }
            String qLose = etQualifyLose.getText().toString();
            if (TextUtils.isEmpty(qLose)) {
                etQualifyLose.setError("qualify lose can't be null");
                return false;
            }
            if (scoreManageGroupTop1Week.getVisibility() == View.VISIBLE) {
                String top1Week = scoreManageTop1Week.getText().toString();
                if (TextUtils.isEmpty(rank)) {
                    scoreManageTop1Week.setError("top 1 week can't be null");
                    return false;
                }
                mRankCareer.setTop1Week(Integer.parseInt(top1Week));
            }

            saveEalierAchieve(cWin, cLose, qWin, qLose);
            onRankListener.onSaveCountRank(mRankCareer);
            return true;
        }

        private void saveEalierAchieve(String cWin, String cLose, String qWin, String qLose) {
            EarlierAchieve challenge = null;
            EarlierAchieve qualify = null;
            for (EarlierAchieve achieve:mUser.getEarlierAchieves()) {
                if (achieve.getType() == AppConstants.ACHIEVE_CHALLENGE) {
                    challenge = achieve;
                }
                else if (achieve.getType() == AppConstants.ACHIEVE_QUALIFY) {
                    qualify = achieve;
                }
            }

            EarlierAchieveDao dao = TApplication.getInstance().getDaoSession().getEarlierAchieveDao();

            if (challenge == null) {
                challenge = new EarlierAchieve();
            }
            challenge.setUserId(mUser.getId());
            challenge.setWin(Integer.parseInt(cWin));
            challenge.setLose(Integer.parseInt(cLose));
            challenge.setType(AppConstants.ACHIEVE_CHALLENGE);
            dao.insertOrReplace(challenge);

            if (qualify == null) {
                qualify = new EarlierAchieve();
            }
            qualify.setUserId(mUser.getId());
            qualify.setWin(Integer.parseInt(qWin));
            qualify.setLose(Integer.parseInt(qLose));
            qualify.setType(AppConstants.ACHIEVE_QUALIFY);
            dao.insertOrReplace(qualify);

            mUser.resetEarlierAchieves();
        }

        private boolean saveYearRank() {
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

        public void setOnRankListener(OnRankListener onRankListener) {
            this.onRankListener = onRankListener;
        }
    }

    public interface OnRankListener {
        void onSaveYearRank(Rank rank);

        void onSaveCountRank(RankCareer rank);
    }
}
