package com.king.app.tcareer.page.player.list;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.comparator.PlayerComparator;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.dao.H2HDao;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.page.player.atp.PlayerAtpViewModel;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.ConstellationUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc user永远不参与sort, filter
 * @auth 景阳
 * @time 2018/5/19 0019 15:36
 */

public class RichPlayerViewModel extends PlayerAtpViewModel {

    public MutableLiveData<String> indexObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> onIndexCreated = new MutableLiveData<>();

    public MutableLiveData<Integer> onSortFinished = new MutableLiveData<>();

    public MutableLiveData<List<RichPlayerBean>> playersObserver = new MutableLiveData<>();

    public MutableLiveData<Integer> updateIndexGravity = new MutableLiveData<>();

    public MutableLiveData<Boolean> setDeleteMode = new MutableLiveData<>();

    public MutableLiveData<Boolean> clearIndex = new MutableLiveData<>();

    public MutableLiveData<Integer> onUpdateAtpCompleted = new MutableLiveData<>();


    private int sortType;

    private int mOperationPosition;

    private List<RichPlayerBean> mList;

    private List<RichPlayerBean> mFullList;

    private String mKeyword;

    private IndexEmitter indexEmitter;

    private Map<Long, Boolean> mExpandMap;

    private boolean hidePlayersWithoutRecords;

    private int mRankHigh;

    private int mRankLow;

    private List<String> mFilterTexts;

    private boolean mOnlyShowUser;

    public RichPlayerViewModel(@NonNull Application application) {
        super(application);
        // 不用SettingProperty.getPlayerSortMode()，因为h2h page与manage page支持的排序类型不尽相同
        sortType = SettingProperty.VALUE_SORT_PLAYER_NAME;
        indexEmitter = new IndexEmitter();
        mExpandMap = new HashMap<>();
    }

    public void loadPlayers(long userId, boolean hidePlayersWithoutRecords, boolean onlyShowUser) {
        mOnlyShowUser = onlyShowUser;
        this.hidePlayersWithoutRecords = hidePlayersWithoutRecords;
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
                        loadPlayers(onlyShowUser);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void loadPlayers(boolean onlyShowUser) {
        mOnlyShowUser = onlyShowUser;
        loadingObserver.setValue(true);
        clearIndex.setValue(true);
        updateSidebarGravity();
        Observable.combineLatest(queryUsers(), queryPlayers()
                , (users, players) -> {
                    List<RichPlayerBean> list = new ArrayList<>();
                    list.addAll(users);
                    if (!onlyShowUser) {
                        list.addAll(players);
                    }
                    return list;
                })
                .flatMap(list -> sortPlayerRx(list))
                .flatMap(list -> {
                    mFullList = list;
                    mList = new ArrayList<>();
                    // 默认全部展开
                    for (int i = 0; i < mFullList.size(); i ++) {
                        RichPlayerBean bean = mFullList.get(i);
                        mList.add(bean);
                    }
                    setExpandAll(true);
                    return createIndexes();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(String index) {
                        indexObserver.setValue(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("Load players failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        loadingObserver.setValue(false);
                        onIndexCreated.setValue(true);
                        playersObserver.setValue(mList);
                        onSortFinished.setValue(sortType);
                    }
                });
    }

    private Observable<List<RichPlayerBean>> sortPlayerRx(List<RichPlayerBean> list) {
        return Observable.create(e -> {
            Collections.sort(list, new PlayerComparator(sortType));
            e.onNext(list);
            e.onComplete();
        });
    }

    private Observable<List<RichPlayerBean>> queryUsers() {
        return Observable.create(e -> {
            UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
            List<User> users = dao.queryBuilder().build().list();

            H2HDao h2hDao = new H2HDao();
            List<RichPlayerBean> list = new ArrayList<>();
            for (User user:users) {
                RichPlayerBean viewBean = new RichPlayerBean();
                viewBean.setImageUrl(ImageProvider.getPlayerHeadPath(user.getNameChn()));
                viewBean.setCompetitorBean(user);

                // count records if control rank
                if (isControlRank()) {
                    createRichWithRank(viewBean, user);
                    if (viewBean.getWin() == 0 && viewBean.getLose() == 0) {
                        continue;
                    }
                }
                else {
                    // search data of all users
                    if (mUser == null) {
                        H2hBean h2hK = h2hDao.getH2h(AppConstants.USER_ID_KING, user.getId(), true);
                        H2hBean h2hF = h2hDao.getH2h(AppConstants.USER_ID_FLAMENCO, user.getId(), true);
                        H2hBean h2hH = h2hDao.getH2h(AppConstants.USER_ID_HENRY, user.getId(), true);
                        H2hBean h2hQ = h2hDao.getH2h(AppConstants.USER_ID_QI, user.getId(), true);

                        viewBean.setWin(h2hK.getWin() + h2hF.getWin() + h2hH.getWin() + h2hQ.getWin());
                        viewBean.setLose(h2hK.getLose() + h2hF.getLose() + h2hH.getLose() + h2hQ.getLose());
                    }
                    // only search data of current user
                    else {
                        H2hBean h2h = h2hDao.getH2h(mUser.getId(), user.getId(), true);
                        viewBean.setWin(h2h.getWin());
                        viewBean.setLose(h2h.getLose());

                        if (hidePlayersWithoutRecords) {
                            if (user.getId() == mUser.getId()
                                    || viewBean.getWin() == 0 && viewBean.getLose() == 0) {
                                continue;
                            }
                        }
                    }
                }

                list.add(viewBean);
            }
            e.onNext(list);
            e.onComplete();
        });
    }

    private void createRichWithRank(RichPlayerBean bean, CompetitorBean competitor) {
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        QueryBuilder<Record> builder = dao.queryBuilder();
        if (mUser != null) {
            builder.where(RecordDao.Properties.UserId.eq(mUser.getId()));
        }
        builder.where(RecordDao.Properties.PlayerId.eq(competitor.getId()));
        builder.where(RecordDao.Properties.PlayerFlag.eq(competitor instanceof User ? AppConstants.COMPETITOR_VIRTUAL:AppConstants.COMPETITOR_NORMAL));
        builder.where(RecordDao.Properties.RankCpt.ge(mRankHigh));
        builder.where(RecordDao.Properties.RankCpt.le(mRankLow));
        List<Record> list = builder.build().list();
        for (Record record:list) {
            if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                bean.setWin(bean.getWin() + 1);
            }
            else {
                bean.setLose(bean.getLose() + 1);
            }
        }
    }

    private boolean isControlRank() {
        return mRankHigh != 0 || mRankLow != 0 && mRankHigh <= mRankLow;
    }

    private Observable<List<RichPlayerBean>> queryPlayers() {
        return Observable.create(e -> {
            List<PlayerBean> players;
            if (hidePlayersWithoutRecords) {
                // 只查询user交手过的players
                RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
                StringBuffer sqlBuffer = new StringBuffer("WHERE ");
                sqlBuffer.append(RecordDao.Properties.UserId.columnName)
                        .append(" = ? AND ")
                        .append(RecordDao.Properties.PlayerFlag.columnName)
                        .append(" = ? GROUP BY ")
                        .append(RecordDao.Properties.PlayerId.columnName);
                List<Record> list = dao.queryRaw(sqlBuffer.toString()
                        , new String[]{String.valueOf(mUser.getId()), String.valueOf(AppConstants.COMPETITOR_NORMAL)});
                players = new ArrayList<>();
                for (Record record:list) {
                    players.add(record.getCompetitor());
                }
            }
            else {
                PlayerBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
                players = dao.queryBuilder().build().list();
            }

            H2HDao h2hDao = new H2HDao();
            List<RichPlayerBean> list = new ArrayList<>();
            for (PlayerBean player:players) {
                RichPlayerBean viewBean = new RichPlayerBean();
                viewBean.setImageUrl(ImageProvider.getPlayerHeadPath(player.getNameChn()));
                viewBean.setCompetitorBean(player);

                // count records if control rank
                if (isControlRank()) {
                    createRichWithRank(viewBean, player);
                    if (viewBean.getWin() == 0 && viewBean.getLose() == 0) {
                        continue;
                    }
                }
                else {
                    // search data of all users
                    if (mUser == null) {
                        H2hBean h2hK = h2hDao.getH2h(AppConstants.USER_ID_KING, player.getId(), false);
                        H2hBean h2hF = h2hDao.getH2h(AppConstants.USER_ID_FLAMENCO, player.getId(), false);
                        H2hBean h2hH = h2hDao.getH2h(AppConstants.USER_ID_HENRY, player.getId(), false);
                        H2hBean h2hQ = h2hDao.getH2h(AppConstants.USER_ID_QI, player.getId(), false);
                        viewBean.setWin(h2hK.getWin() + h2hF.getWin() + h2hH.getWin() + h2hQ.getWin());
                        viewBean.setLose(h2hK.getLose() + h2hF.getLose() + h2hH.getLose() + h2hQ.getLose());
                    }
                    // only search data of current user
                    else {
                        H2hBean h2h = h2hDao.getH2h(mUser.getId(), player.getId(), false);
                        viewBean.setWin(h2h.getWin());
                        viewBean.setLose(h2h.getLose());
                    }
                }

                list.add(viewBean);
            }
            e.onNext(list);
            e.onComplete();
        });
    }

    private Observable<String> createIndexes() {
        return Observable.create(e -> {
            indexEmitter.clear();
            switch (sortType) {
                case SettingProperty.VALUE_SORT_PLAYER_NAME:
                case SettingProperty.VALUE_SORT_PLAYER_NAME_ENG:
                    indexEmitter.createNameIndex(e, mList, sortType);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_COUNTRY:
                    indexEmitter.createCountryIndex(e, mList);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_AGE:
                    indexEmitter.createAgeIndex(e, mList);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION:
                    indexEmitter.createSignIndex(e, mList);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_RECORD:
                    indexEmitter.createRecordsIndex(e, mList, mUser == null ? 100:0);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_RECORD_WIN:
                    indexEmitter.createRecordsIndex(e, mList, 1);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_RECORD_ODDS_WIN:
                    indexEmitter.createRecordsIndex(e, mList, 2);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_RECORD_LOSE:
                    indexEmitter.createRecordsIndex(e, mList, 3);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_RECORD_ODDS_LOSE:
                    indexEmitter.createRecordsIndex(e, mList, 4);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_HEIGHT:
                    indexEmitter.createHeightIndex(e, mList);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_WEIGHT:
                    indexEmitter.createWeightIndex(e, mList);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_CAREER_HIGH:
                    indexEmitter.createCareerHighIndex(e, mList);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_CAREER_TITLES:
                    indexEmitter.createCareerTitlesIndex(e, mList);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_CAREER_WIN:
                    indexEmitter.createCareerWinIndex(e, mList);
                    break;
                case SettingProperty.VALUE_SORT_PLAYER_CAREER_TURNEDPRO:
                    indexEmitter.createTurnedProIndex(e, mList);
                    break;
            }
            e.onComplete();
        });
    }

    public int getLetterPosition(String letter) {
        return indexEmitter.getPlayerIndexMap().get(letter).start;
    }

    public void sortPlayer(int sortType) {
        loadingObserver.setValue(true);
        this.sortType = sortType;
        updateSidebarGravity();
        sortPlayerRx(mList)
                .flatMap(list -> createIndexes())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(String index) {
                        indexObserver.setValue(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("Sort players failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        loadingObserver.setValue(false);
                        onIndexCreated.setValue(true);
                        onSortFinished.setValue(sortType);
                    }
                });
    }

    private void updateSidebarGravity() {
        if (sortType == SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION) {
            updateIndexGravity.setValue(Gravity.RIGHT);
        }
        else {
            updateIndexGravity.setValue(Gravity.CENTER);
        }
    }

    public void updateAtpData(String atpiId, int position) {
        this.mOperationPosition = position;
        updateAtpData(atpiId);
    }

    @Override
    protected void onUpdateAtpCompleted(PlayerAtpBean bean) {
        super.onUpdateAtpCompleted(bean);
        onUpdateAtpCompleted.setValue(mOperationPosition);
    }

    public void deletePlayer(final List<RichPlayerBean> list) {
        Observable.create(e -> {
            PlayerBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();

            for (RichPlayerBean bean:list) {
                // user不允许删除
                if (bean.getCompetitorBean() instanceof PlayerBean) {
                    mList.remove(bean);
                    dao.queryBuilder().where(PlayerBeanDao.Properties.Id.eq(bean.getCompetitorBean().getId()))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                }
            }
            e.onNext(new Object());
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object o) {
                        setDeleteMode.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Delete failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public List<RichPlayerBean> getPlayerList() {
        return mList;
    }

    /**
     * filter by inputted text
     * @param text
     */
    public void filter(String text) {
        if (!text.equals(mKeyword)) {
            filterObservable(filterByText(text));
        }
    }

    /**
     * filter by filter bean
     * @param bean
     */
    public void filter(RichFilterBean bean) {
        // rank与其他所有条件互斥
        if (bean.getRankLow() != mRankLow || bean.getRankHigh() != mRankHigh) {
            mRankLow = bean.getRankLow();
            mRankHigh = bean.getRankHigh();
            // 有rank需要过滤records，重新查询记录
            loadPlayers(mOnlyShowUser);
        }
        else {
            // 除了rank以外的条件都能共同作用
            filterObservable(filterByBean(bean));
        }
        createFilterTexts(bean);
    }

    private void createFilterTexts(RichFilterBean bean) {
        mFilterTexts = new ArrayList<>();
        if (isControlRank()) {
            mFilterTexts.add("Rank between " + mRankHigh + " and " + mRankLow);
        }
        else {
            if (!TextUtils.isEmpty(bean.getSign()) && !bean.getSign().equals(AppConstants.FILTER_ALL)) {
                mFilterTexts.add(bean.getSign());
            }
            if (!TextUtils.isEmpty(bean.getCountry()) && !bean.getCountry().equals(AppConstants.FILTER_ALL)) {
                mFilterTexts.add(bean.getCountry());
            }
            if (bean.getForehand() == 1) {
                mFilterTexts.add("左手持拍");
            }
            else if (bean.getForehand() == 2) {
                mFilterTexts.add("右手持拍");
            }
            if (bean.getBackhand() == 1) {
                mFilterTexts.add("单手反手");
            }
            else if (bean.getForehand() == 2) {
                mFilterTexts.add("双手反手");
            }
        }
    }

    public List<String> getFilterTexts() {
        return mFilterTexts;
    }

    private void filterObservable(Observable<Boolean> observable) {
        loadingObserver.setValue(true);
        clearIndex.setValue(true);
        observable
                .flatMap(aBoolean -> createIndexes())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(String index) {
                        indexObserver.setValue(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                    }

                    @Override
                    public void onComplete() {
                        loadingObserver.setValue(false);
                        playersObserver.setValue(mList);
                        onIndexCreated.setValue(true);
                        onSortFinished.setValue(sortType);
                    }
                });
    }

    private Observable<Boolean> filterByText(String text) {
        return Observable.create(e -> {
            mList.clear();
            mKeyword = text;
            for (int i = 0; i < mFullList.size(); i ++) {
                // 只对competitor进行filter
                if (mFullList.get(i).getCompetitorBean() instanceof User) {
                    mList.add(mFullList.get(i));
                }
                else {
                    if (TextUtils.isEmpty(text)) {
                        mList.add(mFullList.get(i));
                    }
                    else {
                        if (isMatchForKeyword(mFullList.get(i), text)) {
                            mList.add(mFullList.get(i));
                        }
                    }
                }
            }
            e.onNext(true);
            e.onComplete();
        });
    }

    private boolean isMatchForKeyword(RichPlayerBean bean, String text) {
        // 中文、英文、拼音模糊匹配
        if (bean.getCompetitorBean().getNameChn().toLowerCase().contains(text.toLowerCase())) {
            return true;
        }
        if (bean.getCompetitorBean().getNameEng() != null && bean.getCompetitorBean().getNameEng().toLowerCase().contains(text.toLowerCase())) {
            return true;
        }
        if (bean.getCompetitorBean().getNamePinyin() != null && bean.getCompetitorBean().getNamePinyin().toLowerCase().contains(text.toLowerCase())) {
            return true;
        }
        return false;
    }

    private Observable<Boolean> filterByBean(RichFilterBean bean) {
        return Observable.create(e -> {
            mList.clear();
            for (int i = 0; i < mFullList.size(); i ++) {
                // 只对competitor进行filter
                if (mFullList.get(i).getCompetitorBean() instanceof PlayerBean) {
                    if (!checkForehand(mFullList.get(i), bean.getForehand())) {
                        continue;
                    }
                    if (!checkBackhand(mFullList.get(i), bean.getBackhand())) {
                        continue;
                    }
                    if (!checkSign(mFullList.get(i), bean.getSign())) {
                        continue;
                    }
                    if (!checkCountry(mFullList.get(i), bean.getCountry())) {
                        continue;
                    }
                }
                mList.add(mFullList.get(i));
            }
            e.onNext(true);
            e.onComplete();
        });
    }

    private Observable<List<RichPlayerBean>> filterRank() {
        return Observable.create(new ObservableOnSubscribe<List<RichPlayerBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<RichPlayerBean>> e) throws Exception {

            }
        });
    }

    private boolean checkForehand(RichPlayerBean bean, int forehand) {
        PlayerAtpBean atpBean = bean.getCompetitorBean().getAtpBean();
        switch (forehand) {
            // left hand
            case 1:
                return atpBean != null && atpBean.getPlays().startsWith("Left-Handed");
            // right hand
            case 2:
                return atpBean != null && atpBean.getPlays().startsWith("Right-Handed");
            default:
                return true;
        }
    }

    private boolean checkBackhand(RichPlayerBean bean, int forehand) {
        PlayerAtpBean atpBean = bean.getCompetitorBean().getAtpBean();
        switch (forehand) {
            // left hand
            case 1:
                return atpBean != null && atpBean.getPlays().contains("One-Handed");
            // right hand
            case 2:
                return atpBean != null && atpBean.getPlays().contains("Two-Handed");
            default:
                return true;
        }
    }

    private boolean checkSign(RichPlayerBean bean, String sign) {
        if (AppConstants.FILTER_ALL.equals(sign)) {
            return true;
        }
        PlayerAtpBean atpBean = bean.getCompetitorBean().getAtpBean();
        String birthday;
        if (atpBean != null && !TextUtils.isEmpty(atpBean.getBirthday())) {
            birthday = atpBean.getBirthday();
        }
        else {
            birthday = bean.getCompetitorBean().getBirthday();
        }
        try {
            return sign.equals(ConstellationUtil.getConstellationEng(birthday));
        } catch (Exception e) {}
        return false;
    }

    private boolean checkCountry(RichPlayerBean bean, String country) {
        if (AppConstants.FILTER_ALL.equals(country)) {
            return true;
        }
        PlayerAtpBean atpBean = bean.getCompetitorBean().getAtpBean();
        String beanCountry;
        if (atpBean != null && !TextUtils.isEmpty(atpBean.getBirthCountry())) {
            beanCountry = atpBean.getBirthCountry();
        }
        else {
            beanCountry = bean.getCompetitorBean().getCountry();
        }
        return country.equals(beanCountry);
    }

    public void setExpandAll(boolean expandAll) {
        for (int i = 0; i < mList.size(); i ++) {
            CompetitorBean bean = mList.get(i).getCompetitorBean();
            if (expandAll) {
                if (bean instanceof User) {
                    mExpandMap.put(bean.getId(), false);
                }
                else {
                    mExpandMap.put(bean.getId(), true);
                }
            }
            else {
                mExpandMap.put(bean.getId(), expandAll);
            }
        }
    }

    public Map<Long, Boolean> getExpandMap() {
        return mExpandMap;
    }

    public String getItemIndex(int position) {
        return indexEmitter.getIndex(position);
    }

    /**
     * h2h will be changed after user changed
     * @param user
     */
    public void onUserChanged(User user) {
        if (user != mUser) {
            mUser = user;
            loadPlayers(mOnlyShowUser);
        }
    }

    public int[] getWinLoseOfList() {
        int[] result = new int[2];
        for (RichPlayerBean bean:mList) {
            result[0] += bean.getWin();
            result[1] += bean.getLose();
        }
        return result;
    }

    public void resetRank() {
        mRankHigh = 0;
        mRankLow = 0;
    }

    public int getTotalPlayers() {
        return mFullList == null ? 0:mFullList.size();
    }

    public int getListSize() {
        return mList == null ? 0:mList.size();
    }
}
