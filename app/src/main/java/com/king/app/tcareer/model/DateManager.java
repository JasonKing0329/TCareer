package com.king.app.tcareer.model;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/30 10:33
 */
public class DateManager {

    private int nYearStart;
    private int nMonthStart;
    private int nDayStart;

    private Date mDate;

    private String dateStr;

    private SimpleDateFormat dateFormat;

    public DateManager() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void setDate(Date date) {
        mDate = date;
        dateStr = dateFormat.format(date);
    }

    public String getDateStr() {
        return dateStr;
    }

    public Date getDate() {
        return mDate;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void pickDate(Context context, OnDateListener listener) {
        if (nYearStart == 0) {
            Calendar calendar = Calendar.getInstance();
            nYearStart = calendar.get(Calendar.YEAR);
            nMonthStart= calendar.get(Calendar.MONTH);
            nDayStart = 1;
        }

        DatePickerDialog startDlg = new DatePickerDialog(context,
                (view, year, monthOfYear, dayOfMonth) -> {
                    nYearStart = year;
                    nMonthStart = monthOfYear;//日期控件的月份是从0开始编号的
                    nDayStart = dayOfMonth;
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(nYearStart).append("-");
                    buffer.append(nMonthStart + 1 < 10 ? "0" + (nMonthStart + 1) : (nMonthStart + 1)).append("-");
                    buffer.append(nDayStart < 10 ? "0" + nDayStart : nDayStart);
                    dateStr = buffer.toString();
                    try {
                        mDate = dateFormat.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (listener != null) {
                        listener.onDateSet();
                    }
                }, nYearStart, nMonthStart, nDayStart);
        startDlg.show();
    }

    public void reset() {
        mDate = null;
        dateStr = null;
        nDayStart = 0;
        nMonthStart = 0;
        nYearStart = 0;
    }

    public interface OnDateListener {
        void onDateSet();
    }
}
