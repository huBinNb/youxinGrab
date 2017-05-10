package com.lidehang.dataInterface.constant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DataInterfaceUrlConstants {
	
	public static Properties props;

	static {
		props = new Properties();
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("dataInterfaceUrl.properties"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("dataInterfaceUrl.properties文件不存在", e);
		} catch (IOException e) {
			throw new RuntimeException("dataInterfaceUrl.properties文件出错", e);
		}
	}

}
