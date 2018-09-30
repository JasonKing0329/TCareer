package com.king.app.tcareer.view.widget.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.king.app.tcareer.R;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.widget.chart.adapter.BarChartAdapter;

/**
 * Desc:bar chart属于x轴等分，y轴权值不固定的图表
 *
 * @author：Jing Yang
 * @date: 2018/7/3 16:26
 */
public class BarChart extends AxisChart {

    private int mDashColor = Color.parseColor("#dfdfdf");

    private BarChartAdapter mAdapter;

    private int mDegreeY;

    private int mBarWidth = ScreenUtils.dp2px(50);

    private int mBarGap = ScreenUtils.dp2px(5);

    public BarChart(Context context) {
        super(context);
        init(null);
    }

    public BarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BarChart);
        mBarWidth = a.getDimensionPixelSize(R.styleable.BarChart_barWidth, ScreenUtils.dp2px(50));
        mBarGap = a.getDimensionPixelSize(R.styleable.BarChart_barGap, ScreenUtils.dp2px(5));
    }

    public void setAdapter(BarChartAdapter mAdapter) {
        DebugLog.e("");
        this.mAdapter = mAdapter;
        invalidate();
        requestLayout();
    }

    @Override
    protected int measureChartWidth(int defaultWidth) {
        if (mAdapter != null) {
            defaultWidth = getPaddingLeft() + getPaddingRight()
                    + (mBarWidth + mBarGap) * mAdapter.getXCount()
                    + mYAxisTextWidth;
        }
        return defaultWidth;
    }

    @Override
    protected int measureChartHeight(int defaultHeight) {
        if (mAdapter != null) {
            defaultHeight = getPaddingTop() + getPaddingBottom() + mXAxisTextHeight;
        }
        return defaultHeight;
    }

    @Override
    protected void drawContent(Canvas canvas) {
        if (mAdapter != null) {
            int startY = mOriginPoint.y;
            int startX = mOriginPoint.x;
            int height = startY - getPaddingTop();
            int width = getWidth() - getPaddingRight() - startX;
            for (int i = 0; i < mAdapter.getXCount(); i ++) {
                mPaint.setColor(mAdapter.getBarColor(i));
                mPaint.setStyle(Paint.Style.FILL);

                int left = startX + (int) ((float) axisX.getWeightAt(i) / (float) axisX.getTotalWeight() * width)
                        + i * (mBarWidth + mBarGap);
                int top = startY - (int) ((float) mAdapter.getValueWeight(i) / (float) axisY.getTotalWeight() * height);
                int right = left + mBarWidth;
                int bottom = startY;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }
}
