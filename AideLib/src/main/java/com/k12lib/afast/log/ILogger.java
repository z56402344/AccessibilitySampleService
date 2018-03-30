package com.k12lib.afast.log;

import java.io.File;

/**
 * 日志的接口
 * @author 
 *
 */
public interface ILogger {
	void v(String tag, String message);

	void d(String tag, String message);

	void i(String tag, String message);

	void w(String tag, String message);

	void e(String tag, String message);

	void open();

	void close();

	void println(int priority, String tag, String message);

	File getFreshFile();
}
