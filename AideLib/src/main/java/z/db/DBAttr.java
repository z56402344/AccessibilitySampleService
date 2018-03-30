package z.db;

import java.util.HashMap;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public interface DBAttr {
	public static final String TNAME = "t_share";
	public static final String SE = "cse"; // sec name
	public static final String KE = "cke"; // key name
	public static final String TY = "cty"; // type 0=string 1=int 2=float
	public static final String VAL = "cva"; // string value for str/int/float
	public static final int TStr = 0;
	public static final int TInt = 1;
	public static final int TFlo = 2;
	
	public static class Table implements BaseDBHelper.ILTable {
		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "create table " + TNAME + " (" 
					+ SE + " text, " 
					+ KE + " text, "
					+ TY + " int default 0, "
					+ VAL + " text, "
					+ "primary key(" + SE + "," + KE + "))";
			db.execSQL(sql);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
	// 加载Sec信息
	public static class Sec {
		public static SQLiteDatabase getWDB(boolean isUser) {
			return isUser?BaseDBHelper.getWDatabase():BaseDBHelper.getGlobalDatabase();
		}
		private String mName = null;
		private HashMap<String,Object> mAttrs = new HashMap<String,Object>();
		private boolean isUserDB = false;
		public Sec(String sec,boolean isUser) {
			isUserDB = isUser;
			mName = sec;
			load();
		}
		public int getCount() {
			return mAttrs.size();
		}
		public int load() {
			if (mName!=null&&mName.length()>0) {
				return loadSec(isUserDB,mName,mAttrs);
			}
			return 0;
		}
		// 读取sec
		public static int loadSec(boolean isUser,String sec,HashMap<String,Object> attrs) {
			DBQuery dbq = new DBQuery(isUser);
			int count = 0;
			if (dbq.query(TNAME, new String[] {KE,TY,VAL}, SE+"=?", new String[] {sec}, null, null, null, null)) {
				dbq.prepareCols(3).addCol(KE).addCol(TY).addCol(VAL);
				do {
					String key = dbq.getString(0);
					int type = dbq.getInt(1);
					Object val = null;
					switch (type) {
					case TStr:
						val = dbq.getString(2);
						break;
					case TInt:
						val = dbq.getInt(2);
						break;
					case TFlo:
						val = dbq.getFloat(2);
						break;
					}
					attrs.put(key, val);
					++count;
				} while (dbq.next());
			}
			dbq.close();
			return count;
		}
		public static HashMap<String,Object> loadSec(boolean isUser,String sec) {
			HashMap<String,Object> attrs = new HashMap<String,Object>();
			loadSec(isUser,sec,attrs);
			return attrs;
		}
		// 清除sec
		public static void clearSec(boolean isUser,String sec) {
			SQLiteDatabase db = getWDB(isUser);
			try {
				db.delete(TNAME, SE+"=?", new String[] {sec});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 保存sec
		public static void saveSec(boolean isUser,String sec,HashMap<String,Object> attrs) {
			SQLiteDatabase db = getWDB(isUser);
			try {
				db.beginTransaction();
				ContentValues cv = new ContentValues();
				cv.put(SE, sec);
				for (Entry<String,Object> e: attrs.entrySet()) {
					String key = e.getKey();
					cv.put(KE, key);
					Object val = e.getValue();
					if (val instanceof String) {
						cv.put(TY, TStr);
						cv.put(VAL, (String)val);
					} else if (val instanceof Integer) {
						cv.put(TY, TInt);
						cv.put(VAL, (int)(Integer)val);
					} else if (val instanceof Double) {
						cv.put(TY, TFlo);
						cv.put(VAL, (double)(Double)val);
					} else if (val instanceof Float) {
						cv.put(TY, TFlo);
						cv.put(VAL, (double)(Float)val);
					}
					db.insertWithOnConflict(TNAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void save(boolean clearOld) {
			if (clearOld) clearSec(isUserDB,mName);
			if (getCount()==0) return;
			saveSec(isUserDB,mName, mAttrs);
		}
		// 获取属性
		public String getString(String key) {
			Object o = mAttrs.get(key);
			return (o!=null&&o instanceof String)?(String)o:null;
		}
		public int getInt(String key,int def) {
			Object o = mAttrs.get(key);
			return (o!=null&&o instanceof Integer)?(Integer)o:def;
		}
		public int getInt(String key) {
			return getInt(key,0);
		}
		public boolean getBoolean(String key,boolean def) {
			return getInt(key,def?1:0)!=0;
		}
		public boolean getBoolean(String key) {
			return getInt(key)!=0;
		}
		public double getDouble(String key) {
			Object o = mAttrs.get(key);
			return (o!=null&&o instanceof Double)?(Double)o:0;
		}
		// 添加属性
		public void put(String key,String val) {
			mAttrs.put(key, val);
		}
		public void put(String key,int val) {
			mAttrs.put(key, val);
		}
		public void put(String key,boolean val) {
			mAttrs.put(key, val?1:0);
		}
		public void put(String key,double val) {
			mAttrs.put(key, val);
		}
		// 重置sec
		public void setSec(String name) {
			mName = name;
		}
		@Override
		public String toString() {
			return "sec="+mName+",attrs="+mAttrs.toString();
		}
		public void clearAttrs() {
			mAttrs.clear();
		}
		public void clearAttr(String key) {
			mAttrs.remove(key);
		}
	}
	// 直接操作key
	public static class Key {
		// 查询
		public static Object query(boolean isUser,String sec,String key) {
			Object val = null;
			DBQuery dbq = new DBQuery(isUser);
			if (dbq.query(TNAME, new String[] {TY,VAL}, SE+"=? and "+KE+"=?", new String[] {sec,key}, null, null, null, null)) {
				int type = dbq.getInt(TY);
				switch (type) {
				case TStr:
					val = dbq.getString(VAL);
					break;
				case TInt:
					val = dbq.getInt(VAL);
					break;
				case TFlo:
					val = dbq.getDouble(VAL);
					break;
				}
			}
			dbq.close();
			return val;
		}
		public static String loadString(boolean isUser,String sec,String key) {
			return (String)query(isUser,sec,key);
		}
		public static Integer loadInt(boolean isUser,String sec,String key) {
			Object o = query(isUser,sec,key);
			return (o!=null)?(Integer)o:0;
		}
		public static boolean loadBoolean(boolean isUser,String sec,String key) {
			return loadInt(isUser,sec,key)==0?false:true;
		}
		public static Double loadDouble(boolean isUser,String sec,String key) {
			Object o = query(isUser,sec,key);
			return (o!=null)?(Double)o:0;
		}
		// 保存key
		public static ContentValues build(String sec,String key,int type) {
			ContentValues cv = new ContentValues();
			cv.put(SE, sec);
			cv.put(KE, key);
			cv.put(TY, type);
			return cv;
		}
		public static void update(boolean isUser,ContentValues cv) {
			SQLiteDatabase db = Sec.getWDB(isUser);
			try {
				db.insertWithOnConflict(TNAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public static void update(boolean isUser,String sec,String key,String val) {
			ContentValues cv = build(sec, key, TStr);
			cv.put(VAL, val);
			update(isUser,cv);
		}
		public static void update(boolean isUser,String sec,String key,int val) {
			ContentValues cv = build(sec, key, TInt);
			cv.put(VAL, val);
			update(isUser,cv);
		}
		public static void update(boolean isUser,String sec,String key,boolean val) {
			update(isUser,sec,key,val?1:0);
		}
		public static void update(boolean isUser,String sec,String key,double val) {
			ContentValues cv = build(sec, key, TFlo);
			cv.put(VAL, val);
			update(isUser,cv);
		}	
		public static void clear(boolean isUser,String sec,String key) {
			SQLiteDatabase db = Sec.getWDB(isUser);
			try {
				db.delete(TNAME, SE+"=? and "+KE+"=?", new String[]{sec,key});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
