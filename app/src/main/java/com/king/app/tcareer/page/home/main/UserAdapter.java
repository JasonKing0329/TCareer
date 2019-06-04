package com.king.app.tcareer.page.home.main;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterHomeUserBinding;
import com.king.app.tcareer.model.db.entity.User;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/14 17:36
 */
public class UserAdapter extends BaseBindingAdapter<AdapterHomeUserBinding, User> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_home_user;
    }

    @Override
    protected void onBindItem(AdapterHomeUserBinding binding, int position, User bean) {
        binding.setBean(bean);
    }
}
