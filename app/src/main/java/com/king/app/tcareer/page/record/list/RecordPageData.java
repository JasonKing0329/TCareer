package com.king.app.tcareer.page.record.list;

import com.king.app.tcareer.model.db.entity.Record;

import java.util.List;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class RecordPageData {
    private List<Record> recordList;
    private List<YearItem> yearList;
    private int careerWin;
    private int careerLose;
    private String careerRate;
    private int yearWin;
    private int yearLose;
    private String yearRate;

    public List<YearItem> getYearList() {
        return yearList;
    }

    public void setYearList(List<YearItem> yearList) {
        this.yearList = yearList;
    }

    public int getCareerWin() {
        return careerWin;
    }

    public void setCareerWin(int careerWin) {
        this.careerWin = careerWin;
    }

    public int getCareerLose() {
        return careerLose;
    }

    public void setCareerLose(int careerLose) {
        this.careerLose = careerLose;
    }

    public String getCareerRate() {
        return careerRate;
    }

    public void setCareerRate(String careerRate) {
        this.careerRate = careerRate;
    }

    public int getYearWin() {
        return yearWin;
    }

    public void setYearWin(int yearWin) {
        this.yearWin = yearWin;
    }

    public int getYearLose() {
        return yearLose;
    }

    public void setYearLose(int yearLose) {
        this.yearLose = yearLose;
    }

    public String getYearRate() {
        return yearRate;
    }

    public void setYearRate(String yearRate) {
        this.yearRate = yearRate;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }
}
