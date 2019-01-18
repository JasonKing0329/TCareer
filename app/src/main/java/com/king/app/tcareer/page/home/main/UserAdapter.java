package com.king.app.tcareer.page.home.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
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
public class UserAdapter extends BaseRecyclerAdapter<UserAdapter.UserHolder, User> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_home_user;
    }

    @Override
    protected UserHolder newViewHolder(View view) {
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {

        holder.tvUser.setText(getItem(position).getNameEng());

        String imagePath = ImageProvider.getDetailPlayerPath(getItem(position).getNameChn());
        Glide.with(holder.ivUser.getContext())
                .asBitmap()
                .load(imagePath)
                .apply(GlideOptions.getEditorPlayerOptions())
                .into(holder.ivUser);

    }

    public static class UserHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user)
        ImageView ivUser;
        @BindView(R.id.tv_user)
        TextView tvUser;

        public UserHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
