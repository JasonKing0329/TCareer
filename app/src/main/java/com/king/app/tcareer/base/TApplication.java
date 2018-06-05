package com.king.app.tcareer.base;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.king.app.tcareer.BuildConfig;
import com.king.app.tcareer.conf.AppConfig;
import com.king.app.tcareer.model.db.entity.DaoMaster;
import com.king.app.tcareer.model.db.entity.DaoSession;
import com.king.app.tcareer.model.db.entity.EarlierAchieveDao;
import com.king.app.tcareer.model.db.entity.PlayerAtpBeanDao;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.utils.DebugLog;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

public class TApplication extends Application {

	private static TApplication instance;

	private DaoSession daoSession;
	private THelper helper;

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
		if (!BuildConfig.disableLeakCanary) {
			if (LeakCanary.isInAnalyzerProcess(this)) {
				// This process is dedicated to LeakCanary for heap analysis.
				// You should not init your app in this process.
				return;
			}
			LeakCanary.install(this);
		}
	}

	/**
	 * 程序初始化使用外置数据库
	 * 需要由外部调用，如果在onCreate里直接初始化会创建新的数据库
	 */
	public void createGreenDao() {
		helper = new THelper(getApplicationContext(), AppConfig.DB_NAME);
		Database db = helper.getWritableDb();
		daoSession = new DaoMaster(db).newSession();

		QueryBuilder.LOG_SQL = true;
		QueryBuilder.LOG_VALUES = true;
	}

	public void reCreateGreenDao() {
		daoSession.clear();
		helper.close();
		createGreenDao();
	}

	public DaoSession getDaoSession() {
		return daoSession;
	}

	public static class THelper extends DaoMaster.OpenHelper {

		public THelper(Context context, String name) {
			super(context, name);
		}

		public THelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
			super(context, name, factory);
		}

		@Override
		public void onUpgrade(Database db, int oldVersion, int newVersion) {
			DebugLog.e(" oldVersion=" + oldVersion + ", newVersion=" + newVersion);
			switch (oldVersion) {
				case 1:
					EarlierAchieveDao.createTable(db, true);
				case 2:
					RankWeekDao.createTable(db, true);
				case 3:
					PlayerAtpBeanDao.createTable(db, true);
					db.execSQL("ALTER TABLE " + UserDao.TABLENAME + " ADD COLUMN " + UserDao.Properties.AtpId.columnName + " TEXT;");
					db.execSQL("ALTER TABLE " + PlayerBeanDao.TABLENAME + " ADD COLUMN " + PlayerBeanDao.Properties.AtpId.columnName + " TEXT;");
					break;
			}
		}
	}
}
