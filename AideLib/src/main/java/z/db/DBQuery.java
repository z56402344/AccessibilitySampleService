package z.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.k12lib.afast.log.Logger;

public class DBQuery {
	protected SQLiteDatabase db = null;
	public Cursor cursor = null;
	private boolean misUser = true;
	
	public DBQuery(){
	}
	
	public DBQuery(boolean isUserDB) {
		misUser = isUserDB;
	}

	public boolean open() {
		if (db == null)
		db = misUser?BaseDBHelper.getRDatabase():BaseDBHelper.getGlobalDatabase();
		return db != null;
	}
	public boolean query(String tab,String[] cols,String select,String[] selectArgs,String groupBy,String having,String orderBy,String limit) {
		try {
			if (!open()) return false;
			closeCursor();
			cursor = db.query(tab, cols, select, selectArgs, groupBy, having, orderBy,limit);
			if (cursor==null) return false;
			return cursor.moveToNext();
		} catch (Exception e) {
			Logger.e("DBQuery", e.getMessage());
		}
		return false;
	}
		
	public void closeCursor() {
		if (cursor!=null) {
			cursor.close();
			cursor = null;
		}
	}
	public void closeDB() {
//		if (db!=null) {
//			db.close();
//			db = null;
//		}
	}
	public void close() {
		closeCursor();
		closeDB();
	}
	
	// 适宜单词获取属性
	public int getInt(String col) {
		return cursor.getInt(cursor.getColumnIndex(col));
	}
	public long getLong(String col) {
		return cursor.getLong(cursor.getColumnIndex(col));
	}
	public String getString(String col) {
		return cursor.getString(cursor.getColumnIndex(col));
	}
	public float getFloat(String col) {
		return cursor.getFloat(cursor.getColumnIndex(col));
	}
	public double getDouble(String col) {
		return cursor.getDouble(cursor.getColumnIndex(col));
	}
	
	// 对于批量获取列表时 建议优化如下
	public class ColIdxCnt {
		public int mCur = 0;
		public ColIdxCnt(int maxCol) {
			mCols = new int[maxCol]; 
		}
		public ColIdxCnt addCol(String col) {
			return setCol(mCur++,col);
		}
		public ColIdxCnt setCol(int idx,String col) {
			mCols[idx] = cursor.getColumnIndex(col);
			return this;
		}
	}
	public int[] mCols = null;
	public ColIdxCnt prepareCols(int maxCol) {
		return new ColIdxCnt(maxCol);
	}
	public int getInt(int col) {
		return cursor.getInt(mCols[col]);
	}
	public long getLong(int col) {
		return cursor.getLong(mCols[col]);
	}
	public String getString(int col) {
		return cursor.getString(mCols[col]);
	}
	public double getFloat(int col) {
		return cursor.getDouble(mCols[col]);
	}
	// 跳到下一条记录
	public boolean next() {
		return cursor.moveToNext();
	}

	public int getCount() {
		return cursor.getCount();
	}
	
	//  查询有没有存在
	public static boolean has(SQLiteDatabase db,String tab,String kn,String kv) {
		if (kv==null) kv = "";
		boolean bHas = false;
		Cursor cursor = null;
		if (kn!=null) {
			cursor = db.query(tab, null, kn+"=?", new String[]{kv}, null, null, null, null);			
		} else {
			cursor = db.query(tab, null, null, null, null, null, null, null);
		}
		if (cursor!=null) {
			bHas = cursor.moveToNext();
			cursor.close();
		}
		return bHas;
	}
}
