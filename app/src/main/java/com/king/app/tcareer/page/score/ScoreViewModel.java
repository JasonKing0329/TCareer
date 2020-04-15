package com.king.app.tcareer.page.score;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.FrozenScore;
import com.king.app.tcareer.model.db.entity.FrozenScoreDao;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchBeanDao;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;
import com.king.app.tcareer.model.db.entity.Rank;
import com.king.app.tcareer.model.db.entity.RankDao;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.utils.FormatUtil;
import com.king.app.tcareer.utils.ListUtil;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/2/21 13:59
 */
public class ScoreViewModel extends BaseViewModel {

    public MutableLiveData<User> userObserver = new MutableLiveData<>();
    public MutableLiveData<ScorePageData> pageDataObserver = new MutableLiveData<>();

    public ObservableField<String> nameText = new ObservableField<>();
    public ObservableField<String> countryText = new ObservableField<>();
    public ObservableField<String> birthdayText = new ObservableField<>();
    public ObservableField<String> heightText = new ObservableField<>();

    public ObservableField<Integer> totalScoreVisibility = new ObservableField<>();
    public ObservableField<String> totalScoreText = new ObservableField<>();
    public ObservableField<Integer> frozenScoreVisibility = new ObservableField<>();
    public ObservableField<String> frozenScoreText = new ObservableField<>();
    public ObservableField<String> rankText = new ObservableField<>();
    public ObservableField<String> matchCountText = new ObservableField<>();

    private ScoreModel scoreModel;

    private String[] arrLevel;
    private String[] arrCourt;
    
    private ScoreComparator scoreComparator;
    private MatchSeqComparator matchSeqComparator;
    private ScorePageData scorePageData;

    private int thisYear;
    private int thisWeek;
    private int currentYear;
    private int startWeek, endWeek;
    private int startYear, endYear;

    public ScoreViewModel(@NonNull Application application) {
        super(application);
        scoreModel = new ScoreModel();
        init();
    }

    private void init() {
        arrCourt = AppConstants.RECORD_MATCH_COURTS;
        arrLevel = AppConstants.RECORD_MATCH_LEVELS;
        scoreComparator = new ScoreComparator();
        matchSeqComparator = new MatchSeqComparator();
        thisYear = Calendar.getInstance().get(Calendar.YEAR);
        thisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        currentYear = thisYear;
        scorePageData = new ScorePageData();
    }

    public void queryYearRecords(long userId) {
        startYear = currentYear;
        endYear = currentYear;
        startWeek = 0;
        endWeek = 52;
        queryUser(userId)
                .flatMap(user -> {
                    mUser = user;
                    userObserver.postValue(user);
                    return scoreModel.queryYearRecords(user.getId(), currentYear);
                })
                .flatMap(list -> createPageData(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ScorePageData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(ScorePageData data) {
                        updateUserContent(mUser);
                        updateContent(data);
                        pageDataObserver.setValue(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load score failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void updateUserContent(User user) {
        nameText.set(user.getNameEng());
        countryText.set(user.getCountry());
        birthdayText.set(user.getBirthday());
        heightText.set(user.getHeight() + "  " + FormatUtil.formatNumber(user.getWeight()) + "kg");
    }

    private void updateContent(ScorePageData data) {
        totalScoreText.set(String.valueOf(data.getCountScore()));
        if (data.getFrozenScore() > 0) {
            frozenScoreVisibility.set(View.VISIBLE);
            frozenScoreText.set("Frozen(" + data.getFrozenScore() + ")");
        }
        else {
            frozenScoreVisibility.set(View.GONE);
        }
        if (data.getRank() == 0) {
            rankText.set("--");
        }
        else {
            rankText.set(String.valueOf(data.getRank()));
        }
        matchCountText.set("Match count " + ListUtil.getSize(data.getScoreList()));
    }

    public void query52WeekRecords(long userId) {
        startYear = thisYear - 1;
        endYear = thisYear;
        startWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        endWeek = startWeek - 1;
        startYear--;
        queryUser(userId)
                .flatMap(user -> {
                    mUser = user;
                    userObserver.postValue(user);
                    return scoreModel.query52WeekRecords(user.getId(), currentYear);
                })
                .flatMap(list -> createPageData(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ScorePageData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(ScorePageData data) {
                        updateUserContent(mUser);
                        updateContent(data);
                        pageDataObserver.setValue(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load score failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void addScoreBeanToList(boolean isForce, Map<String, ScoreBean> map, List<ScoreBean> gsList, List<MatchNameBean> matchList, int i) {
        ScoreBean scoreBean = map.get(matchList.get(i).getName());
        if (scoreBean == null) {// 没有参加
            if (isForce) {// 强制计0
                scoreBean = new ScoreBean();
                scoreBean.setMatchBean(matchList.get(i));
                scoreBean.setScore(0);
                scoreBean.setChampion(false);
                setScoreBeanDetail(scoreBean);
            }
        }
        if (scoreBean != null) {
            gsList.add(scoreBean);
            map.remove(scoreBean.getMatchBean().getName());
        }
    }

    /**
     * 设置赛事年份、是否已完成标志
     * @param scoreBean
     */
    private void setScoreBeanDetail(ScoreBean scoreBean) {
        int matchWeek = scoreBean.getMatchBean().getMatchBean().getWeek();

        // 按year查询，所有的记录均为已完成记录
        if (startYear == endYear) {
            scoreBean.setYear(currentYear);
            if (endYear == thisYear) {
                scoreBean.setCompleted(matchWeek < thisWeek);
            }
            else {
                scoreBean.setCompleted(true);
            }
        }
        else {// 52 week
            if (matchWeek < thisWeek) {
                scoreBean.setYear(currentYear);
            }
            else {
                scoreBean.setYear(currentYear - 1);
            }

            if (endYear == thisYear) {// 去年到今年，今年的为已完成赛事，去年的为未完成待保分的赛事
                scoreBean.setCompleted(scoreBean.getYear() == thisYear);
            }
            else {
                scoreBean.setCompleted(true);
            }
        }
    }

    private Observable<ScorePageData> createPageData(final List<ScoreBean> list) {
        return Observable.create(new ObservableOnSubscribe<ScorePageData>() {
            @Override
            public void subscribe(ObservableEmitter<ScorePageData> e) throws Exception {
                e.onNext(getPageData(list));
            }
        });
    }

    private List<MatchNameBean> queryMatches(String level) {
        MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
        QueryBuilder<MatchNameBean> queryBuilder = dao.queryBuilder();
        queryBuilder.join(MatchNameBeanDao.Properties.MatchId, MatchBean.class)
            .where(MatchBeanDao.Properties.Level.eq(level));
        return queryBuilder.build().list();
    }

    private ScorePageData getPageData(List<ScoreBean> list) {
        scorePageData.setScoreList(list);

        // 戴维斯杯的积分情况较为复杂，本系统不考虑这种情况，按照other赛事0分处理
        // 4大满贯+年终总决赛+8站强制ATP1000+6站最好
        // 上一年年终top30的有500赛强制罚分，必须参加4项500赛，且有一项是美网后（蒙卡算500赛），只要参加即刻，可以不计入6站最好（比如6个250夺冠，就不计算4个500赛首轮游）
        // 非top30 按照取18站最好成绩的做法，若参加了大满贯和1000赛需要强制计入

        Map<String, ScoreBean> map = new HashMap<>();
        for (ScoreBean bean:list) {
            // 设置年份和完成情况
            setScoreBeanDetail(bean);
            map.put(bean.getMatchBean().getName(), bean);
        }

        // 查询上一年的年终排名
        Rank rank = null;
        try {
            rank = TApplication.getInstance().getDaoSession().getRankDao()
                    .queryBuilder()
                    .where(RankDao.Properties.UserId.eq(mUser.getId())
                            , RankDao.Properties.Year.eq(currentYear - 1))
                    .build().unique();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 上一年年终top30才有强制
        boolean isForce = rank == null ? false:(rank.getRank() > 0 && rank.getRank() <= 30);

        List<ScoreBean> replaceList = new ArrayList<>();

        // create gs list
        List<ScoreBean> gsList = new ArrayList<>();
        List<MatchNameBean> matchList = queryMatches(arrLevel[0]);
        if (matchList != null) {
            for (int i = 0; i < matchList.size(); i ++) {
                addScoreBeanToList(isForce, map, gsList, matchList, i);
            }
        }

        // create master cup
        matchList = queryMatches(arrLevel[1]);
        List<ScoreBean> mcList = new ArrayList<>();
        if (matchList != null) {
            for (int i = 0; i < matchList.size(); i ++) {
                addScoreBeanToList(false, map, mcList, matchList, i);
            }
        }

        // create 1000, 蒙卡先排除
        matchList = queryMatches(arrLevel[2]);
        List<ScoreBean> list1000 = new ArrayList<>();
        if (matchList != null) {
            for (int i = 0; i < matchList.size(); i ++) {
                if (matchList.get(i).getName().equals(AppConstants.MATCH_CONST_MONTECARLO)) {
                    if (map.get(AppConstants.MATCH_CONST_MONTECARLO) != null) {
                        continue;
                    }
                }
                addScoreBeanToList(isForce, map, list1000, matchList, i);
           }
        }
        ScoreBean removeBean = null;
        // 目前的历史记录里，西南财团公开赛和辛辛那提大师赛会出现重复，只保留有分的一站，如果都没分保留辛辛那提大师赛
        for (int i = 1; i < list1000.size(); i ++) {
            if (list1000.get(i).getMatchBean().getMatchId() == list1000.get(i - 1).getMatchBean().getMatchId()) {
                if (list1000.get(i - 1).getScore() > 0) {
                    removeBean = list1000.get(i);
                }
                else {
                    removeBean = list1000.get(i - 1);
                }
            }
        }
        if (removeBean != null) {
            list1000.remove(removeBean);
        }

        // 在剩下的赛事中检查需要罚分的站数（必须够4站，且有一站在美网之后）
        int countPunish = 4;
        if (isForce) {
            boolean hasAfterUsOpen = false;
            for (ScoreBean bean:map.values()) {
                String level = bean.getMatchBean().getMatchBean().getLevel();
                if (level.equals(arrLevel[3])) {// atp500
                    if (countPunish > 0) {
                        countPunish --;
                    }
                    if (bean.getMatchBean().getMatchBean().getWeek() > 35) {// 35是美网的周数
                        hasAfterUsOpen = true;
                    }
                }
                else if (level.equals(arrLevel[2])) {// 蒙特卡洛
                    if (countPunish > 0) {
                        countPunish --;
                    }
                }
            }
            if (countPunish == 0 && !hasAfterUsOpen) {
                countPunish ++;
            }
        }
        else {
            countPunish = 0;
        }

        // 将剩下的全部赛事按照积分排序
        for (ScoreBean bean:map.values()) {
            replaceList.add(bean);
        }
        Collections.sort(replaceList, scoreComparator);

        int countAvailable;
        // top30取包括罚分在内的6站最好成绩
        if (isForce) {
            countAvailable = 6 - countPunish;
        }
        // 非top30取 18-gs-atp1000 站最好成绩
        else {
            countAvailable = 18 - gsList.size() - list1000.size();
        }

        List<ScoreBean> list500 = new ArrayList<>();
        List<ScoreBean> list250 = new ArrayList<>();
        List<ScoreBean> listOther = new ArrayList<>();
        // atp cup归在other类，但是可计入总分
        List<ScoreBean> listAtpCup = new ArrayList<>();

        // 加上罚分赛事
        for (int i = 0; i < countPunish; i ++) {
            list500.add(getPunishScoreBean());
        }

        // 将countAvailable站赛事积分算入积分系统内（进入相应的list1000, 500, 250, replace, other）
        for (int i = 0; i < replaceList.size(); i ++) {
            ScoreBean bean = replaceList.get(i);
            String level = bean.getMatchBean().getMatchBean().getLevel();
            if (i < countAvailable) {
                if (level.equals(arrLevel[2])) {// 蒙特卡洛
                    list1000.add(bean);
                }
                else if (level.equals(arrLevel[3])) {
                    list500.add(bean);
                }
                else if (level.equals(arrLevel[4])) {
                    list250.add(bean);
                }
                else {
                    // 可算作积分的atp杯归为other类
                    if (level.equals(arrLevel[7])) {// ATP杯
                        listAtpCup.add(bean);// 这个list仅用于算作积分
                    }
                    listOther.add(bean);
                }
            }
            // 留在replace里的排除无积分的赛事（戴维斯杯、奥运会）
            else {
                if (level.equals(arrLevel[5]) || level.equals(arrLevel[6])) {// 戴维斯杯，奥运会
                    listOther.add(bean);
                }
            }
        }
        for (int i = countAvailable - 1; i >= 0; i --) {
            if (i < replaceList.size()) {
                replaceList.remove(i);
            }
        }

        scorePageData.setGsList(gsList);
        scorePageData.setMasterCupList(mcList);
        scorePageData.setAtp1000List(list1000);
        scorePageData.setAtp500List(list500);
        scorePageData.setAtp250List(list250);
        scorePageData.setReplaceList(replaceList);
        scorePageData.setOtherList(listOther);

        // 生成统计数据，统计范围为算进积分的赛事
        int countScore = 0, countYear = 0, countLastYear = 0, countHard = 0, countClay = 0
                , countGrass = 0, countInHard = 0;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<ScoreBean> availableList = new ArrayList<>();
        availableList.addAll(gsList);
        availableList.addAll(mcList);
        availableList.addAll(list1000);
        availableList.addAll(list500);
        availableList.addAll(list250);
        availableList.addAll(listAtpCup);

        for (ScoreBean bean:availableList) {
            if (bean.getMatchBean() == null) {
                continue;
            }
            // count score
            countScore += bean.getScore();

            // count year score
            if (bean.getYear() == year) {
                countYear += bean.getScore();
            }
            else {
                countLastYear += bean.getScore();
            }

            String court = bean.getMatchBean().getMatchBean().getCourt();
            // count court score
            if (court.equals(arrCourt[1])) {
                countClay += bean.getScore();
            }
            else if (court.equals(arrCourt[2])) {
                countGrass += bean.getScore();
            }
            else if (court.equals(arrCourt[3])) {
                countInHard += bean.getScore();
            }
            else {
                countHard += bean.getScore();
            }
        }

        // 冻结积分(2020-03月开始疫情影响)
        int frozenScore = 0;
        List<FrozenScore> frozenList = TApplication.getInstance().getDaoSession().getFrozenScoreDao().queryBuilder()
                .where(FrozenScoreDao.Properties.UserId.eq(mUser.getId()))
                .build().list();
        for (FrozenScore fs:frozenList) {
            frozenScore += fs.getScore();
        }
        scorePageData.setFrozenScore(frozenScore);
        countScore += frozenScore;

        scorePageData.setCountScore(countScore);
        scorePageData.setCountScoreClay(countClay);
        scorePageData.setCountScoreGrass(countGrass);
        scorePageData.setCountScoreInHard(countInHard);
        scorePageData.setCountScoreHard(countHard);
        scorePageData.setCountScoreYear(countYear);
        scorePageData.setCountScoreLastYear(countLastYear);

        // load rank
        RankWeekDao weekDao = TApplication.getInstance().getDaoSession().getRankWeekDao();
        try {
            RankWeek week = weekDao.queryBuilder()
                    .where(RankWeekDao.Properties.UserId.eq(mUser.getId()))
                    .orderDesc(RankWeekDao.Properties.Date)
                    .limit(1)
                    .build().unique();
            scorePageData.setRank(week.getRank());
        } catch (DaoException e) {}
        return scorePageData;
    }

    private ScoreBean getPunishScoreBean() {
        ScoreBean bean = new ScoreBean();
        bean.setYear(currentYear);
        bean.setMatchBean(null);
        bean.setCompleted(false);
        bean.setChampion(false);
        bean.setScore(0);
        return bean;
    }

    public int getThisYear() {
        return thisYear;
    }

    public void setCurrentYear(int year) {
        this.currentYear = year;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public int getRank() {
        return scorePageData.getRank();
    }

    public List<Object> getScoresByLevel() {
        List<Object> scoreList = new ArrayList<>();
        // 积分、图表
        // gs
        ScoreBean titleBean = new ScoreBean();
        titleBean.setTitle(AppConstants.RECORD_MATCH_LEVELS[0]);
        titleBean.setTitle(true);
        scoreList.add(titleBean);
        scoreList.addAll(scorePageData.getGsList());

        // master cup
        titleBean = new ScoreBean();
        titleBean.setTitle(AppConstants.RECORD_MATCH_LEVELS[1]);
        titleBean.setTitle(true);
        scoreList.add(titleBean);
        scoreList.addAll(scorePageData.getMasterCupList());

        // 1000
        titleBean = new ScoreBean();
        titleBean.setTitle(AppConstants.RECORD_MATCH_LEVELS[2]);
        titleBean.setTitle(true);
        scoreList.add(titleBean);
        scoreList.addAll(scorePageData.getAtp1000List());

        // 500
        titleBean = new ScoreBean();
        titleBean.setTitle(AppConstants.RECORD_MATCH_LEVELS[3]);
        titleBean.setTitle(true);
        scoreList.add(titleBean);
        scoreList.addAll(scorePageData.getAtp500List());

        // 250
        titleBean = new ScoreBean();
        titleBean.setTitle(AppConstants.RECORD_MATCH_LEVELS[4]);
        titleBean.setTitle(true);
        scoreList.add(titleBean);
        scoreList.addAll(scorePageData.getAtp250List());

        // replace
        titleBean = new ScoreBean();
        titleBean.setTitle("Replace");
        titleBean.setTitle(true);
        scoreList.add(titleBean);
        scoreList.addAll(scorePageData.getReplaceList());

        // other
        titleBean = new ScoreBean();
        titleBean.setTitle("Other");
        titleBean.setTitle(true);
        scoreList.add(titleBean);
        scoreList.addAll(scorePageData.getOtherList());
        return scoreList;
    }

    public List<Object> getScoresByMonth() {
        List<Object> scoreList = new ArrayList<>();

        // 先将计入积分的赛事按week升序排序
        List<ScoreBean> validList = new ArrayList<>();
        validList.addAll(scorePageData.getGsList());
        validList.addAll(scorePageData.getMasterCupList());
        validList.addAll(scorePageData.getAtp1000List());
        validList.addAll(scorePageData.getAtp500List());
        validList.addAll(scorePageData.getAtp250List());
        Collections.sort(validList, new Comparator<ScoreBean>() {
            @Override
            public int compare(ScoreBean left, ScoreBean right) {
                int l = left.getMatchBean() == null ? 0:left.getMatchBean().getMatchBean().getWeek();
                int r = right.getMatchBean() == null ? 0:right.getMatchBean().getMatchBean().getWeek();
                return  l- r;
            }
        });

        List<ScoreBean> punishList = new ArrayList<>();
        // 组装title与sub
        int lastMonth = 0;
        for (ScoreBean bean:validList) {
            // 罚分
            if (bean.getMatchBean() == null) {
                punishList.add(bean);
                continue;
            }

            int month = bean.getMatchBean().getMatchBean().getMonth();
            if (month != lastMonth) {
                ScoreBean titleBean = new ScoreBean();
                titleBean.setTitle(AppConstants.MONTH_CHN[month - 1]);
                titleBean.setTitle(true);
                scoreList.add(titleBean);
                lastMonth = month;
            }
            scoreList.add(bean);
        }

        // punish
        ScoreBean titleBean;
        if (punishList.size() > 0) {
            titleBean = new ScoreBean();
            titleBean.setTitle("罚分");
            titleBean.setTitle(true);
            scoreList.add(titleBean);
            scoreList.addAll(punishList);
        }

        // replace
        titleBean = new ScoreBean();
        titleBean.setTitle("Replace");
        titleBean.setTitle(true);
        scoreList.add(titleBean);
        scoreList.addAll(scorePageData.getReplaceList());

        // other
        titleBean = new ScoreBean();
        titleBean.setTitle("Other");
        titleBean.setTitle(true);
        scoreList.add(titleBean);
        scoreList.addAll(scorePageData.getOtherList());
        return scoreList;
    }

    public void loadUser(long userId) {
        queryUser(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(User user) {
                        userObserver.setValue(user);
                        updateUserContent(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 将赛事按照积分从大到小排列
     */
    private class ScoreComparator implements Comparator<ScoreBean> {

        @Override
        public int compare(ScoreBean lhs, ScoreBean rhs) {
            return rhs.getScore() - lhs.getScore();
        }
    }

    /**
     * 将赛事按照赛事序号从小到大排列
     */
    private class MatchSeqComparator implements Comparator<ScoreBean> {

        @Override
        public int compare(ScoreBean lhs, ScoreBean rhs) {
            int lMatchSeq = 0;
            int rMatchSeq = 0;
            if (lhs.getMatchBean() != null) {
                lMatchSeq = lhs.getMatchBean().getMatchBean().getWeek();
            }
            if (rhs.getMatchBean() != null) {
                rMatchSeq = rhs.getMatchBean().getMatchBean().getWeek();
            }
            return lMatchSeq - rMatchSeq;
        }
    }
}
