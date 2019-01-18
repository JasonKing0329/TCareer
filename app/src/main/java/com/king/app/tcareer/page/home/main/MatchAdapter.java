package com.king.app.tcareer.page.home.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/14 17:36
 */
public class MatchAdapter extends BaseRecyclerAdapter<MatchAdapter.MatchHolder, MatchNameBean> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_home_week_match;
    }

    @Override
    protected MatchHolder newViewHolder(View view) {
        return new MatchHolder(view);
    }

    @Override
    public void onBindViewHolder(MatchHolder holder, int position) {

        holder.tvName.setText(getItem(position).getName());
        MatchBean matchBean = getItem(position).getMatchBean();
        holder.tvPlace.setText(matchBean.getCountry() + "/" + matchBean.getCity());
        holder.tvType.setText(matchBean.getLevel() + "/" + matchBean.getCourt());

        String imagePath = ImageProvider.getMatchHeadPath(getItem(position).getName(), matchBean.getCourt());
        Glide.with(holder.ivMatch.getContext())
                .asBitmap()
                .load(imagePath)
                .apply(GlideOptions.getDefaultMatchOptions())
                .into(holder.ivMatch);

    }

    public static class MatchHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_match)
        ImageView ivMatch;
        @BindView(R.id.v_cover)
        TextView vCover;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_type)
        TextView tvType;
        @BindView(R.id.tv_place)
        TextView tvPlace;

        public MatchHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
