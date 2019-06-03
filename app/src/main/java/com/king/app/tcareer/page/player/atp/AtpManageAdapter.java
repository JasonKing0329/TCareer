package com.king.app.tcareer.page.player.atp;

import android.util.SparseBooleanArray;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterAtpItemBinding;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/18 9:37
 */
public class AtpManageAdapter extends BaseBindingAdapter<AdapterAtpItemBinding, PlayerAtpBean> {

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
    protected void onBindItem(AdapterAtpItemBinding binding, int position, PlayerAtpBean bean) {
        binding.tvNo.setText(String.valueOf(position + 1));
        binding.tvId.setText(list.get(position).getId());
        binding.tvName.setText(list.get(position).getName());
        binding.checkBox.setChecked(checkMap.get(position));
        binding.checkBox.setVisibility(isSelectionMode ? View.VISIBLE:View.GONE);
    }

    public void setSelectionMode(boolean selectionMode) {
        isSelectionMode = selectionMode;
        checkMap.clear();
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (isSelectionMode) {
            boolean targetCheck = !checkMap.get(position);
            checkMap.put(position, targetCheck);
            notifyItemChanged(position);
        }
        else {
            super.onClickItem(v, position);
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
}
