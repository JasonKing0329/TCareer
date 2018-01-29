package com.king.app.tcareer.page.match.common;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
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
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: match for all user
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 16:48
 */
public class MatchCommonPresenter extends BasePresenter<MatchCommonView> {

    @Override
    protected void onCreate() {

    }

    private class MessageBean {
        User user;
        String h2h;
        String years;
        boolean isFinished;
    }

    public void loadMatch(long matchNameId) {
        view.showLoading();
        getMatch(matchNameId)
                .flatMap(new Function<MatchNameBean, ObservableSource<MessageBean>>() {
                    @Override
                    public ObservableSource<MessageBean> apply(MatchNameBean matchNameBean) throws Exception {
                        view.postShowMatchInfor(matchNameBean);
                        return getPresentMessage(matchNameBean.getMatchBean());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<MessageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(MessageBean message) {
                        if (message.isFinished) {
                            view.dismissLoading();
                        }
                        else {
                            view.showUserInfor(message.user, message.h2h, message.years);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<MatchNameBean> getMatch(final long matchNameId) {
        return Observable.create(new ObservableOnSubscribe<MatchNameBean>() {
            @Override
            public void subscribe(ObservableEmitter<MatchNameBean> e) throws Exception {
                MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
                MatchNameBean nameBean = dao.queryBuilder()
                        .where(MatchNameBeanDao.Properties.Id.eq(matchNameId))
                        .build().unique();
                e.onNext(nameBean);
            }
        });
    }

    public ObservableSource<MessageBean> getPresentMessage(final MatchBean matchBean) {
        return new ObservableSource<MessageBean>() {
            @Override
            public void subscribe(Observer<? super MessageBean> observer) {
                UserDao userDao = TApplication.getInstance().getDaoSession().getUserDao();
                List<User> users = userDao.queryBuilder().build().list();
                RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
                for (User user:users) {
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

                    MessageBean messageBean = new MessageBean();
                    messageBean.user = user;
                    messageBean.h2h = win + " - " + lose;
                    messageBean.years = years;
                    observer.onNext(messageBean);
                }

                MessageBean messageBean = new MessageBean();
                messageBean.isFinished = true;
                observer.onNext(messageBean);
            }
        };
    }
}
