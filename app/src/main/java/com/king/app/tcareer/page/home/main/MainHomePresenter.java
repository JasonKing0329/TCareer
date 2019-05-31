package com.king.app.tcareer.page.home.main;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchBeanDao;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.view.widget.scoreboard.BoardStyleProvider;
import com.king.app.tcareer.view.widget.scoreboard.ScoreBoardParam;

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

    public void loadRecords() {
        getLatestRecords()
                .flatMap(list -> toScoreBoards(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
//                .subscribe(new Observer<List<ComplexRecord>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        addDisposable(d);
//                    }
//
//                    @Override
//                    public void onNext(List<ComplexRecord> records) {
//                        view.showRecords(records);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        view.showMessage(e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
                .subscribe(new Observer<List<ScoreBoardParam>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<ScoreBoardParam> records) {
                        view.showScoreBoards(records);
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
            int thisWeek = week;
            while (list.size() == 0 && week > 0) {
                week --;
                list = getDaoSession().getMatchBeanDao().queryBuilder()
                        .where(MatchBeanDao.Properties.Week.eq(week))
                        .build().list();
            }
            if (week != thisWeek) {
                view.postWeekInfo("Week " + week);
            }

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

    /**
     * short for name
     * eg. set Novak Djokovic as N.Djokovic
     * @param name
     * @return
     */
    private String formatEngName(String name, CompetitorBean bean) {
        try {
            String[] arr = name.split(" ");
            if (bean instanceof User && (bean.getId() == AppConstants.USER_ID_KING || bean.getId() == AppConstants.USER_ID_QI)) {
                return arr[1].charAt(0) + "." + arr[0];
            }
            else {
                return arr[0].charAt(0) + "." + arr[1];
            }
        } catch (Exception e) {}
        return name;
    }

    private ObservableSource<List<ScoreBoardParam>> toScoreBoards(List<Record> list) {
        return observer -> {
            List<ScoreBoardParam> result = new ArrayList<>();
            BoardStyleProvider styleProvider = new BoardStyleProvider();
            for (Record record:list) {
                ScoreBoardParam param = new ScoreBoardParam();
                param.setRecord(record);
                if (record.getSeed() > 0) {
                    param.setPlayer1(formatEngName(record.getUser().getNameEng(), record.getUser()) + "[" + record.getSeed() + "]");
                }
                else {
                    param.setPlayer1(formatEngName(record.getUser().getNameEng(), record.getUser()));
                }
                CompetitorBean bean = CompetitorParser.getCompetitorFrom(record);
                if (record.getSeedpCpt() > 0) {
                    param.setPlayer2(formatEngName(bean.getNameEng(), bean) + "[" + record.getSeedpCpt() + "]");
                }
                else {
                    param.setPlayer2(formatEngName(bean.getNameEng(), bean));
                }
                param.setPlayerUrl1(ImageProvider.getPlayerHeadPath(record.getUser().getNameChn()));
                param.setPlayerUrl2(ImageProvider.getPlayerHeadPath(bean.getNameChn()));
                if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                    param.setWinnerIndex(0);
                }
                else {
                    param.setWinnerIndex(1);
                }
                param.setMatchName(record.getMatch().getName());
                param.setRound(AppConstants.getRoundShortName(record.getRound()));
                param.setScoreList(record.getScoreList());

                if (record.getMatch().getMatchBean().getCourt().equals(AppConstants.RECORD_MATCH_COURTS[0])) {
                    param.setBoardStyle(styleProvider.getAustriliaOpen());
                }
                else if (record.getMatch().getMatchBean().getCourt().equals(AppConstants.RECORD_MATCH_COURTS[1])) {
                    param.setBoardStyle(styleProvider.getFrenchOpen());
                }
                else if (record.getMatch().getMatchBean().getCourt().equals(AppConstants.RECORD_MATCH_COURTS[2])) {
                    param.setBoardStyle(styleProvider.getWimbledonOpen());
                }
                else {
                    param.setBoardStyle(styleProvider.getDefault());
                }

                result.add(param);
            }
            observer.onNext(result);
        };
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
