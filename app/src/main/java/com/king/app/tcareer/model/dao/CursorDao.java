package com.king.app.tcareer.model.dao;

import android.database.Cursor;

import com.king.app.tcareer.base.TApplication;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/22 16:13
 */
public abstract class CursorDao {

    protected Cursor getCursor(String sql, String[] args) {
        if (args == null) {
            args = new String[]{};
        }
        return TApplication.getInstance().getDaoSession().getDatabase()
                .rawQuery(sql, args);
    }

}
