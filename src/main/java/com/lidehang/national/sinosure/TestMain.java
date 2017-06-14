package com.lidehang.national.sinosure;

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
	private String url = "https://soluia.sinosure.com.cn/cas/login?service=https%3A%2F%2Fsol.sinosure.com.cn%2Fbiz%2Fssologin.do%3Fmethod%3DssoLogin&systemtype=1";
	private String charset = "utf-8";
	private HttpClientUtil httpClientUtil = null;
	public TestMain() {
		httpClientUtil = new HttpClientUtil();
	}

	public void test() {
		//.setDefaultRequestConfig(requestConfig)
		//RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
		HttpClientBuilder builder = HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy());// 利用LaxRedirectStrategy处理POST重定向问题
		CloseableHttpClient httpclient = builder.build();
		String response = TaxConstants.getMes(httpclient,
				"https://soluia.sinosure.com.cn/cas/login?service=https%3A%2F%2Fsol.sinosure.com.cn%2Fbiz%2Fssologin.do%3Fmethod%3DssoLogin&systemtype=1");
		Document doc = Jsoup.parse(response);
		Elements lt = doc.getElementsByAttributeValue("name", "lt");
		Elements execution = doc.getElementsByAttributeValue("name", "execution");
		InputStream imgCode = TaxConstants.getImgCode(httpclient,
				"https://soluia.sinosure.com.cn/cas/page/sol/image.jsp");
		SinosureGrab.createImgCode(imgCode);
		Scanner in = new Scanner(System.in);
		String httpOrgCreateTest = "https://soluia.sinosure.com.cn" + doc.getElementById("fm1").attr("action");
	//	System.out.println(httpOrgCreateTest);
		Map<String, String> createMap = new HashMap<String, String>();
		createMap.put("username", "101676huwq");
		createMap.put("password", "137058");
		createMap.put("logincode", in.next());
		createMap.put("signDataBase64", "");
		createMap.put("loginmethod", "");
		createMap.put("lt", lt.get(0).val());
		createMap.put("logintype", "1");
		createMap.put("execution", execution.get(0).val());
		createMap.put("_eventId", "submit");
		response = httpClientUtil.doPost(httpclient, httpOrgCreateTest, createMap, charset);
	    response=TaxConstants.getMes(httpclient, "https://sol.sinosure.com.cn/biz/mainFrame/index.jsp?investid=null");
//	    new QuotaQuery().getApprovedQuota(httpclient);
//	    new ShipmentQuery().getAcceptedDeclare(httpclient);
	}
	
	
	
	public static void main(String[] args) {
		TestMain main = new TestMain();
		main.test();
	}
}