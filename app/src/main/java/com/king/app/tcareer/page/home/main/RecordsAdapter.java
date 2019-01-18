package com.king.app.tcareer.page.home.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.BubbleImageView;
import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/18 11:08
 */
public class RecordsAdapter extends BaseRecyclerAdapter<RecordsAdapter.RecordHolder, ComplexRecord> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_home_complex_records;
    }

    @Override
    protected RecordHolder newViewHolder(View view) {
        return new RecordHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordHolder holder, int position) {

        holder.tvMatchName.setText(getItem(position).getMatchName());
        holder.tvRound.setText(getItem(position).getMatchRound());
        holder.tvScore.setText(getItem(position).getScore());
        holder.tvWinner.setText(getItem(position).getWinner());
        holder.tvLoser.setText(getItem(position).getLoser());
        holder.tvRound.setText(getItem(position).getMatchRound());
        holder.tvRound.setText(getItem(position).getMatchRound());
        Glide.with(holder.ivMatch.getContext())
                .asBitmap()
                .load(getItem(position).getImgUrl())
                .apply(GlideOptions.getDefaultMatchOptions())
                .into(holder.ivMatch);
    }

    public static class RecordHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_match)
        BubbleImageView ivMatch;
        @BindView(R.id.tv_match_name)
        TextView tvMatchName;
        @BindView(R.id.tv_round)
        TextView tvRound;
        @BindView(R.id.tv_winner)
        TextView tvWinner;
        @BindView(R.id.tv_def)
        TextView tvDef;
        @BindView(R.id.tv_loser)
        TextView tvLoser;
        @BindView(R.id.tv_score)
        TextView tvScore;

        public RecordHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
