package com.lidehang.national.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author lidehang-IT
 *VPN获取配置文件
 */
public class VPNConstants {
	
	public static Properties props;

	static {
		props = new Properties();
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("VPN.properties"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("VPN.properties文件不存在", e);
		} catch (IOException e) {
			throw new RuntimeException("VPN.properties文件出错", e);
		}
	}

}
