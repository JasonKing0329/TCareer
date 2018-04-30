package com.king.app.tcareer.page.player.page;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;
import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/4/27 9:12
 */
public class FullRecordAdapter extends BaseRecyclerAdapter<FullRecordAdapter.RecordHolder, Object> implements View.OnClickListener {

    private User user;

    private Map<Integer, Palette.Swatch> swatchMap;

    private OnItemListener onItemListener;

    public FullRecordAdapter(User user) {
        this.user = user;
        swatchMap = new HashMap<>();
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_player_page_record_item_full;
    }

    @Override
    protected RecordHolder newViewHolder(View view) {
        return new RecordHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecordHolder holder, final int position) {
        FullRecordBean bean = (FullRecordBean) list.get(position);
        Record record = bean.record;

        if (bean.isYearFirst) {
            holder.tvYear.setVisibility(View.VISIBLE);
            holder.tvYear.setText(bean.year + "\n" + bean.yearWin + "-" + bean.yearLose);
        }
        else {
            holder.tvYear.setVisibility(View.GONE);
        }

        holder.tvName.setText(record.getMatch().getName());
        holder.tvRound.setText(record.getRound());
        int month = Integer.parseInt(record.getDateStr().split("-")[1]);
        holder.tvMonth.setText(month + "月");
        String winner;
        if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
            winner = user.getNameShort();
        }
        else {
            winner = "● " + CompetitorParser.getCompetitorFrom(record).getNameChn();
        }
        holder.tvScore.setText(winner + "  " + ScoreParser.getScoreText(record.getScoreList()
                , record.getWinnerFlag(), record.getRetireFlag()));

        String url = ImageProvider.getMatchHeadPath(record.getMatch().getName(), record.getMatch().getMatchBean().getCourt());
        Glide.with(holder.ivMatch.getContext())
                .load(url)
                .listener(GlidePalette.with(url)
                        .use(GlidePalette.Profile.MUTED_DARK)
                        .intoBackground(holder.tvScore)
                        .intoTextColor(holder.tvScore)
                        .intoTextColor(holder.tvYear)
                        .intoCallBack(new BitmapPalette.CallBack() {
                            @Override
                            public void onPaletteLoaded(@Nullable Palette palette) {
                                handlePallete(palette, holder, position);
                            }
                        }))
                .into(holder.ivMatch);

        holder.groupRecord.setTag(position);
        holder.groupRecord.setOnClickListener(this);
    }

    private void handlePallete(Palette palette, RecordHolder holder, int position) {
        Palette.Swatch swatch = getSuitableSwatch(palette);
        swatchMap.put(position, swatch);
        if (onItemListener != null) {
            onItemListener.onSwatchLoaded(position, swatch);
        }
        GradientDrawable drawable = (GradientDrawable) holder.tvYear.getBackground();
        if (swatch == null) {
            int bgColor = holder.tvYear.getResources().getColor(R.color.player_page_cover_bg);
            int textColor = holder.tvYear.getResources().getColor(R.color.player_page_cover_text);
            drawable.setColor(bgColor);
            holder.tvYear.setTextColor(textColor);
        }
        else {
            drawable.setColor(swatch.getRgb());
        }
    }

    private Palette.Swatch getSuitableSwatch(Palette palette) {
        Palette.Swatch swatch = palette.getDarkMutedSwatch();
        if (swatch == null) {
            swatch = palette.getMutedSwatch();
            if (swatch == null) {
                swatch = palette.getDarkVibrantSwatch();
                if (swatch == null) {
                    swatch = palette.getVibrantSwatch();
                }
            }
        }
        return swatch;
    }

    public Palette.Swatch getSwatch(int position) {
        return swatchMap.get(position);
    }

    public int getYear(int position) {
        FullRecordBean bean = (FullRecordBean) getItem(position);
        return Integer.parseInt(bean.record.getDateStr().split("-")[0]);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (onItemListener != null) {
            onItemListener.onClickRecord(v, ((FullRecordBean) getItem(position)).record);
        }
    }

    public interface OnItemListener {
        void onClickRecord(View v, Record record);
        void onSwatchLoaded(int position, Palette.Swatch swatch);
    }

    public static class RecordHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_match)
        ImageView ivMatch;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_round)
        TextView tvRound;
        @BindView(R.id.tv_year)
        TextView tvYear;
        @BindView(R.id.tv_month)
        TextView tvMonth;
        @BindView(R.id.tv_score)
        TextView tvScore;
        @BindView(R.id.group_record)
        ViewGroup groupRecord;

        public RecordHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
