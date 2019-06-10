package com.king.app.tcareer.page.rank;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.databinding.FragmentRankCountBinding;
import com.king.app.tcareer.view.dialog.frame.FrameContentFragment;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 13:39
 */
public class RankCountFragment extends FrameContentFragment<FragmentRankCountBinding, RankCountViewModel> {

    private static final String KEY_USER_ID = "user_id";

    public static RankCountFragment newInstance(long userId) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        RankCountFragment fragment = new RankCountFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_rank_count;
    }

    @Override
    protected RankCountViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RankCountViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        mBinding.setModel(mModel);
        mBinding.btnCondition.setOnClickListener(v -> mBinding.groupCondition.setVisibility(mBinding.groupCondition.getVisibility() == View.VISIBLE ? View.GONE:View.VISIBLE));
        mBinding.btnConditionOk.setOnClickListener(v -> {
            String min = mBinding.etMin.getText().toString();
            String max = mBinding.etMax.getText().toString();
            if (TextUtils.isEmpty(min) && TextUtils.isEmpty(max)) {
                return;
            }

            int nMin = 0;
            int nMax = 0;
            if (!TextUtils.isEmpty(min)) {
                nMin = Integer.parseInt(min);
            }
            if (!TextUtils.isEmpty(max)) {
                nMax = Integer.parseInt(max);
            }

            if (nMax == 0) {
                nMax = nMin;
            }
            if (nMin == 0) {
                nMin = nMax;
            }
            mModel.queryCondition(nMin, nMax);
        });
    }

    @Override
    protected void onCreateData() {
        long userId = getArguments().getLong(KEY_USER_ID);
        mModel.loadDatas(userId);
    }
}
