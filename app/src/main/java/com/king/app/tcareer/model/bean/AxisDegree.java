package com.king.app.tcareer.model.bean;

/**
 * Desc:应用于LineChart描述其x坐标轴刻度的实体
 *
 * @author：Jing Yang
 * @date: 2018/10/23 16:08
 */
public class AxisDegree<T> {

    private String text;

    private boolean isNotDraw;

    private int weight;

    private T data;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isNotDraw() {
        return isNotDraw;
    }

    public void setNotDraw(boolean notDraw) {
        isNotDraw = notDraw;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
