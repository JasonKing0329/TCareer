package com.king.app.tcareer.page.score;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/2/20 16:43
 */
public class ScoreModel {

    private String[] arrRound;
    private String[] arrLevel;

    public ScoreModel() {
        arrRound = AppConstants.RECORD_MATCH_ROUNDS;
        arrLevel = AppConstants.RECORD_MATCH_LEVELS;
    }

    public Observable<List<ScoreBean>> queryYearRecords(final long userId, final int year) {
        return Observable.create(new ObservableOnSubscribe<List<ScoreBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ScoreBean>> e) throws Exception {
                e.onNext(getYearRecords(userId, year));
            }
        });
    }

    private List<ScoreBean> getYearRecords(long userId, int year) {
        long minTime = getYearMinTime(year);
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        long maxTime;
        if (year == thisYear) {
            maxTime = getMonthMaxTime();
        }
        else {
            maxTime = getYearMaxTime(year);
        }
        // 查询出year当年的所有记录
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        List<Record> list = dao.queryBuilder()
                .where(RecordDao.Properties.UserId.eq(userId)
                    , RecordDao.Properties.DateLong.gt(minTime)
                    , RecordDao.Properties.DateLong.lt(maxTime))
                .build().list();

        // 为了走公共逻辑，实际上就是计算去年52周到今年52周的积分情况
        List<ScoreBean> scoreList = countScoreList(list, year, 52, 52);
        return scoreList;
    }

    public Observable<List<ScoreBean>> query52WeekRecords(final long userId, final int year) {
        return Observable.create(new ObservableOnSubscribe<List<ScoreBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ScoreBean>> e) throws Exception {
                e.onNext(get52WeekRecords(userId, year));
            }
        });
    }

    public List<ScoreBean> get52WeekRecords(long userId, int year) {

        long minTime = getLastYearMinTime();
        long maxTime = getMonthMaxTime();

        // 查询出year当年的所有记录
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        List<Record> list = dao.queryBuilder()
                .where(RecordDao.Properties.UserId.eq(userId)
                        , RecordDao.Properties.DateLong.gt(minTime)
                        , RecordDao.Properties.DateLong.lt(maxTime))
                .build().list();
        // 查询出一整年的记录

        // 去掉不在积分周期的记录
        List<ScoreBean> scoreList = distinctOutsideRecord(list);
        return scoreList;
    }

    /**
     * 计算周期内的积分
     * @param list
     * @param year
     * @param weekOfYear
     * @param weekOfLastYear
     * @return
     */
    private List<ScoreBean> countScoreList(List<Record> list, int year, int weekOfYear, int weekOfLastYear) {
        List<ScoreBean> scoreList = new ArrayList<>();

        ScoreBean masterCupBean = null;
        Map<Long, ScoreBean> recMap = new HashMap<>();
        for (int i = 0; i < list.size(); i ++) {
            Record record = list.get(i);

            // 大师杯积分不走通用情况
            if (arrLevel[1].equals(record.getMatch().getMatchBean().getLevel())) {
                if (masterCupBean == null) {
                    masterCupBean = new ScoreBean();
                }
                addScoreBean(scoreList, masterCupBean, year, weekOfLastYear, weekOfYear, record);
            }
            else {
                ScoreBean bean = recMap.get(record.getMatch().getMatchId());
                if (bean == null) {
                    bean = new ScoreBean();
                    recMap.put(record.getMatch().getMatchId(), bean);
                }
                // 出现负场，该项赛事结束
                if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                    addScoreBean(scoreList, bean, year, weekOfLastYear, weekOfYear, record);
                }
                // 一直胜场，直到Final也是胜，该项赛事结束
                else if (arrRound[0].equals(record.getRound())) {
                    addScoreBean(scoreList, bean, year, weekOfLastYear, weekOfYear, record);
                }
                //其他情况表示赛事未结束或者无积分
            }
        }
        if (masterCupBean != null && masterCupBean.getMatchBean() != null) {
            scoreList.add(masterCupBean);
        }
        return scoreList;
    }

    /**
     * 52 week 积分只支持截止今年的
     * @param list
     * @return
     */
    private List<ScoreBean> distinctOutsideRecord(List<Record> list) {
        // 去年的本周到今年的上一周为一个积分周期
        int weekOfYear = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) - 1;
        int weekOfLastYear = weekOfYear + 1;
        return countScoreList(list, Calendar.getInstance().get(Calendar.YEAR), weekOfYear, weekOfLastYear);
    }

    private void addScoreBean(List<ScoreBean> scoreList, ScoreBean bean, int year, int weekOfLastYear, int weekOfYear, Record record) {
        MatchNameBean matchNameBean = record.getMatch();
        int thisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        // 如果在积分周期，则记录为score bean
        int week = matchNameBean.getMatchBean().getWeek();
        boolean needAdd = false;
        int recordYear = Integer.parseInt(record.getDateStr().split("-")[0]);
        // 今年的赛事，周数应该小于当前
        if (recordYear == year) {
            if (week <= weekOfYear) {
                needAdd = true;
            }
        }
        // 去年的赛事，周数应该大于去年同期
        else {
            if (week >= weekOfLastYear) {
                needAdd = true;
            }
        }
        if (needAdd) {
            bean.setMatchBean(matchNameBean);
            bean.setYear(year);
            bean.setYear(recordYear);
            bean.setChampion(arrRound[0].equals(record.getRound()) && record.getWinnerFlag() == AppConstants.WINNER_USER);
            bean.setCompleted(matchNameBean.getMatchBean().getWeek() < thisWeek);

            // 大师杯积分不走通用情况，这里只累计积分
            if (arrLevel[1].equals(record.getMatch().getMatchBean().getLevel())) {
                // 按照ATP的规则，只有胜才积分，负没有分
                if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                    if (arrRound[7].equals(record.getRound())) {// group, 一场200
                        bean.setScore(bean.getScore() + 200);
                    }
                    if (arrRound[1].equals(record.getRound())) {// semi final胜多加400
                        bean.setScore(bean.getScore() + 400);
                    }
                    if (arrRound[0].equals(record.getRound())) {// final胜多加500
                        bean.setScore(bean.getScore() + 500);
                    }
                }
            }
            else {
                bean.setScore(getMatchScore(record));
                scoreList.add(bean);
            }
        }
    }

    /**
     * 计算该项赛事的最终积分
     * @param record 输掉的轮次或者最终夺冠的轮次
     * @return
     */
    private int getMatchScore(Record record) {
        if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {// 负
            return ScoreTable.getScore(record.getRound(), record.getMatch().getMatchBean().getLevel(), false);
        }
        else if (arrRound[0].equals(record.getRound())) {
            return ScoreTable.getScore(record.getRound(), record.getMatch().getMatchBean().getLevel(), true);
        }
        return 0;
    }

    /**
     * 去年的今年这个月第一毫秒
     * @return
     */
    public long getLastYearMinTime() {
        int year = Calendar.getInstance().get(Calendar.YEAR) - 1;
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        String strDate = "" + year + "-" + month;
        if (month < 10) {
            strDate = "" + year + "-0" + month;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        return time - 1;
    }

    /**
     * year年第一毫秒之前，即去年的最后一毫秒
     * @return
     * @param year
     */
    public long getYearMinTime(int year) {
        String strDate = "" + year + "-01";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        return time - 1;
    }

    /**
     * year年最后一毫秒，即year年明年的最开始一毫秒-1
     * @return
     * @param year
     */
    public long getYearMaxTime(int year) {

        return getYearMinTime(year + 1) - 1;
    }

    /**
     * 今年当月最后一毫秒
     * @return
     */
    public long getMonthMaxTime() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        // 最后一天
        int day = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        StringBuffer buffer = new StringBuffer();
        buffer.append(year).append("-");
        if (month < 10) {
            buffer.append("0").append(month);
        }
        else {
            buffer.append(month);
        }
        buffer.append("-");
        if (day < 10) {
            buffer.append("0").append(day);
        }
        else {
            buffer.append(day);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(buffer.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        return time;
    }

}
