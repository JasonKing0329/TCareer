package com.king.app.tcareer.page.match;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.view.widget.CircleImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/1 14:32
 */
public class MatchItemAdapter extends RecyclerView.Adapter<MatchItemAdapter.MatchHolder> {

    private List<Record> list;

    private RequestOptions playerOptions;

    /**
     * 保存首次从文件夹加载的图片序号
     */
    private Map<String, Integer> imageIndexMap;

    public MatchItemAdapter(List<Record> list) {
        this.list = list;
        imageIndexMap = new HashMap<>();
        playerOptions = GlideOptions.getDefaultPlayerOptions();
    }

    @Override
    public MatchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MatchHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_match_dialog_list, parent, false));
    }

    @Override
    public void onBindViewHolder(MatchHolder holder, int position) {

        Record record = list.get(position);
        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);

        String winner;
        holder.tvPlayer.setText(competitor.getNameChn());
        holder.tvLine1.setText("(" + record.getRankCpt() + "/" + record.getSeedpCpt() + ")  "
                + competitor.getCountry());

        // round
        for (int i = 0; i < AppConstants.RECORD_MATCH_ROUNDS.length; i ++) {
            if (record.getRound().equals(AppConstants.RECORD_MATCH_ROUNDS[i])) {
                holder.tvRound.setText(AppConstants.RECORD_MATCH_ROUNDS_SHORT[i]);
                break;
            }
        }

        // winner and score
        if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
            winner = competitor.getNameChn();
            if (competitor instanceof User) {
                winner = ((User) competitor).getNameShort();
            }
        }
        else {
            winner = record.getUser().getNameShort();
        }
        holder.tvScore.setText(winner + " " + ScoreParser.getScoreText(record.getScoreList(), record.getWinnerFlag(), record.getRetireFlag()));

        // image
        String filePath;
        if (imageIndexMap.get(competitor.getNameChn()) == null) {
            filePath = ImageProvider.getPlayerHeadPath(competitor.getNameChn(), imageIndexMap);
        }
        else {
            filePath = ImageProvider.getPlayerHeadPath(competitor.getNameChn(), imageIndexMap.get(competitor.getNameChn()));
        }

        Glide.with(holder.ivPlayer.getContext())
                .load(filePath)
                .apply(playerOptions)
                .into(holder.ivPlayer);

    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public static class MatchHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_player)
        CircleImageView ivPlayer;
        @BindView(R.id.tv_round)
        TextView tvRound;
        @BindView(R.id.tv_player)
        TextView tvPlayer;
        @BindView(R.id.tv_line1)
        TextView tvLine1;
        @BindView(R.id.tv_score)
        TextView tvScore;

        public MatchHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
