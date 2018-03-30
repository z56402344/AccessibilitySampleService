package com.k12lib.afast.utils;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.k12lib.afast.log.Logger;

/**
 * 网络相关工具类
 * @author Administrator
 *
 */
public class NetUtil {
	private static Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");

	private static Context mContext;
	public static NetState CUR_NETSTATE = NetState.Mobile;

	public enum NetState {
		Mobile, WIFI, NOWAY
	}

	private String mApn; // 接入点名称

	private String mPort; // 端口号

	private String mProxy; // 代理服务器

	private boolean mUseWap; // 是否正在使用WAP

	/**
	 * 获得当前设置的APN相关参数
	 * 
	 * @param context
	 */
	private void checkApn2(Context context) {
		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = PREFERRED_APN_URI;
		String[] apnInfo = new String[3];
		apnInfo[0] = "apn";
		apnInfo[1] = "proxy";
		apnInfo[2] = "port";

		Cursor cursor = contentResolver.query(uri, apnInfo, null, null, null);
		if (cursor != null) {
			while (cursor.moveToFirst()) {
				this.mApn = cursor.getString(cursor.getColumnIndex("apn"));
				this.mProxy = cursor.getString(cursor.getColumnIndex("proxy"));
				this.mPort = cursor.getString(cursor.getColumnIndex("port"));

				// 代理为空
				// if ((this.mProxy == null) || (this.mProxy.length() <= 0)) {
				if (!StringUtil.isNullOrEmpty(this.mApn)) {
					String apn = this.mApn.toUpperCase();

					// 中国移动WAP设置：APN：CMWAP；代理：10.0.0.172；端口：80
					// 中国联通WAP设置：APN：UNIWAP；代理：10.0.0.172；端口：80
					// 中国联通WAP设置（3G）：APN：3GWAP；代理：10.0.0.172；端口：80
					if ((apn.equals("CMWAP")) || (apn.equals("UNIWAP"))
							|| (apn.equals("3GWAP"))) {
						this.mUseWap = true;
						this.mProxy = "10.0.0.172";
						this.mPort = "80";
						// break;
						return;
					}

					// 中国电信WAP设置：APN(或者接入点名称)：CTWAP；代理：10.0.0.200；端口：80
					if (apn.equals("CTWAP")) {
						this.mUseWap = true;
						this.mProxy = "10.0.0.200";
						this.mPort = "80";
						// break;
						return;
					}
				}
				if (!StringUtil.isNullOrEmpty(this.mProxy)) {
					String proxy = this.mProxy;
					if ((proxy.equals("10.0.0.172"))
							|| (proxy.equals("10.0.0.200"))) {
						this.mUseWap = true;
						return;
					}
				}

				// }
				/*
				 * this.mPort = "80"; this.mUseWap = true;
				 */
				break;
				// return;
			}

		}

		this.mUseWap = false;
		cursor.close();
	}

	public String getApn() {
		return this.mApn;
	}

	public String getProxy() {
		return this.mProxy;
	}

	public String getProxyPort() {
		return this.mPort;
	}

	public boolean isWapNetwork() {
		return this.mUseWap;
	}

	private void checkApn(Context context) {
		String proxy = Proxy.getDefaultHost();
		int port = Proxy.getDefaultPort() == -1 ? 80 : Proxy.getDefaultPort();
		if (!StringUtil.isNullOrEmpty(proxy)) {
			this.mUseWap = true;
		} else {
			this.mUseWap = false;
			return;
		}
	}
	
	/**
	 * 检查当前的网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNet(Context context) {
		// 获取到wifi和mobile连接方式
		boolean wifiConnection = isWIFIConnection(context);
		boolean mobileConnection = isMobileConnection(context);

		if (wifiConnection == false && mobileConnection == false) {
			// 如果都不能连接
			// 提示用户设置当前网络——跳转到设置界面
			return false;
		}

		// if(mobileConnection){
		// // mobile:apn ip和端口
		// // 有部分手机：10.0.0.172 010.000.000.172 80
		// // ContentProvier——ContentReserver
		// setApn(context);
		// }
		return true;
	}

	/**
	 * 读取apn设置信息获取到ip 端口
	 */
	private static Object[] setApn(Context context) {
		try {
			ContentResolver resolver = context.getContentResolver();
			Cursor query = resolver
					.query(PREFERRED_APN_URI, null, null, null, null);
			if (query != null && query.moveToNext()) {
				Object[] res = new Object[] {
						query.getString(query.getColumnIndex("proxy")),
						query.getInt(query.getColumnIndex("port"))
				};
				query.close();
				return res;
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 判断wifi是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWIFIConnection(Context context) {
		if (context==null) return false;
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null)
			return networkInfo.isConnected();
		return false;
	}

	// 判断network是否已连接
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Logger.i("**** newwork is off");
			return false;
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info == null) {
				Logger.i("**** newwork is off");
				return false;
			} else {
				if (info.isAvailable()) {
					Logger.i("**** newwork is on");
					return true;
				}
			}
		}
		Logger.i("**** newwork is off");
		return false;
	}

	/**
	 * 获取手机的mac地址
	 *
	 * @param context
	 *            上下文对象
	 * @return
	 */
	public static String getMacInfo(Context context) {
		String result = null;
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (wifiManager == null ? null : wifiManager
				.getConnectionInfo());
		if (info != null) {
			result = info.getMacAddress();
		}
		Logger.e("mac", result);
		return result;
	}

	/**
	 * 判断apn
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnection(Context context) {
		if (context==null) return false;
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null)
			return networkInfo.isConnected();
		return false;

	}

	/**
	 * 跳转到网络设置界面
	 * 
	 * @param activity
	 */
	public static void showNetSetting(Activity activity) {
		Intent intent = new Intent(Settings.ACTION_SETTINGS);
		intent.addCategory("android.intent.category.DEFAULT");
		activity.startActivity(intent);
	}

	public static void showAPNSetting(Activity activity) {
		Intent intent = new Intent(Settings.ACTION_APN_SETTINGS);
		intent.addCategory("android.intent.category.DEFAULT");
		activity.startActivity(intent);
	}

	public class NetStateReceive extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mContext = context;
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent
					.getAction())) {
				WifiManager wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifiManager.getConnectionInfo();
				if (!wifiManager.isWifiEnabled() || -1 == info.getNetworkId()) {
					CUR_NETSTATE = NetState.Mobile;
				}
			}
		}
	}

//	/**
//	 * 获取 当前apn并返回httphost对象
//	 *
//	 * @return
//	 */
//	public static HttpHost getAPN() {
//		HttpHost proxy = null;
//		Uri uri = Uri.parse("content://telephony/carriers/preferapn");
//		Cursor mCursor = null;
//		if (null != mContext) {
//			mCursor = mContext.getContentResolver().query(uri, null, null,
//					null, null);
//		}
//		if (mCursor != null && mCursor.moveToFirst()) {
//			// 游标移至第一条记录，当然也只有一条
//			String proxyStr = mCursor
//					.getString(mCursor.getColumnIndex("proxy"));
//			if (proxyStr != null && proxyStr.trim().length() > 0) {
//				proxy = new HttpHost(proxyStr, 80);
//			}
//			mCursor.close();
//		}
//		return proxy;
//	}

	/**
	 * 获取网络状态 2G,3G,WIFI等
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetState(Context context) {
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		State wifi = connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();

		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			return 3;// wifi
		}
		if (mobile == State.CONNECTED || mobile == State.CONNECTING) {

			NetworkInfo info = connect.getActiveNetworkInfo();
			if (info != null) {
				if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
					if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS
							|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA
							|| info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
						return 2;// 2G网络
					}
				} else {
					return 1;// 3g
				}

			}
		}
		return 4;// Other
	}

}
