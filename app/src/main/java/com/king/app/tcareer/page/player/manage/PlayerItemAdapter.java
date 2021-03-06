package com.king.app.tcareer.page.player.manage;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.view.widget.CircleImageView;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/2/20 13:27
 */
@Deprecated
public class PlayerItemAdapter extends PlayerManageBaseAdapter {

    public PlayerItemAdapter(List<PlayerViewBean> list) {
        super(list);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_player_manage_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        PlayerItemAdapter.ItemHolder holder = (ItemHolder) h;
        PlayerViewBean bean = list.get(position);
        holder.tvIndex.setText(String.valueOf(position + 1));
        holder.tvName.setText(bean.getName());
        holder.tvNameEng.setText(bean.getNameEng() + "  " + bean.getCountry());
        // 当前排序是按星座排序，显示星座名称
        if (isSortByConstellation()) {
            holder.tvBirthday.setText(getConstellation(position));
        }
        else {
            holder.tvBirthday.setText(bean.getBirthday());
        }
        holder.tvCountry.setText(bean.getLose() + "-" + bean.getWin());

        holder.group.setTag(position);
        holder.group.setOnClickListener(this);

        // item 背景
        updateItemBackground(position, holder.container);

        // image view相关显示及事件
        updateItemImage(getPlayerPath(position), position, holder.image);

        // check状态
        updateCheckStatus(position, holder.check);

        String atpId = ((CompetitorBean) bean.getData()).getAtpId();
        if (TextUtils.isEmpty(atpId)) {
            holder.tvAtpId.setVisibility(View.GONE);
        }
        else {
            holder.tvAtpId.setText(atpId);
            holder.tvAtpId.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        ViewGroup container;
        ViewGroup group;
        TextView tvIndex;
        TextView tvName;
        TextView tvNameEng;
        TextView tvCountry;
        TextView tvBirthday;
        TextView tvAtpId;
        CheckBox check;
        CircleImageView image;
        public ItemHolder(View itemView) {
            super(itemView);
            container = (ViewGroup) itemView.findViewById(R.id.manage_item_container);
            group = (ViewGroup) itemView.findViewById(R.id.manage_item_group);
            tvIndex = (TextView) itemView.findViewById(R.id.manage_item_index);
            tvName = (TextView) itemView.findViewById(R.id.manage_item_name);
            tvCountry = (TextView) itemView.findViewById(R.id.manage_item_country);
            tvNameEng = (TextView) itemView.findViewById(R.id.manage_item_name_eng);
            tvBirthday = (TextView) itemView.findViewById(R.id.manage_item_birthday);
            tvAtpId = (TextView) itemView.findViewById(R.id.tv_atp_id);
            check = (CheckBox) itemView.findViewById(R.id.manage_item_check);
            image = (CircleImageView) itemView.findViewById(R.id.manage_item_image);
        }
    }
}
