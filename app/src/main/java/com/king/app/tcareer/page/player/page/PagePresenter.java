package com.king.app.tcareer.page.player.page;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.utils.ConstellationUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: handle operations of player page activity and fragment
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 15:48
 */
public class PagePresenter extends BasePresenter<IPageView> {

    private final String TAB_ALL = "全部";

    private CompetitorBean mCompetitor;

    private User mUser;

    private List<Record> recordList;

    @Override
    protected void onCreate() {

    }

    public void loadPlayerAndUser(final long playerId, final long userId, final boolean playerIsUser) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                UserDao userDao = TApplication.getInstance().getDaoSession().getUserDao();
                mUser = userDao.queryBuilder()
                        .where(UserDao.Properties.Id.eq(userId))
                        .build().unique();

                if (playerIsUser) {
                    mCompetitor = userDao.queryBuilder()
                            .where(UserDao.Properties.Id.eq(playerId))
                            .build().unique();
                }
                else {
                    PlayerBeanDao playerBeanDao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
                    mCompetitor = playerBeanDao.queryBuilder()
                            .where(PlayerBeanDao.Properties.Id.eq(playerId))
                            .build().unique();
                }
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
                    public void onNext(Object playerBean) {
                        loadPlayerInfor();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showError("Load player failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadPlayerInfor() {
        String constel = null;
        try {
            constel = ConstellationUtil.getConstellationChn(mCompetitor.getBirthday());
        } catch (ConstellationUtil.ConstellationParseException e) {
            e.printStackTrace();
        }
        String info = mCompetitor.getNameChn() + "，" + mCompetitor.getBirthday() + "，" + constel;
        view.showPlayerInfo(mCompetitor.getNameEng(), info
                , ImageProvider.getPlayerHeadPath(mCompetitor.getNameChn())
                , mCompetitor.getCountry());
    }

    public void loadRecords() {
        Observable.create(new ObservableOnSubscribe<List<TabBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TabBean>> e) throws Exception {

                RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
                WhereCondition competitorCond[] = new WhereCondition[2];
                if (mCompetitor instanceof User) {
                    competitorCond[0] = RecordDao.Properties.PlayerId.eq(mCompetitor.getId());
                    competitorCond[1] = RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_VIRTUAL);
                }
                else {
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

                for (int i = 0; i < AppConstants.RECORD_MATCH_COURTS.length; i ++) {
                    TabBean tab = new TabBean();
                    tab.name = AppConstants.RECORD_MATCH_COURTS[i];
                    tabList.add(tab);
                }

                for (int i = 0; i < recordList.size(); i ++) {
                    Record record = recordList.get(i);
                    MatchBean matchBean = record.getMatch().getMatchBean();
                    // count h2h by court
                    if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[0])) {
                        tabList.get(0).total ++;
                        //如果是赛前退赛不算作h2h
                        if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                            continue;
                        }
                        else {
                            if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                                tabList.get(0).lose ++;
                            }
                            else {
                                tabList.get(0).win ++;
                            }
                        }
                    }
                    else if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[1])) {
                        tabList.get(1).total ++;
                        //如果是赛前退赛不算作h2h
                        if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                            continue;
                        }
                        else {
                            if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                                tabList.get(1).lose ++;
                            }
                            else {
                                tabList.get(1).win ++;
                            }
                        }
                    }
                    else if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[3])) {
                        tabList.get(3).total ++;
                        //如果是赛前退赛不算作h2h
                        if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                            continue;
                        }
                        else {
                            if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                                tabList.get(3).lose ++;
                            }
                            else {
                                tabList.get(3).win ++;
                            }
                        }
                    }
                    else if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[2])) {
                        tabList.get(2).total ++;
                        //如果是赛前退赛不算作h2h
                        if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                            continue;
                        }
                        else {
                            if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                                tabList.get(2).lose ++;
                            }
                            else {
                                tabList.get(2).win ++;
                            }
                        }
                    }
                }

                TabBean tabAll = new TabBean();
                tabAll.name = TAB_ALL;
                // 如果没有记录就不显示这个tab
                for (int i = tabList.size() - 1; i >= 0; i --) {
                    tabAll.win += tabList.get(i).win;
                    tabAll.lose += tabList.get(i).lose;
                    tabAll.total += tabList.get(i).total;
                    if (tabList.get(i).total == 0) {
                        tabList.remove(i);
                    }
                }
                tabList.add(0, tabAll);

                e.onNext(tabList);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<TabBean>>() {
                    @Override
                    public void accept(List<TabBean> list) throws Exception {
                        view.onTabLoaded(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        view.showError("loadRecords error: " + throwable.getMessage());
                    }
                });
    }

    public void createRecords(final String tabName, final IPageCallback callback) {
        Observable.create(new ObservableOnSubscribe<List<Object>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Object>> e) throws Exception {
                List<Object> list = new ArrayList<>();
                Map<Integer, List<Record>> map = new HashMap<>();
                for (int i = 0; i < recordList.size(); i ++) {
                    Record record = recordList.get(i);
                    if (tabName.equals(TAB_ALL) || tabName.equals(record.getMatch().getMatchBean().getCourt())) {
                        String strYear = record.getDateStr().split("-")[0];
                        int year = Integer.parseInt(strYear);
                        List<Record> child = map.get(year);
                        if (child == null) {
                            child = new ArrayList<>();
                            map.put(year, child);
                        }
                        child.add(record);
                    }
                }

                // 按year降序
                Iterator<Integer> it = map.keySet().iterator();
                List<Integer> yearList = new ArrayList<>();
                while (it.hasNext()) {
                    yearList.add(it.next());
                }
                Collections.sort(yearList);
                Collections.reverse(yearList);

                for (Integer year:yearList) {
                    List<Record> records = map.get(year);
                    PageTitleBean title = countTitle(year, records);
                    list.add(title);
                    list.addAll(records);
                }

                e.onNext(list);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Object>>() {
                    @Override
                    public void accept(List<Object> list) throws Exception {
                        callback.onDataLoaded(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private PageTitleBean countTitle(int year, List<Record> records) {
        PageTitleBean bean = new PageTitleBean();
        int win = 0, lose = 0;
        for (int i = 0; i < records.size(); i ++) {
            Record record = records.get(i);
            //如果是赛前退赛不算作h2h
            if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                continue;
            }
            else {
                if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                    lose ++;
                } else {
                    win ++;
                }
            }
        }
        bean.setYear(year);
        bean.setWin(win);
        bean.setLose(lose);
        return bean;
    }

    public User getUser() {
        return mUser;
    }
}
