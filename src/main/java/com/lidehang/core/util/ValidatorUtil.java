package com.lidehang.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字段验证工具
 *
 */
public class ValidatorUtil {
	
	/**
	 * 判断是否为浮点数或者整数
	 * @param str
	 * @return true Or false
	 */
	public static boolean isNumeric(String str){
          Pattern pattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
          Matcher isNum = pattern.matcher(str);
          if( !isNum.matches() ){
                return false;
          }
          return true;
    }
	
	/**
	 * 判断是否为正确的邮件格式
	 * @param str
	 * @return boolean
	 */
	public static boolean isEmail(String str){
		if(isEmpty(str))
			return false;
		return str.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
	}
	
	/**
	 * 判断字符串是否为合法手机号 11位 13 14 15 18开头
	 * @param str
	 * @return boolean
	 */
	public static boolean isMobile(String str){
		if(isEmpty(str))
			return false;
		return str.matches("^(13|14|15|18)\\d{9}$");
	}
	
	/**
	 * 判断字符串是否为合法密码规则：8位以上，由字母和数字混合
	 * @param str
	 * @return boolean
	 */
	public static boolean isPwd(String str){
		if(isEmpty(str)||str.length()<8)
			return false;
		return str.matches("^(([a-z]+[0-9]+)|([0-9]+[a-z]+))[a-z0-9]*$");
	}
	
	/**
	 * 判断验证码是不是6位数字
	 * @param str
	 * @return boolean
	 */
	public static boolean isVcode(String str){
		if(isEmpty(str))
			return false;
		return str.matches("^\\d{6}$");
	}
	
	/**
	 * 判断是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		try{
			Integer.parseInt(str);
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
		
	/**
	 * 判断字符串是否为非空(包含null与"")
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str){
		if(str == null || "".equals(str))
			return false;
		return true;
	}
	
	/**
	 * 判断字符串是否为非空(包含null与"","    ")
	 * @param str
	 * @return
	 */
	public static boolean isNotEmptyIgnoreBlank(String str){
		if(str == null || "".equals(str) || "".equals(str.trim()))
			return false;
		return true;
	}
	
	/**
	 * 判断字符串是否为空(包含null与"")
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str == null || "".equals(str))
			return true;
		return false;
	}
	
	/**
	 * 判断字符串是否为空(包含null与"","    ")
	 * @param str
	 * @return
	 */
	public static boolean isEmptyIgnoreBlank(String str){
		if(str == null || "".equals(str) || "".equals(str.trim()))
			return true;
		return false;
	}
	
	
	//禁止实例化
	private ValidatorUtil(){} 
}
