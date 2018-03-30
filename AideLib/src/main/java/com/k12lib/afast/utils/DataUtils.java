package com.k12lib.afast.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Data(数据工具类)
 */
public class DataUtils {
	
	/**
	 * 通过获取流中的数据，转成字节数组
	 */
	public static byte[] getData(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len;
		byte[] buf = new byte[1024];
		while ((len = is.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		is.close();
		baos.close();
		byte[] b = baos.toByteArray();
		return b;
	}

	/**
	 * 
	 * @param number
	 * @return double类型转换成String,保留两位小数点
	 */
	public static String format(double number) {
		if (number == 0.0) {
			return "0.00";
		}
		String amount = Double.toString(number);
		try {
			NumberFormat format = NumberFormat
					.getCurrencyInstance(Locale.CHINA);

			if (number >= 0.0) {
				return format.format(Double.parseDouble(amount.toString()))
						.substring(1);
			} else {
				return "-"
						+ format.format(Double.parseDouble(amount.toString()))
								.substring(2);
			}
		} catch (Exception e) {
			return amount.toString();
		}
	}
	
	/**
	 * 
	 * @param number
	 * @return double类型转换成String,保留两位小数点
	 */
	public static String formatdot(Double number) {
		if (number != null) {
			DecimalFormat df = new DecimalFormat("#0.00");
			return df.format(number);
		}
		return "";
	}

	/**
	 * 增加逗号，小数点，小数点后两位
	 * @param doubleValue
	 * @return
	 */
	public static String amtFormat(double doubleValue) {
		if (doubleValue == 0.0) {
			return "0.00";
		}
		String amount = Double.toString(doubleValue);
		try {
			NumberFormat format = NumberFormat
					.getCurrencyInstance(Locale.CHINA);

			if (doubleValue >= 0.0) {
				return format.format(Double.parseDouble(amount.toString()))
						.substring(1);
			} else {
				return "-"
						+ format.format(Double.parseDouble(amount.toString()))
								.substring(2);
			}
		} catch (Exception e) {
			return amount.toString();
		}
	}
	
	/**
	 * 格式化数据
	 * "xfjf":"00000000000100.00" --->100
	 * @param str
	 * @return
	 */
	public static String strFormat(String str){
		String valueOf = null;
		try {
			int parseInt = Integer.parseInt(str.split("\\.")[0]);
			valueOf = String.valueOf(parseInt);
		} catch (Exception e) {
			return valueOf;
		}
		
		return valueOf;
	}
	
	/**
	 * 字符串转整型
	 * @param str
	 * @return
	 */
	public static int intFormat(String str){
		int parseInt = 0;
		try {
			parseInt = Integer.parseInt(str);
		} catch (Exception e) {
			return parseInt;
		}
		
		return parseInt;
	}
	
	/**
	 * 将每三个数字加上逗号处理（通常使用金额方面的编辑）
	 * @param str
	 * @return
	 */
	private static String addComma(String str) {  
	     // 将传进数字反转  
	     String reverseStr = new StringBuilder(str).reverse().toString();  
	    String strTemp = "";  
	     for (int i=0; i<reverseStr.length(); i++) {  
	        if (i*3+3 > reverseStr.length()) {  
	            strTemp += reverseStr.substring(i*3, reverseStr.length());  
	            break;  
	         }  
	         strTemp += reverseStr.substring(i*3, i*3+3)+",";  
	     }  
	     // 将 【789,456,】 中最后一个【,】去除  
	     if (strTemp.endsWith(",")) {  
	         strTemp = strTemp.substring(0, strTemp.length()-1);  
	    }  
	     // 将数字重新反转  
	     String resultStr = new StringBuilder(strTemp).reverse().toString();  
	     return resultStr;  
	 } 

	

	/**
	 * 固定长 12 位，右对齐，左补零
	 * @param number
	 * @return number转换成12位
	 */
	public static String formatToLongString(Double number) {
		if (number != null) {
			DecimalFormat df = new DecimalFormat("#0.00");
			String num = df.format(number);
			String[] splits = num.split("\\.");
			int ilen = 10 - splits[0].length();
			StringBuffer num1 = new StringBuffer();
			while (num1.length() != ilen) {
				num1.append("0");
			}
			return num = num1.toString() + splits[0] + splits[1];
		}
		return "";
	}
	/**
	 * 固定长 10 位，右对齐，左补零
	 * 
	 * @param number
	 * @return number转换成10位
	 */
	public static String formatTo10LongString(Double number) {
		return number == null ? "" : String.format("%010d",
				(int) (number * 100));
	}
	/**
	 * 比较大小，如果num1小于num2,返回false
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static boolean compare(String num1, String num2) {
		int compare = Double.compare(Double.parseDouble(num1),
				Double.parseDouble(num2));
		if (compare <= 0)
			return false;
		else
			return true;
	}
	
}
