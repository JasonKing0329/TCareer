package com.king.app.tcareer.page.match.manage;

import com.king.app.tcareer.R;
import com.king.app.tcareer.databinding.AdapterMatchManageItemBinding;
import com.king.app.tcareer.model.bean.MatchImageBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/2/20 13:27
 */
public class MatchItemAdapter extends MatchManageBaseAdapter<AdapterMatchManageItemBinding> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_match_manage_item;
    }

    @Override
    protected void onBindItem(AdapterMatchManageItemBinding binding, int position, MatchImageBean imageBean) {
        binding.setBean(imageBean);
        MatchNameBean bean = imageBean.getBean();
        binding.tvIndex.setText(String.valueOf(position + 1));
        binding.tvName.setText(bean.getName());
        binding.tvInfor.setText(bean.getMatchBean().getLevel() + "/" + bean.getMatchBean().getCourt()
                + " W" + String.valueOf(bean.getMatchBean().getWeek()));
        binding.tvCountry.setText(bean.getMatchBean().getCountry());
        binding.tvCity.setText(bean.getMatchBean().getCity());

        onBindCheckStatus(binding.cbCheck, position);

        onBindImage(binding.ivImage, position, bean);
    }
}
