package com.king.app.tcareer.page.home;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterHomeMatchBinding;
import com.king.app.tcareer.page.match.gallery.UserMatchBean;

/**
 * Created by Administrator on 2017/4/4 0004.
 */

public class HomeMatchAdapter extends BaseBindingAdapter<AdapterHomeMatchBinding, UserMatchBean> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_home_match;
    }

    @Override
    protected void onBindItem(AdapterHomeMatchBinding binding, int position, UserMatchBean bean) {
        binding.setBean(bean);
    }
}
