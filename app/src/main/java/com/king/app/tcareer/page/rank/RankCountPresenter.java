package com.king.app.tcareer.page.rank;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.model.RankModel;
import com.king.app.tcareer.model.bean.RankRangeBean;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.model.db.entity.User;

import java.text.SimpleDateFormat;
import java.util.Date;

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
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 13:39
 */
public class RankCountPresenter extends BasePresenter<RankCountView> {

    private RankModel rankModel;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate() {
        rankModel = new RankModel();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void loadDatas(long userId) {
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<CountData>>() {
                    @Override
                    public ObservableSource<CountData> apply(User user) throws Exception {
                        return new ObservableSource<CountData>() {
                            @Override
                            public void subscribe(Observer<? super CountData> observer) {
                                CountData data = new CountData();
                                data.current = rankModel.queryCurrentRank(mUser.getId());
                                data.highest = rankModel.queryHighestRank(mUser.getId());
                                if (data.highest == 1) {
                                    data.isTop1 = true;
                                    data.rankRangeBean = rankModel.queryRankRange(mUser.getId(), 1, 1);
                                }
                                observer.onNext(data);
                            }
                        };
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<CountData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(CountData data) {
                        view.showBasic(data.current, data.highest, data.isTop1);
                        if (data.isTop1) {
                            String sequence = data.rankRangeBean.getSequences() + " (";
                            Date start = data.rankRangeBean.getRankStart().getDate();
                            Date end = data.rankRangeBean.getRankEnd().getDate();
                            if (start.getTime() == end.getTime()) {
                                sequence = sequence + dateFormat.format(start) + ")";
                            }
                            else {
                                sequence = sequence + dateFormat.format(start) + " to " + dateFormat.format(end) + ")";
                            }
                            view.showTop1(data.rankRangeBean.getWeeks(), sequence);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Query error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void queryCondition(final int min, final int max) {
        Observable.create(new ObservableOnSubscribe<RankRangeBean>() {
            @Override
            public void subscribe(ObservableEmitter<RankRangeBean> e) throws Exception {
                RankRangeBean bean = rankModel.queryRankRange(mUser.getId(), min, max);
                e.onNext(bean);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RankRangeBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(RankRangeBean bean) {
                        String sequence = String.valueOf(bean.getSequences());
                        if (bean.getSequences() > 0) {
                            sequence = sequence + " (";
                            Date start = bean.getRankStart().getDate();
                            Date end = bean.getRankEnd().getDate();
                            if (start.getTime() == end.getTime()) {
                                sequence = sequence + dateFormat.format(start) + ")";
                            }
                            else {
                                sequence = sequence + dateFormat.format(start) + " to " + dateFormat.format(end) + ")";
                            }
                        }
                        view.showConditions(bean.getWeeks(), sequence);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Query error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private class CountData {
        int current;
        int highest;
        boolean isTop1;
        RankRangeBean rankRangeBean;
    }
}
