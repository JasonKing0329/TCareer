package com.king.app.tcareer.page.imagemanager;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.king.app.tcareer.conf.AppConfig;
import com.king.app.tcareer.model.http.AppHttpClient;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.http.KHCareerHttpClient;
import com.king.app.tcareer.model.http.RequestCallback;
import com.king.app.tcareer.model.http.bean.GdbRespBean;
import com.king.app.tcareer.model.http.bean.ImageItemBean;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;
import com.king.app.tcareer.page.download.DownloadDialog;
import com.king.app.tcareer.page.download.DownloadItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/9.
 */
public class DataController {

    private RequestCallback mCallback;
    public DataController(RequestCallback callback) {
        mCallback = callback;
    }

    /**
     * 从服务端获取key有哪些图片，将通过mCallback反馈所有图片的URL
     * @param flag 图片类型
     * @param key 图片关键字
     */
    public void getImages(final String flag, final String key) {
        AppHttpClient.getInstance().getAppService().isServerOnline()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GdbRespBean>() {
                    @Override
                    public void accept(GdbRespBean gdbRespBean) throws Exception {
                        if (gdbRespBean.isOnline()) {
                            requestGetImages(flag, key);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mCallback.onServiceDisConnected();
                    }
                });

    }

    /**
     * 从服务端获取key有哪些图片，将通过mCallback反馈所有图片的URL
     * @param flag
     * @param key
     */
    private void requestGetImages(String flag, String key) {
        KHCareerHttpClient.getInstance().getService().getImages(flag, key)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ImageUrlBean>() {
                    @Override
                    public void accept(ImageUrlBean bean) throws Exception {
                        mCallback.onImagesReceived(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mCallback.onRequestError();
                    }
                });
    }

    /**
     * 启动下载器进行图片下载
     * @param context
     * @param itemList 要下载的图片url列表
     * @param savePath 保存的路径，如果为null，则从DownloadItem里的flag进行判断
     * @param noOption 是否提示有多少需要下载
     */
    public void downloadImage(Context context, final List<DownloadItem> itemList, final String savePath, final boolean noOption) {

        DownloadDialog downloadDialog = new DownloadDialog();
        downloadDialog.setDownloadList(itemList);
        downloadDialog.setSavePath(savePath);
        downloadDialog.setStartNoOption(noOption);
        downloadDialog.setOnDownloadListener(new DownloadDialog.OnDownloadListener() {
            @Override
            public void onDownloadFinish(DownloadItem item) {
                mCallback.onDownloadFinished();
            }

            @Override
            public void onDownloadFinish(List<DownloadItem> downloadList) {
                mCallback.onDownloadFinished();
            }
        });
        if (context instanceof FragmentActivity) {
            downloadDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "DownloadDialog");
        }
    }

    /**
     * 显示网络图片浏览框
     * @param fragmentManager
     * @param bean
     * @param listener
     */
    public void showHttpImageDialog(FragmentManager fragmentManager, ImageUrlBean bean
            , ImageSelector.OnSelectorListener<DownloadItem> listener) {
        ImageSelectorHttp dialog = new ImageSelectorHttp();
        dialog.setImageUrlBean(bean);
        dialog.setDialogTitle(bean.getKey());
        dialog.setOnSelectorListener(listener);
        dialog.show(fragmentManager, "ImageSelectorHttp");
    }

    /**
     * 显示本地图片浏览框
     * @param fragmentManager
     * @param bean
     * @param listener
     */
    public void showLocalImageDialog(FragmentManager fragmentManager, ImageUrlBean bean
            , ImageSelector.OnSelectorListener<String> listener) {
        ImageSelectorLocal dialog = new ImageSelectorLocal();
        dialog.setImageUrlBean(bean);
        dialog.setDialogTitle(bean.getKey());
        dialog.setOnSelectorListener(listener);
        dialog.show(fragmentManager, "ImageSelectorLocal");
    }

    /**
     * 删除指定图片文件
     * @param list
     * @param deleteParentWhileEmpty 删除文件后如果父目录下再无其他文件，则删除父目录
     */
    public void deleteImages(List<String> list, boolean deleteParentWhileEmpty) {
        for (int i = 0; i < list.size(); i ++) {
            File file = new File(list.get(i));
            if (file.exists()) {
                file.delete();

                if (deleteParentWhileEmpty) {
                    File parent = file.getParentFile();
                    if (parent.exists() && parent.list().length == 0) {
                        parent.delete();
                    }
                }
            }
        }
    }

    /**
     * 获取本地match对应的图片
     * @param match
     * @return
     */
    public ImageUrlBean getMatchImageUrlBean(String match) {
        String[] folders = new String[]{AppConfig.IMG_MATCH_BASE};
        String[] cmdTypes = new String[]{Command.TYPE_IMG_MATCH};
        return createImageUrlBean(match, folders, cmdTypes);
    }

    /**
     * 获取本地player对应的图片
     * @param player
     * @return
     */
    public ImageUrlBean getPlayerImageUrlBean(String player) {
        String[] folders = new String[]{AppConfig.IMG_PLAYER_BASE, AppConfig.IMG_PLAYER_HEAD};
        String[] cmdTypes = new String[]{Command.TYPE_IMG_PLAYER, Command.TYPE_IMG_PLAYER_HEAD};
        return createImageUrlBean(player, folders, cmdTypes);
    }

    /**
     * key对应的本地图片包括basePath目录下的单张jpg图片，和basePath下以key为文件夹里的所有图片
     * @param key
     * @param basePaths
     * @return
     */
    private ImageUrlBean createImageUrlBean(String key, String[] basePaths, String[] cmdTypes) {
        ImageUrlBean bean = new ImageUrlBean();
        bean.setKey(key);
        bean.setItemList(new ArrayList<ImageItemBean>());

        for (int i = 0; i < basePaths.length; i ++) {
            String basePath = basePaths[i];
            // 先检查单张图片
            File file = new File(basePath + key + ".jpg");
            if (file.exists()) {
                ImageItemBean itemBean = new ImageItemBean();
                itemBean.setKey(cmdTypes[i]);
                itemBean.setUrl(file.getPath());
                itemBean.setSize(file.length());
                bean.getItemList().add(itemBean);
            }
            // 检查文件夹中的图片
            file = new File(basePath + key);
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f:files) {
                    ImageItemBean itemBean = new ImageItemBean();
                    itemBean.setKey(cmdTypes[i]);
                    itemBean.setUrl(f.getPath());
                    itemBean.setSize(f.length());
                    bean.getItemList().add(itemBean);
                }
            }
        }
        return bean;
    }
}
