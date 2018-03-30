package z.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

// update values to db
// usage for update multi-values:
// 1. DBUpdate du = new DBUpdate(isUserDB)
// 2. du.init(tabName,idName,idValue)
// 3. du.setAttr(col-1,value-1,false)
//      ...
// 4. du.setAttr(col-N,value-N,true)
// 更新1个属性
// Once.updateAttr(boolean isUser,String tab,String kn,String kv,String vn,String/int/double vv)
public interface DU {
	public static final String EQ = "=?";
	public static final String NEQ = "!=?";
	// 更新多个属性
	public static class DBUpdate {
		private SQLiteDatabase db = null;
		private ContentValues cv = new ContentValues();
		private String keyName,keyVal;
		private String mTab = null;
		private String mCont = EQ;
		private String[] mContArgs = null;

		public DBUpdate(boolean isUserDB) {
			db = isUserDB?BaseDBHelper.getWDatabase():BaseDBHelper.getGlobalDatabase();
		}
		
		public DBUpdate init(String tab,String kn,String kv) {
			mTab = tab; keyName = kn; keyVal = kv;
			return this;
		}
		public DBUpdate setCont(String cont) {
			mCont = cont;
			return this;
		}
		public DBUpdate setContArgs(String[] args) {
			mContArgs = args;
			return this;
		}
		public DBUpdate update() {
			try {
				if (keyName!=null&&keyName.length()>0&&keyVal!=null) {
					db.update(mTab, cv, keyName + mCont, new String[] { keyVal });					
				} else if (mContArgs!=null) {
					// 完全条件
					db.update(mTab, cv, mCont, mContArgs);
				} else {
					db.update(mTab, cv, null, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return this;
		}
		public DBUpdate setAttr(String vn,String vv,boolean bUp) {
			cv.put(vn, vv);
			if (bUp) update();
			return this;
		}
		public DBUpdate setAttr(String vn,int vv,boolean bUp) {
			cv.put(vn, vv);
			if (bUp) update();
			return this;
		}
		public DBUpdate setAttr(String vn,double vv,boolean bUp) {
			cv.put(vn, vv);
			if (bUp) update();
			return this;
		}
		public DBUpdate setAttr(String vn,String vv) {
			cv.put(vn, vv);
			return this;
		}
		public DBUpdate setAttr(String vn,int vv) {
			cv.put(vn, vv);
			return this;
		}
		public DBUpdate setAttr(String vn,double vv) {
			cv.put(vn, vv);
			return this;
		}
		// 设置获取所有属性
		public DBUpdate setAttrs(ContentValues all) {
			cv = all;
			return this;
		}
		public ContentValues getAttrs() {
			return cv;
		}
		// 添加或替换
		public DBUpdate insertOrUpdate() {
			try {
				if (keyName!=null&&keyName.length()>0&&keyVal!=null) {
					// 存在则替换 不存在则插入
					if (DBQuery.has(db, mTab, keyName, keyVal)) {
						db.update(mTab, cv, keyName + mCont, new String[] { keyVal });					
					} else {
                        cv.put(keyName,keyVal); // 需要手动添加条件的参数
						db.insert(mTab, null, cv);
					}
				} else {
					db.update(mTab, cv, null, null);					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return this;
		}
	}

	// 更新一次
	public static class Once {
		// 更新单个属性的快捷封装
		public static void updateAttr(boolean isUser,String tab,String kn,String kv,String vn,String vv) {
			new DBUpdate(isUser).init(tab, kn, kv).setAttr(vn, vv, true);
		}
		public static void updateAttr(boolean isUser,String tab,String kn,String kv,String vn,int vv) {
			new DBUpdate(isUser).init(tab, kn, kv).setAttr(vn, vv, true);
		}
		public static void updateAttr(boolean isUser,String tab,String kn,String kv,String vn,double vv) {
			new DBUpdate(isUser).init(tab, kn, kv).setAttr(vn, vv, true);
		}

		// 删除记录
		public static void delete(boolean isUser,String tab,String kn,String kv) {
			SQLiteDatabase db = isUser?BaseDBHelper.getWDatabase():BaseDBHelper.getGlobalDatabase();
			try {
				if (kn!=null) {
					db.delete(tab, kn+"=?", new String[] {kv});
				} else {
					db.delete(tab, null, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
