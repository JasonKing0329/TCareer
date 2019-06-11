package com.king.app.tcareer.page.glory;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterGloryListItemBinding;
import com.king.app.tcareer.page.glory.bean.GloryRecordItem;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/11 17:13
 */
public class GloryRecordAdapter extends BaseBindingAdapter<AdapterGloryListItemBinding, GloryRecordItem> {
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_glory_list_item;
    }

    @Override
    protected void onBindItem(AdapterGloryListItemBinding binding, int position, GloryRecordItem bean) {
        binding.setBean(bean);
    }
}
