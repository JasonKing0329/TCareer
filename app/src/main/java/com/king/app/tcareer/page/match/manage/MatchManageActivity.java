package com.king.app.tcareer.page.match.manage;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.PopupMenu;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityMatchManageBinding;
import com.king.app.tcareer.model.bean.MatchImageBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.page.match.common.MatchCommonActivity;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.view.dialog.frame.FrameDialogFragment;

import java.util.List;

/**
 * 描述: match management view
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/30 14:09
 */
public class MatchManageActivity extends MvvmActivity<ActivityMatchManageBinding, MatchManageViewModel> {

    public static final String KEY_START_MODE = "key_start_mode";
    public static final int START_MODE_SELECT = 1;

    public static final String RESPONSE_MATCH_NAME_ID = "resp_match_name_id";

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
    protected MatchManageViewModel createViewModel() {
        return ViewModelProviders.of(this).get(MatchManageViewModel.class);
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
        mBinding.rvList.setLayoutManager(manager);
        mBinding.rvList.setItemAnimator(new DefaultItemAnimator());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mBinding.rvGrid.setLayoutManager(gridLayoutManager);
        mBinding.rvGrid.setItemAnimator(new DefaultItemAnimator());

        if (isGridMode) {
            mBinding.rvList.setVisibility(View.GONE);
            mBinding.rvGrid.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.rvList.setVisibility(View.VISIBLE);
            mBinding.rvGrid.setVisibility(View.GONE);
        }

    }

    private void initActionbar() {
        mBinding.actionbar.setTitle(getString(R.string.match_manage_title));
        if (isSelectMode) {
            mBinding.actionbar.inflateMenu(R.menu.match_manage_select);
        }
        else {
            mBinding.actionbar.inflateMenu(R.menu.match_manage);
        }
        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_manage_add:
                    showYesNoMessage("Only add match name from existed matches?"
                            , (dialogInterface, i) -> {
                                isOnlyAddName = true;
                                isEditMode = true;
                                mBinding.actionbar.showConfirmStatus(R.id.menu_manage_add);
                            }
                            , (dialogInterface, i) -> openMatchEditDialog(null));
                    break;
                case R.id.menu_manage_delete:
                    mBinding.actionbar.showConfirmStatus(menuId);
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
                    mBinding.actionbar.showConfirmStatus(menuId, true, "Cancel");
                    break;
                case R.id.menu_manage_view:
                    if (isGridMode) {
                        isGridMode = false;
                        mBinding.rvGrid.startAnimation(getDisappearAnim(mBinding.rvGrid));
                        mBinding.rvList.startAnimation(getAppearAnim(mBinding.rvList));
                        SettingProperty.setMatchManageGridMode(false);
                    }
                    else {
                        isGridMode = true;
                        mBinding.rvList.startAnimation(getDisappearAnim(mBinding.rvList));
                        mBinding.rvGrid.startAnimation(getAppearAnim(mBinding.rvGrid));
                        SettingProperty.setMatchManageGridMode(true);
                    }
                    refreshList();
                    break;
            }
        });
        mBinding.actionbar.setOnConfirmListener(actionId -> {
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
        });
        mBinding.actionbar.setOnCancelListener(actionId -> {
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
        });
        mBinding.actionbar.registerPopupMenu(R.id.menu_manage_sort);
        mBinding.actionbar.setPopupMenuProvider((iconMenuId, anchorView) -> {
            PopupMenu popupMenu = null;
            switch (iconMenuId) {
                case R.id.menu_manage_sort:
                    popupMenu = getSortPopup(anchorView);
                    break;
            }
            return popupMenu;
        });
        mBinding.actionbar.setOnSearchListener(words -> mModel.filterMatches(words));
    }

    @Override
    protected void initData() {
        mModel.matchesObserver.observe(this, list -> showMatches(list));
        mModel.loadMatches();
    }

    private void refreshList() {
        mModel.loadMatches();
    }

    public void showMatches(List<MatchImageBean> list) {
        if (isGridMode) {
            if (matchGridAdapter == null) {
                matchGridAdapter = new MatchGridAdapter();
                matchGridAdapter.setList(list);
                matchGridAdapter.setOnItemClickListener((view, position, data) -> onMatchItemClick(data.getBean()));
                matchGridAdapter.setFragmentManager(getSupportFragmentManager());
                mBinding.rvGrid.setAdapter(matchGridAdapter);
            }
            else {
                matchGridAdapter.setList(list);
                matchGridAdapter.notifyDataSetChanged();
            }
        }
        else {
            if (matchItemAdapter == null) {
                matchItemAdapter = new MatchItemAdapter();
                matchItemAdapter.setList(list);
                matchItemAdapter.setOnItemClickListener((view, position, data) -> onMatchItemClick(data.getBean()));
                matchItemAdapter.setFragmentManager(getSupportFragmentManager());
                mBinding.rvList.setAdapter(matchItemAdapter);
            }
            else {
                matchItemAdapter.setList(list);
                matchItemAdapter.notifyDataSetChanged();
            }
        }
    }

    private void onMatchItemClick(MatchNameBean bean) {
        if (isEditMode) {
            openMatchEditDialog(bean);
            // 只添加新名称，打开对话框后恢复到正常模式
            if (isOnlyAddName) {
                isOnlyAddName = false;
                isEditMode = false;
                mBinding.actionbar.cancelConfirmStatus();
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

    private void openMatchEditDialog(MatchNameBean bean) {
        MatchEditDialog content = new MatchEditDialog();
        content.setIsOnlyAddName(isOnlyAddName);
        content.setEditBean(bean);
        content.setOnMatchEditListener(new MatchEditDialog.OnMatchEditListener() {

            @Override
            public void onMatchAdded() {
                refreshList();
            }

            @Override
            public void onMatchUpdated(MatchNameBean editBean) {
                getCurrentAdapter().notifyItemChanged(editBean);
            }
        });
        FrameDialogFragment dialog = new FrameDialogFragment();
        dialog.setContentFragment(content);
        if (bean == null) {
            dialog.setTitle("New Match");
        }
        else {
            dialog.setTitle(bean.getName());
        }
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
            mModel.deleteMatch(list);
        }
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
                            mModel.sortMatch(SettingProperty.VALUE_SORT_MATCH_NAME);
                            break;
                        case R.id.menu_sort_week:
                            mModel.sortMatch(SettingProperty.VALUE_SORT_MATCH_WEEK);
                            break;
                        case R.id.menu_sort_level:
                            mModel.sortMatch(SettingProperty.VALUE_SORT_MATCH_LEVEL);
                            break;
                    }
                    return true;
                }
            });
        }
        return popSort;
    }

    @Override
    public void onBackPressed() {
        if (mBinding.actionbar != null && mBinding.actionbar.onBackPressed()) {
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
