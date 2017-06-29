package com.lidehang.action;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServlet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.data.collection.dao.CompanyDataDao;
import com.lidehang.data.collection.returnInfo.GetDataResponse;
import com.lidehang.national.foreignCurrency.ForeignBasicInfo;
import com.lidehang.national.foreignCurrency.ForeignDeclaration;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.util.ImageUtil;
import com.lidehang.national.util.SHA256Util;
import com.lidehang.national.util.TaxConstants;

import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 江苏国税数据抓取
 * @author Hobn
 *
 */
@RestController
@RequestMapping(value = "/JsGsAction")
public class JsGsAction extends HttpServlet {
	@Value("${youxin.dataGrab}")
	private String dataGrabUrl;
	
	@Value("${youxin.token}")
	private String token;
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger=Logger.getLogger(JsGsAction.class);
	
	private ConcurrentHashMap<String, AtomicBoolean> cmap=new ConcurrentHashMap<String,AtomicBoolean>();
	
	private ConcurrentHashMap<String, CloseableHttpClient> hmap=new ConcurrentHashMap<String,CloseableHttpClient>();
	
//	private CloseableHttpClient httpclient=HttpClients.createDefault();
	
	@Autowired
	private CompanyDataDao companyDataDao;

	/**
	 * 获取验证码
	 * @return
	 */
	@ApiOperation(value="获得验证码",notes="根据链接获取验证码")
	@GetMapping(value="/getCode")
	//@ResponseBody
	public String  getCode(){
		//http://asone.safesvc.gov.cn/asone/jsp/code.jsp?refresh=1495445325467  
		CloseableHttpClient httpclient=HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
		hmap.put("httpclient", httpclient);
		InputStream imgcode=TaxConstants.getImgCode(httpclient, "http://etax.jsgs.gov.cn/sso/captcha2?id=" + new Date().getTime());
		String code=ImageUtil.encodeImgageToBase64(imgcode);
		return code;
	}
	
	/**
	 * 账户密码验证
	 * @param username  用户名
	 * @param password	密码
	 * @param yzm		验证码
	 * @return
	 */
	@PostMapping(value="/checkLogin")
	@ResponseBody
	public GetDataResponse checkLogin(@RequestParam String username,@RequestParam String password,@RequestParam String yzm) {
		GetDataResponse getDataResponse=new GetDataResponse();
		String charset = "utf-8";
		String lt =null;
		String execution =null;
		String httpOrgCreateTest = null;
		
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		try {
		String response = TaxConstants.getMes(hmap.get("httpclient"),
			"http://etax.jsgs.gov.cn/sso/login?service=http%3A%2F%2Fetax.jsgs.gov.cn%2Fportal%2Findex.do");
		logger.info("@response@:"+response);
		Document doc = Jsoup.parse(response);
			lt = doc.getElementsByAttributeValue("name", "lt").get(0).val();
			execution = doc.getElementsByAttributeValue("name", "execution").get(0).val();
			httpOrgCreateTest = "http://etax.jsgs.gov.cn" + doc.getElementById("fm1").attr("action");
		} catch (Exception e) {
			e.printStackTrace();
			 getDataResponse.setCode("400");
			 getDataResponse.setMessage("登入失败");
			 return getDataResponse;
		}
		Map<String, String> createMap = new HashMap<String, String>();
		createMap.put("username", username);                                                       
		createMap.put("loginmode", "00");      
		createMap.put("causername", "");                                                           
		String DLM= httpClientUtil.doPost(hmap.get("httpclient"), "http://etax.jsgs.gov.cn/sso/swryHandler?method=upgradePrompt", createMap, charset);
		JSONObject json=JsonArrayUtils.objectToJson(DLM);
		Map<String, String> addMap = new HashMap<String, String>();
		addMap.put("password", SHA256Util.SHA_256(password + "{"+json.getString("DLM")+"}"));                              
		addMap.put("yzm", yzm);                                                            
		addMap.put("cacert", "");                                                          
		addMap.put("signature", "");                                                       
		addMap.put("lt", lt);                                                              
		addMap.put("execution", execution);                                                
		addMap.put("_eventId", "submit");                                                  
		addMap.put("lt", lt);                                                              
		addMap.put("execution", execution);                                                
		addMap.put("_eventId", "submit");    
		addMap.putAll(createMap);
		String response1 = httpClientUtil.doPost(hmap.get("httpclient"), httpOrgCreateTest, addMap, charset);
		System.out.println(response1);
		try {
			String returncode =  response1.substring(response1.indexOf("_returncode"));
			getDataResponse.setCode("400");
			getDataResponse.setMessage("登入失败");
			
		} catch (Exception e) {
			getDataResponse.setCode("200");
			getDataResponse.setMessage("登入成功");
		}
		
		
		
		
		/*List<BasicNameValuePair> values=new ArrayList<BasicNameValuePair>();
		values.add(new BasicNameValuePair("username", username));                                                       
		values.add(new BasicNameValuePair("loginmode", "00"));      
		values.add(new BasicNameValuePair("causername", "")); 
		values.add(new BasicNameValuePair("password", SHA256Util.SHA_256(password + "{"+json.getString("DLM")+"}")));                                   
		values.add(new BasicNameValuePair("yzm", yzm));                                                                 
		values.add(new BasicNameValuePair("cacert", ""));                                                               
		values.add(new BasicNameValuePair("signature", ""));                                                            
		values.add(new BasicNameValuePair("lt", lt));                                                                   
		values.add(new BasicNameValuePair("execution", execution));                                                     
		values.add(new BasicNameValuePair("_eventId", "submit"));                                                       
		values.add(new BasicNameValuePair("lt", lt));                                                                   
		values.add(new BasicNameValuePair("execution", execution));                                                     
		values.add(new BasicNameValuePair("_eventId", "submit"));  
		
		Map<String, String> createMap = new HashMap<String, String>();
		createMap.put("username", username);                                                       
		createMap.put("loginmode", "00");      
		createMap.put("causername", "");                                                           
		//String respose11= httpClientUtil.doPost(hmap.get("httpclient"), "http://etax.jsgs.gov.cn/sso/swryHandler?method=upgradePrompt", createMap, charset);
		Map<String, Object> map1=new HashMap<String,Object>();
		map1.put("password", SHA256Util.SHA_256(password));                                   
		map1.put("yzm", yzm);                                                                 
		map1.put("cacert", "");                                                               
		map1.put("signature", "");                                                            
		map1.put("lt", lt);                                                                   
		map1.put("execution", execution);                                                     
		map1.put("_eventId", "submit");                                                       
		map1.put("lt", lt);                                                                   
		map1.put("execution", execution);                                                     
		map1.put("_eventId", "submit");         
		map1.putAll(createMap);
		String response1 = TaxConstants.postMes(hmap.get("httpclient"), httpOrgCreateTest,values);
		System.out.println(response1);
//		 response1 =TaxConstants.getMes(hmap.get("httpclient"), "https://sol.sinosure.com.cn/biz/mainFrame/index.jsp?investid=null");
		response1 =TaxConstants.getMes(hmap.get("httpclient"), "http://etax.jsgs.gov.cn/portal/index.do?ticket=ST-326572-pDEzcXLZgh2FRWbrRAcZ-sso.jsgs.gov.cn");
		System.out.println(response1);	
//		httpClientUtil.doPCGet111(httpClient, url, charset, headerMap)
		 response1 =TaxConstants.getMes(hmap.get("httpclient"), "http://etax.jsgs.gov.cn/portal/index.do");
		 System.out.println(response1);
		   Document doc1=Jsoup.parse(response1);*/
			return getDataResponse;
	}
	
    /**
     * 数据抓取```````
     * @param httpclient
     * @param organizationCode  机构代码
     * @return
     */
	public String addData(CloseableHttpClient httpclient,String organizationCode) {
     	new ForeignDeclaration().getDeclareQuota(httpclient, organizationCode);
		new ForeignBasicInfo().getDeclareQuota(httpclient, organizationCode);
		return "success";
	}
	
	/**
	 * 对外提供数据接口
	 * @param orgCode    机构代码
	 * @param userCode   用户代码
	 * @param pwd        用户密码
	 * @param code       验证码
	 * @param type       类型
	 * @param pathStr    解析的表
	 * @return
	 */
	@GetMapping(value="/getData")
	@ResponseBody
	public GetDataResponse getData(@RequestParam String username,@RequestParam String password,@RequestParam String yzm,@RequestParam String type,@RequestParam String pathStr){
		 GetDataResponse getDataResponse=new GetDataResponse();
		   if(cmap.get(username)==null){
				synchronized (cmap) {
				  if(cmap.get(username)==null){
					  getDataResponse=checkLogin(username, password, yzm);
					  cmap.put(username,new AtomicBoolean(true));
				  }	
				}
			}else if(cmap.get(username).get()){
				getDataResponse.setCode("200");
				getDataResponse.setMessage("processing");
				return getDataResponse;
			}else if(!cmap.get(username).get()){
				getDataResponse.setCode("200");
				getDataResponse.setData(companyDataDao.getDataByType(username, type));
				cmap.remove(username);
				hmap.remove("httpclient");
				return getDataResponse;
			}
		   if(getDataResponse.getCode().equals("200")){
		    List<org.bson.Document> data=companyDataDao.getDataByType(username, type);
		    if(!data.isEmpty()){
				for (org.bson.Document document : data) {
					Set<String> keys=document.keySet();
					for (String key : keys) {
						if(document.get(key)!=null&&document.get(key) instanceof List && !((List)document.get(key)).isEmpty()){
							getDataResponse.setData(data);
							if(cmap.get(username)!=null){
								cmap.get(username).set(false);
								hmap.remove("httpclient");
							}
							return getDataResponse;
						}
					}
				}
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if("success".equals(addData(hmap.get("httpclient"),username))){
							List<org.bson.Document> data=companyDataDao.getDataByType(username, type);
							JSONArray json=JsonArrayUtils.objectToArrray(data);
							Map<String, Object> map=new HashMap<String,Object>();
							map.put("jsonStr",json.toString());
							//回调给对方
							String result=TaxConstants.postMes(HttpClients.createDefault(), dataGrabUrl+pathStr+"/callBack/"+username+"?token="+token, map);
						    System.out.println(result);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						if(cmap.get(username)!=null){
							cmap.get(username).set(false);
							hmap.remove("httpclient");
						}
					}
				  }
				}).start();
			getDataResponse.setMessage( "processing");
			return getDataResponse;
			}
		    }else {
				if(cmap.get(username)!=null){
					cmap.get(username).set(false);
					cmap.remove(username);
					hmap.remove("httpclient");
				}
			}
			return getDataResponse;
	}
}