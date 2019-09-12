package com.king.app.tcareer.page.compare;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterCompSubTotalBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/9/12 14:39
 */
public class SubTotalAdapter extends BaseBindingAdapter<AdapterCompSubTotalBinding, SubTotalBean> {
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_comp_sub_total;
    }

    @Override
    protected void onBindItem(AdapterCompSubTotalBinding binding, int position, SubTotalBean bean) {
        binding.setBean(bean);
    }
}
