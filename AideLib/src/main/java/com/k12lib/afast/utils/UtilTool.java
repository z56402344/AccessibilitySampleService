package com.k12lib.afast.utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * 封装一些常用的工具类
 */
public class UtilTool {

	private final static String FORMAT = "yyyy-MM-dd HH:mm:ss";
	private final static String SHORT_FORMAT = "yyyy-MM-dd";
	private final static SimpleDateFormat sf2 = new SimpleDateFormat(FORMAT);
	private final static SimpleDateFormat shortSf = new SimpleDateFormat(
			SHORT_FORMAT);
	private final static SimpleDateFormat sfLong = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss:SS");

	private static SimpleDateFormat sf;

	private static long day = 24 * 60 * 60 * 1000;
	private static long hour = 60 * 60 * 1000;
	private static long minute = 60 * 1000;
	private static long second = 1000;

	private static long lastClickTime;

	public static boolean isNull(String str) {
		if (str == null || "".equals(str) || "null".equals(str)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isExtNull(List list) {
		if (list == null || list.isEmpty() || list.size() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isExtNull(String[] list) {
		if (list == null || list.length == 0) {
			return true;
		}
		return false;
	}

	public static boolean JsonIsNull(String array) {
		if (UtilTool.isNull(array) || "null".equals(array)) {
			return true;
		}
		return false;
	}

	public static boolean isExtJSONArray(JSONArray array) {
		if ("null".equals(array) || array == null || array.length() <= 0) {
			return true;
		}
		return false;
	}

	public static String toString(Object obj) {
		if (obj != null && !"".equals(obj)) {
			return obj.toString();
		}
		return "";
	}

	public static String toNoNullString(Object obj) {
		if (obj != null && !"".equals(obj) && !"null".equals(obj)
				&& !"Null".equals(obj) && !"NULL".equals(obj)) {
			return obj.toString();
		}
		return "";
	}

	public static String toUTF_8String(String obj) {
		if (obj != null && !"".equals(obj)) {
			try {
				obj = new String(obj.getBytes("ISO8859_1"), "UTF-8");
				return obj;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "";
			}
		}
		return "";
	}

	public static double toDouble(Object obj) {
		if (obj != null && !"".equals(obj)) {
			return Double.parseDouble(obj.toString());
		}
		return 0.0;
	}

	public static float toFloat(Object obj) {
		if (obj != null && !"".equals(obj)) {
			return Float.parseFloat(obj.toString());
		}
		return 0.0f;
	}

	public static int toInteger(Object obj) {
		if (obj != null && !"".equals(obj)) {
			return Integer.parseInt(obj.toString());
		}
		return 0;
	}

	public static long toLong(Object obj) {
		if (obj != null && !"".equals(obj)) {
			return Long.parseLong(obj.toString());
		}
		return 0;
	}

	public static String getTwitterTime(String nowTime, String dateTime) {
		Date now = null;
		Date date = null;
		String twitterTime = "";
		try {
			if (!isNull(nowTime)) {
				now = sf2.parse(nowTime);
			}
			if (!isNull(dateTime)) {
				date = sf2.parse(dateTime);
			}
			if (now != null && date != null) {
				long nowT = now.getTime();
				long dateT = date.getTime();
				long time = nowT - dateT;
				long show = 0;
				if (time >= day) {
					if (dateTime.length() > 5)
						twitterTime = dateTime.substring(5);
				} else if (time >= hour) {
					show = time / hour;
					twitterTime = show + "小时前";
				} else if (time >= minute) {
					show = time / minute;
					twitterTime = show + "分钟前";
				} else {
					show = time / second;
					if (show < 10) {
						show = 10;
					} else {
						show = (show / 10) * 10;
					}
					twitterTime = show + "秒前";
				}
			} else if (date != null) {
				if (dateTime.length() > 5)
					twitterTime = dateTime.substring(5);
			}
		} catch (Exception e) {
			e.printStackTrace();
			twitterTime = "";
		}
		return twitterTime;
	}

	public static String formatShortDate(Date date) {

		try {
			if (date != null) {
				return shortSf.format(date);
			}
		} catch (Exception e) {
		}
		return "";

	}

	public static String formatDateTime(Date date, String format) {

		try {
			if (date != null) {
				sf = new SimpleDateFormat(format);
				return sf.format(date);
			}
		} catch (Exception e) {
		}
		return "";

	}

	public static Date fomatStringToDate(String date, String format) {
		if (date != null && !date.equals("")) {
			try {
				sf = new SimpleDateFormat(format);
				return sf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String formatDateTime(Date date) {

		try {
			if (date != null) {
				return sf2.format(date);
			}
		} catch (Exception e) {
		}
		return "";

	}

	public static String formatLongDateTime(Date date) {

		try {
			if (date != null) {
				return sfLong.format(date);
			}
		} catch (Exception e) {
		}
		return "";

	}

	public static Date fomatStringToDate(String date) {
		if (date != null && !date.equals("")) {
			try {
				return sf2.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String formatNumber(Number num) {

		try {
			if (num != null) {
				DecimalFormat df = new DecimalFormat("0.00");
				return df.format(num);
			}
		} catch (Exception e) {

		}
		return "";

	}

	public static boolean isNumeric(String str) {
		if (!UtilTool.isNull(str)) {
			Pattern pattern = Pattern.compile("[0-9]*");
			return pattern.matcher(str).matches();
		}
		return false;
	}

	public static boolean isCharacter(String str) {
		if (!UtilTool.isNull(str)) {
			Pattern pattern = Pattern.compile("[a-zA-Z]*");
			return pattern.matcher(str).matches();
		}
		return false;
	}

	/**
	 * 二进制第n位为1、其余位为0的数字
	 * 
	 * @param n
	 *            第n位
	 * @return 二进制第n位为1的数字
	 */
	private static int IBS(int n) {
		return 0x01 << (n - 1);
	}

	/**
	 * 将x二进制的第n位设为1
	 * 
	 * @param x
	 *            数字
	 * @param n
	 *            第n位
	 * @return 将x转为二进制的第n位设为1后的新数字
	 */
	public static int Set_N_To_1(int x, int n) {
		return x |= IBS(n);
	}

	/**
	 * 将x二进制的第n位设为0
	 * 
	 * @param x
	 *            数字
	 * @param n
	 *            第n位
	 * @return 将x转为二进制的第n位设为0后的新数字
	 */
	public static int Clear_N_To_0(int x, int n) {
		return x &= ~IBS(n);
	}

	/**
	 * 得到x二进制的第n位是0还是1
	 * 
	 * @param x
	 *            数字
	 * @param n
	 *            第n位
	 * @return x转为二进制的第n位
	 */
	public static int See_N_To_01(int x, int n) {
		return (x >> (n - 1)) & 1;
	}

	public static double getChangePCT(double price, double pctPrice) {
		double closePrice = price - pctPrice;
		if (price == 0 || closePrice == 0) {
			return 0.0;
		}
		double changePCT = pctPrice * 100 / closePrice;
		String pct = formatNumber(changePCT);
		if (!isNull(pct)) {
			return Double.parseDouble(pct);
		} else {
			return 0.0;
		}
	}

	public static boolean isEmail(String strEmail) {
		String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			String id = tm.getDeviceId();
			if (UtilTool.isNull(id) || id.equals("000000000000000")) {
				id = getMAC(context);
			}
			return id != null ? id : "unknown";
		} else {
			return "unknown";
		}
	}

	public static String getPhoneNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return toString(tm.getLine1Number());
	}

	public static String getMAC(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return toString(info.getMacAddress());
	}

	public static String Now() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		return formatter.format(curDate);
	}

	public static String format(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(time);
		return formatter.format(curDate);
	}

	public static Date formatHttpDate(String time) {
		Date curDate = null;
		if (!isNull(time)) {
			time = time.replace("/Date(", "").replace(")/", "");
			curDate = new Date(Long.parseLong(time));
		}
		return curDate;
	}

	public static String formatHttpDateString(String time) {
		if (!isNull(time)) {
			SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);
			time = time.replace("/Date(", "").replace(")/", "");
			Date curDate = new Date(Long.parseLong(time));
			return formatter.format(curDate);
		}
		return "";
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static String toBigImageUrl(String imageUrl) {
		if (!isNull(imageUrl)) {
			if (imageUrl.contains(".sinaimg.cn")) {
				imageUrl = imageUrl.replace("/thumbnail/", "/bmiddle/");
			} else {
				imageUrl = imageUrl.replace("_s.", "_b.");
			}
		}
		return imageUrl;
	}

	/**
	 * 判断是否是连续点击
	 * 
	 * @return 布尔类型
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 1000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 按字节计算字符串的长度
	 * 
	 * @param value
	 *            需要计算的字符串
	 * @return 长度
	 */
	public static int validateLength(String value) {
		if (isNull(value)) {
			return 0;
		}
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 2;
			} else {
				valueLength += 1;
			}
		}
		return valueLength;
	}

	/**
	 * 按字节长度截取字符串
	 * 
	 * @param str
	 *            需要截取的字符串
	 * @param len
	 *            要截取的字节长度,一个中文字符为2,一个英文字符为1
	 * @return 截取后的字符串
	 */
	public static String getSubString(String str, int len) {
		if (str == null || "".equals(str)) {
			return "";
		}
		try {
			int counterOfDoubleByte = 0;
			byte[] b = str.getBytes("gb2312");
			if (b.length <= len)
				return str;
			for (int i = 0; i < len; i++) {
				if (b[i] < 0) {
					counterOfDoubleByte++;
				}
			}
			if (counterOfDoubleByte % 2 == 0)
				return new String(b, 0, len, "gb2312");
			else
				return new String(b, 0, len - 1, "gb2312");
		} catch (Exception ex) {
			return "";
		}
	}
	

}
