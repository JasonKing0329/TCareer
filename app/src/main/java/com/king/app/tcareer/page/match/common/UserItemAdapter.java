package com.king.app.tcareer.page.match.common;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterMatchCommonUserBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/4 15:30
 */
public class UserItemAdapter extends BaseBindingAdapter<AdapterMatchCommonUserBinding, UserItem> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_match_common_user;
    }

    @Override
    protected void onBindItem(AdapterMatchCommonUserBinding binding, int position, UserItem bean) {
        binding.setBean(bean);
    }
}
