package com.lidehang.action;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.http.HttpServlet;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lidehang.data.collection.dao.CompanyDataDao;
import com.lidehang.national.foreignCurrency.ForeignCurrencyGrab;
import com.lidehang.national.foreignCurrency.ForeignDeclaration;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.httpsUtil.SSLClient;
import com.lidehang.national.learningNetwork.EducationQuery;
import com.lidehang.national.learningNetwork.SchoolQuery;
import com.lidehang.national.learningNetwork.TestMain;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;

/**
 * 宁波国税
 * 
 * @author Hobn
 *
 */
@Controller
@RequestMapping(value = "/NbgsAction")
public class NbgsAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 获取手机验证码
	 * 
	 * @param username
	 *            用户名
	 * @param phone
	 *            手机号码
	 * @param httpclient
	 * @return
	 */
	@PostMapping(value = "/getDynamicPwd")
	@ResponseBody
	public String getDynamicPwd(@RequestParam String username, @RequestParam String phone, HttpClient httpclient) {
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		Map<String, String> dynamicPwdMap = new HashMap<>();
		String path = "https://wsbs.nb-n-tax.gov.cn";
		dynamicPwdMap.put("user", username);
		dynamicPwdMap.put("phone", phone);
		String response = httpClientUtil.doPost(httpclient, path + "/wsbs/ptlogin/dynamicPwd", dynamicPwdMap, "utf-8");
		Scanner in = new Scanner(System.in);
		System.out.println("请输入验证码");
		String dPwd = in.next();
		return dPwd;
	}

	/**
	 * 登录
	 * 
	 * @param username
	 *            用户名
	 * @param pwd
	 *            密码
	 * @param phone
	 *            手机号码
	 * @return
	 */
	@PostMapping(value = "/login")
	@ResponseBody
	public String login(@RequestParam String username, @RequestParam String pwd, @RequestParam String phone) {
		String path = "https://wsbs.nb-n-tax.gov.cn";
		String charset = "utf-8";
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		HttpClientBuilder builder = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy());// 利用LaxRedirectStrategy处理POST重定向问题
		CloseableHttpClient httpclient = builder.build();
		try {
			httpclient = new SSLClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String response = httpClientUtil.doGet222(httpclient, path + "/login.html", charset);
		Map<String, String> loginMap = new HashMap<>();
		// loginMap.put("username", "91330206MA281H1U1G");
		// loginMap.put("pwd", "123456Ab*");
		// loginMap.put("phone", "13588030641");
		loginMap.put("username", username);
		loginMap.put("pwd", pwd);
		loginMap.put("phone", phone);
		loginMap.put("dPwd", getDynamicPwd(username, phone, httpclient));
		loginMap.put("loginType", "05");
		loginMap.put("wlyys", "");
		response = httpClientUtil.doPost(httpclient, path + "/wsbs/ptlogin/check", loginMap, charset);
		response = httpClientUtil.doGet222(httpclient, path + "/wsbs/index", charset);
		// System.out.println(response);
		return "success";
	}

	/**
	 * 开通网厅
	 * 
	 * @param nsrsbh
	 *            纳税人识别号
	 * @param fddbr
	 *            法定代表人名称
	 * @param frzjhm
	 *            法定代表人证件号
	 * @param phone
	 *            国税预留手机后4位
	 * @return
	 */
	@PostMapping(value = "/ktwt")
	@ResponseBody
	public String ktwt(@RequestParam String nsrsbh, @RequestParam String fddbr, @RequestParam String frzjhm,
			@RequestParam String phone) {
		Map<String, String> headerMap = new HashMap<>();
		String path = "https://wsbs.nb-n-tax.gov.cn";
		String charset = "UTF-8";
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		HttpClientBuilder builder = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy());// 利用LaxRedirectStrategy处理POST重定向问题
		CloseableHttpClient httpclient = builder.build();
		try {
			httpclient = new SSLClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, String> ktwtMap = new HashMap<>();
		ktwtMap.put("nsrsbh", nsrsbh);
		ktwtMap.put("fddbr", fddbr);
		ktwtMap.put("frzjhm", frzjhm);
		ktwtMap.put("phone", phone.substring(phone.length()-4));
		ktwtMap.put("loginType", "05");
		headerMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		headerMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
		String response = httpClientUtil.doPCPost111(httpclient, path + "/wsbs/ptlogin/zhmm", ktwtMap, charset,
				headerMap);
		return response;
	}

	/**
	 * 找回密码
	 * 
	 * @param nsrsbh
	 *            纳税人识别号
	 * @param fddbr
	 *            法定代表人名称
	 * @param frzjhm
	 *            法定代表人证件号
	 * @param phone
	 *            国税预留手机后4位
	 * @return
	 */
	@PostMapping(value = "/zhmm")
	@ResponseBody
	public String zhmm(@RequestParam String nsrsbh, @RequestParam String fddbr, @RequestParam String frzjhm,
			@RequestParam String phone) {
		String path = "https://wsbs.nb-n-tax.gov.cn";
		String charset = "utf-8";
		Map<String, String> headerMap = new HashMap<>();
		Map<String, String> ktwtMap = new HashMap<>();
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		HttpClientBuilder builder = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy());// 利用LaxRedirectStrategy处理POST重定向问题
		CloseableHttpClient httpclient = builder.build();
		try {
			httpclient = new SSLClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ktwtMap.put("nsrsbh", nsrsbh);
		ktwtMap.put("fddbr", fddbr);
		ktwtMap.put("frzjhm", frzjhm);
		ktwtMap.put("phone", phone.substring(phone.length()-4));
		ktwtMap.put("loginType", "05");
		headerMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		headerMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
		String response = httpClientUtil.doPCPost111(httpclient, path + "/wsbs/ptlogin/zhmm", ktwtMap, charset,
				headerMap);
		return response;
	}
}
