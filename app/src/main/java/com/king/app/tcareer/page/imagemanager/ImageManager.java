package com.king.app.tcareer.page.imagemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConfig;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.http.RequestCallback;
import com.king.app.tcareer.model.http.bean.ImageItemBean;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;
import com.king.app.tcareer.page.download.DownloadItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 从服务端下载、刷新、管理本地图片，弹出列表及后续动作
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/17 13:06
 */
public class ImageManager implements RequestCallback {

    private Context context;

    /**
     * Player list，下载图片/刷新头像/管理图片
     */
    protected DataController dataController;

    private OnActionListener onActionListener;

    private DataProvider dataProvider;

    private int position;
    private String flag;
    private String key;

    public ImageManager(Context context) {
        this.context = context;
        dataController = new DataController(this);
    }

    public DataController getDataController() {
        return dataController;
    }

    /**
     * 直接从服务端下载
     * @param flag
     * @param key
     */
    public void download(String flag, String key) {
        this.flag = flag;
        this.key = key;
        onItemClickDownload();
    }

    /**
     * 直接管理本地图片列表
     */
    public void manageLocal() {
        onItemClickManage();
    }

    /**
     * 显示从服务端下载、刷新、管理本地图片列表
     * @param title
     * @param position
     * @param flag
     * @param key
     */
    public void showOptions(String title, int position, String flag, String key) {
        this.position = position;
        this.flag = flag;
        this.key = key;

        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setTitle(title);
        dlg.setItems(context.getResources().getStringArray(R.array.cptdlg_item_oper)
                , itemListener);
        dlg.show();
    }

    DialogInterface.OnClickListener itemListener = new DialogInterface.OnClickListener() {

        private final int DOWNLOAD = 0;
        private final int REFRESH = 1;
        private final int MANAGE = 2;
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DOWNLOAD) {
                onItemClickDownload();
            }
            else if (which == REFRESH) {
                if (onActionListener != null) {
                    onActionListener.onRefresh(position);
                }
            }
            else if (which == MANAGE) {
                onItemClickManage();
            }
        }
    };

    /**
     * 设置事件回调
     * @param onActionListener
     */
    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    /**
     * 设置数据提供者
     * @param dataProvider
     */
    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    private void onItemClickDownload() {
        dataController.getImages(flag, key);
    }

    private void onItemClickManage() {

        dataController.showLocalImageDialog(onActionListener.getFragmentManager(), dataProvider.createImageUrlBean(dataController)
                , new ImageSelector.OnSelectorListener<String>() {
                    @Override
                    public void onSelectDone(List<String> list) {
                        dataController.deleteImages(list, true);
                        onActionListener.onManageFinished();
                    }
                });
    }

    @Override
    public void onServiceDisConnected() {
        Toast.makeText(context, R.string.gdb_server_offline, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestError() {
        Toast.makeText(context, R.string.gdb_request_fail, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onImagesReceived(final ImageUrlBean bean) {
        if (bean.getItemList() == null) {
            String text = context.getString(R.string.image_not_found);
            text = String.format(text, bean.getKey());
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
        else {
            // 直接下载更新
            if (bean.getItemList().size() == 1) {
                ImageItemBean itemBean = bean.getItemList().get(0);
                List<DownloadItem> list = new ArrayList<>();
                DownloadItem item = new DownloadItem();
                item.setKey(itemBean.getUrl());
                item.setFlag(itemBean.getKey());
                item.setSize(itemBean.getSize());
                item.setTargetPath(getSavePathFromFlag(itemBean.getKey(), bean.getKey()));

                String url = itemBean.getUrl();
                if (url.contains("/")) {
                    String[] array = url.split("/");
                    url = array[array.length - 1];
                }
                item.setName(url);

                list.add(item);

                startDownload(list);
            }
            // 显示对话框选择下载
            else {
                dataController.showHttpImageDialog(onActionListener.getFragmentManager(), bean
                        , new ImageSelector.OnSelectorListener<DownloadItem>() {
                            @Override
                            public void onSelectDone(List<DownloadItem> list) {
                                for (DownloadItem item:list) {
                                    item.setTargetPath(getSavePathFromFlag(item.getFlag(), bean.getKey()));
                                }
                                startDownload(list);
                            }
                        });
            }
        }
    }

    @Override
    public void onDownloadFinished() {
        if (onActionListener != null) {
            onActionListener.onDownloadFinished();
        }
    }

    private void startDownload(List<DownloadItem> list) {
        dataController.downloadImage(context, list, null, true);
    }

    /**
     * 根据flag设置默认保存目录
     * @param flag
     * @param key
     * @return
     */
    private String getSavePathFromFlag(String flag, String key) {
        if (flag.equals(Command.TYPE_IMG_PLAYER)) {
            File file = new File(AppConfig.IMG_PLAYER_BASE + key);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdir();
            }
            return file.getPath();
        }
        else if (flag.equals(Command.TYPE_IMG_PLAYER_HEAD)) {
            File file = new File(AppConfig.IMG_PLAYER_HEAD + key);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdir();
            }
            return file.getPath();
        }
        else if (flag.equals(Command.TYPE_IMG_MATCH)) {
            File file = new File(AppConfig.IMG_MATCH_BASE + key);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdir();
            }
            return file.getPath();
        }
        return null;
    }

    /**
     * 数据提供
     */
    public interface DataProvider {
        ImageUrlBean createImageUrlBean(DataController dataController);
    }

    /**
     * 事件回调
     */
    public interface OnActionListener {
        void onRefresh(int position);

        void onManageFinished();

        void onDownloadFinished();

        FragmentManager getFragmentManager();
    }
}
