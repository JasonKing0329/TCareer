package com.king.app.tcareer.page.rank;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.model.db.entity.RankWeek;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/8 14:14
 */
public class RankDetailAdapter extends RecyclerView.Adapter<RankDetailAdapter.RankHolder> {

    private List<RankWeek> list;

    private OnRankItemListener onRankItemListener;

    private SimpleDateFormat dateFormat;

    public RankDetailAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public RankHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RankHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rank_week_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(RankHolder holder, int position) {
        RankWeek rank = list.get(position);
        holder.tvDate.setText(dateFormat.format(rank.getDate()));
        holder.tvRank.setText(String.valueOf(rank.getRank()));
        holder.tvScore.setText(String.valueOf(rank.getScore()));
        holder.tvWeek.setText("W" + rank.getWeek());

        holder.ivDelete.setTag(position);
        holder.ivDelete.setOnClickListener(deleteListener);
        holder.ivUpdate.setTag(position);
        holder.ivUpdate.setOnClickListener(updateListener);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setList(List<RankWeek> list) {
        this.list = list;
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

    public static class RankHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_week)
        TextView tvWeek;
        @BindView(R.id.tv_score)
        TextView tvScore;
        @BindView(R.id.tv_rank)
        TextView tvRank;
        @BindView(R.id.iv_update)
        ImageView ivUpdate;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        public RankHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnRankItemListener {
        void onUpdateItem(int position, RankWeek item);
        void onDeleteItem(int position, RankWeek item);
    }
}
