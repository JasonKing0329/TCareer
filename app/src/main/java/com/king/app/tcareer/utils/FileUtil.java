package com.king.app.tcareer.utils;

import android.database.sqlite.SQLiteDatabase;

import com.king.app.tcareer.base.TApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 14:29
 */
public class FileUtil {

    /**
     * 从assets目录复制的方法
     * @param dbFile
     */
    public static void copyDbFromAssets(String dbFile) {

        SQLiteDatabase db = null;
        //先检查是否存在，不存在才复制
        String dbPath = TApplication.getInstance().getFilesDir().getParent() + "/databases";
        try {
            db = SQLiteDatabase.openDatabase(dbPath + "/" + dbFile
                    , null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            db = null;
        }
        if (db == null) {
            try {
                InputStream assetsIn = TApplication.getInstance().getAssets().open(dbFile);
                File file = new File(dbPath);
                if (!file.exists()) {
                    file.mkdir();
                }
                OutputStream fileOut = new FileOutputStream(dbPath + "/" + dbFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = assetsIn.read(buffer))>0){
                    fileOut.write(buffer, 0, length);
                }

                fileOut.flush();
                fileOut.close();
                assetsIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (db != null) {
            db.close();
        }
    }

}
