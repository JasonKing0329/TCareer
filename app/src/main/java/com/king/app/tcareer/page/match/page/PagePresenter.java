package com.king.app.tcareer.page.match.page;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;

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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/21 14:14
 */
public class PagePresenter extends BasePresenter<IPageView> {

    private MatchNameBean mMatchBean;

    private int win;

    private int lose;

    @Override
    protected void onCreate() {

    }

    public void loadData(final long matchNameId, final long userId) {
        view.showLoading();
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<MatchNameBean>>() {
                    @Override
                    public ObservableSource<MatchNameBean> apply(User user) throws Exception {
                        return new ObservableSource<MatchNameBean>() {
                            @Override
                            public void subscribe(Observer<? super MatchNameBean> observer) {
                                // load match
                                MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
                                MatchNameBean bean = dao.queryBuilder()
                                        .where(MatchNameBeanDao.Properties.Id.eq(matchNameId))
                                        .build().unique();
                                observer.onNext(bean);
                            }
                        };
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<MatchNameBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(MatchNameBean matchNameBean) {
                        mMatchBean = matchNameBean;
                        view.showMatchInfo(mMatchBean.getName(), mMatchBean.getMatchBean().getCountry(), mMatchBean.getMatchBean().getCity()
                                , mMatchBean.getMatchBean().getLevel(), mMatchBean.getMatchBean().getCourt());
                        loadRecords(userId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showError("Load match failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void loadRecords(final long userId) {
        Observable.create(new ObservableOnSubscribe<List<Object>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Object>> e) throws Exception {
                /**
                 * 按照matchId来查询, 解决不同赛事名称但是是一站赛事的问题
                 */
                List<MatchNameBean> matches = mMatchBean.getMatchBean().getNameBeanList();

                List<Record> recordList = new ArrayList<>();
                RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
                for (MatchNameBean matchNameBean:matches) {
                    List<Record> list = recordDao.queryBuilder()
                            .where(RecordDao.Properties.MatchNameId.eq(matchNameBean.getId())
                                , RecordDao.Properties.UserId.eq(userId))
                            .build().list();
                    recordList.addAll(list);
                }

                // 统计胜负场
                countWinLose(recordList);

                // 查出来的是时间升序，按时间降序排列
                Collections.reverse(recordList);

                List<Object> list = new ArrayList<>();
                Map<Integer, List<Record>> map = new HashMap<>();

                for (int i = 0; i < recordList.size(); i ++) {
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
                        view.dismissLoading();
                        view.onRecordsLoaded(list, win, lose);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        view.dismissLoading();
                        view.showError("loadRecords error: " + throwable.getMessage());
                    }
                });
    }

    private PageTitleBean countTitle(int year, List<Record> list) {
        PageTitleBean bean = new PageTitleBean();
        for (Record record:list) {
            if (record.getRound().equals(AppConstants.RECORD_MATCH_ROUNDS[0])
                    && record.getWinnerFlag() == AppConstants.WINNER_USER) {
                bean.setWinner(true);
            }
        }
        bean.setYear(year);
        return bean;
    }

    private void countWinLose(List<Record> list) {
        win = 0;
        lose = 0;
        for (Record record:list) {
            //如果是赛前退赛不算胜负场
            if (AppConstants.RETIRE_WO == record.getRetireFlag()) {
                continue;
            }
            if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                win ++;
            }
            else {
                lose ++;
            }
        }
    }
}
