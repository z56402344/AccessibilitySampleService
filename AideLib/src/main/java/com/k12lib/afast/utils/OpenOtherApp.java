package com.k12lib.afast.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * 打开第三方应用接口类
 * 
 * @author 
 */
public class OpenOtherApp {
	/**
	 * 进入第三方应用
	 * @param context
	 * @param appPackageName
	 * @param bundle
	 */
	public void openApp(Context context, String appPackageName,String appPackageNam,Bundle bundle) {
		PackageInfo pi = null;
		try {
			pi = context.getPackageManager().getPackageInfo(appPackageName,
					PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		if ((pi.activities != null) && (pi.activities.length > 0)) {
			ActivityInfo ai = pi.activities[0];
			if (ai == null)
				try {
					throw new Exception(appPackageName + "未找到Activity");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			String sName = ai.name;

			Intent intent = new Intent();
			intent.setComponent(new ComponentName(appPackageName, sName));
			intent.putExtra("bundle", bundle);
			context.startActivity(intent);
		}
	}

	/**
	 * 判断第三方应用是否安装
	 * 
	 * @param context
	 * @param appPackageName
	 *            第三方应用包名
	 */
	public boolean isExistApp(Context context, String appPackageName) {
		try {
			context.getPackageManager().getPackageInfo(
					appPackageName, PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {

			e.printStackTrace();
			return false;
		}
		return true;
	}
}
