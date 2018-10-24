package com.king.app.tcareer.page.record.complex;

import android.graphics.PorterDuff;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.LineChartData;
import com.king.app.tcareer.view.widget.chart.LineChart;
import com.king.app.tcareer.view.widget.chart.adapter.IAxis;
import com.king.app.tcareer.view.widget.chart.adapter.LineChartAdapter;
import com.king.app.tcareer.view.widget.chart.adapter.LineData;

import java.util.List;

import butterknife.BindView;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/10/23 15:12
 */
public class CareerCompareActivity extends BaseMvpActivity<CareerComparePresenter> implements CareerCompareView {

    @BindView(R.id.chart_week)
    LineChart chartWeek;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ctl_toolbar)
    CollapsingToolbarLayout ctlToolbar;
    @BindView(R.id.rv_data)
    RecyclerView rvData;
    @BindView(R.id.tv_title_king)
    TextView tvTitleKing;
    @BindView(R.id.tv_title_fla)
    TextView tvTitleFla;
    @BindView(R.id.tv_title_hen)
    TextView tvTitleHen;
    @BindView(R.id.tv_title_qi)
    TextView tvTitleQi;

    private CareerCompareAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_career_compare;
    }

    @Override
    protected void initView() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_filterrable);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.dark_grey), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTitleKing.setTextColor(AppConstants.USER_KING_LINE_COLOR);
        tvTitleFla.setTextColor(AppConstants.USER_FLAMENCO_LINE_COLOR);
        tvTitleHen.setTextColor(AppConstants.USER_HENRY_LINE_COLOR);
        tvTitleQi.setTextColor(AppConstants.USER_QI_LINE_COLOR);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvData.setLayoutManager(manager);
    }

    @Override
    protected CareerComparePresenter createPresenter() {
        return new CareerComparePresenter();
    }

    @Override
    protected void initData() {
        presenter.loadData();
        presenter.loadRankCompares();
    }

    @Override
    public void showData(List<CompareItem> list) {
        adapter = new CareerCompareAdapter();
        adapter.setList(list);
        rvData.setAdapter(adapter);
    }

    @Override
    public void showChart(LineChartData data) {
        chartWeek.setDegreeCombine(8);
        chartWeek.setDrawAxisY(true);
        chartWeek.setAxisX(new IAxis() {
            @Override
            public int getDegreeCount() {
                return data.getAxisXCount();
            }

            @Override
            public int getTotalWeight() {
                return data.getAxisXTotalWeight();
            }

            @Override
            public int getWeightAt(int position) {
                return data.getAxisXDegreeList().get(position).getWeight();
            }

            @Override
            public String getTextAt(int position) {
                return data.getAxisXDegreeList().get(position).getText();
            }

            @Override
            public boolean isNotDraw(int position) {
                return data.getAxisXDegreeList().get(position).isNotDraw();
            }
        });
        chartWeek.setAxisY(new IAxis() {
            @Override
            public int getDegreeCount() {
                return data.getAxisYCount();
            }

            @Override
            public int getTotalWeight() {
                return data.getAxisYTotalWeight();
            }

            @Override
            public int getWeightAt(int position) {
                return data.getAxisYDegreeList().get(position).getWeight();
            }

            @Override
            public String getTextAt(int position) {
                return data.getAxisYDegreeList().get(position).getText();
            }

            @Override
            public boolean isNotDraw(int position) {
                return data.getAxisYDegreeList().get(position).isNotDraw();
            }
        });
        chartWeek.setAdapter(new LineChartAdapter() {
            @Override
            public int getLineCount() {
                return data.getLineList().size();
            }

            @Override
            public LineData getLineData(int lineIndex) {
                return data.getLineList().get(lineIndex);
            }
        });
        chartWeek.scrollToEnd();
    }

}
