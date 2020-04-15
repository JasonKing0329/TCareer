package com.king.app.tcareer.page.score;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.FrozenScore;
import com.king.app.tcareer.model.db.entity.FrozenScoreDao;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchBeanDao;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;
import com.king.app.tcareer.model.db.entity.Rank;
import com.king.app.tcareer.model.db.entity.RankDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
        ScoreBean atpCupBean = null;
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
            // ATP杯积分不走通用情况
            else if (arrLevel[7].equals(record.getMatch().getMatchBean().getLevel())) {
                if (atpCupBean == null) {
                    atpCupBean = new ScoreBean();
                }
                addScoreBean(scoreList, atpCupBean, year, weekOfLastYear, weekOfYear, record);
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
        if (atpCupBean != null && atpCupBean.getMatchBean() != null) {
            scoreList.add(atpCupBean);
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
            bean.setTitle(matchNameBean.getName());
            bean.setRecord(record);
            bean.setYear(year);
            bean.setYear(recordYear);
            bean.setChampion(arrRound[0].equals(record.getRound()) && record.getWinnerFlag() == AppConstants.WINNER_USER);
            bean.setCompleted(matchNameBean.getMatchBean().getWeek() < thisWeek);

            // 大师杯积分不走通用情况，累计积分
            if (arrLevel[1].equals(record.getMatch().getMatchBean().getLevel())) {
                // 按照ATP的规则，只有胜才积分，负没有分
                if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                    bean.setScore(bean.getScore() + ScoreTable.getMasterCupScore(record.getRound()));
                }
            }
            // ATP杯积分不走通用情况，累计积分
            else if (arrLevel[7].equals(record.getMatch().getMatchBean().getLevel())) {
                // 按照ATP的规则，只有胜才积分，负没有分
                if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                    // 不同轮次参照ATP杯积分表
                    bean.setScore(bean.getScore() + ScoreTable.getAtpCupScore(record.getRound(), record.getRankCpt()));
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

    public Observable<List<ScoreBean>> queryScoreToDate(final String strDate, final long userId) {
        return Observable.create(new ObservableOnSubscribe<List<ScoreBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ScoreBean>> e) throws Exception {
                e.onNext(getScoresByDate(strDate, userId));
            }
        });
    }

    /**
     * 根据日期获取截止到指定日期的52周参赛记录
     * @param strDate must matched yyyy-MM-dd，例如2011-05-02是星期一，那么就取从这一天的上周开始往前累计52个星期
     * @return
     */
    private List<ScoreBean> getScoresByDate(String strDate, long userId) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(strDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int endWeek = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
        int endYear = calendar.get(Calendar.YEAR);
        int startYear = endYear - 1;
        int startWeek = endWeek + 1;
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        String[] args = new String[] {
                String.valueOf(userId),
                endYear + "%",
                String.valueOf(endWeek),
                startYear + "%",
                String.valueOf(startWeek)
        };
        List<Record> list = dao.queryRaw(
                " JOIN match_names mn ON T.match_name_id = mn._id\n" +
                        " JOIN matches m ON mn.match_id = m._id\n" +
                        " WHERE T.user_id = ? AND ((T.date_str like ? AND m.week <= ?) OR\n" +
                        " (T.date_str like ? AND m.week >= ?))\n" +
                        " ORDER BY T.date_str DESC, m.week DESC"
            , args);
        return countFromList(list);
    }

    /**
     * 计算list内的积分item
     * @param list
     * @return
     */
    private List<ScoreBean> countFromList(List<Record> list) {
        List<ScoreBean> scoreList = new ArrayList<>();

        ScoreBean masterCupBean = null;
        ScoreBean atpCupBean = null;
        Map<Long, ScoreBean> recMap = new HashMap<>();
        for (int i = 0; i < list.size(); i ++) {
            Record record = list.get(i);

            // 大师杯积分不走通用情况
            if (arrLevel[1].equals(record.getMatch().getMatchBean().getLevel())) {
                if (masterCupBean == null) {
                    masterCupBean = new ScoreBean();
                }
                parseScoreFrom(scoreList, masterCupBean, record);
            }
            // ATP杯积分不走通用情况
            else if (arrLevel[7].equals(record.getMatch().getMatchBean().getLevel())) {
                if (atpCupBean == null) {
                    atpCupBean = new ScoreBean();
                }
                parseScoreFrom(scoreList, atpCupBean, record);
            }
            else {
                ScoreBean bean = recMap.get(record.getMatch().getMatchId());
                if (bean == null) {
                    bean = new ScoreBean();
                    recMap.put(record.getMatch().getMatchId(), bean);
                }
                // 出现负场，该项赛事结束
                if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                    parseScoreFrom(scoreList, bean, record);
                }
                // 一直胜场，直到Final也是胜，该项赛事结束
                else if (arrRound[0].equals(record.getRound())) {
                    parseScoreFrom(scoreList, bean, record);
                }
                //其他情况表示赛事未结束或者无积分
            }
        }
        if (masterCupBean != null && masterCupBean.getMatchBean() != null) {
            scoreList.add(masterCupBean);
        }
        if (atpCupBean != null && atpCupBean.getMatchBean() != null) {
            scoreList.add(atpCupBean);
        }
        return scoreList;
    }

    /**
     * 添加没有规则的score bean
     * @param scoreList
     * @param bean
     * @param record
     */
    private void parseScoreFrom(List<ScoreBean> scoreList, ScoreBean bean, Record record) {
        MatchNameBean matchNameBean = record.getMatch();

        bean.setMatchBean(matchNameBean);
        bean.setTitle(matchNameBean.getName());
        bean.setRecord(record);
        bean.setYear(Integer.parseInt(record.getDateStr().split("-")[0]));
        bean.setChampion(arrRound[0].equals(record.getRound()) && record.getWinnerFlag() == AppConstants.WINNER_USER);

        // 大师杯积分不走通用情况，这里只累计积分
        if (arrLevel[1].equals(record.getMatch().getMatchBean().getLevel())) {
            // 按照ATP的规则，只有胜才积分，负没有分
            if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                bean.setScore(bean.getScore() + ScoreTable.getMasterCupScore(record.getRound()));
            }
        }
        // ATP杯积分不走通用情况，这里只累计积分
        else if (arrLevel[7].equals(record.getMatch().getMatchBean().getLevel())) {
            // 按照ATP的规则，只有胜才积分，负没有分
            if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                bean.setScore(bean.getScore() + ScoreTable.getAtpCupScore(record.getRound(), record.getRankCpt()));
            }
        }
        else {
            bean.setScore(getMatchScore(record));
            scoreList.add(bean);
        }
    }

    /**
     * 处理周期内所有积分的 有效积分/替换积分/罚分等
     * @param list
     * @param userId
     * @param strDate 周期的结束日期，例如2011-05-02是星期一，那么就取从这一天的上周开始往前累计52个星期
     * @return
     */
    public Observable<ValidScores> countValidScores(final List<ScoreBean> list, final long userId, final String strDate) {
        return Observable.create(new ObservableOnSubscribe<ValidScores>() {
            @Override
            public void subscribe(ObservableEmitter<ValidScores> e) throws Exception {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(strDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int lastYear = calendar.get(Calendar.YEAR) - 1;

                RankDao rankDao = TApplication.getInstance().getDaoSession().getRankDao();
                boolean isTo30 = false;
                try {
                    Rank rank = rankDao.queryBuilder()
                            .where(RankDao.Properties.UserId.eq(userId)
                                    , RankDao.Properties.Year.eq(lastYear))
                            .build().uniqueOrThrow();
                    if (rank.getRank() <= 30) {
                        isTo30 = true;
                    }
                } catch (DaoException exception) {}

                ValidScores validScores = pickScores(userId, list, isTo30, calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR));
                e.onNext(validScores);
            }
        });
    }

    /**
     // 戴维斯杯的积分情况较为复杂，本系统不考虑这种情况，按照other赛事0分处理
     // 4大满贯+年终总决赛+8站强制ATP1000+6站最好
     // 上一年年终top30的有500赛强制罚分，必须参加4项500赛，且有一项是美网后（蒙卡算500赛），只要参加即刻，可以不计入6站最好（比如6个250夺冠，就不计算4个500赛首轮游）
     // 非top30 按照取18站最好成绩的做法，若参加了大满贯和8站强制1000赛需要强制计入
     // 2020-3月开始的疫情影响，引入冻结积分
     * @param list
     * @param isTo30
     * @param targetYear 周期结束的年份
     * @param weekOfYear 周期结束的周数
     * @return
     */
    private ValidScores pickScores(long userId, List<ScoreBean> list, boolean isTo30, int targetYear, int weekOfYear) {
        ValidScores scores = new ValidScores();
        scores.setAllList(list);

        List<ScoreBean> validList = new ArrayList<>();
        List<ScoreBean> replaceList = new ArrayList<>();
        List<ScoreBean> otherList = new ArrayList<>();
        scores.setValidList(validList);
        scores.setReplaceList(replaceList);
        scores.setOtherList(otherList);

        List<ScoreBean> tempList = new ArrayList<>();
        List<ScoreBean> temp500List = new ArrayList<>();

        // 1.记录所有参加的赛事
        for (ScoreBean bean:list) {
            String level = bean.getMatchBean().getMatchBean().getLevel();
            // 大满贯强制计分
            if (level.equals(arrLevel[0])) {
                validList.add(bean);
            }
            // 大师杯
            else if (level.equals(arrLevel[1])) {
                validList.add(bean);
            }
            // ATP1000
            else if (level.equals(arrLevel[2])) {
                // 蒙特卡洛非强制，top30球员可视作参加了ATP500
                if (bean.getMatchBean().getMatchId() == AppConstants.ATP_1000_MATCH_ID[2]) {
                    if (isTo30) {
                        temp500List.add(bean);
                    }
                    else {
                        tempList.add(bean);
                    }
                }
                // 其他8站ATP1000强制计分
                else {
                    validList.add(bean);
                }
            }
            // ATP500
            else if (level.equals(arrLevel[3])) {
                if (isTo30) {
                    temp500List.add(bean);
                }
                else {
                    tempList.add(bean);
                }
            }
            // ATP250，ATP杯都算作可积分的非强制赛事
            else if (level.equals(arrLevel[4]) || level.equals(arrLevel[7])) {
                tempList.add(bean);
            }
            else {
                otherList.add(bean);
            }

        }

        ScoreComparator scoreComparator = new ScoreComparator();
        if (isTo30) {
            // 2.top30检查大满贯强制计0
            List<MatchNameBean> matchList = queryMatches(arrLevel[0]);
            for (MatchNameBean match:matchList) {
                boolean attended = false;
                for (ScoreBean bean:validList) {
                    if (bean.getMatchBean().getMatchId() == match.getMatchId()) {
                        attended = true;
                        break;
                    }
                }
                if (!attended) {
                    validList.add(addForce0Score(match, targetYear, weekOfYear));
                }
            }
            // 3.top30检查ATP1000强制计0
            matchList = queryMatches(arrLevel[2]);
            for (MatchNameBean match:matchList) {
                // 蒙卡不强制
                if (match.getMatchId() == AppConstants.ATP_1000_MATCH_ID[2]) {
                    continue;
                }
                boolean attended = false;
                for (ScoreBean bean:validList) {
                    if (bean.getMatchBean().getMatchId() == match.getMatchId()) {
                        attended = true;
                        break;
                    }
                }
                if (!attended) {
                    validList.add(addForce0Score(match, targetYear, weekOfYear));
                }
            }
            // 4.top30检查ATP500罚分
            int punishCount = 0;
            int value = temp500List.size() - 4;
            // 需要罚分
            if (value < 0) {
                for (int i = 0; i < -value; i ++) {
                    validList.add(addPunishScore());
                    punishCount ++;
                }
            }
            // 5.top30选择余下的最好6-punishCount站赛事进入validList，其余的进入replaceList
            for (int i = 0; i < temp500List.size(); i ++) {
                tempList.add(temp500List.get(i));
            }
            Collections.sort(tempList, scoreComparator);
            // 计算起记分
            if (tempList.size() > 5) {
                scores.setStartScore(tempList.get(4).getScore());
            }
            else {
                scores.setStartScore(0);
            }
            for (int i = 0; i < tempList.size(); i ++) {
                if (i < 6 - punishCount) {
                    validList.add(tempList.get(i));
                }
                else {
                    replaceList.add(tempList.get(i));
                }
            }
        }
        // 2.非top30直接取18-validList.size()最好比赛进入validList
        else {
            int count = 18 - validList.size();
            Collections.sort(tempList, new ScoreComparator());
            for (int i = 0; i < tempList.size(); i ++) {
                if (i < count) {
                    validList.add(tempList.get(i));
                }
                else {
                    replaceList.add(tempList.get(i));
                }
            }
        }

        // 冻结积分
        int frozenScore = 0;
        List<FrozenScore> frozenList = TApplication.getInstance().getDaoSession().getFrozenScoreDao().queryBuilder()
                .where(FrozenScoreDao.Properties.UserId.eq(userId))
                .build().list();
        for (FrozenScore fs:frozenList) {
            frozenScore += fs.getScore();
        }
        scores.setFrozenScore(frozenScore);

        // 计算总积分
        int sum = 0;
        for (ScoreBean bean:validList) {
            sum += bean.getScore();
        }
        sum += frozenScore;
        scores.setValidScore(sum);

        return scores;
    }

    /**
     * top30强制计0赛事
     * MatchNameBean不为空
     * @return
     */
    private ScoreBean addForce0Score(MatchNameBean match, int targetYear, int weekOfYear) {
        ScoreBean bean = new ScoreBean();
        bean.setTitle(match.getName());
        bean.setScore(0);
        bean.setMatchBean(match);
        if (match.getMatchBean().getWeek() < weekOfYear) {
            bean.setYear(targetYear);
        }
        else {
            bean.setYear(targetYear - 1);
        }
        return bean;
    }

    /**
     * top30 500赛未参加够，罚分
     * @return MatchNameBean为空
     */
    private ScoreBean addPunishScore() {
        ScoreBean bean = new ScoreBean();
        bean.setTitle("500赛罚分");
        bean.setScore(0);
        return bean;
    }

    private List<MatchNameBean> queryMatches(String level) {
        MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
        QueryBuilder<MatchNameBean> queryBuilder = dao.queryBuilder();
        queryBuilder.join(MatchNameBeanDao.Properties.MatchId, MatchBean.class)
                .where(MatchBeanDao.Properties.Level.eq(level));
        return queryBuilder.build().list();
    }

    private class ScoreComparator implements Comparator<ScoreBean> {

        @Override
        public int compare(ScoreBean left, ScoreBean right) {
            int result = right.getScore() - left.getScore();
            if (result < 0) {
                return -1;
            }
            else if (result > 0) {
                return 1;
            }
            else {
                return 0;
            }
        }
    }

}
