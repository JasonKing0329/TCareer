package com.king.app.tcareer.page.match.page;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述: title + items
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/21 9:34
 */
public class PageRecordAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private final int TYPE_TITLE = 1;
    private final int TYPE_RECORD = 0;

    private final User user;

    private List<Object> list;

    private RequestOptions requestOptions;

    private OnItemClickListener onItemClickListener;

    /**
     * 保存首次从文件夹加载的图片序号
     */
    private Map<String, Integer> imageIndexMap;

    public PageRecordAdapter(User user, List<Object> list) {
        this.user = user;
        this.list = list;
        requestOptions = GlideOptions.getDefaultMatchOptions();
        imageIndexMap = new HashMap<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof PageTitleBean) {
            return TYPE_TITLE;
        }
        return TYPE_RECORD;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TITLE) {
            return new TitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_match_page_group_title, parent, false));
        }
        return new RecordHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_match_page_record_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleHolder) {
            onBindTitle((TitleHolder) holder, (PageTitleBean) list.get(position));
        } else {
            onBindRecord((RecordHolder) holder, (Record) list.get(position));
        }
    }

    private void onBindTitle(TitleHolder holder, PageTitleBean pageTitleBean) {
        holder.tvTitle.setText(String.valueOf(pageTitleBean.getYear()));
        holder.ivCup.setVisibility(pageTitleBean.isWinner() ? View.VISIBLE:View.GONE);
    }

    private void onBindRecord(RecordHolder holder, Record record) {
        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);
        holder.tvLevel.setText(AppConstants.getMasterGloryForRound(record.getRound()));
        holder.tvLine2.setText(competitor.getNameChn() + " " + record.getRankCpt() + "/" + record.getSeedpCpt());
        GradientDrawable drawable = (GradientDrawable) holder.tvLevel.getBackground();
        String winner = record.getUser().getNameShort();
        if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
            if (record.getRound().equals(AppConstants.RECORD_MATCH_ROUNDS[0])) {
                drawable.setColor(holder.tvLevel.getResources().getColor(R.color.colorAccent));
            }
            else {
                drawable.setColor(holder.tvLevel.getResources().getColor(R.color.match_timeline));
            }
        }
        else {
            winner = competitor.getNameChn();
            if (competitor instanceof User) {
                winner = ((User) competitor).getNameShort();
            }
            drawable.setColor(holder.tvLevel.getResources().getColor(R.color.match_timeline));
        }
        holder.tvLevel.setBackground(drawable);
        holder.tvLine3.setText(winner + " " + ScoreParser.getScoreText(record.getScoreList(), record.getWinnerFlag(), record.getRetireFlag()));

        String filePath;
        if (imageIndexMap.get(record.getCompetitor()) == null) {
            filePath = ImageProvider.getPlayerHeadPath(competitor.getNameChn(), imageIndexMap);
        }
        else {
            filePath = ImageProvider.getPlayerHeadPath(competitor.getNameChn(), imageIndexMap.get(competitor.getNameChn()));
        }
        Glide.with(holder.ivPlayer.getContext())
                .load(filePath)
                .apply(requestOptions)
                .into(holder.ivPlayer);

        holder.groupCard.setOnClickListener(this);
        holder.groupCard.setTag(record);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onClickRecord((Record) v.getTag());
        }
    }

    public static class TitleHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.iv_cup)
        ImageView ivCup;

        public TitleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class RecordHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.group_card)
        ViewGroup groupCard;
        @BindView(R.id.iv_player)
        ImageView ivPlayer;
        @BindView(R.id.tv_level)
        TextView tvLevel;
        @BindView(R.id.tv_line2)
        TextView tvLine2;
        @BindView(R.id.tv_line3)
        TextView tvLine3;

        public RecordHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onClickRecord(Record record);
    }
}
