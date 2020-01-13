package com.king.app.tcareer.page.match.manage;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.bean.MatchImageBean;
import com.king.app.tcareer.model.comparator.MatchImageComparator;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchBeanDao;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;
import com.king.app.tcareer.page.setting.SettingProperty;

import org.greenrobot.greendao.query.QueryBuilder;

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
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/30 14:13
 */
public class MatchManageViewModel extends BaseViewModel {

    private int sortType;

    private String mKeyword;

    public MutableLiveData<List<MatchImageBean>> matchesObserver = new MutableLiveData<>();

    public MatchManageViewModel(@NonNull Application application) {
        super(application);
        sortType = SettingProperty.getMatchSortMode();
    }

    public void loadMatches() {
        loadingObserver.setValue(true);
        Observable<List<MatchNameBean>> observable = queryMatches();
        if (!TextUtils.isEmpty(mKeyword)) {
            observable.flatMap(list -> filterMatch(list));
        }
        observable
                .flatMap(list -> toImageItems(list))
                .flatMap(list -> sortMatchesRx(list, sortType))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<MatchImageBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<MatchImageBean> list) {
                        loadingObserver.setValue(false);
                        matchesObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("Load matches failed:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private ObservableSource<List<MatchImageBean>> toImageItems(List<MatchNameBean> list) {
        return observer -> {
            List<MatchImageBean> results = new ArrayList<>();
            for (MatchNameBean bean:list) {
                MatchImageBean imageBean = new MatchImageBean();
                imageBean.setBean(bean);
                imageBean.setImageUrl(ImageProvider.getMatchHeadPath(bean.getName(), bean.getMatchBean().getCourt()));
                results.add(imageBean);
            }
            observer.onNext(results);
        };
    }

    public Observable<List<MatchNameBean>> queryMatches() {
        return Observable.create(e -> {
            MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
            QueryBuilder<MatchNameBean> builder = dao.queryBuilder();
            builder.join(MatchNameBeanDao.Properties.MatchId, MatchBean.class);

            // 默认按week升序排列
            String sortColumn = MatchBeanDao.Properties.Week.columnName;
            builder.orderRaw("J1." + sortColumn);
            List<MatchNameBean> list = builder.build().list();
            e.onNext(list);
        });
    }

    private Observable<List<MatchImageBean>> sortMatchesRx(List<MatchImageBean> list, int sortType) {
        return Observable.create(e -> {
            Collections.sort(list, new MatchImageComparator(sortType));
            e.onNext(list);
        });
    }

    private Observable<List<MatchNameBean>> filterMatch(List<MatchNameBean> list) {
        return Observable.create(e -> {
            List<MatchNameBean> results = new ArrayList<>();
            for (int i = 0; i < list.size(); i ++) {
                if (TextUtils.isEmpty(mKeyword)) {
                    results.add(list.get(i));
                }
                else {
                    if (isMatchForKeyword(list.get(i), mKeyword)) {
                        results.add(list.get(i));
                    }
                }
            }
            e.onNext(results);
        });
    }

    private boolean isMatchForKeyword(MatchNameBean bean, String text) {
        // 支持name，国家，城市
        if (bean.getName().toLowerCase().contains(text.toLowerCase())) {
            return true;
        }
        if (bean.getMatchBean().getCountry().contains(text.toLowerCase())) {
            return true;
        }
        if (bean.getMatchBean().getCity().contains(text.toLowerCase())) {
            return true;
        }
        return false;
    }

    /**
     * sort matches by type
     * @param sortType
     */
    public void sortMatch(int sortType) {{
        if (matchesObserver.getValue() == null) {
            return;
        }
        loadingObserver.setValue(true);
        sortMatchesRx(matchesObserver.getValue(), sortType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<MatchImageBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<MatchImageBean> list) {
                        updateSortType(sortType);
                        SettingProperty.setMatchSortMode(sortType);
                        loadingObserver.setValue(false);
                        matchesObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("Sort failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    }

    private void updateSortType(int sortType) {
        this.sortType = sortType;
    }

    /**
     * delete match
     * @param list
     */
    public void deleteMatch(List<MatchNameBean> list) {
        deleteMatchRx(list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object o) {
                        messageObserver.setValue("Delete successfully");
                        loadMatches();
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

    private Observable<Object> deleteMatchRx(final List<MatchNameBean> list) {
        return Observable.create(e -> {
            for (MatchNameBean bean:list) {
                // 1对1的MatchNameBean还要删除MatchBean
                if (bean.getMatchBean().getNameBeanList().size() == 1) {
                    MatchBeanDao dao = TApplication.getInstance().getDaoSession().getMatchBeanDao();
                    dao.queryBuilder().where(MatchBeanDao.Properties.Id.eq(bean.getMatchBean().getId()))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                }
                MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
                dao.queryBuilder().where(MatchNameBeanDao.Properties.Id.eq(bean.getId()))
                        .buildDelete()
                        .executeDeleteWithoutDetachingEntities();
            }
        });
    }

    public void filterMatches(String words) {
        mKeyword = words;
        loadMatches();
    }

    public void setWeekOffset(String week) {
        try {
            int offset = Integer.parseInt(week);
            List<MatchBean> list = getDaoSession().getMatchBeanDao().loadAll();
            for (MatchBean bean:list) {
                bean.setWeek(bean.getWeek() + offset);
            }
            getDaoSession().getMatchBeanDao().updateInTx(list);
            getDaoSession().getMatchBeanDao().detachAll();
        } catch (Exception e) {
            messageObserver.setValue("输入有误");
        }
    }
}
