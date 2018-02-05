package com.king.app.tcareer.model;

import com.king.app.tcareer.page.match.gallery.UserMatchBean;

import java.util.Calendar;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/5 14:12
 */
public class MatchModel {

    /**
     * 找到与当前周数最靠近的赛事，前后出现等间隔的以前者优先（跨周数赛事）
     * matchList 已经是按周数升序排列了
     * @return
     */
    public int findLatestWeekItem(List<UserMatchBean> matchList) {

        int position = 0;
        // 最小间隔
        int minSpace = Integer.MAX_VALUE;

        // 当前周
        int curWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        for (int i = 0; i < matchList.size(); i ++) {
            int week = matchList.get(i).getNameBean().getMatchBean().getWeek();
            int space = Math.abs(week - curWeek);
            if (space < minSpace) {
                minSpace = space;
                position = i;
            }
            else if (space == minSpace) {
                // 等于的情况以前者优先
                continue;
            }
            // 由于已经是按周数的升序排列了，所以当出现大于等于最小间隔的情况时，可以终止遍历
            else {
                break;
            }
        }
        return position;
    }

}
