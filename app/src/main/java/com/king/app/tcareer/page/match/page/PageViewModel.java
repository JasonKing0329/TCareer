package com.king.app.tcareer.page.match.page;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ImageProvider;
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
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/21 14:14
 */
public class PageViewModel extends BaseViewModel {

    public ObservableField<String> matchImageUrl = new ObservableField<>();
    public ObservableField<String> matchNameText = new ObservableField<>();
    public ObservableField<String> matchPlaceText = new ObservableField<>();
    public ObservableField<String> matchLevelText = new ObservableField<>();
    public ObservableField<String> winLoseText = new ObservableField<>();

    public MutableLiveData<List<Object>> recordsObserver = new MutableLiveData<>();

    private MatchNameBean mMatchBean;

    private int win;

    private int lose;

    public PageViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadData(final long matchNameId, final long userId) {
        loadingObserver.setValue(true);
        queryUser(userId)
                .flatMap((Function<User, ObservableSource<MatchNameBean>>) user -> observer -> {
                    // load match
                    MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
                    MatchNameBean bean = dao.queryBuilder()
                            .where(MatchNameBeanDao.Properties.Id.eq(matchNameId))
                            .build().unique();
                    observer.onNext(bean);
                })
                .flatMap(bean -> {
                    mMatchBean = bean;
                    matchNameText.set(bean.getName());
                    matchPlaceText.set(bean.getMatchBean().getCountry() + "/" + bean.getMatchBean().getCity());
                    matchLevelText.set(bean.getMatchBean().getLevel() + "/" + bean.getMatchBean().getCourt());
                    matchImageUrl.set(ImageProvider.getMatchHeadPath(bean.getName(), bean.getMatchBean().getCourt()));
                    return loadRecords(userId);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Object> list) {
                        loadingObserver.setValue(false);
                        winLoseText.set(win + "胜" + lose + "负");
                        recordsObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("Load match failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private ObservableSource<List<Object>> loadRecords(final long userId) {
        return Observable.create(e -> {
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
                record.setImageUrl(ImageProvider.getPlayerHeadPath(CompetitorParser.getCompetitorFrom(record).getNameChn()));
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
