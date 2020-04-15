package com.king.app.tcareer.page.score;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterFrozenItemBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/4/15 8:45
 */
public class FrozenItemAdapter extends BaseBindingAdapter<AdapterFrozenItemBinding, FrozenItem> {

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_frozen_item;
    }

    @Override
    protected void onBindItem(AdapterFrozenItemBinding binding, int position, FrozenItem bean) {
        binding.setBean(bean);
        binding.ivDelete.setOnClickListener(v -> onDeleteListener.onDelete(position, bean));
    }

    public interface OnDeleteListener {
        void onDelete(int position, FrozenItem bean);
    }
}
