package com.king.app.tcareer.page.player.page;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.MvvmFragment;
import com.king.app.tcareer.databinding.FragmentPlayerPageBinding;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.record.page.RecordPageActivity;
import com.king.app.tcareer.utils.DebugLog;

import java.util.List;

/**
 * 描述: fragment to present records of competitor
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 16:05
 */
public class PageFragment extends MvvmFragment<FragmentPlayerPageBinding, SubPageViewModel> {

    protected static final String KEY_USER_ID = "key_user_id";
    protected static final String KEY_COURT = "key_court";
    protected static final String KEY_LEVEL = "key_level";
    protected static final String KEY_YEAR = "key_year";

    private PageRecordAdapter cardAdapter;
    private FullRecordAdapter fullAdapter;

    private int mFirstPosition;

    private boolean initialYearTitle;

    public static PageFragment newInstance(long userId, String court, String level, String year) {
        DebugLog.e("newInstance userId=" + userId + ", court=" + court + ", level=" + level + ", year=" + year);
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putLong(KEY_USER_ID, userId);
        args.putString(KEY_COURT, court);
        args.putString(KEY_LEVEL, level);
        args.putString(KEY_YEAR, year);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_player_page;
    }

    @Override
    protected SubPageViewModel createViewModel() {
        return ViewModelProviders.of(this).get(SubPageViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvRecords.setLayoutManager(manager);
    }

    private PageViewModel getMainViewModel() {
        return ViewModelProviders.of(getActivity()).get(PageViewModel.class);
    }

    @Override
    protected void onCreateData() {
        mModel.setCompetitor(getMainViewModel().getCompetitor());

        long userId = getArguments().getLong(KEY_USER_ID);
        String court = getArguments().getString(KEY_COURT);
        String level = getArguments().getString(KEY_LEVEL);
        String year = getArguments().getString(KEY_YEAR);
        DebugLog.e("userId=" + userId + ", court=" + court + ", level=" + level + ", year=" + year);

        mModel.listObserver.observe(this, list -> onDataLoaded(list, mModel.getViewType()));
        mModel.createRecords(userId, court, level, year);
    }

    private void refreshYear(int first) {
        mFirstPosition = first;
        mBinding.tvYear.setText(mModel.getYearTitle(fullAdapter.getYear(first)));
    }

    public void onDataLoaded(List<Object> list, int viewType) {
        if (viewType == SubPageViewModel.TYPE_PURE) {
            mBinding.tvYear.setVisibility(View.VISIBLE);
            mBinding.rvRecords.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int first = ((LinearLayoutManager) mBinding.rvRecords.getLayoutManager()).findFirstVisibleItemPosition();
                    if (first != mFirstPosition) {
                        refreshYear(first);
                    }
                }
            });

            fullAdapter = new FullRecordAdapter();
            fullAdapter.setUser(mModel.getUser());
            fullAdapter.setList(list);
            fullAdapter.setOnItemClickListener((view, position, data) -> {
                FullRecordBean bean = (FullRecordBean) data;
                showRecordPage(view, bean.record);
            });
            mBinding.rvRecords.setAdapter(fullAdapter);
        }
        else {
            mBinding.tvYear.setVisibility(View.GONE);
            cardAdapter = new PageRecordAdapter();
            cardAdapter.setUser(mModel.getUser());
            cardAdapter.setList(list);
            cardAdapter.setOnItemClickListener((view, position, data) -> showRecordPage(view, data));
            mBinding.rvRecords.setAdapter(cardAdapter);
        }
    }

    private void showRecordPage(View view, Record record) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), RecordPageActivity.class);
        intent.putExtra(RecordPageActivity.KEY_RECORD_ID, record.getId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity()
                , Pair.create(view.findViewById(R.id.iv_match),getString(R.string.anim_match_page_head)));
        startActivity(intent, transitionActivityOptions.toBundle());
    }
}
