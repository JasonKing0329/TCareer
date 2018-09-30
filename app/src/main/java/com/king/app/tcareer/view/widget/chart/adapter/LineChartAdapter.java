package com.king.app.tcareer.view.widget.chart.adapter;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/3 16:27
 */
public abstract class LineChartAdapter {
    public abstract int getXAxisCount();
    public abstract String getXAxisName(int position);

    public abstract int getYAxisCount();
    public abstract String getYAxisName(int position);
    public abstract Integer getYAxisValue(int i);

    public abstract int getLineCount();
    public abstract int getLineColor(int position);

    public abstract Integer getValue(int lineIndex, int xIndex);

    public abstract String getText(int lineIndex, int xIndex);
}
