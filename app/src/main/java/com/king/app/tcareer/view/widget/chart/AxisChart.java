package com.king.app.tcareer.view.widget.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.widget.chart.adapter.IAxis;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/30 15:37
 */
public abstract class AxisChart extends View {

    protected Paint mPaint = new Paint();

    private int mAxisLineColor = Color.parseColor("#333333");

    protected IAxis axisY;

    protected IAxis axisX;

    protected Point mOriginPoint;

    protected int mXAxisTextHeight = ScreenUtils.dp2px(30);

    protected int mYAxisTextWidth = ScreenUtils.dp2px(30);

    private boolean mDrawDashGrid = false;

    private boolean mDrawYAxis = true;

    public AxisChart(Context context) {
        super(context);
    }

    public AxisChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAxisX(IAxis axisX) {
        this.axisX = axisX;
    }

    public void setAxisY(IAxis axisY) {
        this.axisY = axisY;
    }

    public void setDrawDashGrid(boolean mDrawDashGrid) {
        this.mDrawDashGrid = mDrawDashGrid;
    }

    public void setDrawAxisY(boolean mDrawYAxis) {
        this.mDrawYAxis = mDrawYAxis;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int minimumWidth = getSuggestedMinimumWidth();
        int minimumHeight = getSuggestedMinimumHeight();
        DebugLog.e("---minimumWidth = " + minimumWidth + "");
        DebugLog.e("---minimumHeight = " + minimumHeight + "");
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * specMode判断控件设置的layout_width
     * 1. 本view layout_width指定为固定值，specMode=固定值
     * 2. 本view嵌套在HorizontalScrollView中，HorizontalScrollView作用于横向滚动
     *      --> 无论本view layout_width设置的是match_parent还是wrap_content，specMode=UNSPECIFIED
     *          为支持嵌入HorizontalScrollView滚动视图，在UNSPECIFIED里计算本view应该有的宽度
     * 3. 本view嵌套在其他没有横向滚动功能的ViewGroup中
     *      --> ViewGroup宽度已知（指定过大小，或match_parent，parent已知大小，比如整个屏幕）
     *          --> 无论本view layout_width设置的是match_parent还是wrap_content，specMode=AT_MOST
     *              所以这里选择在AT_MOST也运用本view应该有的宽度，也可以改为运用parent的宽度
     *      --> ViewGroup宽度未知（不是说设置为wrap_content就是未知，而是比如嵌套在HorizontalScrollView中，导致ViewGroup的宽度也未知）
     *          --> 同第2条
     *
     *  measureHeight同理，考虑layout_height与是否嵌入ScrollView
     * @param defaultWidth
     * @param measureSpec
     * @return
     */
    private int measureWidth(int defaultWidth, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        DebugLog.e("---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                DebugLog.e("---speMode = AT_MOST");
                defaultWidth = measureChartWidth(defaultWidth);
                break;
            case MeasureSpec.EXACTLY:
                DebugLog.e("---speMode = EXACTLY");
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                DebugLog.e("---speMode = UNSPECIFIED");
//                defaultWidth = Math.max(defaultWidth, specSize);
                defaultWidth = measureChartWidth(defaultWidth);
        }
        DebugLog.e("---defaultWidth = " + defaultWidth + "");
        return defaultWidth;
    }

    protected abstract int measureChartWidth(int defaultWidth);

    protected abstract int measureChartHeight(int defaultHeight);

    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        DebugLog.e("---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                DebugLog.e("---speMode = AT_MOST");
                defaultHeight = measureChartHeight(defaultHeight);
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                DebugLog.e("---speSize = EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
//                defaultHeight = Math.max(defaultHeight, specSize);
                DebugLog.e("---speSize = UNSPECIFIED");
                defaultHeight = measureChartHeight(defaultHeight);
                break;
        }
        DebugLog.e("---defaultHeight = " + defaultHeight + "");
        return defaultHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mOriginPoint = new Point(mYAxisTextWidth + getPaddingLeft(), getHeight() - getPaddingBottom() - mXAxisTextHeight);
        drawAxisX(canvas);
        if (mDrawYAxis) {
            drawAxisY(canvas);
        }
        drawContent(canvas);
    }

    protected abstract void drawContent(Canvas canvas);

    private void drawAxisX(Canvas canvas) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        mPaint.setColor(mAxisLineColor);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(1));
        canvas.drawLine(startX, startY, getWidth() - getPaddingRight(), startY, mPaint);
        int width = getWidth() - getPaddingRight() - startX;
        int xCount = axisX.getDegreeCount();
        if (xCount == 0) {
            return;
        }
        int degree = ScreenUtils.dp2px(2);

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(ScreenUtils.dp2px(12));
        textPaint.setColor(mAxisLineColor);

        for (int i = 0; i < xCount; i ++) {
            int x = startX + (int) ((float) axisX.getWeightAt(i) / (float) axisX.getTotalWeight() * width);
            if (i > 0) {
                canvas.drawLine(x, startY, x, startY - degree, mPaint);
            }
            // drawText不能换行，用TextPaint和StaticLayout
//            mPaint.setTextSize(ScreenUtils.dp2px(14));
//            int textTop = ScreenUtils.dp2px(14);
//            canvas.drawText(mAdapter.getXAxisName(i), x - 20, startY + textTop, mPaint);

            // StaticLayout只能画在canvas的0,0上，因此必须通过translate画布实现，注意save和restore
            StaticLayout layout = new StaticLayout(axisX.getTextAt(i), textPaint
                    , ScreenUtils.dp2px(50), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, true);
            canvas.save();
            canvas.translate(x - 20, startY);
            layout.draw(canvas);
            canvas.restore();
        }
    }

    private void drawAxisY(Canvas canvas) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        mPaint.setColor(mAxisLineColor);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(1));
        canvas.drawLine(startX, startY, startX, getPaddingTop(), mPaint);
        int height = startY - getPaddingTop();
        int yCount = axisY.getDegreeCount();
        if (yCount == 0) {
            return;
        }
        mPaint.setTextSize(ScreenUtils.dp2px(14));
        int textTop = ScreenUtils.dp2px(5);
        int degreeWidth = ScreenUtils.dp2px(2);
        for (int i = 0; i < yCount; i ++) {
            int y = startY - (int) ((float) axisY.getWeightAt(i) / (float) axisY.getTotalWeight() * height);
            if (i > 0) {
                canvas.drawLine(startX, y, startX + degreeWidth, y, mPaint);
            }
            canvas.drawText(axisY.getTextAt(i), getPaddingLeft(), y + textTop, mPaint);
        }
    }
}
