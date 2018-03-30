package z.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;

import z.frame.ICommon;

/**
 * 说明： 数据库的帮助类
 */
public abstract class BaseDBHelper implements ICommon {
	public static void dropTable(SQLiteDatabase db,String tab) {
		try {
			db.execSQL(new StringBuilder(64).append("DROP TABLE IF EXISTS ").append(tab).append(';').toString());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static StringBuilder addCol(SQLiteDatabase db,String tab,String colName, int type, boolean defalut, StringBuilder sb) {
		if (sb == null){
			sb = new StringBuilder(128);
		} else {
			sb.setLength(0);
		}
		sb .append("alter table ").append(tab).append(" add column ").append(colName);
		if (type == 0){
			sb.append(" int");
			if (defalut) sb.append(" DEFAULT 0");
		}else if (type == 1){
			sb.append(" text");
			if (defalut) sb.append(" DEFAULT \"\"");
		}
		try {
			db.execSQL(sb.toString());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return sb;
	}
	public interface ILTable {
		/**
		 * 创建数据库时执行的方法
		 */
		void onCreate(SQLiteDatabase db);
		/**
		 * 升级数据库时执行的方法
		 * */
		void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}
	private InnerHelper mUserDB = null; // 用户相关表
	private InnerHelper mGlobalDB = null; // 全局信息表

	/**
	 * 只要是新用户
     * 在tablist中add要创建的表
     * */  
	private ArrayList<ILTable> getTables(boolean isGlobal) {
		ArrayList<ILTable> tl = new ArrayList<ILTable>();
		tl.add(new DBAttr.Table());
		addTables(tl, isGlobal);
		return tl;
	}
	// app层实现不同的数据库表
	protected abstract void addTables(ArrayList<ILTable> tl, boolean isGlobal);
	protected abstract String getUserId();
	protected abstract int getVer(boolean isGlobal);

	private static class InnerHelper extends SQLiteOpenHelper {
	    private BaseDBHelper mBh;
	    private boolean mGlobal = false;
	    public InnerHelper(Context context, String name, CursorFactory factory,int version) {
			super(context, name, factory, version);
		}
		private InnerHelper setGlobal(BaseDBHelper bh,boolean isGlobal) {
			mGlobal = isGlobal;
			mBh = bh;
			return this;
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			ArrayList<ILTable> tl = mBh.getTables(mGlobal);
			for (ILTable table : tl) {
				try {
					table.onCreate(db);
				}catch (Throwable e){
					e.printStackTrace();
				}
			}
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			ArrayList<ILTable> tl = mBh.getTables(mGlobal);
			for (ILTable table : tl) {
				try {
					table.onUpgrade(db, oldVersion, newVersion);
				}catch (Throwable e){
					e.printStackTrace();
				}
			}
		}
    }
	public BaseDBHelper() {
		mCtx = new ContextWrapper(app.ctx) {
			@Override
			public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
				return openOrCreateDatabase(name, mode, factory, null);
			}
			@Override
			public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory, DatabaseErrorHandler errorHandler) {
				try {
					if (Build.VERSION.SDK_INT>=11) {
						return super.openOrCreateDatabase(name,mode,factory,errorHandler);
					} else {
						return super.openOrCreateDatabase(name,mode,factory);
					}
				} catch (SQLiteException e) {
					// 有异常出现 尝试自己打开
				}
				// 尝试以无localized方式打开 避免"zh_CN"错误
				File f = getDatabasePath(name);
				int flags = SQLiteDatabase.CREATE_IF_NECESSARY|SQLiteDatabase.NO_LOCALIZED_COLLATORS;
				if ((mode & MODE_ENABLE_WRITE_AHEAD_LOGGING) != 0) {
					flags |= SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING;
				}
				return (Build.VERSION.SDK_INT>=11)?SQLiteDatabase.openDatabase(f.getPath(), factory, flags, errorHandler)
						:SQLiteDatabase.openDatabase(f.getPath(), factory, flags);
			}
		};
		reOpen();
	}
	
	private SQLiteDatabase mrUserDB = null;
	private SQLiteDatabase mwUserDB = null;
	private SQLiteDatabase mwGlobalDB = null;
	private Context mCtx;
	// 重新初始化 避免用错
	public void reOpen() {
		close();
		mGlobalDB = new InnerHelper(mCtx, "global.db", null, getVer(true)).setGlobal(this,true);
	}
	// 销毁关闭
	public void close() {
		if (mrUserDB!=null) {
			mrUserDB.close();
			mrUserDB = null;
		}
		if (mwUserDB!=null) {
			mwUserDB.close();
			mwUserDB = null;
		}
		if (mUserDB!=null) {
			mUserDB.close();
			mUserDB = null;
		}
		if (mwGlobalDB!=null) {
			mwGlobalDB.close();
			mwGlobalDB = null;
		}
		if (mGlobalDB!=null) {
			mGlobalDB.close();
			mGlobalDB = null;
		}
	}
	private InnerHelper getUserDBH() {
		if (mUserDB==null) {
//			android.util.Log.e("BaseDBHelper", "open:"+userId);
			mUserDB = new InnerHelper(mCtx, getUserId()+".db", null, getVer(false)).setGlobal(this,false);
		}
		return mUserDB;
	}
	// 获取可写db
	public static SQLiteDatabase getWDatabase() {
		BaseDBHelper bd = app.db;
		if (bd.mwUserDB==null) {
			bd.mwUserDB = bd.getUserDBH().getWritableDatabase();
		}
		return bd.mwUserDB;
	}
	// 获取只读db
	public static SQLiteDatabase getRDatabase() {
		BaseDBHelper bd = app.db;
		if (bd.mrUserDB==null) {
			bd.mrUserDB = bd.getUserDBH().getReadableDatabase();
		}
		return bd.mrUserDB;
	}
	// 批量操作
	public static SQLiteDatabase beginBAT() {
		SQLiteDatabase db = getWDatabase();
		db.beginTransaction();
		return db;
	}
	public static SQLiteDatabase endBAT() {
		SQLiteDatabase db = getWDatabase();
		db.setTransactionSuccessful();
		try { // 某些系统可能出现空提交
			db.endTransaction();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return db;
	}
	// 全局数据库
	// 获取可写db
	public static SQLiteDatabase getGlobalDatabase() {
		BaseDBHelper bd = app.db;
		if (bd.mwGlobalDB==null) {
			bd.mwGlobalDB = bd.mGlobalDB.getWritableDatabase();
		}
		return bd.mwGlobalDB;
	}
}
