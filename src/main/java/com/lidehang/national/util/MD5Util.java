package com.lidehang.national.util;


import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.commons.lang3.StringUtils;

/**
 * MD5加密
 * @author Hobn
 *
 */
public class MD5Util {
	public static String MD5(String s) {
		if (StringUtils.isEmpty(s)) {
			return s;
		}
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static MessageDigest md = null;

	public static String md5Encrypt(String input) {
		try {
			if (md == null) {
				md = MessageDigest.getInstance("MD5");
			}
			byte buffer[] = input.getBytes();
			md.update(buffer);
			byte bDigest[] = md.digest();
			md.reset();
			BigInteger bi = new BigInteger(1, bDigest);
			return bi.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(MD5("88065212Zl"));
		
	}
}
