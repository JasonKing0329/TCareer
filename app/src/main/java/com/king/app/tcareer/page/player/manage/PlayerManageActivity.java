package com.king.app.tcareer.page.player.manage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

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
    @BindView(R.id.tv_sort)
    TextView tvSort;
    @BindView(R.id.iv_sidebar)
    ImageView ivSidebar;

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

        updateSortText();

        ivSidebar.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        ivSidebar.setOnClickListener(view -> ftRich.toggleSidebar());
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
                    if (ftRich.isExpandAll()) {
                        actionbar.updateMenuText(R.id.menu_manage_expand, "Expand all");
                        ftRich.setExpandAll(false);
                    }
                    else {
                        ftRich.setExpandAll(true);
                        actionbar.updateMenuText(R.id.menu_manage_expand, "Collapse all");
                    }
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

    @Override
    public void onSortFinished() {
        updateSortText();
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

    private void updateSortText() {
        switch (SettingProperty.getPlayerSortMode()) {
            case SettingProperty.VALUE_SORT_PLAYER_NAME:
                tvSort.setText(R.string.menu_sort_name);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_NAME_ENG:
                tvSort.setText(R.string.menu_sort_name_eng);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_COUNTRY:
                tvSort.setText(R.string.menu_sort_country);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_AGE:
                tvSort.setText(R.string.menu_sort_age);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION:
                tvSort.setText(R.string.menu_sort_constellation);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_RECORD:
                tvSort.setText(R.string.menu_sort_record);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_HEIGHT:
                tvSort.setText(R.string.menu_sort_height);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_WEIGHT:
                tvSort.setText(R.string.menu_sort_weight);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_HIGH:
                tvSort.setText(R.string.menu_sort_career_high);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_TITLES:
                tvSort.setText(R.string.menu_sort_career_titles);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_WIN:
                tvSort.setText(R.string.menu_sort_career_win);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_TURNEDPRO:
                tvSort.setText(R.string.menu_sort_turned_pro);
                break;
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_LAST_UPDATE:
                tvSort.setText(R.string.menu_sort_last_update);
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
        if (actionbar != null && actionbar.onBackPressed()) {
            return;
        }
        if (ftRich != null && ftRich.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

}
