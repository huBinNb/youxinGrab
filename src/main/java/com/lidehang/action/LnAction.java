package com.lidehang.action;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServlet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.data.collection.dao.CompanyDataDao;
import com.lidehang.data.collection.returnInfo.GetDataResponse;
import com.lidehang.national.foreignCurrency.ForeignCurrencyGrab;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.learningNetwork.EducationQuery;
import com.lidehang.national.learningNetwork.LearningGrab;
import com.lidehang.national.learningNetwork.SchoolQuery;
import com.lidehang.national.util.ImageUtil;
import com.lidehang.national.util.TaxConstants;

import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;

/**
 * 学信网数据抓取
 * @author Hobn
 *
 */
@RestController
@RequestMapping(value = "/LnAction")
public class LnAction extends HttpServlet {
	
	
	@Value("${youxin.dataGrab}")
	private String dataGrabUrl;
	
	@Value("${youxin.token}")
	private String token;
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(LnAction.class);
	private ConcurrentHashMap<String,AtomicBoolean> cmap = new ConcurrentHashMap<String,AtomicBoolean>();
	private ConcurrentHashMap<String,AtomicBoolean> codeMap = new ConcurrentHashMap<String,AtomicBoolean>();
	private ConcurrentHashMap<String,CloseableHttpClient> hmap = new ConcurrentHashMap<String,CloseableHttpClient>();
	
	@Autowired
	private CompanyDataDao companyDataDao;
	
	/**
	 * 获取验证码
	 * @return
	 */
	@ApiOperation(value="获得验证码",notes="根据链接获取验证码")
	@GetMapping(value="/getCode")
	public String  getCode(){
		
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		HttpClientBuilder builder = HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy());// 利用LaxRedirectStrategy处理POST重定向问题
		CloseableHttpClient httpclient = builder.build();
		hmap.put("httpclient", httpclient);
			InputStream imgcode=TaxConstants.getImgCode(httpclient, "https://account.chsi.com.cn/passport/captcha.image?id=" + Math.random()*10000);
			String code=ImageUtil.encodeImgageToBase64(imgcode);
//			System.out.println(code);
		return code;
	}

	/**
	 * 账户密码验证
	 * @param username		身份证号
	 * @param password		密码
	 * @param code		          验证码
	 * @return
	 */
	@PostMapping(value="/checkLogin")
	public GetDataResponse checkLogin(@RequestParam String username,@RequestParam String password,@RequestParam String code) {
			GetDataResponse getDataResponse=new GetDataResponse();
			HttpClientUtil httpClientUtil = new HttpClientUtil();
			
			/*Boolean flag=true;
			if(flag){
			}*/
			/*HttpClientBuilder builder = HttpClients.custom()
					.setRedirectStrategy(new LaxRedirectStrategy());// 利用LaxRedirectStrategy处理POST重定向问题
			CloseableHttpClient httpclient = builder.build();
			hmap.put("httpclient", httpclient);*/
			
			
		    String url="https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check";
			String charset = "utf-8";
			String response = TaxConstants.getMes(hmap.get("httpclient"),url);
			Document doc = Jsoup.parse(response);
			Elements lt = doc.getElementsByAttributeValue("name", "lt");
			Elements _eventId = doc.getElementsByAttributeValue("name", "_eventId");
			Elements submit = doc.getElementsByAttributeValue("name", "submit");
			String httpOrgCreateTest = "https://account.chsi.com.cn" + doc.getElementById("fm1").attr("action");
			Map<String, String> createMap = new HashMap<String, String>();
			createMap.put("username", username);
			createMap.put("password", password);
			createMap.put("captcha", code);
			
			/*try {
			if(codeMap.get("verifyCode").get()){
				createMap.put("captcha", code);
			}
				
			} catch (Exception e) {
				// TODO: handle exception
			}*/
			
			
			createMap.put("lt", lt.get(0).val());
			createMap.put("_eventId", _eventId.get(0).val());
			createMap.put("submit", submit.get(0).val());
			response = httpClientUtil.doPost(hmap.get("httpclient"), httpOrgCreateTest, createMap, charset);
			doc = Jsoup.parse(response);
		   
			if(response.indexOf("学信档案登录页面")==-1){
				getDataResponse.setCode("200");
				getDataResponse.setMessage("登入成功");
			}else{
				getDataResponse.setCode("400");
				getDataResponse.setMessage("登入失败");
				try {
					Elements errors=doc.select("[class=ct_input errors]");//ct_input errors
					String value=errors.get(0).text();
					if(value!=null){
						codeMap.put("verifyCode", new AtomicBoolean(true));
						getDataResponse.setMessage(value);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			return getDataResponse;
	}

	/**
     * 数据抓取
     * @param httpclient
     * @return
     */
	@GetMapping(value="/addData")
	public String addData(CloseableHttpClient httpclient,String username) {
		    TaxConstants.getMes(httpclient, "https://my.chsi.com.cn/archive/index.action");
		    String  response=TaxConstants.getMes(httpclient, "https://my.chsi.com.cn/archive/gdjy/xj/show.action");
		    //学籍查询
		    new SchoolQuery().getInformation(httpclient,username,response);
		    //学历查询
		    new EducationQuery().getInformation(httpclient,username);
		    logger.info("学籍学历抓取完成");
		return "success";
	}
	
	/**
	 * 对外提供数据接口
	 * @param username		身份证号
	 * @param password		密码
	 * @param code		          验证码
	 * @param type			类型
	 * @param pathStr		解析的表
	 * @return
	 */
	@GetMapping(value="/getData")
	public GetDataResponse getData(@RequestParam String username,@RequestParam String password,@RequestParam String type,@RequestParam String code,@RequestParam String pathStr){
		GetDataResponse getDataResponse=new GetDataResponse();
		if(cmap.get(username)==null){
			synchronized (cmap) {
				if(cmap.get(username)==null){
					getDataResponse=checkLogin(username, password,code);
					cmap.put(username, new AtomicBoolean(true));
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
			codeMap.remove("verifyCode");
			return getDataResponse;
		}
		if(getDataResponse.getCode().equals("200")){
		List<org.bson.Document>  data=companyDataDao.getDataByType(username, type);
		if(!data.isEmpty()){
			for(org.bson.Document document : data){
				Set<String> keys = document.keySet();
				for(String key : keys){
					if(document.get(key)!=null && document.get(key) instanceof List && !((List)document.get(key)).isEmpty()){
						getDataResponse.setData(data);
						if(cmap.get(username)!=null){
							cmap.get(username).set(false);
							hmap.remove("httpclient");
							codeMap.remove("verifyCode");
						}
						return getDataResponse;
					}
				}
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if("success".equals(addData(hmap.get("httpclient"), username))){
							List<org.bson.Document> data=companyDataDao.getDataByType(username, type);
							JSONArray json=JsonArrayUtils.objectToArrray(data);
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("jsonStr", json.toString());
//							回调给对方  上线时需要将这个接口地址改改
//							String result=TaxConstants.postMes(HttpClients.createDefault(), "http://192.168.100.44:8080/"+pathStr+"/callBack/"+orgCode+"?token=1", map);
							String result = TaxConstants.postMes(HttpClients.createDefault(), dataGrabUrl+pathStr+"/callBack/"+username+
									"?token="+token, map);
							System.out.println(result);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						if(cmap.get(username)!=null){
							cmap.get(username).set(false);
							hmap.remove("httpclient");
							codeMap.remove("verifyCode");
						}
					}
				}
			}).start();
			getDataResponse.setMessage("processing");
			return getDataResponse;
		}
		}else{
			if(cmap.get(username)!=null){
				cmap.get(username).set(false);
				cmap.remove(username);
				hmap.remove("httpclient");
				codeMap.remove("verifyCode");
			}
		}
		return getDataResponse;
	}
}
