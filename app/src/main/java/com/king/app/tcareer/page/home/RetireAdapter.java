package com.king.app.tcareer.page.home;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterRetireItemBinding;
import com.king.app.tcareer.model.db.entity.Retire;

import java.text.SimpleDateFormat;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/30 8:58
 */
public class RetireAdapter extends BaseBindingAdapter<AdapterRetireItemBinding, Retire> {

    private SimpleDateFormat dateFormat;

    private OnRetireListener onRetireListener;

    public RetireAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void setOnRetireListener(OnRetireListener onRetireListener) {
        this.onRetireListener = onRetireListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_retire_item;
    }

    @Override
    protected void onBindItem(AdapterRetireItemBinding binding, int position, Retire retire) {
        if (retire.getRelieveId() > 0) {
            binding.tvType.setText("Relive");
        }
        else {
            binding.tvType.setText("Retire");
        }
        binding.tvDeclare.setText(dateFormat.format(retire.getDeclareDate()));
        binding.tvEffect.setText(dateFormat.format(retire.getEffectDate()));
        binding.ivDelete.setOnClickListener(v -> onRetireListener.onDeleteItem(list.get(position)));
    }

    public interface OnRetireListener {
        void onDeleteItem(Retire retire);
    }
}
