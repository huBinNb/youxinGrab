package com.lidehang.national.personalCredit;

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

//个人征信测试
public class TestMain {
	private String url="https://ipcrs.pbccrc.org.cn/login.do?method=initLogin";
    private String charset = "utf-8";
	private HttpClientUtil httpClientUtil = null;
	public TestMain() {
		httpClientUtil = new HttpClientUtil();
	}
	public void test() {
		//个人征信的header
		Map<String, String> headerMap=new HashMap<>();
		HttpClientBuilder builder = HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy());  //重定向
		CloseableHttpClient httpclient = builder.build();
		try {
			httpclient=new SSLClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		headerMap.put("Referer", "https://ipcrs.pbccrc.org.cn/top1.do");
		String response =httpClientUtil.doPCGet111(httpclient,url, charset,headerMap);
		Document doc= Jsoup.parse(response);
		Elements method = doc.getElementsByAttributeValue("name", "method");
		Elements date = doc.getElementsByAttributeValue("name", "date");
		Elements taken = doc.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN");
		String imgrc=doc.getElementById("imgrc").attr("src");
		String login=doc.getElementsByTag("form").get(0).attr("action");
		InputStream imgCode =PersonalCreditGrab.getPCImgCode(httpclient,
				"https://ipcrs.pbccrc.org.cn"+imgrc);
		PersonalCreditGrab.createImgCode(imgCode);
		Scanner in = new Scanner(System.in);
		String loginUrl = "https://ipcrs.pbccrc.org.cn" + login;
		Map<String, String> createMap = new HashMap<String, String>();
		createMap.put("org.apache.struts.taglib.html.TOKEN", taken.get(0).val());
		createMap.put("method", method.get(0).val());
		createMap.put("date",date.get(0).val());
		String userName="hubin13_shangyu";
		createMap.put("loginname", userName);
		createMap.put("password", "1990hu1001");
		createMap.put("_@IMGRC@_", in.next());
		headerMap.put("Referer", "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp");  
		response = httpClientUtil.doPCPost111(httpclient, loginUrl, createMap, charset,headerMap);
		String reportUrl=Jsoup.parse(response).getElementById("mainFrame").attr("src");
		headerMap.put("Referer", "https://ipcrs.pbccrc.org.cn/login.do");  
		response=httpClientUtil.doPCGet111(httpclient, reportUrl, charset, headerMap);
		String  f_setReprot="https://ipcrs.pbccrc.org.cn/reportAction.do?method=queryReport";
		headerMap.put("Referer", "https://ipcrs.pbccrc.org.cn/menu.do");  
		response=httpClientUtil.doPCGet111(httpclient, f_setReprot, charset, headerMap);
		//这边还需要添加radio_type
		/*String postStr=response.substring(response.indexOf("$.ajax({"), response.indexOf("return returnvalu;"));
		String postUrl=postStr.substring(postStr.indexOf("url:"), postStr.indexOf("url:"));*/
		/*System.out.println("请输入身份验证码");
		Scanner vCode=new Scanner(System.in);
		String code=vCode.next();*/
		Map<String,String> creditInfor=new HashMap<>();
		creditInfor.put("method","checkTradeCode");
		creditInfor.put("code", "kqyknd");
		creditInfor.put("reportformat", "21");
		headerMap.put("Referer", "https://ipcrs.pbccrc.org.cn/reportAction.do?method=queryReport");
		String creditLogin= httpClientUtil.doPCPost111(httpclient, "https://ipcrs.pbccrc.org.cn/reportAction.do", creditInfor, charset, headerMap);
		Map<String, String> creditPageMap=new HashMap<>();
		creditPageMap.put("counttime", "");
		creditPageMap.put("reportformat", "21");
		creditPageMap.put("tradeCode", "kqyknd");
		//url需要修改   个人信用信息提示    个人信用信息概要    个人信用报告
		String personalCreditReporUrl="https://ipcrs.pbccrc.org.cn/simpleReport.do?method=viewReport";
		String creditPage=httpClientUtil.doPCPost111(httpclient, reportUrl,creditPageMap, charset, headerMap);
		//获取个人信用信用报告    需测试
	    new CreditReport().getPersonalCreditReport(creditPage,userName);
	}
	
	
	
	public static void main(String[] args) {
		TestMain main = new TestMain();
		main.test();
	}
}