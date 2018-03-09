package com.king.app.tcareer.page.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.view.widget.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 9:19
 */
public class NotifyRankAdapter extends RecyclerView.Adapter<NotifyRankAdapter.UserHolder> implements View.OnClickListener {

    private List<NotifyRankBean> list;

    private SimpleDateFormat dateFormat;

    private OnItemListener onItemListener;

    public NotifyRankAdapter() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_notify_rank, parent, false));
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        Glide.with(holder.ivUser.getContext())
                .load(ImageProvider.getPlayerHeadPath(list.get(position).getUser().getNameChn()))
                .apply(GlideOptions.getDefaultPlayerOptions())
                .into(holder.ivUser);
        holder.tvDate.setText(dateFormat.format(list.get(position).getLastRank().getDate()));
        holder.groupItem.setTag(position);
        holder.groupItem.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        onItemListener.onClickItem(list.get(position), position);

    }

    public void setList(List<NotifyRankBean> list) {
        this.list = list;
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public static class UserHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user)
        CircleImageView ivUser;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.group_item)
        LinearLayout groupItem;

        public UserHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemListener {
        void onClickItem(NotifyRankBean bean, int position);
    }
}
