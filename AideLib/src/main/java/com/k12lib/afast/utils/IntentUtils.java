package com.k12lib.afast.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 常用Intent调用
 */
public class IntentUtils {
	/**
	 * 打开网络设置页面
	 * @param context
	 */
	public static void openNetSetting(Context context) {
		Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
		intent.addCategory("android.intent.category.DEFAULT");
		context.startActivity(intent);
	}
	/**
	 * 安装应用
	 * @param context
	 * @param apkfile apk路径
	 */
	public static void installApk(Context context,String apkPath) {
		installApk(context, new File(apkPath));
	}	
	/**
	 * 安装应用
	 * @param context
	 * @param apkfile apk路径
	 */
	public static void installApk(Context context,File apkfile) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");   
		context.startActivity(intent);	
	}	
	/**
	 * 拨打电话 显示按键页面
	 * @param context
	 * @param number 电话号码
	 */
	public static void callPhoneDail(Context context,String number) {
		Uri uri = Uri.parse("tel:" + number);
		Intent intent = new Intent(Intent.ACTION_DIAL,uri);
		context.startActivity(intent);
	}
	/**
	 * 直接拨打电话
	 * @param context
	 * @param number 电话号码
	 */
	public static void callPhone(Context context,String number) {
		Uri uri = Uri.parse("tel:" + number);
		Intent intent = new Intent(Intent.ACTION_CALL,uri);
		context.startActivity(intent);
	}
	/**
	 * 调用浏览器打开一个网址
	 * @param context
	 * @param url 网址
	 */
	public static void openBrowser(Context context,String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setData(Uri.parse(url));
		context.startActivity(intent);
	}
	/**
	 * 发送邮件页面
	 * @param context
	 * @param emailAddr  邮箱地址
	 */
	public static void sendEmail(Context context,String emailAddr) {
		openBrowser(context, emailAddr);
	}
	/**
	 * 回到桌面
	 */
	public static void backToLauncher(Context context) {
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		context.startActivity(i);
	}
	
	/**
	 * 给应用评分
	 * @param context
	 */
	public static void startScore(Context context){
		try {
			Uri uri = Uri.parse("market://details?id="+context.getPackageName());  
//			Uri uri = Uri.parse("market://details?id="+"com.talk51.dasheng");
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);  
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			context.startActivity(intent);  
		} catch (Exception e) {
			ToastUtils.showShort(context, "您的手机没有安装应用市场");
		}
		
	}
}
