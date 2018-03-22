package com.king.app.tcareer.page.player.page;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.BubbleImageView;
import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.MatchBean;
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
public class PageRecordAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

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

    public void setList(List<Object> list) {
        this.list = list;
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
            return new TitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_player_page_record_title, parent, false));
        }
        return new RecordHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_player_page_record_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleHolder) {
            onBindTitle((TitleHolder) holder, (PageTitleBean) list.get(position), position);
        } else {
            onBindRecord((RecordHolder) holder, (Record) list.get(position));
        }
    }

    private void onBindTitle(TitleHolder holder, PageTitleBean pageTitleBean, int position) {
        holder.tvTitle.setText(pageTitleBean.getYear() + " （" + pageTitleBean.getWin() + "胜" + pageTitleBean.getLose() + "负）");
    }

    private void onBindRecord(RecordHolder holder, Record record) {
        MatchBean matchBean = record.getMatch().getMatchBean();
        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);
        holder.tvLine1.setText(matchBean.getLevel() + "  " + record.getDateStr().split("-")[1] + "月  " + record.getRound());
        holder.tvLine2.setText(record.getMatch().getName() + "  " + matchBean.getCourt());
        if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
            holder.tvLine3.setText(user.getNameShort() + "  def.  " + record.getRankCpt() + "/" + record.getSeedpCpt());
            holder.tvLine3.setTextColor(holder.tvLine3.getResources().getColor(R.color.record_item_text_gray));
        }
        else {
            String winner = competitor.getNameChn();
            if (competitor instanceof User) {
                winner = ((User) competitor).getNameShort();
            }
            holder.tvLine3.setText(winner + " " + record.getRankCpt() + "/" + record.getSeedpCpt() + "  def.");
            holder.tvLine3.setTextColor(holder.tvLine3.getResources().getColor(R.color.red));
        }
        holder.tvLine4.setText(ScoreParser.getScoreText(record.getScoreList(), record.getWinnerFlag(), record.getRetireFlag()));

        String filePath;
        if (imageIndexMap.get(record.getMatch()) == null) {
            filePath = ImageProvider.getMatchHeadPath(record.getMatch().getName(), matchBean.getCourt(), imageIndexMap);
        }
        else {
            filePath = ImageProvider.getMatchHeadPath(record.getMatch().getName(), matchBean.getCourt(), imageIndexMap.get(record.getMatch()));
        }
        Glide.with(holder.ivMatch.getContext())
                .load(filePath)
                .apply(requestOptions)
                .into(holder.ivMatch);

        holder.groupCard.setTag(record);
        holder.groupCard.setOnClickListener(this);
        holder.groupCard.setOnLongClickListener(this);
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
            onItemClickListener.onClickRecord(v, (Record) v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onLongClickRecord(v, (Record) v.getTag());
        }
        return true;
    }

    public static class TitleHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView tvTitle;

        public TitleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class RecordHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.group_card)
        ViewGroup groupCard;
        @BindView(R.id.iv_match)
        BubbleImageView ivMatch;
        @BindView(R.id.tv_line1)
        TextView tvLine1;
        @BindView(R.id.tv_line2)
        TextView tvLine2;
        @BindView(R.id.tv_line3)
        TextView tvLine3;
        @BindView(R.id.tv_line4)
        TextView tvLine4;

        public RecordHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onClickRecord(View v, Record record);
        void onLongClickRecord(View view, Record record);
    }
}
