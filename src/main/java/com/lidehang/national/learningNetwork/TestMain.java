package com.lidehang.national.learningNetwork;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.util.TaxConstants;

//对接口进行测试  
public class TestMain {
	private String url = "https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check";
	private String charset = "utf-8";
	private HttpClientUtil httpClientUtil = null;
	public TestMain() {
		httpClientUtil = new HttpClientUtil();
	}
	public void test() {
		HttpClientBuilder builder = HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy());// 利用LaxRedirectStrategy处理POST重定向问题
		CloseableHttpClient httpclient = builder.build();
		String response = TaxConstants.getMes(httpclient,url);
		Document doc = Jsoup.parse(response);
		Elements lt = doc.getElementsByAttributeValue("name", "lt");
		Elements _eventId = doc.getElementsByAttributeValue("name", "_eventId");
		Elements submit = doc.getElementsByAttributeValue("name", "submit");
		String httpOrgCreateTest = "https://account.chsi.com.cn" + doc.getElementById("fm1").attr("action");
		Map<String, String> createMap = new HashMap<String, String>();
		createMap.put("username", "330682199010014436");
		createMap.put("password", "1990hu1001");
		createMap.put("lt", lt.get(0).val());
		createMap.put("_eventId", _eventId.get(0).val());
		createMap.put("submit", submit.get(0).val());
		response = httpClientUtil.doPost(httpclient, httpOrgCreateTest, createMap, charset);
	    response=TaxConstants.getMes(httpclient, "https://my.chsi.com.cn/archive/index.action");
	    response=TaxConstants.getMes(httpclient, "https://my.chsi.com.cn/archive/gdjy/xj/show.action");
//	    new SchoolQuery().getInformation(httpclient);
	 //   new EducationQuery().getInformation(httpclient);
	    
	    
	}
	
	
	
	public static void main(String[] args) {
		TestMain main = new TestMain();
		main.test();
	}
}