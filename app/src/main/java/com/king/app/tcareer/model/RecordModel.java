package com.king.app.tcareer.model;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/9/16 13:38
 */
public class RecordModel {

    public String getAoImageUrl() {
        return ImageProvider.getMatchHeadPath("澳大利亚网球公开赛", AppConstants.RECORD_MATCH_COURTS[0]);
    }

    public MatchNameBean getMatchNameBean(String name) {
        MatchNameBean bean = TApplication.getInstance().getDaoSession().getMatchNameBeanDao().queryBuilder()
                .where(MatchNameBeanDao.Properties.Name.eq(name))
                .build().unique();
        return bean;
    }

    public List<Record> getAoRecords(long userId) {
        return getRecordsByMatch(userId, getMatchNameBean("澳大利亚网球公开赛").getId());
    }

    public String getFoImageUrl() {
        return ImageProvider.getMatchHeadPath("法国网球公开赛", AppConstants.RECORD_MATCH_COURTS[1]);
    }

    public List<Record> getFoRecords(long userId) {
        return getRecordsByMatch(userId, getMatchNameBean("法国网球公开赛").getId());
    }

    public String getWoImageUrl() {
        return ImageProvider.getMatchHeadPath("温布尔顿网球公开赛", AppConstants.RECORD_MATCH_COURTS[2]);
    }

    public List<Record> getWoRecords(long userId) {
        return getRecordsByMatch(userId, getMatchNameBean("温布尔顿网球公开赛").getId());
    }

    public String getUoImageUrl() {
        return ImageProvider.getMatchHeadPath("美国网球公开赛", AppConstants.RECORD_MATCH_COURTS[0]);
    }

    public List<Record> getUoRecords(long userId) {
        return getRecordsByMatch(userId, getMatchNameBean("美国网球公开赛").getId());
    }

    public List<Record> getRecordsByMatch(long userId, long matchNameId) {
        List<Record> records = TApplication.getInstance().getDaoSession().getRecordDao().queryBuilder()
                .where(RecordDao.Properties.UserId.eq(userId))
                .where(RecordDao.Properties.MatchNameId.eq(matchNameId))
                .build().list();
        return records;
    }

    public int countMatchTimes(long userId, long matchNameId) {
        List<Record> records = TApplication.getInstance().getDaoSession().getRecordDao().queryBuilder()
                .where(RecordDao.Properties.UserId.eq(userId))
                .where(RecordDao.Properties.MatchNameId.eq(matchNameId))
                .build().list();
        Map<String, Boolean> dateMap = new HashMap<>();
        int times = 0;
        for (Record record:records) {
            if (dateMap.get(record.getDateStr()) == null) {
                times ++;
                dateMap.put(record.getDateStr(), true);
            }
        }
        return times;
    }

    public List<Record> getAoRoundRecords(long userId, String round) {
        return getRoundRecords(userId, getMatchNameBean("澳大利亚网球公开赛").getId(), round);
    }

    public List<Record> getFoRoundRecords(long userId, String round) {
        return getRoundRecords(userId, getMatchNameBean("法国网球公开赛").getId(), round);
    }

    public List<Record> getWoRoundRecords(long userId, String round) {
        return getRoundRecords(userId, getMatchNameBean("温布尔顿网球公开赛").getId(), round);
    }

    public List<Record> getUoRoundRecords(long userId, String round) {
        return getRoundRecords(userId, getMatchNameBean("美国网球公开赛").getId(), round);
    }

    public List<Record> getRoundRecords(long userId, long matchNameId, String round) {
        List<Record> records = TApplication.getInstance().getDaoSession().getRecordDao().queryBuilder()
                .where(RecordDao.Properties.UserId.eq(userId))
                .where(RecordDao.Properties.MatchNameId.eq(matchNameId))
                .where(RecordDao.Properties.Round.eq(round))
                .build().list();
        return records;
    }
}
