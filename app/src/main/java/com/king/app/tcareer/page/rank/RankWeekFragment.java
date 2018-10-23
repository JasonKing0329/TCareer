package com.king.app.tcareer.page.rank;

import android.os.Bundle;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpFragment;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.bean.LineChartData;
import com.king.app.tcareer.view.widget.chart.LineChart;
import com.king.app.tcareer.view.widget.chart.adapter.IAxis;
import com.king.app.tcareer.view.widget.chart.adapter.LineChartAdapter;
import com.king.app.tcareer.view.widget.chart.adapter.LineData;

import butterknife.BindView;

/**
 * 描述: 替换RankDetailFragment，采用自定义LineChart，支持退役期间的数据及第二段职业生涯
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 9:52
 */
public class RankWeekFragment extends BaseMvpFragment<RankWeekPresenter> implements RankWeekView {

    private static final String KEY_USER_ID = "user_id";

    @BindView(R.id.chart_week)
    LineChart chartWeek;

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
    protected void onCreate(View view) {
        chartWeek.setOnClickListener(onChartClickListener);
    }

    @Override
    protected RankWeekPresenter createPresenter() {
        return new RankWeekPresenter();
    }

    public void setOnChartClickListener(View.OnClickListener onChartClickListener) {
        this.onChartClickListener = onChartClickListener;
    }

    @Override
    protected void onCreateData() {
        long userId = getArguments().getLong(KEY_USER_ID);
        presenter.loadRanks(userId, false);
    }

    @Override
    public void postShowUser(String nameEng) {

    }

    public void refresh() {
        presenter.loadRanks(presenter.getUser().getId(), false);
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
                return data.getAxisXWeightList().get(position);
            }

            @Override
            public String getTextAt(int position) {
                return data.getAxisXTextList().get(position);
            }

            @Override
            public boolean isNotDraw(int position) {
                return data.getAxisXIsNotDrawList().get(position);
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
                return data.getAxisYWeightList().get(position);
            }

            @Override
            public String getTextAt(int position) {
                return data.getAxisYTextList().get(position);
            }

            @Override
            public boolean isNotDraw(int position) {
                return data.getAxisYIsNotDrawList().get(position);
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
    }
}
