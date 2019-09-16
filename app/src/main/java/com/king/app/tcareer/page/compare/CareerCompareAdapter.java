package com.king.app.tcareer.page.compare;

import android.text.TextUtils;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterCareerCompareBinding;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/20 13:43
 */
public class CareerCompareAdapter extends BaseBindingAdapter<AdapterCareerCompareBinding, CompareItem> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_career_compare;
    }

    @Override
    protected void onBindItem(AdapterCareerCompareBinding binding, int position, CompareItem bean) {
        if (list.get(position).isHead()) {
            binding.tvHead.setText(list.get(position).getTitle());
            binding.tvHead.setVisibility(View.VISIBLE);
            binding.groupItem.setVisibility(View.GONE);
        }
        else {
            binding.tvHead.setVisibility(View.GONE);
            binding.groupItem.setVisibility(View.VISIBLE);
            binding.tvTitle.setText(list.get(position).getTitle());
            binding.tvItemK.setText(parseIntValue(list.get(position).getValueK()));
            binding.tvItemF.setText(parseIntValue(list.get(position).getValueF()));
            binding.tvItemH.setText(parseIntValue(list.get(position).getValueH()));
            binding.tvItemQ.setText(parseIntValue(list.get(position).getValueQ()));
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
}
