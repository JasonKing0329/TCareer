package com.king.app.tcareer.page.download;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.http.progress.ProgressListener;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/2 0002 23:12
 */

public class DownloadDialog extends DraggableDialogFragment implements IDownloadHolder {

    private DownloadFragment downloadFragment;

    private List<DownloadItem> downloadList;
    private boolean startNoOption;
    private String optionMessage;
    private String savePath;
    private OnDownloadListener onDownloadListener;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestCloseAction();
        setTitle("Download");
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        downloadFragment = new DownloadFragment();
        downloadFragment.setDownloadList(downloadList);
        downloadFragment.setSavePath(savePath);
        downloadFragment.setOptionMessage(optionMessage);
        downloadFragment.setStartNoOption(startNoOption);
        return downloadFragment;
    }

    public void setDownloadList(List<DownloadItem> downloadList) {
        this.downloadList = downloadList;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public void setStartNoOption(boolean startNoOption) {
        this.startNoOption = startNoOption;
    }

    public void setOptionMessage(String optionMessage) {
        this.optionMessage = optionMessage;
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    @Override
    public void exit() {
        dismissAllowingStateLoss();
    }

    public static class DownloadFragment extends ContentFragment {

        @BindView(R.id.download_empty)
        TextView tvEmpty;
        @BindView(R.id.download_list)
        RecyclerView rvDownload;
        Unbinder unbinder;

        private IDownloadHolder holder;

        private DownloadAdapter adapter;

        /**
         * 全部下载内容
         */
        private List<DownloadItemProxy> itemList;
        /**
         * 直接下载，不提示
         */
        private boolean startNoOption;
        /**
         * 不直接下载的提示内容
         */
        private String optionMessage;
        /**
         * 下载目录
         */
        private String savePath;

        private DownloadManager downloadManager;

        private OnDownloadListener onDownloadListener;
        private List<DownloadItem> downloadList;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_download;
        }

        @Override
        protected void onCreate(View view) {
            unbinder = ButterKnife.bind(this, view);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvDownload.setLayoutManager(layoutManager);

            itemList = new ArrayList<>();
            downloadManager = new DownloadManager(downloadCallback, 1);

            downloadManager.setSavePath(savePath);
            fillProxy(downloadList);

            updateDownloadList();
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {
            this.holder = (IDownloadHolder) holder;
        }

        private void fillProxy(List<DownloadItem> list) {
            itemList.clear();
            for (DownloadItem item:list) {
                DownloadItemProxy proxy = new DownloadItemProxy();
                proxy.setItem(item);
                proxy.setProgress(0);
                itemList.add(proxy);
            }
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }

        public void setDownloadList(List<DownloadItem> downloadList) {
            this.downloadList = downloadList;
        }

        public void setStartNoOption(boolean startNoOption) {
            this.startNoOption = startNoOption;
        }

        public void setOptionMessage(String optionMessage) {
            this.optionMessage = optionMessage;
        }

        public void setSavePath(String savePath) {
            this.savePath = savePath;
        }

        public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
            this.onDownloadListener = onDownloadListener;
        }

        private void updateDownloadList() {
            if (itemList.size() == 0) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvDownload.setVisibility(View.INVISIBLE);
            }
            else {
                if (startNoOption) {
                    showListAndStartDownload();
                }
                else {
                    new AlertDialog.Builder(getContext())
                            .setMessage(optionMessage)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showListAndStartDownload();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.exit();
                                }
                            })
                            .show();
                }
            }
        }

        private void showListAndStartDownload() {
            adapter = new DownloadAdapter(getContext(), itemList);
            rvDownload.setAdapter(adapter);
            startDownload();
        }

        private void startDownload() {
            for (int i = 0; i < itemList.size(); i ++) {
                final int index = i;
                downloadManager.downloadFile(itemList.get(i).getItem(), new ProgressListener() {
                    private int lastProgress;
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = (int)(100 * 1f * bytesRead / contentLength);
//                    DebugLog.e("progress:" + progress);

                        if (progress - lastProgress > 8 || done) {// 避免更新太过频繁
                            lastProgress = progress;
                            Bundle bundle = new Bundle();
                            bundle.putInt("index", index);
                            bundle.putInt("progress", progress);
                            Message message = new Message();
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                });
            }
        }

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                int index = bundle.getInt("index");
                int progress = bundle.getInt("progress");
                itemList.get(index).setProgress(progress);
                adapter.notifyDataSetChanged();
            }
        };

        DownloadCallback downloadCallback = new DownloadCallback() {
            @Override
            public void onDownloadFinish(DownloadItem item) {
                if (onDownloadListener != null) {

                    // 下载完成后才设置文件路径
                    item.setPath(savePath + "/" + item.getName());
                    onDownloadListener.onDownloadFinish(item);
                }
            }

            @Override
            public void onDownloadError(DownloadItem item) {

            }

            @Override
            public void onDownloadAllFinish() {
                if (onDownloadListener != null) {
                    for (DownloadItem item:downloadList) {
                        item.setPath(savePath + "/" + item.getName());
                    }
                    onDownloadListener.onDownloadFinish(downloadList);
                }
            }
        };

    }

    public interface OnDownloadListener {
        void onDownloadFinish(DownloadItem item);
        void onDownloadFinish(List<DownloadItem> downloadList);
    }

}
