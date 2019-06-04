package com.king.app.tcareer.page.match.common;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.utils.DebugLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: match for all user
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 16:48
 */
public class MatchCommonViewModel extends BaseViewModel {

    public ObservableField<String> matchImageUrl = new ObservableField<>();

    public ObservableField<String> matchNameText = new ObservableField<>();

    public ObservableField<String> matchCountryText = new ObservableField<>();

    public ObservableField<String> matchLevelText = new ObservableField<>();

    public ObservableField<String> matchCourtText = new ObservableField<>();

    public MutableLiveData<List<UserItem>> usersObserver = new MutableLiveData<>();

    private MatchNameBean mMatchNameBean;

    public MatchCommonViewModel(@NonNull Application application) {
        super(application);
    }

    public MatchNameBean getmMatchNameBean() {
        return mMatchNameBean;
    }

    public void loadMatch(long matchNameId) {
        loadingObserver.setValue(true);
        getMatch(matchNameId)
                .flatMap(bean -> {
                    mMatchNameBean = bean;
                    matchNameText.set(bean.getName());
                    matchCountryText.set(bean.getMatchBean().getCountry() + "/" + bean.getMatchBean().getCity());
                    matchCourtText.set(bean.getMatchBean().getCourt());
                    matchLevelText.set(bean.getMatchBean().getLevel());
                    matchImageUrl.set(ImageProvider.getMatchHeadPath(bean.getName(), bean.getMatchBean().getCourt()));
                    return getUsers(bean.getMatchBean());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<UserItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<UserItem> userItems) {
                        loadingObserver.setValue(false);
                        usersObserver.setValue(userItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<MatchNameBean> getMatch(long matchNameId) {
        return Observable.create(e -> {
            MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
            MatchNameBean nameBean = dao.queryBuilder()
                    .where(MatchNameBeanDao.Properties.Id.eq(matchNameId))
                    .build().unique();
            e.onNext(nameBean);
        });
    }

    public ObservableSource<List<UserItem>> getUsers(MatchBean matchBean) {
        return observer -> {
            List<UserItem> userList = new ArrayList<>();

            UserDao userDao = TApplication.getInstance().getDaoSession().getUserDao();
            List<User> users = userDao.queryBuilder().build().list();
            RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
            for (User user:users) {
                UserItem item = new UserItem();
                item.setUser(user);
                item.setName(user.getNameEng());
                item.setImageUrl(ImageProvider.getDetailPlayerPath(user.getNameChn()));

                // 查询MatchBean对应所有赛事的record
                StringBuffer sqlBuffer = new StringBuffer("WHERE ");
                sqlBuffer.append(RecordDao.Properties.UserId.columnName)
                        .append(" = ? AND (");
                List<MatchNameBean> nameList = matchBean.getNameBeanList();
                String[] args = new String[1 + nameList.size()];
                args[0] = String.valueOf(user.getId());
                for (int i = 0; i < nameList.size(); i ++) {
                    MatchNameBean bean = nameList.get(i);
                    if (i > 0) {
                        sqlBuffer.append(" OR ");
                    }
                    sqlBuffer.append(RecordDao.Properties.MatchNameId.columnName).append(" = ?");
                    args[i + 1] = String.valueOf(bean.getId());
                }
                sqlBuffer.append(")");
                DebugLog.e(sqlBuffer.toString());
                List<Record> list = recordDao.queryRaw(sqlBuffer.toString(), args);

                List<Integer> yearList = new ArrayList<>();
                int win = 0, lose = 0;
                for (Record record:list) {

                    // 统计参赛年份
                    int year = Integer.parseInt(record.getDateStr().split("-")[0]);
                    if (!yearList.contains(year)) {
                        yearList.add(year);
                    }

                    // 统计胜负场次
                    // W/0不算作胜负场
                    if (record.getRetireFlag() != AppConstants.RETIRE_WO) {
                        if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                            win ++;
                        }
                        else {
                            lose ++;
                        }
                    }
                }

                // 参赛年份按升序排列
                String years = null;
                Collections.sort(yearList);
                for (Integer year:yearList) {
                    if (years == null) {
                        years = String.valueOf(year);
                    }
                    else {
                        years = years.concat(", ").concat(String.valueOf(year));
                    }
                }

                item.setH2h(win + " - " + lose);
                item.setYears(years);
                userList.add(item);
            }

            observer.onNext(userList);
        };
    }
}
