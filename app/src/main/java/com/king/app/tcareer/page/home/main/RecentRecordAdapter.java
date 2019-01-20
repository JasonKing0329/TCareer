package com.king.app.tcareer.page.home.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.view.adapter.HeadItemRecyclerAdapter;
import com.king.app.tcareer.view.widget.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentRecordAdapter extends HeadItemRecyclerAdapter<RecentRecordAdapter.RoundHolder, RecentRecordAdapter.ItemHolder, String, Record> {

    private RequestOptions playerOptions;

    public RecentRecordAdapter() {
        playerOptions = GlideOptions.getDefaultPlayerOptions();
    }

    @Override
    protected Class getHeaderClass() {
        return String.class;
    }

    @Override
    protected int getHeaderLayoutRes() {
        return R.layout.adapter_match_recent_round;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_match_recent_record;
    }

    @Override
    protected RoundHolder newHeaderHolder(View view) {
        return new RoundHolder(view);
    }

    @Override
    protected ItemHolder newItemHolder(View view) {
        return new ItemHolder(view);
    }

    @Override
    protected void onBindHeader(RoundHolder holder, int position, String bean) {
        holder.tvRound.setText(bean);
    }

    @Override
    protected void onBindItem(ItemHolder holder, int position, Record curRecord) {
        holder.groupRecord.setVisibility(View.VISIBLE);

        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(curRecord);
        holder.tvPlayer.setText(competitor.getNameChn());
        holder.tvRankSeed.setText("(".concat(String.valueOf(curRecord.getRankCpt())).concat("/")
                .concat(String.valueOf(curRecord.getSeedpCpt()).concat(")")));

        String score = ScoreParser.getScoreText(curRecord.getScoreList(), curRecord.getWinnerFlag(), curRecord.getRetireFlag());
        if (AppConstants.WINNER_USER == curRecord.getWinnerFlag()) {
            String winner = curRecord.getUser().getNameShort();
            holder.tvScore.setText(winner + "  " + score);
        }
        else {
            String winner = competitor instanceof User ? ((User) competitor).getNameShort():competitor.getNameChn();
            holder.tvScore.setText(winner + "  " + score);
        }

        String path = ImageProvider.getPlayerHeadPath(competitor.getNameChn());
        Glide.with(holder.ivPlayer.getContext())
                .load(path)
                .apply(playerOptions)
                .into(holder.ivPlayer);

        path = ImageProvider.getPlayerHeadPath(curRecord.getUser().getNameChn());
        Glide.with(holder.ivUser.getContext())
                .load(path)
                .apply(playerOptions)
                .into(holder.ivUser);
    }

    public static class RoundHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_round)
        TextView tvRound;

        public RoundHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user)
        CircleImageView ivUser;
        @BindView(R.id.iv_player)
        CircleImageView ivPlayer;
        @BindView(R.id.tv_player)
        TextView tvPlayer;
        @BindView(R.id.tv_rank_seed)
        TextView tvRankSeed;
        @BindView(R.id.tv_score)
        TextView tvScore;
        @BindView(R.id.group_record)
        LinearLayout groupRecord;
        @BindView(R.id.group_item)
        RelativeLayout groupItem;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
