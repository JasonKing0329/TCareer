package com.king.app.tcareer.page.home;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.page.match.gallery.UserMatchBean;
import com.king.app.tcareer.page.player.slider.SlideItem;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.repository.MatchRepository;
import com.king.app.tcareer.utils.DBExportor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/4/3 0003.
 */

public class HomeViewModel extends BaseViewModel {

    public MutableLiveData<User> userObserver = new MutableLiveData<>();

    public MutableLiveData<List<User>> allUsersObserver = new MutableLiveData<>();

    public MutableLiveData<Record> lastRecordObserver = new MutableLiveData<>();

    public MutableLiveData<List<SlideItem<CompetitorBean>>> competitorsObserver = new MutableLiveData<>();

    public MutableLiveData<List<UserMatchBean>> matchesObserver = new MutableLiveData<>();

    private final int PLAYER_NUM = 10;

    private List<User> mAllUsers;

    private List<UserMatchBean> matchList;

    private MatchRepository matchRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        matchRepository = new MatchRepository();
    }

    public List<User> getAllUsers() {
        return mAllUsers;
    }

    /**
     * load login user,all users,
     * load last record,recent players,matches for login user
     */
    public void loadHomeDatas(long userId) {
        if (userId == -1) {
            userId = AppConstants.USER_ID_KING;
        }
        loadingObserver.setValue(true);
        queryUser(userId)
                .flatMap(user -> {
                    userObserver.postValue(user);
                    return queryAllUsers();
                }).flatMap(users -> {
                    mAllUsers = users;
                    allUsersObserver.postValue(users);
                    return queryLastRecord();
                })
                .flatMap(record -> {
                    lastRecordObserver.postValue(record);
                    return queryLastPlayers();
                })
                .flatMap(list -> {
                    competitorsObserver.postValue(list);
                    return queryMatches();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<UserMatchBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<UserMatchBean> list) {
                        matchList = list;
                        loadingObserver.setValue(false);
                        matchesObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("errors: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * change login user
     * load last record,recent players,matches for login user
     * @param user
     */
    public void changeUser(User user) {
        mUser = user;
        SettingProperty.setUserId(user.getId());
        userObserver.setValue(user);
        queryLastRecord()
                .flatMap(record -> {
                    lastRecordObserver.postValue(record);
                    return queryLastPlayers();
                })
                .flatMap(list -> {
                    competitorsObserver.postValue(list);
                    return queryMatches();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<UserMatchBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<UserMatchBean> list) {
                        matchList = list;
                        matchesObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("errors: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * record changed, refresh last record and recent competitors
     */
    public void setRecordChanged() {
        queryLastRecord()
                .flatMap(record -> {
                    lastRecordObserver.postValue(record);
                    return queryLastPlayers();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<SlideItem<CompetitorBean>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<SlideItem<CompetitorBean>> list) {
                        competitorsObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("errors: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<List<User>> queryAllUsers() {
        return Observable.create(e -> {
            UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
            List<User> list = dao.queryBuilder()
                    .build().list();
            e.onNext(list);
        });
    }

    private Observable<Record> queryLastRecord() {
        return Observable.create(e -> {
            RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
            Record record = dao.queryBuilder()
                    .where(RecordDao.Properties.UserId.eq(mUser.getId()))
                    .orderDesc(RecordDao.Properties.Id)
                    .limit(1)
                    .build().unique();
            e.onNext(record);
        });
    }

    private Observable<List<SlideItem<CompetitorBean>>> queryLastPlayers() {
        return Observable.create(e -> {
            Map<String, CompetitorBean> map = new HashMap<>();
            RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
            List<Record> list = dao.queryBuilder()
                    .where(RecordDao.Properties.UserId.eq(mUser.getId()))
                    .orderDesc(RecordDao.Properties.Id)
                    .build().list();
            List<SlideItem<CompetitorBean>> cptList = new ArrayList<>();
            int count = 0;
            for (int i = 0; count < PLAYER_NUM && i < list.size(); i ++) {
                Record record = list.get(i);
                String key = record.getPlayerId() + "_" + record.getPlayerFlag();
                CompetitorBean bean = map.get(key);
                if (bean == null) {
                    bean = CompetitorParser.getCompetitorFrom(record);
                    map.put(key, bean);

                    SlideItem<CompetitorBean> slideItem = new SlideItem<>();
                    slideItem.setBean(bean);
                    slideItem.setImageUrl(ImageProvider.getPlayerHeadPath(bean.getNameChn()));
                    cptList.add(slideItem);
                    count ++;
                }
            }
            e.onNext(cptList);
        });
    }

    public Observable<List<UserMatchBean>> queryMatches() {
        return matchRepository.queryUserMatches(mUser.getId());
    }

    public int findLatestWeekItem() {
        return matchRepository.findLatestWeekItem(matchList);
    }

    public void saveDatabase() {
        Observable.create(e -> {
            DBExportor.exportAsHistory();
            e.onNext(new Object());
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object object) {
                        messageObserver.setValue("save successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("save failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
