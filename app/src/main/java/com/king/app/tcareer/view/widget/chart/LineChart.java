package com.king.app.tcareer.view.widget.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
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

    private int mMinXCellWidth = ScreenUtils.dp2px(5);

    private int mNormalXCellWidth = ScreenUtils.dp2px(30);

    private int mXCellWidth;

    private int mValueTextColor;

    private int mLineSize = ScreenUtils.dp2px(1);
    private int mPointSize = ScreenUtils.dp2px(2);
    private int mValueTextSize = ScreenUtils.dp2px(12);

    private int mLineColor;

    /**
     * 指定当运用mMinXCellWidth时，隔几个刻度显示一个刻度
     */
    private int mDegreeCombine = 5;

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
        mMinXCellWidth = a.getDimensionPixelSize(R.styleable.LineChart_minXCellWidth, ScreenUtils.dp2px(5));
        mNormalXCellWidth = a.getDimensionPixelSize(R.styleable.LineChart_normalXCellWidth, ScreenUtils.dp2px(30));
        mValueTextColor = a.getColor(R.styleable.LineChart_valueTextColor, Color.parseColor("#333333"));
        mValueTextSize = a.getDimensionPixelSize(R.styleable.LineChart_valueTextSize, ScreenUtils.dp2px(12));
        mXCellWidth = mNormalXCellWidth;
        mLineColor = a.getColor(R.styleable.LineChart_lineColor, Color.parseColor("#3399ff"));
    }

    public void setAdapter(LineChartAdapter mAdapter) {
        DebugLog.e("");
        this.mAdapter = mAdapter;
        invalidate();
        requestLayout();
    }

    public void setDegreeCombine(int mDegreeCombine) {
        this.mDegreeCombine = mDegreeCombine;
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
            // 超过父容器最大宽度应用最小刻度宽度
            if (mYAxisTextWidth + 2 * mAxisLineXExtend + max * mNormalXCellWidth > getParentWidth()) {
                mXCellWidth = mMinXCellWidth;
            }
            else {
                mXCellWidth = mNormalXCellWidth;
            }
            defaultWidth = getPaddingLeft() + getPaddingRight()
                    + mXCellWidth * max;
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
        DebugLog.e("cur scroll " + getScrollX());
        Point startPoint;
        for (int i = 0; i < mAdapter.getLineCount(); i ++) {
            LineData line = mAdapter.getLineData(i);
            startPoint = null;
            for (int j = line.getStartX(); j <= line.getEndX(); j ++) {
                if (line.getColor() == 0) {
                    mPaint.setColor(mLineColor);
                }
                else {
                    mPaint.setColor(line.getColor());
                }
                int pointX = getDegreeX(j);
                int linePointIndex = j - line.getStartX();
                Integer value = line.getValues().get(linePointIndex);

                Point point = new Point(pointX, getDegreeY(value));
                if (startPoint != null) {
                    mPaint.setStrokeWidth(mLineSize);
                    canvas.drawLine(startPoint.x, startPoint.y, point.x, point.y, mPaint);
                }
                mPaint.setStrokeWidth(mPointSize);
                canvas.drawPoint(point.x, point.y, mPaint);
                startPoint = point;

                if (line.getValuesText() != null && linePointIndex >= 0 && linePointIndex < line.getValuesText().size()) {
                    String text = line.getValuesText().get(linePointIndex);
                    if (!TextUtils.isEmpty(text)) {
                        mPaint.setColor(mValueTextColor);
                        mPaint.setTextSize(mValueTextSize);
                        float tw = mPaint.measureText(text);
                        canvas.drawText(text, point.x - tw / 2, point.y - 10, mPaint);
                    }
                }
            }
        }
    }

    /**
     * 如果运用了最小距离表示有很多个点，这种密集情况为了能显示开x坐标文字
     * 采用每隔mDegreeCombine点显示一个刻度
     * @param position
     * @return
     */
    @Override
    protected boolean isDrawDegreeX(int position) {
        // 最后一个肯定画
        if (position == axisX.getDegreeCount() - 1) {
            return true;
        }
        // 刻度非常密集的情况
        if (mXCellWidth == mMinXCellWidth) {
            // 每隔mDegreeCombine点显示一个刻度
            if (position % mDegreeCombine != 0) {
                return false;
            }
            else {
                // 由于刻度按照mDegreeCombine来等分决定，所以会出现倒数第二个刻度仍然离最后一个刻度很近的情况
                // 这种情况下这个刻度也不画
                if (axisX.getDegreeCount() - 1 - position < mDegreeCombine) {
                    return false;
                }
            }
        }
        return true;
    }
}
