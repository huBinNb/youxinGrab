package com.lidehang.dataInterface.service;

import java.util.Map;

public interface DataInterfaceHandler {
	/**
	 * 验证输入数据
	 */
	public void verify(Map<String,Object> params);
	
	/**
	 * 获取请求json字符串
	 * @return
	 */
	public Map<String, Object> initMessage(Map<String,Object> params);
	
	/**
	 * 处理返回的字符串
	 * @param data
	 */
	public Object handle(String data,Map<String, Object> map);


}
