package com.king.app.tcareer.page.match.manage;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.king.app.jactionbar.JActionbar;
import com.king.app.jactionbar.OnBackListener;
import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.jactionbar.OnMenuItemListener;
import com.king.app.jactionbar.PopupMenuProvider;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.page.match.common.MatchCommonActivity;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.view.widget.SideBar;

import java.util.List;

import butterknife.BindView;

/**
 * 描述: match management view
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/30 14:09
 */
public class MatchManageActivity extends BaseMvpActivity<MatchManagePresenter> implements MatchManageView {

    public static final String KEY_START_MODE = "key_start_mode";
    public static final int START_MODE_SELECT = 1;

    public static final String RESPONSE_MATCH_NAME_ID = "resp_match_name_id";

    @BindView(R.id.actionbar)
    JActionbar actionbar;
    @BindView(R.id.rv_stagger)
    RecyclerView rvStagger;
    @BindView(R.id.rv_grid)
    RecyclerView rvGrid;
    @BindView(R.id.sidebar)
    SideBar sidebar;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.tv_index_popup)
    TextView tvIndexPopup;

    private PopupMenu popSort;

    // 选择模式
    private boolean isSelectMode;
    // 编辑模式
    private boolean isEditMode;
    // 只添加新name选择模式
    private boolean isOnlyAddName;

    // 网格or列表
    private boolean isGridMode;

    private MatchItemAdapter matchItemAdapter;
    private MatchGridAdapter matchGridAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_match_manage;
    }

    @Override
    protected void initView() {

        int mode = getIntent().getIntExtra(KEY_START_MODE, 0);
        if (mode == START_MODE_SELECT) {
            isSelectMode = true;
        }

        isGridMode = SettingProperty.isMatchManageGridMode();

        initActionbar();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvList.setLayoutManager(manager);
        rvList.setItemAnimator(new DefaultItemAnimator());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvGrid.setLayoutManager(gridLayoutManager);
        rvGrid.setItemAnimator(new DefaultItemAnimator());

        if (isGridMode) {
            rvList.setVisibility(View.GONE);
            rvGrid.setVisibility(View.VISIBLE);
        }
        else {
            rvList.setVisibility(View.VISIBLE);
            rvGrid.setVisibility(View.GONE);
        }

    }

    private void initActionbar() {
        actionbar.setTitle(getString(R.string.match_manage_title));
        if (isSelectMode) {
            actionbar.inflateMenu(R.menu.match_manage_select);
        }
        else {
            actionbar.inflateMenu(R.menu.match_manage);
        }
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
                    case R.id.menu_manage_add:
                        showYesNoMessage("Only add match name from existed matches?"
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        isOnlyAddName = true;
                                        isEditMode = true;
                                        actionbar.showConfirmStatus(R.id.menu_manage_add);
                                    }
                                }
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        openMatchEditDialog(null);
                                    }
                                });
                        break;
                    case R.id.menu_manage_delete:
                        actionbar.showConfirmStatus(menuId);
                        if (matchItemAdapter != null) {
                            matchItemAdapter.setSelectMode(true);
                            matchItemAdapter.notifyDataSetChanged();
                        }
                        if (matchGridAdapter != null) {
                            matchGridAdapter.setSelectMode(true);
                            matchGridAdapter.notifyDataSetChanged();
                        }
                        break;
                    case R.id.menu_manage_edit:
                        isEditMode = true;
                        actionbar.showConfirmStatus(menuId);
                        break;
                    case R.id.menu_manage_view:
                        if (isGridMode) {
                            isGridMode = false;
                            rvGrid.startAnimation(getDisappearAnim(rvGrid));
                            rvList.startAnimation(getAppearAnim(rvList));
                            SettingProperty.setMatchManageGridMode(false);
                        }
                        else {
                            isGridMode = true;
                            rvList.startAnimation(getDisappearAnim(rvList));
                            rvGrid.startAnimation(getAppearAnim(rvGrid));
                            SettingProperty.setMatchManageGridMode(true);
                        }
                        refreshList();
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
                    case R.id.menu_manage_delete:
                        deleteMatchItems();
                        if (matchItemAdapter != null) {
                            matchItemAdapter.setSelectMode(false);
                            matchItemAdapter.notifyDataSetChanged();
                        }
                        if (matchGridAdapter != null) {
                            matchGridAdapter.setSelectMode(false);
                            matchGridAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                isEditMode = false;
                isOnlyAddName = false;
                return true;
            }

            @Override
            public boolean onCancel(int actionId) {
                switch (actionId) {
                    case R.id.menu_manage_delete:
                        if (matchItemAdapter != null) {
                            matchItemAdapter.setSelectMode(false);
                            matchItemAdapter.notifyDataSetChanged();
                        }
                        if (matchGridAdapter != null) {
                            matchGridAdapter.setSelectMode(false);
                            matchGridAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                isEditMode = false;
                isOnlyAddName = false;
                return true;
            }
        });
        actionbar.registerPopupMenu(R.id.menu_manage_sort);
        actionbar.setPopupMenuProvider(new PopupMenuProvider() {
            @Override
            public PopupMenu getPopupMenu(int iconMenuId, View anchorView) {
                PopupMenu popupMenu = null;
                switch (iconMenuId) {
                    case R.id.menu_manage_sort:
                        popupMenu = getSortPopup(anchorView);
                        break;
                }
                return popupMenu;
            }
        });
    }

    @Override
    protected MatchManagePresenter createPresenter() {
        return new MatchManagePresenter();
    }

    @Override
    protected void initData() {
        presenter.loadMatches();
    }

    private void refreshList() {
        presenter.loadMatches();
    }

    @Override
    public void showMatches(List<MatchNameBean> list) {
        if (isGridMode) {
            if (matchGridAdapter == null) {
                matchGridAdapter = new MatchGridAdapter(list);
                matchGridAdapter.setOnMatchItemClickListener(onMatchItemClickListener);
                matchGridAdapter.setFragmentManager(getSupportFragmentManager());
                rvGrid.setAdapter(matchGridAdapter);
            }
            else {
                matchGridAdapter.setList(list);
                matchGridAdapter.notifyDataSetChanged();
            }
        }
        else {
            if (matchItemAdapter == null) {
                matchItemAdapter = new MatchItemAdapter(list);
                matchItemAdapter.setOnMatchItemClickListener(onMatchItemClickListener);
                matchItemAdapter.setFragmentManager(getSupportFragmentManager());
                rvList.setAdapter(matchItemAdapter);
            }
            else {
                matchItemAdapter.setList(list);
                matchItemAdapter.notifyDataSetChanged();
            }
        }
    }

    MatchManageBaseAdapter.OnMatchItemClickListener onMatchItemClickListener = new MatchManageBaseAdapter.OnMatchItemClickListener() {
        @Override
        public void onMatchItemClick(MatchNameBean bean) {
            if (isEditMode) {
                openMatchEditDialog(bean);
                // 只添加新名称，打开对话框后恢复到正常模式
                if (isOnlyAddName) {
                    isOnlyAddName = false;
                    isEditMode = false;
                    actionbar.cancelConfirmStatus();
                }
            }
            else {
                if (isSelectMode) {
                    Intent intent = new Intent();
                    intent.putExtra(RESPONSE_MATCH_NAME_ID, bean.getId());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Intent intent = new Intent().setClass(MatchManageActivity.this, MatchCommonActivity.class);
                    intent.putExtra(MatchCommonActivity.KEY_MATCH, bean.getId());
                    startActivity(intent);
                }
            }
        }
    };

    private void openMatchEditDialog(MatchNameBean bean) {
        MatchEditDialog dialog = new MatchEditDialog();
        dialog.setOnlyAddName(isOnlyAddName);
        dialog.setEditBean(bean);
        dialog.setOnMatchEditListener(new MatchEditDialog.OnMatchEditListener() {

            @Override
            public void onMatchAdded() {
                refreshList();
            }

            @Override
            public void onMatchUpdated(MatchNameBean editBean) {
                getCurrentAdapter().notifyItemChanged(editBean);
            }
        });
        dialog.show(getSupportFragmentManager(), "MatchEditDialog");
    }

    private MatchManageBaseAdapter getCurrentAdapter() {
        if (isGridMode) {
            return matchGridAdapter;
        }
        else {
            return matchItemAdapter;
        }
    }

    private void deleteMatchItems() {
        List<MatchNameBean> list = getCurrentAdapter().getSelectedList();
        if (list != null) {
            presenter.deleteMatch(list);
        }
    }

    @Override
    public void deleteSuccess() {
        refreshList();
    }

    private PopupMenu getSortPopup(View anchor) {
        if (popSort == null) {
            popSort = new PopupMenu(this, anchor);
            popSort.getMenuInflater().inflate(R.menu.sort_match, popSort.getMenu());
            popSort.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_sort_name:
                            presenter.sortMatch(SettingProperty.VALUE_SORT_MATCH_NAME);
                            break;
                        case R.id.menu_sort_week:
                            presenter.sortMatch(SettingProperty.VALUE_SORT_MATCH_WEEK);
                            break;
                        case R.id.menu_sort_level:
                            presenter.sortMatch(SettingProperty.VALUE_SORT_MATCH_LEVEL);
                            break;
                    }
                    return true;
                }
            });
        }
        return popSort;
    }

    @Override
    public void sortFinished(List<MatchNameBean> matchList) {
        getCurrentAdapter().setList(matchList);
        getCurrentAdapter().notifyDataSetChanged();
        if (isGridMode) {
            rvGrid.scrollToPosition(0);
        }
        else {
            rvList.scrollToPosition(0);
        }
    }

    @Override
    public void onBackPressed() {
        if (actionbar != null && actionbar.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    public Animation getDisappearAnim(final View view) {
        AlphaAnimation anim = new AlphaAnimation(1, 0);
        anim.setDuration(500);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return anim;
    }

    public Animation getAppearAnim(final View view) {
        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(500);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return anim;
    }
}
