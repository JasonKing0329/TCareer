package com.king.app.tcareer.page.score;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.databinding.FragmentFrozenAddBinding;
import com.king.app.tcareer.model.db.entity.FrozenScore;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;
import com.king.app.tcareer.page.match.manage.MatchManageActivity;
import com.king.app.tcareer.view.dialog.frame.FrameContentFragment;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/4/15 9:52
 */
public class FrozenAddFragment extends FrameContentFragment<FragmentFrozenAddBinding, BaseViewModel> {

    private final int REQUEST_MATCH = 111;

    private FrozenScore bean;

    private OnFrozenScoreListener onFrozenScoreListener;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_frozen_add;
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void onCreate(View view) {
        mBinding.tvMatch.setOnClickListener(v -> selectMatch());
        mBinding.tvOk.setOnClickListener(v -> onConfirm());
    }

    public void setBean(FrozenScore bean) {
        this.bean = bean;
    }

    public void setOnFrozenScoreListener(OnFrozenScoreListener onFrozenScoreListener) {
        this.onFrozenScoreListener = onFrozenScoreListener;
    }

    @Override
    protected void onCreateData() {
        if (bean == null) {
            bean = new FrozenScore();
        }
        else {
            getMatchName(bean.getMatchNameId());
            mBinding.etDate.setText(bean.getMatchDate());
            mBinding.etScore.setText(String.valueOf(bean.getScore()));
        }
    }

    private void selectMatch() {
        Intent intent = new Intent().setClass(getContext(), MatchManageActivity.class);
        intent.putExtra(MatchManageActivity.KEY_START_MODE, MatchManageActivity.START_MODE_SELECT);
        startActivityForResult(intent, REQUEST_MATCH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MATCH) {
            if (resultCode == Activity.RESULT_OK) {
                long matchNameId = data.getLongExtra(MatchManageActivity.RESPONSE_MATCH_NAME_ID, -1);
                bean.setMatchNameId(matchNameId);
                getMatchName(matchNameId);
            }
        }
    }

    private void getMatchName(long matchNameId) {
        MatchNameBean bean = TApplication.getInstance().getDaoSession().getMatchNameBeanDao().queryBuilder()
                .where(MatchNameBeanDao.Properties.Id.eq(matchNameId))
                .build().unique();
        mBinding.tvMatch.setText(bean.getName());
    }

    private void onConfirm() {
        if (bean.getMatchNameId() == 0) {
            showMessageShort("Please select match");
            return;
        }
        String date = mBinding.etDate.getText().toString().trim();
        if (TextUtils.isEmpty(date)) {
            showMessageShort("Please input date");
            return;
        }
        bean.setMatchDate(date);
        try {
            int score = Integer.parseInt(mBinding.etScore.getText().toString().trim());
            bean.setScore(score);
        } catch (Exception e) {
            showMessageShort("Please input right score");
            return;
        }
        onFrozenScoreListener.onUpdateItem(bean);
        dismissAllowingStateLoss();
    }

    public interface OnFrozenScoreListener {
        void onUpdateItem(FrozenScore score);
    }
}
