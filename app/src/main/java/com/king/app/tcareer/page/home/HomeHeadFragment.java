package com.king.app.tcareer.page.home;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.MvvmFragment;
import com.king.app.tcareer.databinding.FragmentHomeHeadBinding;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.Retire;
import com.king.app.tcareer.model.image.ImageBindingAdapter;
import com.king.app.tcareer.page.score.ScoreViewModel;
import com.king.app.tcareer.utils.RetireUtil;
import com.king.app.tcareer.utils.ScreenUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/14 15:41
 */
public class HomeHeadFragment extends MvvmFragment<FragmentHomeHeadBinding, ScoreViewModel> {

    private static final String BUNDLE_USERID = "userId";

    private IHomeHeaderHolder holder;

    public static HomeHeadFragment newInstance(long userId) {

        Bundle args = new Bundle();
        args.putLong(BUNDLE_USERID, userId);
        HomeHeadFragment fragment = new HomeHeadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        if (holder instanceof IHomeHeaderHolder) {
            this.holder = (IHomeHeaderHolder) holder;
        }
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_home_head;
    }

    @Override
    protected ScoreViewModel createViewModel() {
        return ViewModelProviders.of(this).get(ScoreViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        mBinding.setModel(mModel);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mBinding.tvRank.getLayoutParams();
        params.bottomMargin = params.bottomMargin + ScreenUtils.dp2px(20);

        mBinding.clBasic.setOnClickListener(v -> holder.onClickScoreHead());
    }

    @Override
    protected void onCreateData() {

        mModel.userObserver.observe(this, user -> {
            String imagePath = ImageProvider.getDetailPlayerPath(user.getNameChn());
            ImageBindingAdapter.setPlayerDetailUrl(mBinding.ivFlagBg, imagePath);
            checkRetirement();
        });
        // retired and in efficient time
        if (RetireUtil.isRetired(getUserId(), new Date(), true)) {
            mBinding.tvRank.setVisibility(View.INVISIBLE);
            mBinding.tvTotal.setVisibility(View.INVISIBLE);
            mModel.loadUser(getUserId());
        }
        // count score and rank
        else {
            load52WeekScore();
        }
    }

    private void load52WeekScore() {

        mModel.query52WeekRecords(getUserId());
    }

    private long getUserId() {
        long userId = getArguments().getLong(BUNDLE_USERID);
        return userId;
    }

    public void onRankChanged() {
        mBinding.tvRank.setText(String.valueOf(mModel.getRank()));
    }

    private void checkRetirement() {
        Retire retire = RetireUtil.getRetired(getUserId(), new Date(), false);
        if (retire != null) {
            mBinding.tvRetire.setVisibility(View.VISIBLE);
            mBinding.tvRetireTime.setVisibility(View.VISIBLE);
            mBinding.tvRetireTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(retire.getDeclareDate()));
        }
    }
}
