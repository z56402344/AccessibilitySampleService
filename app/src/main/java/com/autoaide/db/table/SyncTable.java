package com.autoaide.db.table;

import android.database.sqlite.SQLiteDatabase;

import z.db.BaseDBHelper;

/**
 * 说明： 离线同步信息
 */
public class SyncTable implements BaseDBHelper.ILTable {

	//{name id 金币数 时常, refresh_token, 是否设置了兴趣} xxx.txt
	/** 表名 */  
    public static final String TNAME = "t_sync";  
    /** 记录id */  
    public static final String ID = "id";
    /** 时间戳 */  
    public static final String TimeStamp = "ts";
    // 类型
    public static final String Type = "tp";
    // 状态 0=未开始 1=进行中 2=完成
    public static final String Status = "st";
    // 数值列
    public static final String V_I1 = "i1"; // int
    public static final String V_I2 = "i2"; // int
    public static final String V_S1 = "s1";
    public static final String V_S2 = "s2";
	// 添加重试次数及扩展字段
	public static final String Retry = "rt"; // 非网络原因失败次数
	public static final String Extra = "ex"; // 扩展字段

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sb = new StringBuffer(320)
				.append("create table ").append(TNAME).append(" (")
				.append(ID).append(" integer primary key autoincrement,")
		        .append(TimeStamp).append(" text,")
				.append(Type).append(" int DEFAULT 0,")
				.append(Status).append(" int DEFAULT 0,")
				.append(V_I1).append(" int DEFAULT 0,")
				.append(V_I2).append(" int DEFAULT 0,")
				.append(Retry).append(" int DEFAULT 0,")
				.append(V_S1).append(" text,")
				.append(V_S2).append(" text,")
				.append(Extra).append(" text default \"\");");
        db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
