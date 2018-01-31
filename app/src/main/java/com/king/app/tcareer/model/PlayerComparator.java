package com.king.app.tcareer.model;

import android.text.TextUtils;

import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.manage.PlayerViewBean;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.ConstellationUtil;
import com.king.app.tcareer.utils.PinyinUtil;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 17:07
 */

public class PlayerComparator implements Comparator<PlayerViewBean> {

    private int sortMode;
    private Map<String, String> countryPinyinMap;
    private SimpleDateFormat sdf;

    public PlayerComparator(int sortMode) {
        this.sortMode = sortMode;
        if (sortMode == SettingProperty.VALUE_SORT_PLAYER_COUNTRY) {
            countryPinyinMap = new HashMap<>();
        }
        else if (sortMode == SettingProperty.VALUE_SORT_PLAYER_AGE) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        else if (sortMode == SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION) {
            sdf = new SimpleDateFormat("MM-dd");
        }
    }

    @Override
    public int compare(PlayerViewBean lpb, PlayerViewBean rpb) {
        // 只排序PlayerBean
        if (lpb.getData() instanceof User) {
            return -1;
        }
        else if (rpb.getData() instanceof User) {
            return 1;
        }

        PlayerBean lhs = (PlayerBean) lpb.getData();
        PlayerBean rhs = (PlayerBean) rpb.getData();
        if (sortMode == SettingProperty.VALUE_SORT_PLAYER_AGE) {
            return compareByAge(lhs, rhs);
        }
        else if (sortMode == SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION) {
            return compareByConstellation(lhs, rhs);
        }
        else if (sortMode == SettingProperty.VALUE_SORT_PLAYER_COUNTRY) {
            return compareByCoutry(lhs, rhs);
        }
        else if (sortMode == SettingProperty.VALUE_SORT_PLAYER_NAME_ENG) {
            return compareByNameEng(lhs, rhs);
        }
        else {
            return lhs.getNamePinyin().compareTo(rhs.getNamePinyin());
        }
    }

    private int compareByCoutry(PlayerBean lhs, PlayerBean rhs) {
        String pinyinL = countryPinyinMap.get(lhs.getCountry());
        if (pinyinL == null) {
            pinyinL = PinyinUtil.getPinyin(lhs.getCountry());
        }
        String pinyinR = countryPinyinMap.get(rhs.getCountry());
        if (pinyinR == null) {
            pinyinR = PinyinUtil.getPinyin(rhs.getCountry());
        }
        return pinyinL.compareTo(pinyinR);
    }

    private int compareByAge(PlayerBean lhs, PlayerBean rhs) {
        // 未知的放在最后
        long dateL;
        try {
            dateL = sdf.parse(lhs.getBirthday()).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            dateL = Long.MAX_VALUE;
        }
        long dateR;
        try {
            dateR = sdf.parse(rhs.getBirthday()).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            dateR = Long.MAX_VALUE;
        }
        if (dateL - dateR < 0) {
            return -1;
        }
        else if (dateL - dateR > 0) {
            return 1;
        }
        else {
            return 0;
        }
    }

    private int compareByConstellation(PlayerBean lhs, PlayerBean rhs) {
        // 未知的放在最后
        int indexL;
        try {
            indexL = ConstellationUtil.getConstellationIndex(lhs.getBirthday());
        } catch (ConstellationUtil.ConstellationParseException e) {
            e.printStackTrace();
            indexL = 999;
        }
        int indexR;
        try {
            indexR = ConstellationUtil.getConstellationIndex(rhs.getBirthday());
        } catch (ConstellationUtil.ConstellationParseException e) {
            e.printStackTrace();
            indexR = 999;
        }
        return indexL - indexR;
    }

    private int compareByNameEng(PlayerBean lhs, PlayerBean rhs) {
        // 未知的放在最后
        String strLeft = lhs.getNameEng();
        if (TextUtils.isEmpty(strLeft)) {
            strLeft = "zzzzzzzzzzzzz";
        }
        String strRight = rhs.getNameEng();
        if (TextUtils.isEmpty(strRight)) {
            strRight = "zzzzzzzzzzzzz";
        }
        return strLeft.toLowerCase().compareTo(strRight.toLowerCase());
    }

}
