package com.king.app.tcareer.page.rank;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpFragment;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.utils.ListUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 9:52
 * @deprecated replace with RankWeekFragment
 */
@Deprecated
public class RankDetailFragment extends BaseMvpFragment<RankDetailPresenter> implements RankDetailView {

    private static final String KEY_USER_ID = "user_id";

    @BindView(R.id.chart_week)
    LineChart chartWeek;
    @BindView(R.id.bar_fake)
    View barFake;

    private View.OnClickListener onChartClickListener;

    public static RankDetailFragment newInstance(long userId) {
        RankDetailFragment fragment = new RankDetailFragment();
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
        return R.layout.fragment_rank_detail;
    }

    @Override
    protected void onCreate(View view) {
        if (onChartClickListener != null) {
            // 实测当BarChart有数据时，直接设置barChart有效。但是没有数据时，onClick就不管用了
            // 因此，只能设置在fake view上，这样会拦截BarChart的触摸事件
            // 整个group的点击功能只用于主页，因此目前没有别的影响
//            barChart.setOnClickListener(onChartClickListener);
            barFake.setVisibility(View.VISIBLE);
            barFake.setOnClickListener(onChartClickListener);
        }

        chartWeek.setTouchEnabled(true);
        chartWeek.setScaleEnabled(true);
        chartWeek.setDragEnabled(true);
        chartWeek.setDrawGridBackground(false);
        // 如果不设置为null，会在右下角显示默认的"description label"(Description类的默认标签)
        chartWeek.setDescription(null);

        XAxis xAxis = chartWeek.getXAxis();
        // 设置X轴在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 设置grid line是虚线
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis yAxis = chartWeek.getAxisLeft();
        // 设置grid line是虚线
        yAxis.enableGridDashedLine(10f, 10f, 0f);

        chartWeek.getAxisRight().setEnabled(false);
        // false则可以单独向X或者Y方向缩放，true则在X和Y方向上同时缩放
        chartWeek.setPinchZoom(false);
    }

    @Override
    protected RankDetailPresenter createPresenter() {
        return new RankDetailPresenter();
    }

    @Override
    protected void onCreateData() {
        long userId = getArguments().getLong(KEY_USER_ID);
        presenter.loadRanks(userId, false);
    }

    @Override
    public void postShowUser(String nameEng) {

    }

    @Override
    public void showRanks(List<RankWeek> ranks) {

        if (ListUtil.isEmpty(ranks)) {
            return;
        }

        final ArrayList<String> dates = new ArrayList<>();

        ArrayList<Entry> values = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < ranks.size(); i++) {

            float val = (float) ranks.get(i).getRank();
            // 参数1是x轴值（这里用position，在XAxis里转换为要显示的数据），参数2是y值（这里直接赋值，也可以在YAxis里转换为要显示的数据）
            values.add(new Entry(i, val));
            dates.add(sdf.format(ranks.get(i).getDate()));

        }

        LineDataSet set1 = new LineDataSet(values, "Week-Rank");
        set1.setDrawIcons(false);

        // set the line to be drawn like this "- - - - - -"
//            set1.enableDashedLine(10f, 5f, 0f);
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(getResources().getColor(R.color.actionbar_bk_blue));
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(1f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
//            set1.setDrawFilled(true);
        set1.setFormLineWidth(1f);
//            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);

        // y值转化为整数
        LineData data = new LineData(set1);
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int) value);
            }
        });

        // x轴显示年-月-日
        XAxis xAxis = chartWeek.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates.get((int) value);
            }
        });

        YAxis yAxis = chartWeek.getAxisLeft();
        // 反向，排名高的在上面，否则，数值越大在越上面
        yAxis.setInverted(true);

        // LineDataSet的label显示，设置在左上方，默认在左下方
        Legend l = chartWeek.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);

        // set data
        chartWeek.setData(data);

        // 作为fragment嵌入到home时，经常在切换user后图标会加载不出来，加上animateY后就可以保证显示出来了
        chartWeek.animateY(700);
    }

    public void refresh() {
        presenter.loadRanks(presenter.getUser().getId(), false);
    }

    public void setOnChartClickListener(View.OnClickListener onChartClickListener) {
        this.onChartClickListener = onChartClickListener;
    }
}
