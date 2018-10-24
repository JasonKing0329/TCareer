package com.king.app.tcareer.page.record.complex;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.bean.LineChartData;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/20 13:49
 */
public interface CareerCompareView extends BaseView {

    void showData(List<CompareItem> compareItems);

    void showChart(LineChartData data);
}
