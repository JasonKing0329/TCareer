package com.king.app.tcareer.page.player.h2hlist;

import com.king.app.tcareer.model.bean.H2hBean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class H2hListPageData {

    private List<H2hBean> headerList;

    private String[] chartContents;
    private Integer[] careerChartWinValues;
    private Integer[] seasonChartWinValues;
    private Integer[] careerChartLoseValues;
    private Integer[] seasonChartLoseValues;

    private List<H2hBean> showList;

    public String[] getChartContents() {
        return chartContents;
    }

    public void setChartContents(String[] chartContents) {
        this.chartContents = chartContents;
    }

    public Integer[] getCareerChartWinValues() {
        return careerChartWinValues;
    }

    public void setCareerChartWinValues(Integer[] careerChartWinValues) {
        this.careerChartWinValues = careerChartWinValues;
    }

    public Integer[] getSeasonChartWinValues() {
        return seasonChartWinValues;
    }

    public void setSeasonChartWinValues(Integer[] seasonChartWinValues) {
        this.seasonChartWinValues = seasonChartWinValues;
    }

    public Integer[] getCareerChartLoseValues() {
        return careerChartLoseValues;
    }

    public void setCareerChartLoseValues(Integer[] careerChartLoseValues) {
        this.careerChartLoseValues = careerChartLoseValues;
    }

    public Integer[] getSeasonChartLoseValues() {
        return seasonChartLoseValues;
    }

    public void setSeasonChartLoseValues(Integer[] seasonChartLoseValues) {
        this.seasonChartLoseValues = seasonChartLoseValues;
    }

    public List<H2hBean> getShowList() {
        return showList;
    }

    public void setShowList(List<H2hBean> showList) {
        this.showList = showList;
    }

    public List<H2hBean> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<H2hBean> headerList) {
        this.headerList = headerList;
    }
}
