package com.lidehang.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
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
import com.lidehang.national.localtax.cwbbcx.LandGrabLiRunBiao;
import com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx.LandGrab;
import com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx.LandGrabSbbcxCjrb;
import com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx.LandGrabSbbcxFjs;
import com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx.LandGrabSbbcxTy;
import com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx.LandGrabSbbcxYhs;
import com.lidehang.national.localtax.shuiwudengjicx.LandGrabJbxxcx;
import com.lidehang.national.localtax.xinyongcx.LandGrabXydjcx;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.TaxConstants;
import com.lidehang.national.util.TimeUtils;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 地税数据抓取
 * @author Hobn
 *
 */
@RestController
@RequestMapping(value = "/DsAction")
public class DsAction extends HttpServlet {
	@Value("${youxin.dataGrab}")
	private String dataGrabUrl;
	
	@Value("${youxin.token}")
	private String token;
	
	@Value("${youxin.ds.webUpdateTime}")
	private String webUpdateTime;
	
	private static Logger logger = Logger.getLogger(DsAction.class);
	
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<String, AtomicBoolean> cmap=new ConcurrentHashMap<String,AtomicBoolean>();
	private ConcurrentHashMap<String, String> mapId=new ConcurrentHashMap<String,String>();
	private ConcurrentHashMap<String, CloseableHttpClient> hmap=new ConcurrentHashMap<String,CloseableHttpClient>();

//	private CloseableHttpClient httpclient=HttpClients.createDefault();
	@Autowired
	private CompanyDataDao companyDataDao;
	
	/**
	 * 获取验证码
	 * @return
	 */
	@GetMapping(value="/getCode")
	public String  getCode(){
		CloseableHttpClient httpclient=HttpClients.createDefault();
		hmap.put("httpclient", httpclient);
		String imgCode = TaxConstants.getMes(httpclient,
		"http://www.zjds-etax.cn/wsbs/api/home/auth/imgcode?sid=" + Math.random());
		JSONObject json = JsonArrayUtils.objectToJson(imgCode);
		String imgStr = json.getString("imgCode");
		return imgStr;  
	} 
	
	/**
	 * 账户密码验证
	 * @param username		用户名
	 * @param password		密码
	 * @param mobile		手机后四位
	 * @param code			验证码
	 * @return
	 */
	@PostMapping(value = "/checkLogin")
	public GetDataResponse checkLogin(@RequestParam String username,@RequestParam String password,@RequestParam String mobile,@RequestParam String code) {
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
				MongoCollection<org.bson.Document>  collectionId = MongoUtil.getDatabase().getCollection("c_"+username);
				try {
					FindIterable<org.bson.Document> findIterable = collection.find(Filters.eq("companyId", username));
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
						collection.deleteMany(Filters.eq("companyId", username));
						collectionId.drop();
					}
				} catch (Exception e) {
					logger.info("从未抓取过该公司",e);
				}
			
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("mmqrdbj", "0");
		maps.put("imgCode", code);
		maps.put("mobile", mobile);
		maps.put("password", MD5Util.MD5(password));
		maps.put("username", username);
		String response = TaxConstants.postMes(hmap.get("httpclient"),"http://www.zjds-etax.cn/wsbs/api/home/auth/login",maps);
		JSONObject json = JsonArrayUtils.objectToJson(response);
		 try {
			 String userId = json.getString("USERID");
			 if(!userId.isEmpty()){
				 getDataResponse.setDescription(userId);
				 getDataResponse.setCode("200");
				 getDataResponse.setMessage("登入成功");
			 }
		} catch (Exception e) {
			 getDataResponse.setCode("400");
			 getDataResponse.setMessage("登入失败");
		}
		return getDataResponse;
	}
	
	/**
	 * 数据抓取
	 * @param httpclient
	 * @param userId		用户Id
	 * @param username		用户名
	 * @return
	 */
	public String addData(CloseableHttpClient httpclient,String userId,String username) {
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
		map.put("companyId", username);
		map.put("updateTime", updateTimeStr);
		collection.insertOne(new org.bson.Document(map));
		
		
		 new LandGrabXydjcx().selectLandTaxByDate(httpclient, userId,username); //纳税申报 信用等级查询 11001   dddddddddd
		 new LandGrabSbbcxTy().selectLandTaxByDate(httpclient, userId,username); //纳税申报 申报表查询 通用申报表 11002  ddddddddd
		 new LandGrabSbbcxFjs().selectLandTaxByDate(httpclient, userId,username); //纳税申报 申报表查询  城建税、教育费附加、地方教育附加税（费）申报表   11003  ddddd
		 new LandGrabSbbcxCjrb().selectLandTaxByDate(httpclient, userId,username); //纳税申报 申报表查询    残疾人就业保障金缴费申报表 11004   ddddd
		 new LandGrab().selectLandTaxByDate(httpclient, userId,username); //纳税申报 申报表查询   社会保险费缴费申报表（适用单位缴费人） 11005   ddddddddd
		 new LandGrabSbbcxYhs().selectLandTaxByDate(httpclient, userId,username); //纳税申报 申报表查询   印花税纳税申报（报告）表 11006   ddddddddd
       /*new LandGrabKkcx().selectLandTaxByDate(httpclient, userId); //纳税申报 扣款查询 11007
		 new LandGrabQscxSssr().selectLandTaxByFeestaxes(httpclient, userId); //纳税申报 欠税查询 税收收入 11008
		 new LandGrabQscxShbxsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 社会保险基金收入 11009 无数据
		 new LandGrabQscxFssr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 非税收入 11010
		 new LandGrabQscxZfxjjsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 政府性基金收入 11011
		 new LandGrabQscxZxsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 专项收入 11012
		 new LandGrabQscxXzsyxsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 行政事业性收费收入 11013
		 new LandGrabQscxFmsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 罚没收入 11014
		 new LandGrabQscxGyzcsysr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 国有资源（资产）有偿使用收入 11015
		 new LandGrabQscxQtsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 其他收入 11016
		 new LandGrabDzjsfkdy().selectLandTaxByDate(httpclient, userId); // 纳税申报 电子缴税付款凭证打印  11017
		 new LandGrabShoudaoFapiaocx().selectLandTaxByDate(httpclient, userId); //发票查询 收到的电子发票查询 11018
		 new LandGrabKaijuFapiaocx().selectLandTaxByDate(httpclient, userId); //发票查询 开具的电子发票查询 11019
		 new LandGrabWenshucx().selectLandTaxByDate(httpclient, userId); //文书查询 11020
		 new LandGrabWeizhangWeiguicx().selectLandTaxByDate(httpclient, userId); //违章违规综合查询  11021
		 new LandGrabSyqyssxx().selectLandTaxByDate(httpclient, userId); //税源报表查询 重点税源企业税收信息 11022
		 new LandGrabSyqydcb().selectLandTaxByDate(httpclient, userId); //税源报表查询 11023 重点税源企业景气调查问卷（月报）表
		 new LandGrabSyqycwxxb().selectLandTaxByDate(httpclient, userId); //税源报表查询 11024  重点税源企业财务信息（季报）表
         new LandGrabSyfdcqyxxb().selectLandTaxByDate("201601", "201702");//税源报表查询  重点税源房地产企业开发经营信息（季报）表 11025
		  */
		 //未完成
		 /*new LandGrabSbcx().selectLandTaxByDate();//纳税申报 申报查询 多项选择
		 new LandGrabRukucx().selectLandTaxByDate();//纳税申报 入库查询 多项选择
		  */	
		new LandGrabJbxxcx().selectLandTaxByDate(httpclient, userId,username); //税务登记查询 基本信息查询 11026
		new LandGrabLiRunBiao().selectLandTaxByDate(httpclient, userId,username); //财务报表 利润表    11027   dddddddd 
		logger.info("地税抓取完成");
		return "success";
	}
	
	/**
	 * 对外提供数据接口
	 * @param username		用户名
	 * @param password		密码
	 * @param mobile		手机后四位
	 * @param code			验证码
	 * @param type			类型
	 * @return
	 */
	@GetMapping(value="/getData")
	@ResponseBody
	public GetDataResponse getData(@RequestParam String username,@RequestParam String password,@RequestParam String mobile,@RequestParam String code,@RequestParam String type){
		GetDataResponse getDataResponse=new GetDataResponse();
		if(cmap.get(username)==null){
			synchronized (cmap) {
				if(cmap.get(username)==null){
					getDataResponse=checkLogin(username, password, mobile, code);
					if(getDataResponse.getDescription()!=null){
						mapId.put("userId", getDataResponse.getDescription());
					}
					cmap.put(username, new AtomicBoolean(true));
				}
			}
		}else if(cmap.get(username).get()&&getDataResponse.getCode()!=null){
			getDataResponse.setCode("200");
		    getDataResponse.setMessage("processing");
			return getDataResponse;
		}else if(!cmap.get(username).get()){
			getDataResponse.setCode("200");
		    getDataResponse.setData(companyDataDao.getDataByType(username, type));
			cmap.remove(username);
			mapId.remove("userId");
			hmap.remove("httpclient");
			return getDataResponse;
		}
		
		if(getDataResponse.getCode()!=null&&("200").equals(getDataResponse.getCode())){
		List<Document> data=companyDataDao.getDataByType(username, type);
		if(!data.isEmpty()){
			for (Document document : data) {
				Set<String> keys=document.keySet();
				for (String key : keys) {
					if(document.get(key)!=null&&document.get(key) instanceof List && !((List)document.get(key)).isEmpty()){
						if(cmap.get(username)!=null){
							cmap.get(username).set(false);
							cmap.remove(username);
							mapId.remove("userId");
						}
						getDataResponse.setData(data);
						return getDataResponse;
					}
				}
			}
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if("success".equals(addData(hmap.get("httpclient"), mapId.get("userId"), username))){
							List<Document> data=companyDataDao.getDataByType(username, type);
							JSONArray json= JsonArrayUtils.objectToArrray(data);
							Map<String, Object> map=new HashMap<String,Object>();
							map.put("jsonStr", json.toString());
//							回调给对方  上线时需要将这个接口地址改改
							String result=TaxConstants.postMes(HttpClients.createDefault(), dataGrabUrl+"dcsCds/callBack/"+username+
									"?token="+token, map);
							System.out.println(result);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						if(cmap.get(username)!=null){
						cmap.get(username).set(false);
						hmap.remove("httpclient");
						mapId.remove("userId");
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
				mapId.remove("userId");
				hmap.remove("httpclient");
			}
		}
		return getDataResponse;
	}
}
