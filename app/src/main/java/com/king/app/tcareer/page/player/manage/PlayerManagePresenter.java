package com.king.app.tcareer.page.player.manage;

import android.text.TextUtils;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.PlayerComparator;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.page.setting.SettingProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 11:48
 */

public class PlayerManagePresenter extends BasePresenter<PlayerManageView> {

    private List<PlayerViewBean> playerList;

    private Map<Character, Integer> playerIndexMap;

    private int sortType;

    @Override
    protected void onCreate() {
        sortType = SettingProperty.getPlayerSortMode();
    }

    public List<PlayerViewBean> getPlayerList() {
        return playerList;
    }

    public void loadPlayers() {
        view.showLoading();
        Observable.combineLatest(queryUsers(), queryPlayers()
                , new BiFunction<List<PlayerViewBean>, List<PlayerViewBean>, List<PlayerViewBean>>() {
                    @Override
                    public List<PlayerViewBean> apply(List<PlayerViewBean> users, List<PlayerViewBean> players) throws Exception {
                        List<PlayerViewBean> list = new ArrayList<>();
                        list.addAll(users);
                        list.addAll(players);
                        return list;
                    }
                })
                .flatMap(new Function<List<PlayerViewBean>, ObservableSource<List<PlayerViewBean>>>() {
                    @Override
                    public ObservableSource<List<PlayerViewBean>> apply(List<PlayerViewBean> list) throws Exception {
                        return sortPlayerRx(list);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<PlayerViewBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<PlayerViewBean> list) {
                        view.dismissLoading();
                        playerList = list;
                        view.showPlayers(list);
                        createIndex();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Load players failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<PlayerViewBean>> queryUsers() {
        return Observable.create(new ObservableOnSubscribe<List<PlayerViewBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PlayerViewBean>> e) throws Exception {
                UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
                List<User> users = dao.queryBuilder().build().list();

                List<PlayerViewBean> list = new ArrayList<>();
                for (User user:users) {
                    PlayerViewBean viewBean = new PlayerViewBean();
                    viewBean.setData(user);
                    viewBean.setBirthday(user.getBirthday());
                    viewBean.setCountry(user.getCountry());
                    viewBean.setName(user.getNameChn());
                    viewBean.setNameEng(user.getNameEng());
                    viewBean.setNamePinyin(user.getNamePinyin());
                    list.add(viewBean);
                }
                e.onNext(list);
            }
        });
    }

    private Observable<List<PlayerViewBean>> queryPlayers() {
        return Observable.create(new ObservableOnSubscribe<List<PlayerViewBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PlayerViewBean>> e) throws Exception {
                PlayerBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
                List<PlayerBean> players = dao.queryBuilder().build().list();

                List<PlayerViewBean> list = new ArrayList<>();
                for (PlayerBean player:players) {
                    PlayerViewBean viewBean = new PlayerViewBean();
                    viewBean.setData(player);
                    viewBean.setBirthday(player.getBirthday());
                    viewBean.setCountry(player.getCountry());
                    viewBean.setName(player.getNameChn());
                    viewBean.setNameEng(player.getNameEng());
                    viewBean.setNamePinyin(player.getNamePinyin());
                    list.add(viewBean);
                }
                e.onNext(list);
            }
        });
    }

    private void createIndex() {
        if (sortType == SettingProperty.VALUE_SORT_PLAYER_NAME || sortType == SettingProperty.VALUE_SORT_PLAYER_NAME_ENG) {
            view.clearSideBar();
            playerIndexMap = new HashMap<>();
            // player list查询出来已经是升序的
            for (int i = 0; i < playerList.size(); i ++) {
                if (playerList.get(i).getData() instanceof User) {
                    continue;
                }
                String targetText;
                if (sortType == SettingProperty.VALUE_SORT_PLAYER_NAME) {
                    targetText = playerList.get(i).getNamePinyin();
                }
                else {
                    targetText = playerList.get(i).getNameEng();
                    // 没有录入英文名的排在最后
                    if (TextUtils.isEmpty(targetText)) {
                        targetText = "ZZZZZZZZ";
                    }
                }
                char first = targetText.charAt(0);
                Integer index = playerIndexMap.get(first);
                if (index == null) {
                    playerIndexMap.put(first, i);
                    view.addSideBarIndex(String.valueOf(first));
                }
            }
            view.showSideBar(true);
        }
        else {
            view.showSideBar(false);
        }
    }

    public void sortPlayer(final int sortType) {
        view.showLoading();
        sortPlayerRx(playerList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<PlayerViewBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<PlayerViewBean> list) {
                        updateSortType(sortType);
                        SettingProperty.setPlayerSortMode(sortType);
                        view.dismissLoading();
                        view.sortFinished(list);
                        createIndex();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Sort players failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void updateSortType(int sortType) {
        this.sortType = sortType;
    }

    private Observable<List<PlayerViewBean>> sortPlayerRx(final List<PlayerViewBean> list) {
        return Observable.create(new ObservableOnSubscribe<List<PlayerViewBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PlayerViewBean>> e) throws Exception {
                Collections.sort(list, new PlayerComparator(sortType));
                e.onNext(list);
            }
        });
    }

    public void deletePlayer(final List<PlayerViewBean> list) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                PlayerBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();

                for (PlayerViewBean bean:list) {
                    // user不允许删除
                    if (bean.getData() instanceof PlayerBean) {
                        dao.queryBuilder().where(PlayerBeanDao.Properties.Id.eq(((PlayerBean) bean.getData()).getId()))
                                .buildDelete()
                                .executeDeleteWithoutDetachingEntities();
                    }
                }
                e.onNext(new Object());
            }
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

    public int getLetterPosition(String letter) {
        return playerIndexMap.get(letter.charAt(0));
    }
}
