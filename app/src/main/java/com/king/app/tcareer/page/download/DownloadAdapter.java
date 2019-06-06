package com.king.app.tcareer.page.download;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterDownloadListBinding;
import com.king.app.tcareer.utils.FileSizeUtil;

/**
 * Created by Administrator on 2016/9/2.
 */
public class DownloadAdapter extends BaseBindingAdapter<AdapterDownloadListBinding, DownloadItemProxy> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_download_list;
    }

    @Override
    protected void onBindItem(AdapterDownloadListBinding binding, int position, DownloadItemProxy bean) {
        binding.downloadItemName.setText(bean.getItem().getName());
        binding.downloadItemSize.setText(FileSizeUtil.convertFileSize(bean.getItem().getSize()));
        binding.downloadItemProgressbar.setProgress(bean.getProgress());
    }
}
