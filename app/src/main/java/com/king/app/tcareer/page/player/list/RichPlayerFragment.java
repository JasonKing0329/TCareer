package com.king.app.tcareer.page.player.list;

import android.content.Intent;
import android.graphics.Rect;
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
import com.king.app.tcareer.view.widget.SideBar;

import java.util.List;

import butterknife.BindView;

/**
 * @desc
 * @auth 景阳
 * @time 2018/5/19 0019 15:37
 */

public class RichPlayerFragment extends BaseMvpFragment<RichPlayerPresenter> implements RichPlayerView
        , RichPlayerAdapter.OnRichPlayerListener {

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.sidebar)
    SideBar sidebar;
    @BindView(R.id.tv_index_popup)
    TextView tvIndexPopup;

    private RichPlayerHolder holder;

    private RichPlayerAdapter adapter;

    private boolean isSelectPlayerMode;

    private boolean isEditMode;

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
                outRect.top = ScreenUtils.dp2px(10);
            }
        });

        sidebar.setVisibility(View.VISIBLE);
        sidebar.setOnTouchingLetterChangedListener(s -> {
            int selection = presenter.getLetterPosition(s);
            rvList.scrollToPosition(selection);
        });
        sidebar.setTextView(tvIndexPopup);

    }

    @Override
    protected RichPlayerPresenter createPresenter() {
        return new RichPlayerPresenter();
    }

    @Override
    protected void onCreateData() {
        presenter.loadPlayers();
    }

    @Override
    public SideBar getSidebar() {
        return sidebar;
    }

    @Override
    public void showPlayers(List<RichPlayerBean> list) {
        if (adapter == null) {
            adapter = new RichPlayerAdapter();
            adapter.setOnRichPlayerListener(this);
            adapter.setFragmentManager(getChildFragmentManager());
            adapter.setList(list);
            adapter.setExpandAll(true);
            rvList.setAdapter(adapter);
        } else {
            adapter.setList(list);
            adapter.setExpandAll(true);
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
    public void onClickImage(int position, CompetitorBean bean) {

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
    public void sortFinished() {
        adapter.notifyDataSetChanged();
    }

    public void reload() {
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

    public void toggleExpandStatus() {
        adapter.toggleExpandStatus();
        adapter.notifyDataSetChanged();
    }
}
