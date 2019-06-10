package com.king.app.tcareer.page.record.complex;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.ActivityCareerCompareBinding;
import com.king.app.tcareer.model.bean.LineChartData;
import com.king.app.tcareer.view.widget.chart.adapter.IAxis;
import com.king.app.tcareer.view.widget.chart.adapter.LineChartAdapter;
import com.king.app.tcareer.view.widget.chart.adapter.LineData;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/10/23 15:12
 */
public class CareerCompareActivity extends MvvmActivity<ActivityCareerCompareBinding, CareerCompareViewModel> {

    private CareerCompareAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_career_compare;
    }

    @Override
    protected CareerCompareViewModel createViewModel() {
        return ViewModelProviders.of(this).get(CareerCompareViewModel.class);
    }

    @Override
    protected void initView() {
        mBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_filterrable);
        mBinding.toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.dark_grey), PorterDuff.Mode.SRC_ATOP);
        mBinding.toolbar.setNavigationOnClickListener(v -> finish());

        mBinding.tvTitleKing.setTextColor(AppConstants.USER_KING_LINE_COLOR);
        mBinding.tvTitleFla.setTextColor(AppConstants.USER_FLAMENCO_LINE_COLOR);
        mBinding.tvTitleHen.setTextColor(AppConstants.USER_HENRY_LINE_COLOR);
        mBinding.tvTitleQi.setTextColor(AppConstants.USER_QI_LINE_COLOR);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvData.setLayoutManager(manager);
    }

    @Override
    protected void initData() {
        mModel.compareItemsObserver.observe(this, list -> showData(list));
        mModel.chartObserver.observe(this, data -> showChart(data));
        mModel.loadData();
        mModel.loadRankCompares();
    }

    private void showData(List<CompareItem> list) {
        adapter = new CareerCompareAdapter();
        adapter.setList(list);
        mBinding.rvData.setAdapter(adapter);
    }

    private void showChart(LineChartData data) {
        mBinding.chartWeek.setDegreeCombine(8);
        mBinding.chartWeek.setDrawAxisY(true);
        mBinding.chartWeek.setAxisX(new IAxis() {
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
        mBinding.chartWeek.setAxisY(new IAxis() {
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
        mBinding.chartWeek.setAdapter(new LineChartAdapter() {
            @Override
            public int getLineCount() {
                return data.getLineList().size();
            }

            @Override
            public LineData getLineData(int lineIndex) {
                return data.getLineList().get(lineIndex);
            }
        });
        mBinding.chartWeek.scrollToEnd();
    }

}
