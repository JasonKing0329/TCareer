package com.king.app.tcareer.page;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.view.widget.chart.BarChart;
import com.king.app.tcareer.view.widget.chart.LineChart;
import com.king.app.tcareer.view.widget.chart.adapter.BarChartAdapter;
import com.king.app.tcareer.view.widget.chart.adapter.IAxis;
import com.king.app.tcareer.view.widget.chart.adapter.LineChartAdapter;
import com.king.app.tcareer.view.widget.chart.adapter.LineData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.bar_chart)
    BarChart barChart;
    @BindView(R.id.line_chart)
    LineChart lineChart;

    Integer[] ranks = {0, 600, 95, 12, 7, 3, 1, 1};
    int[] colorBars = {
            Color.rgb(0x33, 0x99, 0xff), Color.rgb(0, 0xa5, 0xc4)
    };
    int[] DEGREE_POINT_BAR = {9999, 30, 0};
    int DEGREE_AREA = 10;
    int[] DEGREE_POINT_LINE = {9999, 1000, 500, 200, 100, 50, 30, 10, 0};

    private List<RankWeek> list;

    private List<RankWeek> listH;

    private List<LineData> lineData;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        initLineChart();
//        initBarChart();
    }

    private void initLineChart() {

        TApplication.getInstance().createGreenDao();
        list = TApplication.getInstance().getDaoSession().getRankWeekDao().queryBuilder()
                .where(RankWeekDao.Properties.UserId.eq(AppConstants.USER_ID_QI))
                .orderAsc(RankWeekDao.Properties.Date)
                .build().list();
        listH = TApplication.getInstance().getDaoSession().getRankWeekDao().queryBuilder()
                .where(RankWeekDao.Properties.UserId.eq(AppConstants.USER_ID_HENRY))
                .orderAsc(RankWeekDao.Properties.Date)
                .build().list();
        createLineData();
        final int totalCount = getTotalLineXCount();

        lineChart.setVisibility(View.VISIBLE);
        lineChart.setDegreeCombine(8);
        lineChart.setDrawAxisY(true);
        lineChart.setDrawDashGrid(false);

        lineChart.setAxisX(new IAxis() {
            @Override
            public int getDegreeCount() {
                return totalCount;
            }

            @Override
            public int getTotalWeight() {
                return totalCount;
            }

            @Override
            public int getWeightAt(int position) {
                return position;
            }

            @Override
            public String getTextAt(int position) {
                if (position < list.size()) {
                    return dateFormat.format(list.get(position).getDate());
                }
                if (position < listH.size()) {
                    return dateFormat.format(listH.get(position).getDate());
                }
                return "";
            }

            @Override
            public boolean isNotDraw(int position) {
                return false;
            }
        });
        lineChart.setAxisY(new IAxis() {
            @Override
            public int getDegreeCount() {
                return DEGREE_AREA * (DEGREE_POINT_LINE.length - 1) + 1;
            }

            @Override
            public int getTotalWeight() {
                return DEGREE_AREA * (DEGREE_POINT_LINE.length - 1) + 1;
            }

            @Override
            public int getWeightAt(int position) {
                return position;
            }

            @Override
            public String getTextAt(int position) {
                int rank = positionToRankLine(position);
                return "" + rank;
            }

            @Override
            public boolean isNotDraw(int position) {
                int rank = positionToRankLine(position);
                return !isKeyDegree(rank);
            }
        });
        lineChart.setAdapter(new LineChartAdapter() {
            @Override
            public int getLineCount() {
                return lineData.size();
            }

            @Override
            public LineData getLineData(int lineIndex) {
                return lineData.get(lineIndex);
            }
        });
        lineChart.scrollToEnd();
    }

    public int getTotalLineXCount() {
        int fla = 0;
        int hen = 0;
        int count = 0;
        while (fla < list.size() || hen < listH.size()) {
            int flaBefore = fla;
            int henBefore = hen;
            long timeFla = list.get(fla).getDate().getTime();
            long timeHen = listH.get(hen).getDate().getTime();
            if (timeFla > timeHen) {
                hen ++;
            }
            else if (timeFla < timeHen) {
                fla ++;
            }
            else {
                fla ++;
                hen ++;
            }
            if (flaBefore == 0 && fla == 1) {
                lineData.get(0).setStartX(count);
            }
            if (henBefore == 0 && hen == 1) {
                lineData.get(1).setStartX(count);
            }
            count ++;
        }
        lineData.get(0).setEndX(lineData.get(0).getStartX() + lineData.get(0).getValues().size() - 1);
        lineData.get(1).setEndX(lineData.get(1).getStartX() + lineData.get(1).getValues().size() - 1);
        return count;
    }

    private void createLineData() {
        lineData = new ArrayList<>();
        for (int n = 0; n < 2; n ++) {
            List<RankWeek> list = n == 0 ? this.list:listH;
            LineData data = new LineData();
            data.setColor(colorBars[n]);
            data.setStartX(0);
            data.setEndX(list.size() - 1);
            data.setValues(new ArrayList<>());
            data.setValuesText(new ArrayList<>());
            for (int i = 0; i < list.size(); i ++) {
                data.getValues().add(rankToDegreeLine(list.get(i).getRank()));
                if (i > 0) {
                    // 出现名次变化才显示
                    if (data.getValues().get(i) != data.getValues().get(i - 1)) {
                        data.getValuesText().add(String.valueOf(list.get(i).getRank()));
                    }
                    else {
                        data.getValuesText().add(null);
                    }
                }
                else {
                    data.getValuesText().add(String.valueOf(list.get(i).getRank()));
                }
            }
            lineData.add(data);
        }
    }

    private boolean isKeyDegree(int position) {
        for (int i = 0; i < DEGREE_POINT_LINE.length; i ++) {
            if (position == DEGREE_POINT_LINE[i]) {
                return true;
            }
        }
        return false;
    }

    private void initBarChart() {
        barChart.setVisibility(View.VISIBLE);
        barChart.setDrawAxisY(true);
        barChart.setDrawValueText(true);
        barChart.setDrawDashGrid(true);
        barChart.setAxisX(new IAxis() {
            @Override
            public int getDegreeCount() {
                return ranks.length;
            }

            @Override
            public int getTotalWeight() {
                return ranks.length;
            }

            @Override
            public int getWeightAt(int position) {
                return position;
            }

            @Override
            public String getTextAt(int position) {
                return String.valueOf(2016 + position);
            }

            @Override
            public boolean isNotDraw(int position) {
                return false;
            }
        });
        barChart.setAxisY(new IAxis() {
            @Override
            public int getDegreeCount() {
                return DEGREE_AREA * (DEGREE_POINT_BAR.length - 1) + 1;
            }

            @Override
            public int getTotalWeight() {
                return DEGREE_AREA * (DEGREE_POINT_BAR.length - 1) + 1;
            }

            /**
             * 刻度高度均匀分配
             * @param position
             * @return
             */
            @Override
            public int getWeightAt(int position) {
                return position;
            }

            @Override
            public String getTextAt(int position) {
                int rank = positionToRankBar(position);
                return String.valueOf(rank);
            }

            @Override
            public boolean isNotDraw(int position) {
                return false;
            }
        });
        barChart.setAdapter(new BarChartAdapter() {
            @Override
            public int getXCount() {
                return ranks.length;
            }

            @Override
            public int getBarColor(int position) {
                return colorBars[position % 2];
            }

            @Override
            public Integer getValueWeight(int xIndex) {
                return rankToDegree(ranks[xIndex]);
            }

            @Override
            public String getValueText(int xIndex) {
                return String.valueOf(ranks[xIndex]);
            }
        });
    }

    /**
     * y 刻度对应的rank
     * @param position
     * @return
     */
    private int positionToRankBar(int position) {
        int max = DEGREE_POINT_BAR[position / DEGREE_AREA];
        int min = (position / DEGREE_AREA + 1 == DEGREE_POINT_BAR.length) ?
                max: DEGREE_POINT_BAR[position / DEGREE_AREA + 1];
        int rank = max - (max - min) / DEGREE_AREA * (position % DEGREE_AREA);
        return rank;
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
    private int rankToDegree(int rank) {
        if (rank == 0 || rank > 9999) {
            rank = 9999;
        }

        int degree = 0;
        for (int i = 0; i < DEGREE_POINT_BAR.length; i ++) {
            if (i < DEGREE_POINT_BAR.length - 1) {
                if (rank <= DEGREE_POINT_BAR[i] && rank > DEGREE_POINT_BAR[i + 1]) {
                    int max = DEGREE_POINT_BAR[i];
                    int min = DEGREE_POINT_BAR[i + 1];
                    int piece = (max - min) / DEGREE_AREA;
                    degree = i * DEGREE_AREA + (max - rank) / piece;
                    break;
                }
            }
        }
        return degree;
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
