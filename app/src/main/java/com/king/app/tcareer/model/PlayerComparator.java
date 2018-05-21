package com.king.app.tcareer.model;

import android.text.TextUtils;

import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.list.RichPlayerBean;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.ConstellationUtil;

import java.util.Comparator;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 17:07
 */

public class PlayerComparator implements Comparator<RichPlayerBean> {

    private int sortMode;

    public PlayerComparator(int sortMode) {
        this.sortMode = sortMode;
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
        switch (sortMode) {
            case SettingProperty.VALUE_SORT_PLAYER_AGE:
                return compareByAge(lhs, rhs);
            case SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION:
                return compareByConstellation(lhs, rhs);
            case SettingProperty.VALUE_SORT_PLAYER_COUNTRY:
                return compareByCountry(lhs, rhs);
            case SettingProperty.VALUE_SORT_PLAYER_NAME_ENG:
                return compareByNameEng(lhs, rhs);
            case SettingProperty.VALUE_SORT_PLAYER_RECORD:
                return compareByRecords(lpb, rpb);
            case SettingProperty.VALUE_SORT_PLAYER_HEIGHT:
                return compareByHeight(lhs, rhs);
            case SettingProperty.VALUE_SORT_PLAYER_WEIGHT:
                return compareByWeight(lhs, rhs);
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_HIGH:
                return compareByCareerHigh(lhs, rhs);
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_TITLES:
                return compareByCareerTitles(lhs, rhs);
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_WIN:
                return compareByCareerWin(lhs, rhs);
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_TURNEDPRO:
                return compareByTurnedPro(lhs, rhs);
            case SettingProperty.VALUE_SORT_PLAYER_CAREER_LAST_UPDATE:
                return compareByLastUpdate(lhs, rhs);
            default:
                return compareByNamePinyin(lhs, rhs);

        }
    }

    private int compareByHeight(PlayerBean lhs, PlayerBean rhs) {
        double left = Double.MAX_VALUE;
        try {
            left = lhs.getAtpBean().getCm();
        } catch (Exception e) {}
        double right = Double.MAX_VALUE;
        try {
            right = rhs.getAtpBean().getCm();
        } catch (Exception e) {}

        return compareHighToLow(left, right);
    }

    /**
     * 从低到高
     * @param left
     * @param right
     * @return
     */
    public int compareLowToHigh(double left, double right) {
        if (right > left) {
            return -1;
        }
        else if (right < left) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * 从高到低
     * @param left
     * @param right
     * @return
     */
    public int compareHighToLow(double left, double right) {
        if (right > left) {
            return 1;
        }
        else if (right < left) {
            return -1;
        }
        else {
            return 0;
        }
    }

    private int compareByWeight(PlayerBean lhs, PlayerBean rhs) {
        double left = 0;
        try {
            left = lhs.getAtpBean().getKg();
        } catch (Exception e) {}
        double right = 0;
        try {
            right = rhs.getAtpBean().getKg();
        } catch (Exception e) {}

        return compareHighToLow(left, right);
    }

    private int compareByCareerHigh(PlayerBean lhs, PlayerBean rhs) {
        double left = Double.MAX_VALUE;
        try {
            left = lhs.getAtpBean().getCareerHighSingle();
        } catch (Exception e) {}
        double right = Double.MAX_VALUE;
        try {
            right = rhs.getAtpBean().getCareerHighSingle();
        } catch (Exception e) {}

        return compareLowToHigh(left, right);
    }

    private int compareByCareerTitles(PlayerBean lhs, PlayerBean rhs) {
        double left = 0;
        try {
            left = lhs.getAtpBean().getCareerSingles();
        } catch (Exception e) {}
        double right = 0;
        try {
            right = rhs.getAtpBean().getCareerSingles();
        } catch (Exception e) {}

        return compareHighToLow(left, right);
    }

    private int compareByCareerWin(PlayerBean lhs, PlayerBean rhs) {
        double left = 0;
        try {
            left = lhs.getAtpBean().getCareerWin();
        } catch (Exception e) {}
        double right = 0;
        try {
            right = rhs.getAtpBean().getCareerWin();
        } catch (Exception e) {}

        return compareHighToLow(left, right);
    }

    private int compareByTurnedPro(PlayerBean lhs, PlayerBean rhs) {
        double left = 0;
        try {
            left = lhs.getAtpBean().getTurnedPro();
        } catch (Exception e) {}
        double right = 0;
        try {
            right = rhs.getAtpBean().getTurnedPro();
        } catch (Exception e) {}

        return compareHighToLow(left, right);
    }

    private int compareByLastUpdate(PlayerBean lhs, PlayerBean rhs) {
        long left = 0;
        try {
            left = lhs.getAtpBean().getLastUpdateDate();
        } catch (Exception e) {}
        long right = 0;
        try {
            right = lhs.getAtpBean().getLastUpdateDate();
        } catch (Exception e) {}

        return compareHighToLow(left, right);
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

    private int compareByCountry(PlayerBean lhs, PlayerBean rhs) {
        // 未知的放在最后
        String strLeft = getCountry(lhs);
        if (TextUtils.isEmpty(strLeft)) {
            strLeft = "zzzzzzzzzzzzz";
        }
        String strRight = getCountry(rhs);
        if (TextUtils.isEmpty(strRight)) {
            strRight = "zzzzzzzzzzzzz";
        }
        return strLeft.toLowerCase().compareTo(strRight.toLowerCase());
    }

    private String getCountry(PlayerBean bean) {
        String country = null;
        // atp info优先
        if (bean.getAtpBean() != null && !TextUtils.isEmpty(bean.getAtpBean().getBirthCountry())) {
            country = bean.getAtpBean().getBirthCountry();
        }
        if (country == null) {
            country = bean.getCountry();
        }
        return country;
    }

    private int compareByAge(PlayerBean lhs, PlayerBean rhs) {
        // 未知的放在最后
        String dateL = getBirthday(lhs);
        if (dateL == null) {
            return 1;
        }
        String dateR = getBirthday(rhs);
        if (dateR == null) {
            return -1;
        }
        return dateR.compareTo(dateL);
    }

    private String getBirthday(PlayerBean bean) {
        String date = null;
        // atp info优先
        if (bean.getAtpBean() != null && !TextUtils.isEmpty(bean.getAtpBean().getBirthday())) {
            date = bean.getAtpBean().getBirthday();
        }
        if (date == null) {
            date = bean.getBirthday();
        }
        return date;
    }

    private int compareByConstellation(PlayerBean lhs, PlayerBean rhs) {
        // 未知的放在最后
        int indexL;
        try {
            indexL = ConstellationUtil.getConstellationIndex(getBirthday(lhs));
        } catch (ConstellationUtil.ConstellationParseException e) {
            e.printStackTrace();
            indexL = 999;
        }
        int indexR;
        try {
            indexR = ConstellationUtil.getConstellationIndex(getBirthday(rhs));
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
