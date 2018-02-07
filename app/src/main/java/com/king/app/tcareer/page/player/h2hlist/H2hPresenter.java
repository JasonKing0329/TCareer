package com.king.app.tcareer.page.player.h2hlist;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.dao.H2HDao;
import com.king.app.tcareer.model.db.Sqls;
import com.king.app.tcareer.model.db.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
 * Created by Administrator on 2017/4/30 0030.
 */

public class H2hPresenter extends BasePresenter<IH2hListView> {

    private H2hListPageData h2hListPageData;

    private int sortType;
    private int sortOrder;

    private H2HDao h2HDao;

    @Override
    protected void onCreate() {
        sortType = SortDialog.SORT_TYPE_NAME;
        sortOrder = SortDialog.SORT_ORDER_ASC;
        h2HDao = new H2HDao();
    }

    public void loadPlayers(final long userId) {
        Observable observable = queryUser(userId)
                .flatMap(new Function<User, ObservableSource<List<H2hBean>>>() {
                    @Override
                    public ObservableSource<List<H2hBean>> apply(User user) throws Exception {
                        view.postShowUser(user);
                        String sql = getSortSql();
                        return h2HDao.queryH2HList(sql);
                    }
                });
        if (sortType == SortDialog.SORT_TYPE_NAME) {
            observable.flatMap(new Function<List<H2hBean>, ObservableSource<List<H2hBean>>>() {
                @Override
                public ObservableSource<List<H2hBean>> apply(List<H2hBean> list) throws Exception {
                    return sortList(list, sortType, sortOrder);
                }
            });
        }
        observable.flatMap(new Function<List<H2hBean>, ObservableSource<H2hListPageData>>() {
                    @Override
                    public ObservableSource<H2hListPageData> apply(List<H2hBean> list) throws Exception {
                        return queryPageData(list);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<H2hListPageData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(H2hListPageData data) {
                        view.onDataLoaded(data);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        view.showMessage("Load h2h beans failed: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<H2hListPageData> queryPageData(final List<H2hBean> list) {
        return Observable.create(new ObservableOnSubscribe<H2hListPageData>() {
            @Override
            public void subscribe(ObservableEmitter<H2hListPageData> e) throws Exception {
                h2hListPageData = new H2hListPageData();

                h2hListPageData.setHeaderList(list);
                h2hListPageData.setShowList(new ArrayList<H2hBean>());
                h2hListPageData.getShowList().addAll(list);

                // chart datas
                h2hListPageData.setChartContents(new String[]{
                        "Top10", "Top11-20", "Top21-50", "Top51-100", "OutOf100"
                });
                h2hListPageData.setCareerChartWinValues(h2HDao.getTotalCount(mUser.getId(), AppConstants.WINNER_USER, false));
                h2hListPageData.setCareerChartLoseValues(h2HDao.getTotalCount(mUser.getId(), AppConstants.WINNER_COMPETITOR, false));
                h2hListPageData.setSeasonChartWinValues(h2HDao.getTotalCount(mUser.getId(), AppConstants.WINNER_USER, true));
                h2hListPageData.setSeasonChartLoseValues(h2HDao.getTotalCount(mUser.getId(), AppConstants.WINNER_COMPETITOR, true));

                e.onNext(h2hListPageData);
            }
        });
    }

    public int getSortType() {
        return sortType;
    }

    public void sortDatas(int sortType, int sortOrder) {
        this.sortType = sortType;
        this.sortOrder = sortOrder;
        List<H2hBean> list = h2hListPageData.getShowList();
        sortList(list, sortType, sortOrder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<H2hBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<H2hBean> list) {
                        view.onSortFinished(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("sort failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<H2hBean>> sortList(final List<H2hBean> list, final int sortType, final int sortOrder) {
        return Observable.create(new ObservableOnSubscribe<List<H2hBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<H2hBean>> e) throws Exception {
                handleSort(list, sortType, sortOrder);
                h2hListPageData.setShowList(list);
                e.onNext(list);
            }
        });
    }

    private void handleSort(List<H2hBean> list, int sortType, int sortOrder) {
        switch (sortType) {
            case SortDialog.SORT_TYPE_NAME:
                Collections.sort(list, new PlayerComparator(sortOrder));
                break;
            case SortDialog.SORT_TYPE_TOTAL:
                Collections.sort(list, new TotalComparator(sortOrder));
                break;
            case SortDialog.SORT_TYPE_WIN:
                Collections.sort(list, new WinComparator(sortOrder));
                break;
            case SortDialog.SORT_TYPE_LOSE:
                Collections.sort(list, new LoseComparator(sortOrder));
                break;
            case SortDialog.SORT_TYPE_PUREWIN:
                Collections.sort(list, new PureWinComparator(sortOrder));
                break;
            case SortDialog.SORT_TYPE_PURELOSE:
                Collections.sort(list, new PureLoseComparator(sortOrder));
                break;
        }
    }

    private interface Filter {
        boolean isPass(H2hBean bean);
    }

    private void filter(final Filter filter) {
        Observable.create(new ObservableOnSubscribe<List<H2hBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<H2hBean>> e) throws Exception {
                List<H2hBean> list = h2hListPageData.getHeaderList();
                List<H2hBean> showList = new ArrayList<>();
                for (H2hBean bean:list) {
                    if (filter.isPass(bean)) {
                        showList.add(bean);
                    }
                }
                h2hListPageData.setShowList(showList);
                e.onNext(showList);
            }
        }).flatMap(new Function<List<H2hBean>, ObservableSource<List<H2hBean>>>() {
            @Override
            public ObservableSource<List<H2hBean>> apply(List<H2hBean> list) throws Exception {
                return sortList(list, sortType, sortOrder);
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<H2hBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<H2hBean> list) {
                        view.onFilterFinished(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("filter failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 不过滤，还原所有数据
     */
    public void filterNothing() {
        filter(new Filter() {
            @Override
            public boolean isPass(H2hBean bean) {
                return true;
            }
        });
    }

    public void filterCountry(final String country) {
        filter(new Filter() {
            @Override
            public boolean isPass(H2hBean bean) {
                return bean.getCompetitor().getCountry().equals(country);
            }
        });
    }

    public void filterCount(final int min, final int max) {
        filter(new Filter() {
            @Override
            public boolean isPass(H2hBean bean) {
                return bean.getTotal() >= min && bean.getTotal() <= max;
            }
        });
    }

    public void filterWin(final int min, final int max) {
        filter(new Filter() {
            @Override
            public boolean isPass(H2hBean bean) {
                return bean.getWin() >= min && bean.getWin() <= max;
            }
        });
    }

    public void filterLose(final int min, final int max) {
        filter(new Filter() {
            @Override
            public boolean isPass(H2hBean bean) {
                return bean.getLose() >= min && bean.getLose() <= max;
            }
        });
    }

    /**
     * 净胜场
     * @param min
     * @param max
     */
    public void filterOdds(final int min, final int max) {
        filter(new Filter() {
            @Override
            public boolean isPass(H2hBean bean) {
                return bean.getWin() - bean.getLose() >= min && bean.getWin() - bean.getLose() <= max;
            }
        });
    }

    public String getSortSql() {
        boolean desc = sortOrder == SortDialog.SORT_ORDER_DESC;
        String sql;
        switch (sortType) {
            case SortDialog.SORT_TYPE_TOTAL:
                sql = Sqls.getH2hOrderByTotal(mUser.getId(), desc);
                break;
            case SortDialog.SORT_TYPE_WIN:
                sql = Sqls.getH2hOrderByWin(mUser.getId(), desc);
                break;
            case SortDialog.SORT_TYPE_LOSE:
                sql = Sqls.getH2hOrderByLose(mUser.getId(), desc);
                break;
            case SortDialog.SORT_TYPE_PUREWIN:
                sql = Sqls.getH2hOrderByOdds(mUser.getId(), desc);
                break;
            case SortDialog.SORT_TYPE_PURELOSE:
                sql = Sqls.getH2hOrderByOdds(mUser.getId(), !desc);
                break;
            case SortDialog.SORT_TYPE_NAME:
            default:
                sql = Sqls.getH2hNoOrderBy(mUser.getId(), desc);
                break;
        }
        return sql;
    }

    private class PlayerComparator implements Comparator<H2hBean> {

        private int order;

        public PlayerComparator(int order) {
            this.order = order;
        }

        @Override
        public int compare(H2hBean item1, H2hBean item2) {
            CompetitorBean pb1 = item1.getCompetitor();
            CompetitorBean pb2 = item2.getCompetitor();
            String pinyin1 = pb1 == null ? "zzzz":pb1.getNamePinyin();
            String pinyin2 = pb2 == null ? "zzzz":pb2.getNamePinyin();
            if (order == SortDialog.SORT_ORDER_DESC) {
                return pinyin2.compareTo(pinyin1);
            }
            else {
                return pinyin1.compareTo(pinyin2);
            }
        }
    }

    private class TotalComparator implements Comparator<H2hBean> {

        private int order;

        public TotalComparator(int order) {
            this.order = order;
        }

        @Override
        public int compare(H2hBean item1, H2hBean item2) {
            int lTotal = item1.getWin() + item1.getLose();
            int rTotal = item2.getWin() + item2.getLose();
            if (order == SortDialog.SORT_ORDER_DESC) {
                return rTotal - lTotal;
            }
            else {
                return lTotal - rTotal;
            }
        }
    }

    private class WinComparator implements Comparator<H2hBean> {

        private int order;

        public WinComparator(int order) {
            this.order = order;
        }

        @Override
        public int compare(H2hBean item1, H2hBean item2) {
            int lWin = item1.getWin();
            int rWin = item2.getWin();
            if (order == SortDialog.SORT_ORDER_DESC) {
                return rWin - lWin;
            }
            else {
                return lWin - rWin;
            }
        }
    }

    private class LoseComparator implements Comparator<H2hBean> {

        private int order;

        public LoseComparator(int order) {
            this.order = order;
        }

        @Override
        public int compare(H2hBean item1, H2hBean item2) {
            int lWin = item1.getLose();
            int rWin = item2.getLose();
            if (order == SortDialog.SORT_ORDER_DESC) {
                return rWin - lWin;
            }
            else {
                return lWin - rWin;
            }
        }
    }

    private class PureWinComparator implements Comparator<H2hBean> {

        private int order;

        public PureWinComparator(int order) {
            this.order = order;
        }

        @Override
        public int compare(H2hBean item1, H2hBean item2) {
            int lTotal = item1.getWin() - item1.getLose();
            int rTotal = item2.getWin() - item2.getLose();
            if (order == SortDialog.SORT_ORDER_DESC) {
                return rTotal - lTotal;
            }
            else {
                return lTotal - rTotal;
            }
        }
    }

    private class PureLoseComparator implements Comparator<H2hBean> {

        private int order;

        public PureLoseComparator(int order) {
            this.order = order;
        }

        @Override
        public int compare(H2hBean item1, H2hBean item2) {
            int lTotal = item1.getLose() - item1.getWin();
            int rTotal = item2.getLose() - item2.getWin();
            if (order == SortDialog.SORT_ORDER_DESC) {
                return rTotal - lTotal;
            }
            else {
                return lTotal - rTotal;
            }
        }
    }
}
