package com.king.app.tcareer.page.glory.gs;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterGloryAtp1000YearItemBinding;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.glory.title.OnRecordItemListener;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/14 11:30
 */
public class MasterYearAdapter extends BaseBindingAdapter<AdapterGloryAtp1000YearItemBinding, GloryMasterItem> implements View.OnClickListener {

    private OnRecordItemListener onRecordItemListener;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_glory_atp1000_year_item;
    }

    @Override
    protected void onBindItem(AdapterGloryAtp1000YearItemBinding binding, int position, GloryMasterItem item) {
        binding.tvYear.setText(String.valueOf(item.getYear()));
        binding.tvIw.setText(TextUtils.isEmpty(item.getIw()) ? "--" : item.getIw());
        binding.tvMm.setText(TextUtils.isEmpty(item.getMiami()) ? "--" : item.getMiami());
        binding.tvMc.setText(TextUtils.isEmpty(item.getMc()) ? "--" : item.getMc());
        binding.tvMd.setText(TextUtils.isEmpty(item.getMadrid()) ? "--" : item.getMadrid());
        binding.tvRo.setText(TextUtils.isEmpty(item.getRoma()) ? "--" : item.getRoma());
        binding.tvRc.setText(TextUtils.isEmpty(item.getRc()) ? "--" : item.getRc());
        binding.tvCc.setText(TextUtils.isEmpty(item.getCicinati()) ? "--" : item.getCicinati());
        binding.tvSh.setText(TextUtils.isEmpty(item.getSh()) ? "--" : item.getSh());
        binding.tvPa.setText(TextUtils.isEmpty(item.getParis()) ? "--" : item.getParis());

        updateTextAppearance(binding.tvIw, item.getIw());
        updateTextAppearance(binding.tvMm, item.getMiami());
        updateTextAppearance(binding.tvMc, item.getMc());
        updateTextAppearance(binding.tvMd, item.getMadrid());
        updateTextAppearance(binding.tvRo, item.getRoma());
        updateTextAppearance(binding.tvRc, item.getRc());
        updateTextAppearance(binding.tvCc, item.getCicinati());
        updateTextAppearance(binding.tvSh, item.getSh());
        updateTextAppearance(binding.tvPa, item.getParis());

        binding.tvIw.setTag(item.getRecordIW());
        binding.tvIw.setOnClickListener(this);
        binding.tvMm.setTag(item.getRecordMiami());
        binding.tvMm.setOnClickListener(this);
        binding.tvMc.setTag(item.getRecordMC());
        binding.tvMc.setOnClickListener(this);
        binding.tvMd.setTag(item.getRecordMadrid());
        binding.tvMd.setOnClickListener(this);
        binding.tvRo.setTag(item.getRecordRoma());
        binding.tvRo.setOnClickListener(this);
        binding.tvRc.setTag(item.getRecordRC());
        binding.tvRc.setOnClickListener(this);
        binding.tvCc.setTag(item.getRecordCicinati());
        binding.tvCc.setOnClickListener(this);
        binding.tvSh.setTag(item.getRecordSH());
        binding.tvSh.setOnClickListener(this);
        binding.tvPa.setTag(item.getRecordParis());
        binding.tvPa.setOnClickListener(this);
    }

    private void updateTextAppearance(TextView textView, String result) {
        if ("W".equals(result)) {
            textView.setTextAppearance(R.style.TvMatchResultItemWinner);
        }
        else {
            textView.setTextAppearance(R.style.TvMatchResultItemNormal);
        }
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
