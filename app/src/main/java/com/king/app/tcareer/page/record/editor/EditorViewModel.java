package com.king.app.tcareer.page.record.editor;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.view.View;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
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
public class EditorViewModel extends BaseViewModel {

    public ObservableField<String> matchNameText = new ObservableField<>();
    public ObservableField<String> matchCountryText = new ObservableField<>();
    public ObservableField<String> matchCityText = new ObservableField<>();
    public ObservableField<String> matchLevelText = new ObservableField<>();
    public ObservableField<String> matchCourtText = new ObservableField<>();
    public ObservableField<String> matchImageUrl = new ObservableField<>();
    public ObservableInt matchVisibility = new ObservableInt(View.GONE);
    public ObservableField<String> winnerText = new ObservableField<>();
    public ObservableField<String> scoreText = new ObservableField<>();
    public ObservableInt winnerVisibility = new ObservableInt(View.INVISIBLE);

    public ObservableField<String> h2hText = new ObservableField<>();
    public ObservableField<String> userNameText = new ObservableField<>();
    public ObservableField<String> userRankText = new ObservableField<>();
    public ObservableField<String> userSeedText = new ObservableField<>();
    public ObservableField<String> playerNameText = new ObservableField<>();
    public ObservableField<String> playerBirthday = new ObservableField<>();
    public ObservableField<String> playerImageUrl = new ObservableField<>();
    public ObservableField<String> playerSeedText = new ObservableField<>();
    public ObservableField<String> playerRankText = new ObservableField<>();
    public ObservableInt playerVisibility = new ObservableInt(View.GONE);

    public MutableLiveData<Integer> matchYearSelection = new MutableLiveData<>();
    public MutableLiveData<Integer> matchRoundSelection = new MutableLiveData<>();
    public MutableLiveData<Boolean> insertSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();

    public MutableLiveData<List<MatchNameBean>> recentMatchesObserver = new MutableLiveData<>();

    private H2HDao h2hDao;

    private Record mRecord;
    private CompetitorBean mCompetitor;
    private MatchNameBean mMatchNameBean;
    private List<Score> mScoreList;

    private boolean isEditMode;
    private long userId;
    private long recordId;

    private String[] arr_round;
    private String[] arr_year;
    protected int cur_year = 2, cur_round = 0;// 记录当前spinner选项

    public EditorViewModel(@NonNull Application application) {
        super(application);
        h2hDao = new H2HDao();
        arr_round = AppConstants.RECORD_MATCH_ROUNDS;
        arr_year = new String[20];
        for (int n = 0; n < 20; n++) {
            arr_year[n] = "" + (n + 2010);
        }
    }

    public void saveInit(long userId, long recordId) {
        this.userId = userId;
        this.recordId = recordId;
    }

    public void initData() {
        init(userId, recordId);
    }

    public void init(long userId, long recordId) {
        if (userId == -1) {
            // 新增记录，且支持选择user
            isEditMode = false;
            mRecord = new Record();
            return;
        }
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
                        userNameText.set(user.getNameShort());
                        // 修改
                        if (recordId > 0) {
                            isEditMode = true;
                            loadRecord(recordId);
                        }
                        // 添加
                        else {
                            isEditMode = false;
                            mRecord = new Record();
                            mRecord.setUserId(mUser.getId());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load user failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadRecord(final long recordId) {
        Observable.create((ObservableOnSubscribe<Record>) e -> {
            mRecord = TApplication.getInstance().getDaoSession().getRecordDao()
                    .queryBuilder()
                    .where(RecordDao.Properties.Id.eq(recordId))
                    .build().unique();
            mCompetitor = CompetitorParser.getCompetitorFrom(mRecord);
            mMatchNameBean = mRecord.getMatch();
            mScoreList = mRecord.getScoreList();
            e.onNext(mRecord);
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Record>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Record record) {
                        bindPlayer(CompetitorParser.getCompetitorFrom(record));
                        bindRecordPlayer();
                        queryH2H();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load record failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void queryH2H() {
        if (mUser == null || mCompetitor == null) {
            return;
        }
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
                        h2hText.set("H2H  " + h2hBean.getWin() + "-" + h2hBean.getLose());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load h2h failed: " + e.getMessage());
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
        updateMatchNameBean(mMatchNameBean);
    }

    public void reLoadUser(long userId) {
        UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
        mUser = dao.queryBuilder()
                .where(UserDao.Properties.Id.eq(userId))
                .build().unique();
        userNameText.set(mUser.getNameShort());
        mRecord.setUserId(mUser.getId());
        queryH2H();
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
        bindPlayer(mCompetitor);
        queryH2H();
    }

    public Record getRecord() {
        return mRecord;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public CompetitorBean getCompetitor() {
        return mCompetitor;
    }

    public void updateMatchNameBean(MatchNameBean bean) {
        mMatchNameBean = bean;
        bindMatch(bean);
    }

    public void insertOrUpdate() {
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {

            final boolean isUpdate = mRecord.getId() != null;
            // control in transaction
            TApplication.getInstance().getDaoSession().runInTx(() -> {
                try {
                    RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
                    ScoreDao scoreDao = TApplication.getInstance().getDaoSession().getScoreDao();
                    // insert into or update from match_records
                    if (isUpdate) {
                        // delete from scores before update
                        scoreDao.queryBuilder().where(ScoreDao.Properties.RecordId.eq(mRecord.getId()))
                                .buildDelete().executeDeleteWithoutDetachingEntities();

                        recordDao.update(mRecord);
                        // notify reset scoreList
                        mRecord.resetScoreList();
                    }
                    else {
                        recordDao.insert(mRecord);
                    }
                    // insert into scores
                    if (!ListUtil.isEmpty(mScoreList)) {
                        for (Score score:mScoreList) {
                            score.setRecordId(mRecord.getId());
                        }
                        scoreDao.insertInTx(mScoreList);
                    }
                } catch (Exception exc) {
                    exc.printStackTrace();
                } finally {
                    e.onNext(isUpdate);
                }
            });
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
                            updateSuccess.setValue(true);
                        }
                        else {
                            insertSuccess.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Insert or update record failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void setCurRound(int cur_round) {
        this.cur_round = cur_round;
    }

    public void setCurYear(int cur_year) {
        this.cur_year = cur_year;
    }

    public void initMatchPage() {
        // 修改
        if (isEditMode) {
            bindRecordMatch();
            bindScore();
            if (mMatchNameBean != null) {
                bindMatch(mMatchNameBean);
            }
        }
        // 新增
        else {
            AutoFillMatchBean autoFill = SettingProperty.getAutoFillMatch();
            if (autoFill != null) {
                long matchId = autoFill.getMatchId();
                queryMatch(matchId);
                matchYearSelection.setValue(autoFill.getIndexYear());
                matchRoundSelection.setValue(getRoundIndex(autoFill.getRound()));
            }
        }
    }
    private void bindRecordPlayer() {
        userRankText.set(String.valueOf(mRecord.getRank()));
        userSeedText.set(String.valueOf(mRecord.getSeed()));
        playerRankText.set(String.valueOf(mRecord.getRankCpt()));
        playerSeedText.set(String.valueOf(mRecord.getSeedpCpt()));
    }

    private void bindPlayer(CompetitorBean bean) {
        playerNameText.set(bean.getNameEng());
        playerBirthday.set(bean.getBirthday());
        playerImageUrl.set(ImageProvider.getDetailPlayerPath(bean.getNameChn()));
        playerVisibility.set(View.VISIBLE);
    }

    private void bindScore() {
        String score = ScoreParser.getScoreText(mScoreList
                , mRecord.getWinnerFlag(), mRecord.getRetireFlag());
        String winner = mRecord.getWinnerFlag() == AppConstants.WINNER_USER ?
                mUser.getNameShort() : mCompetitor.getNameChn();
        scoreText.set(score);
        winnerText.set(winner);
        winnerVisibility.set(View.VISIBLE);
    }

    private void bindRecordMatch() {
        matchRoundSelection.setValue(getRoundIndex(mRecord.getRound()));
        int year = Integer.parseInt(mRecord.getDateStr().split("-")[0]);
        matchYearSelection.setValue(getYearIndex(year));
    }

    private void bindMatch(MatchNameBean bean) {
        matchNameText.set(bean.getName());
        matchImageUrl.set(ImageProvider.getMatchHeadPath(bean.getName(), bean.getMatchBean().getCourt()));
        matchLevelText.set(bean.getMatchBean().getLevel());
        matchCourtText.set(bean.getMatchBean().getCourt());
        matchCountryText.set(bean.getMatchBean().getCountry());
        matchCityText.set(bean.getMatchBean().getCity());
        matchVisibility.set(View.VISIBLE);
    }

    public void updateScore(List<Score> scoreList, int retireFlag, int winnerFlag) {
        mScoreList = scoreList;
        mRecord.setRetireFlag(retireFlag);
        mRecord.setWinnerFlag(winnerFlag);
        bindScore();
    }

    public void reset() {
        mRecord = new Record();
        mRecord.setUserId(mUser.getId());
        mCompetitor = null;
        mScoreList = null;
        // mMatchNameBean不重置，继续保留上一次的
//        mMatchNameBean = null;
    }

    public void loadRecentMatches() {
        queryRecentMatches()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<MatchNameBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<MatchNameBean> matches) {
                        recentMatchesObserver.setValue(matches);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<MatchNameBean>> queryRecentMatches() {
        return Observable.create(e -> {
            List<MatchNameBean> list = new ArrayList<>();
            RecentMatches rm = SettingProperty.getRecentMatches();
            // 后加入的显示在前面
            Collections.reverse(rm.getMatchIdList());
            MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
            for (long id:rm.getMatchIdList()) {
                try {
                    list.add(dao.queryBuilder().where(MatchNameBeanDao.Properties.Id.eq(id)).build().unique());
                } catch (Exception exception) {
                    rm.getMatchIdList().remove(id);
                    SettingProperty.setRecentMatches(rm);
                    break;
                }
            }
            e.onNext(list);
        });
    }

    public void saveAsRecentMatch() {
        RecentMatches rm = SettingProperty.getRecentMatches();
        for (long id:rm.getMatchIdList()) {
            // 已存在的去掉重新添加，提高优先级
            if (id == mMatchNameBean.getId()) {
                rm.getMatchIdList().remove(id);
                break;
            }
        }
        rm.getMatchIdList().add(mMatchNameBean.getId());
        // 最多只保存3个
        if (rm.getMatchIdList().size() > 3) {
            rm.getMatchIdList().remove(0);
        }
        SettingProperty.setRecentMatches(rm);
    }

    public String[] getRoundArrays() {
        return arr_round;
    }

    public String[] getYearArrays() {
        return arr_year;
    }

    public int getYearIndex(int year) {
        for (int i = 0; i < arr_year.length; i++) {
            if (arr_year[i].equals(String.valueOf(year))) {
                return i;
            }
        }
        return 0;
    }

    public int getRoundIndex(String round) {
        for (int i = 0; i < arr_round.length; i++) {
            if (arr_round[i].equals(round)) {
                return i;
            }
        }
        return 0;
    }

    public void executeUpdate() {
        if (mCompetitor == null) {
            messageObserver.setValue("Empty player");
            return;
        }

        fillPlayer();

        if (fillMatch()) {
            saveAutoFill();
            saveAsRecentMatch();
            insertOrUpdate();
        }
    }

    public void fillPlayer() {
        int rank = 0;
        try {
            rank = Integer.parseInt(userRankText.get());
        } catch (Exception e) {}
        int seed = 0;
        try {
            seed = Integer.parseInt(userSeedText.get());
        } catch (Exception e) {}
        int rankCpt = 0;
        try {
            rankCpt = Integer.parseInt(playerRankText.get());
        } catch (Exception e) {}
        int seedCpt = 0;
        try {
            seedCpt = Integer.parseInt(playerSeedText.get());
        } catch (Exception e) {}

        mRecord.setRank(rank);
        mRecord.setSeed(seed);
        mRecord.setRankCpt(rankCpt);
        mRecord.setSeedpCpt(seedCpt);
        mRecord.setPlayerId(mCompetitor.getId());
        mRecord.setPlayerFlag(mCompetitor instanceof User ? AppConstants.COMPETITOR_VIRTUAL : AppConstants.COMPETITOR_NORMAL);
    }

    public boolean fillMatch() {
        if (mMatchNameBean == null) {
            messageObserver.setValue("Empty match");
            return false;
        }
        if (ListUtil.isEmpty(mScoreList)
                && mRecord.getRetireFlag() != AppConstants.RETIRE_WO) {
            messageObserver.setValue("Empty score");
            return false;
        }
        mRecord.setMatchNameId(mMatchNameBean.getId());
        mRecord.setRound(arr_round[cur_round]);
        int cur_month = 0;
        try {
            cur_month = mMatchNameBean.getMatchBean().getMonth() - 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String month = (1 + cur_month) < 10 ? ("0" + (1 + cur_month)) : ("" + (1 + cur_month));
        int year = 2010 + cur_year;
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
        return true;
    }

    public void saveAutoFill() {
        // 新增模式下保存为最近操作赛事，编辑模式下不保存
        if (!isEditMode) {
            AutoFillMatchBean item = new AutoFillMatchBean();
            item.setMatchId(mMatchNameBean.getId());
            item.setIndexYear(cur_year);
            item.setRound(arr_round[cur_round]);
            SettingProperty.setAutoFillMatch(item);
        }
    }

    public List<Score> getScoreList() {
        return mScoreList;
    }
}
