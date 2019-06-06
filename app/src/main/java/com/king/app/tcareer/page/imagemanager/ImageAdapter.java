package com.king.app.tcareer.page.imagemanager;

import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.HeadChildBindingAdapter;
import com.king.app.tcareer.databinding.AdapterImageSelectorGridBinding;
import com.king.app.tcareer.databinding.AdapterImageSelectorHeadBinding;
import com.king.app.tcareer.model.http.bean.ImageItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/6 13:42
 */
public class ImageAdapter extends HeadChildBindingAdapter<AdapterImageSelectorHeadBinding, AdapterImageSelectorGridBinding
        , GroupBean, ItemBean> {

    @Override
    protected Class getItemClass() {
        return ItemBean.class;
    }

    @Override
    protected int getHeaderRes() {
        return R.layout.adapter_image_selector_head;
    }

    @Override
    protected int getItemRes() {
        return R.layout.adapter_image_selector_grid;
    }

    @Override
    protected void onBindHead(AdapterImageSelectorHeadBinding binding, int position, GroupBean head) {
        binding.setBean(head);
    }

    @Override
    protected void onBindItem(AdapterImageSelectorGridBinding binding, int position, ItemBean item) {
        binding.setBean(item);
    }

    @Override
    protected void onClickItem(View view, int position, ItemBean data) {
        data.setCheck(!data.isCheck());
    }

    public List<ImageItemBean> getSelectedKey() {
        List<ImageItemBean> result = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i ++) {
            if (list.get(i) instanceof ItemBean) {
                ItemBean bean = (ItemBean) list.get(i);
                if (bean.isCheck()) {
                    result.add(bean.getBean());
                }
            }
        }
        return result;
    }

    public int getSpanSize(int position, int column) {
        if (isHead(position)) {
            return column;
        }
        else {
            return 1;
        }

    }
}
