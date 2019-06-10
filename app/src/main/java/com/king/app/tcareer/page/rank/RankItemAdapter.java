package com.king.app.tcareer.page.rank;

import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterRankManageItemBinding;
import com.king.app.tcareer.model.db.entity.Rank;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/5 11:45
 */
public class RankItemAdapter extends BaseBindingAdapter<AdapterRankManageItemBinding, Rank> implements View.OnClickListener {

    private OnRankActionListener onRankActionListener;

    public void setOnRankActionListener(OnRankActionListener onRankActionListener) {
        this.onRankActionListener = onRankActionListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_rank_manage_item;
    }

    @Override
    protected void onBindItem(AdapterRankManageItemBinding binding, int position, Rank bean) {
        binding.tvYear.setText(String.valueOf(bean.getYear()));
        binding.tvRank.setText(String.valueOf(bean.getRank()));
        binding.ivDelete.setTag(position);
        binding.ivEdit.setTag(position);
        binding.ivDelete.setOnClickListener(this);
        binding.ivEdit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        switch (v.getId()) {
            case R.id.iv_delete:
                onRankActionListener.onDeleteRank(position);
                break;
            case R.id.iv_edit:
                onRankActionListener.onEditRank(position);
                break;
        }
    }

    public interface OnRankActionListener {
        void onDeleteRank(int position);
        void onEditRank(int position);
    }
}
