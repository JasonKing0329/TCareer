package com.king.app.tcareer.page.home;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterNotifyRankBinding;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;

import java.text.SimpleDateFormat;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 9:19
 */
public class NotifyRankAdapter extends BaseBindingAdapter<AdapterNotifyRankBinding, NotifyRankBean> {

    private SimpleDateFormat dateFormat;

    public NotifyRankAdapter() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_notify_rank;
    }

    @Override
    protected void onBindItem(AdapterNotifyRankBinding binding, int position, NotifyRankBean bean) {
        Glide.with(binding.ivUser.getContext())
                .load(ImageProvider.getPlayerHeadPath(list.get(position).getUser().getNameChn()))
                .apply(GlideOptions.getDefaultPlayerOptions())
                .into(binding.ivUser);
        binding.tvDate.setText(dateFormat.format(list.get(position).getLastRank().getDate()));
    }

}
