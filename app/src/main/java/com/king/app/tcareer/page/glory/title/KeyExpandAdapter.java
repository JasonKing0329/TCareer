package com.king.app.tcareer.page.glory.title;

import android.support.annotation.NonNull;

import com.zaihuishou.expandablerecycleradapter.adapter.BaseExpandableAdapter;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractAdapterItem;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/21 16:07
 */
public class KeyExpandAdapter extends BaseExpandableAdapter {

    private final int ITEM_TYPE_HEAD = 1;
    private final int ITEM_TYPE_ITEM = 2;

    private OnRecordItemListener onRecordItemListener;

    /**
     *
     * @param data
     * @param onRecordItemListener
     */
    protected KeyExpandAdapter(List<HeaderItem> data, OnRecordItemListener onRecordItemListener) {
        super(data);
        this.onRecordItemListener = onRecordItemListener;
    }

    @NonNull
    @Override
    public AbstractAdapterItem<Object> getItemView(Object type) {
        int itemType = (int) type;
        switch (itemType) {
            case ITEM_TYPE_HEAD:
                return new HeaderAdapter();
            case ITEM_TYPE_ITEM:
                return new SubItemAdapter(onRecordItemListener);
        }
        return null;
    }

    @Override
    public Object getItemViewType(Object t) {
        if (t instanceof HeaderItem) {
            return ITEM_TYPE_HEAD;
        }
        else if (t instanceof SubItem) {
            return ITEM_TYPE_ITEM;
        }
        return -1;
    }

}
