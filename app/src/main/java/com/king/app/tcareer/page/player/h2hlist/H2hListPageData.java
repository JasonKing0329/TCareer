package com.king.app.tcareer.page.player.h2hlist;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class H2hListPageData {

    private String[] chartContents;
    private Integer[] careerChartWinValues;
    private Integer[] seasonChartWinValues;
    private Integer[] careerChartLoseValues;
    private Integer[] seasonChartLoseValues;

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

}
