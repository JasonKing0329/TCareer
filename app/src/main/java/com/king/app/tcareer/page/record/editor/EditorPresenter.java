package com.king.app.tcareer.page.record.editor;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.bean.AutoFillMatchBean;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.dao.H2HDao;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.Score;
import com.king.app.tcareer.model.db.entity.ScoreDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.ListUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
 * <p/>创建时间: 2017/11/28 9:47
 */
public class EditorPresenter extends BasePresenter<IEditorView> {

    private H2HDao h2hDao;

    private Record mRecord;
    private CompetitorBean mCompetitor;
    private MatchNameBean mMatchNameBean;
    private List<Score> mScoreList;

    @Override
    protected void onCreate() {
        h2hDao = new H2HDao();
    }

    public void init(long userId, final long recordId) {
        queryUser(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(User user) {
                        view.showUser(user);
                        // 修改
                        if (recordId > 0) {
                            loadRecord(recordId);
                        }
                        // 添加
                        else {
                            mRecord = new Record();
                            mRecord.setUserId(mUser.getId());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Load user failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadRecord(final long recordId) {
        Observable.create(new ObservableOnSubscribe<Record>() {
            @Override
            public void subscribe(ObservableEmitter<Record> e) throws Exception {
                mRecord = TApplication.getInstance().getDaoSession().getRecordDao()
                        .queryBuilder()
                        .where(RecordDao.Properties.Id.eq(recordId))
                        .build().unique();
                mCompetitor = CompetitorParser.getCompetitorFrom(mRecord);
                mMatchNameBean = mRecord.getMatch();
                mScoreList = mRecord.getScoreList();
                e.onNext(mRecord);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Record>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Record record) {
                        view.showRecord(record);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Load record failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void queryH2H() {
        h2hDao.queryH2H(mUser.getId(), mCompetitor.getId(), mCompetitor instanceof User)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<H2hBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(H2hBean h2hBean) {
                        view.showH2h(h2hBean.getWin(), h2hBean.getLose());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Load h2h failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void queryMatch(long matchNameId) {
        MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
        mMatchNameBean = dao.queryBuilder()
                .where(MatchNameBeanDao.Properties.Id.eq(matchNameId))
                .build().unique();
    }

    public void queryCompetitor(long playerId, boolean isUser) {
        if (isUser) {
            UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
            mCompetitor = dao.queryBuilder()
                    .where(UserDao.Properties.Id.eq(playerId))
                    .build().unique();
        }
        else {
            PlayerBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
            mCompetitor = dao.queryBuilder()
                    .where(PlayerBeanDao.Properties.Id.eq(playerId))
                    .build().unique();
        }
    }

    public Record getRecord() {
        return mRecord;
    }

    public CompetitorBean getCompetitor() {
        return mCompetitor;
    }

    public MatchNameBean getMatchNameBean() {
        return mMatchNameBean;
    }

    public List<Score> getScoreList() {
        return mScoreList;
    }

    public void setCompetitor(CompetitorBean mCompetitor) {
        this.mCompetitor = mCompetitor;
    }

    public void setMatchNameBean(MatchNameBean mMatchNameBean) {
        this.mMatchNameBean = mMatchNameBean;
    }

    public void setScoreList(List<Score> mScoreList) {
        this.mScoreList = mScoreList;
    }

    public void insertOrUpdate() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                boolean isUpdate = mRecord.getId() != null;
                RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
                if (isUpdate) {
                    recordDao.update(mRecord);
                }
                else {
                    recordDao.insert(mRecord);
                }

                if (!ListUtil.isEmpty(mScoreList)) {
                    for (Score score:mScoreList) {
                        score.setRecordId(mRecord.getId());
                    }
                    ScoreDao scoreDao = TApplication.getInstance().getDaoSession().getScoreDao();
                    scoreDao.insertInTx(mScoreList);
                }
                e.onNext(isUpdate);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean isUpdate) {
                        if (isUpdate) {
                            view.updateSuccess();
                        }
                        else {
                            view.insertSuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Insert or update record failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void fillPlayerPage(int rank, int seed, int rankCpt, int seedCpt) {
        mRecord.setRank(rank);
        mRecord.setSeed(seed);
        mRecord.setRankCpt(rankCpt);
        mRecord.setSeedpCpt(seedCpt);
        mRecord.setPlayerId(mCompetitor.getId());
        mRecord.setPlayerFlag(mCompetitor instanceof User ? AppConstants.COMPETITOR_VIRTUAL : AppConstants.COMPETITOR_NORMAL);
    }

    public void initMatchPage() {
        AutoFillMatchBean autoFill = SettingProperty.getAutoFillMatch();
        if (autoFill != null) {
            long matchId = autoFill.getMatchId();
            queryMatch(matchId);
            view.showMatchAutoFill(autoFill);
        }
        view.showMatchInfor(mRecord.getId() == null ? null:mRecord, mMatchNameBean, mCompetitor, mScoreList);
    }

    public void holdScore(List<Score> scoreList, int retireFlag, int winnerFlag) {
        mScoreList = scoreList;
        mRecord.setRetireFlag(retireFlag);
        mRecord.setWinnerFlag(winnerFlag);
    }

    public void saveAutoFill(int yearIndex, String round) {
        AutoFillMatchBean item = new AutoFillMatchBean();
        item.setMatchId(mMatchNameBean.getId());
        item.setIndexYear(yearIndex);
        item.setRound(round);
        SettingProperty.setAutoFillMatch(item);
    }

    public void fillMatchPage(String round, int year) {
        mRecord.setMatchNameId(mMatchNameBean.getId());
        mRecord.setRound(round);
        int cur_month = 0;
        try {
            cur_month = mMatchNameBean.getMatchBean().getMonth() - 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String month = (1 + cur_month) < 10 ? ("0" + (1 + cur_month)) : ("" + (1 + cur_month));
        String dateStr = year + "-" + month;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        long dateLong;
        try {
            dateLong = format.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            dateLong = System.currentTimeMillis();
        }
        mRecord.setDateStr(dateStr);
        mRecord.setDateLong(dateLong);
    }

    public void reset() {
        mRecord = new Record();
        mRecord.setUserId(mUser.getId());
        mCompetitor = null;
        mScoreList = null;
        // mMatchNameBean不重置，继续保留上一次的
//        mMatchNameBean = null;
    }
}
