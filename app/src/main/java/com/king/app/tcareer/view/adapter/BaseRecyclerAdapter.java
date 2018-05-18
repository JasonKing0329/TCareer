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
public abstract class BaseRecyclerAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {

    protected List<T> list;

    private OnItemClickListener<T> onItemClickListener;

    public void setList(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutRes(), parent, false);
        final VH holder = newViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem(v, holder);
            }
        });
        return holder;
    }

    /**
     * 允许子类覆盖
     * @param v
     * @param holder
     */
    protected void onClickItem(View v, VH holder) {
        if (onItemClickListener != null) {
            onItemClickListener.onClickItem(holder.getLayoutPosition(), list.get(holder.getLayoutPosition()));
        }
    }

    protected abstract int getItemLayoutRes();

    protected abstract VH newViewHolder(View view);

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public T getItem(int position) {
        return list.get(position);
    }

    public interface OnItemClickListener<T> {
        void onClickItem(int position, T data);
    }
}
