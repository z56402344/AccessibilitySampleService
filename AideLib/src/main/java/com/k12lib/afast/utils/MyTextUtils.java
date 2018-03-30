package com.k12lib.afast.utils;

import java.text.DecimalFormat;

import android.text.Editable;

public class MyTextUtils {

	/**
	 * 获得卡号后四位
	 * @param cardno
	 * @return
	 */
	public static String getCardAfterBody(String cardno) {
		if (cardno == null || cardno.length() < 4) {
			return "";
		}
		return cardno.trim().substring(cardno.length() - 4);
	}

	public static double parseDouble(Editable text) {
		return parseDouble(text.toString());
	}
	public static double parseDouble(String number) {
		try {
			return  Double.parseDouble(number);
		} catch (Exception e) {
			return 0.0;
		}
	}
	/**
	 * 获取两个金钱数相加值
	 * @param amount1
	 * @param amount2
	 * @return
	 */
	public static String addMoney(String amount1,String amount2) {
		return parseDouble(amount1) + parseDouble(amount2) + "";
	}
	/**
	 * 获取两个金钱数相减值
	 * @param amount1
	 * @param amount2
	 * @return
	 */
	public static String subMoney(String amount1, String amount2) {
		return parseDouble(amount1) - parseDouble(amount2) + "";
	}

	/**
	 * 小数点后保留指定位数
	 * @param count
	 * @return
	 */
	public static String addZero(String number,int size) {
		return addZero(parseDouble(number), size);
	}
	/**
	 * 小数点后保留指定位数
	 * @param count
	 * @return
	 */
	public static String addZero(double number,int size) {
		StringBuilder builder = new StringBuilder("0.");
		for (int i = 0;i < size;i++) {
			builder.append("0");
		}
		DecimalFormat format = new DecimalFormat(builder.toString());
		return format.format(number);
	}
}
