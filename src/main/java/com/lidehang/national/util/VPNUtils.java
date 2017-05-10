package com.lidehang.national.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author lidehang-IT VPN工具类
 */
public class VPNUtils {

	private static String VPN = "";
	private static String VPN1 = "";
	static {
		VPN = VPNConstants.props.getProperty("VPNName");
		VPN1 = VPNConstants.props.getProperty("VPNName1");
	}
	
	/**
	 * @param userName
	 * @param password
	 *            电脑VPN123名称的VPN进行账号密码登入
	 */
	public static String checkConnectVPN(String userName, String password) {
		String stateCode=null;
		System.out.println("打开VPN+++++++");
		BufferedReader br = null;
		try {
			if(userName.endsWith("hzgs")||userName.endsWith("jxgs")||userName.endsWith("sxgs")||userName.endsWith("hugs")||userName.endsWith("tzgs")){
				Process p = Runtime.getRuntime().exec("rasdial " + VPN + " " + userName + " " + password);
				br = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
				String line = null;
				String info = null;
				while ((line = br.readLine()) != null) {
					info+=line;
			}
				System.out.println(info);
				if(info.indexOf("691")==-1){
					stateCode="VPN:1";
				}else{
					stateCode="VPN:0";
					return stateCode;
				}
				}else{
				Process p = Runtime.getRuntime().exec("rasdial " + VPN1 + " " + userName + " " + password);
				br = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
				String line = null;
				String info = null;
				while ((line = br.readLine()) != null) {
					info+=line;
			}
				System.out.println(info);
			}
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("++++++++VPN连接失败！++++++++");
			stateCode="VPN:0";
		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
//					e.printStackTrace();
					System.out.println("++++++++VPN连接失败！++++++++");
					stateCode="VPN:0";
				}
			}
			
		}
		return stateCode;
	}

	/**
	 * @param userName
	 * @param password
	 *            电脑VPN123名称的VPN进行账号密码登入
	 */
	//public static void ConnectVPN(String userName, String password) {
	public static void ConnectVPN(String userName, String password) {
		System.out.println("打开VPN+++++++");
		BufferedReader br = null;
		try {
			if(userName.endsWith("hzgs")||userName.endsWith("jxgs")||userName.endsWith("sxgs")||userName.endsWith("hugs")||userName.endsWith("tzgs")){
				Process p = Runtime.getRuntime().exec("rasdial " + VPN + " " + userName + " " + password);
				br = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
			}
				}else{
				Process p = Runtime.getRuntime().exec("rasdial " + VPN1 + " " + userName + " " + password);
				br = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
			}
			}
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("++++++++VPN连接失败！++++++++");
		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
//					e.printStackTrace();
					System.out.println("++++++++VPN连接失败！++++++++");
				}
			}
		}
	}

	/**
	 * 电脑VPN123名称的VPN断开连接
	 */
	public static void DisconnectVPN(String userName) {
		System.out.println("关闭VPN-----");
		BufferedReader br = null;
		try {
			if(userName.endsWith("hzgs")||userName.endsWith("jxgs")||userName.endsWith("sxgs")||userName.endsWith("hugs")||userName.endsWith("tzgs")){
				Process p = Runtime.getRuntime().exec("rasphone -h " + VPN);
				br = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
			}else{
				Process p = Runtime.getRuntime().exec("rasphone -h " + VPN1);
				br = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("++++++++VPN断开失败！++++++++");
		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
//					e.printStackTrace();
					System.out.println("++++++++VPN断开失败！++++++++");
				}
			}
		}
	}
}
