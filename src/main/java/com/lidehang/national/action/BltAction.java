package com.lidehang.national.action;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServlet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
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
import com.lidehang.dataInterface.model.constant.JsonArrayUtils;
import com.lidehang.national.foreignCurrency.ForeignCurrencyGrab;
import com.lidehang.national.foreignCurrency.SinosureGrab;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.sinosure.QuotaQuery;
import com.lidehang.national.sinosure.ShipmentQuery;
import com.lidehang.national.util.ImageUtil;
import com.lidehang.national.util.TaxConstants;

import net.minidev.json.annotate.JsonSmartAnnotation;
import net.sf.json.JSONArray;

/**
 * 保理通
 * @author Hobn
 *
 */
@Controller
@RequestMapping(value = "/BltAction")
public class BltAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ConcurrentHashMap< String, AtomicBoolean> cmap=new ConcurrentHashMap<String,AtomicBoolean>();
	
	@Autowired
	private CompanyDataDao companyDataDao;
	
	private CloseableHttpClient httpclient=HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
	@GetMapping(value="/getCode")
	@ResponseBody
	public String  getCode(){
		InputStream imgCode=TaxConstants.getImgCode(httpclient, "https://soluia.sinosure.com.cn/cas/page/sol/image.jsp");
		SinosureGrab.createImgCode(imgCode);
		String logincode=ImageUtil.encodeImgageToBase64(new File("D:\\SinosureCode\\imgCode.jpg"));
		return logincode;
	}

	@PostMapping(value = "/addData")
	@ResponseBody
	public String addData(@RequestParam String username,@RequestParam String password,@RequestParam String logincode) {
		String charset = "utf-8";
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		/*HttpClientBuilder builder = HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy());// 利用LaxRedirectStrategy处理POST重定向问题
		CloseableHttpClient httpclient = builder.build();*/
		String response = TaxConstants.getMes(httpclient,
				"https://soluia.sinosure.com.cn/cas/login?service=https%3A%2F%2Fsol.sinosure.com.cn%2Fbiz%2Fssologin.do%3Fmethod%3DssoLogin&systemtype=1");
		Document doc = Jsoup.parse(response);
		Elements lt = doc.getElementsByAttributeValue("name", "lt");
		Elements execution = doc.getElementsByAttributeValue("name", "execution");
	/*	InputStream imgCode = TaxConstants.getImgCode(httpclient,
				"https://soluia.sinosure.com.cn/cas/page/sol/image.jsp");
		SinosureGrab.createImgCode(imgCode);
		Scanner in = new Scanner(System.in);*/
		String httpOrgCreateTest = "https://soluia.sinosure.com.cn" + doc.getElementById("fm1").attr("action");
		Map<String, String> createMap = new HashMap<String, String>();
		/*createMap.put("username", "101676huwq");
		createMap.put("password", "137058");
		createMap.put("logincode", in.next());*/
		createMap.put("username", username);
		createMap.put("password", password);
		createMap.put("logincode", logincode);
		createMap.put("signDataBase64", "");
		createMap.put("loginmethod", "");
		createMap.put("lt", lt.get(0).val());
		createMap.put("logintype", "1");
		createMap.put("execution", execution.get(0).val());
		createMap.put("_eventId", "submit");
		response = httpClientUtil.doPost(httpclient, httpOrgCreateTest, createMap, charset);
	    response=TaxConstants.getMes(httpclient, "https://sol.sinosure.com.cn/biz/mainFrame/index.jsp?investid=null");
	    new QuotaQuery().getApprovedQuota(httpclient);       //批复限额列表
	    new ShipmentQuery().getAcceptedDeclare(httpclient);  //出运信息列表
		return "success";
	}
	
	@GetMapping(value="/getData")
	@ResponseBody
	public Object getData(@RequestParam String username,@RequestParam String type,@RequestParam String password,@RequestParam String logincode){
		List<org.bson.Document>  data=companyDataDao.getDataByType(username, type);
		if(!data.isEmpty()){
			for (org.bson.Document document : data) {
			 Set<String>	keys= document.keySet();
			 	for (String key : keys) {
					if(document.get(key)!=null&& document.get(key) instanceof List && ((List)document.get(key)).isEmpty()){
			 		return data;
				}
			}
		}
		if(cmap.get(username)==null){
			synchronized (cmap) {
			  if(cmap.get(username)==null){
				  cmap.put(username,new AtomicBoolean(true));
			  }	
			}
		}else if(cmap.get(username).get()){
			return "processing";
		}else if(!cmap.get(username).get()){
			cmap.remove(username);
			return "success";
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					if("success".equals(addData(username, password, logincode))){
						List<org.bson.Document> data=companyDataDao.getDataByType(username, type);
						JSONArray json= JsonArrayUtils.objectToArrray(data);
						Map<String, Object> map=new HashMap<String,Object>();
						map.put("jsonStr", json.toString());
						String result=TaxConstants.postMes(HttpClients.createDefault(), "", map);
						System.out.println(result);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					cmap.get(username).set(false);
				}
			}
		}).start();
		return "processing";
		}
		return data;
	}
}

