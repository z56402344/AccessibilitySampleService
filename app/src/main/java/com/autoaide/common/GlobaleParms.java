package com.autoaide.common;


import com.autoaide.core.FileCenter;

/**
 * 说明：全局的变量
 */
public class GlobaleParms {
    /**
     * 创建数据库所有表的名称
     */
    public static String TABLE_USERINFO = "userInfo";
    public static String TABLE_MSG = "msg";
    public static String TABLE_XT = "xt";

    public static String[] UmengAppKey = {""};
    public static String[] UmengSecret = {""};


    // coderoom.cfg ==> {"url":"http://","debug":true}
    // 调试开关
    public static final boolean isDebug = FileCenter.getJsonKey(FileCenter.loadCfg(), "debug", true);
//	public static final boolean isDebug = true;

    /** 线上环境*/
//	public static final String BaseURL = "";
    /** 预上线环境*/
//	public static final String BaseURL = "";
    /**
     * 测试线环境
     */
//	public static final String BaseURL = "";
    public static String BaseURL = FileCenter.getJsonKey(FileCenter.loadCfg(), "url", "");

    public static final String URL = BaseURL + "";

}
