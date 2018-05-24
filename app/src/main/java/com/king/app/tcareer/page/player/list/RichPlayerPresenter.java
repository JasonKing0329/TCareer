package com.king.app.tcareer.page.player.list;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.PlayerComparator;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.dao.H2HDao;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.page.player.atp.PlayerAtpPresenter;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.ConstellationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc user永远不参与sort, filter
 * @auth 景阳
 * @time 2018/5/19 0019 15:36
 */

public class RichPlayerPresenter extends PlayerAtpPresenter<RichPlayerView> {

    private int sortType;

    private int mOperationPosition;

    private List<RichPlayerBean> mList;

    private List<RichPlayerBean> mFullList;

    private String mKeyword;

    private IndexEmitter indexEmitter;

    private Map<Long, Boolean> mExpandMap;

    private User mUser;

    public RichPlayerPresenter() {
        sortType = SettingProperty.getPlayerSortMode();
        indexEmitter = new IndexEmitter();
        mExpandMap = new HashMap<>();
    }

    public void loadPlayers() {
        view.showLoading();
        view.getSidebar().clear();
        updateSidebarGravity();
        Observable.combineLatest(queryUsers(), queryPlayers()
                , (users, players) -> {
                    List<RichPlayerBean> list = new ArrayList<>();
                    list.addAll(users);
                    list.addAll(players);
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
                        view.getSidebar().addIndex(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Load players failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.dismissLoading();
                        view.getSidebar().build();
                        view.getSidebar().setVisibility(View.VISIBLE);
                        view.showPlayers(mList);
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
                viewBean.setCompetitorBean(user);

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
                }

                list.add(viewBean);
            }
            e.onNext(list);
            e.onComplete();
        });
    }

    private Observable<List<RichPlayerBean>> queryPlayers() {
        return Observable.create(e -> {
            PlayerBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
            List<PlayerBean> players = dao.queryBuilder().build().list();

            H2HDao h2hDao = new H2HDao();
            List<RichPlayerBean> list = new ArrayList<>();
            for (PlayerBean player:players) {
                RichPlayerBean viewBean = new RichPlayerBean();
                viewBean.setCompetitorBean(player);

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
                    indexEmitter.createRecordsIndex(e, mList);
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

    public void sortPlayer(final int sortType) {
        view.showLoading();
        this.sortType = sortType;
        view.getSidebar().clear();
        updateSidebarGravity();
        sortPlayerRx(mList)
                .flatMap(list -> {
                    SettingProperty.setPlayerSortMode(sortType);
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
                        view.getSidebar().addIndex(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Sort players failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.dismissLoading();
                        view.getSidebar().build();
                        view.getSidebar().setVisibility(View.VISIBLE);
                        view.sortFinished();
                    }
                });
    }

    private void updateSidebarGravity() {
        if (sortType == SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION) {
            view.getSidebar().setGravity(Gravity.RIGHT);
        }
        else {
            view.getSidebar().setGravity(Gravity.CENTER);
        }
    }

    public void updateAtpData(String atpiId, int position) {
        this.mOperationPosition = position;
        updateAtpData(atpiId);
    }

    @Override
    protected void onUpdateAtpCompleted(PlayerAtpBean bean) {
        super.onUpdateAtpCompleted(bean);
        view.onUpdateAtpCompleted(mOperationPosition);
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
                        view.deleteSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Delete failed: " + e.getMessage());
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
        filterObservable(filterByBean(bean));
    }

    private void filterObservable(Observable<Boolean> observable) {
        view.showLoading();
        view.getSidebar().clear();
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
                        view.getSidebar().addIndex(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                    }

                    @Override
                    public void onComplete() {
                        view.dismissLoading();
                        view.showPlayers(mList);
                        view.getSidebar().build();
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
        if ("All".equals(sign)) {
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
        if ("All".equals(country)) {
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
            loadPlayers();
        }
    }
}
