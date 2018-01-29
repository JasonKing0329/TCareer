package com.king.app.tcareer.base;

import android.app.Application;
import android.os.Build;

import com.king.app.tcareer.model.db.entity.DaoMaster;
import com.king.app.tcareer.model.db.entity.DaoSession;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

public class TApplication extends Application {

	private static TApplication instance;

	private DaoSession daoSession;

	public static int getSDKVersion() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * use number 21 to mark, make codes runs well under android L
	 * @return
	 */
	public static boolean isLollipop() {
		return Build.VERSION.SDK_INT >= 21;//Build.VERSION_CODES.L;
	}

	/**
	 * use number 23 to mark, make codes runs well under android L
	 * @return
	 */
	public static boolean isM() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	public static boolean DEBUG = false;

	public static TApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		instance = this;
		super.onCreate();

		initGreenDao();
	}

	private void initGreenDao() {
		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "khcareer.db");
		Database db = helper.getWritableDb();
		daoSession = new DaoMaster(db).newSession();

		QueryBuilder.LOG_SQL = true;
		QueryBuilder.LOG_VALUES = true;
	}

	public DaoSession getDaoSession() {
		return daoSession;
	}
}
