package com.king.app.tcareer.page.glory.title;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述: 线性展开列表，只有一级
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/13 9:22
 */
public class SeqListAdapter extends RecyclerView.Adapter<SeqListAdapter.ItemHolder> implements View.OnClickListener {

    private List<Record> recordList;
    private OnRecordItemListener onRecordItemListener;
    private boolean showCompetitor;
    private boolean showTitle;
    private boolean hideSequence;
    private boolean showLose;
    private List<String> titleList;

    private RequestOptions matchOptions;
    private RequestOptions playerOptions;

    public SeqListAdapter(List<Record> recordList) {
        this.recordList = recordList;
        matchOptions = GlideOptions.getDefaultMatchOptions();
        playerOptions = GlideOptions.getDefaultPlayerOptions();
    }

    @Override
    public SeqListAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_glory_list_item, parent, false));
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public void setShowCompetitor(boolean showCompetitor) {
        this.showCompetitor = showCompetitor;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public void setShowLose(boolean showLose) {
        this.showLose = showLose;
    }

    public void setHideSequence(boolean hideSequence) {
        this.hideSequence = hideSequence;
    }

    public void setTitleList(List<String> list) {
        this.titleList = list;
    }

    @Override
    public void onBindViewHolder(SeqListAdapter.ItemHolder holder, int position) {
        Record record = recordList.get(position);
        holder.tvCity.setText(record.getMatch().getMatchBean().getCountry() + "/" + record.getMatch().getMatchBean().getCity());
        holder.tvLevel.setText(record.getMatch().getMatchBean().getLevel());
        holder.tvName.setText(record.getMatch().getName());
        holder.tvYear.setText(record.getDateStr());

        // list是倒序排列的
        if (hideSequence) {
            holder.tvSeq.setVisibility(View.GONE);
        }
        else {
            holder.tvSeq.setVisibility(View.VISIBLE);
            holder.tvSeq.setText(String.valueOf(getItemCount() - position));
        }

        Glide.with(holder.ivMatch.getContext())
                .load(ImageProvider.getMatchHeadPath(record.getMatch().getName(), record.getMatch().getMatchBean().getCourt()))
                .apply(matchOptions)
                .into(holder.ivMatch);

        holder.groupItem.setTag(position);
        holder.groupItem.setOnClickListener(this);

        if (showCompetitor) {
            CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);
            holder.groupCompetitor.setVisibility(View.VISIBLE);
            holder.tvCompetitor.setText(competitor.getNameChn() + "(" + competitor.getCountry() + ")");
            holder.tvScore.setText(ScoreParser.getScoreText(record.getScoreList(), record.getWinnerFlag(), record.getRetireFlag()));
            Glide.with(holder.ivCompetitor.getContext())
                    .load(ImageProvider.getPlayerHeadPath(competitor.getNameChn()))
                    .apply(playerOptions)
                    .into(holder.ivCompetitor);
        }
        else {
            holder.groupCompetitor.setVisibility(View.GONE);
        }

        if (showTitle) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(titleList.get(position));
        }
        else {
            holder.tvTitle.setVisibility(View.GONE);
        }

        if (showLose && record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
            holder.tvLose.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvLose.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return recordList == null ? 0 : recordList.size();
    }

    @Override
    public void onClick(View v) {
        if (onRecordItemListener != null) {
            int position = (int) v.getTag();
            onRecordItemListener.onClickRecord(recordList.get(position));
        }
    }

    public void setOnRecordItemListener(OnRecordItemListener onRecordItemListener) {
        this.onRecordItemListener = onRecordItemListener;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.group_item)
        ViewGroup groupItem;
        @BindView(R.id.group_competitor)
        ViewGroup groupCompetitor;
        @BindView(R.id.iv_match)
        RoundedImageView ivMatch;
        @BindView(R.id.iv_competitor)
        CircularImageView ivCompetitor;
        @BindView(R.id.tv_seq)
        TextView tvSeq;
        @BindView(R.id.tv_lose)
        TextView tvLose;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_level)
        TextView tvLevel;
        @BindView(R.id.tv_city)
        TextView tvCity;
        @BindView(R.id.tv_year)
        TextView tvYear;
        @BindView(R.id.tv_competitor)
        TextView tvCompetitor;
        @BindView(R.id.tv_score)
        TextView tvScore;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
