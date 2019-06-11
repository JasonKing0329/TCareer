package com.king.app.tcareer.page.record.editor;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.base.mvvm.MvvmFragment;
import com.king.app.tcareer.databinding.FragmentEditorMatchBinding;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.page.match.manage.MatchManageActivity;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/13 13:22
 */
public class MatchFragment extends MvvmFragment<FragmentEditorMatchBinding, BaseViewModel> {

    private final int REQUEST_CHANGE_MATCH = 101;

    private SpinnerListener spinnerListener;

    private RecentMatchAdapter recentMatchAdapter;

    private EditorViewModel parentViewModel;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_editor_match;
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void onCreate(View view) {
        parentViewModel = ViewModelProviders.of(getActivity()).get(EditorViewModel.class);
        mBinding.setModel(parentViewModel);
        
        spinnerListener = new SpinnerListener();
        ArrayAdapter<String> spinnerAdapter;

        spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, parentViewModel.getYearArrays());
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_item);
        mBinding.spYear.setAdapter(spinnerAdapter);
        mBinding.spYear.setOnItemSelectedListener(spinnerListener);
        spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, parentViewModel.getRoundArrays());
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spRound.setAdapter(spinnerAdapter);
        mBinding.spRound.setOnItemSelectedListener(spinnerListener);

        mBinding.rvRecentMatches.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        mBinding.ivChange.setOnClickListener(v -> selectMatch());
        mBinding.llScore.setOnClickListener(v -> {
            if (parentViewModel.getCompetitor() == null) {
                showMessageShort("还没有选择对手");
                return;
            }
            RecordScoreDialog recordScoreDialog = new RecordScoreDialog();
            recordScoreDialog.setCompetitor(parentViewModel.getCompetitor());
            recordScoreDialog.setUser(parentViewModel.getUser());
            recordScoreDialog.setRecord(parentViewModel.getRecord());
            recordScoreDialog.setScoreList(parentViewModel.getScoreList());
            recordScoreDialog.setOnScoreListener((scoreList, retireFlag, winnerFlag) -> {
                parentViewModel.updateScore(scoreList, retireFlag, winnerFlag);
            });
            recordScoreDialog.show(getChildFragmentManager(), "RecordScoreDialog");
        });
    }

    @Override
    protected void onCreateData() {

        parentViewModel.matchYearSelection.observe(this, selection -> mBinding.spYear.setSelection(selection));
        parentViewModel.matchRoundSelection.observe(this, selection -> mBinding.spRound.setSelection(selection));
        parentViewModel.recentMatchesObserver.observe(this, list -> showRecentMatches(list));
        parentViewModel.initMatchPage();

        // 新增record显示最近操作的赛事
        if (parentViewModel.isEditMode()) {
            mBinding.rvRecentMatches.setVisibility(View.GONE);
        }
        else {
            parentViewModel.loadRecentMatches();
        }
    }

    /**
     * 已添加完一个记录后继续添加保留上次添加的赛事信息，清空winner和score
     */
    public void reset() {
        mBinding.tvWinner.setText("");
        mBinding.tvScore.setText("");
        mBinding.llWinner.setVisibility(View.INVISIBLE);
    }

    private void showRecentMatches(List<MatchNameBean> matches) {
        if (recentMatchAdapter == null) {
            recentMatchAdapter = new RecentMatchAdapter();
            recentMatchAdapter.setList(matches);
            recentMatchAdapter.setOnItemClickListener((view, position, data) -> parentViewModel.updateMatchNameBean(data));
            mBinding.rvRecentMatches.setAdapter(recentMatchAdapter);
        }
        else {
            recentMatchAdapter.setList(matches);
            recentMatchAdapter.notifyDataSetChanged();
        }
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (parent == mBinding.spYear) {
                parentViewModel.setCurYear(position);
            } else if (parent == mBinding.spRound) {
                parentViewModel.setCurRound(position);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    private void selectMatch() {
        Intent intent = new Intent().setClass(getContext(), MatchManageActivity.class);
        intent.putExtra(MatchManageActivity.KEY_START_MODE, MatchManageActivity.START_MODE_SELECT);
        startActivityForResult(intent, REQUEST_CHANGE_MATCH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHANGE_MATCH) {
            if (resultCode == Activity.RESULT_OK) {
                long matchId = data.getLongExtra(MatchManageActivity.RESPONSE_MATCH_NAME_ID, -1);
                parentViewModel.queryMatch(matchId);
            }
        }
    }
}
