package com.king.app.tcareer.page.match.manage;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

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

    private ImageView ivSort;
    private ViewGroup groupNormal;
    private ViewGroup groupConfirm;
    private PopupMenu popSort;

    // 选择模式
    private boolean isSelectMode;
    // 编辑模式
    private boolean isEditMode;
    // 删除模式
    private boolean isDeleteMode;
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
        ImageView backView = findViewById(R.id.view7_actionbar_back);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(actionbarListener);
        groupConfirm = findViewById(R.id.view7_actionbar_action_confirm);
        groupNormal = findViewById(R.id.view7_actionbar_action_normal);
        ivSort = findViewById(R.id.view7_actionbar_sort);
        groupNormal.setVisibility(View.VISIBLE);
        findViewById(R.id.view7_actionbar_edit_group).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.view7_actionbar_title)).setText(getString(R.string.match_manage_title));

        findViewById(R.id.view7_actionbar_add).setOnClickListener(actionbarListener);
        findViewById(R.id.view7_actionbar_edit).setOnClickListener(actionbarListener);
        findViewById(R.id.view7_actionbar_delete).setOnClickListener(actionbarListener);
        findViewById(R.id.view7_actionbar_done).setOnClickListener(actionbarListener);
        findViewById(R.id.view7_actionbar_close).setOnClickListener(actionbarListener);
        findViewById(R.id.view7_actionbar_mode).setOnClickListener(actionbarListener);
        ivSort.setOnClickListener(actionbarListener);
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
                    updateActionbarStatus(false);
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

    View.OnClickListener actionbarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.view7_actionbar_back:
                    finish();
                    break;
                case R.id.view7_actionbar_sort:
                    showSortPopup();
                    break;
                case R.id.view7_actionbar_add:
                    showYesNoMessage("Only add match name from existed matches?"
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    isOnlyAddName = true;
                                    isEditMode = true;
                                    updateActionbarStatus(true);
                                }
                            }
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    openMatchEditDialog(null);
                                }
                            });
                    break;
                case R.id.view7_actionbar_edit:
                    isEditMode = true;
                    updateActionbarStatus(true);
                    break;
                case R.id.view7_actionbar_delete:
                    isDeleteMode = true;
                    updateActionbarStatus(true);
                    if (matchItemAdapter != null) {
                        matchItemAdapter.setSelectMode(true);
                        matchItemAdapter.notifyDataSetChanged();
                    }
                    if (matchGridAdapter != null) {
                        matchGridAdapter.setSelectMode(true);
                        matchGridAdapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.view7_actionbar_done:
                    if (isEditMode) {

                    }
                    else if (isDeleteMode) {
                        deleteMatchItems();
                    }
                    updateActionbarStatus(false);
                    break;
                case R.id.view7_actionbar_close:
                    updateActionbarStatus(false);
                    break;
                case R.id.view7_actionbar_mode:
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
    };

    public void updateActionbarStatus(boolean editMode) {
        if (editMode) {
            groupConfirm.setVisibility(View.VISIBLE);
            groupNormal.setVisibility(View.GONE);
        }
        else {
            groupConfirm.setVisibility(View.GONE);
            groupNormal.setVisibility(View.VISIBLE);
            if (isDeleteMode) {
                if (matchItemAdapter != null) {
                    matchItemAdapter.setSelectMode(false);
                    matchItemAdapter.notifyDataSetChanged();
                }
                if (matchGridAdapter != null) {
                    matchGridAdapter.setSelectMode(false);
                    matchGridAdapter.notifyDataSetChanged();
                }
            }
            isEditMode = false;
            isDeleteMode = false;
            isOnlyAddName = false;
        }
    }

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

    private void showSortPopup() {
        if (popSort == null) {
            popSort = new PopupMenu(this, ivSort);
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
        popSort.show();
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
        if (isEditMode || isDeleteMode) {
            updateActionbarStatus(false);
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
