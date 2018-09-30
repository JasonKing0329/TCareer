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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

//        barChart.setDrawAxisY(false);
        barChart.setAxisX(new IAxis() {
            @Override
            public int getDegreeCount() {
                return 16;
            }

            @Override
            public int getTotalWeight() {
                return 16;
            }

            @Override
            public int getWeightAt(int position) {
                return position;
            }

            @Override
            public String getTextAt(int position) {
                return "pos" + position;
            }
        });
        barChart.setAxisY(new IAxis() {
            @Override
            public int getDegreeCount() {
                return 6;
            }

            @Override
            public int getTotalWeight() {
                return 12;
            }

            @Override
            public int getWeightAt(int position) {
                if (position < 3) {
                    return position + 1;
                }
                else {
                    return position * 2;
                }
            }

            @Override
            public String getTextAt(int position) {
                return "yp" + position;
            }
        });
        barChart.setAdapter(new BarChartAdapter() {
            @Override
            public int getXCount() {
                return 16;
            }

            @Override
            public int getBarColor(int position) {
                return Color.BLACK;
            }

            @Override
            public Integer getValueWeight(int xIndex) {
                return xIndex % 6;
            }

            @Override
            public String getValueText(int xIndex) {
                return "value" + xIndex;
            }
        });
    }
}
