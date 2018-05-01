package com.king.app.tcareer.page.player.page;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/5/1 0001 12:40
 */

public class YearPageModel implements SubPageModel {
    @Override
    public List<TabBean> createTabs(User user, CompetitorBean competitor) {
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        WhereCondition competitorCond[] = new WhereCondition[2];
        if (competitor instanceof User) {
            competitorCond[0] = RecordDao.Properties.PlayerId.eq(competitor.getId());
            competitorCond[1] = RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_VIRTUAL);
        } else {
            competitorCond[0] = RecordDao.Properties.PlayerId.eq(competitor.getId());
            competitorCond[1] = RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_NORMAL);
        }
        List<Record> recordList = dao.queryBuilder()
                .where(RecordDao.Properties.UserId.eq(user.getId())
                        , competitorCond)
                .build().list();

        // 查出来的是时间升序，按时间降序排列
        Collections.reverse(recordList);

        List<TabBean> tabList = new ArrayList<>();

        TabBean curTab = null;
        for (int i = 0; i < recordList.size(); i++) {
            Record record = recordList.get(i);
            String year = record.getDateStr().split("-")[0];
            if (curTab == null || !year.equals(curTab.year)) {
                curTab = new TabBean() {
                    @Override
                    public String getTitle() {
                        return year;
                    }
                };
                curTab.year = year;
                tabList.add(curTab);
            }
            curTab.total++;
            //如果是赛前退赛不算作h2h
            if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                continue;
            } else {
                if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                    curTab.lose++;
                } else {
                    curTab.win++;
                }
            }
        }

        TabBean tabAll = new TabBean() {
            @Override
            public String getTitle() {
                return year;
            }
        };

        tabAll.year = TAB_ALL;
        // 如果没有记录就不显示这个tab
        for (int i = tabList.size() - 1; i >= 0; i--) {
            tabAll.win += tabList.get(i).win;
            tabAll.lose += tabList.get(i).lose;
            tabAll.total += tabList.get(i).total;
            if (tabList.get(i).total == 0) {
                tabList.remove(i);
            }
        }
        tabList.add(0, tabAll);
        return tabList;
    }
}
