package com.lidehang.core.util;

public class UserPwdUtil {
	public static String encrypt(String password){
		
		return SHAUtil.shaEncode(password);
	}
	
}
