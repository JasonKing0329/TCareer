package com.king.app.tcareer.page.player.atp;

import android.view.View;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerAtpBeanDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/18 9:22
 */
public class AtpManagePresenter extends PlayerAtpPresenter<AtpManageView> {

    private Map<String, Integer> indexMap;

    @Override
    protected void onCreate() {
        super.onCreate();
        indexMap = new HashMap<>();
    }

    public void loadData() {
        view.getSideBar().clear();
        loadAtpPlayers()
                .flatMap(list -> {
                    view.postShowPlayers(list);
                    return createIndex(list);
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
                        view.getSideBar().setVisibility(View.VISIBLE);
                        view.getSideBar().addIndex(index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        view.getSideBar().setVisibility(View.VISIBLE);
                    }
                });
    }

    private Observable<List<PlayerAtpBean>> loadAtpPlayers() {
        return Observable.create(e -> {
            PlayerAtpBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerAtpBeanDao();
            List<PlayerAtpBean> list = dao.queryBuilder()
                    .orderAsc(PlayerAtpBeanDao.Properties.Name)
                    .build().list();
            e.onNext(list);
        });
    }

    private Observable<String> createIndex(final List<PlayerAtpBean> list) {
        return Observable.create(e -> {
            indexMap.clear();
            String index = null;
            for (int i = 0; i < list.size(); i ++) {
                String newIndex = String.valueOf(list.get(i).getName().charAt(0));
                if (!newIndex.equals(index)) {
                    indexMap.put(newIndex, i);
                    index = newIndex;
                }
            }

            int last = 0;
            for (char i = 'A'; i <= 'Z'; i ++) {
                String curIndex = String.valueOf(i);
                // 如果没有，定位到上一个
                if (indexMap.get(curIndex) == null) {
                    indexMap.put(curIndex, last);
                }
                else {
                    last = indexMap.get(curIndex);
                }
                e.onNext(curIndex);
            }

            // 不知道为什么不起作用
            e.onComplete();
        });
    }

    public int getIndexPosition(String index) {
        return indexMap.get(index);
    }

    public void deleteData(List<PlayerAtpBean> list) {
        PlayerAtpBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerAtpBeanDao();
        dao.deleteInTx(list);
        dao.detachAll();
    }

    @Override
    protected void onFetchCompleted() {
        super.onFetchCompleted();
        // refresh data
        loadData();
    }
}
