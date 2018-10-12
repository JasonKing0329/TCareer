package com.king.app.tcareer.page;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.king.app.tcareer.R;
import com.king.app.tcareer.view.widget.chart.BarChart;
import com.king.app.tcareer.view.widget.chart.adapter.BarChartAdapter;
import com.king.app.tcareer.view.widget.chart.adapter.IAxis;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.bar_chart)
    BarChart barChart;

    int[] ranks = {0, 600, 95, 12, 7, 3, 1, 1};
    int[] colorBars = {
            Color.rgb(0x33, 0x99, 0xff), Color.rgb(0, 0xa5, 0xc4)
    };
    int[] DEGREE_POINT = {9999, 30, 0};
    int DEGREE_AREA = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        barChart.setDrawAxisY(true);
        barChart.setDrawValueText(true);
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
        });
        barChart.setAxisY(new IAxis() {
            @Override
            public int getDegreeCount() {
                return DEGREE_AREA * (DEGREE_POINT.length - 1) + 1;
            }

            @Override
            public int getTotalWeight() {
                return DEGREE_AREA * (DEGREE_POINT.length - 1) + 1;
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
                int rank = positionToRank(position);
                return String.valueOf(rank);
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
    private int positionToRank(int position) {
        int max = DEGREE_POINT[position / DEGREE_AREA];
        int min = (position / DEGREE_AREA + 1 == DEGREE_POINT.length) ?
                max:DEGREE_POINT[position / DEGREE_AREA + 1];
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
        for (int i = 0; i < DEGREE_POINT.length; i ++) {
            if (i < DEGREE_POINT.length - 1) {
                if (rank <= DEGREE_POINT[i] && rank > DEGREE_POINT[i + 1]) {
                    int max = DEGREE_POINT[i];
                    int min = DEGREE_POINT[i + 1];
                    int piece = (max - min) / DEGREE_AREA;
                    degree = i * DEGREE_AREA + (max - rank) / piece;
                    break;
                }
            }
        }
        return degree;
    }
}
