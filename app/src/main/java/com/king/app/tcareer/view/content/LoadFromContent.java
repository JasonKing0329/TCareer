package com.king.app.tcareer.view.content;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConfig;
import com.king.app.tcareer.databinding.AdapterItemLoadfromBinding;
import com.king.app.tcareer.databinding.FragmentContentLoadfromBinding;
import com.king.app.tcareer.utils.FileUtil;
import com.king.app.tcareer.view.dialog.AlertDialogFragment;
import com.king.app.tcareer.view.dialog.frame.FrameContentFragment;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/8 14:58
 */
public class LoadFromContent extends FrameContentFragment<FragmentContentLoadfromBinding, BaseViewModel> {

    private List<File> list;

    private ItemAdapter itemAdapter;

    private OnDatabaseChangedListener onDatabaseChangedListener;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_content_loadfrom;
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void onCreate(View view) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvList.setLayoutManager(manager);

        mBinding.tvConfirm.setOnClickListener(v -> onSave());
    }

    @Override
    protected void onCreateData() {
        File file = new File(AppConfig.HISTORY_BASE);
        list = Arrays.asList(file.listFiles());

        itemAdapter = new ItemAdapter();
        itemAdapter.setList(list);
        mBinding.rvList.setAdapter(itemAdapter);
    }

    private boolean onSave() {
        if (itemAdapter.getSelection() != -1) {
            final File file = list.get(itemAdapter.getSelection());
            new AlertDialogFragment()
                    .setMessage(getString(R.string.load_from_warning_msg))
                    .setPositiveText(getString(R.string.ok))
                    .setPositiveListener((dialogInterface, i) -> {
                        FileUtil.replaceDatabase(file);
                        if (onDatabaseChangedListener != null) {
                            onDatabaseChangedListener.onDatabaseChanged();
                        }
                        dismissAllowingStateLoss();
                    })
                    .setNegativeText(getString(R.string.cancel))
                    .show(getChildFragmentManager(), "AlertDialogFragment");
            return false;
        }
        return true;
    }

    public void setOnDatabaseChangedListener(OnDatabaseChangedListener onDatabaseChangedListener) {
        this.onDatabaseChangedListener = onDatabaseChangedListener;
    }

    private class ItemAdapter extends BaseBindingAdapter<AdapterItemLoadfromBinding, File> {

        private int selection = -1;

        public int getSelection() {
            return selection;
        }

        @Override
        protected int getItemLayoutRes() {
            return R.layout.adapter_item_loadfrom;
        }

        @Override
        protected void onBindItem(AdapterItemLoadfromBinding binding, int position, File bean) {
            binding.tvName.setText(bean.getName());
            if (position == selection) {
                binding.groupItem.setBackgroundColor(getResources().getColor(R.color.normal_court_clay));
            }
            else {
                binding.groupItem.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        }

        @Override
        protected void onClickItem(View v, int position) {
            int lastPosition = selection;
            selection = position;
            if (lastPosition != -1) {
                notifyItemChanged(lastPosition);
            }
            notifyItemChanged(selection);
        }
    }

    public interface OnDatabaseChangedListener {
        void onDatabaseChanged();
    }
}
