package com.king.app.tcareer.page.rank;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.MvvmFragment;
import com.king.app.tcareer.databinding.FragmentRankWeekBinding;
import com.king.app.tcareer.model.bean.LineChartData;
import com.king.app.tcareer.view.widget.chart.adapter.IAxis;
import com.king.app.tcareer.view.widget.chart.adapter.LineChartAdapter;
import com.king.app.tcareer.view.widget.chart.adapter.LineData;

/**
 * 描述: 替换RankDetailFragment，采用自定义LineChart，支持退役期间的数据及第二段职业生涯
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 9:52
 */
public class RankWeekFragment extends MvvmFragment<FragmentRankWeekBinding, RankWeekViewModel> {

    private static final String KEY_USER_ID = "user_id";

    private View.OnClickListener onChartClickListener;

    public static RankWeekFragment newInstance(long userId) {
        RankWeekFragment fragment = new RankWeekFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_rank_week;
    }

    @Override
    protected RankWeekViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RankWeekViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        mBinding.chartWeek.setOnClickListener(onChartClickListener);
    }

    public void setOnChartClickListener(View.OnClickListener onChartClickListener) {
        this.onChartClickListener = onChartClickListener;
    }

    @Override
    protected void onCreateData() {
        long userId = getArguments().getLong(KEY_USER_ID);
        mModel.chartObserver.observe(this, data -> showChart(data));
        mModel.loadRanks(userId);
    }

    public void refresh() {
        mModel.loadRanks(mModel.getUser().getId());
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
                return data.getLineList() == null ? 0:data.getLineList().size();
            }

            @Override
            public LineData getLineData(int lineIndex) {
                return data.getLineList().get(lineIndex);
            }
        });
        mBinding.chartWeek.scrollToEnd();
    }
}
