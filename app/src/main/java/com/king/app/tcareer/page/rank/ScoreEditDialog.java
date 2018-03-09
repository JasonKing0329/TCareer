package com.king.app.tcareer.page.rank;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.utils.ListUtil;
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
            groupRankYear.setVisibility(View.VISIBLE);
            if (mRank.getYear() != 0) {
                etYear.setText(String.valueOf(mRank.getYear()));
            }
            if (mRank.getRank() != 0) {
                etRank.setText(String.valueOf(mRank.getRank()));
            }
        }

        private void showPlayerRank() {
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

        public void setRank(Rank mRank) {
            this.mRank = mRank;
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

            saveEalierAchieve(cWin, cLose, qWin, qLose);
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
    }
}
