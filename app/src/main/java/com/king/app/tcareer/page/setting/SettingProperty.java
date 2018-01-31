package com.king.app.tcareer.page.setting;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.king.app.tcareer.base.TApplication;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 14:40
 */
public class SettingProperty {

    /**
     * sort type of match mange page
     */
    public static final int VALUE_SORT_MATCH_WEEK = 0;
    public static final int VALUE_SORT_MATCH_NAME = 1;
    public static final int VALUE_SORT_MATCH_LEVEL = 2;

    /**
     * sort type of player mange page
     */
    public static final int VALUE_SORT_PLAYER_NAME = 0;
    public static final int VALUE_SORT_PLAYER_NAME_ENG = 1;
    public static final int VALUE_SORT_PLAYER_COUNTRY = 2;
    public static final int VALUE_SORT_PLAYER_AGE = 3;
    public static final int VALUE_SORT_PLAYER_CONSTELLATION = 4;

    private static final String getString(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TApplication.getInstance());
        return sp.getString(key, "");
    }

    private static final void setString(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static final int getInt(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TApplication.getInstance());
        return sp.getInt(key, -1);
    }

    private static final void setInt(String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private static final boolean getBoolean(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TApplication.getInstance());
        return sp.getBoolean(key, false);
    }

    private static final void setBoolean(String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setEnableFingerPrint(boolean enable) {
        setBoolean("enable_finger_print", enable);
    }

    public static boolean isEnableFingerPrint() {
        return getBoolean("enable_finger_print");
    }

    public static void setMatchManageGridMode(boolean mode) {
        setBoolean("key_match_manage_grid", mode);
    }

    public static boolean isMatchManageGridMode() {
        return getBoolean("key_match_manage_grid");
    }

    public static void setMatchSortMode(int mode) {
        setInt("key_sort_match", mode);
    }

    public static int getMatchSortMode() {
        return getInt("key_sort_match");
    }

    public static void setPlayerSortMode(int mode) {
        setInt("key_sort_player", mode);
    }

    public static int getPlayerSortMode() {
        return getInt("key_sort_player");
    }

    public static void setPlayerManageCardMode(boolean mode) {
        setBoolean("key_player_manage_card", mode);
    }

    public static boolean isPlayerManageCardMode() {
        return getBoolean("key_player_manage_card");
    }

}
