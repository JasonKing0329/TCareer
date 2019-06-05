package com.king.app.tcareer.repository;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.page.match.gallery.UserMatchBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/5 16:57
 */
public class MatchRepository {

    /**
     * 找到与当前周数最靠近的赛事，前后出现等间隔的以前者优先（跨周数赛事）
     * matchList 已经是按周数升序排列了
     * @return
     */
    public int findLatestWeekItem(List<UserMatchBean> matchList) {

        int position = 0;
        // 最小间隔
        int minSpace = Integer.MAX_VALUE;

        // 当前周
        int curWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        for (int i = 0; i < matchList.size(); i ++) {
            int week = matchList.get(i).getNameBean().getMatchBean().getWeek();
            int space = Math.abs(week - curWeek);
            if (space < minSpace) {
                minSpace = space;
                position = i;
            }
            else if (space == minSpace) {
                // 等于的情况以前者优先
                continue;
            }
            // 由于已经是按周数的升序排列了，所以当出现大于等于最小间隔的情况时，可以终止遍历
            else {
                break;
            }
        }
        return position;
    }

    public Observable<List<UserMatchBean>> queryUserMatches(long userId) {
        return Observable.create(e -> {
            RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
            List<Record> recordList = recordDao.queryBuilder()
                    .where(RecordDao.Properties.UserId.eq(userId))
                    .build().list();
            List<UserMatchBean> list = getMatchList(recordList);
            e.onNext(list);
        });
    }

    private List<UserMatchBean> getMatchList(List<Record> recordList) {
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
                    bean.setRecordList(new ArrayList<>());
                }
                list.add(bean);
            }
            bean.getRecordList().add(recordList.get(i));
            // 以最新的赛事名称为主，所以永远记录最新的
            bean.setNameBean(recordList.get(i).getMatch());
            bean.setImageUrl(ImageProvider.getMatchHeadPath(bean.getNameBean().getName(), bean.getNameBean().getMatchBean().getCourt()));
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
                        if (!AppConstants.CHAMPOION.equals(best)) {
                            best = AppConstants.CHAMPOION;
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
                        round = AppConstants.RUNNERUP;
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
        if (round.equals(AppConstants.RUNNERUP)) {
            level = max - 1;
        }
        else if (round.equals(AppConstants.CHAMPOION)) {
            level = max;
        }

        for (int i = 0; i < roundArray.length - 1; i ++) {//Final不计算
            if (round.equals(roundArray[i])) {
                level = max - 1 - i;
            }
        }
        return level;
    }

    private class WeekComparator implements Comparator<UserMatchBean> {

        @Override
        public int compare(UserMatchBean beanL, UserMatchBean beanR) {
            return beanL.getNameBean().getMatchBean().getWeek() - beanR.getNameBean().getMatchBean().getWeek();
        }
    }
}
