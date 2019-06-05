package com.king.app.tcareer.page.player.slider;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.dao.H2HDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.player.page.PageTitleBean;

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
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/24 14:56
 */
public class SlidePresenter extends BasePresenter<ISlideView> {

    private List<SlideItem<H2hBean>> h2hList;

    private H2HDao h2HDao;

    @Override
    protected void onCreate() {
        h2HDao = new H2HDao();
    }

    public void loadPlayers(final long userId) {
        queryUser(userId)
                .flatMap(user -> h2HDao.queryH2HListOrderByInsert(userId))
                .flatMap(list -> toSlideItems(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<SlideItem<H2hBean>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<SlideItem<H2hBean>> h2hBeans) {
                        h2hList = h2hBeans;
                        view.onPlayerLoaded(h2hBeans);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        view.onPlayerLoadFailed("Load h2h beans failed: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public List<SlideItem<H2hBean>> getCompetitorList() {
        return h2hList;
    }

    private ObservableSource<List<SlideItem<H2hBean>>> toSlideItems(List<H2hBean> list) {
        return observer -> {
            List<SlideItem<H2hBean>> result = new ArrayList<>();
            for (H2hBean bean:list) {
                SlideItem<H2hBean> item = new SlideItem<>();
                item.setBean(bean);
                item.setImageUrl(ImageProvider.getPlayerHeadPath(bean.getCompetitor().getNameChn()));
                result.add(item);
            }
            observer.onNext(result);
        };
    }

    public void loadRecords(H2hBean bean) {
        h2HDao.queryH2HRecords(mUser.getId(), bean)
                .flatMap(new Function<List<Record>, ObservableSource<List<Object>>>() {
                    @Override
                    public ObservableSource<List<Object>> apply(List<Record> records) throws Exception {
                        return parseRecords(records);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Object>>() {
                    @Override
                    public void accept(List<Object> list) throws Exception {
                        view.onRecordLoaded(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private ObservableSource<List<Object>> parseRecords(final List<Record> recordList) {
        return new Observable<List<Object>>() {
            @Override
            protected void subscribeActual(Observer<? super List<Object>> e) {
                List<Object> list = new ArrayList<>();
                Map<Integer, List<Record>> map = new HashMap<>();
                for (int i = 0; i < recordList.size(); i ++) {
                    Record record = recordList.get(i);
                    record.setImageUrl(ImageProvider.getMatchHeadPath(record.getMatch().getName(), record.getMatch().getMatchBean().getCourt()));
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
        };
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

}
