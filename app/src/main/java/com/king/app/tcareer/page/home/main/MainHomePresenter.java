package com.king.app.tcareer.page.home.main;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchBeanDao;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/14 17:09
 */
public class MainHomePresenter extends BasePresenter<MainHomeView> {

    @Override
    protected void onCreate() {
        loadUsers();
        loadMatches();
        loadRecords();
    }

    private void loadUsers() {
        queryAllUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<User> users) {
                        view.showUsers(users);
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

    private Observable<List<User>> queryAllUsers() {
        return Observable.create(e -> {
            List<User> list = getDaoSession().getUserDao().queryBuilder()
                    .build().list();
            e.onNext(list);
        });
    }

    private void loadMatches() {
        queryWeekMatches()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<MatchNameBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<MatchNameBean> list) {
                        view.showMatches(list);
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

    private void loadRecords() {
        getLatestRecords()
                .flatMap(list -> toViewRecords(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<ComplexRecord>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<ComplexRecord> records) {
                        view.showRecords(records);
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

    private Observable<List<MatchNameBean>> queryWeekMatches() {
        return Observable.create(e -> {
            int week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
            List<MatchBean> list = getDaoSession().getMatchBeanDao().queryBuilder()
                    .where(MatchBeanDao.Properties.Week.eq(week))
                    .build().list();

            List<MatchNameBean> matches = new ArrayList<>();
            for (MatchBean bean:list) {
                // 只取最近的名称
                matches.add(bean.getNameBeanList().get(bean.getNameBeanList().size() - 1));
            }
            e.onNext(matches);
        });
    }

    private Observable<List<Record>> getLatestRecords() {
        return Observable.create(e -> {
            List<Record> list = getDaoSession().getRecordDao().queryBuilder()
                    .orderDesc(RecordDao.Properties.Id)
                    .limit(10)
                    .build().list();
            e.onNext(list);
        });
    }

    private ObservableSource<List<ComplexRecord>> toViewRecords(List<Record> list) {
        return observer -> {
            List<ComplexRecord> result = new ArrayList<>();
            for (Record record:list) {
                ComplexRecord cr = new ComplexRecord();
                cr.setRecord(record);
                cr.setMatchName(record.getMatch().getName());
                cr.setMatchRound(AppConstants.getRoundShortName(record.getRound()));
                cr.setImgUrl(ImageProvider.getMatchHeadPath(record.getMatch().getName(), record.getMatch().getMatchBean().getCourt()));
                cr.setScore(ScoreParser.getScoreText(record.getScoreList(), record.getWinnerFlag(), record.getRetireFlag()));
                String userName = record.getUser().getNameShort();
                String compName = CompetitorParser.getCompetitorFrom(record).getNameChn();
                if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                    cr.setWinner(userName);
                    cr.setLoser(compName);
                }
                else {
                    cr.setWinner(compName);
                    cr.setLoser(userName);
                }
                result.add(cr);
            }
            observer.onNext(result);
        };
    }
}
