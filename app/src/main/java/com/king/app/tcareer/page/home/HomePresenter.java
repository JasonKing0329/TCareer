package com.king.app.tcareer.page.home;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.MatchModel;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.page.match.gallery.UserMatchBean;
import com.king.app.tcareer.page.match.gallery.UserMatchPresenter;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.DBExportor;
import com.king.app.tcareer.utils.RetireUtil;

import org.greenrobot.greendao.DaoException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/4/3 0003.
 */

public class HomePresenter extends BasePresenter<IHomeView> {

    private final int PLAYER_NUM = 10;

    private List<User> mAllUsers;

    private List<UserMatchBean> matchList;

    @Override
    protected void onCreate() {

    }

    public List<User> getAllUsers() {
        return mAllUsers;
    }

    /**
     * load login user,all users,
     * load last record,recent players,matches for login user
     */
    public void loadHomeDatas() {
        view.showLoading();
        long userId = SettingProperty.getUserId();
        if (userId == -1) {
            userId = AppConstants.USER_ID_KING;
        }
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<List<User>>>() {
                    @Override
                    public ObservableSource<List<User>> apply(User user) throws Exception {
                        view.postShowCurrentUser();
                        return queryAllUsers();
                    }
                }).flatMap(new Function<List<User>, ObservableSource<Record>>() {
                    @Override
                    public ObservableSource<Record> apply(List<User> users) throws Exception {
                        mAllUsers = users;
                        view.postShowAllUsers();
                        return queryLastRecord();
                    }
                })
                .flatMap(new Function<Record, ObservableSource<List<CompetitorBean>>>() {
                    @Override
                    public ObservableSource<List<CompetitorBean>> apply(Record record) throws Exception {
                        view.postShowLastRecord(record);
                        return queryLastPlayers();
                    }
                })
                .flatMap(new Function<List<CompetitorBean>, ObservableSource<List<UserMatchBean>>>() {
                    @Override
                    public ObservableSource<List<UserMatchBean>> apply(List<CompetitorBean> list) throws Exception {
                        view.postShowCompetitors(list);
                        return queryMatches();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<UserMatchBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<UserMatchBean> list) {
                        matchList = list;
                        view.dismissLoading();
                        view.showMatches(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("errors: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * change login user
     * load last record,recent players,matches for login user
     * @param user
     */
    public void changeUser(User user) {
        mUser = user;
        SettingProperty.setUserId(user.getId());
        view.postShowCurrentUser();
        queryLastRecord()
                .flatMap(new Function<Record, ObservableSource<List<CompetitorBean>>>() {
                    @Override
                    public ObservableSource<List<CompetitorBean>> apply(Record record) throws Exception {
                        view.postShowLastRecord(record);
                        return queryLastPlayers();
                    }
                })
                .flatMap(new Function<List<CompetitorBean>, ObservableSource<List<UserMatchBean>>>() {
                    @Override
                    public ObservableSource<List<UserMatchBean>> apply(List<CompetitorBean> list) throws Exception {
                        view.postShowCompetitors(list);
                        return queryMatches();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<UserMatchBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<UserMatchBean> list) {
                        matchList = list;
                        view.showMatches(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("errors: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * record changed, refresh last record and recent competitors
     */
    public void setRecordChanged() {
        queryLastRecord()
                .flatMap(new Function<Record, ObservableSource<List<CompetitorBean>>>() {
                    @Override
                    public ObservableSource<List<CompetitorBean>> apply(Record record) throws Exception {
                        view.postShowLastRecord(record);
                        return queryLastPlayers();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<CompetitorBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<CompetitorBean> list) {
                        view.postShowCompetitors(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("errors: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<List<User>> queryAllUsers() {
        return Observable.create(new ObservableOnSubscribe<List<User>>() {
            @Override
            public void subscribe(ObservableEmitter<List<User>> e) throws Exception {
                UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
                List<User> list = dao.queryBuilder()
                        .build().list();
                e.onNext(list);
            }
        });
    }

    private Observable<Record> queryLastRecord() {
        return Observable.create(new ObservableOnSubscribe<Record>() {
            @Override
            public void subscribe(ObservableEmitter<Record> e) throws Exception {
                RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
                Record record = dao.queryBuilder()
                        .where(RecordDao.Properties.UserId.eq(mUser.getId()))
                        .orderDesc(RecordDao.Properties.Id)
                        .limit(1)
                        .build().unique();
                e.onNext(record);
            }
        });
    }

    private Observable<List<CompetitorBean>> queryLastPlayers() {
        return Observable.create(new ObservableOnSubscribe<List<CompetitorBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<CompetitorBean>> e) throws Exception {
                Map<String, CompetitorBean> map = new HashMap<>();
                RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
                List<Record> list = dao.queryBuilder()
                        .where(RecordDao.Properties.UserId.eq(mUser.getId()))
                        .orderDesc(RecordDao.Properties.Id)
                        .build().list();
                List<CompetitorBean> cptList = new ArrayList<>();
                int count = 0;
                for (int i = 0; count < PLAYER_NUM && i < list.size(); i ++) {
                    Record record = list.get(i);
                    String key = record.getPlayerId() + "_" + record.getPlayerFlag();
                    CompetitorBean bean = map.get(key);
                    if (bean == null) {
                        bean = CompetitorParser.getCompetitorFrom(record);
                        map.put(key, bean);
                        cptList.add(bean);
                        count ++;
                    }
                }
                e.onNext(cptList);
            }
        });
    }

    public Observable<List<UserMatchBean>> queryMatches() {
        return new UserMatchPresenter().queryMatches(mUser.getId());
    }

    public int findLatestWeekItem() {
        return new MatchModel().findLatestWeekItem(matchList);
    }

    public void saveDatabase() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                DBExportor.exportAsHistory();
                e.onNext(new Object());
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object object) {
                        view.showMessage("save successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("save failed: " + e.getMessage());
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
                        view.notifyRankFound(list);
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
}
