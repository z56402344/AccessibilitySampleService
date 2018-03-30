package com.k12lib.afast.log.util;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.k12lib.afast.log.Logger;
import com.k12lib.afast.log.common.AndroidVersionCheckUtils;

/**
 * 缓存的工具类,Android 2.2以上版本使用
 * @author 
 *
 */
public class ExternalOverFroyoUtils {
	/**
	 * 判断是否存在外部存储设备
	 * 
	 * @return 如果不存在返回false
	 */
	public static boolean hasExternalStorage() {
		Boolean externalStorage = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		Logger.i("externalStorage = " + externalStorage);
		return externalStorage;
	}

	/**
	 * 获取目录使用的空间大小
	 * 
	 * @param path
	 *            检查的路径路径
	 * @return 在字节的可用空间
	 */
	@SuppressLint("NewApi")
	public static long getUsableSpace(File path) {
		if (AndroidVersionCheckUtils.hasGingerbread()) {
			return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	/**
	 * 检查如果外部存储器是内置的或是可移动的。
	 * 
	 * @return 如果外部存储是可移动的(就像一个SD卡)返回为 true,否则false。
	 */
	@SuppressLint("NewApi")
	public static boolean isExternalStorageRemovable() {
		if (AndroidVersionCheckUtils.hasGingerbread()) {
			Logger.i("isRemovable --" + Environment.isExternalStorageRemovable());
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	/**
	 * 一个散列方法,改变一个字符串(如URL)到一个散列适合使用作为一个磁盘文件名。
	 */
	public static String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 获得外部应用程序缓存目录
	 * 
	 * @param context
	 *            上下文信息
	 * @return 外部缓存目录
	 */
	public static File getExternalSdCardDir(Context context) {
		if (AndroidVersionCheckUtils.hasFroyo()) {
			Logger.i("filesDir ---" + context.getExternalFilesDir(null));
			return context.getExternalFilesDir(null);
		}
		final String filesDir = "/Android/data/" + context.getPackageName() + "/files/";
		File file = new File(Environment.getExternalStorageDirectory().getPath() + filesDir);
		Logger.i("fill is null ------" + file.getPath());
		return file;
	}

	/**
	 * 得到一个可用的缓存目录(如果外部可用使用外部,否则内部)。
	 * 
	 * @param context
	 *            上下文信息
	 * @param uniqueName
	 *            目录名字
	 * @return 返回目录名字
	 */
	public static File getDiskSdCardDir(Context context, String uniqueName) {
		// 检查是否安装或存储媒体是内置的,如果是这样,试着使用
		// 外部缓存 目录
		// 否则使用内部缓存 目录
		Logger.i("storage --" + Environment.getExternalStorageDirectory() + ","
				+ Environment.getExternalStorageState() + "," + context.getFilesDir().getPath()
				+ "," + isExternalStorageRemovable());
		final String cachePath = hasExternalStorage() || !isExternalStorageRemovable() ? 
				getExternalSdCardDir(context).getPath()
				: context.getFilesDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 得到一个可用的缓存目录(如果外部可用使用外部,否则内部)。
	 * 
	 * @param context
	 *            上下文信息
	 * @param uniqueName
	 *            目录名字
	 * @return 返回目录名字
	 */
	public static File getSystemDiskFilesDir(Context context) {
		// 检查是否安装或存储媒体是内置的,如果是这样,试着使用
		// 外部缓存 目录
		// 否则使用内部缓存 目录
		final String cachePath = hasExternalStorage() || !isExternalStorageRemovable() ? getExternalSdCardDir(
				context).getPath()
				: context.getFilesDir().getPath();

		return new File(cachePath);
	}

	public static int getMemoryClass(Context context) {
		return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
				.getMemoryClass();
	}

}
