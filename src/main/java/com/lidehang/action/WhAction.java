package com.lidehang.action;

import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServlet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.core.util.MongoUtil;
import com.lidehang.data.collection.dao.CompanyDataDao;
import com.lidehang.data.collection.returnInfo.GetDataResponse;
import com.lidehang.national.foreignCurrency.ForeignBasicInfo;
import com.lidehang.national.foreignCurrency.ForeignCurrencyGrab;
import com.lidehang.national.foreignCurrency.ForeignDeclaration;
import com.lidehang.national.foreignCurrency.ForeignDeclarationEnter;
import com.lidehang.national.foreignCurrency.ForeignUnDeclaration;
import com.lidehang.national.util.ImageUtil;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;
import com.lidehang.national.util.TimeUtils;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import io.swagger.annotations.ApiOperation;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 外汇数据抓取
 * @author Hobn
 *
 */
@RestController
@RequestMapping(value = "/WhAction")
public class WhAction extends HttpServlet {
	@Value("${youxin.dataGrab}")
	private String dataGrabUrl;
	
	@Value("${youxin.token}")
	private String token;
	
	@Value("${youxin.wh.webUpdateTime}")
	private String webUpdateTime;
	
	private static Logger logger=Logger.getLogger(WhAction.class);
	
	private static final long serialVersionUID = 1L;
	
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
		CloseableHttpClient httpclient=HttpClients.createDefault();
		hmap.put("httpclient", httpclient);
		InputStream imgcode=TaxConstants.getImgCode(httpclient, "http://asone.safesvc.gov.cn/asone/jsp/code.jsp?refresh=" + Math.random());
		String code=ImageUtil.encodeImgageToBase64(imgcode);
		return code;
	}
	
	/**
	 * 账户密码验证
	 * @param orgCode    机构代码
	 * @param userCode   用户代码
	 * @param pwd        用户密码
	 * @param code       验证码
	 * @return
	 */
	@PostMapping(value="/checkLogin")
	@ResponseBody
	public GetDataResponse checkLogin(@RequestParam String orgCode,@RequestParam String userCode,@RequestParam String pwd,@RequestParam String code) {
		GetDataResponse getDataResponse=new GetDataResponse();
		//获取当前的年月日
		Calendar nowTime=Calendar.getInstance();
		int nowYear=nowTime.get(Calendar.YEAR);
		int nowMonth=nowTime.get(Calendar.MONTH)+1;
		int nowDay=nowTime.get(Calendar.DATE);
		//网站更新日期（日）
		int webUpdateDay=Integer.valueOf(webUpdateTime);
		logger.info("网站更新日期（日）："+webUpdateDay); 
		
		
		//通过上次的更新时间判断是否需要更新
		MongoCollection<org.bson.Document>  collection = MongoUtil.getDatabase().getCollection("c_updateTime");
		MongoCollection<org.bson.Document>  collectionId = MongoUtil.getDatabase().getCollection("c_"+orgCode);
		try {
			FindIterable<org.bson.Document> findIterable = collection.find(Filters.eq("companyId", orgCode));
			List<org.bson.Document> list=new ArrayList<>();
			MongoCursor<org.bson.Document> mongoCursor = findIterable.iterator();
		    JSONObject	jsonObject=null;
		    while (mongoCursor.hasNext()) {
		    	String jsons=com.mongodb.util.JSON.serialize(mongoCursor.next());
		    	jsonObject=JsonArrayUtils.objectToJson(jsons);
			}
		    String updateTime=jsonObject.getString("updateTime");
		    int lastUpdateYear=Integer.valueOf(updateTime.substring(0, 4));
			int lastUpdateMonth=Integer.valueOf(updateTime.substring(4, 6));
			int lastUpdateDay=Integer.valueOf(updateTime.substring(6));
			int lastMonthDayNum=TimeUtils.getDaysByYearMonth(lastUpdateYear, lastUpdateMonth);
			logger.info("上次更新时间的月份："+lastUpdateMonth);
			//在不同的月份中两个时间段必须超过28天||在相同的岳峰中当前时间必须超过网站更新时间
			if(((nowMonth>lastUpdateMonth)&&(lastMonthDayNum-lastUpdateDay+nowDay)>28)||((nowMonth==lastUpdateMonth)&&(nowDay>webUpdateDay))){
				collection.deleteMany(Filters.eq("companyId", orgCode));
				collectionId.drop();
			}
		} catch (Exception e) {
			logger.info("从未抓取过该公司",e);
		}
	
		
		
		List<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
		values.add(new BasicNameValuePair("orgCode", orgCode));
		values.add(new BasicNameValuePair("userCode", userCode));
		values.add(new BasicNameValuePair("pwd", MD5Util.MD5(pwd)));
		values.add(new BasicNameValuePair("check", code));
		String response = TaxConstants.postMes(hmap.get("httpclient"), "http://asone.safesvc.gov.cn/asone/jsp/checkCode.jsp",
				values);
		values.clear();
		Document document = Jsoup.parse(StringUtils.rpAll(response));
		String safeValidateCode = document.getElementsByAttributeValue("name", "safeValidateCode").val();
		String backUrl = document.getElementsByAttributeValue("name", "backUrl").val();
		String enterUrl = document.getElementsByAttributeValue("name", "enterUrl").val();
		String userCodeNew = document.getElementsByAttributeValue("name", "userCode").val();
		String pwdNew  = document.getElementsByAttributeValue("name", "pwd").val();
		String orgCodeNew  = document.getElementsByAttributeValue("name", "orgCode").val();
		values.add(new BasicNameValuePair("safeValidateCode", safeValidateCode));
		values.add(new BasicNameValuePair("backUrl", backUrl));
		values.add(new BasicNameValuePair("enterUrl", enterUrl));
		values.add(new BasicNameValuePair("userCode", userCodeNew));
		values.add(new BasicNameValuePair("pwd", pwdNew));
		values.add(new BasicNameValuePair("orgCode", orgCodeNew));
		response = TaxConstants.postMes(hmap.get("httpclient"), "http://asone.safesvc.gov.cn/asone/servlet/AuthorityServlet",values);
			Document doc=Jsoup.parse(response);
			try {
				Element userName= doc.getElementById("userName");
				if(userName!=null){
					getDataResponse.setCode("200");
					getDataResponse.setMessage("登入成功");
					}else {
						 getDataResponse.setCode("400");
						 getDataResponse.setMessage("登入失败");
					}
			} catch (Exception e) {
				   e.printStackTrace();
			}
			return getDataResponse;
	}
	
    /**
     * 数据抓取
     * @param httpclient
     * @param organizationCode  机构代码
     * @return
     */
	public String addData(CloseableHttpClient httpclient,String organizationCode) { 
		Map<String, Object> map = new HashMap<String,Object>();
		Calendar nowTime=Calendar.getInstance();
		int nowYear=nowTime.get(Calendar.YEAR);
		int nowMonth=nowTime.get(Calendar.MONTH)+1;
		int nowDay=nowTime.get(Calendar.DATE);
		String monthStr="";
		if(nowMonth<10){
			monthStr="0"+String.valueOf(nowMonth);
		}else {
			monthStr=String.valueOf(nowMonth);
		}
		//更新时间
		String updateTimeStr=String.valueOf(nowYear)+monthStr+String.valueOf(nowDay);
		//创建updateTime
		MongoCollection<org.bson.Document> collection = MongoUtil.getDatabase().getCollection("c_updateTime");
		map.put("companyId", organizationCode);
		map.put("updateTime", updateTimeStr);
		collection.insertOne(new org.bson.Document(map));
		
        new ForeignDeclaration().getDeclareQuota(httpclient, organizationCode);//涉外收入申报表--已申报（已审核）信息查询		15001
     	new ForeignBasicInfo().getDeclareQuota(httpclient, organizationCode);//外汇企业档案信息--基础档案管理--企业档案信息	15002
//		new ForeignUnDeclaration().getDeclareQuota(httpclient, organizationCode);//涉外收入申报表--已申报（待审核）信息查询【账号没有数据】
     	new ForeignDeclarationEnter().getInformation(httpclient, organizationCode);//涉外收入申报表--申报信息录入		15004
     	
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
	public GetDataResponse getData(@RequestParam String orgCode,@RequestParam String userCode,@RequestParam String pwd,@RequestParam String code,@RequestParam String type,@RequestParam String pathStr){
		 GetDataResponse getDataResponse=new GetDataResponse();
		   if(cmap.get(orgCode)==null){
				synchronized (cmap) {
				  if(cmap.get(orgCode)==null){
					  getDataResponse=checkLogin(orgCode, userCode, pwd, code);
					  cmap.put(orgCode,new AtomicBoolean(true));
				  }	
				}
			}else if(cmap.get(orgCode).get()){
				getDataResponse.setCode("200");
				getDataResponse.setMessage("processing");
				return getDataResponse;
			}else if(!cmap.get(orgCode).get()){
				getDataResponse.setCode("200");
				getDataResponse.setData(companyDataDao.getDataByType(orgCode, type));
				cmap.remove(orgCode);
				hmap.remove("httpclient");
				return getDataResponse;
			}
		   if(getDataResponse.getCode().equals("200")){
		    List<org.bson.Document> data=companyDataDao.getDataByType(orgCode, type);
		    if(!data.isEmpty()){
				for (org.bson.Document document : data) {
					Set<String> keys=document.keySet();
					for (String key : keys) {
						if(document.get(key)!=null&&document.get(key) instanceof List && !((List)document.get(key)).isEmpty()){
							getDataResponse.setData(data);
							if(cmap.get(orgCode)!=null){
								cmap.get(orgCode).set(false);
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
						if("success".equals(addData(hmap.get("httpclient"),orgCode))){
							List<org.bson.Document> data=companyDataDao.getDataByType(orgCode, type);
							JSONArray json=JsonArrayUtils.objectToArrray(data);
							Map<String, Object> map=new HashMap<String,Object>();
							map.put("jsonStr",json.toString());
							logger.info(map);
							logger.info(dataGrabUrl+pathStr+"/callBack/"+orgCode+"?token="+token);
							//回调给对方  上线时需要将这个接口地址改改
//							String result=TaxConstants.postMes(HttpClients.createDefault(), "http://192.168.100.44:8080/"+pathStr+"/callBack/"+orgCode+"?token=1", map);
							String result=TaxConstants.postMes(HttpClients.createDefault(), dataGrabUrl+pathStr+"/callBack/"+orgCode+"?token="+token, map);
							logger.info("回调给对方的信息:"+result);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						if(cmap.get(orgCode)!=null){
							cmap.get(orgCode).set(false);
							hmap.remove("httpclient");
						}
					}
				  }
				}).start();
			getDataResponse.setMessage( "processing");
			return getDataResponse;
			}
		    }else {
				if(cmap.get(orgCode)!=null){
//					cmap.get(orgCode).set(false);
					cmap.remove(orgCode);
					hmap.remove("httpclient");
				}
			}
			return getDataResponse;
	}
}