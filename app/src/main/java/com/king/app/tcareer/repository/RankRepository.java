package com.king.app.tcareer.repository;

import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.ChartModel;
import com.king.app.tcareer.model.bean.AxisDegree;
import com.king.app.tcareer.model.bean.LineChartData;
import com.king.app.tcareer.view.widget.chart.adapter.LineData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import io.reactivex.Observable;
import io.reactivex.functions.Function4;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/10/23 15:25
 */
public class RankRepository {

    private ChartModel chartModel;

    public RankRepository() {
        chartModel = new ChartModel();
    }

    public Observable<LineChartData> loadUserWeekRankChart(long userId) {
        return chartModel.loadUserWeekRankChart(userId);
    }

    public Observable<LineChartData> compareUserWeekRankChart() {
        return Observable.zip(loadUserWeekRankChart(AppConstants.USER_ID_KING)
                , loadUserWeekRankChart(AppConstants.USER_ID_FLAMENCO)
                , loadUserWeekRankChart(AppConstants.USER_ID_HENRY)
                , loadUserWeekRankChart(AppConstants.USER_ID_QI)
                , combineLineData());
    }

    private Function4<LineChartData, LineChartData, LineChartData, LineChartData, LineChartData> combineLineData() {
        return (data1, data2, data3, data4) -> {

            LineChartData result = new LineChartData();
            // y轴都一样
            result.setAxisYCount(data1.getAxisYCount());
            result.setAxisYTotalWeight(data1.getAxisYTotalWeight());
            result.setAxisYDegreeList(data1.getAxisYDegreeList());

            // 合并x轴
            result.setAxisXDegreeList(new ArrayList<>());
            result.setLineList(new ArrayList<>());
            AxisDegree<Date> start1 = data1.getAxisXDegreeList().get(0);
            AxisDegree<Date> start2 = data2.getAxisXDegreeList().get(0);
            AxisDegree<Date> start3 = data3.getAxisXDegreeList().get(0);
            AxisDegree<Date> start4 = data4.getAxisXDegreeList().get(0);
            AxisDegree<Date> end1 = data1.getAxisXDegreeList().get(data1.getAxisXDegreeList().size() - 1);
            AxisDegree<Date> end2 = data2.getAxisXDegreeList().get(data2.getAxisXDegreeList().size() - 1);
            AxisDegree<Date> end3 = data3.getAxisXDegreeList().get(data3.getAxisXDegreeList().size() - 1);
            AxisDegree<Date> end4 = data4.getAxisXDegreeList().get(data4.getAxisXDegreeList().size() - 1);
            AxisDegree<Date> start = minDegree(start1, minDegree(start2, minDegree(start3, start4)));
            AxisDegree<Date> end = maxDegree(end1, maxDegree(end2, maxDegree(end3, end4)));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(start.getData());
            AxisDegree<Date> degree = start;
            result.getAxisXDegreeList().add(degree);
            int index = 0;
            boolean fixed1 = false;
            boolean fixed2 = false;
            boolean fixed3 = false;
            boolean fixed4 = false;
            while (degree.getData().getTime() <= end.getData().getTime()) {
                // 将data1~data4代表的走势填补到合并走势中，修正最终的x坐标
                if (!fixed1) {
                    fixed1 = fixData(calendar.getTime(), index, data1, AppConstants.USER_KING_LINE_COLOR, result);
                }
                if (!fixed2) {
                    fixed2 = fixData(calendar.getTime(), index, data2, AppConstants.USER_FLAMENCO_LINE_COLOR, result);
                }
                if (!fixed3) {
                    fixed3 = fixData(calendar.getTime(), index, data3, AppConstants.USER_HENRY_LINE_COLOR, result);
                }
                if (!fixed4) {
                    fixed4 = fixData(calendar.getTime(), index, data4, AppConstants.USER_QI_LINE_COLOR, result);
                }

                degree = new AxisDegree<>();
                degree.setData(calendar.getTime());
                degree.setText(sdf.format(calendar.getTime()));
                degree.setNotDraw(false);
                degree.setWeight(index);
                result.getAxisXDegreeList().add(degree);

                index ++;
                calendar.add(GregorianCalendar.DAY_OF_YEAR, 7);
            }
            result.setAxisXCount(index);
            result.setAxisXTotalWeight(index);
            
            return result;
        };
    }

    /**
     * 修正x坐标
     * @param time 当前坐标对应的时间
     * @param index 相对合并坐标原点的偏移量
     * @param data 自身参考系的chart数据
     * @param lineColor
     *@param result 合并后的chart数据  @return
     */
    private boolean fixData(Date time, int index, LineChartData data, int lineColor, LineChartData result) {
        // 寻找起始点
        if (time.getTime() == data.getAxisXDegreeList().get(0).getData().getTime()) {
            for (LineData line:data.getLineList()) {
                line.setStartX(line.getStartX() + index);
                line.setEndX(line.getEndX() + index);
                line.setColor(lineColor);
                result.getLineList().add(line);
            }
            return true;
        }
        return false;
    }

    private AxisDegree<Date> maxDegree(AxisDegree<Date> degree1, AxisDegree<Date> degree2) {
        if (degree1.getData().getTime() < degree2.getData().getTime()) {
            return degree2;
        }
        else {
            return degree1;
        }
    }

    private AxisDegree<Date> minDegree(AxisDegree<Date> degree1, AxisDegree<Date> degree2) {
        if (degree1.getData().getTime() > degree2.getData().getTime()) {
            return degree2;
        }
        else {
            return degree1;
        }
    }
}
