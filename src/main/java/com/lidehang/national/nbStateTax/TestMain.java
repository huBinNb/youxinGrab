package com.lidehang.national.nbStateTax;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.httpsUtil.SSLClient;
import com.lidehang.national.util.TaxConstants;

//宁波国税
public class TestMain {
    private String charset = "utf-8";
	private HttpClientUtil httpClientUtil = null;
	public TestMain() {
		httpClientUtil = new HttpClientUtil();
	}
	public void test() {
		String path="https://wsbs.nb-n-tax.gov.cn";
		HttpClientBuilder builder = HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy());  //重定向
		CloseableHttpClient httpclient = builder.build();
		try {
			httpclient=new SSLClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String response =httpClientUtil.doGet222(httpclient,path+"/login.html", charset);
		Map<String, String> dynamicPwdMap=new HashMap<>();
		dynamicPwdMap.put("user", "91330206MA281H1U1G");
		dynamicPwdMap.put("phone", "13588030641");
		response=httpClientUtil.doPost(httpclient, path+"/wsbs/ptlogin/dynamicPwd", dynamicPwdMap, charset);
		Scanner in=new Scanner(System.in);
		System.out.println("请输入验证码");
		String dPwd=in.next();
		Map<String, String> loginMap=new HashMap<>();
		loginMap.put("username", "91330206MA281H1U1G");
		loginMap.put("pwd", "123456Ab*");
		loginMap.put("phone", "13588030641");
		loginMap.put("dPwd", dPwd);
		loginMap.put("loginType", "05");
		loginMap.put("wlyys", "");
		
		response=httpClientUtil.doPost(httpclient, path+"/wsbs/ptlogin/check", loginMap, charset);
		response=httpClientUtil.doGet222(httpclient,path+"/wsbs/index", charset);
		System.out.println(response);
	}
	
	
	
	public static void main(String[] args) {
		TestMain main = new TestMain();
		main.test();
	}
	
}