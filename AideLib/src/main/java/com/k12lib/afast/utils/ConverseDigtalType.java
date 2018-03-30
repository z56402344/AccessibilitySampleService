package com.k12lib.afast.utils;

/**
 * 字符串格式转换，增加小数点，转变成double类型
 */
public class ConverseDigtalType {
//	500 0000
	/**
	 * 1>将负号替换成空白字符;2>最后，若有负号，把负号加上去
	 * @param number 数字字符串，可能含正负号，不含小数点
	 * @return
	 */
	public static double conversePoint(String number){
		boolean hasSign = false;
		if (StringUtil.isNotEmpty(number)) {
			if(number.contains("-")){
				hasSign = true;
				number = number.replace("-", "");
			}
			if (Double.parseDouble(number) == 0.0) {//返回数据为0，直接返回0.00
				return 0.00;
			} else {
				String pointNumber=number.substring(number.length()-2, number.length());
				String beforNumber=number.substring(0, number.length()-2);
				String targetNumber=beforNumber+"."+pointNumber;
				return Double.parseDouble(hasSign ? "-"+targetNumber : targetNumber);
			}
		}
		return 0.00;
		
	}
	
	public static long converse(String number){
		return Long.parseLong(number);
	}
}
