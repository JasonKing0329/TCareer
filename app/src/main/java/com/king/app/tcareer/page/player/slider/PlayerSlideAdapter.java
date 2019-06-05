package com.king.app.tcareer.page.player.slider;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterSlidePlayerItemBinding;

public class PlayerSlideAdapter<V> extends BaseBindingAdapter<AdapterSlidePlayerItemBinding, SlideItem<V>> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_slide_player_item;
    }

    @Override
    protected void onBindItem(AdapterSlidePlayerItemBinding binding, int position, SlideItem bean) {
        binding.setBean(bean);
    }

}
