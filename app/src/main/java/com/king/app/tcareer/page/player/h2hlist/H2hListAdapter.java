package com.king.app.tcareer.page.player.h2hlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.widget.SideBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/21 16:07
 */
public class H2hListAdapter extends RecyclerView.Adapter<H2hListAdapter.ParentHolder> implements View.OnClickListener {

    private List<H2hBean> data;
    private OnItemMenuListener onItemMenuListener;

    // only random image path for the first time
    private Map<String, String> imagePathMap;

    private int colorWin;
    private int colorLose;
    private int colorTie;

    private Map<String, Integer> indexMap;

    protected H2hListAdapter(Context context, List<H2hBean> data, OnItemMenuListener onItemMenuListener) {
        this.data = data;
        this.onItemMenuListener = onItemMenuListener;
        imagePathMap = new HashMap<>();
        indexMap = new HashMap<>();
        colorWin = context.getResources().getColor(R.color.h2hlist_color_win);
        colorLose = context.getResources().getColor(R.color.h2hlist_color_lose);
        colorTie = context.getResources().getColor(R.color.h2hlist_color_tie);
    }

    @Override
    public ParentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ParentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_h2hlist_group, parent, false));
    }

    @Override
    public void onBindViewHolder(ParentHolder holder, final int position) {
        H2hBean bean = data.get(position);
        holder.tvName.setText(bean.getCompetitor().getNameChn());
        holder.tvCountry.setText(bean.getCompetitor().getCountry());
        holder.tvWin.setText(String.valueOf(bean.getWin()));
        holder.tvLose.setText(String.valueOf(bean.getLose()));
        if (bean.getWin() > bean.getLose()) {
            holder.tvWin.setTextColor(colorWin);
            holder.tvLose.setTextColor(colorLose);
        }
        else if (bean.getWin() < bean.getLose()) {
            holder.tvWin.setTextColor(colorLose);
            holder.tvLose.setTextColor(colorWin);
        }
        else {
            holder.tvWin.setTextColor(colorTie);
            holder.tvLose.setTextColor(colorTie);
        }

        if (imagePathMap.get(bean.getCompetitor().getNameChn()) == null) {
            String path = ImageProvider.getDetailPlayerPath(bean.getCompetitor().getNameChn());
            imagePathMap.put(bean.getCompetitor().getNameChn(), path);
        }
        Glide.with(holder.ivPlayer.getContext())
                .load("file://" + imagePathMap.get(bean.getCompetitor().getNameChn()))
                .apply(GlideOptions.getCommonOptions())
                .into(holder.ivPlayer);

        holder.groupRecord.setTag(bean);
        holder.groupRecord.setOnClickListener(this);

        //
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.groupCard.getLayoutParams();
        if (position == getItemCount() - 1) {
            params.bottomMargin = ScreenUtils.dp2px(40);
        }
        else {
            params.bottomMargin = 0;
        }
        holder.groupCard.setLayoutParams(params);
    }

    public int[] getWinLose() {
        int[] result = new int[2];
        for (int i = 0; i < data.size(); i ++) {
            result[0] += data.get(i).getWin();
            result[1] += data.get(i).getLose();
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public void onClick(View v) {
        H2hBean bean = (H2hBean) v.getTag();
        onItemMenuListener.onItemClicked(v, bean);
    }

    public void updateData(List<H2hBean> headerList) {
        this.data = headerList;
    }

    /**
     * data must be already sorted by pinyin
     * @param sideBar
     */
    public void updateSideBar(SideBar sideBar) {
        sideBar.clear();
        indexMap.clear();
        if (data.size() > 0) {
            for (int i = 0; i < data.size(); i ++) {
                H2hBean bean = data.get(i);
                String key;
                CompetitorBean pb = bean.getCompetitor();
                if (pb == null || TextUtils.isEmpty(pb.getNamePinyin())) {
                    key = "#";
                }
                else {
                    key = String.valueOf(pb.getNamePinyin().charAt(0)).toUpperCase();
                }
                if (indexMap.get(key) == null) {
                    sideBar.addIndex(key);
                    indexMap.put(key, i);
                }
            }
        }
        sideBar.invalidate();
    }

    public int getIndexPosition(String letter) {
        return indexMap.get(letter);
    }

    public static class ParentHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_player)
        ImageView ivPlayer;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_country)
        TextView tvCountry;
        @BindView(R.id.tv_win)
        TextView tvWin;
        @BindView(R.id.tv_lose)
        TextView tvLose;
        @BindView(R.id.group_record)
        LinearLayout groupRecord;
        @BindView(R.id.group_card)
        ViewGroup groupCard;
        
        public ParentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
