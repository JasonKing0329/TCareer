package com.king.app.tcareer.page.home.main;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterHomeWeekMatchBinding;
import com.king.app.tcareer.model.bean.MatchImageBean;
import com.king.app.tcareer.model.db.entity.MatchBean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/14 17:36
 */
public class MatchAdapter extends BaseBindingAdapter<AdapterHomeWeekMatchBinding, MatchImageBean> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_home_week_match;
    }

    @Override
    protected void onBindItem(AdapterHomeWeekMatchBinding binding, int position, MatchImageBean bean) {
        binding.setBean(bean);
        binding.tvName.setText(getItem(position).getBean().getName());
        MatchBean matchBean = getItem(position).getBean().getMatchBean();
        binding.tvPlace.setText(matchBean.getCountry() + "/" + matchBean.getCity());
        binding.tvType.setText(matchBean.getLevel() + "/" + matchBean.getCourt());
    }
}
