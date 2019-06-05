package com.king.app.tcareer.page.match.gallery;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.repository.MatchRepository;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/15 10:12
 */
public class UserMatchPresenter extends BasePresenter<UserMatchView> {

    private List<UserMatchBean> matchList;

    private MatchRepository repository;

    @Override
    protected void onCreate() {
        repository = new MatchRepository();
    }

    public void loadMatches(final long userId) {
        queryUser(userId)
                .flatMap(user -> repository.queryUserMatches(userId))
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
                        view.showMatches(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Load matches failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 找到与当前周数最靠近的赛事，前后出现等间隔的以前者优先（跨周数赛事）
     * matchList 已经是按周数升序排列了
     * @return
     */
    public int findLatestWeekItem() {
        return repository.findLatestWeekItem(matchList);
    }

    public UserMatchBean getUserMatchBean(int position) {
        return matchList.get(position);
    }

}
