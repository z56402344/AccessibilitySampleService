package com.k12lib.afast.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.k12lib.afast.log.Logger;


/**
 * 日期工具类
 * @author Duguang
 */
public class CalendarUtils {

	// 这个类不能实例化
	private CalendarUtils() {
		
	}

	/**
	 * 传入两个日期数组,计算两个日期相隔天数
	 * 
	 * @param startDate
	 *            参数示例:[2013,5,12]
	 * @param endDate
	 *            参数示例:[2014,11,2]
	 * @return
	 */
	public static String getHeadway(int[] startDate, int[] endDate) {
		Calendar calendar = Calendar.getInstance();
		
	
		calendar.set(startDate[0], startDate[1] - 1, startDate[2]); // 将日历翻到2013年六月十二日，5表示六月
		long timeOne = calendar.getTimeInMillis();
		calendar.set(endDate[0], endDate[1] - 1, endDate[2]); // 将日历翻到2014年十一月二日,2表示三月
		long timeTwo = calendar.getTimeInMillis();
		long headway = (timeTwo - timeOne) / (1000 * 60 * 60 * 24);
		Logger.d("getHeadway--------->startDate  timeOne  " + startDate[0]+"  "+startDate[1]+"  "+startDate[2]);
		Logger.d("getHeadway--------->endDate timeTwo  " + endDate[0]+"  "+endDate[1]+"  "+endDate[2]);
		return String.valueOf(headway);
	}

	/**
	 * 根据输入的年和月份,获取当月天数
	 * 
	 * @param dyear 
	 * @param dmouth
	 * @return
	 */
	public static int getDayByYearAndMonth(String dyear, String dmouth) {
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM");
		Calendar rightNow = Calendar.getInstance();
		try {
			rightNow.setTime(simpleDate.parse(dyear + "/" + dmouth));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);// 根据年月 获取月份天数
	}

	/**
	 * 比较两个日期
	 * @param oneDate
	 * @param twoDate
	 * @return
	 */
	public static boolean isDateSize(int[] oneDate, int[] twoDate) {
		Calendar oneCalendar = Calendar.getInstance();
		Calendar twoCalendar = Calendar.getInstance();
		oneCalendar.set(oneDate[0], oneDate[1] - 1, oneDate[2]);
		twoCalendar.set(twoDate[0], twoDate[1] - 1, twoDate[2]);
		return twoCalendar.after(oneCalendar);
	}
}
