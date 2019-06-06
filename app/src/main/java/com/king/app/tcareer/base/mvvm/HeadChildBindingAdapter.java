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
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/17 9:33
 */
public abstract class HeadChildBindingAdapter<VH extends ViewDataBinding, VI extends ViewDataBinding, H, I> extends RecyclerView.Adapter {

    protected final int TYPE_HEAD = 0;
    protected final int TYPE_ITEM = 1;

    protected List<Object> list;

    private OnHeadClickListener<H> onHeadClickListener;

    private OnItemClickListener<I> onItemClickListener;

    public void setList(List<Object> list) {
        this.list = list;
    }

    protected abstract Class getItemClass();

    public void setOnHeadClickListener(OnHeadClickListener<H> onHeadClickListener) {
        this.onHeadClickListener = onHeadClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener<I> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getClass() == getItemClass()) {
            return TYPE_ITEM;
        }
        return TYPE_HEAD;
    }

    public boolean isHead(int position) {
        return getItemViewType(position) == TYPE_HEAD;
    }

    public boolean isItem(int position) {
        return getItemViewType(position) == TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            VH binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , getHeaderRes(), parent, false);
            BindingHolder holder = new BindingHolder(binding.getRoot());
            binding.getRoot().setOnClickListener(v -> onClickHead(binding.getRoot(), holder.getLayoutPosition()
                    , (H) list.get(holder.getLayoutPosition())));
            return holder;
        }
        else {
            VI binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , getItemRes(), parent, false);
            BindingHolder holder = new BindingHolder(binding.getRoot());
            binding.getRoot().setOnClickListener(v -> onClickItem(binding.getRoot(), holder.getLayoutPosition()
                    , (I) list.get(holder.getLayoutPosition())));
            return holder;
        }
    }

    protected void onClickHead(View view, int position, H data) {
        if (onHeadClickListener != null) {
            onHeadClickListener.onClickHead(view, position, data);
        }
    }

    protected void onClickItem(View view, int position, I data) {
        if (onItemClickListener != null) {
            onItemClickListener.onClickItem(view, position, data);
        }
    }

    protected abstract int getHeaderRes();

    protected abstract int getItemRes();

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEAD) {
            VH binding = DataBindingUtil.getBinding(holder.itemView);
            onBindHead(binding, position, (H) list.get(position));
            binding.executePendingBindings();
        }
        else {
            VI binding = DataBindingUtil.getBinding(holder.itemView);
            onBindItem(binding, position, (I) list.get(position));
            binding.executePendingBindings();
        }
    }

    protected abstract void onBindHead(VH binding, int position, H head);

    protected abstract void onBindItem(VI binding, int position, I item);

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();// 首尾分别为header和footer
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {

        public BindingHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnHeadClickListener<H> {
        void onClickHead(View view, int position, H data);
    }

    public interface OnItemClickListener<I> {
        void onClickItem(View view, int position, I data);
    }

}
