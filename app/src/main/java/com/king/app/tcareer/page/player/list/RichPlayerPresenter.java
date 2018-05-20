package com.king.app.tcareer.page.player.list;

import android.text.TextUtils;
import android.view.View;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.PlayerComparator;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.dao.H2HDao;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.page.player.atp.PlayerAtpPresenter;
import com.king.app.tcareer.page.setting.SettingProperty;

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
 * @desc
 * @auth 景阳
 * @time 2018/5/19 0019 15:36
 */

public class RichPlayerPresenter extends PlayerAtpPresenter<RichPlayerView> {

    private int sortType;

    private int mOperationPosition;

    private List<RichPlayerBean> mList;

    private List<RichPlayerBean> mFullList;

    private Map<Character, Integer> playerIndexMap;

    private String mKeyword;

    public RichPlayerPresenter() {
        sortType = SettingProperty.getPlayerSortMode();
    }

    public void loadPlayers() {
        view.showLoading();
        Observable.combineLatest(queryUsers(), queryPlayers()
                , (users, players) -> {
                    List<RichPlayerBean> list = new ArrayList<>();
                    list.addAll(users);
                    list.addAll(players);
                    return list;
                })
                .flatMap(list -> sortPlayerRx(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<RichPlayerBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<RichPlayerBean> list) {
                        mFullList = list;
                        mList = new ArrayList<>();
                        for (RichPlayerBean bean:list) {
                            mList.add(bean);
                        }

                        view.dismissLoading();
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

    private Observable<List<RichPlayerBean>> sortPlayerRx(List<RichPlayerBean> list) {
        return Observable.create(e -> {
            Collections.sort(list, new PlayerComparator(sortType));
            e.onNext(list);
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

                H2hBean h2hK = h2hDao.getH2h(AppConstants.USER_ID_KING, user.getId(), true);
                H2hBean h2hF = h2hDao.getH2h(AppConstants.USER_ID_FLAMENCO, user.getId(), true);
                H2hBean h2hH = h2hDao.getH2h(AppConstants.USER_ID_HENRY, user.getId(), true);
                H2hBean h2hQ = h2hDao.getH2h(AppConstants.USER_ID_QI, user.getId(), true);

                viewBean.setWin(h2hK.getWin() + h2hF.getWin() + h2hH.getWin() + h2hQ.getWin());
                viewBean.setLose(h2hK.getLose() + h2hF.getLose() + h2hH.getLose() + h2hQ.getLose());

                list.add(viewBean);
            }
            e.onNext(list);
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

                H2hBean h2hK = h2hDao.getH2h(AppConstants.USER_ID_KING, player.getId(), false);
                H2hBean h2hF = h2hDao.getH2h(AppConstants.USER_ID_FLAMENCO, player.getId(), false);
                H2hBean h2hH = h2hDao.getH2h(AppConstants.USER_ID_HENRY, player.getId(), false);
                H2hBean h2hQ = h2hDao.getH2h(AppConstants.USER_ID_QI, player.getId(), false);
                viewBean.setWin(h2hK.getWin() + h2hF.getWin() + h2hH.getWin() + h2hQ.getWin());
                viewBean.setLose(h2hK.getLose() + h2hF.getLose() + h2hH.getLose() + h2hQ.getLose());

                list.add(viewBean);
            }
            e.onNext(list);
        });
    }

    private void createIndex() {
        if (sortType == SettingProperty.VALUE_SORT_PLAYER_NAME || sortType == SettingProperty.VALUE_SORT_PLAYER_NAME_ENG) {
            view.getSidebar().clear();
            playerIndexMap = new HashMap<>();
            // player list查询出来已经是升序的
            for (int i = 0; i < mList.size(); i ++) {
                if (mList.get(i).getCompetitorBean() instanceof User) {
                    continue;
                }
                String targetText;
                if (sortType == SettingProperty.VALUE_SORT_PLAYER_NAME) {
                    targetText = mList.get(i).getCompetitorBean().getNamePinyin();
                }
                else {
                    targetText = mList.get(i).getCompetitorBean().getNameEng();
                    // 没有录入英文名的排在最后
                    if (TextUtils.isEmpty(targetText)) {
                        targetText = "ZZZZZZZZ";
                    }
                }
                char first = targetText.charAt(0);
                Integer index = playerIndexMap.get(first);
                if (index == null) {
                    playerIndexMap.put(first, i);
                    view.getSidebar().addIndex(String.valueOf(first));
                }
            }
            view.getSidebar().setVisibility(View.VISIBLE);
        }
        else {
            view.getSidebar().setVisibility(View.GONE);
        }
    }

    public int getLetterPosition(String letter) {
        return playerIndexMap.get(letter.charAt(0));
    }

    public void sortPlayer(final int sortType) {
        view.showLoading();
        this.sortType = sortType;
        sortPlayerRx(mList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<RichPlayerBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<RichPlayerBean> list) {
                        SettingProperty.setPlayerSortMode(sortType);
                        view.dismissLoading();
                        view.sortFinished();
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

    public void updateAtpData(String atpiId, int position) {
        this.mOperationPosition = position;
        updateAtpData(atpiId);
    }

    @Override
    protected void onUpdateAtpCompleted() {
        super.onUpdateAtpCompleted();
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

    public void filter(String text) {
        if (!text.equals(mKeyword)) {
            mList.clear();
            mKeyword = text;
            for (int i = 0; i < mFullList.size(); i ++) {
                if (TextUtils.isEmpty(text)) {
                    mList.add(mFullList.get(i));
                }
                else {
                    if (isMatchForKeyword(mFullList.get(i), text)) {
                        mList.add(mFullList.get(i));
                    }
                }
            }
            view.showPlayers(mList);
        }
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

}
