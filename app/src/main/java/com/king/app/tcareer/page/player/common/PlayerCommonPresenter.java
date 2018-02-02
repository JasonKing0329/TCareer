package com.king.app.tcareer.page.player.common;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.page.player.manage.PlayerViewBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 11:02
 */

public class PlayerCommonPresenter extends BasePresenter<PlayerCommonView> {

    private PlayerViewBean mPlayerViewBean;

    @Override
    protected void onCreate() {

    }

    public void loadPlayer(final long playerId, final boolean isUser) {
        view.showLoading();
        Observable observable;
        if (isUser) {
            observable = queryUser(playerId);
        }
        else {
            observable = queryPlayer(playerId);
        }
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PlayerViewBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(PlayerViewBean playerBean) {
                        mPlayerViewBean = playerBean;
                        view.showPlayer(playerBean);
                        loadH2H(playerId, isUser);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Load player failed:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<PlayerViewBean> queryPlayer(final long playerId) {
        return Observable.create(new ObservableOnSubscribe<PlayerViewBean>() {
            @Override
            public void subscribe(ObservableEmitter<PlayerViewBean> e) throws Exception {
                PlayerBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
                PlayerBean bean = dao.queryBuilder()
                        .where(PlayerBeanDao.Properties.Id.eq(playerId))
                        .build().unique();
                PlayerViewBean viewBean = new PlayerViewBean();
                viewBean.setData(bean);
                viewBean.setNameEng(bean.getNameEng());
                viewBean.setName(bean.getNameChn());
                viewBean.setNamePinyin(bean.getNamePinyin());
                viewBean.setCountry(bean.getCountry());
                viewBean.setBirthday(bean.getBirthday());
                e.onNext(viewBean);
            }
        });
    }

    private Observable<PlayerViewBean> queryUser(final long userId) {
        return Observable.create(new ObservableOnSubscribe<PlayerViewBean>() {
            @Override
            public void subscribe(ObservableEmitter<PlayerViewBean> e) throws Exception {
                UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
                User bean = dao.queryBuilder()
                        .where(UserDao.Properties.Id.eq(userId))
                        .build().unique();
                PlayerViewBean viewBean = new PlayerViewBean();
                viewBean.setData(bean);
                viewBean.setNameEng(bean.getNameEng());
                viewBean.setName(bean.getNameChn());
                viewBean.setNamePinyin(bean.getNamePinyin());
                viewBean.setCountry(bean.getCountry());
                viewBean.setBirthday(bean.getBirthday());
                e.onNext(viewBean);
            }
        });
    }

    public PlayerViewBean getmPlayerViewBean() {
        return mPlayerViewBean;
    }

    private class H2HBean {
        int win;
        int lose;
        User user;
        boolean isFinish;
    }

    private void loadH2H(long playerId, boolean isUser) {
        queryH2H(playerId, isUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<H2HBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(H2HBean h2HBean) {
                        if (h2HBean.isFinish) {
                            view.dismissLoading();
                        }
                        else {
                            view.showH2H(h2HBean.user, h2HBean.win, h2HBean.lose);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Load h2h failed:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<H2HBean> queryH2H(final long playerId, final boolean isUser) {
        return Observable.create(new ObservableOnSubscribe<H2HBean>() {
            @Override
            public void subscribe(ObservableEmitter<H2HBean> e) throws Exception {
                UserDao userDao = TApplication.getInstance().getDaoSession().getUserDao();
                List<User> users = userDao.queryBuilder().build().list();

                RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
                for (User user:users) {
                    WhereCondition[] conditions = new WhereCondition[3];
                    conditions[0] = RecordDao.Properties.PlayerId.eq(playerId);
                    conditions[1] = RecordDao.Properties.RetireFlag.notEq(AppConstants.RETIRE_WO);
                    if (isUser) {
                        conditions[2] = RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_VIRTUAL);
                    }
                    else {
                        conditions[2] = RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_NORMAL);
                    }
                    List<Record> records = recordDao.queryBuilder()
                            .where(RecordDao.Properties.UserId.eq(user.getId())
                                , conditions)
                            .build()
                            .list();
                    H2HBean bean = new H2HBean();
                    bean.user = user;
                    for (Record record:records) {
                        if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                            bean.lose ++;
                        }
                        else {
                            bean.win ++;
                        }
                    }
                    e.onNext(bean);
                }
                H2HBean bean = new H2HBean();
                bean.isFinish = true;
                e.onNext(bean);
            }
        });
    }
}
