package com.lidehang.national.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.client.RestTemplate;

import com.lidehang.data.collection.dao.CompanyDataDao;
import com.lidehang.dataInterface.model.constant.JsonArrayUtils;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.learningNetwork.EducationQuery;
import com.lidehang.national.learningNetwork.SchoolQuery;
import com.lidehang.national.util.TaxConstants;

import net.sf.json.JSONArray;

/**
 * 学信网
 * @author Hobn
 *
 */
@Controller
@RequestMapping(value = "/LnAction")
public class LnAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ConcurrentHashMap<String,AtomicBoolean> cmap = new ConcurrentHashMap<String,AtomicBoolean>();
	
	@Autowired
	private CompanyDataDao companyDataDao;
	
	
	@PostMapping(value="/checkLogin")
	@ResponseBody
	//@RequestParam String orgCode,@RequestParam String userCode,@RequestParam String pwd,@RequestParam String code
	public String checkLogin(@RequestParam String username,@RequestParam String password) {
			String url="https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check";
			String charset = "utf-8";
			HttpClientUtil httpClientUtil = new HttpClientUtil();
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
			/*createMap.put("username", "330682199010014436");
			createMap.put("password", "1990hu1001");*/
			createMap.put("username", username);
			createMap.put("password", password);
			createMap.put("lt", lt.get(0).val());
			createMap.put("_eventId", _eventId.get(0).val());
			createMap.put("submit", submit.get(0).val());
			response = httpClientUtil.doPost(httpclient, httpOrgCreateTest, createMap, charset);
			String state;
			
			if(response.indexOf("学信档案登录页面")==-1){
				 state="1";//登录成功
			}else{
				state="0";//登录失败
			}
			return state;
	}

	@PostMapping(value="/addData")
	@ResponseBody
	//@RequestParam String orgCode,@RequestParam String userCode,@RequestParam String pwd,@RequestParam String code
	public String addData(@RequestParam String username,@RequestParam String password) {
			String url="https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check";
			String charset = "utf-8";
			HttpClientUtil httpClientUtil = new HttpClientUtil();
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
			/*createMap.put("username", "330682199010014436");
			createMap.put("password", "1990hu1001");*/
			createMap.put("username", username);
			createMap.put("password", password);
			createMap.put("lt", lt.get(0).val());
			createMap.put("_eventId", _eventId.get(0).val());
			createMap.put("submit", submit.get(0).val());
			response = httpClientUtil.doPost(httpclient, httpOrgCreateTest, createMap, charset);
		    response=TaxConstants.getMes(httpclient, "https://my.chsi.com.cn/archive/index.action");
		    response=TaxConstants.getMes(httpclient, "https://my.chsi.com.cn/archive/gdjy/xj/show.action");
		    //学籍查询
		    new SchoolQuery().getInformation(httpclient,createMap.get("username"),response);
		    //学历查询
		    new EducationQuery().getInformation(httpclient,createMap.get("username"));
		return "success";
	}
	
	@GetMapping(value="/getData")
	@ResponseBody
	public Object getData(@RequestParam String username,@RequestParam String password,@RequestParam String type){
		List<org.bson.Document>  data=companyDataDao.getDataByType(username, type);
		if(!data.isEmpty()){
			for(org.bson.Document document : data){
				Set<String> keys = document.keySet();
				for(String key : keys){
					if(document.get(key)!=null && document.get(key) instanceof List && !((List)document.get(key)).isEmpty()){
						return data;
					}
				}
			}
			
			if(cmap.get(username)==null){
				synchronized (cmap) {
					if(cmap.get(username)==null){
						cmap.put(username, new AtomicBoolean(true));
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
						if("success".equals(addData(username, password))){
							List<org.bson.Document> data=companyDataDao.getDataByType(username, type);
							JSONArray json=JsonArrayUtils.objectToArrray(data);
							String ss=json.toString();
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("jsonStr", ss);
							String result = TaxConstants.postMes(HttpClients.createDefault(), "http://wb.lidehang.com/dcsPxlDegree/callBack/"+username+
									"?token=1", map);
//							String result = restTemplate.postForEntity("http://wb.lidehang.com/dcsPxlDegree/callBack/"+username+
//									"?token=1", null, String.class, map).getBody();
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
