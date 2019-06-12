package com.king.app.tcareer.page.glory.title;

import android.databinding.DataBindingUtil;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.databinding.AdapterGloryListItemBinding;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/21 10:05
 */
public class SubItemAdapter extends AbstractExpandableAdapterItem implements View.OnClickListener {

    private AdapterGloryListItemBinding binding;
    
    private SubItem subItem;

    private OnRecordItemListener onRecordItemListener;

    public SubItemAdapter(OnRecordItemListener onRecordItemListener) {
        this.onRecordItemListener = onRecordItemListener;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.adapter_glory_list_item;
    }

    @Override
    public void onBindViews(View root) {
        binding = DataBindingUtil.bind(root);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        super.onUpdateViews(model, position);
        subItem = (SubItem) model;
        binding.setBean(subItem.getRecord());
        binding.groupItem.setTag(position);
        binding.groupItem.setOnClickListener(this);
    }

    @Override
    public void onExpansionToggled(boolean expanded) {

    }

    @Override
    public void onClick(View v) {
        if (onRecordItemListener != null) {
            onRecordItemListener.onClickRecord(subItem.getRecord().getRecord());
        }
    }
}
