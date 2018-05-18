package com.king.app.tcareer.page.player.atp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.king.app.jactionbar.JActionbar;
import com.king.app.jactionbar.OnBackListener;
import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.jactionbar.OnMenuItemListener;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;
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

    @Override
    protected int getContentView() {
        return R.layout.activity_atp_manage;
    }

    @Override
    protected void initView() {
        actionbar.setOnBackListener(new OnBackListener() {
            @Override
            public void onBack() {
                onBackPressed();
            }
        });
        actionbar.setOnMenuItemListener(new OnMenuItemListener() {
            @Override
            public void onMenuItemSelected(int menuId) {
                switch (menuId) {
                    case R.id.menu_atp_add:
                        editPlayer(null);
                        break;
                    case R.id.menu_atp_fetch:

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
                                    , new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            presenter.deleteData(list);
                                            isEditing = false;
                                            actionbar.cancelConfirmStatus();
                                            adapter.setSelectionMode(false);
                                            presenter.loadData();
                                        }
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

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = presenter.getIndexPosition(s);
                rvItems.scrollToPosition(position);
            }
        });
        sideBar.setTextView(tvIndexPopup);

        sideBar.post(new Runnable() {
            @Override
            public void run() {
                sideBar.invalidate();
            }
        });
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    adapter = new AtpManageAdapter();
                    adapter.setList(list);
                    adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<PlayerAtpBean>() {
                        @Override
                        public void onClickItem(int position, PlayerAtpBean data) {
                            if (isEditing) {
                                editPlayer(data.getId());
                            }
                            else {
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
            }
        });
    }
}
