package com.king.app.tcareer.model;

import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.compare.SubTotalBean;
import com.king.app.tcareer.page.compare.TwoLineBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/9/16 13:33
 */
public class CompareModel {

    private RecordModel recordModel = new RecordModel();

    public Observable<List<SubTotalBean>> getGsRound(long userId, String round) {
        return Observable.create(e -> {
            List<SubTotalBean> list = new ArrayList<>();
            boolean winAsSub;
            String targetRound;
            if (AppConstants.CHAMPOION.equals(round)) {
                winAsSub = true;
                targetRound = AppConstants.RECORD_MATCH_ROUNDS[0];
            }
            else {
                winAsSub = false;
                targetRound = round;
            }
            List<Record> records = recordModel.getAoRoundRecords(userId, targetRound);
            int times = recordModel.countMatchTimes(userId, recordModel.getMatchNameBean("澳大利亚网球公开赛").getId());
            SubTotalBean ao = getRoundInfo(records, winAsSub);
            ao.setUnder(times + "次参赛");
            ao.setImageUrl(recordModel.getAoImageUrl());
            list.add(ao);
            records = recordModel.getFoRoundRecords(userId, targetRound);
            times = recordModel.countMatchTimes(userId, recordModel.getMatchNameBean("法国网球公开赛").getId());
            SubTotalBean fo = getRoundInfo(records, winAsSub);
            fo.setUnder(times + "次参赛");
            fo.setImageUrl(recordModel.getFoImageUrl());
            list.add(fo);
            records = recordModel.getWoRoundRecords(userId, targetRound);
            times = recordModel.countMatchTimes(userId, recordModel.getMatchNameBean("温布尔顿网球公开赛").getId());
            SubTotalBean wo = getRoundInfo(records, winAsSub);
            wo.setImageUrl(recordModel.getWoImageUrl());
            wo.setUnder(times + "次参赛");
            list.add(wo);
            records = recordModel.getUoRoundRecords(userId, targetRound);
            times = recordModel.countMatchTimes(userId, recordModel.getMatchNameBean("美国网球公开赛").getId());
            SubTotalBean uo = getRoundInfo(records, winAsSub);
            uo.setUnder(times + "次参赛");
            uo.setImageUrl(recordModel.getUoImageUrl());
            list.add(uo);

            e.onNext(list);
        });
    }

    private SubTotalBean getRoundInfo(List<Record> records, boolean winAsSub) {
        SubTotalBean bean = new SubTotalBean();
        int sub = 0;
        int total = 0;
        for (Record record:records) {
            total ++;
            if (winAsSub) {
                if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                    sub ++;
                }
            }
            else {
                if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                    sub ++;
                }
            }
        }
        bean.setSub("" + sub);
        bean.setTotal("/" + total);
        return bean;
    }

    public Observable<List<TwoLineBean>> getGsWinLose(long userId) {
        return Observable.create(e -> {
            List<TwoLineBean> list = new ArrayList<>();
            List<Record> records = recordModel.getAoRecords(userId);
            TwoLineBean ao = getMatchWinLoseInfo(records);
            ao.setImageUrl(recordModel.getAoImageUrl());
            list.add(ao);
            records = recordModel.getFoRecords(userId);
            TwoLineBean fo = getMatchWinLoseInfo(records);
            fo.setImageUrl(recordModel.getFoImageUrl());
            list.add(fo);
            records = recordModel.getWoRecords(userId);
            TwoLineBean wo = getMatchWinLoseInfo(records);
            wo.setImageUrl(recordModel.getWoImageUrl());
            list.add(wo);
            records = recordModel.getUoRecords(userId);
            TwoLineBean uo = getMatchWinLoseInfo(records);
            uo.setImageUrl(recordModel.getUoImageUrl());
            list.add(uo);
            e.onNext(list);
        });
    }

    private TwoLineBean getMatchWinLoseInfo(List<Record> records) {
        TwoLineBean bean = new TwoLineBean();
        int times = 0;
        int win = 0;
        int lose = 0;
        Map<String, Boolean> dateMap = new HashMap<>();
        for (Record record:records) {
            if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                win ++;
            }
            else {
                lose ++;
            }
            if (dateMap.get(record.getDateStr()) == null) {
                times ++;
                dateMap.put(record.getDateStr(), true);
            }
        }
        bean.setText1(win + "胜" + lose + "负");
        bean.setText2(times + "次参赛");
        return bean;
    }
}
