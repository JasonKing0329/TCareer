package com.king.app.tcareer.page.player.atp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityAtpManageBinding;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/18 9:23
 */
public class AtpManageActivity extends MvvmActivity<ActivityAtpManageBinding, AtpManageViewModel> {

    public static final String RESP_ATP_ID = "resp_atp_id";

    public static final String EXTRA_SELECT = "extra_select";

    private AtpManageAdapter adapter;

    private AtpEditor atpEditor;

    private boolean isEditing;

    private boolean isSelectMode;

    @Override
    protected int getContentView() {
        return R.layout.activity_atp_manage;
    }

    @Override
    protected AtpManageViewModel createViewModel() {
        return ViewModelProviders.of(this).get(AtpManageViewModel.class);
    }

    @Override
    protected void initView() {
        isSelectMode = getIntent().getBooleanExtra(EXTRA_SELECT, false);

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_atp_add:
                    editPlayer(null);
                    break;
                case R.id.menu_atp_fetch:
                    showConfirmCancelMessage("是否重新从网络获取数据？"
                            , (dialogInterface, i) -> mModel.fetchData(), null);
                    break;
                case R.id.menu_atp_edit:
                    mBinding.actionbar.showConfirmStatus(R.id.menu_atp_edit);
                    isEditing = true;
                    break;
                case R.id.menu_atp_delete:
                    isEditing = true;
                    mBinding.actionbar.showConfirmStatus(R.id.menu_atp_delete);
                    adapter.setSelectionMode(true);
                    adapter.notifyDataSetChanged();
                    break;
            }
        });
        mBinding.actionbar.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public boolean disableInstantDismissConfirm() {
                return false;
            }

            @Override
            public boolean disableInstantDismissCancel() {
                return false;
            }

            @Override
            public boolean onConfirm(int actionId) {
                switch (actionId) {
                    case R.id.menu_atp_edit:
                        isEditing = false;
                        mBinding.actionbar.cancelConfirmStatus();
                        break;
                    case R.id.menu_atp_delete:
                        final List<PlayerAtpBean> list = adapter.getSelectedList();
                        if (list.size() > 0) {
                            showConfirmCancelMessage("确认删除？"
                                    , (dialog, which) -> {
                                        mModel.deleteData(list);
                                        isEditing = false;
                                        mBinding.actionbar.cancelConfirmStatus();
                                        adapter.setSelectionMode(false);
                                        mModel.loadData();
                                    }, null);
                        }
                        else {
                            isEditing = false;
                            mBinding.actionbar.cancelConfirmStatus();
                            adapter.setSelectionMode(false);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                }
                return false;
            }

            @Override
            public boolean onCancel(int actionId) {
                isEditing = false;
                adapter.setSelectionMode(false);
                adapter.notifyDataSetChanged();
                mBinding.actionbar.cancelConfirmStatus();
                return false;
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvItems.setLayoutManager(manager);

        mBinding.sideBar.setOnTouchingLetterChangedListener(s -> {
            int position = mModel.getIndexPosition(s);
            mBinding.rvItems.scrollToPosition(position);
        });
        mBinding.sideBar.setTextView(mBinding.tvIndexPopup);
    }

    private void editPlayer(String atpId) {
        atpEditor = new AtpEditor();
        atpEditor.setAtpId(atpId);
        atpEditor.setOnEditListener(new AtpEditor.OnEditListener() {
            @Override
            public void onUpdated(PlayerAtpBean bean) {

            }

            @Override
            public void onInserted(PlayerAtpBean bean) {
                mModel.loadData();
            }
        });
        atpEditor.show(getSupportFragmentManager(), "AtpEditor");
    }

    @Override
    protected void initData() {
        mModel.clearIndex.observe(this, clear -> mBinding.sideBar.clear());
        mModel.indexObserver.observe(this, index -> mBinding.sideBar.addIndex(index));
        mModel.playersObserver.observe(this, list -> postShowPlayers(list));
        mModel.loadData();
    }

    private void postShowPlayers(final List<PlayerAtpBean> list) {
        if (adapter == null) {
            adapter = new AtpManageAdapter();
            adapter.setList(list);
            adapter.setOnItemClickListener((view, position, data) -> {
                if (isEditing) {
                    editPlayer(data.getId());
                }
                else {
                    if (isSelectMode) {
                        Intent intent = new Intent();
                        intent.putExtra(RESP_ATP_ID, data.getId());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
            mBinding.rvItems.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }
}
