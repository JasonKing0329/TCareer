package com.king.app.tcareer.page.record.editor;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.tcareer.R;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;
import com.king.app.tcareer.view.widget.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 14:13
 */
public class RecentMatchAdapter extends BaseRecyclerAdapter<RecentMatchAdapter.MatchHolder, MatchNameBean> {

    private RequestOptions matchOptions;

    public RecentMatchAdapter() {
        matchOptions = GlideOptions.getDefaultMatchOptions();
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_editor_recent;
    }

    @Override
    protected MatchHolder newViewHolder(View view) {
        return new MatchHolder(view);
    }

    @Override
    public void onBindViewHolder(MatchHolder holder, int position) {
        Glide.with(holder.ivMatch.getContext())
                .load(ImageProvider.getMatchHeadPath(list.get(position).getName(), list.get(position).getMatchBean().getCourt()))
                .apply(matchOptions)
                .into(holder.ivMatch);
        holder.tvMatch.setText(list.get(position).getName());
    }

    public static class MatchHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_match)
        CircleImageView ivMatch;
        @BindView(R.id.tv_match)
        TextView tvMatch;

        public MatchHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
