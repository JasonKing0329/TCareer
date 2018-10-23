package com.king.app.tcareer.page.rank;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.bean.LineChartData;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/8 14:03
 */
public interface RankWeekView extends BaseView {

    void postShowUser(String nameEng);

    void showChart(LineChartData data);
}
