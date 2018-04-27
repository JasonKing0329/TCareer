package com.king.app.tcareer.page.setting;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.AutoFillMatchBean;

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
    public static final int VALUE_SORT_PLAYER_RECORD = 5;

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

    private static final int getInt(String key, int defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TApplication.getInstance());
        return sp.getInt(key, defaultValue);
    }

    private static final void setInt(String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private static final long getLong(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TApplication.getInstance());
        return sp.getLong(key, -1);
    }

    private static final void setLong(String key, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
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

    public static void setServerBaseUrl(String url) {
        setString("pref_http_server", url);
    }

    public static String getServerBaseUrl() {
        return getString("pref_http_server");
    }

    public static void setUserId(long userId) {
        setLong("key_user_id", userId);
    }

    public static long getUserId() {
        return getLong("key_user_id");
    }

    public static void setAutoFillMatch(AutoFillMatchBean bean) {

        setString("auto_fill_match", new Gson().toJson(bean));
    }

    public static AutoFillMatchBean getAutoFillMatch() {
        String json = getString("auto_fill_match");
        AutoFillMatchBean bean = null;
        try {
            bean = new Gson().fromJson(json, AutoFillMatchBean.class);
        } catch (Exception e) {}
        return bean;
    }

    /**
     * glory page
     */
    public static int getGloryPageIndex() {
        return getInt("key_glory_page_index", 0);
    }

    /**
     * glory page
     * @param index
     */
    public static void setGloryPageIndex(int index) {
        setInt("key_glory_page_index", index);
    }

    /**
     * glory target: select win
     */
    public static boolean isGloryTargetWin() {
        return getBoolean("key_glory_target_win");
    }

    /**
     * glory target: select win
     * @param check
     */
    public static void setGloryTargetWin(boolean check) {
        setBoolean("key_glory_target_win", check);
    }

    /**
     * glory champion group mode
     */
    public static int getGloryChampionGroupMode() {
        return getInt("key_glory_champion_group_mode", AppConstants.GROUP_BY_ALL);
    }

    /**
     * glory champion group mode
     * @param mode
     */
    public static void setGloryChampionGroupMode(int mode) {
        setInt("key_glory_champion_group_mode", mode);
    }

    /**
     * glory runnerup group mode
     */
    public static int getGloryRunnerupGroupMode() {
        return getInt("key_glory_runnerup_group_mode", AppConstants.GROUP_BY_ALL);
    }

    /**
     * glory runnerup group mode
     * @param mode
     */
    public static void setGloryRunnerupGroupMode(int mode) {
        setInt("key_glory_runnerup_group_mode", mode);
    }

    /**
     * card or pure type of player page
     */
    public static int getPlayerPageViewType() {
        return getInt("key_player_page_view_type", 0);
    }

    /**
     * card or pure type of player page
     * @param mode
     */
    public static void setPlayerPageViewType(int mode) {
        setInt("key_player_page_view_type", mode);
    }

}
