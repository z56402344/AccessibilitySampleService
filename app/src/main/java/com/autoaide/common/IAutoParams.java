package com.autoaide.common;


import z.db.ShareDB;

// 和设备相关的固定参数
public interface IAutoParams {
	public static final String kSec = "autoparams";
	public static final String kVerS = "versionName"; // 版本号
	public static final String kVerI = "versionCode"; // 版本数字
	public static final String kScrW = "width"; // 屏幕宽
	public static final String kScrH = "height"; // 屏幕高
	public static final String kViewH = "vheight"; // 窗口高 去掉状态栏
	public static final String kViewW = kScrW; // 窗口宽
	public static final String kDensity = "density"; // 屏幕密度
	public static final String kVendor = "vendor"; // 渠道
	public static final String kDevId = "deviceid"; // 设备号
	public static final String kPushId = "push"; // 个推id
	public static final String kQQ = "qq"; // qq群
	public static final String kSystemVer = "systemVer"; // 系统版本

	public static class Sec {
		private Sec() {}
		public static int loadInt(String key) {
			return ShareDB.Key.loadInt(kSec,key);
		}
		public static float loadFloat(String key) {
			return (float)(double) ShareDB.Key.loadDouble(kSec,key);
		}
		public static String loadString(String key) {
			return ShareDB.Key.loadString(kSec,key);
		}
		public static void update(String key,int val) {
			ShareDB.Key.update(kSec,key,val);
		}
		public static void update(String key,String val) {
			ShareDB.Key.update(kSec,key,val);
		}
		public static void update(String key,float val) {
			ShareDB.Key.update(kSec,key,(double)val);
		}
	}
}
