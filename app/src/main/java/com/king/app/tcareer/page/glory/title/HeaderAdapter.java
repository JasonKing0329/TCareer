package com.king.app.tcareer.page.glory.title;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.AdapterGloryListGroupBinding;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/21 10:37
 */
public class HeaderAdapter extends AbstractExpandableAdapterItem {

    private AdapterGloryListGroupBinding binding;

    @Override
    public int getLayoutResId() {
        return R.layout.adapter_glory_list_group;
    }

    @Override
    public void onBindViews(View root) {
        binding = DataBindingUtil.bind(root);
        root.setOnClickListener(view -> doExpandOrUnexpand());
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        super.onUpdateViews(model, position);
        HeaderItem item = (HeaderItem) model;
        String key = item.getHeaderBean().getKey();
        binding.tvKey.setText(key);
        binding.tvContent.setText(item.getHeaderBean().getContent());

        GradientDrawable drawable = (GradientDrawable) binding.tvTag.getBackground();
        if (AppConstants.RECORD_MATCH_COURTS[0].equals(key)) {
            binding.tvTag.setText(key.substring(0, 1).toUpperCase());
            drawable.setColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_court_hard));
            binding.tvKey.setTextColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_court_hard));
        }
        else if (AppConstants.RECORD_MATCH_COURTS[1].equals(key)) {
            binding.tvTag.setText(key.substring(0, 1).toUpperCase());
            drawable.setColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_court_clay));
            binding.tvKey.setTextColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_court_clay));
        }
        else if (AppConstants.RECORD_MATCH_COURTS[2].equals(key)) {
            binding.tvTag.setText(key.substring(0, 1).toUpperCase());
            drawable.setColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_court_grass));
            binding.tvKey.setTextColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_court_grass));
        }
        else if (AppConstants.RECORD_MATCH_COURTS[3].equals(key)) {
            binding.tvTag.setText(key.substring(0, 1).toUpperCase());
            drawable.setColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_court_inhard));
            binding.tvKey.setTextColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_court_inhard));
        }
        else if (AppConstants.RECORD_MATCH_LEVELS[0].equals(key)) {
            binding.tvTag.setText("GS");
            drawable.setColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_gs));
            binding.tvKey.setTextColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_gs));
        }
        else if (AppConstants.RECORD_MATCH_LEVELS[1].equals(key)) {
            binding.tvTag.setText("MC");
            drawable.setColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_mc));
            binding.tvKey.setTextColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_mc));
        }
        else if (AppConstants.RECORD_MATCH_LEVELS[2].equals(key)) {
            binding.tvTag.setText("1000");
            drawable.setColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_1000));
            binding.tvKey.setTextColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_1000));
        }
        else if (AppConstants.RECORD_MATCH_LEVELS[3].equals(key)) {
            binding.tvTag.setText("500");
            drawable.setColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_500));
            binding.tvKey.setTextColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_500));
        }
        else if (AppConstants.RECORD_MATCH_LEVELS[4].equals(key)) {
            binding.tvTag.setText("250");
            drawable.setColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_250));
            binding.tvKey.setTextColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_250));
        }
        else if (AppConstants.RECORD_MATCH_LEVELS[6].equals(key)) {
            binding.tvTag.setText("OG");
            drawable.setColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_oly));
            binding.tvKey.setTextColor(binding.tvTag.getContext().getResources().getColor(R.color.normal_level_oly));
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {

    }
}
