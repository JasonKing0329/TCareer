package com.king.app.tcareer.page.glory.gs;

import android.text.TextUtils;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterGloryGsYearItemBinding;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.glory.title.OnRecordItemListener;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/14 11:30
 */
public class GsYearAdapter extends BaseBindingAdapter<AdapterGloryGsYearItemBinding, GloryGsItem> implements View.OnClickListener {

    private OnRecordItemListener onRecordItemListener;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_glory_gs_year_item;
    }

    @Override
    protected void onBindItem(AdapterGloryGsYearItemBinding binding, int position, GloryGsItem item) {
        binding.tvYear.setText(String.valueOf(item.getYear()));
        binding.tvAo.setText(TextUtils.isEmpty(item.getAo()) ? "--":item.getAo());
        binding.tvFo.setText(TextUtils.isEmpty(item.getFo()) ? "--":item.getFo());
        binding.tvWo.setText(TextUtils.isEmpty(item.getWo()) ? "--":item.getWo());
        binding.tvUo.setText(TextUtils.isEmpty(item.getUo()) ? "--":item.getUo());

        binding.tvAo.setTag(item.getRecordAo());
        binding.tvAo.setOnClickListener(this);
        binding.tvFo.setTag(item.getRecordFo());
        binding.tvFo.setOnClickListener(this);
        binding.tvWo.setTag(item.getRecordWo());
        binding.tvWo.setOnClickListener(this);
        binding.tvUo.setTag(item.getRecordUo());
        binding.tvUo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (onRecordItemListener != null) {
            if (v.getTag() != null) {
                Record record = (Record) v.getTag();
                onRecordItemListener.onClickRecord(record);
            }
        }
    }

    public void setOnRecordItemListener(OnRecordItemListener onRecordItemListener) {
        this.onRecordItemListener = onRecordItemListener;
    }
}
