package com.king.app.tcareer.page.match.manage;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.MatchComparator;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchBeanDao;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;
import com.king.app.tcareer.page.setting.SettingProperty;

import org.greenrobot.greendao.query.QueryBuilder;

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
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/30 14:13
 */
public class MatchManagePresenter extends BasePresenter<MatchManageView> {

    private int sortType;

    private List<MatchNameBean> matchList;

    @Override
    protected void onCreate() {
        sortType = SettingProperty.getMatchSortMode();
    }

    public void loadMatches() {
        view.showLoading();
        Observable<List<MatchNameBean>> observable = queryMatches();
        // week是默认排序
        if (sortType != SettingProperty.VALUE_SORT_MATCH_WEEK) {
            observable.flatMap(new Function<List<MatchNameBean>, ObservableSource<List<MatchNameBean>>>() {
                @Override
                public ObservableSource<List<MatchNameBean>> apply(List<MatchNameBean> list) throws Exception {
                    return sortMatchesRx(list, sortType);
                }
            });
        }
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<MatchNameBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<MatchNameBean> list) {
                        matchList = list;
                        view.dismissLoading();
                        view.showMatches(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Load matches failed:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<List<MatchNameBean>> queryMatches() {
        return Observable.create(new ObservableOnSubscribe<List<MatchNameBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MatchNameBean>> e) throws Exception {
                MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
                QueryBuilder<MatchNameBean> builder = dao.queryBuilder();
                builder.join(MatchNameBeanDao.Properties.MatchId, MatchBean.class);

                // 默认按week升序排列
                String sortColumn = MatchBeanDao.Properties.Week.columnName;
                builder.orderRaw("J1." + sortColumn);
                List<MatchNameBean> list = builder.build().list();
                e.onNext(list);
            }
        });
    }

    private Observable<List<MatchNameBean>> sortMatchesRx(final List<MatchNameBean> list, final int sortType) {
        return Observable.create(new ObservableOnSubscribe<List<MatchNameBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MatchNameBean>> e) throws Exception {
                Collections.sort(list, new MatchComparator(sortType));
                e.onNext(list);
            }
        });
    }

    /**
     * sort matches by type
     * @param sortType
     */
    public void sortMatch(final int sortType) {{
        if (matchList == null) {
            return;
        }
        view.showLoading();
        sortMatchesRx(matchList, sortType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object o) {
                        view.dismissLoading();
                        SettingProperty.setMatchSortMode(sortType);
                        view.sortFinished(matchList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.dismissLoading();
                        view.showMessage("Sort failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    }

    /**
     * delete match
     * @param bean
     */
    public void deleteMatch(MatchNameBean bean) {
        deleteMatchRx(bean)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object o) {
                        view.showMessage("Delete successfully");
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

    private Observable<Object> deleteMatchRx(final MatchNameBean bean) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
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
}
