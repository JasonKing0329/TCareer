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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.king.app.tcareer.R;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.widget.chart.adapter.IAxis;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/30 15:37
 */
public abstract class AxisChart extends View implements View.OnTouchListener {

    private int mDashColor = Color.parseColor("#dfdfdf");

    protected Paint mPaint = new Paint();

    protected int mAxisLineColor = Color.parseColor("#333333");

    protected int mAxisLineXExtend;

    protected IAxis axisY;

    protected IAxis axisX;

    protected Point mOriginPoint;

    protected int mXAxisTextHeight = ScreenUtils.dp2px(30);

    protected int mYAxisTextWidth = ScreenUtils.dp2px(30);

    private boolean mDrawDashGrid = false;

    private boolean mDrawYAxis = true;

    private Scroller scroller;

    private GestureDetector gestureDetector;

    public AxisChart(Context context) {
        super(context);
        init(null);
    }

    public AxisChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOnTouchListener(this);
        scroller = new Scroller(getContext());
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                DebugLog.scroll("");
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                DebugLog.scroll("");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                DebugLog.scroll("");
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                DebugLog.scroll("");
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                DebugLog.scroll("");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                DebugLog.scroll("x1=" + e1.getX() + ", x2=" + e2.getX() + ", velocityX=" + velocityX);
                // 速率小于200就不用继续滑动了，相当于慢速拖动
                if (Math.abs(velocityX) < 300) {
                    return true;
                }
                int scrollX = createScrollByVelocity(velocityX);
                DebugLog.scroll("scrollX=" + scrollX);
                scrollX = checkScrollEdge(scrollX);
                scroller.startScroll(getScrollX(), 0, scrollX, 0);
                invalidate();
                return true;
            }
        });

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AxisChart);
        mAxisLineXExtend = a.getDimensionPixelSize(R.styleable.AxisChart_axisXExtendLineWidth, ScreenUtils.dp2px(0));
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

    /**
     * 根据速率来处理继续滑动，一般有效的fling速率都在绝对值1000-15000(像素)之间，这里定义越快就继续滚动得越多
     * 为了适应不同的分辨率，根据dp值来分档
     * @param velocity
     * @return
     */
    private int createScrollByVelocity(float velocity) {
        return (int) (getResources().getDisplayMetrics().density * (-velocity / 18));
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
                defaultWidth = measureDefaultWidth(defaultWidth);
                break;
            case MeasureSpec.EXACTLY:
                DebugLog.e("---speMode = EXACTLY");
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                DebugLog.e("---speMode = UNSPECIFIED");
//                defaultWidth = Math.max(defaultWidth, specSize);
                defaultWidth = measureDefaultWidth(defaultWidth);
        }
        DebugLog.e("---defaultWidth = " + defaultWidth + "");
        return defaultWidth;
    }

    /**
     * 总宽度等于 两端延长线的宽度+Y轴显示刻度文字的宽度+X轴所有刻度总宽度
     * @param defaultWidth
     * @return
     */
    private int measureDefaultWidth(int defaultWidth) {
        return getPaddingLeft() + getPaddingRight()
                + mAxisLineXExtend * 2 + mYAxisTextWidth + measureChartWidth(defaultWidth);
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
                defaultHeight = getPaddingTop() + getPaddingBottom() + measureChartHeight(defaultHeight);
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                DebugLog.e("---speSize = EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
//                defaultHeight = Math.max(defaultHeight, specSize);
                DebugLog.e("---speSize = UNSPECIFIED");
                defaultHeight = getPaddingTop() + getPaddingBottom() + measureChartHeight(defaultHeight);
                break;
        }
        DebugLog.e("---defaultHeight = " + defaultHeight + "");
        return defaultHeight;
    }

    public Point getOriginPoint() {
        return mOriginPoint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mOriginPoint = new Point(mYAxisTextWidth + getPaddingLeft(), getHeight() - getPaddingBottom() - mXAxisTextHeight);
        if (axisX != null) {
            drawAxisX(canvas);
        }
        if (mDrawYAxis && axisY != null) {
            drawAxisY(canvas);
        }
        if (mDrawDashGrid && axisY != null && axisX != null) {
            drawDashGrid(canvas);
        }
        drawContent(canvas);
    }

    protected abstract void drawContent(Canvas canvas);

    private void drawAxisX(Canvas canvas) {
        int originY = mOriginPoint.y;
        int originX = mOriginPoint.x;
        mPaint.setColor(mAxisLineColor);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(1));
        canvas.drawLine(originX, originY, getWidth() - getPaddingRight(), originY, mPaint);
        int xCount = axisX.getDegreeCount();
        if (xCount == 0) {
            return;
        }

        // 绘制x轴刻度
        for (int i = 0; i < xCount; i ++) {
            int x = getDegreeX(i);
            boolean drawDegree = false;
            // 设置了延长线，刻画第0个点
            if (mAxisLineXExtend > 0) {
                drawDegree = true;
            }
            // 没有设置延长线，第0个点即原点，不刻画
            else {
                if (i > 0) {
                    drawDegree = true;
                }
            }
            if (drawDegree) {
                int degree = ScreenUtils.dp2px(2);// 刻度的高度
                canvas.drawLine(x, mOriginPoint.y, x, mOriginPoint.y - degree, mPaint);
            }

            // drawText不能换行，用TextPaint和StaticLayout
//            mPaint.setTextSize(ScreenUtils.dp2px(14));
//            int textTop = ScreenUtils.dp2px(14);
//            canvas.drawText(mAdapter.getXAxisName(i), x - 20, startY + textTop, mPaint);

            String text = axisX.getTextAt(i);
            if (!TextUtils.isEmpty(text)) {
                drawDegreeXText(text, x, originY, canvas);
            }
        }
    }

    protected void drawDegreeXText(String text, int textX, int textY, Canvas canvas) {
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(ScreenUtils.dp2px(12));
        textPaint.setColor(mAxisLineColor);

        // StaticLayout只能画在canvas的0,0上，因此必须通过translate画布实现，注意save和restore
        StaticLayout layout = new StaticLayout(text, textPaint
                , ScreenUtils.dp2px(50), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, true);
        canvas.save();
        canvas.translate(textX - 20, textY);
        layout.draw(canvas);
        canvas.restore();
    }

    private void drawAxisY(Canvas canvas) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        mPaint.setColor(mAxisLineColor);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(1));
        canvas.drawLine(startX, startY, startX, getPaddingTop(), mPaint);
        int yCount = axisY.getDegreeCount();
        if (yCount == 0) {
            return;
        }
        mPaint.setTextSize(ScreenUtils.dp2px(14));
        int textTop = ScreenUtils.dp2px(5);
        int degreeWidth = ScreenUtils.dp2px(2);
        for (int i = 0; i < yCount; i ++) {
            int y = getDegreeY(i);
            if (i > 0) {
                canvas.drawLine(startX, y, startX + degreeWidth, y, mPaint);
            }
            String text = axisY.getTextAt(i);
            if (!TextUtils.isEmpty(text)) {
                canvas.drawText(text, getPaddingLeft(), y + textTop, mPaint);
            }
        }
    }

    /**
     * 获取X坐标刻度的准确位置
     * @return
     */
    protected int getDegreeX(int index) {
        int totalDegreeWidth = getWidth() - getPaddingRight() - mOriginPoint.x - 2 * mAxisLineXExtend;
        int degreeX = (int) ((float) axisX.getWeightAt(index) / (float) axisX.getTotalWeight() * totalDegreeWidth);
        // 原点+延长线+刻度间隔
        int x = mOriginPoint.x + mAxisLineXExtend + degreeX;
        return x;
    }

    /**
     * 获取Y坐标刻度的准确位置
     * @return
     */
    protected int getDegreeY(int index) {
        int height = mOriginPoint.y - getPaddingTop();
        return mOriginPoint.y - (int) ((float) axisY.getWeightAt(index) / (float) axisY.getTotalWeight() * height);
    }

    private void drawDashGrid(Canvas canvas) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        mPaint.setColor(mDashColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(0.5f));
        mPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
        int xCount = axisX.getDegreeCount();
        int yCount = axisY.getDegreeCount();
        for (int i = 0; i < xCount; i ++) {
            // 原点+延长线+刻度间隔
            int x = getDegreeX(i);
            boolean drawDegree = false;
            // 设置了延长线，刻画第0个点
            if (mAxisLineXExtend > 0) {
                drawDegree = true;
            }
            // 没有设置延长线，第0个点即原点，不刻画
            else {
                if (i > 0) {
                    drawDegree = true;
                }
            }
            if (drawDegree) {
                Path path = new Path();
                path.moveTo(x, startY);
                path.lineTo(x, getPaddingTop());
                canvas.drawPath(path, mPaint);
            }
        }
        for (int i = 0; i < yCount; i ++) {
            if (i > 0) {
                int y = getDegreeY(i);
//                canvas.drawLine(startX, y, getWidth() - mPadding, y, mPaint);
                Path path = new Path();
                path.moveTo(startX, y);
                path.lineTo(getWidth() - getPaddingRight(), y);
                canvas.drawPath(path, mPaint);
            }
        }

        mPaint.reset();
    }

    float lastX;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                break;
//
            case MotionEvent.ACTION_MOVE:
                float mXMove = event.getRawX();
                int scrolledX = (int) (lastX - mXMove);
                setScroll(scrolledX);
                lastX = mXMove;
                break;
//            case MotionEvent.ACTION_UP:
//                // 调用startScroll()方法来初始化滚动数据并刷新界面
//                scroller.startScroll(getScrollX(), 0, 250, 0);
//                invalidate();
//                break;
        }

        return gestureDetector.onTouchEvent(event);
    }

    /**
     * 控制边界
     * @param scrolledX
     */
    private void setScroll(int scrolledX) {
        scrolledX = checkScrollEdge(scrolledX);
        DebugLog.scroll("move scrollX=" + scrolledX);
        scrollBy(scrolledX, 0);
    }

    private int checkScrollEdge(int scrolledX) {
        // 还没充满父控件不用滑动
        if (getWidth() < getParentWidth()) {
            return 0;
        }
        // 右边界
        if (getScrollX() + getParentWidth() + scrolledX > getWidth()) {
            scrolledX = getWidth() - getParentWidth() - getScrollX();
        }
        // 左边界
        else if (getScrollX() + scrolledX < 0) {
            scrolledX = 0 - getScrollX();
        }
        return scrolledX;
    }

    private int getParentWidth() {
        if (getParent() != null && getParent() instanceof ViewGroup) {
            return ((ViewGroup) getParent()).getWidth();
        }
        return 0;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }
}
