package com.k12lib.afast.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.content.Context;

import com.k12lib.afast.log.Logger;

/**
 * 数据格式工具类
 * @author 
 */
public class MathUtil {
	
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2)
				+ Math.abs(y1 - y2) * Math.abs(y1 - y2));
	}
 
	public static double pointTotoDegrees(double x, double y) {
		return Math.toDegrees(Math.atan2(x, y));
	}

	/**
	 * 将用户真实姓名，前面加*
	 * 
	 * @param context
	 * @param strRealName
	 * @return
	 */
	public static String formatStarRealName(Context context, String strRealName) {
		String star = "**";
		if (!StringUtil.isNullOrEmpty(strRealName)) {
			if (strRealName.length() >= 2) {
				return star + strRealName.substring(1, strRealName.length());
			} else {
				return star;
			}

		} else {
			return star;
		}
	}

	/**
	 * 返回定义好的卡号样式，尾号后四位
	 * 
	 * @param context
	 * @param strCardNo
	 * @return
	 */
	public static String formatCardNoLatterFour(Context context,
			String strCardNo) {
		if (!StringUtil.isNullOrEmpty(strCardNo)) {
			return "尾号 "
					+ strCardNo.substring(strCardNo.length() - 4,
							strCardNo.length());
		} else {
			return "尾号 ";
		}
	}
	
	/**
	 * 返回定义好的卡号样式，尾号后四位加括号，用于首页欠款信用卡列表显示
	 * 
	 * @param context
	 * @param strCardNo
	 * @return
	 */
	public static String formatCardNoLatterFourDebit(Context context,
			String strCardNo) {
		if (!StringUtil.isNullOrEmpty(strCardNo)) {
			return "尾号 "
					+ "("
					+ strCardNo.substring(strCardNo.length() - 4,
							strCardNo.length()) + ")";
		} else {
			return " ";
		}
	}

	public static String formatPointDoubleString(String strAccount) {
		if (!StringUtil.isNullOrEmpty(strAccount)) {
			// 如果有负号，先去掉负号
			DecimalFormat df = new DecimalFormat("######0.00");
			strAccount = StringUtil.removeSign(strAccount);
			Double dbPoint = Double.parseDouble(strAccount);
			return String.valueOf(df.format(dbPoint / 100));
		} else {
			return "0.00";
		}
	}

	/**
	 * 格式化传输金额格式
	 * 
	 * 例如：*123.456*--格式化为-->12345 注：*代表还有多位
	 * 
	 * @param money
	 * @return
	 */
	public static String formatMoneyString(String money) {

		String moneyNow = "000000000000";
		try {
			// 输入为null或者""时返回12个零
			if (money == null || "".equals(money.trim()))
				return moneyNow;
			// 判断当输入金额没有小数点时，获取所有数据，左补12个零，从右截取12位
			String[] moneys = money.split("\\.");
			if (moneys.length < 2) {
				moneyNow += moneys[0] + "00";
				return moneyNow.substring(moneyNow.length() - 12);
			}
			// 有小数点时，删除小数点，返回12位数据
			String temp = moneys[1];
			if (temp.length() > 1) {
				temp = temp.substring(0, 2);
			} else {
				temp = temp + "00";
				temp = temp.substring(0, 2);
			}
			moneyNow += moneys[0] + temp;

			return moneyNow.substring(moneyNow.length() - 12);
		} catch (Exception e) {
			Logger.d(e.toString() + "" + e.getMessage());
		}
		return "000000000000";
	}
	
	public static double formatPointDouble(String strAccount){
		if(!StringUtil.isNullOrEmpty(strAccount)){
			//如果有负号，先去掉负号
			strAccount = StringUtil.removeSign(strAccount);
			Double dbPoint = Double.parseDouble(strAccount);
			return dbPoint / 100;
		} else {
			return 0.00;
		}
	}

	/**
	 * 将卡号整理为后四位为数字，前面均为*，并且以空格分开
	 * @param strAccount 卡号
	 * @return
	 */
	public static String showLastFourNo(String strAccount) {
		String strResult = "";
		if (!StringUtil.isEmpty(strAccount)) {
			if (strAccount.trim().length() == 15) {//卡号为15位，显示如 **** **** *** 1111
				strResult += "**** **** *** "
						+ strAccount.substring(strAccount.length() - 4, strAccount.length());
			} else if (strAccount.trim().length() == 16) {//卡号为16位，显示如 **** **** **** 1111
				strResult += "**** **** **** "
						+ strAccount.substring(strAccount.length() - 4, strAccount.length());
			} else if (strAccount.trim().length() == 19) {//卡号为19位，显示如 **** **** **** *** 1111
				strResult += "**** **** **** *** "
						+ strAccount.substring(strAccount.length() - 4, strAccount.length());
			}
		}
		return strResult;
	}
	
	


	/**
	 * 
	 * @param beginTime
	 *            //"yyyyMMdd"格式 如 20131022
	 * @param sepDate
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCompareDate(String beginTime, int sepDate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// 输入日期的格式
		Date date1 = null;

		try {
			date1 = simpleDateFormat.parse(beginTime);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GregorianCalendar cal1 = new GregorianCalendar();
		cal1.setTime(date1);

		double dateCount = cal1.getTimeInMillis() - sepDate
				* (1000 * 3600 * 24);// 从间隔毫秒变成间隔天数
		simpleDateFormat.format(dateCount);
		return simpleDateFormat.format(dateCount);

	}

	@SuppressLint("SimpleDateFormat")
	public static int getCompareDateCount(String beginTime, String endTime) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// 输入日期的格式
		Date dateBegin = null;

		try {
			dateBegin = simpleDateFormat.parse(beginTime);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Date dateEnd = null;

		try {
			dateEnd = simpleDateFormat.parse(endTime);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GregorianCalendar cal1 = new GregorianCalendar();
		GregorianCalendar cal2 = new GregorianCalendar();
		cal1.setTime(dateBegin);
		cal2.setTime(dateEnd);
		double dayCount = (cal2.getTimeInMillis() - cal1.getTimeInMillis())
				/ (1000 * 3600 * 24);// 从间隔毫秒变成间隔天数
		return (int) dayCount;

	}
	
	
	public static double division(int num,double money){
		if (0 == num)
			throw new RuntimeException(
					"The num must bigger than zero");
		double target = money/num;
		BigDecimal   b   =   new   BigDecimal(target);  
		double   d   =   b.setScale(3,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
		d = Math.ceil(d*100)/100;
		return d;
	}

	/**
	 * 随机指定范围内N个不重复的数
	 * @param min 指定范围最小值
	 * @param max 指定范围最大值
	 * @param n 随机数个数
	 * @return 随机数数组
	 */
	public static int[] randomCommon(int min, int max, int n) {
		if (n > (max - min + 1) || max < min) {
			return null;
		}
		int[] result = new int[n];
		int count = 0;
		while (count < n) {
			int num = (int) (Math.random() * (max - min)) + min;
			boolean flag = true;
			for (int j = 0; j < n; j++) {
				if (num == result[j]) {
					flag = false;
					break;
				}
			}
			if (flag) {
				result[count] = num;
				count++;
			}
		}
		return result;
	}

}
