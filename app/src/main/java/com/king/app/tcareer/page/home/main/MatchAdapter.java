package com.king.app.tcareer.page.home.main;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterHomeWeekMatchBinding;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/14 17:36
 */
public class MatchAdapter extends BaseBindingAdapter<AdapterHomeWeekMatchBinding, MatchNameBean> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_home_week_match;
    }

    @Override
    protected void onBindItem(AdapterHomeWeekMatchBinding binding, int position, MatchNameBean bean) {

        binding.tvName.setText(getItem(position).getName());
        MatchBean matchBean = getItem(position).getMatchBean();
        binding.tvPlace.setText(matchBean.getCountry() + "/" + matchBean.getCity());
        binding.tvType.setText(matchBean.getLevel() + "/" + matchBean.getCourt());

        String imagePath = ImageProvider.getMatchHeadPath(getItem(position).getName(), matchBean.getCourt());
        Glide.with(binding.ivMatch.getContext())
                .asBitmap()
                .load(imagePath)
                .apply(GlideOptions.getDefaultMatchOptions())
                .into(binding.ivMatch);

    }
}
