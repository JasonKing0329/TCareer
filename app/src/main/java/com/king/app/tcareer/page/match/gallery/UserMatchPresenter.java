package com.king.app.tcareer.page.match.gallery;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.MatchModel;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * <p/>创建时间: 2017/3/15 10:12
 */
public class UserMatchPresenter extends BasePresenter<UserMatchView> {

    private String strChampion = "冠军";
    private String strRunnerup = "亚军";

    private List<UserMatchBean> matchList;

    @Override
    protected void onCreate() {

    }

    public void loadMatches(final long userId) {
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<List<UserMatchBean>>>() {
                    @Override
                    public ObservableSource<List<UserMatchBean>> apply(User user) throws Exception {
                        return queryMatches(userId);
                    }
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

    public Observable<List<UserMatchBean>> queryMatches(final long userId) {
        return Observable.create(new ObservableOnSubscribe<List<UserMatchBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<UserMatchBean>> e) throws Exception {
                RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
                List<Record> recordList = recordDao.queryBuilder()
                        .where(RecordDao.Properties.UserId.eq(userId))
                        .build().list();
                List<UserMatchBean> list = getMatchList(recordList);
                e.onNext(list);
            }
        });
    }

    public List<UserMatchBean> getMatchList(List<Record> recordList) {
        List<UserMatchBean> list = new ArrayList<>();

        Map<Long, UserMatchBean> idBeanMap = new HashMap<>();

        // 查询出所有记录，按照matchId进行分类
        for (int i = 0; i < recordList.size(); i ++) {
            // 获取/生成UserMatchBean
            UserMatchBean bean = idBeanMap.get(recordList.get(i).getMatchNameId());
            if (bean == null) {
                bean = new UserMatchBean();
                idBeanMap.put(recordList.get(i).getMatchNameId(), bean);
                if (bean.getRecordList() == null) {
                    bean.setRecordList(new ArrayList<Record>());
                }
                list.add(bean);
            }
            bean.getRecordList().add(recordList.get(i));
            // 以最新的赛事名称为主，所以永远记录最新的
            bean.setNameBean(recordList.get(i).getMatch());
        }

        // 统计胜负场
        for (UserMatchBean bean:list) {
            calculateMatch(bean);
        }

        // 按照week进行排序
        Collections.sort(list, new WeekComparator());

        return list;
    }

    /**
     * 统计总胜负和最佳成绩
     * @param bean
     */
    private void calculateMatch(UserMatchBean bean) {
        if (bean.getRecordList() != null) {
            int win = 0, lose = 0;
            String[] roundArray = AppConstants.RECORD_MATCH_ROUNDS;
            String best = null;
            StringBuffer bestYears = null;
            for (int i = 0; i < bean.getRecordList().size(); i ++) {
                Record record = bean.getRecordList().get(i);

                if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                    win ++;
                    //Final winner is champion，肯定是最佳
                    if (roundArray[0].equals(record.getRound())) {
                        if (!strChampion.equals(best)) {
                            best = strChampion;
                            bestYears = new StringBuffer(record.getDateStr().split("-")[0]);
                        }
                        else {
                            bestYears.append(", ").append(record.getDateStr().split("-")[0]);
                        }
                    }
                }
                else {
                    lose ++;

                    //在某一轮输了，可能是最佳
                    String round = record.getRound();
                    if (round.equals(roundArray[0])) {//Final
                        round = strRunnerup;
                    }
                    int result = compareMatchBest(roundArray, round, best);
                    if (result > 0) {
                        best = round;
                        bestYears = new StringBuffer(record.getDateStr().split("-")[0]);
                    }
                    else if (result == 0) {
                        bestYears.append(", ").append(record.getDateStr().split("-")[0]);
                    }
                }
            }
            bean.setWin(win);
            bean.setLose(lose);

            if (best == null) {//戴维斯杯
                bean.setBest(roundArray[roundArray.length - 1]);//group
                bean.setBestYears("");
            }
            else {
                bean.setBest(best);
                bean.setBestYears(bestYears.toString());
            }
        }
    }

    /**
     * 按照比赛轮次，比较更好的成绩
     * @param roundArray 轮次数组Final/Semi Final...
     * @param target 当前轮次
     * @param best 累计统计中暂时最好的轮次
     * @return 大于0则target为当前最好的轮次，等于0则并列为当前最好轮次
     */
    private int compareMatchBest(String[] roundArray, String target, String best) {
        if (best == null) {
            return 1;
        }
        else {
            return getRoundLevel(roundArray, target) - getRoundLevel(roundArray, best);
        }
    }

    private int getRoundLevel(String[] roundArray, String round) {
        int level = 0;
        int max = roundArray.length;
        //Final区分冠亚军
        if (round.equals(strRunnerup)) {
            level = max - 1;
        }
        else if (round.equals(strChampion)) {
            level = max;
        }

        for (int i = 0; i < roundArray.length - 1; i ++) {//Final不计算
            if (round.equals(roundArray[i])) {
                level = max - 1 - i;
            }
        }
        return level;
    }

    /**
     * 找到与当前周数最靠近的赛事，前后出现等间隔的以前者优先（跨周数赛事）
     * matchList 已经是按周数升序排列了
     * @return
     */
    public int findLatestWeekItem() {
        return new MatchModel().findLatestWeekItem(matchList);
    }

    public UserMatchBean getUserMatchBean(int position) {
        return matchList.get(position);
    }

    private class WeekComparator implements Comparator<UserMatchBean> {

        @Override
        public int compare(UserMatchBean beanL, UserMatchBean beanR) {
            return beanL.getNameBean().getMatchBean().getWeek() - beanR.getNameBean().getMatchBean().getWeek();
        }
    }
}
