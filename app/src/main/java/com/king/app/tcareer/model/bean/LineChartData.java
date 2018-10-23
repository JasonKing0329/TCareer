package com.king.app.tcareer.model.bean;

import com.king.app.tcareer.view.widget.chart.adapter.LineData;

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

    private List<Integer> axisYWeightList;

    private List<String> axisYTextList;

    private List<Boolean> axisYIsNotDrawList;

    private int axisXCount;

    private int axisXTotalWeight;

    private List<Integer> axisXWeightList;

    private List<String> axisXTextList;

    private List<Boolean> axisXIsNotDrawList;

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

    public List<Integer> getAxisYWeightList() {
        return axisYWeightList;
    }

    public void setAxisYWeightList(List<Integer> axisYWeightList) {
        this.axisYWeightList = axisYWeightList;
    }

    public List<String> getAxisYTextList() {
        return axisYTextList;
    }

    public void setAxisYTextList(List<String> axisYTextList) {
        this.axisYTextList = axisYTextList;
    }

    public List<Boolean> getAxisYIsNotDrawList() {
        return axisYIsNotDrawList;
    }

    public void setAxisYIsNotDrawList(List<Boolean> axisYIsNotDrawList) {
        this.axisYIsNotDrawList = axisYIsNotDrawList;
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

    public List<Integer> getAxisXWeightList() {
        return axisXWeightList;
    }

    public void setAxisXWeightList(List<Integer> axisXWeightList) {
        this.axisXWeightList = axisXWeightList;
    }

    public List<String> getAxisXTextList() {
        return axisXTextList;
    }

    public void setAxisXTextList(List<String> axisXTextList) {
        this.axisXTextList = axisXTextList;
    }

    public List<Boolean> getAxisXIsNotDrawList() {
        return axisXIsNotDrawList;
    }

    public void setAxisXIsNotDrawList(List<Boolean> axisXIsNotDrawList) {
        this.axisXIsNotDrawList = axisXIsNotDrawList;
    }

    public List<LineData> getLineList() {
        return lineList;
    }

    public void setLineList(List<LineData> lineList) {
        this.lineList = lineList;
    }
}
