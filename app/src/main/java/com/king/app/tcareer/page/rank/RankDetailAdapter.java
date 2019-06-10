package com.king.app.tcareer.page.rank;

import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterRankWeekDetailBinding;
import com.king.app.tcareer.model.db.entity.RankWeek;

import java.text.SimpleDateFormat;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/8 14:14
 */
public class RankDetailAdapter extends BaseBindingAdapter<AdapterRankWeekDetailBinding, RankWeek> {

    private OnRankItemListener onRankItemListener;

    private SimpleDateFormat dateFormat;

    public RankDetailAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_rank_week_detail;
    }

    @Override
    protected void onBindItem(AdapterRankWeekDetailBinding binding, int position, RankWeek rank) {
        binding.tvDate.setText(dateFormat.format(rank.getDate()));
        binding.tvRank.setText(String.valueOf(rank.getRank()));
        binding.tvScore.setText(String.valueOf(rank.getScore()));
        binding.tvWeek.setText("W" + rank.getWeek());

        binding.ivDelete.setTag(position);
        binding.ivDelete.setOnClickListener(deleteListener);
        binding.ivUpdate.setTag(position);
        binding.ivUpdate.setOnClickListener(updateListener);
    }

    public void setOnRankItemListener(OnRankItemListener onRankItemListener) {
        this.onRankItemListener = onRankItemListener;
    }

    private View.OnClickListener deleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            if (onRankItemListener != null) {
                onRankItemListener.onDeleteItem(position, list.get(position));
            }
        }
    };

    private View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            if (onRankItemListener != null) {
                onRankItemListener.onUpdateItem(position, list.get(position));
            }
        }
    };

    public void removeItem(int position) {
        list.remove(position);
    }

    public interface OnRankItemListener {
        void onUpdateItem(int position, RankWeek item);
        void onDeleteItem(int position, RankWeek item);
    }
}
