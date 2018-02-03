package com.king.app.tcareer.page.player.manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.common.PlayerCommonActivity;
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
    private ImageView ivChart;
    private ViewGroup groupNormal;
    private ViewGroup groupConfirm;
    private PopupMenu popSort;

    // 选择模式
    private boolean isSelectMode;
    // 编辑模式
    private boolean isEditMode;
    // 删除模式
    private boolean isDeleteMode;

    private boolean isCardMode;

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
        ((TextView) findViewById(R.id.view7_actionbar_title)).setText(getString(R.string.player_manage_title));

        sidebar.setVisibility(View.VISIBLE);
        sidebar.setOnTouchingLetterChangedListener(letterListener);
        sidebar.setTextView(tvIndexPopup);

        initActionbar();
    }

    private void initActionbar() {
        findViewById(R.id.view7_actionbar_menu).setVisibility(View.GONE);
        ImageView backView = findViewById(R.id.view7_actionbar_back);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(actionbarListener);
        groupConfirm = findViewById(R.id.view7_actionbar_action_confirm);
        groupNormal = findViewById(R.id.view7_actionbar_action_normal);
        ivSort = findViewById(R.id.view7_actionbar_sort);
        ivChart = findViewById(R.id.view7_actionbar_chart);
        groupNormal.setVisibility(View.VISIBLE);
        findViewById(R.id.view7_actionbar_edit_group).setVisibility(View.VISIBLE);

        findViewById(R.id.view7_actionbar_add).setOnClickListener(actionbarListener);
        findViewById(R.id.view7_actionbar_edit).setOnClickListener(actionbarListener);
        findViewById(R.id.view7_actionbar_delete).setOnClickListener(actionbarListener);
        findViewById(R.id.view7_actionbar_done).setOnClickListener(actionbarListener);
        findViewById(R.id.view7_actionbar_close).setOnClickListener(actionbarListener);
        findViewById(R.id.view7_actionbar_mode).setOnClickListener(actionbarListener);
        ivSort.setOnClickListener(actionbarListener);
        ivChart.setOnClickListener(actionbarListener);
        ivChart.setVisibility(View.VISIBLE);
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
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent();
//                    bundle.putString("name", mEditBean.getNameChn());
//                    bundle.putString("name_eng", mEditBean.getNameEng());
//                    bundle.putString("country", mEditBean.getCountry());
//                    bundle.putString("birthday", mEditBean.getBirthday());
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Intent intent = new Intent().setClass(PlayerManageActivity.this, PlayerCommonActivity.class);
                    if (bean.getData() instanceof User) {
                        intent.putExtra(PlayerCommonActivity.KEY_IS_USER, true);
                        intent.putExtra(PlayerCommonActivity.KEY_PLAYER, ((User) bean.getData()).getId());
                    }
                    else {
                        intent.putExtra(PlayerCommonActivity.KEY_PLAYER, ((PlayerBean) bean.getData()).getId());
                    }
                    startActivity(intent);
                }
            }
        }
    };

    private View.OnClickListener actionbarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.view7_actionbar_back:
                    finish();
                    break;
                case R.id.view7_actionbar_mode:
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
                case R.id.view7_actionbar_sort:
                    showSortPopup();
                    break;
                case R.id.view7_actionbar_chart:
                    showChartDialog();
                    break;
                case R.id.view7_actionbar_add:
                    openEditDialog(null);
                    break;
                case R.id.view7_actionbar_edit:
                    isEditMode = true;
                    updateActionbarStatus(true);
                    break;
                case R.id.view7_actionbar_delete:
                    isDeleteMode = true;
                    updateActionbarStatus(true);
                    if (playerItemAdapter != null) {
                        playerItemAdapter.setSelectMode(true);
                        playerItemAdapter.notifyDataSetChanged();
                    }
                    if (playerStaggerAdapter != null) {
                        playerStaggerAdapter.setSelectMode(true);
                        playerStaggerAdapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.view7_actionbar_done:
                    if (isEditMode) {

                    }
                    else if (isDeleteMode) {
                        deletePlayerItems();
                    }
                    updateActionbarStatus(false);
                    break;
                case R.id.view7_actionbar_close:
                    updateActionbarStatus(false);
                    break;
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

    public void updateActionbarStatus(boolean editMode) {
        if (editMode) {
            groupConfirm.setVisibility(View.VISIBLE);
            groupNormal.setVisibility(View.GONE);
        }
        else {
            groupConfirm.setVisibility(View.GONE);
            groupNormal.setVisibility(View.VISIBLE);
            if (isDeleteMode) {
                if (playerItemAdapter != null) {
                    playerItemAdapter.setSelectMode(false);
                    playerItemAdapter.notifyDataSetChanged();
                }
                if (playerStaggerAdapter != null) {
                    playerStaggerAdapter.setSelectMode(false);
                    playerStaggerAdapter.notifyDataSetChanged();
                }
            }
            isEditMode = false;
            isDeleteMode = false;
        }
    }

    private void showSortPopup() {
        if (popSort == null) {
            popSort = new PopupMenu(this, ivSort);
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
                    }
                    return false;
                }
            });
        }
        popSort.show();
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
        if (isEditMode || isDeleteMode) {
            updateActionbarStatus(false);
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
