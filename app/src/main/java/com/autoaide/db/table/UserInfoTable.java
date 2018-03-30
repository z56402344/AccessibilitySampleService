package com.autoaide.db.table;

import android.database.sqlite.SQLiteDatabase;

import com.autoaide.common.GlobaleParms;

import z.db.BaseDBHelper;

public interface UserInfoTable {
	/** 表名 */
	String TNAME = GlobaleParms.TABLE_USERINFO;
	String TOKEN = "token";
	String USER_ID = "student_no";
	String iS_BUY = "is_buy";

	class Table implements BaseDBHelper.ILTable {
		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuffer sb = new StringBuffer(128);
			sb.append("create table ").append(TNAME).append('(');
			sb.append(TOKEN).append(" text default \"\", ");
			sb.append(USER_ID).append(" text default \"\", ");
			sb.append(iS_BUY).append(" int default \"\"");
			sb.append(")");
	        db.execSQL(sb.toString());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
