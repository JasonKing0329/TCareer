package com.king.app.tcareer.page.record.search;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.Score;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/7 14:23
 */
public class SearchPresenter extends BasePresenter<SearchView> {

    @Override
    protected void onCreate() {

    }

    public void searchFrom(final List<Record> list, final SearchBean searchBean) {
        view.showLoading();
        Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                List<Record> result = filterList(list, searchBean);
                e.onNext(result);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Record>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Record> records) {
                        view.dismissLoading();
                        view.searchResult(records);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Search failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private List<Record> filterList(List<Record> list, SearchBean searchBean) {

        if (list == null || list.size() == 0) {
            return null;
        }
        List<Record> newList = new ArrayList<>();
        for (int i = 0, n = list.size(); i < n; i++) {
            Record record = list.get(i);
            boolean isPass = checkCompetitor(record, searchBean);
            isPass = isPass && checkCptCountry(record, searchBean);
            isPass = isPass && checkCptRank(record, searchBean);
            isPass = isPass && checkMatchName(record, searchBean);
            isPass = isPass && checkMatchCountry(record, searchBean);
            isPass = isPass && checkMatchCourt(record, searchBean);
            isPass = isPass && checkMatchLevel(record, searchBean);
            isPass = isPass && checkMatchRegion(record, searchBean);
            isPass = isPass && checkRound(record, searchBean);
            isPass = isPass && checkDate(record, searchBean);
            isPass = isPass && checkUserWinner(record, searchBean);
            isPass = isPass && checkScore(record, searchBean);
            if (isPass) {
                newList.add(record);
            }
        }
        return newList;
    }

    private boolean checkCompetitor(Record record, SearchBean searchBean) {
        if (searchBean.isCompetitorOn()) {
            CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);
            return competitor.getNameChn().contains(searchBean.getCompetitor())
                    || competitor.getNameEng().contains(searchBean.getCompetitor())
                    || competitor.getNamePinyin().contains(searchBean.getCompetitor());
        }
        return true;
    }

    private boolean checkCptCountry(Record record, SearchBean searchBean) {
        if (searchBean.isCptCountryOn()) {
            CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);
            return competitor.getCountry().contains(searchBean.getCptCountry());
        }
        return true;
    }

    private boolean checkMatchName(Record record, SearchBean searchBean) {
        if (searchBean.isMatchOn()) {
            return record.getMatch().getName().contains(searchBean.getMatch());
        }
        return true;
    }

    private boolean checkMatchCountry(Record record, SearchBean searchBean) {
        if (searchBean.isMatchCountryOn()) {
            return record.getMatch().getMatchBean().getCountry().contains(searchBean.getMatchCountry());
        }
        return true;
    }

    private boolean checkMatchCity(Record record, SearchBean searchBean) {
//        if (searchBean.is()) {
//            return record.getMatch().getMatchBean().getCourt().contains(searchBean.getCourt());
//        }
        return true;
    }

    private boolean checkMatchCourt(Record record, SearchBean searchBean) {
        if (searchBean.isCourtOn()) {
            return record.getMatch().getMatchBean().getCourt().equals(searchBean.getCourt());
        }
        return true;
    }

    private boolean checkMatchLevel(Record record, SearchBean searchBean) {
        if (searchBean.isLevelOn()) {
            return record.getMatch().getMatchBean().getLevel().equals(searchBean.getLevel());
        }
        return true;
    }

    private boolean checkMatchRegion(Record record, SearchBean searchBean) {
        if (searchBean.isRegionOn()) {
            return record.getMatch().getMatchBean().getRegion().equals(searchBean.getRegion());
        }
        return true;
    }

    private boolean checkRound(Record record, SearchBean searchBean) {
        if (searchBean.isRoundOn()) {
            // must be equals, cause Final, Semi Final...
            return record.getRound().equals(searchBean.getRound());
        }
        return true;
    }

    private boolean checkCptRank(Record record, SearchBean searchBean) {
        if (searchBean.isRankOn()) {
            return record.getRankCpt() >= searchBean.getRankMin() && record.getRankCpt() <= searchBean.getRankMax();
        }
        return true;
    }

    private boolean checkDate(Record record, SearchBean searchBean) {
        if (searchBean.isDateOn()) {
            return record.getDateLong() >= searchBean.getDate_start() && record.getDateLong() <= searchBean.getDate_end();
        }
        return true;
    }


    private boolean checkUserWinner(Record record, SearchBean searchBean) {
        if (searchBean.isWinnerOn()) {
            return record.getWinnerFlag() == (searchBean.isWinner() ? AppConstants.WINNER_USER:AppConstants.WINNER_COMPETITOR);
        }
        return true;
    }

    private boolean checkScore(Record record, SearchBean searchBean) {
        if (searchBean.isScoreOn()) {
            boolean pass = false;
            List<Score> scoreList = record.getScoreList();
            for (Score score:scoreList) {
                pass = score.getUserPoint() == searchBean.getScoreUser() && score.getCompetitorPoint() == searchBean.getScoreCpt();
                if (pass) {
                    break;
                }

                if (searchBean.isScoreEachOther()) {
                    pass = score.getUserPoint() == searchBean.getScoreCpt() && score.getCompetitorPoint() == searchBean.getScoreUser();
                }
                if (pass) {
                    break;
                }
            }
            return pass;
        }
        return true;
    }

}
