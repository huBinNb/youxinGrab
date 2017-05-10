package com.lidehang.dataInterface.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.lidehang.core.util.HttpUtil;
import com.lidehang.core.util.SpringBeanUtil;
import com.lidehang.dataInterface.constant.DataInterfaceUrlConstants;
import com.lidehang.dataInterface.model.constant.DataHandlerType;

/**
 * 请求数据
 * 
 * @author guoxing
 *
 */
@Service
public class RequestDataHome {
	/**
	 * 
	 */
	public Object handleRequest(DataHandlerType handlerType, Map<String, Object> params) {
		DataInterfaceHandler handler = null;
		try {
			handler = (DataInterfaceHandler) SpringBeanUtil.getBeanByName(handlerType.getId()+"Handler");
		} catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
			//抛出一个自定义异常
		}
		
		//验证输入参数
		handler.verify(params);
		
		//获取请求报文
		Map<String, Object> req_data = handler.initMessage(params);
		//获取请求URL
		String url = DataInterfaceUrlConstants.props.getProperty(handlerType.getId());
		
		//请求数据接口
		String data = HttpUtil.sendGet(url, req_data);
		//处理返回的报文
		return handler.handle(data,params);
	}
	
}
