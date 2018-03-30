package com.k12lib.afast.log.common;

import android.os.Build;

/**
 * 用于多版本兼容检测
 * @author 
 */
public class AndroidVersionCheckUtils {
	private AndroidVersionCheckUtils() {
	};

	/**
	 * 当前Android系统版本是否在（ Donut） Android 1.6或以上
	 * 
	 * @return
	 */
	public static boolean hasDonut() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT;
	}

	/**
	 * 当前Android系统版本是否在（ Eclair） Android 2.0或 以上
	 * 
	 * @return
	 */
	public static boolean hasEclair() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR;
	}

	/**
	 * 当前Android系统版本是否在（ Froyo） Android 2.2或 Android 2.2以上
	 * 
	 * @return
	 */
	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	/**
	 * 当前Android系统版本是否在（ Gingerbread） Android 2.3x或 Android 2.3x 以上
	 * 
	 * @return
	 */
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	/**
	 * 当前Android系统版本是否在（ Honeycomb） Android3.1或 Android3.1以上
	 * 
	 * @return
	 */
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= 11;
	}

	/**
	 * 当前Android系统版本是否在（ HoneycombMR1） Android3.1.1或 Android3.1.1以上
	 * 
	 * @return
	 */
	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= 12;
	}

	/**
	 * 当前Android系统版本是否在（ IceCreamSandwich） Android4.0或 Android4.0以上
	 * 
	 * @return
	 */
	public static boolean hasIcecreamsandwich() {
		return Build.VERSION.SDK_INT >= 14;
	}

	/**
	 * 当前Android系统版本是否在（ IceCreamSandwich） Android4.1.2或 Android4.1.2以上
	 * 
	 * @return
	 */
	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= 16; 
	}
	 
}
