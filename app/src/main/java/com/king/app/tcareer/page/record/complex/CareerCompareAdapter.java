package com.king.app.tcareer.page.record.complex;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/20 13:43
 */
public class CareerCompareAdapter extends BaseRecyclerAdapter<CareerCompareAdapter.ItemHolder, CompareItem> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_career_compare;
    }

    @Override
    protected ItemHolder newViewHolder(View view) {
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        if (list.get(position).isHead()) {
            holder.tvHead.setText(list.get(position).getTitle());
            holder.tvHead.setVisibility(View.VISIBLE);
            holder.groupItem.setVisibility(View.GONE);
        }
        else {
            holder.tvHead.setVisibility(View.GONE);
            holder.groupItem.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(list.get(position).getTitle());
            holder.tvItemK.setText(parseIntValue(list.get(position).getValueK()));
            holder.tvItemF.setText(parseIntValue(list.get(position).getValueF()));
            holder.tvItemH.setText(parseIntValue(list.get(position).getValueH()));
            holder.tvItemQ.setText(parseIntValue(list.get(position).getValueQ()));
        }
    }

    private String parseIntValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return "0";
        }
        else {
            return value;
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.group_item)
        ViewGroup groupItem;
        @BindView(R.id.tv_head)
        TextView tvHead;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_item_k)
        TextView tvItemK;
        @BindView(R.id.tv_item_f)
        TextView tvItemF;
        @BindView(R.id.tv_item_h)
        TextView tvItemH;
        @BindView(R.id.tv_item_q)
        TextView tvItemQ;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
