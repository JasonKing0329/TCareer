package com.king.app.tcareer.page.player.manage;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.PopupMenu;

import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityPlayerManageBinding;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.atp.AtpManageActivity;
import com.king.app.tcareer.page.player.list.RichPlayerFilterDialog;
import com.king.app.tcareer.page.player.list.RichPlayerFragment;
import com.king.app.tcareer.page.player.list.RichPlayerHolder;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.view.dialog.AlertDialogFragment;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 11:49
 */
public class PlayerManageActivity extends MvvmActivity<ActivityPlayerManageBinding, PlayerManageViewModel> implements RichPlayerHolder {

    public static final String KEY_START_MODE = "key_start_mode";
    public static final String KEY_ONLY_USER = "only_user";
    public static final int START_MODE_SELECT = 1;

    public static final String RESPONSE_PLAYER_ID = "resp_player_id";
    public static final String RESPONSE_PLAYER_IS_USER = "resp_player_is_user";

    private PopupMenu popSort;

    private RichPlayerFragment ftRich;

    private boolean isSelectMode;

    @Override
    protected int getContentView() {
        return R.layout.activity_player_manage;
    }

    @Override
    protected PlayerManageViewModel createViewModel() {
        return ViewModelProviders.of(this).get(PlayerManageViewModel.class);
    }

    @Override
    protected void initView() {

        int mode = getIntent().getIntExtra(KEY_START_MODE, 0);
        if (mode == START_MODE_SELECT) {
            isSelectMode = true;
        }

        initActionbar();

        mBinding.tvUser.setOnClickListener(v -> chooseUser());

        ftRich = new RichPlayerFragment();
        ftRich.setSelectPlayerMode(isSelectMode);
        ftRich.setOnlyShowUser(isOnlyShowUser());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_ft, ftRich, "RichPlayerFragment")
                .commit();

        mBinding.ivSidebar.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        mBinding.ivSidebar.setOnClickListener(view -> ftRich.toggleSidebar());
    }

    private void initActionbar() {
        mBinding.actionbar.setTitle(getString(R.string.player_manage_title));
        if (isSelectMode) {
            mBinding.actionbar.inflateMenu(R.menu.player_manage_select);
        }
        else {
            mBinding.actionbar.inflateMenu(R.menu.player_manage);
        }
        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_manage_filter:
                    openFilterDialog();
                    break;
                case R.id.menu_manage_count:
                    showChartDialog();
                    break;
                case R.id.menu_manage_add:
                    addNewPlayer();
                    break;
                case R.id.menu_manage_delete:
                    mBinding.actionbar.showConfirmStatus(menuId);
                    ftRich.setDeleteMode(true);
                    break;
                case R.id.menu_manage_edit:
                    mBinding.actionbar.showConfirmStatus(menuId);
                    ftRich.setEditMode(true);
                    break;
                case R.id.menu_manage_expand:
                    ftRich.setExpandAll(true);
                    break;
                case R.id.menu_manage_collapse:
                    ftRich.setExpandAll(false);
                    break;
                case R.id.menu_manage_atp:
                    startActivity(new Intent().setClass(PlayerManageActivity.this, AtpManageActivity.class));
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
                    case R.id.menu_manage_delete:
                        ftRich.confirmDelete();
                        break;
                }
                return true;
            }

            @Override
            public boolean onCancel(int actionId) {
                switch (actionId) {
                    case R.id.menu_manage_delete:
                        ftRich.setDeleteMode(false);
                        break;
                    case R.id.menu_manage_edit:
                        ftRich.setEditMode(false);
                        break;
                }
                return true;
            }
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
        mBinding.actionbar.setOnSearchListener(words -> ftRich.filter(words));
    }

    private void openFilterDialog() {
        RichPlayerFilterDialog dialog = new RichPlayerFilterDialog();
        dialog.setOnFilterListener(bean -> ftRich.filterPlayer(bean));
        dialog.show(getSupportFragmentManager(), "RichPlayerFilterDialog");
    }

    private void chooseUser() {
        new AlertDialogFragment()
                .setItems(mModel.getUserSelector(), (dialog, which) -> onUserChanged(which))
                .show(getSupportFragmentManager(), "AlertDialogFragment");
    }

    private void onUserChanged(int position) {
        if (position == 0) {
            mBinding.tvUser.setText("All users");
        }
        else {
            mBinding.tvUser.setText(mModel.getUser(position).getNameEng());
        }
        ftRich.updateUser(mModel.getUser(position));
    }

    @Override
    protected void initData() {

    }

    private boolean isOnlyShowUser() {
        return getIntent().getBooleanExtra(KEY_ONLY_USER, false);
    }

    @Override
    public void onSelectPlayer(CompetitorBean bean) {
        if (isSelectMode) {
            Intent intent = new Intent();
            intent.putExtra(RESPONSE_PLAYER_ID, bean.getId());
            intent.putExtra(RESPONSE_PLAYER_IS_USER, bean instanceof User);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onSortFinished(int sortType) {
        updateSortText(sortType);
    }

    private void showChartDialog() {
        PlayerChartDialog dialog = new PlayerChartDialog();
        dialog.setPlayerList(ftRich.getPlayerList());
        dialog.show(getSupportFragmentManager(), "PlayerChartDialog");
    }

    private void addNewPlayer() {
        PlayerEditDialog dialog = new PlayerEditDialog();
        dialog.setOnPlayerEditListener(bean -> ftRich.reload());
        dialog.show(getSupportFragmentManager(), "PlayerEditDialog");
    }

    private void updateSortText(int sortType) {
        switch (sortType) {
            case SettingProperty.VALUE_SORT_PLAYER_NAME:
                mBinding.tvSort.setText(R.string.menu_sort_name);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_NAME_ENG:
                mBinding.tvSort.setText(R.string.menu_sort_name_eng);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_COUNTRY:
                mBinding.tvSort.setText(R.string.menu_sort_country);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_AGE:
                mBinding.tvSort.setText(R.string.menu_sort_age);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION:
                mBinding.tvSort.setText(R.string.menu_sort_constellation);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_RECORD:
                mBinding.tvSort.setText(R.string.menu_sort_record);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_HEIGHT:
                mBinding.tvSort.setText(R.string.menu_sort_height);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_WEIGHT:
                mBinding.tvSort.setText(R.string.menu_sort_weight);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_HIGH:
                mBinding.tvSort.setText(R.string.menu_sort_career_high);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_TITLES:
                mBinding.tvSort.setText(R.string.menu_sort_career_titles);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_WIN:
                mBinding.tvSort.setText(R.string.menu_sort_career_win);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_TURNEDPRO:
                mBinding.tvSort.setText(R.string.menu_sort_turned_pro);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_LAST_UPDATE:
                mBinding.tvSort.setText(R.string.menu_sort_last_update);
                break;
        }
    }

    private PopupMenu getSortPopup(View anchor) {
        if (popSort == null) {
            popSort = new PopupMenu(this, anchor);
            popSort.getMenuInflater().inflate(R.menu.sort_player, popSort.getMenu());
            popSort.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_sort_name:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_NAME);
                        break;
                    case R.id.menu_sort_name_eng:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_NAME_ENG);
                        break;
                    case R.id.menu_sort_country:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_COUNTRY);
                        break;
                    case R.id.menu_sort_age:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_AGE);
                        break;
                    case R.id.menu_sort_constellation:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION);
                        break;
                    case R.id.menu_sort_record:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_RECORD);
                        break;
                    case R.id.menu_sort_height:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_HEIGHT);
                        break;
                    case R.id.menu_sort_weight:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_WEIGHT);
                        break;
                    case R.id.menu_sort_career_high:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_CAREER_HIGH);
                        break;
                    case R.id.menu_sort_career_titles:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_CAREER_TITLES);
                        break;
                    case R.id.menu_sort_career_win:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_CAREER_WIN);
                        break;
                    case R.id.menu_sort_turned_pro:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_CAREER_TURNEDPRO);
                        break;
                    case R.id.menu_sort_last_update:
                        ftRich.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_CAREER_LAST_UPDATE);
                        break;
                }
                return false;
            });
        }
        return popSort;
    }

    @Override
    public void onBackPressed() {
        if (mBinding.actionbar != null && mBinding.actionbar.onBackPressed()) {
            return;
        }
        if (ftRich != null && ftRich.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void updateFirstIndex(String index) {
        mBinding.tvSortValue.setText(index);
    }
}
