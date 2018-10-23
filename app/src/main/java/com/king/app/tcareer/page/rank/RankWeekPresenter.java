package com.king.app.tcareer.page.rank;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.bean.LineChartData;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.model.db.entity.Retire;
import com.king.app.tcareer.model.db.entity.RetireDao;
import com.king.app.tcareer.view.widget.chart.adapter.LineData;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
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
 * <p/>创建时间: 2018/3/8 14:03
 */
public class RankWeekPresenter extends BasePresenter<RankWeekView> {

    int DEGREE_AREA = 10;
    int[] DEGREE_POINT_LINE = {9999, 1000, 500, 200, 100, 50, 30, 10, 0};
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate() {

    }

    public void loadRanks(final long userId, final boolean desc) {
        queryUser(userId)
                .flatMap(user -> {
                    view.postShowUser(user.getNameEng());
                    return queryWeekRank(userId, desc);
                })
                .flatMap(list -> toChartData(list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<LineChartData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(LineChartData data) {
                        view.showChart(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showMessage("Load error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * query rank of week
     * @param userId
     * @return
     */
    private Observable<List<RankWeek>> queryWeekRank(final long userId, final boolean desc) {
        return Observable.create(e -> {
            RankWeekDao dao = TApplication.getInstance().getDaoSession().getRankWeekDao();
            QueryBuilder<RankWeek> builder = dao.queryBuilder()
                    .where(RankWeekDao.Properties.UserId.eq(userId));
            if (desc) {
                builder.orderDesc(RankWeekDao.Properties.Date);
            }
            else {
                builder.orderAsc(RankWeekDao.Properties.Date);
            }
            List<RankWeek> list = builder.build().list();
            e.onNext(list);
        });
    }

    private ObservableSource<LineChartData> toChartData(List<RankWeek> rankList) {
        return observer -> {
            LineChartData chartData = new LineChartData();
            // axis y data
            chartData.setAxisYCount(DEGREE_AREA * (DEGREE_POINT_LINE.length - 1) + 1);
            chartData.setAxisYTotalWeight(chartData.getAxisYCount());
            chartData.setAxisYWeightList(new ArrayList<>());
            chartData.setAxisYIsNotDrawList(new ArrayList<>());
            chartData.setAxisYTextList(new ArrayList<>());
            for (int i = 0; i < chartData.getAxisYCount(); i ++)  {
                chartData.getAxisYWeightList().add(i);
                // 转换y轴数据坐标
                int rank = positionToRankLine(i);
                chartData.getAxisYIsNotDrawList().add(!isKeyDegree(rank));
                chartData.getAxisYTextList().add(String.valueOf(rank));
            }

            if (rankList.size() > 0) {
                // axis x data and line data
                chartData.setAxisXWeightList(new ArrayList<>());
                chartData.setAxisXIsNotDrawList(new ArrayList<>());
                chartData.setAxisXTextList(new ArrayList<>());
                chartData.setLineList(new ArrayList<>());

                LineData data = new LineData();
                chartData.getLineList().add(data);
                data.setStartX(0);
                data.setEndX(rankList.size() - 1);
                data.setValues(new ArrayList<>());
                data.setValuesText(new ArrayList<>());

                int realCount = rankList.size();
                Retire relieve = queryRelieve();
                for (int i = 0; i < rankList.size(); i ++) {
                    RankWeek week = rankList.get(i);
                    // 属于第二段职业生涯
                    if (isSecondCareer(week.getDate(), relieve)) {
                        // 修改第一段结尾
                        data.setEndX(i - 1);
                        // 构建退役期间时间数据
                        int count = buildRetireDate(relieve, i, chartData);
                        // x轴的总量加上退役期间的时间数据
                        realCount += count;
                        // 结束第一段Line data，构建第二段Line Data
                        buildSecondCareer(rankList, i, count, chartData);
                        break;
                    }
                    // 构建每一个刻度(星期)对应的value
                    buildWeek(data, rankList, i, 0, chartData);
                }
                // 设置x轴的总刻度
                chartData.setAxisXCount(realCount);
                chartData.setAxisXTotalWeight(realCount);
            }

            observer.onNext(chartData);
        };
    }

    /**
     * 构建退役期间的x坐标轴
     * @param relieve
     * @param startIndex
     * @param chartData
     * @return
     */
    private int buildRetireDate(Retire relieve, int startIndex, LineChartData chartData) {
        Date start = relieve.getRelieveRetire().getEffectDate();
        Date end = relieve.getEffectDate();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(start);
        int count = 0;
        for (Date date = start;
             date.getTime() < end.getTime();// end代表当周恢复rank数据
             calendar.add(GregorianCalendar.DAY_OF_YEAR, 7)) {
            chartData.getAxisXIsNotDrawList().add(false);
            chartData.getAxisXWeightList().add(startIndex ++);
            chartData.getAxisXTextList().add(dateFormat.format(date));
            count ++;
            date = calendar.getTime();
        }
        return count;
    }

    /**
     * 构建第二段职业生涯数据
     * @param rankList
     * @param indexOfRank
     * @param offsetToChart
     * @param chartData
     */
    private void buildSecondCareer(List<RankWeek> rankList, int indexOfRank, int offsetToChart, LineChartData chartData) {
        LineData data = new LineData();
        chartData.getLineList().add(data);
        data.setStartX(indexOfRank + offsetToChart);
        data.setEndX(data.getStartX() + rankList.size() - 1 - indexOfRank);
        data.setValues(new ArrayList<>());
        data.setValuesText(new ArrayList<>());
        for (int i = indexOfRank; i < rankList.size(); i ++) {
            buildWeek(data, rankList, i, offsetToChart, chartData);
        }
    }

    /**
     * 构建每一周rank映射于坐标轴的degree, value
     * @param data 所属的LineData（LineChart描述一组连续连线的数据）
     * @param rankList week rank
     * @param indexOfRank index in rankList
     * @param offset x坐标轴的位置为 indexOfRank + offset
     * @param chartData chart data
     */
    private void buildWeek(LineData data, List<RankWeek> rankList, int indexOfRank, int offset, LineChartData chartData) {
        RankWeek week = rankList.get(indexOfRank);
        int degreeValue = rankToDegreeLine(week.getRank());
        data.getValues().add(degreeValue);
        int size = data.getValues().size();
        // 名次变化才显示value
        if (size > 1) {
            // 出现名次变化才显示
            if (degreeValue != data.getValues().get(size - 2)) {
                data.getValuesText().add(String.valueOf(week.getRank()));
            }
            else {
                data.getValuesText().add(null);
            }
        }
        else {
            data.getValuesText().add(String.valueOf(week.getRank()));
        }
        chartData.getAxisXWeightList().add(indexOfRank + offset);
        chartData.getAxisXIsNotDrawList().add(false);
        chartData.getAxisXTextList().add(dateFormat.format(week.getDate()));
    }

    /**
     * 只支持一次退役并付出
     * @return
     */
    private Retire queryRelieve() {
        List<Retire> list = getDaoSession().getRetireDao().queryBuilder()
                .where(RetireDao.Properties.RelieveId.notEq(0))
                .build().list();
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 判断是否属于第二段职业生涯
     * @param date
     * @param relieve
     * @return
     */
    private boolean isSecondCareer(Date date, Retire relieve) {
        if (relieve != null) {
            if (date.getTime() >= relieve.getEffectDate().getTime()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 需要刻画的y轴刻度
     * @param rank
     * @return
     */
    private boolean isKeyDegree(int rank) {
        if (rank == 1) {
            return true;
        }
        if (rank == 0) {
            return false;
        }
        for (int i = 0; i < DEGREE_POINT_LINE.length; i ++) {
            if (rank == DEGREE_POINT_LINE[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * y 刻度对应的rank
     * @param position
     * @return
     */
    private int positionToRankLine(int position) {
        int max = DEGREE_POINT_LINE[position / DEGREE_AREA];
        int min = (position / DEGREE_AREA + 1 == DEGREE_POINT_LINE.length) ?
                max: DEGREE_POINT_LINE[position / DEGREE_AREA + 1];
        int rank = max - (max - min) / DEGREE_AREA * (position % DEGREE_AREA);
        return rank;
    }

    /**
     * rank对应的y刻度
     * @param rank
     * @return
     */
    private int rankToDegreeLine(int rank) {
        if (rank == 0 || rank > 9999) {
            rank = 9999;
        }

        int degree = 0;
        for (int i = 0; i < DEGREE_POINT_LINE.length; i ++) {
            if (i < DEGREE_POINT_LINE.length - 1) {
                if (rank <= DEGREE_POINT_LINE[i] && rank > DEGREE_POINT_LINE[i + 1]) {
                    int max = DEGREE_POINT_LINE[i];
                    int min = DEGREE_POINT_LINE[i + 1];
                    int piece = (max - min) / DEGREE_AREA;
                    degree = i * DEGREE_AREA + (max - rank) / piece;
                    break;
                }
            }
        }
        return degree;
    }
}
