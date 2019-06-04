package com.king.app.tcareer.page.home.main;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.bean.MatchImageBean;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchBeanDao;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.utils.DBExportor;
import com.king.app.tcareer.utils.RetireUtil;
import com.king.app.tcareer.view.widget.scoreboard.BoardStyleProvider;
import com.king.app.tcareer.view.widget.scoreboard.ScoreBoardParam;

import org.greenrobot.greendao.DaoException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/14 17:09
 */
public class MainHomeViewModel extends BaseViewModel {

    public ObservableField<String> titleText = new ObservableField<>("This Week");

    public MutableLiveData<List<User>> usersObserver = new MutableLiveData<>();

    public MutableLiveData<List<MatchImageBean>> matchObserver = new MutableLiveData<>();

    public MutableLiveData<List<ScoreBoardParam>> scoreboardsObserver = new MutableLiveData<>();

    public MutableLiveData<List<NotifyRankBean>> notifyRankFound = new MutableLiveData<>();

    public MainHomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadData() {
        loadUsers();
        loadMatches();
        loadRecords();
        checkWeekRank();
    }

    /**
     * 检查是否更新week rank
     */
    public void checkWeekRank() {
        getNotifications()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<NotifyRankBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<NotifyRankBean> list) {
                        notifyRankFound.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<NotifyRankBean>> getNotifications() {
        return Observable.create(e -> {
            RankWeekDao rankDao = TApplication.getInstance().getDaoSession().getRankWeekDao();
            UserDao userDao = TApplication.getInstance().getDaoSession().getUserDao();
            List<User> users = userDao.queryBuilder().build().list();
            List<NotifyRankBean> list = new ArrayList<>();

            // 因为数据库存的是yyyy-MM-dd转化而成的date，所以在取今天的时候也要转化一下，否则后面的比较会出问题
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String strToday = format.format(new Date());
            Date today = format.parse(strToday);

            for (int i = 0; i < users.size(); i ++) {
                // 已退役生效，不再检查是否更新排名
                if (RetireUtil.isEffectiveRetiredNow(users.get(i).getId())) {
                    continue;
                }
                RankWeek rankWeek = null;
                try {
                    rankWeek = rankDao.queryBuilder()
                            .where(RankWeekDao.Properties.UserId.eq(users.get(i).getId()))
                            .orderDesc(RankWeekDao.Properties.Date)
                            .limit(1)
                            .build().unique();
                } catch (DaoException de) {}

                if (rankWeek != null) {
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(today);
                    // 周日是1，周一是2 ...
                    int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK);
                    // 采用week和day的计算方式可以解决跨年的问题
                    // 如果今天是星期日，星期一，比较最近一条是否小于上周一
                    if (dayOfWeek < 3) {
                        gc.add(GregorianCalendar.WEEK_OF_YEAR, -1);
                        gc.add(GregorianCalendar.DAY_OF_YEAR, 2 - dayOfWeek);
                    }
                    // 如果今天是星期二到星期六，比较最近一条是否小于本周一
                    else {
                        gc.add(GregorianCalendar.DAY_OF_YEAR, 2 - dayOfWeek);
                    }
                    Date targetMonday = gc.getTime();

                    if (rankWeek.getDate().getTime() < targetMonday.getTime()) {
                        NotifyRankBean bean = new NotifyRankBean();
                        bean.setUser(users.get(i));
                        bean.setLastRank(rankWeek);
                        list.add(bean);
                    }
                }
            }
            e.onNext(list);
        });
    }

    private void loadUsers() {
        queryAllUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<User> users) {
                        usersObserver.setValue(users);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<User>> queryAllUsers() {
        return Observable.create(e -> {
            List<User> list = getDaoSession().getUserDao().queryBuilder()
                    .build().list();
            for (User user:list) {
                user.setImageUrl(ImageProvider.getDetailPlayerPath(user.getNameChn()));
            }
            e.onNext(list);
        });
    }

    private void loadMatches() {
        queryWeekMatches()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<MatchImageBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<MatchImageBean> list) {
                        matchObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void loadRecords() {
        getLatestRecords()
                .flatMap(list -> toScoreBoards(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<ScoreBoardParam>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<ScoreBoardParam> records) {
                        scoreboardsObserver.setValue(records);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<MatchImageBean>> queryWeekMatches() {
        return Observable.create(e -> {
            int week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
            List<MatchBean> list = getDaoSession().getMatchBeanDao().queryBuilder()
                    .where(MatchBeanDao.Properties.Week.eq(week))
                    .build().list();
            int thisWeek = week;
            while (list.size() == 0 && week > 0) {
                week --;
                list = getDaoSession().getMatchBeanDao().queryBuilder()
                        .where(MatchBeanDao.Properties.Week.eq(week))
                        .build().list();
            }
            if (week != thisWeek) {
                titleText.set("Week " + week);
            }

            List<MatchImageBean> matches = new ArrayList<>();
            for (MatchBean bean:list) {
                // 只取最近的名称
                MatchNameBean nameBean = bean.getNameBeanList().get(bean.getNameBeanList().size() - 1);

                MatchImageBean imageBean = new MatchImageBean();
                imageBean.setBean(nameBean);
                imageBean.setImageUrl(ImageProvider.getMatchHeadPath(nameBean.getName(), nameBean.getMatchBean().getCourt()));
                matches.add(imageBean);
            }
            e.onNext(matches);
        });
    }

    private Observable<List<Record>> getLatestRecords() {
        return Observable.create(e -> {
            List<Record> list = getDaoSession().getRecordDao().queryBuilder()
                    .orderDesc(RecordDao.Properties.Id)
                    .limit(10)
                    .build().list();
            e.onNext(list);
        });
    }

    /**
     * short for name
     * eg. set Novak Djokovic as N.Djokovic
     * @param name
     * @return
     */
    private String formatEngName(String name, CompetitorBean bean) {
        try {
            String[] arr = name.split(" ");
            if (bean instanceof User && (bean.getId() == AppConstants.USER_ID_KING || bean.getId() == AppConstants.USER_ID_QI)) {
                return arr[1].charAt(0) + "." + arr[0];
            }
            else {
                return arr[0].charAt(0) + "." + arr[1];
            }
        } catch (Exception e) {}
        return name;
    }

    private ObservableSource<List<ScoreBoardParam>> toScoreBoards(List<Record> list) {
        return observer -> {
            List<ScoreBoardParam> result = new ArrayList<>();
            BoardStyleProvider styleProvider = new BoardStyleProvider();
            for (Record record:list) {
                ScoreBoardParam param = new ScoreBoardParam();
                param.setRecord(record);
                if (record.getSeed() > 0) {
                    param.setPlayer1(formatEngName(record.getUser().getNameEng(), record.getUser()) + "[" + record.getSeed() + "]");
                }
                else {
                    param.setPlayer1(formatEngName(record.getUser().getNameEng(), record.getUser()));
                }
                CompetitorBean bean = CompetitorParser.getCompetitorFrom(record);
                if (record.getSeedpCpt() > 0) {
                    param.setPlayer2(formatEngName(bean.getNameEng(), bean) + "[" + record.getSeedpCpt() + "]");
                }
                else {
                    param.setPlayer2(formatEngName(bean.getNameEng(), bean));
                }
                param.setPlayerUrl1(ImageProvider.getPlayerHeadPath(record.getUser().getNameChn()));
                param.setPlayerUrl2(ImageProvider.getPlayerHeadPath(bean.getNameChn()));
                if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                    param.setWinnerIndex(0);
                }
                else {
                    param.setWinnerIndex(1);
                }
                param.setMatchName(record.getMatch().getName());
                param.setRound(AppConstants.getRoundShortName(record.getRound()));
                param.setScoreList(record.getScoreList());

                if (record.getMatch().getMatchBean().getCourt().equals(AppConstants.RECORD_MATCH_COURTS[0])) {
                    param.setBoardStyle(styleProvider.getAustriliaOpen());
                }
                else if (record.getMatch().getMatchBean().getCourt().equals(AppConstants.RECORD_MATCH_COURTS[1])) {
                    param.setBoardStyle(styleProvider.getFrenchOpen());
                }
                else if (record.getMatch().getMatchBean().getCourt().equals(AppConstants.RECORD_MATCH_COURTS[2])) {
                    param.setBoardStyle(styleProvider.getWimbledonOpen());
                }
                else {
                    param.setBoardStyle(styleProvider.getDefault());
                }

                result.add(param);
            }
            observer.onNext(result);
        };
    }

    public void saveDatabase() {
        Observable.create(e -> {
            DBExportor.exportAsHistory();
            e.onNext(new Object());
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object object) {
                        messageObserver.setValue("save successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("save failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
