package com.king.app.tcareer.model.dao;

import android.database.Cursor;

import com.king.app.tcareer.model.bean.RecordWinFlagBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/22 16:13
 */
public class RecordExtendDao extends CursorDao {

    public List<RecordWinFlagBean> queryRecordWinnerFlagsByCourt(long userId, String court, boolean thisYear) {
        String sql = "SELECT match_records._id, winner_flag\n" +
                " FROM match_records\n" +
                " JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                " JOIN matches ON match_names.match_id=matches._id\n" +
                " WHERE match_records.user_id=?\n" +
                " AND matches.court=?";
        if (thisYear) {
            sql = sql + " AND date_str like '" + Calendar.getInstance().get(Calendar.YEAR) + "%'";
        }
        String args[] = new String[] {
                String.valueOf(userId), court
        };
        return parseRecordWinFlagBean(getCursor(sql, args));
    }

    public List<RecordWinFlagBean> queryRecordWinnerFlagsByLevel(long userId, String level, boolean thisYear) {
        String sql = "SELECT match_records._id, winner_flag\n" +
                " FROM match_records\n" +
                " JOIN match_names ON match_records.match_name_id=match_names._id\n" +
                " JOIN matches ON match_names.match_id=matches._id\n" +
                " WHERE match_records.user_id=?\n" +
                " AND matches.level=?";
        if (thisYear) {
            sql = sql + " AND date_str like '" + Calendar.getInstance().get(Calendar.YEAR) + "%'";
        }
        String args[] = new String[] {
                String.valueOf(userId), level
        };
        return parseRecordWinFlagBean(getCursor(sql, args));
    }

    private List<RecordWinFlagBean> parseRecordWinFlagBean(Cursor cursor) {
        List<RecordWinFlagBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            RecordWinFlagBean bean = new RecordWinFlagBean();
            bean.setRecordId(cursor.getLong(0));
            bean.setWinnerFlag(cursor.getInt(1));
            list.add(bean);
        }
        return list;
    }

}
