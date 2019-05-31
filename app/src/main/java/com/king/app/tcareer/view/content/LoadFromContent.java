package com.king.app.tcareer.view.content;

import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConfig;
import com.king.app.tcareer.utils.FileUtil;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.dialog.AlertDialogFragment;
import com.king.app.tcareer.view.dialog.CommonContentFragment;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/8 14:58
 */
public class LoadFromContent extends CommonContentFragment {

    @BindView(R.id.rv_list)
    RecyclerView rvList;

    private List<File> list;

    private ItemAdapter itemAdapter;

    private OnDatabaseChangedListener onDatabaseChangedListener;

    @Override
    protected void customToolbar() {
        dialogHolder.requestOkAction();
        dialogHolder.requestCloseAction();
        dialogHolder.setTitle("Load from");
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_content_loadfrom;
    }

    @Override
    protected void onCreate(View view) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvList.setLayoutManager(manager);
    }

    @Override
    protected void onCreateData() {
        File file = new File(AppConfig.HISTORY_BASE);
        list = Arrays.asList(file.listFiles());

        itemAdapter = new ItemAdapter();
        rvList.setAdapter(itemAdapter);
    }

    @Override
    public int getMaxHeight() {
        return ScreenUtils.getScreenHeight(getActivity()) * 2 / 3;
    }

    @Override
    public boolean onSave() {
        if (itemAdapter.getSelection() != -1) {
            final File file = list.get(itemAdapter.getSelection());
            new AlertDialogFragment()
                    .setMessage(getString(R.string.load_from_warning_msg))
                    .setPositiveText(getString(R.string.ok))
                    .setPositiveListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FileUtil.replaceDatabase(file);
                            dialogHolder.dismiss();
                            if (onDatabaseChangedListener != null) {
                                onDatabaseChangedListener.onDatabaseChanged();
                            }
                        }
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

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {

        private int selection = -1;

        public int getSelection() {
            return selection;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_loadfrom, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, final int position) {
            holder.tvName.setText(list.get(position).getName());
            if (position == selection) {
                holder.groupItem.setBackgroundColor(getResources().getColor(R.color.normal_court_clay));
            }
            else {
                holder.groupItem.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            holder.groupItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int lastPosition = selection;
                    selection = position;
                    if (lastPosition != -1) {
                        notifyItemChanged(lastPosition);
                    }
                    notifyItemChanged(selection);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.group_item)
        ViewGroup groupItem;

        @BindView(R.id.tv_name)
        TextView tvName;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnDatabaseChangedListener {
        void onDatabaseChanged();
    }
}
