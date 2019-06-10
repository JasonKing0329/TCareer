package com.king.app.tcareer.page.rank;

import android.app.Application;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.view.View;

import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.model.RankModel;
import com.king.app.tcareer.model.bean.RankRangeBean;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 13:39
 */
public class RankCountViewModel extends BaseViewModel {

    public ObservableField<String> currentRankText = new ObservableField<>();
    public ObservableField<String> highestRankText = new ObservableField<>();
    public ObservableInt isTop1Visibility = new ObservableInt();
    public ObservableField<String> top1WeeksText = new ObservableField<>();
    public ObservableField<String> top1LongestText = new ObservableField<>();
    public ObservableField<String> contitionTotalText = new ObservableField<>();
    public ObservableField<String> contitionLongestText = new ObservableField<>();

    private RankModel rankModel;
    private SimpleDateFormat dateFormat;

    public RankCountViewModel(@NonNull Application application) {
        super(application);
        rankModel = new RankModel();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void loadDatas(long userId) {
        queryUser(userId)
                .flatMap(user -> (ObservableSource<CountData>) observer -> {
                    CountData data = new CountData();
                    data.current = rankModel.queryCurrentRank(mUser.getId());
                    data.highest = rankModel.queryHighestRank(mUser.getId());
                    if (data.highest == 1) {
                        data.isTop1 = true;
                        data.rankRangeBean = rankModel.queryRankRange(mUser.getId(), 1, 1);
                    }
                    observer.onNext(data);
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
                        currentRankText.set(String.valueOf(data.current));
                        highestRankText.set(String.valueOf(data.highest));
                        isTop1Visibility.set(data.isTop1 ? View.VISIBLE:View.GONE);

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
                            top1WeeksText.set(String.valueOf(data.rankRangeBean.getWeeks()));
                            top1LongestText.set(sequence);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Query error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<RankRangeBean> getCondition(int min, int max) {
        return Observable.create(e -> {
            RankRangeBean bean = rankModel.queryRankRange(mUser.getId(), min, max);
            e.onNext(bean);
        });
    }

    public void queryCondition(int min, int max) {
        getCondition(min, max)
                .observeOn(AndroidSchedulers.mainThread())
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
                        contitionTotalText.set(String.valueOf(bean.getWeeks()));
                        contitionLongestText.set(sequence);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Query error: " + e.getMessage());
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
