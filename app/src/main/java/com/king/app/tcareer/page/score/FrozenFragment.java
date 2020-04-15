package com.king.app.tcareer.page.score;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.databinding.FragmentFrozenScoreBinding;
import com.king.app.tcareer.model.db.entity.FrozenScore;
import com.king.app.tcareer.view.dialog.frame.FrameContentFragment;
import com.king.app.tcareer.view.dialog.frame.FrameDialogFragment;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/4/15 8:49
 */
public class FrozenFragment extends FrameContentFragment<FragmentFrozenScoreBinding, FrozenViewModel> {

    private FrozenItemAdapter adapter;
    private long mUserId;

    private OnDataChangedListener onDataChangedListener;

    public void setOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected FrozenViewModel createViewModel() {
        return ViewModelProviders.of(this).get(FrozenViewModel.class);
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_frozen_score;
    }

    @Override
    protected void onCreate(View view) {
        mBinding.rvList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mBinding.llAdd.setOnClickListener(v -> editScore(null));
    }

    @Override
    protected void onCreateData() {
        mModel.itemsObserver.observe(this, list -> showItems(list));
        mModel.loadScores(mUserId);
    }

    private void showItems(List<FrozenItem> list) {
        if (adapter == null) {
            adapter = new FrozenItemAdapter();
            adapter.setList(list);
            adapter.setOnItemClickListener((view, position, data) -> editScore(data.getBean()));
            adapter.setOnDeleteListener((position, bean) -> {
                showConfirmCancelMessage("Are you sure to delete it?", (dialog, which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        mModel.deleteScore(bean);
                        onDataChangedListener.onDataChanged();
                    }
                });
            });
            mBinding.rvList.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }

    private void editScore(FrozenScore item) {
        FrozenAddFragment content = new FrozenAddFragment();
        content.setBean(item);
        content.setOnFrozenScoreListener(bean -> {
            mModel.insertOrUpdateScore(bean);
            onDataChangedListener.onDataChanged();
        });
        FrameDialogFragment dialog = new FrameDialogFragment();
        dialog.setContentFragment(content);
        if (item == null) {
            dialog.setTitle("Add Score");
        }
        else {
            dialog.setTitle("Edit");
        }
        dialog.show(getChildFragmentManager(), "FrozenAddFragment");
    }

    public void setUserId(long userId) {
        mUserId = userId;
    }

    public interface OnDataChangedListener {
        void onDataChanged();
    }
}
