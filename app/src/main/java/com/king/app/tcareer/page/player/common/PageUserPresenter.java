package com.king.app.tcareer.page.player.common;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.dao.H2HDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.page.player.page.PagePresenter;
import com.king.app.tcareer.page.player.page.TabBean;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @desc
 * @auth 景阳
 * @time 2018/4/21 0021 18:18
 */

public class PageUserPresenter extends PagePresenter {

    @Override
    public void loadPlayerAndUser(long playerId, long userId, boolean playerIsUser) {
        queryCompetitor(playerId, playerIsUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object playerBean) {
                        loadPlayerInfor();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showError("Load player failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected List<TabBean> createTabs() {
        List<TabBean> list = new ArrayList<>();
        UserDao userDao = TApplication.getInstance().getDaoSession().getUserDao();
        H2HDao h2HDao = new H2HDao();
        List<User> users = userDao.loadAll();
        for (User user:users) {
            H2hBean h2h = h2HDao.getH2h(user.getId(), mCompetitor.getId(), mCompetitor instanceof User);
            UserTabBean bean = new UserTabBean();
            // TAG_SET_USER_ID_AS_TAB_ID
            bean.id = String.valueOf(user.getId());
            // page user tab是以当前mCompetitor为主角的，所以这里的win lose与page court tab是相反的
            bean.win = h2h.getLose();
            bean.lose = h2h.getWin();
            bean.total = h2h.getTotal();
            bean.user = user;
            if (bean.total > 0) {
                list.add(bean);
            }
        }
        return list;
    }

    @Override
    public User getUser(String tabId) {
        // refer to TAG_SET_USER_ID_AS_TAB_ID
        long userId = Long.parseLong(tabId);
        UserDao userDao = TApplication.getInstance().getDaoSession().getUserDao();
        return userDao.queryBuilder()
                .where(UserDao.Properties.Id.eq(userId))
                .build().unique();
    }

    @Override
    protected List<Record> createTabRecords(String tabId) {
        // refer to TAG_SET_USER_ID_AS_TAB_ID
        long userId = Long.parseLong(tabId);
        QueryBuilder<Record> builder = TApplication.getInstance().getDaoSession().getRecordDao().queryBuilder();
        builder.where(RecordDao.Properties.UserId.eq(userId));
        builder.where(RecordDao.Properties.PlayerId.eq(mCompetitor.getId()));
        if (mCompetitor instanceof User) {
            builder.where(RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_VIRTUAL));
        }
        else {
            builder.where(RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_NORMAL));
        }
        builder.orderDesc(RecordDao.Properties.Id);
        return builder.build().list();
    }

    @Override
    protected boolean filterRecord(Record record) {
        return true;
    }
}
