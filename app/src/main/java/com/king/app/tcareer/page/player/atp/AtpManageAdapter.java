package com.king.app.tcareer.page.player.atp;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/18 9:37
 */
public class AtpManageAdapter extends BaseRecyclerAdapter<AtpManageAdapter.ItemHolder, PlayerAtpBean> {

    private SparseBooleanArray checkMap;

    private boolean isSelectionMode;

    public AtpManageAdapter() {
        super();
        checkMap = new SparseBooleanArray();
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_atp_item;
    }

    @Override
    protected ItemHolder newViewHolder(View view) {
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.tvId.setText(list.get(position).getId());
        holder.tvName.setText(list.get(position).getName());
        holder.checkBox.setChecked(checkMap.get(position));
        holder.checkBox.setVisibility(isSelectionMode ? View.VISIBLE:View.GONE);
    }

    public void setSelectionMode(boolean selectionMode) {
        isSelectionMode = selectionMode;
        checkMap.clear();
    }

    @Override
    protected void onClickItem(View v, ItemHolder holder) {
        if (isSelectionMode) {
            boolean targetCheck = !checkMap.get(holder.getLayoutPosition());
            checkMap.put(holder.getLayoutPosition(), targetCheck);
            holder.checkBox.setChecked(targetCheck);
        }
        else {
            super.onClickItem(v, holder);
        }
    }

    public List<PlayerAtpBean> getSelectedList() {
        List<PlayerAtpBean> results = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i ++) {
            if (checkMap.get(i)) {
                results.add(list.get(i));
            }
        }
        return results;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_id)
        TextView tvId;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.checkBox)
        CheckBox checkBox;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
