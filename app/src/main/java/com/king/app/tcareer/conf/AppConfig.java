package com.king.app.tcareer.conf;

import android.os.Environment;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 14:18
 */
public class AppConfig {

    public static final String SDCARD = Environment.getExternalStorageDirectory().getPath();

    public static final String DEF_CONTENT = SDCARD + "/tcareer";

    public static final String EXPORT_BASE = DEF_CONTENT + "/export";
    public static final String HISTORY_BASE = DEF_CONTENT + "/history";
    public static final String DOWNLOAD_BASE = DEF_CONTENT + "/download";
    public static final String DOWNLOAD_IMAGE = DOWNLOAD_BASE + "/img";
    public static final String CONF_DIR = DEF_CONTENT + "/conf";
    public static final String TEMP_DIR = DEF_CONTENT + "/temp";
    public static final String APP_UPDATE_DIR = DEF_CONTENT + "/apk";

    // TODO 暂时沿用mytennis的图片目录
    public static final String BASE_IMG = SDCARD + "/mytennis";
    public static final String IMG_PLAYER_BASE = BASE_IMG + "/img_player/";
    public static final String IMG_MATCH_BASE = BASE_IMG + "/img_match/";
    public static final String IMG_BK_BASE = BASE_IMG + "/img_bk/";
    public static final String IMG_DEFAULT_BASE = BASE_IMG + "/img_default/";
    public static final String IMG_PLAYER_HEAD = BASE_IMG + "/img_player/head/";

    public static final String DEF_IMG_HARD = "hard.jpg";
    public static final String DEF_IMG_INNERHARD = "innerhard.jpg";
    public static final String DEF_IMG_CLAY = "clay.jpg";
    public static final String DEF_IMG_GRASS = "grass.jpg";

    public static final String[] DIRS = new String[] {
            DEF_CONTENT, EXPORT_BASE, HISTORY_BASE, DOWNLOAD_BASE, DOWNLOAD_IMAGE, CONF_DIR, APP_UPDATE_DIR
            , TEMP_DIR, BASE_IMG, IMG_PLAYER_BASE, IMG_MATCH_BASE, IMG_BK_BASE, IMG_DEFAULT_BASE, IMG_PLAYER_HEAD
    };

}
