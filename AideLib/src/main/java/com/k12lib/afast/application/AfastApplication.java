package com.k12lib.afast.application;

import z.image.universal_image_loader.cache.disc.naming.Md5FileNameGenerator;
import z.image.universal_image_loader.cache.memory.impl.WeakMemoryCache;
import z.image.universal_image_loader.core.ImageLoader;
import z.image.universal_image_loader.core.ImageLoaderConfiguration;
import android.app.Application;

import com.k12lib.afast.log.ILogger;
import com.k12lib.afast.log.Logger;
import com.k12lib.afast.log.PrintToLogCatLogger;

public class AfastApplication extends Application {
	private static AfastApplication application;
	// 实例化PrintToFileLogger，将Log日志写入文件
	private ILogger fileLogger;

	@Override
	public void onCreate() {
		super.onCreate();
		AfastApplication.application = this;
		
		// 在Appliction里面设置我们的异常处理器为UncaughtExceptionHandler处理器
//		CrashHandler handler = CrashHandler.getInstance();
//		handler.init(getApplicationContext()); 
		
		initImageLoader();

		// 默认的Log日志是打印到Logcat中的，添加写入文件的Logger
		fileLogger = getLogger();
		Logger.addLogger(fileLogger);
	}

	/**
	 * 初始化ImageLoader
	 */
	protected void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(1024 * 1024 * 15)
				// 1.5 Mb
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.memoryCache(new WeakMemoryCache()).build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 获取静态Application(上下文)的方法
	 * 
	 * @return
	 */
	public static AfastApplication getApplication() {
		return application;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		// 移除写入文件的Logger
		Logger.removeLogger(fileLogger);
	};

	/**
	 * 获取LogCat上的日志
	 * 
	 * @return
	 */
	public ILogger getLogger() {
		return new PrintToLogCatLogger();
	}
}
