package com.king.app.tcareer.page.player.list;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpFragment;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.manage.PlayerEditDialog;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.widget.FitSideBar;

import java.util.List;

import butterknife.BindView;

/**
 * @desc
 * @auth 景阳
 * @time 2018/5/19 0019 15:37
 */

public class RichPlayerFragment extends BaseMvpFragment<RichPlayerPresenter> implements RichPlayerView
        , RichPlayerAdapter.OnRichPlayerListener {

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_HEDE_NO_RECORDS = "hide_players_without_records";

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.sidebar)
    FitSideBar sidebar;
    @BindView(R.id.tv_index_popup)
    TextView tvIndexPopup;

    private RichPlayerHolder holder;

    private RichPlayerAdapter adapter;

    private boolean isSelectPlayerMode;

    private boolean isEditMode;

    private int mBottomMargin;

    public static RichPlayerFragment newInstance(long userId, boolean hidePlayersWithoutRecords) {
        RichPlayerFragment fragment = new RichPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        bundle.putBoolean(KEY_HEDE_NO_RECORDS, hidePlayersWithoutRecords);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (RichPlayerHolder) holder;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_player_rich;
    }

    @Override
    protected void onCreate(View view) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvList.setLayoutManager(manager);

        rvList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.top = ScreenUtils.dp2px(5);
                }
                if (position == presenter.getListSize() - 1) {
                    outRect.bottom = mBottomMargin;
                }
            }
        });

        rvList.addOnScrollListener(new RecyclerViewListener());

        sidebar.setOnSidebarStatusListener(new FitSideBar.OnSidebarStatusListener() {
            @Override
            public void onChangeFinished() {
                tvIndexPopup.setVisibility(View.GONE);
            }

            @Override
            public void onSideIndexChanged(String index) {
                int selection = presenter.getLetterPosition(index);
                scrollToPosition(selection);

                tvIndexPopup.setText(index);
                tvIndexPopup.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * set the bottom margin of the last item in list
     * @param mBottomMargin
     */
    public void setBottomMargin(int mBottomMargin) {
        this.mBottomMargin = mBottomMargin;
    }

    private boolean needMove;
    private int nSelection;

    private void scrollToPosition(int selection) {
        nSelection = selection;
        final LinearLayoutManager manager = (LinearLayoutManager) rvList.getLayoutManager();
        int fir = manager.findFirstVisibleItemPosition();
        int end = manager.findLastVisibleItemPosition();
        if (selection <= fir) {
            rvList.scrollToPosition(selection);
        } else if (selection <= end) {
            int top = rvList.getChildAt(selection - fir).getTop();
            rvList.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            rvList.scrollToPosition(selection);
            //记录当前需要在RecyclerView滚动监听里面继续第二次滚动
            needMove = true;
        }
    }

    public void toggleSidebar() {
        if (sidebar.getVisibility() == View.VISIBLE) {
            sidebar.setVisibility(View.GONE);
        }
        else {
            sidebar.setVisibility(View.VISIBLE);
        }
    }

    public void filterPlayer(RichFilterBean bean) {
        presenter.filter(bean);
    }

    private int nCurrentFirst;

    public int[] getWinLoseOfList() {
        return presenter.getWinLoseOfList();
    }

    public int getTotalPlayers() {
        return presenter.getTotalPlayers();
    }

    public List<String> getFilterTexts() {
        return presenter.getFilterTexts();
    }

    private class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在这里进行第二次滚动（最后的距离）
            if (needMove) {
                needMove = false;
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                int n = nSelection - ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (n >= 0 && n < recyclerView.getChildCount()) {
                    recyclerView.scrollBy(0, recyclerView.getChildAt(n).getTop()); //滚动到顶部
                }
            }

            // 更新第一个index标签
            LinearLayoutManager manager = (LinearLayoutManager) rvList.getLayoutManager();
            int first = manager.findFirstVisibleItemPosition();
            if (first != nCurrentFirst) {
                nCurrentFirst = first;
                String index = presenter.getItemIndex(nCurrentFirst);
                holder.updateFirstIndex(index);
            }
        }
    }

    @Override
    protected RichPlayerPresenter createPresenter() {
        return new RichPlayerPresenter();
    }

    @Override
    protected void onCreateData() {
        if (getArguments() == null || getArguments().getLong(KEY_USER_ID, -1) == -1) {
            presenter.loadPlayers();
        }
        else {
            presenter.loadPlayers(getArguments().getLong(KEY_USER_ID), getArguments().getBoolean(KEY_HEDE_NO_RECORDS));
        }
    }

    @Override
    public FitSideBar getSidebar() {
        return sidebar;
    }

    @Override
    public void showPlayers(List<RichPlayerBean> list) {
        if (adapter == null) {
            adapter = new RichPlayerAdapter();
            adapter.setExpandMap(presenter.getExpandMap());
            adapter.setOnRichPlayerListener(this);
            adapter.setFragmentManager(getChildFragmentManager());
            adapter.setList(list);
            rvList.setAdapter(adapter);
        } else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }

        sidebar.post(() -> sidebar.invalidate());
    }

    @Override
    public void onClickItem(int position, CompetitorBean bean) {
        if (isSelectPlayerMode) {
            if (holder != null) {
                holder.onSelectPlayer(bean);
            }
        } else if (isEditMode) {
            openEditDialog(bean);
        } else {
            Intent intent = new Intent().setClass(getContext(), PlayerPageActivity.class);
            if (presenter.getUser() != null) {
                intent.putExtra(PlayerPageActivity.KEY_USER_ID, presenter.getUser().getId());
            }
            if (bean instanceof User) {
                intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_IS_USER, true);
                intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, bean.getId());
            } else {
                intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, bean.getId());
            }
            startActivity(intent);
        }
    }

    @Override
    public void onRefreshItem(int position, CompetitorBean bean) {
        presenter.updateAtpData(bean.getAtpId(), position);
    }

    @Override
    public void onUpdateAtpCompleted(int position) {
        adapter.notifyItemChanged(position);
    }

    private void openEditDialog(CompetitorBean bean) {
        PlayerEditDialog dialog = new PlayerEditDialog();
        dialog.setCompetitorBean(bean);
        dialog.setCustomTitle(bean.getNameEng());
        dialog.setOnPlayerEditListener(bean1 -> adapter.notifyPlayerChanged(bean1.getId()));
        dialog.show(getChildFragmentManager(), "PlayerEditDialog");
    }

    public void setSelectPlayerMode(boolean selectPlayerMode) {
        isSelectPlayerMode = selectPlayerMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public void setDeleteMode(boolean deleteMode) {
        adapter.setSelectMode(deleteMode);
        adapter.notifyDataSetChanged();
    }

    /**
     * confirm delete
     */
    public void confirmDelete() {
        presenter.deletePlayer(adapter.getSelectedList());
    }

    @Override
    public void deleteSuccess() {
        setDeleteMode(false);
    }

    public void sortPlayer(int sortType) {
        presenter.sortPlayer(sortType);
    }

    @Override
    public void sortFinished(int sortType) {
        adapter.notifyDataSetChanged();
        holder.onSortFinished(sortType);
    }

    public void reload() {
        presenter.resetRank();
        presenter.loadPlayers();
    }

    public boolean onBackPressed() {
        if (adapter.isSelectMode()) {
            adapter.setSelectMode(false);
            return true;
        }
        return false;
    }

    public List<RichPlayerBean> getPlayerList() {
        return presenter.getPlayerList();
    }

    public void filter(String words) {
        presenter.filter(words);
    }

    public void setExpandAll(boolean expand) {
        presenter.setExpandAll(expand);
        adapter.notifyDataSetChanged();
    }

    public void updateUser(User user) {
        presenter.onUserChanged(user);
    }

}
