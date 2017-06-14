package com.lidehang.data.collection.service;

import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.lidehang.data.collection.constant.SiteStatus;
import com.lidehang.data.collection.exception.SiteLoginFailedException;
import com.lidehang.data.collection.exception.SiteNotLoginException;
import com.lidehang.national.util.TaxConstants;

public abstract class SiteHandler<P,M extends ModuleHandler> {

	protected CloseableHttpClient client;

	private List<M> moduleHandlerList;
	
	public P params;

	
	public final void init(P params,List<M> list) {
		moduleHandlerList = list;
		this.params = params;
	}

	/**
	 * 抓取前的准备，如连接VPN
	 */
	protected abstract void begin();
	

	/**
	 * 检测vpn
	 */
	protected abstract String checkVPN();

	/**
	 * 处理网站登录
	 * 
	 * @return
	 * @throws SiteLoginFailedException
	 */
	protected abstract String login() throws SiteLoginFailedException;
	
	/**
	 * 处理登录操作
	 * 
	 * @return
	 * @throws SiteLoginFailedException
	 */
	protected abstract String checkLogin() throws SiteLoginFailedException;

	/**
	 * 异常过滤器，判断请求后返回的页面是否正常
	 * 
	 * @param html
	 * @throws SiteNotLoginException
	 */
	protected abstract void exceptionFilter(String html) throws SiteNotLoginException;

	/**
	 * 抓取完毕后操作，如断开VPN
	 */
	protected abstract void end();

	public String getPage(String url) throws SiteLoginFailedException {
		String html = TaxConstants.getMes(client, url);
		try {
			exceptionFilter(html);
		} catch (SiteNotLoginException e) {
			login();
		}
		return html;
	}
	
	public String postPage(String url,List<BasicNameValuePair> values) throws SiteLoginFailedException {
		String html = TaxConstants.postMes(client, url,values);
		try {
			exceptionFilter(html);
		} catch (SiteNotLoginException e) {
			login();
		}
		return html;
	}

	/**
	 * 执行查询操作
	 */
	public String doGet() {
		String stateCode=null;
		begin();

		try {
			stateCode=login();
			if(stateCode==null){
			  if (moduleHandlerList != null && moduleHandlerList.size() > 0) {
				for (M moduleHandler : moduleHandlerList) {
					SiteStatus ret = moduleHandler.start(this);
					if (ret == SiteStatus.fail) {
						// TODO 记录日志
						break;
					}
					// TODO 记录已获取
				}
				stateCode="success";
			 }
			}
		} catch (SiteLoginFailedException e) {
			e.printStackTrace();
		}
		end();
		return stateCode;
	}
	/**
	 * 执行登录操作  先判断vpn是否正确，错误返回VPN:0  正确1向下执行 再判断登录账户密码是否正确 正确返回1 错误返回0
	 */
	public String doCheck() {
		String stateCode=null;
		stateCode=checkVPN();
		if(stateCode.startsWith("VPN:登入失败")){
			return stateCode;
		}else{
		try {
			stateCode=checkLogin();
		} catch (SiteLoginFailedException e) {
			e.printStackTrace();
		}
		}
		end();
		return stateCode;
	}
}
