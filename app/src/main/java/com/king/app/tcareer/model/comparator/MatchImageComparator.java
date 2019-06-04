package com.king.app.tcareer.model.comparator;

import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.MatchImageBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.PinyinUtil;

import java.util.Comparator;

/**
 * 描述: sort match
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/30 16:08
 */
public class MatchImageComparator implements Comparator<MatchImageBean> {

    private int sortMode;

    public MatchImageComparator(int sortMode) {
        this.sortMode = sortMode;
    }

    @Override
    public int compare(MatchImageBean lhs, MatchImageBean rhs) {
        if (sortMode == SettingProperty.VALUE_SORT_MATCH_NAME) {
            return compareByName(lhs.getBean(), rhs.getBean());
        }
        else if (sortMode == SettingProperty.VALUE_SORT_MATCH_LEVEL) {
            return compareByLevel(lhs.getBean(), rhs.getBean());
        }
        else {
            return lhs.getBean().getMatchBean().getWeek() - rhs.getBean().getMatchBean().getWeek();
        }
    }

    private int compareByName(MatchNameBean lhs, MatchNameBean rhs) {
        return PinyinUtil.getPinyin(lhs.getName()).compareTo(PinyinUtil.getPinyin(rhs.getName()));
    }

    private int compareByLevel(MatchNameBean lhs, MatchNameBean rhs) {
        int valueL = getLevelValue(lhs.getMatchBean().getLevel());
        int valueR = getLevelValue(rhs.getMatchBean().getLevel());
        return valueL - valueR;
    }

    private int getLevelValue(String level) {
        for (int i = 0; i < AppConstants.RECORD_MATCH_LEVELS.length; i ++) {
            if (level.equals(AppConstants.RECORD_MATCH_LEVELS[i])) {
                return i;
            }
        }
        return 0;
    }
}
