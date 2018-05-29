package com.king.app.tcareer.page.player.atp;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.king.app.jactionbar.JActionbar;
import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.view.widget.SideBar;

import java.util.List;

import butterknife.BindView;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/18 9:23
 */
public class AtpManageActivity extends BaseMvpActivity<AtpManagePresenter> implements AtpManageView {

    public static final String RESP_ATP_ID = "resp_atp_id";

    public static final String EXTRA_SELECT = "extra_select";

    @BindView(R.id.actionbar)
    JActionbar actionbar;
    @BindView(R.id.sideBar)
    SideBar sideBar;
    @BindView(R.id.rv_items)
    RecyclerView rvItems;
    @BindView(R.id.tv_index_popup)
    TextView tvIndexPopup;

    private AtpManageAdapter adapter;

    private AtpEditor atpEditor;

    private boolean isEditing;

    private boolean isSelectMode;

    @Override
    protected int getContentView() {
        return R.layout.activity_atp_manage;
    }

    @Override
    protected void initView() {
        isSelectMode = getIntent().getBooleanExtra(EXTRA_SELECT, false);

        actionbar.setOnBackListener(() -> onBackPressed());
        actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_atp_add:
                    editPlayer(null);
                    break;
                case R.id.menu_atp_fetch:
                    showConfirmCancelMessage("是否重新从网络获取数据？"
                            , (dialogInterface, i) -> presenter.fetchData(), null);
                    break;
                case R.id.menu_atp_edit:
                    actionbar.showConfirmStatus(R.id.menu_atp_edit);
                    isEditing = true;
                    break;
                case R.id.menu_atp_delete:
                    isEditing = true;
                    actionbar.showConfirmStatus(R.id.menu_atp_delete);
                    adapter.setSelectionMode(true);
                    adapter.notifyDataSetChanged();
                    break;
            }
        });
        actionbar.setOnConfirmListener(new OnConfirmListener() {
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
                        actionbar.cancelConfirmStatus();
                        break;
                    case R.id.menu_atp_delete:
                        final List<PlayerAtpBean> list = adapter.getSelectedList();
                        if (list.size() > 0) {
                            showConfirmCancelMessage("确认删除？"
                                    , (dialog, which) -> {
                                        presenter.deleteData(list);
                                        isEditing = false;
                                        actionbar.cancelConfirmStatus();
                                        adapter.setSelectionMode(false);
                                        presenter.loadData();
                                    }, null);
                        }
                        else {
                            isEditing = false;
                            actionbar.cancelConfirmStatus();
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
                actionbar.cancelConfirmStatus();
                return false;
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvItems.setLayoutManager(manager);

        sideBar.setOnTouchingLetterChangedListener(s -> {
            int position = presenter.getIndexPosition(s);
            rvItems.scrollToPosition(position);
        });
        sideBar.setTextView(tvIndexPopup);
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
                presenter.loadData();
            }
        });
        atpEditor.show(getSupportFragmentManager(), "AtpEditor");
    }

    @Override
    protected AtpManagePresenter createPresenter() {
        return new AtpManagePresenter();
    }

    @Override
    protected void initData() {
        presenter.loadData();
    }

    @Override
    public SideBar getSideBar() {
        return sideBar;
    }

    @Override
    public void postShowPlayers(final List<PlayerAtpBean> list) {
        runOnUiThread(() -> {
            if (adapter == null) {
                adapter = new AtpManageAdapter();
                adapter.setList(list);
                adapter.setOnItemClickListener((position, data) -> {
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
                rvItems.setAdapter(adapter);
            }
            else {
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
