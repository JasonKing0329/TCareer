package com.king.app.tcareer.page.player.page;

import android.text.TextUtils;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.setting.SettingProperty;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/4/27 13:16
 */
public class SubPagePresenter extends BasePresenter<SubPageView> {

    protected CompetitorBean mCompetitor;

    public static final int TYPE_CARD = 0;
    public static final int TYPE_PURE = 1;

    private int mViewType;

    private Map<Integer, FullRecordBean> yearMap;

    @Override
    protected void onCreate() {
        yearMap = new HashMap<>();
        mViewType = SettingProperty.getPlayerPageViewType();
    }

    public void setCompetitor(CompetitorBean mCompetitor) {
        this.mCompetitor = mCompetitor;
    }

    public int getViewType() {
        return mViewType;
    }

    public void createRecords(long userId, String court, String level, String year) {
        createRecords(userId, court, mViewType, level, year);
    }

    private Observable<List<Record>> queryRecords(final long userId, final String court, final String level, final String year) {
        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
                QueryBuilder<Record> builder = dao.queryBuilder();
                builder.where(RecordDao.Properties.PlayerId.eq(mCompetitor.getId()));
                if (mCompetitor instanceof User) {
                    builder.where(RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_VIRTUAL));
                }
                else {
                    builder.where(RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_NORMAL));
                }
                if (userId != -1) {
                    builder.where(RecordDao.Properties.UserId.eq(userId));
                }
                if (!TextUtils.isEmpty(year) && !year.equals(SubPageModel.TAB_ALL)) {
                    builder.where(RecordDao.Properties.DateStr.like(year + "%"));
                }
                List<Record> list = builder.build().list();
                List<Record> results = new ArrayList<>();
                for (Record record : list) {
                    boolean isPast = true;
                    // filter court
                    if (!TextUtils.isEmpty(court) && !court.equals(SubPageModel.TAB_ALL)) {
                        if (!record.getMatch().getMatchBean().getCourt().equals(court)) {
                            isPast = false;
                        }
                    }
                    // filter level
                    if (!TextUtils.isEmpty(level) && !level.equals(SubPageModel.TAB_ALL)) {
                        if (!record.getMatch().getMatchBean().getLevel().equals(level)) {
                            isPast = false;
                        }
                    }
                    if (isPast) {
                        results.add(record);
                    }
                }

                Collections.reverse(results);
                e.onNext(results);
            }
        });
    }

    private ObservableSource<List<Object>> convertToYearAtFirst(final List<Record> recordList) {
        return new ObservableSource<List<Object>>() {
            @Override
            public void subscribe(Observer<? super List<Object>> observer) {
                yearMap.clear();
                List<Object> list = new ArrayList<>();
                FullRecordBean yearCountBean = new FullRecordBean();
                for (int i = 0; i < recordList.size(); i++) {
                    Record record = recordList.get(i);
                    FullRecordBean bean = new FullRecordBean();
                    bean.record = record;
                    list.add(bean);

                    int year = Integer.parseInt(record.getDateStr().split("-")[0]);
                    if (year != yearCountBean.year) {
                        yearMap.put(year, bean);
                        bean.isYearFirst = true;
                        bean.year = year;
                        yearCountBean = bean;
                    }
                    if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                        if (record.getRetireFlag() != AppConstants.RETIRE_WO) {
                            yearCountBean.yearWin ++;
                        }
                    }
                    else {
                        yearCountBean.yearLose ++;
                    }
                }
                observer.onNext(list);
            }
        };
    }

    private void createRecords(final long userId, final String court, final int type, final String level, final String year) {
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<List<Record>>>() {
                    @Override
                    public ObservableSource<List<Record>> apply(User user) throws Exception {
                        return queryRecords(userId, court, level, year);
                    }
                })
                .flatMap(new Function<List<Record>, ObservableSource<List<Object>>>() {
                    @Override
                    public ObservableSource<List<Object>> apply(List<Record> records) throws Exception {
                        if (type == TYPE_PURE) {
                            return convertToYearAtFirst(records);
                        }
                        else {
                            return convertToYearAsTitle(records);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Object>>() {
                    @Override
                    public void accept(List<Object> list) throws Exception {
                        view.onDataLoaded(list, mViewType);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public String getYearTitle(int year) {
        FullRecordBean bean = yearMap.get(year);
        return bean.year + "\n" + bean.yearWin + "-" + bean.yearLose;
    }

    protected PageTitleBean countTitle(int year, List<Record> records) {
        PageTitleBean bean = new PageTitleBean();
        int win = 0, lose = 0;
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            //如果是赛前退赛不算作h2h
            if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                continue;
            } else {
                if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                    lose++;
                } else {
                    win++;
                }
            }
        }
        bean.setYear(year);
        bean.setWin(win);
        bean.setLose(lose);
        return bean;
    }

    private ObservableSource<List<Object>> convertToYearAsTitle(final List<Record> recordList) {
        return new ObservableSource<List<Object>>() {
            @Override
            public void subscribe(Observer<? super List<Object>> observer) {
                List<Object> list = new ArrayList<>();
                Map<Integer, List<Record>> map = new HashMap<>();
                for (int i = 0; i < recordList.size(); i++) {
                    Record record = recordList.get(i);
                    String strYear = record.getDateStr().split("-")[0];
                    int year = Integer.parseInt(strYear);
                    List<Record> child = map.get(year);
                    if (child == null) {
                        child = new ArrayList<>();
                        map.put(year, child);
                    }
                    child.add(record);
                }

                // 按year降序
                Iterator<Integer> it = map.keySet().iterator();
                List<Integer> yearList = new ArrayList<>();
                while (it.hasNext()) {
                    yearList.add(it.next());
                }
                Collections.sort(yearList);
                Collections.reverse(yearList);

                for (Integer year : yearList) {
                    List<Record> records = map.get(year);
                    PageTitleBean title = countTitle(year, records);
                    list.add(title);
                    list.addAll(records);
                }

                observer.onNext(list);
            }
        };
    }
}
