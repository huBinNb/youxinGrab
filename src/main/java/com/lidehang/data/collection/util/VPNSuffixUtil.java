package com.lidehang.data.collection.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.VPNConstants;

/**
 * VPN后缀自动添加工具
 */
public class VPNSuffixUtil {

	/**
	 * 税号
	 * @param taxNumber
	 * @return
	 */
	public static String getVpnUser(String taxNumber) {
		String  str=taxNumber.substring(2,6);
		String  vpnUser;
		if(str.equals("3301")){
			vpnUser=taxNumber+"@hzgs";
		}else if(str.equals("3302")){
			vpnUser=taxNumber+"@nbgs";
		}else if(str.equals("3303")){
			vpnUser=taxNumber+"@wzgs";
		}else if(str.equals("3304")){
			vpnUser=taxNumber+"@jxgs";
		}else if(str.equals("3305")){
			vpnUser=taxNumber+"@hugs";
		}else if(str.equals("3306")){
			vpnUser=taxNumber+"@sxgs";
		}else if(str.equals("3307")){
			vpnUser=taxNumber+"@jhgs";
		}else if(str.equals("3308")){
			vpnUser=taxNumber+"@qzgs";
		}else if(str.equals("3309")){
			vpnUser=taxNumber+"@zsgs";
		}else if(str.equals("3310")){
			vpnUser=taxNumber+"@tzgs";
		}else{
			vpnUser=taxNumber+"@lsgs";
		}
		return vpnUser;
	}
}
