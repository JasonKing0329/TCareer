package com.king.app.tcareer.page.player.page;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.MatchBean;
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
 * @time 2018/4/21 0021 17:58
 */

public class PageCourtPresenter extends PagePresenter {

    private final String TAB_ALL = "全部";

    private List<Record> recordList;

    private String mTabId;

    @Override
    protected List<TabBean> createTabs() {
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        WhereCondition competitorCond[] = new WhereCondition[2];
        if (mCompetitor instanceof User) {
            competitorCond[0] = RecordDao.Properties.PlayerId.eq(mCompetitor.getId());
            competitorCond[1] = RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_VIRTUAL);
        } else {
            competitorCond[0] = RecordDao.Properties.PlayerId.eq(mCompetitor.getId());
            competitorCond[1] = RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_NORMAL);
        }
        recordList = dao.queryBuilder()
                .where(RecordDao.Properties.UserId.eq(mUser.getId())
                        , competitorCond)
                .build().list();

        // 查出来的是时间升序，按时间降序排列
        Collections.reverse(recordList);

        List<TabBean> tabList = new ArrayList<>();

        for (int i = 0; i < AppConstants.RECORD_MATCH_COURTS.length; i++) {
            TabBean tab = new TabBean() {

                @Override
                public String getTitle() {
                    return id;
                }
            };
            tab.id = AppConstants.RECORD_MATCH_COURTS[i];
            tabList.add(tab);
        }

        for (int i = 0; i < recordList.size(); i++) {
            Record record = recordList.get(i);
            MatchBean matchBean = record.getMatch().getMatchBean();
            // count h2h by court
            if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[0])) {
                tabList.get(0).total++;
                //如果是赛前退赛不算作h2h
                if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                    continue;
                } else {
                    if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                        tabList.get(0).lose++;
                    } else {
                        tabList.get(0).win++;
                    }
                }
            } else if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[1])) {
                tabList.get(1).total++;
                //如果是赛前退赛不算作h2h
                if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                    continue;
                } else {
                    if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                        tabList.get(1).lose++;
                    } else {
                        tabList.get(1).win++;
                    }
                }
            } else if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[3])) {
                tabList.get(3).total++;
                //如果是赛前退赛不算作h2h
                if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                    continue;
                } else {
                    if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                        tabList.get(3).lose++;
                    } else {
                        tabList.get(3).win++;
                    }
                }
            } else if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[2])) {
                tabList.get(2).total++;
                //如果是赛前退赛不算作h2h
                if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                    continue;
                } else {
                    if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                        tabList.get(2).lose++;
                    } else {
                        tabList.get(2).win++;
                    }
                }
            }
        }

        TabBean tabAll = new TabBean() {
            @Override
            public String getTitle() {
                return id;
            }
        };

        tabAll.id = TAB_ALL;
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

    @Override
    protected List<Record> createTabRecords(String tabId) {
        mTabId = tabId;
        return recordList;
    }

    @Override
    protected boolean filterRecord(Record record) {
        return mTabId.equals(TAB_ALL) || mTabId.equals(record.getMatch().getMatchBean().getCourt());
    }
}
