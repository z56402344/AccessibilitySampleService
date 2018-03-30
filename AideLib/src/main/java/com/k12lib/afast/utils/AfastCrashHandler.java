package com.k12lib.afast.utils;

import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 全局未捕获异常处理类
 */
public class AfastCrashHandler implements UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
	private static AfastCrashHandler INSTANCE = new AfastCrashHandler();// CrashHandler实例
	private String crashInfo;
	private Context context;
	private OnCrashedListener listerner = null;

	/** 保证只有一个CrashHandler实例 */
	private AfastCrashHandler() {

	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static AfastCrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 */
	public void init(Context context) {
		this.context = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
		Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
	}
	
	/**
	 * 设置异常捕获监听函数
	 * @param listerner
	 */
	public void setOnCrashedListener(OnCrashedListener listerner){
		this.listerner = listerner;
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处理
	 */
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果自定义的没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);// 如果处理了，让程序继续运行3秒再退出，保证文件保存并上传到服务器
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 错误处理方法.
	 * 
	 * @param ex
	 *            异常信息
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	public boolean handleException(Throwable ex) {
		if (ex == null)
			return false;
		if(this.listerner != null){
			crashInfo = getCrashInfo(ex);
			listerner.onCrashed(context,crashInfo);
		}
		return true;
	}

	
	/**
	 * 获取异常信息
	 * @param ex
	 * @return errormessage
	 */
	private String getCrashInfo(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		//循环着把所有的异常信息写入writer中
		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();//记得关闭
		String result = writer.toString();
		sb.append(result);
		return sb.toString();
	}
	
	/**
	 * 异常捕获监听接口
	 * @author supered_yang
	 *
	 */
	public interface OnCrashedListener{
		public void onCrashed(Context context, String crashInfo);
	};
}
