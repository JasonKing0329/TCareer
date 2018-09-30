package com.king.app.tcareer.page.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.model.db.entity.Retire;
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/30 8:58
 */
public class RetireAdapter extends BaseRecyclerAdapter<RetireAdapter.RetireHolder, Retire> {

    private SimpleDateFormat dateFormat;

    private OnRetireListener onRetireListener;

    public RetireAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void setOnRetireListener(OnRetireListener onRetireListener) {
        this.onRetireListener = onRetireListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_retire_item;
    }

    @Override
    protected RetireHolder newViewHolder(View view) {
        return new RetireHolder(view);
    }

    @Override
    public void onBindViewHolder(RetireHolder holder, int position) {
        Retire retire = list.get(position);
        if (retire.getRelieveId() > 0) {
            holder.tvType.setText("Relive");
        }
        else {
            holder.tvType.setText("Retire");
        }
        holder.tvDeclare.setText(dateFormat.format(retire.getDeclareDate()));
        holder.tvEffect.setText(dateFormat.format(retire.getEffectDate()));
        holder.ivDelete.setOnClickListener(v -> onRetireListener.onDeleteItem(list.get(position)));
    }

    public static class RetireHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_type)
        TextView tvType;
        @BindView(R.id.tv_declare)
        TextView tvDeclare;
        @BindView(R.id.tv_effect)
        TextView tvEffect;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        public RetireHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnRetireListener {
        void onDeleteItem(Retire retire);
    }
}
