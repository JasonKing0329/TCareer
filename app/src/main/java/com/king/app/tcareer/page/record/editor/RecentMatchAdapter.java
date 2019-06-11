package com.king.app.tcareer.page.record.editor;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterEditorRecentBinding;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.MatchNameBean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 14:13
 */
public class RecentMatchAdapter extends BaseBindingAdapter<AdapterEditorRecentBinding, MatchNameBean> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_editor_recent;
    }

    @Override
    protected void onBindItem(AdapterEditorRecentBinding binding, int position, MatchNameBean bean) {

        binding.setBean(bean);
        binding.setImageUrl(ImageProvider.getMatchHeadPath(list.get(position).getName(), list.get(position).getMatchBean().getCourt()));
    }
}
