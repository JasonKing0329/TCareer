package com.king.app.tcareer.model.bean;

import com.king.app.tcareer.view.widget.chart.adapter.LineData;

import java.util.Date;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/10/23 10:41
 */
public class LineChartData {

    private int axisYCount;

    private int axisYTotalWeight;

    private List<AxisDegree<Integer>> axisYDegreeList;

    private int axisXCount;

    private int axisXTotalWeight;

    private List<AxisDegree<Date>> axisXDegreeList;

    private List<LineData> lineList;

    public int getAxisYCount() {
        return axisYCount;
    }

    public void setAxisYCount(int axisYCount) {
        this.axisYCount = axisYCount;
    }

    public int getAxisYTotalWeight() {
        return axisYTotalWeight;
    }

    public void setAxisYTotalWeight(int axisYTotalWeight) {
        this.axisYTotalWeight = axisYTotalWeight;
    }

    public int getAxisXCount() {
        return axisXCount;
    }

    public void setAxisXCount(int axisXCount) {
        this.axisXCount = axisXCount;
    }

    public int getAxisXTotalWeight() {
        return axisXTotalWeight;
    }

    public void setAxisXTotalWeight(int axisXTotalWeight) {
        this.axisXTotalWeight = axisXTotalWeight;
    }

    public List<LineData> getLineList() {
        return lineList;
    }

    public void setLineList(List<LineData> lineList) {
        this.lineList = lineList;
    }

    public List<AxisDegree<Integer>> getAxisYDegreeList() {
        return axisYDegreeList;
    }

    public void setAxisYDegreeList(List<AxisDegree<Integer>> axisYDegreeList) {
        this.axisYDegreeList = axisYDegreeList;
    }

    public List<AxisDegree<Date>> getAxisXDegreeList() {
        return axisXDegreeList;
    }

    public void setAxisXDegreeList(List<AxisDegree<Date>> axisXDegreeList) {
        this.axisXDegreeList = axisXDegreeList;
    }
}
