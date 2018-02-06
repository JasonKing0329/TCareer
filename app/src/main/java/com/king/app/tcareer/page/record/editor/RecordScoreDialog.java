package com.king.app.tcareer.page.record.editor;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.Score;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.utils.ListUtil;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/6 9:43
 */
public class RecordScoreDialog extends DraggableDialogFragment {

    private User mUser;
    private CompetitorBean mCompetitor;
    private Record mRecord;
    private OnScoreListener onScoreListener;

    private EditFragment editFragment;
    private List<Score> scoreList;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestOkAction();
        requestCloseAction();
        setCancelable(false);
        setTitle("Edit score");
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        editFragment = new EditFragment();
        editFragment.setCompetitor(mCompetitor);
        editFragment.setUser(mUser);
        editFragment.setRecord(mRecord);
        editFragment.setScoreList(scoreList);
        editFragment.setOnScoreListener(onScoreListener);
        return editFragment;
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
    }

    public void setCompetitor(CompetitorBean mCompetitor) {
        this.mCompetitor = mCompetitor;
    }

    public void setRecord(Record mRecord) {
        this.mRecord = mRecord;
    }

    @Override
    protected boolean onClickOk() {
        return editFragment.onSave();
    }

    public void setOnScoreListener(OnScoreListener onScoreListener) {
        this.onScoreListener = onScoreListener;
    }

    public void setScoreList(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    public static class EditFragment extends ContentFragment implements View.OnClickListener {

        private ViewGroup groupSet;
        private Stack<ViewGroup> stackSet;
        private TextView tvPlayer1, tvPlayer2;
        private ViewGroup groupAdd;

        private CheckBox cbRetire1, cbRetire2;
        private CheckBox cbWO1, cbWO2;
        // 退赛情况只能勾选一种，也可以全都不勾选
        private CheckBox[] checkBoxes;

        private String competitor;
        private String winner;

        private User mUser;
        private CompetitorBean mCompetitor;
        private Record mRecord;
        private OnScoreListener onScoreListener;
        private List<Score> scoreList;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_input_score;
        }

        @Override
        protected void onCreate(View view) {
            initViewParams(view);
            tvPlayer1.setText(mUser.getNameShort());
            tvPlayer2.setText(mCompetitor.getNameChn());
            initScore();
        }

        private void initScore() {
            if (mRecord == null) {
                return;
            }
            // W/O
            if (mRecord.getRetireFlag() == AppConstants.RETIRE_WO) {
                if (mRecord.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                    cbWO1.setChecked(true);
                }
                else {
                    cbWO2.setChecked(true);
                }
                return;
            }
            // retire with score
            if (mRecord.getRetireFlag() == AppConstants.RETIRE_WITH_SCORE) {
                if (mRecord.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                    cbRetire1.setChecked(true);
                }
                else {
                    cbRetire2.setChecked(true);
                }
            }

            if (ListUtil.isEmpty(scoreList)) {
                addSet(null);
                return;
            }

            for (int i = 0; i < scoreList.size(); i ++) {
                Score score = scoreList.get(i);
                addSet(score);
            }
        }

        private void initViewParams(View view) {
            groupAdd = (ViewGroup) view.findViewById(R.id.input_add_set);
            groupAdd.setOnClickListener(this);
            tvPlayer1 = (TextView) view.findViewById(R.id.input_title_player1);
            tvPlayer2 = (TextView) view.findViewById(R.id.input_title_player2);
            cbRetire1 = (CheckBox) view.findViewById(R.id.input_retire1);
            cbRetire2 = (CheckBox) view.findViewById(R.id.input_retire2);
            cbWO1 = (CheckBox) view.findViewById(R.id.input_wo1);
            cbWO2 = (CheckBox) view.findViewById(R.id.input_wo2);
            checkBoxes = new CheckBox[4];
            checkBoxes[0] = cbRetire1;
            checkBoxes[1] = cbRetire2;
            checkBoxes[2] = cbWO1;
            checkBoxes[3] = cbWO2;
            for (int i = 0 ; i < 4; i ++) {
                checkBoxes[i].setOnClickListener(this);
            }
            groupSet = (ViewGroup) view.findViewById(R.id.group_set);
            stackSet = new Stack<>();
        }

        public void setUser(User mUser) {
            this.mUser = mUser;
        }

        public void setCompetitor(CompetitorBean mCompetitor) {
            this.mCompetitor = mCompetitor;
        }

        public void setRecord(Record mRecord) {
            this.mRecord = mRecord;
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.input_add_set) {
                addSet(null);
            }
            // 各种退赛情况只能有一种，或者都没有
            else if (view instanceof CheckBox) {
                CheckBox cb = (CheckBox) view;
                checkRetire(cb);
            }
        }

        private void checkRetire(CheckBox cb) {
            boolean targetCheck = false;
            // onClickListener中check状态是已经完成check的状态
            if (cb.isChecked()) {
                targetCheck = true;
            }
            if (targetCheck) {
                for (int i = 0; i < checkBoxes.length; i++) {
                    if (checkBoxes[i] != cb) {
                        checkBoxes[i].setChecked(false);
                    }
                }
            }
        }

        private void addSet(Score score) {
            // 只有顶部group显示删除按钮
            if (!stackSet.empty()) {
                stackSet.peek().findViewById(R.id.iv_delete).setVisibility(View.GONE);
            }
            ViewGroup group = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.adapter_record_score_item, null);
            TextView tvSet = group.findViewById(R.id.tv_set);
            EditText etScoreUser = group.findViewById(R.id.et_score_user);
            EditText etScoreCpt = group.findViewById(R.id.et_score_cpt);
            final EditText etTie = group.findViewById(R.id.et_tie);
            tvSet.setText("Set" + (stackSet.size() + 1));
            group.findViewById(R.id.tv_tie).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    etTie.setVisibility(etTie.getVisibility() == View.VISIBLE ? View.GONE:View.VISIBLE);
                }
            });
            ImageView ivDelete = group.findViewById(R.id.iv_delete);
            if (stackSet.empty()) {
                ivDelete.setVisibility(View.GONE);
            }
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteSet();
                }
            });

            if (score != null) {
                etScoreUser.setText(String.valueOf(score.getUserPoint()));
                etScoreCpt.setText(String.valueOf(score.getCompetitorPoint()));
                if (score.getIsTiebreak()) {
                    etTie.setVisibility(View.VISIBLE);
                    etTie.setText(String.valueOf(score.getUserTiebreak() > 0
                            ? score.getUserTiebreak():score.getCompetitorTiebreak()));
                }
            }

            stackSet.push(group);

            groupSet.addView(group);

            if (stackSet.size() == 5) {
                groupAdd.setVisibility(View.GONE);
            }
        }

        /**
         * 删除最末一盘，恢复上一盘的删除按钮，重新显示add set
         */
        private void deleteSet() {
            ViewGroup group = stackSet.pop();
            groupSet.removeView(group);
            ViewGroup top = stackSet.peek();
            // 第一盘不允许删除
            if (stackSet.size() != 1) {
                top.findViewById(R.id.iv_delete).setVisibility(View.VISIBLE);
            }
            groupAdd.setVisibility(View.VISIBLE);
        }

        public boolean onSave() {
            int retireFlag = AppConstants.RETIRE_NONE;
            int winnerFlag = AppConstants.WINNER_USER;
            List<Score> scoreList = new ArrayList<>();
            if (cbWO1.isChecked()) {
                retireFlag = AppConstants.RETIRE_WO;
                winnerFlag = AppConstants.WINNER_COMPETITOR;
            }
            else if (cbWO2.isChecked()) {
                retireFlag = AppConstants.RETIRE_WO;
            }
            else {
                int userWin = 0;
                int userLose = 0;
                while (stackSet.size() > 0) {
                    ViewGroup group = stackSet.pop();
                    Score score = formatScore(group);
                    scoreList.add(score);
                    if (score.getUserPoint() > score.getCompetitorPoint()) {
                        userWin ++;
                    }
                    else {
                        userLose ++;
                    }
                }
                Collections.reverse(scoreList);
                if (cbRetire1.isChecked()) {
                    retireFlag = AppConstants.RETIRE_WITH_SCORE;
                    winnerFlag = AppConstants.WINNER_COMPETITOR;
                }
                else if (cbRetire2.isChecked()) {
                    retireFlag = AppConstants.RETIRE_WITH_SCORE;
                }
                else {
                    // 根据盘分判定winner
                    if (userLose > userWin) {
                        winnerFlag = AppConstants.WINNER_COMPETITOR;
                    }
                }
            }
            onScoreListener.onCompleteScore(scoreList, retireFlag, winnerFlag);
            return true;
        }

        private EditText getUserPointEdit(ViewGroup group) {
            return group.findViewById(R.id.et_score_user);
        }

        private EditText getCompetitorPointEdit(ViewGroup group) {
            return group.findViewById(R.id.et_score_cpt);
        }

        private EditText getTieEdit(ViewGroup group) {
            return group.findViewById(R.id.et_tie);
        }

        private Score formatScore(ViewGroup group) {
            Score score = new Score();
            score.setSetNo(stackSet.size() + 1);
            score.setUserPoint(Integer.parseInt(getUserPointEdit(group).getText().toString()));
            score.setCompetitorPoint(Integer.parseInt(getCompetitorPointEdit(group).getText().toString()));
            if (getTieEdit(group).getVisibility() == View.VISIBLE) {
                score.setIsTiebreak(true);
                if (score.getUserPoint() > score.getCompetitorPoint()) {
                    score.setCompetitorTiebreak(Integer.parseInt(getTieEdit(group).getText().toString()));
                }
                else {
                    score.setUserTiebreak(Integer.parseInt(getTieEdit(group).getText().toString()));
                }
            }
            return score;
        }

        public void setOnScoreListener(OnScoreListener onScoreListener) {
            this.onScoreListener = onScoreListener;
        }

        public void setScoreList(List<Score> scoreList) {
            this.scoreList = scoreList;
        }

    }

    public interface OnScoreListener {
        void onCompleteScore(List<Score> scoreList, int retireFlag, int winnerFlag);
    }
}
