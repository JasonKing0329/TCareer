package com.king.app.tcareer.model;

import android.graphics.Bitmap;

import com.king.app.tcareer.conf.AppConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Random;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 17:51
 */
public class ImageProvider {

    /**
     * 采用开源框架ImageLoader加载文件路径
     * @param name
     * @param court
     * @return
     */
    public static String getMatchHeadPath(String name, String court, int indexInFolder) {
        try {
            File file = new File(AppConfig.IMG_MATCH_BASE + name);
            name = file.listFiles()[indexInFolder].getPath();
        } catch (Exception e) {
            e.printStackTrace();
            name = getMatchHeadPath(name, court);
        }
        return name;
    }
    /**
     * 采用开源框架ImageLoader加载文件路径
     * @param name
     * @param court
     * @return
     */
    public static String getMatchHeadPath(String name, String court) {
        return getMatchHeadPath(name, court, null);
    }
    /**
     * 采用开源框架ImageLoader加载文件路径
     * @param name
     * @param court
     * @return
     */
    public static String getMatchHeadPath(String name, String court, Map<String, Integer> indexMap) {
        File file = new File(AppConfig.IMG_MATCH_BASE + name);
        // 存在文件夹，则随机显示里面的任何图片
        if (file.exists() && file.isDirectory()) {
            File files[] = file.listFiles();
            // 没有图片
            if (files == null || files.length == 0) {
                name = null;
            }
            else {
                if (files.length == 1) {
                    if (indexMap != null) {
                        indexMap.put(name, 0);
                    }
                    name = files[0].getPath();
                }
                else {
                    int index = Math.abs(new Random().nextInt()) % files.length;
                    if (indexMap != null) {
                        indexMap.put(name, index);
                    }
                    name = files[index].getPath();
                }
            }
        }
        else {
            name = AppConfig.IMG_MATCH_BASE + name + ".jpg";
            if (!new File(name).exists()) {
                name = null;
            }
        }
        if (name == null) {
            if (court.equals("硬地")) {
                name = AppConfig.IMG_DEFAULT_BASE + AppConfig.DEF_IMG_HARD;
            }
            else if (court.equals("红土")) {
                name = AppConfig.IMG_DEFAULT_BASE + AppConfig.DEF_IMG_CLAY;
            }
            else if (court.equals("室内硬地")) {
                name = AppConfig.IMG_DEFAULT_BASE + AppConfig.DEF_IMG_INNERHARD;
            }
            else if (court.equals("草地")) {
                name = AppConfig.IMG_DEFAULT_BASE + AppConfig.DEF_IMG_GRASS;
            }
        }
        return name;
    }

    /**
     * 采用开源框架ImageLoader加载文件路径（加载文件夹中指定序号的图片）
     * 调用该方法表示已有player对应的文件夹
     * @param name
     * @param indexInFolder
     * @return could be null
     */
    public static String getPlayerHeadPath(String name, int indexInFolder) {
        try {
            File file = new File(AppConfig.IMG_PLAYER_HEAD + name);
            name = file.listFiles()[indexInFolder].getPath();
        } catch (Exception e) {
            e.printStackTrace();
            name = getDetailPlayerPath(name);
        }
        return name;
    }
    /**
     * 采用开源框架ImageLoader加载文件路径
     * @param name
     * @return could be null
     */
    public static String getPlayerHeadPath(String name) {
        return getPlayerHeadPath(name, null);
    }

    /**
     * 采用开源框架ImageLoader加载文件路径
     * @param name
     * @param indexMap 如果存在文件夹，保存本次随机的序号
     * @return could be null
     */
    public static String getPlayerHeadPath(String name, Map<String, Integer> indexMap) {
        File file = new File(AppConfig.IMG_PLAYER_HEAD + name);
        // 存在文件夹，则随机显示里面的任何图片
        if (file.exists() && file.isDirectory()) {
            File files[] = file.listFiles();
            // 没有图片
            if (files == null || files.length == 0) {
                name = null;
            }
            else {
                if (files.length == 1) {
                    if (indexMap != null) {
                        indexMap.put(name, 0);
                    }
                    name = files[0].getPath();
                }
                else {
                    int index = Math.abs(new Random().nextInt()) % files.length;
                    if (indexMap != null) {
                        indexMap.put(name, index);
                    }
                    name = files[index].getPath();
                }
            }
        }
        else {
            // 只有单张图的情况
            name = AppConfig.IMG_PLAYER_HEAD + name+".jpg";
            // 没有图片
            if (!new File(name).exists()) {
                name = null;
            }
        }
        return name;
    }

    /**
     * 采用开源框架ImageLoader加载文件路径（加载文件夹中指定序号的图片）
     * 调用该方法表示已有player对应的文件夹
     * @param name
     * @param indexInFolder
     * @return
     */
    public static String getDetailPlayerPath(String name, int indexInFolder) {
        try {
            File file = new File(AppConfig.IMG_PLAYER_BASE + name);
            name = file.listFiles()[indexInFolder].getPath();
        } catch (Exception e) {
            e.printStackTrace();
            name = getDetailPlayerPath(name);
        }
        return name;
    }
    /**
     * 采用开源框架ImageLoader加载文件路径
     * @param name
     * @return
     */
    public static String getDetailPlayerPath(String name) {
        return getDetailPlayerPath(name, null);
    }
    /**
     * 采用开源框架ImageLoader加载文件路径
     * @param name
     * @param indexMap 如果存在文件夹，保存本次随机的序号
     * @return
     */
    public static String getDetailPlayerPath(String name, Map<String, Integer> indexMap) {
        File file = new File(AppConfig.IMG_PLAYER_BASE + name);
        // 存在文件夹，则随机显示里面的任何图片
        if (file.exists() && file.isDirectory()) {
            File files[] = file.listFiles();
            // 没有图片
            if (files == null || files.length == 0) {
                name = null;
            }
            else {
                if (files.length == 1) {
                    if (indexMap != null) {
                        indexMap.put(name, 0);
                    }
                    name = files[0].getPath();
                }
                else {
                    int index = Math.abs(new Random().nextInt()) % files.length;
                    if (indexMap != null) {
                        indexMap.put(name, index);
                    }
                    name = files[index].getPath();
                }
            }
        }
        else {
            // 只有单张图的情况
            name = AppConfig.IMG_PLAYER_BASE + name+".jpg";
            // 没有图片
            if (!new File(name).exists()) {
                name = null;
            }
        }
        return name;
    }

}
