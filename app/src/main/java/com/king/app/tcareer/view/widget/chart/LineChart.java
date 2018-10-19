package com.king.app.tcareer.view.widget.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.king.app.tcareer.R;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.widget.chart.adapter.LineChartAdapter;
import com.king.app.tcareer.view.widget.chart.adapter.LineData;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/3 16:26
 */
public class LineChart extends AxisChart {

    private LineChartAdapter mAdapter;

    private int mMinXCellWidth = ScreenUtils.dp2px(30);

    private int mMinYCellHeight = ScreenUtils.dp2px(30);

    public LineChart(Context context) {
        super(context);
        init(null);
    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOnTouchListener(this);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LineChart);
        mMinXCellWidth = a.getDimensionPixelSize(R.styleable.LineChart_minXCellWidth, ScreenUtils.dp2px(30));
        mMinYCellHeight = a.getDimensionPixelSize(R.styleable.LineChart_minYCellHeight, ScreenUtils.dp2px(30));
    }

    public void setAdapter(LineChartAdapter mAdapter) {
        DebugLog.e("");
        this.mAdapter = mAdapter;
        invalidate();
        requestLayout();
    }

    @Override
    protected int measureChartWidth(int defaultWidth) {
        if (mAdapter != null && mAdapter.getLineCount() > 0) {
            int max = mAdapter.getLineData(0).getEndX();
            for (int i = 1; i < mAdapter.getLineCount(); i ++) {
                if (mAdapter.getLineData(i).getEndX() > max) {
                    max = mAdapter.getLineData(i).getEndX();
                }
            }
            defaultWidth = getPaddingLeft() + getPaddingRight()
                    + mMinXCellWidth * max;
        }
        return defaultWidth;
    }

    @Override
    protected int measureChartHeight(int defaultHeight) {
        if (mAdapter != null) {
            defaultHeight = mXAxisTextHeight;
        }
        return defaultHeight;
    }

    @Override
    protected void drawContent(Canvas canvas) {
        if (mAdapter != null) {
            drawPointAndLine(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void drawPointAndLine(Canvas canvas) {
        Point startPoint;
        for (int i = 0; i < mAdapter.getLineCount(); i ++) {
            LineData line = mAdapter.getLineData(i);
            mPaint.setColor(line.getColor());
            startPoint = null;
            for (int j = line.getStartX(); j <= line.getEndX(); j ++) {
                int linePointIndex = j - line.getStartX();
                Integer value = line.getValues().get(linePointIndex);

                Point point = new Point(getDegreeX(j), getDegreeY(value));
                if (startPoint != null) {
                    mPaint.setStrokeWidth(ScreenUtils.dp2px(1));
                    canvas.drawLine(startPoint.x, startPoint.y, point.x, point.y, mPaint);
                }
                mPaint.setStrokeWidth(ScreenUtils.dp2px(2));
                canvas.drawPoint(point.x, point.y, mPaint);
                startPoint = point;

                if (line.getValuesText() != null && linePointIndex >= 0 && linePointIndex < line.getValuesText().size()) {
                    String text = line.getValuesText().get(linePointIndex);
                    if (!TextUtils.isEmpty(text)) {
                        mPaint.setTextSize(ScreenUtils.dp2px(12));
                        canvas.drawText(text, point.x + 10, point.y, mPaint);
                    }
                }
            }
        }
    }

}
