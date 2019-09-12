package com.king.app.tcareer.page.compare;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterComp2lineBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/9/12 14:39
 */
public class TwoLineAdapter extends BaseBindingAdapter<AdapterComp2lineBinding, TwoLineBean> {
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_comp_2line;
    }

    @Override
    protected void onBindItem(AdapterComp2lineBinding binding, int position, TwoLineBean bean) {
        binding.setBean(bean);
    }
}
