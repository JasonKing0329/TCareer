package com.king.app.tcareer.page.match.manage;

import com.king.app.tcareer.R;
import com.king.app.tcareer.databinding.AdapterMatchGridBinding;
import com.king.app.tcareer.model.bean.MatchImageBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/18 10:53
 */
public class MatchGridAdapter extends MatchManageBaseAdapter<AdapterMatchGridBinding> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_match_grid;
    }

    @Override
    protected void onBindItem(AdapterMatchGridBinding binding, int position, MatchImageBean imageBean) {
        binding.setBean(imageBean);
        MatchNameBean bean = imageBean.getBean();
        binding.tvName.setText(bean.getName());
        binding.tvCountry.setText(bean.getMatchBean().getCountry() + "/" + bean.getMatchBean().getCity());
        binding.tvLine2.setText(bean.getMatchBean().getLevel() + "/" + bean.getMatchBean().getCourt());
        binding.tvWeek.setText("W" + String.valueOf(bean.getMatchBean().getWeek()));

        onBindCheckStatus(binding.cbCheck, position);
        onBindImage(binding.ivMatch, position, bean);
    }
}