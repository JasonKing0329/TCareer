package com.king.app.tcareer.view.widget.chart.adapter;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/3 16:27
 */
public abstract class BarChartAdapter {

    public abstract int getXCount();
    public abstract int getBarColor(int position);

    public abstract Integer getValueWeight(int xIndex);
    public abstract String getValueText(int xIndex);
}
