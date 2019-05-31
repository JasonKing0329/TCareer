package com.king.app.tcareer.base.mvvm;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Desc:RecyclerView.Adapter的databinding方式的基类
 *
 * @author：Jing Yang
 * @date: 2018/6/7 16:50
 */
public abstract class BaseBindingAdapter<V extends ViewDataBinding, T> extends RecyclerView.Adapter {

    protected List<T> list;

    protected OnItemClickListener<T> onItemClickListener;

    protected OnItemLongClickListener<T> onItemLongClickListener;

    public void setList(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        V binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , getItemLayoutRes(), parent, false);
        BindingHolder holder = new BindingHolder(binding.getRoot());
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getLayoutPosition();
            onClickItem(v, position);
        });
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                int position = holder.getLayoutPosition();
                onItemLongClickListener.onLongClickItem(v, position, list.get(position));
                return true;
            });
        }
        return holder;
    }

    protected abstract int getItemLayoutRes();

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        V binding = getBindingFromHolder(holder);
        onBindItem(binding, position, list.get(position));
        binding.executePendingBindings();
    }

    protected V getBindingFromHolder(RecyclerView.ViewHolder holder) {
        return DataBindingUtil.getBinding(holder.itemView);
    }

    protected abstract void onBindItem(V binding, int position, T bean);

    protected void onClickItem(View v, int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onClickItem(v, position, list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public T getItem(int position) {
        return list.get(position);
    }

    public interface OnItemClickListener<T> {
        void onClickItem(View view, int position, T data);
    }

    public interface OnItemLongClickListener<T> {
        void onLongClickItem(View view, int position, T data);
    }
}
