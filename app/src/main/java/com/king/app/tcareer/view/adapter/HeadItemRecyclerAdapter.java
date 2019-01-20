package com.king.app.tcareer.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/14 15:12
 */
public abstract class HeadItemRecyclerAdapter<VH extends RecyclerView.ViewHolder, VI extends RecyclerView.ViewHolder, H, I> extends RecyclerView.Adapter {

    private final int HEADER = 1;

    private final int ITEM = 0;

    protected List<Object> list;

    private OnItemClickListener<H, I> onItemClickListener;

    public void setList(List<Object> list) {
        this.list = list;
    }

    public List<Object> getList() {
        return list;
    }

    public void setOnItemClickListener(OnItemClickListener<H, I> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getClass() == getHeaderClass()) {
            return HEADER;
        }
        return ITEM;
    }

    protected abstract Class getHeaderClass();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(getHeaderLayoutRes(), parent, false);
            final VH holder = newHeaderHolder(view);
            view.setOnClickListener(v -> onClickHeader(v, holder));
            return holder;
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutRes(), parent, false);
            final VI holder = newItemHolder(view);
            view.setOnClickListener(v -> onClickItem(v, holder));
            return holder;
        }
    }

    /**
     * 允许子类覆盖
     * @param v
     * @param holder
     */
    protected void onClickHeader(View v, VH holder) {
        if (onItemClickListener != null) {
            onItemClickListener.onClickHead(holder.getLayoutPosition(), (H) list.get(holder.getLayoutPosition()));
        }
    }

    /**
     * 允许子类覆盖
     * @param v
     * @param holder
     */
    protected void onClickItem(View v, VI holder) {
        if (onItemClickListener != null) {
            onItemClickListener.onClickItem(holder.getLayoutPosition(), (I) list.get(holder.getLayoutPosition()));
        }
    }

    protected abstract int getHeaderLayoutRes();

    protected abstract int getItemLayoutRes();

    protected abstract VH newHeaderHolder(View view);

    protected abstract VI newItemHolder(View view);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == HEADER) {
            onBindHeader((VH) holder, position, (H) list.get(position));
        }
        else {
            onBindItem((VI) holder, position, (I) list.get(position));
        }
    }

    protected abstract void onBindHeader(VH holder, int position, H bean);

    protected abstract void onBindItem(VI holder, int position, I bean);

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public interface OnItemClickListener<H, I> {
        void onClickHead(int position, H data);
        void onClickItem(int position, I data);
    }
}
