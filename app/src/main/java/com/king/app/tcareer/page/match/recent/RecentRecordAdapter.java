package com.king.app.tcareer.page.match.recent;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.HeadChildBindingAdapter;
import com.king.app.tcareer.databinding.AdapterMatchRecentRecordBinding;
import com.king.app.tcareer.databinding.AdapterMatchRecentRoundBinding;

public class RecentRecordAdapter extends HeadChildBindingAdapter<AdapterMatchRecentRoundBinding, AdapterMatchRecentRecordBinding, String, RecentItem> {

    @Override
    protected int getHeaderRes() {
        return R.layout.adapter_match_recent_round;
    }

    @Override
    protected int getItemRes() {
        return R.layout.adapter_match_recent_record;
    }

    @Override
    protected Class getItemClass() {
        return RecentItem.class;
    }

    @Override
    protected void onBindHead(AdapterMatchRecentRoundBinding binding, int position, String head) {
        binding.tvRound.setText(head);
    }

    @Override
    protected void onBindItem(AdapterMatchRecentRecordBinding binding, int position, RecentItem item) {
        binding.setBean(item);
    }
}
