package com.king.app.tcareer.page.match.recent;

import android.support.v7.graphics.Palette;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.utils.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecentPresenter extends BasePresenter<RecentMatchView> {

    private MatchNameBean matchNameBean;

    private List<Integer> yearList;

    private Map<Integer, List<Record>> yearMap;

    @Override
    protected void onCreate() {

    }

    public void loadMatch(long matchId, int startYear) {
        getMatch(matchId)
                .flatMap(bean -> {
                    matchNameBean = bean;
                    view.postShowMatch(bean);
                    return loadRecords();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        view.showYears(yearList);

                        int index = 0;
                        for (int i = 0; i < yearList.size(); i ++) {
                            if (yearList.get(i) == startYear) {
                                index = i;
                            }
                        }
                        showYear(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<MatchNameBean> getMatch(long matchId) {
        return Observable.create(e -> {
            List<MatchNameBean> list = getDaoSession().getMatchNameBeanDao().queryBuilder()
                    .where(MatchNameBeanDao.Properties.MatchId.eq(matchId))
                    .build().list();
            e.onNext(list.get(list.size() - 1));
        });
    }

    private ObservableSource<Object> loadRecords() {
        return observer -> {
            List<Record> list = getDaoSession().getRecordDao().queryBuilder()
                    .where(RecordDao.Properties.MatchNameId.eq(matchNameBean.getId()))
                    .orderDesc(RecordDao.Properties.Id)
                    .build().list();
            yearList = new ArrayList<>();
            yearMap = new HashMap<>();
            for (Record record:list) {
                int year = Integer.parseInt(record.getDateStr().split("-")[0]);
                if (yearMap.get(year) == null) {
                    yearList.add(year);
                    yearMap.put(year, new ArrayList<>());
                }
                yearMap.get(year).add(record);
            }
            observer.onNext(new Object());
        };
    }

    public void showYear(int yearIndex) {
        showYearRecords(yearIndex)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Object> list) {
                        view.showRecords(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<Object>> showYearRecords(int yearIndex) {
        return Observable.create(e -> {
            List<Object> list = new ArrayList<>();
            List<String> rounds = new ArrayList<>();
            List<Record> records = yearMap.get(yearList.get(yearIndex));
            Map<String, List<Record>> map = new HashMap<>();
            for (Record record:records) {
                String round = record.getRound();
                if (map.get(round) == null) {
                    rounds.add(round);
                    map.put(round, new ArrayList<>());
                }
                map.get(round).add(record);
            }

            Collections.sort(rounds, new RoundComparator());
            for (String round:rounds) {
                list.add(round);
                list.addAll(map.get(round));
            }
            e.onNext(list);
        });
    }

    /**
     * title bar运用的颜色
     * vibrant优先，其次muted，再其次任意
     * @param palette
     * @return
     */
    public Palette.Swatch getTitlebarSwatch(Palette palette) {
        if (palette == null) {
            return null;
        }
        Palette.Swatch swatch = palette.getVibrantSwatch();
        if (swatch == null) {
            swatch = palette.getMutedSwatch();
            if (swatch == null) {
                List<Palette.Swatch> swatches = palette.getSwatches();
                if (!ListUtil.isEmpty(swatches)) {
                    swatch = swatches.get(0);
                }
            }
        }
        return swatch;
    }

    private class RoundComparator implements Comparator<String> {

        @Override
        public int compare(String round1, String round2) {
            int sortValue1 = AppConstants.getRoundSortValue(round1);
            int sortValue2 = AppConstants.getRoundSortValue(round2);
            if (sortValue1 - sortValue2 < 0) {
                return -1;
            }
            else if (sortValue1 - sortValue2 > 0) {
                return 1;
            }
            return 0;
        }
    }
}
