package com.king.app.tcareer.page.player.manage;

import android.content.Intent;
import android.view.View;
import android.widget.PopupMenu;

import com.king.app.jactionbar.JActionbar;
import com.king.app.jactionbar.OnConfirmListener;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.list.RichPlayerFragment;
import com.king.app.tcareer.page.player.list.RichPlayerHolder;
import com.king.app.tcareer.page.setting.SettingProperty;

import butterknife.BindView;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 11:49
 */
public class PlayerManageActivity extends BaseMvpActivity<PlayerManagePresenter> implements PlayerManageView, RichPlayerHolder {

    public static final String KEY_START_MODE = "key_start_mode";
    public static final int START_MODE_SELECT = 1;

    public static final String RESPONSE_PLAYER_ID = "resp_player_id";
    public static final String RESPONSE_PLAYER_IS_USER = "resp_player_is_user";

    @BindView(R.id.actionbar)
    JActionbar actionbar;

    private PopupMenu popSort;

    private RichPlayerFragment ftRich;

    private boolean isSelectMode;

    @Override
    protected int getContentView() {
        return R.layout.activity_player_manage;
    }

    @Override
    protected void initView() {

        int mode = getIntent().getIntExtra(KEY_START_MODE, 0);
        if (mode == START_MODE_SELECT) {
            isSelectMode = true;
        }

        initActionbar();

        ftRich = new RichPlayerFragment();
        ftRich.setSelectPlayerMode(isSelectMode);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.group_ft, ftRich, "RichPlayerFragment")
                .commit();
    }

    private void initActionbar() {
        actionbar.setTitle(getString(R.string.player_manage_title));
        if (isSelectMode) {
            actionbar.inflateMenu(R.menu.player_manage_select);
        }
        else {
            actionbar.inflateMenu(R.menu.player_manage);
        }
        actionbar.setOnBackListener(() -> onBackPressed());
        actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_manage_count:
                    showChartDialog();
                    break;
                case R.id.menu_manage_add:
                    addNewPlayer();
                    break;
                case R.id.menu_manage_delete:
                    actionbar.showConfirmStatus(menuId);
                    ftRich.setDeleteMode(true);
                    break;
                case R.id.menu_manage_edit:
                    actionbar.showConfirmStatus(menuId);
                    ftRich.setEditMode(true);
                    break;
                case R.id.menu_manage_expand:
                    ftRich.toggleExpandStatus();
                    break;
                case R.id.menu_manage_fetch:
                    showConfirmCancelMessage("是否重新从网络获取数据？"
                            , (dialogInterface, i) -> presenter.fetchData(), null);
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
        actionbar.registerPopupMenu(R.id.menu_manage_sort);
        actionbar.setPopupMenuProvider((iconMenuId, anchorView) -> {
            PopupMenu popupMenu = null;
            switch (iconMenuId) {
                case R.id.menu_manage_sort:
                    popupMenu = getSortPopup(anchorView);
                    break;
            }
            return popupMenu;
        });
        actionbar.setOnSearchListener(words -> ftRich.filter(words));
    }

    @Override
    protected PlayerManagePresenter createPresenter() {
        return new PlayerManagePresenter();
    }

    @Override
    protected void initData() {

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
        if (actionbar != null && actionbar.onBackPressed()) {
            return;
        }
        if (ftRich != null && ftRich.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

}
