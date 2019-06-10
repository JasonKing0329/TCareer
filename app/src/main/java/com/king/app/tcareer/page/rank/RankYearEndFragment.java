package com.king.app.tcareer.page.rank;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.MvvmFragment;
import com.king.app.tcareer.databinding.FragmentRankYearEndBinding;
import com.king.app.tcareer.model.db.entity.Rank;
import com.king.app.tcareer.view.widget.chart.adapter.BarChartAdapter;
import com.king.app.tcareer.view.widget.chart.adapter.IAxis;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/5 10:29
 */
public class RankYearEndFragment extends MvvmFragment<FragmentRankYearEndBinding, RankYearViewModel> {

    private static final String KEY_USER_ID = "user_id";

    private View.OnClickListener onChartClickListener;

    private long userId;

    int[] colorBars = {
            Color.rgb(0x33, 0x99, 0xff), Color.rgb(0, 0xa5, 0xc4)
    };
    int[] DEGREE_POINT = {9999, 1000, 300, 100, 30, 10, 0};
    int DEGREE_AREA = 10;

    public static RankYearEndFragment newInstance(long userId) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        RankYearEndFragment fragment = new RankYearEndFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        holder = null;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_rank_year_end;
    }

    @Override
    protected RankYearViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RankYearViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        mBinding.barChartRank.setOnClickListener(onChartClickListener);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onCreateData() {
        userId = getArguments().getLong(KEY_USER_ID);

        mModel.ranksObserver.observe(this, list -> initChart(list));
        mModel.loadYearRanks(userId);
    }

    /**
     * 发生User变化后presenter持有的dao还保留对上个user的数据库访问
     * 需要重新初始化
     */
    public void onUserChanged(long userId) {
        this.userId = userId;
        mModel.loadYearRanks(userId);
    }

    /**
     * 重新加载图标数据（注：访问的数据库不会改变）
     *
     */
    public void refreshRanks() {
        mModel.loadYearRanks(userId);
    }

    private void initChart(List<Rank> rankList) {
        if (rankList == null || rankList.size() == 0) {
            return;
        }

        mBinding.barChartRank.setDrawValueText(true);
        mBinding.barChartRank.setDrawDashGrid(false);
        mBinding.barChartRank.setDrawAxisY(false);
        mBinding.barChartRank.setAxisX(new IAxis() {
            @Override
            public int getDegreeCount() {
                return rankList.size();
            }

            @Override
            public int getTotalWeight() {
                return rankList.size();
            }

            @Override
            public int getWeightAt(int position) {
                return position;
            }

            @Override
            public String getTextAt(int position) {
                return String.valueOf(rankList.get(position).getYear());
            }

            @Override
            public boolean isNotDraw(int position) {
                return false;
            }
        });
        mBinding.barChartRank.setAxisY(new IAxis() {
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

            @Override
            public boolean isNotDraw(int position) {
                return false;
            }
        });
        mBinding.barChartRank.setAdapter(new BarChartAdapter() {
            @Override
            public int getXCount() {
                return rankList.size();
            }

            @Override
            public int getBarColor(int position) {
                return colorBars[position % 2];
            }

            @Override
            public Integer getValueWeight(int xIndex) {
                return rankToDegree(rankList.get(xIndex).getRank());
            }

            @Override
            public String getValueText(int xIndex) {
                return String.valueOf(rankList.get(xIndex).getRank());
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

    /**
     * 设置整个group的点击事件
     *
     * @param onChartClickListener
     */
    public void setOnChartGroupClickListener(View.OnClickListener onChartClickListener) {
        this.onChartClickListener = onChartClickListener;
    }

}
