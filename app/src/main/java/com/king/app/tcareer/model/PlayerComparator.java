package com.king.app.tcareer.model;

import android.text.TextUtils;

import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.list.RichPlayerBean;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.ConstellationUtil;
import com.king.app.tcareer.utils.PinyinUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 17:07
 */

public class PlayerComparator implements Comparator<RichPlayerBean> {

    private int sortMode;
    private Map<String, String> countryPinyinMap;

    public PlayerComparator(int sortMode) {
        this.sortMode = sortMode;
        if (sortMode == SettingProperty.VALUE_SORT_PLAYER_COUNTRY) {
            countryPinyinMap = new HashMap<>();
        }
    }

    @Override
    public int compare(RichPlayerBean lpb, RichPlayerBean rpb) {
        // 只排序PlayerBean
        if (lpb.getCompetitorBean() instanceof User) {
            return -1;
        }
        else if (rpb.getCompetitorBean() instanceof User) {
            return 1;
        }

        PlayerBean lhs = (PlayerBean) lpb.getCompetitorBean();
        PlayerBean rhs = (PlayerBean) rpb.getCompetitorBean();
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
        else if (sortMode == SettingProperty.VALUE_SORT_PLAYER_RECORD) {
            return compareByRecords(lpb, rpb);
        }
        else {
            return compareByNamePinyin(lhs, rhs);
        }
    }

    private int compareByNamePinyin(PlayerBean lhs, PlayerBean rhs) {
        return lhs.getNamePinyin().compareTo(rhs.getNamePinyin());
    }

    /**
     * 交手次数降序排列
     * @param lpb
     * @param rpb
     * @return
     */
    private int compareByRecords(RichPlayerBean lpb, RichPlayerBean rpb) {
        int resultL = lpb.getWin() + lpb.getLose();
        int resultR = rpb.getWin() + rpb.getLose();
        if (resultL > resultR) {
            return -1;
        }
        else if (resultL < resultR) {
            return 1;
        }
        else {
            // 如相等再按姓名排
            return compareByNamePinyin((PlayerBean) lpb.getCompetitorBean(), (PlayerBean) rpb.getCompetitorBean());
        }
    }

    private int compareByCoutry(PlayerBean lhs, PlayerBean rhs) {
        String pinyinL = countryPinyinMap.get(lhs.getCountry());
        if (pinyinL == null) {
            pinyinL = PinyinUtil.getPinyin(lhs.getCountry());
            countryPinyinMap.put(lhs.getCountry(), pinyinL);
        }
        String pinyinR = countryPinyinMap.get(rhs.getCountry());
        if (pinyinR == null) {
            pinyinR = PinyinUtil.getPinyin(rhs.getCountry());
            countryPinyinMap.put(rhs.getCountry(), pinyinR);
        }
        return pinyinL.compareTo(pinyinR);
    }

    private int compareByAge(PlayerBean lhs, PlayerBean rhs) {
        // 未知的放在最后
        String dateL = lhs.getBirthday();
        if (dateL == null) {
            return 1;
        }
        String dateR = rhs.getBirthday();
        if (dateR == null) {
            return -1;
        }
        return dateL.compareTo(dateR);
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

        if (indexL > indexR) {
            return -1;
        }
        else if (indexL < indexR) {
            return 1;
        }
        else {
            // 如相等再按年龄排
            return compareByAge(lhs, rhs);
        }
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
