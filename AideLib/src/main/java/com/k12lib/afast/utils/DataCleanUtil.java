package com.k12lib.afast.utils;

import java.io.File;

import com.k12lib.afast.log.Logger;

import android.content.Context;
import android.os.Environment;

/** * 本应用数据清除管理器 */
public class DataCleanUtil {

	/** * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context */
	public static void cleanInternalCache(Context context) {
		deleteFilesByDirectory(context.getCacheDir());
	}

	/** * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context */
	public static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/databases"));
	}

	/**
	 * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
	 * context
	 */
	public static void cleanSharedPreference(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/shared_prefs"));
	}

	/** * 按名字清除本应用数据库 * * @param context * @param dbName */
	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	/** * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context */
	public static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	/**
	 * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
	 * context
	 */
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	/** * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath */
	public static void cleanCustomCache(String filePath) {
		deleteFilesByDirectory(new File(filePath));
	}

	/** * 清除本应用所有的数据 * * @param context * @param filepath */
	public static void cleanApplicationData(Context context, String... filepath) {
		cleanInternalCache(context);
		cleanExternalCache(context);
		cleanDatabases(context);
		cleanSharedPreference(context);
		cleanFiles(context);
		for (String filePath : filepath) {
			cleanCustomCache(filePath);
		}
	}

	/** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	/**
	 * 递归删除指定路径下的所有文件
	 * @param file
	 */
	public static void deleteAll(File file) {
		if (file == null) return;
		if (file.isFile()) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			if (files != null){
				for (File f : files) {
					deleteAll(f);// 递归删除每一个文件
					f.delete();// 删除该文件夹
				}
			}
			file.delete();
		}
	}

    /**
     * 删除对应后缀名称的文件
     * @param f
     */
	public static void deleteAllFiles(File f) {
		if (f == null || !f.exists()) return;
		File[] files = f.listFiles();
		if(files != null) {
			for(File file : files)
				if(file.isDirectory()) {
					deleteAllFiles(file);
					try {
						file.delete(); //删除目录下的所有文件后，该目录变成了空目录，可直接删除
						Logger.i("成功删除了，名字为： " + file.getName());
					}catch (Exception e){
						e.printStackTrace();
					}
				} else if(file.isFile()) {
					try {
						file.delete(); //删除目录下的所有文件后，该目录变成了空目录，可直接删除
						Logger.i("成功删除了，名字为： " + file.getName());
					}catch (Exception e){
						e.printStackTrace();
					}
				}
		}
		File[] files2 = f.listFiles();
		if(files2==null || files2.length == 0){
			try {
				f.delete(); //删除目录下的所有文件后，该目录变成了空目录，可直接删除
			}catch (Exception e){
				e.printStackTrace();
			}
		}

	   Logger.i("成功删除了，名字为： " + f.getName());
    }

}
