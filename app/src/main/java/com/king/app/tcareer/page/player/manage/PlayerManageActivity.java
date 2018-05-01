package com.king.app.tcareer.page.player.manage;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.king.app.jactionbar.OnSearchListener;
import com.king.app.jactionbar.PopupMenuProvider;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.widget.SideBar;

import java.util.List;

import butterknife.BindView;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 11:49
 */

public class PlayerManageActivity extends BaseMvpActivity<PlayerManagePresenter> implements PlayerManageView {

    public static final String KEY_START_MODE = "key_start_mode";
    public static final int START_MODE_SELECT = 1;

    public static final String RESPONSE_PLAYER_ID = "resp_player_id";
    public static final String RESPONSE_PLAYER_IS_USER = "resp_player_is_user";

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

    private boolean isCardMode;

    private boolean isSelectMode;

    private boolean isEditMode;

    private PlayerItemAdapter playerItemAdapter;
    private PlayerStaggerAdapter playerStaggerAdapter;

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

        initVerticalList();
        initStaggerList();
        isCardMode = SettingProperty.isPlayerManageCardMode();
        if (isCardMode) {
            rvList.setVisibility(View.GONE);
            rvStagger.setVisibility(View.VISIBLE);
        }
        else {
            rvList.setVisibility(View.VISIBLE);
            rvStagger.setVisibility(View.GONE);
        }

        sidebar.setVisibility(View.VISIBLE);
        sidebar.setOnTouchingLetterChangedListener(letterListener);
        sidebar.setTextView(tvIndexPopup);

        initActionbar();
    }

    private void initActionbar() {
        actionbar.setTitle(getString(R.string.player_manage_title));
        if (isSelectMode) {
            actionbar.inflateMenu(R.menu.player_manage_select);
        }
        else {
            actionbar.inflateMenu(R.menu.player_manage);
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
                    case R.id.menu_manage_count:
                        showChartDialog();
                        break;
                    case R.id.menu_manage_add:
                        openEditDialog(null);
                        break;
                    case R.id.menu_manage_delete:
                        actionbar.showConfirmStatus(menuId);
                        if (playerItemAdapter != null) {
                            playerItemAdapter.setSelectMode(true);
                            playerItemAdapter.notifyDataSetChanged();
                        }
                        if (playerStaggerAdapter != null) {
                            playerStaggerAdapter.setSelectMode(true);
                            playerStaggerAdapter.notifyDataSetChanged();
                        }
                        break;
                    case R.id.menu_manage_edit:
                        isEditMode = true;
                        actionbar.showConfirmStatus(menuId);
                        break;
                    case R.id.menu_manage_view:
                        if (isWaterfall()) {
                            isCardMode = false;
                            rvStagger.startAnimation(getDisappearAnim(rvStagger));
                            rvList.startAnimation(getAppearAnim(rvList));
                            SettingProperty.setPlayerManageCardMode(false);
                        }
                        else {
                            isCardMode = true;
                            rvList.startAnimation(getDisappearAnim(rvList));
                            rvStagger.startAnimation(getAppearAnim(rvStagger));
                            SettingProperty.setPlayerManageCardMode(true);
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
                        deletePlayerItems();
                        if (playerItemAdapter != null) {
                            playerItemAdapter.setSelectMode(false);
                            playerItemAdapter.notifyDataSetChanged();
                        }
                        if (playerStaggerAdapter != null) {
                            playerStaggerAdapter.setSelectMode(false);
                            playerStaggerAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                isEditMode = false;
                return true;
            }

            @Override
            public boolean onCancel(int actionId) {
                switch (actionId) {
                    case R.id.menu_manage_delete:
                        if (playerItemAdapter != null) {
                            playerItemAdapter.setSelectMode(false);
                            playerItemAdapter.notifyDataSetChanged();
                        }
                        if (playerStaggerAdapter != null) {
                            playerStaggerAdapter.setSelectMode(false);
                            playerStaggerAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                isEditMode = false;
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
        actionbar.setOnSearchListener(new OnSearchListener() {
            @Override
            public void onSearchWordsChanged(String words) {
                if (playerItemAdapter != null) {
                    playerItemAdapter.filter(words);
                }
                if (playerStaggerAdapter != null) {
                    playerStaggerAdapter.filter(words);
                }
            }
        });
    }

    private void initVerticalList() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvList.setLayoutManager(manager);
        rvList.setItemAnimator(new DefaultItemAnimator());
    }

    private void initStaggerList() {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvStagger.setLayoutManager(manager);
    }

    @Override
    protected PlayerManagePresenter createPresenter() {
        return new PlayerManagePresenter();
    }

    @Override
    protected void initData() {
        presenter.loadPlayers();
    }

    private void refreshList () {
        presenter.loadPlayers();
    }

    @Override
    public void showPlayers(List<PlayerViewBean> list) {
        if (isWaterfall()) {
            if (playerStaggerAdapter == null) {
                playerStaggerAdapter = new PlayerStaggerAdapter(list, ScreenUtils.getScreenWidth(this) / 2 - ScreenUtils.dp2px(10) * 2);
                playerStaggerAdapter.setOnPlayerItemClickListener(playerItemClickListener);
                playerStaggerAdapter.setFragmentManager(getSupportFragmentManager());
                rvStagger.setAdapter(playerStaggerAdapter);
            }
            else {
                playerStaggerAdapter.setList(list);
                playerStaggerAdapter.notifyDataSetChanged();
            }
        }
        else {
            if (playerItemAdapter == null) {
                playerItemAdapter = new PlayerItemAdapter(list);
                playerItemAdapter.setOnPlayerItemClickListener(playerItemClickListener);
                playerItemAdapter.setFragmentManager(getSupportFragmentManager());
                rvList.setAdapter(playerItemAdapter);
            }
            else {
                playerItemAdapter.setList(list);
                playerItemAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void clearSideBar() {
        sidebar.clear();
    }

    @Override
    public void addSideBarIndex(String index) {
        sidebar.addIndex(index);
    }

    @Override
    public void showSideBar(boolean show) {
        if (show) {
            sidebar.setVisibility(View.VISIBLE);
            sidebar.invalidate();
        }
        else {
            sidebar.setVisibility(View.GONE);
        }
    }

    public boolean isWaterfall() {
        return isCardMode;
    }

    private PlayerManageBaseAdapter.OnPlayerItemClickListener playerItemClickListener = new PlayerManageBaseAdapter.OnPlayerItemClickListener() {
        @Override
        public void onPlayerItemClick(PlayerViewBean bean) {
            if (isEditMode) {
                openEditDialog(bean);
            }
            else {
                if (isSelectMode) {
                    Intent intent = new Intent();
                    if (bean.getData() instanceof User) {
                        intent.putExtra(RESPONSE_PLAYER_ID, ((User) bean.getData()).getId());
                        intent.putExtra(RESPONSE_PLAYER_IS_USER, true);
                    }
                    else {
                        intent.putExtra(RESPONSE_PLAYER_ID, ((PlayerBean) bean.getData()).getId());
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Intent intent = new Intent().setClass(PlayerManageActivity.this, PlayerPageActivity.class);
                    if (bean.getData() instanceof User) {
                        intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_IS_USER, true);
                        intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, ((User) bean.getData()).getId());
                    }
                    else {
                        intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, ((PlayerBean) bean.getData()).getId());
                    }
                    startActivity(intent);
                }
            }
        }
    };

    private PlayerManageBaseAdapter getAdapter() {
        if (isWaterfall()) {
            return playerStaggerAdapter;
        }
        else {
            return playerItemAdapter;
        }
    }

    private void deletePlayerItems() {
        List<PlayerViewBean> list = getAdapter().getSelectedList();
        if (list != null) {
            presenter.deletePlayer(list);
        }
    }

    @Override
    public void deleteSuccess() {
        refreshList();
    }

    private void showChartDialog() {
        PlayerChartDialog dialog = new PlayerChartDialog();
        dialog.setPlayerList(presenter.getPlayerList());
        dialog.show(getSupportFragmentManager(), "PlayerChartDialog");
    }

    private void openEditDialog(PlayerViewBean playerBean) {
        PlayerEditDialog dialog = new PlayerEditDialog();
        if (playerBean != null) {
            if (playerBean.getData() instanceof User) {
                dialog.setUser((User) playerBean.getData());
            }
            else {
                dialog.setPlayerBean((PlayerBean) playerBean.getData());
            }
        }
        dialog.setOnPlayerEditListener(new PlayerEditDialog.OnPlayerEditListener() {
            @Override
            public void onPlayerAdded() {
                refreshList();
            }

            @Override
            public void onPlayerUpdated(PlayerBean bean) {
                getAdapter().notifyPlayerChanged(bean.getId());
            }

            @Override
            public void onUserUpdated(User user) {
                getAdapter().notifyUserChanged(user.getId());
            }
        });
        dialog.show(getSupportFragmentManager(), "PlayerEditDialog");
    }

    private PopupMenu getSortPopup(View anchor) {
        if (popSort == null) {
            popSort = new PopupMenu(this, anchor);
            popSort.getMenuInflater().inflate(R.menu.sort_player, popSort.getMenu());
            popSort.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_sort_name:
                            presenter.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_NAME);
                            break;
                        case R.id.menu_sort_name_eng:
                            presenter.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_NAME_ENG);
                            break;
                        case R.id.menu_sort_country:
                            presenter.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_COUNTRY);
                            break;
                        case R.id.menu_sort_age:
                            presenter.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_AGE);
                            break;
                        case R.id.menu_sort_constellation:
                            presenter.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION);
                            break;
                        case R.id.menu_sort_record:
                            presenter.sortPlayer(SettingProperty.VALUE_SORT_PLAYER_RECORD);
                            break;
                    }
                    return false;
                }
            });
        }
        return popSort;
    }

    @Override
    public void sortFinished(List<PlayerViewBean> list) {
        getAdapter().setList(list);
        getAdapter().notifyDataSetChanged();
        if (isWaterfall()) {
            rvStagger.scrollToPosition(0);
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

    private SideBar.OnTouchingLetterChangedListener letterListener = new SideBar.OnTouchingLetterChangedListener() {
        @Override
        public void onTouchingLetterChanged(String s) {
            int selection = presenter.getLetterPosition(s);
            if (isWaterfall()) {
                rvStagger.scrollToPosition(selection);
            }
            else {
                rvList.scrollToPosition(selection);
            }
        }
    };

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
