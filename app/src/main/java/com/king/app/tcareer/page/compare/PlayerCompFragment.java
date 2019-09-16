package com.king.app.tcareer.page.compare;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.MvvmFragment;
import com.king.app.tcareer.databinding.FragmentComparePlayerBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/9/12 14:45
 */
public class PlayerCompFragment extends MvvmFragment<FragmentComparePlayerBinding, PlayerCompViewModel> {

    private static final String ARG_PLAYER_ID = "player_id";
    private static final String ARG_LEVEL1 = "level1";
    private static final String ARG_LEVEL2 = "level2";

    private TwoLineAdapter twoLineAdapter;
    private SubTotalAdapter subTotalAdapter;

    public static PlayerCompFragment newInstance(long playerId) {
        PlayerCompFragment fragment = new PlayerCompFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_PLAYER_ID, playerId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected PlayerCompViewModel createViewModel() {
        return ViewModelProviders.of(this).get(PlayerCompViewModel.class);
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_compare_player;
    }

    @Override
    protected void onCreate(View view) {
        mBinding.setModel(mModel);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.rvContent.setLayoutManager(manager);
    }

    @Override
    protected void onCreateData() {

        mModel.twoLineList.observe(this, list -> {
            twoLineAdapter = new TwoLineAdapter();
            twoLineAdapter.setList(list);
            mBinding.rvContent.setAdapter(twoLineAdapter);
        });

        mModel.subTotalList.observe(this, list -> {
            subTotalAdapter = new SubTotalAdapter();
            subTotalAdapter.setList(list);
            mBinding.rvContent.setAdapter(subTotalAdapter);
        });

        mModel.loadPlayer(getArguments().getLong(ARG_PLAYER_ID));
        onArgumentsChanged();
    }

    public void setParams(String level1, String level2) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            setArguments(bundle);
        }
        bundle.putString(ARG_LEVEL1, level1);
        bundle.putString(ARG_LEVEL2, level2);
    }

    public void onArgumentsChanged() {
        mModel.loadContents(getArguments().getString(ARG_LEVEL1), getArguments().getString(ARG_LEVEL2));
    }
}
