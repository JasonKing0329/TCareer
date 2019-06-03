package com.king.app.tcareer.page.player.list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.MvvmFragment;
import com.king.app.tcareer.databinding.FragmentPlayerRichBinding;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.manage.PlayerEditDialog;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.widget.FitSideBar;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/5/19 0019 15:37
 */

public class RichPlayerFragment extends MvvmFragment<FragmentPlayerRichBinding, RichPlayerViewModel> {

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_HEDE_NO_RECORDS = "hide_players_without_records";

    private RichPlayerHolder holder;

    private RichPlayerAdapter adapter;

    private boolean isSelectPlayerMode;

    private boolean isEditMode;

    private int mBottomMargin;

    private boolean mOnlyShowUser;

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
    protected RichPlayerViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RichPlayerViewModel.class);
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_player_rich;
    }

    @Override
    protected void onCreate(View view) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvList.setLayoutManager(manager);

        mBinding.rvList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.top = ScreenUtils.dp2px(5);
                }
                if (position == mModel.getListSize() - 1) {
                    outRect.bottom = mBottomMargin;
                }
            }
        });

        mBinding.rvList.addOnScrollListener(new RecyclerViewListener());

        mBinding.sidebar.setOnSidebarStatusListener(new FitSideBar.OnSidebarStatusListener() {
            @Override
            public void onChangeFinished() {
                mBinding.tvIndexPopup.setVisibility(View.GONE);
            }

            @Override
            public void onSideIndexChanged(String index) {
                int selection = mModel.getLetterPosition(index);
                scrollToPosition(selection);

                mBinding.tvIndexPopup.setText(index);
                mBinding.tvIndexPopup.setVisibility(View.VISIBLE);
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
        final LinearLayoutManager manager = (LinearLayoutManager) mBinding.rvList.getLayoutManager();
        int fir = manager.findFirstVisibleItemPosition();
        int end = manager.findLastVisibleItemPosition();
        if (selection <= fir) {
            mBinding.rvList.scrollToPosition(selection);
        } else if (selection <= end) {
            int top = mBinding.rvList.getChildAt(selection - fir).getTop();
            mBinding.rvList.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            mBinding.rvList.scrollToPosition(selection);
            //记录当前需要在RecyclerView滚动监听里面继续第二次滚动
            needMove = true;
        }
    }

    public void toggleSidebar() {
        if (mBinding.sidebar.getVisibility() == View.VISIBLE) {
            mBinding.sidebar.setVisibility(View.GONE);
        }
        else {
            mBinding.sidebar.setVisibility(View.VISIBLE);
        }
    }

    public void filterPlayer(RichFilterBean bean) {
        mModel.filter(bean);
    }

    private int nCurrentFirst;

    public int[] getWinLoseOfList() {
        return mModel.getWinLoseOfList();
    }

    public int getTotalPlayers() {
        return mModel.getTotalPlayers();
    }

    public List<String> getFilterTexts() {
        return mModel.getFilterTexts();
    }

    public void setOnlyShowUser(boolean onlyShowUser) {
        mOnlyShowUser = onlyShowUser;
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
            LinearLayoutManager manager = (LinearLayoutManager) mBinding.rvList.getLayoutManager();
            int first = manager.findFirstVisibleItemPosition();
            if (first != nCurrentFirst) {
                nCurrentFirst = first;
                String index = mModel.getItemIndex(nCurrentFirst);
                holder.updateFirstIndex(index);
            }
        }
    }

    @Override
    protected void onCreateData() {
        mModel.indexObserver.observe(this, index -> mBinding.sidebar.addIndex(index));
        mModel.updateIndexGravity.observe(this, gravity -> mBinding.sidebar.setGravity(gravity));
        mModel.clearIndex.observe(this, deleteMode -> mBinding.sidebar.clear());
        mModel.onIndexCreated.observe(this, finished -> {
            mBinding.sidebar.build();
            mBinding.sidebar.setVisibility(View.VISIBLE);
        });
        mModel.onSortFinished.observe(this, sortType -> {
            adapter.notifyDataSetChanged();
            holder.onSortFinished(sortType);
        });
        mModel.setDeleteMode.observe(this, deleteMode -> setDeleteMode(deleteMode));
        mModel.playersObserver.observe(this, list -> showPlayers(list));
        mModel.onUpdateAtpCompleted.observe(this, position -> adapter.notifyItemChanged(position));

        if (getArguments() == null || getArguments().getLong(KEY_USER_ID, -1) == -1) {
            mModel.loadPlayers(mOnlyShowUser);
        }
        else {
            mModel.loadPlayers(getArguments().getLong(KEY_USER_ID), getArguments().getBoolean(KEY_HEDE_NO_RECORDS), mOnlyShowUser);
        }
    }

    private void showPlayers(List<RichPlayerBean> list) {
        if (adapter == null) {
            adapter = new RichPlayerAdapter();
            adapter.setExpandMap(mModel.getExpandMap());
            adapter.setOnRichPlayerListener(new RichPlayerAdapter.OnRichPlayerListener() {
                @Override
                public void onRefreshItem(int position, CompetitorBean bean) {
                    mModel.updateAtpData(bean.getAtpId(), position);
                }

                @Override
                public void onClickItem(View view, int position, CompetitorBean bean) {
                    if (isSelectPlayerMode) {
                        if (holder != null) {
                            holder.onSelectPlayer(bean);
                        }
                    } else if (isEditMode) {
                        openEditDialog(bean);
                    } else {
                        Intent intent = new Intent().setClass(getContext(), PlayerPageActivity.class);
                        if (mModel.getUser() != null) {
                            intent.putExtra(PlayerPageActivity.KEY_USER_ID, mModel.getUser().getId());
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
            });
            adapter.setFragmentManager(getChildFragmentManager());
            adapter.setList(list);
            mBinding.rvList.setAdapter(adapter);
        } else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }

        mBinding.sidebar.post(() -> mBinding.sidebar.invalidate());
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
        mModel.deletePlayer(adapter.getSelectedList());
    }

    public void sortPlayer(int sortType) {
        mBinding.sidebar.clear();
        mModel.sortPlayer(sortType);
    }

    public void reload() {
        mModel.resetRank();
        mModel.loadPlayers(mOnlyShowUser);
    }

    public boolean onBackPressed() {
        if (adapter.isSelectMode()) {
            adapter.setSelectMode(false);
            return true;
        }
        return false;
    }

    public List<RichPlayerBean> getPlayerList() {
        return mModel.getPlayerList();
    }

    public void filter(String words) {
        mModel.filter(words);
    }

    public void setExpandAll(boolean expand) {
        mModel.setExpandAll(expand);
        adapter.notifyDataSetChanged();
    }

    public void updateUser(User user) {
        mModel.onUserChanged(user);
    }

}
