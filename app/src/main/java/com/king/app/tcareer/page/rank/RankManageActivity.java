package com.king.app.tcareer.page.rank;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.db.entity.Rank;
import com.king.app.tcareer.model.db.entity.RankCareer;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.page.score.ScoreCalculator;
import com.king.app.tcareer.utils.ListUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/22 19:07
 */
public class RankManageActivity extends BaseMvpActivity<RankPresenter> implements RankView, View.OnClickListener {

    public static final String KEY_USER_ID = "key_user_id";

    @BindView(R.id.view7_actionbar_back)
    ImageView ivBack;
    @BindView(R.id.view7_actionbar_title)
    TextView tvTitle;
    @BindView(R.id.view7_actionbar_menu)
    ImageView ivMenu;
    @BindView(R.id.iv_chart)
    ImageView ivChart;
    @BindView(R.id.group_chart_container)
    ViewGroup groupChartContainer;
    @BindView(R.id.rank_manage_list)
    RecyclerView rvRankList;
    @BindView(R.id.chart_week)
    LineChart chartWeek;

    private RankChartFragment ftChart;

    private RankItemAdapter rankItemAdapter;

    private long userId;

    @Override
    protected int getContentView() {
        return R.layout.activity_rank_manage;
    }

    @Override
    protected void initView() {
        ivBack.setVisibility(View.VISIBLE);
        ivChart.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        tvTitle.setText("Rank");
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRankList.setLayoutManager(manager);
        rvRankList.setItemAnimator(new DefaultItemAnimator());

        initWeekChart();
    }

    private void initWeekChart() {
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
    protected RankPresenter createPresenter() {
        return new RankPresenter();
    }

    @Override
    public void initData() {
        userId = getIntent().getLongExtra(KEY_USER_ID, -1);
        presenter.loadRanks(userId);
    }

    @Override
    public void postShowRanks(final List<Rank> rankList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showYearRanks(rankList);
            }
        });
    }

    private void showYearRanks(final List<Rank> rankList) {
        rankItemAdapter = new RankItemAdapter(rankList);
        rankItemAdapter.setOnRankActionListener(new RankItemAdapter.OnRankActionListener() {
            @Override
            public void onDeleteRank(int position) {
                presenter.deleteRank(rankList.get(position));
                tagUpdated();
                refreshRanks();
            }

            @Override
            public void onEditRank(int position) {
                updateRank(rankList.get(position));
            }
        });
        rvRankList.setAdapter(rankItemAdapter);

        initChartFragment();
    }

    private void initChartFragment() {
        ftChart = RankChartFragment.newInstance(userId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.group_chart_container, ftChart, "RankChartFragment");
        ft.commit();
    }

    @Override
    public void showWeekRanks(List<RankWeek> ranks) {

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
    }

    /**
     * 标志更新过数据
     */
    private void tagUpdated() {
        setResult(RESULT_OK);
    }

    @OnClick({R.id.view7_actionbar_back, R.id.iv_chart, R.id.iv_add, R.id.iv_add_week})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view7_actionbar_back:
                // 加入了转场动画，必须用onBackPressed，finish无效果
                onBackPressed();
                break;
            case R.id.iv_add:
                addRank();
                break;
            case R.id.iv_chart:
                break;
            case R.id.iv_add_week:
                showScoreCalculator();
                break;
        }
    }

    private void showScoreCalculator() {
        ScoreCalculator calculator = new ScoreCalculator();
        calculator.setUserId(userId);
        calculator.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                presenter.loadRanks(userId);
            }
        });
        calculator.show(getSupportFragmentManager(), "ScoreCalculator");
    }

    private void addRank() {
        ScoreEditDialog dialog = new ScoreEditDialog();
        dialog.setMode(ScoreEditDialog.MODE_YEAR_RANK);
        dialog.setUser(presenter.getUser());
        dialog.setOnRankListener(new ScoreEditDialog.OnRankListener() {
            @Override
            public void onSaveYearRank(Rank rank) {
                rank.setUserId(userId);
                presenter.saveRankFinal(rank);
                tagUpdated();
                refreshRanks();
            }

            @Override
            public void onSaveCountRank(RankCareer rank) {

            }
        });
        dialog.show(getSupportFragmentManager(), "ScoreEditDialog");
    }

    private void updateRank(Rank rank) {
        ScoreEditDialog dialog = new ScoreEditDialog();
        dialog.setMode(ScoreEditDialog.MODE_YEAR_RANK);
        dialog.setRank(rank);
        dialog.setUser(presenter.getUser());
        dialog.setOnRankListener(new ScoreEditDialog.OnRankListener() {
            @Override
            public void onSaveYearRank(Rank rank) {
                rank.setUserId(userId);
                presenter.saveRankFinal(rank);
                tagUpdated();
                refreshRanks();
            }

            @Override
            public void onSaveCountRank(RankCareer rank) {

            }
        });
        dialog.show(getSupportFragmentManager(), "ScoreEditDialog");
    }

    private void refreshRanks() {
        initData();
    }

}
