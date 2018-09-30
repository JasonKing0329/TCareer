package com.king.app.tcareer.view.widget.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.widget.chart.adapter.LineChartAdapter;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/3 16:26
 */
public class LineChart extends View {

    private Paint mPaint = new Paint();

    private int mXAxisTextHeight = ScreenUtils.dp2px(30);

    private int mYAxisTextWidth = ScreenUtils.dp2px(30);

    private int mAxisLineColor = Color.parseColor("#333333");

    private int mDashColor = Color.parseColor("#dfdfdf");

    private LineChartAdapter mAdapter;

    private Point mOriginPoint;

    private int mDegreeX;

    private int mDegreeY;

    private boolean mDrawDashGrid;

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

    public void setDrawDashGrid(boolean mDrawDashGrid) {
        this.mDrawDashGrid = mDrawDashGrid;
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
                if (mAdapter != null) {
                    defaultWidth = getPaddingLeft() + getPaddingRight() + mMinXCellWidth * mAdapter.getXAxisCount() + mYAxisTextWidth;
                }
                break;
            case MeasureSpec.EXACTLY:
                DebugLog.e("---speMode = EXACTLY");
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                DebugLog.e("---speMode = UNSPECIFIED");
//                defaultWidth = Math.max(defaultWidth, specSize);
                if (mAdapter != null) {
                    defaultWidth = getPaddingLeft() + getPaddingRight() + mMinXCellWidth * mAdapter.getXAxisCount() + mYAxisTextWidth;
                }
        }
        DebugLog.e("---defaultWidth = " + defaultWidth + "");
        return defaultWidth;
    }


    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        DebugLog.e("---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                DebugLog.e("---speMode = AT_MOST");
                if (mAdapter != null) {
                    defaultHeight = getPaddingTop() + getPaddingBottom() + mMinYCellHeight * mAdapter.getYAxisCount() + mXAxisTextHeight;
                }
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                DebugLog.e("---speSize = EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
//                defaultHeight = Math.max(defaultHeight, specSize);
                DebugLog.e("---speSize = UNSPECIFIED");
                if (mAdapter != null) {
                    defaultHeight = getPaddingTop() + getPaddingBottom() + mMinYCellHeight * mAdapter.getYAxisCount() + mXAxisTextHeight;
                }
                break;
        }
        DebugLog.e("---defaultHeight = " + defaultHeight + "");
        return defaultHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAdapter != null) {
            mOriginPoint = new Point(mYAxisTextWidth + getPaddingLeft(), getHeight() - getPaddingBottom() - mXAxisTextHeight);
            drawXAxis(canvas);
            drawYAxis(canvas);
            if (mDrawDashGrid) {
                drawDashGrid(canvas);
            }
            drawPointAndLine(canvas);
        }
        super.onDraw(canvas);
    }

    private void drawXAxis(Canvas canvas) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        mPaint.setColor(mAxisLineColor);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(1));
        canvas.drawLine(startX, startY, getWidth() - getPaddingRight(), startY, mPaint);
        int width = getWidth() - getPaddingRight() - startX;
        int xCount = mAdapter.getXAxisCount();
        if (xCount == 0) {
            return;
        }
        mDegreeX = width / xCount;
        int degree = ScreenUtils.dp2px(2);

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(ScreenUtils.dp2px(12));
        textPaint.setColor(mAxisLineColor);

        for (int i = 0; i < xCount; i ++) {
            int x = startX + i * mDegreeX;
            if (i > 0) {
                canvas.drawLine(x, startY, x, startY - degree, mPaint);
            }
            // drawText不能换行，用TextPaint和StaticLayout
//            mPaint.setTextSize(ScreenUtils.dp2px(14));
//            int textTop = ScreenUtils.dp2px(14);
//            canvas.drawText(mAdapter.getXAxisName(i), x - 20, startY + textTop, mPaint);

            // StaticLayout只能画在canvas的0,0上，因此必须通过translate画布实现，注意save和restore
            StaticLayout layout = new StaticLayout(mAdapter.getXAxisName(i), textPaint
                    , mDegreeX, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, true);
            canvas.save();
            canvas.translate(x - 20, startY);
            layout.draw(canvas);
            canvas.restore();
        }
    }

    private void drawYAxis(Canvas canvas) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        mPaint.setColor(mAxisLineColor);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(1));
        canvas.drawLine(startX, startY, startX, getPaddingTop(), mPaint);
        int height = startY - getPaddingTop();
        int yCount = mAdapter.getYAxisCount();
        if (yCount == 0) {
            return;
        }
        mDegreeY = height / yCount;
        int degree = ScreenUtils.dp2px(2);

        mPaint.setTextSize(ScreenUtils.dp2px(14));
        int textTop = ScreenUtils.dp2px(5);
        for (int i = 0; i < yCount; i ++) {
            int y = startY - i * mDegreeY;
            if (i > 0) {
                canvas.drawLine(startX, y, startX + degree, y, mPaint);
            }
            canvas.drawText(mAdapter.getYAxisName(i), getPaddingLeft(), y + textTop, mPaint);
        }
    }

    private void drawDashGrid(Canvas canvas) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        mPaint.setColor(mDashColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(0.5f));
        mPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
        int xCount = mAdapter.getXAxisCount();
        int yCount = mAdapter.getYAxisCount();
        for (int i = 0; i < xCount; i ++) {
            int x = startX + i * mDegreeX;
            if (i > 0) {
//                canvas.drawLine(x, startY, x, mPadding, mPaint);
                Path path = new Path();
                path.moveTo(x, startY);
                path.lineTo(x, getPaddingTop());
                canvas.drawPath(path, mPaint);
            }
        }
        for (int i = 0; i < yCount; i ++) {
            int y = startY - i * mDegreeY;
            if (i > 0) {
//                canvas.drawLine(startX, y, getWidth() - mPadding, y, mPaint);
                Path path = new Path();
                path.moveTo(startX, y);
                path.lineTo(getWidth() - getPaddingRight(), y);
                canvas.drawPath(path, mPaint);
            }
        }

        mPaint.reset();
    }

    private void drawPointAndLine(Canvas canvas) {
        int lineNumber = mAdapter.getLineCount();
        Point startPoint;
        for (int i = 0; i < lineNumber; i ++) {
            mPaint.setColor(mAdapter.getLineColor(i));
            startPoint = null;
            for (int j = 0; j < mAdapter.getXAxisCount(); j ++) {
                Integer value = mAdapter.getValue(i, j);
                int index = findValueIndex(value);
                if (index == -1) {
                    startPoint = null;
                    continue;
                }

                Point point = getPoint(j, index);
                if (startPoint != null) {
                    mPaint.setStrokeWidth(ScreenUtils.dp2px(1));
                    canvas.drawLine(startPoint.x, startPoint.y, point.x, point.y, mPaint);
                }
                mPaint.setStrokeWidth(ScreenUtils.dp2px(2));
                canvas.drawPoint(point.x, point.y, mPaint);
                startPoint = point;

                String text = mAdapter.getText(i, j);
                if (!TextUtils.isEmpty(text)) {
                    mPaint.setTextSize(ScreenUtils.dp2px(12));
                    canvas.drawText(text, point.x + 10, point.y, mPaint);
                }
            }
        }
    }

    private int findValueIndex(Integer value) {
        if (value != null) {
            for (int i = 0; i < mAdapter.getYAxisCount(); i ++) {
                if (mAdapter.getYAxisValue(i) != null && mAdapter.getYAxisValue(i) == value) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Point getPoint(int indexX, int indexY) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        return new Point(startX + indexX * mDegreeX, startY - indexY * mDegreeY);
    }
}
