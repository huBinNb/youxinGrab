package com.lidehang.action;

import static org.mockito.Mockito.CALLS_REAL_METHODS;

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
import org.apache.http.impl.client.LaxRedirectStrategy;
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
import org.springframework.web.bind.annotation.RestController;

import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.core.util.MongoUtil;
import com.lidehang.data.collection.dao.CompanyDataDao;
import com.lidehang.data.collection.returnInfo.GetDataResponse;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.sinosure.QuotaBalanceQuery;
import com.lidehang.national.sinosure.QuotaQuery;
import com.lidehang.national.sinosure.SettlementQuery;
import com.lidehang.national.sinosure.ShipmentDeclarationQuery;
import com.lidehang.national.sinosure.ShipmentQuery;
import com.lidehang.national.util.ImageUtil;
import com.lidehang.national.util.TaxConstants;
import com.lidehang.national.util.TimeUtils;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 保理通数据抓取
 * 
 * @author Hobn
 */
@RestController
@RequestMapping(value = "/BltAction")
public class BltAction extends HttpServlet {
	@Value("${youxin.dataGrab}")
	private String dataGrabUrl;
	
	@Value("${youxin.token}")
	private String token;
	
	@Value("${youxin.blt.webUpdateTime}")
	private String webUpdateTime;
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger=Logger.getLogger(BltAction.class);

	private ConcurrentHashMap<String, AtomicBoolean> cmap = new ConcurrentHashMap<String, AtomicBoolean>();
	private ConcurrentHashMap<String, CloseableHttpClient> hmap = new ConcurrentHashMap<String, CloseableHttpClient>();

	@Autowired
	private CompanyDataDao companyDataDao;

	/**
	 * 获取验证码
	 * 
	 * @return
	 */
	@GetMapping(value = "/getCode")
	public String getCode() {
		CloseableHttpClient httpclient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
		InputStream imgCode = TaxConstants.getImgCode(httpclient,
				"https://soluia.sinosure.com.cn/cas/page/sol/image.jsp");
		String logincode = ImageUtil.encodeImgageToBase64(imgCode);
		hmap.put("httpclient", httpclient);
		return logincode;
	}

	/**
	 * 账户密码验证
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @param logincode
	 *            验证码
	 * @return
	 */
	@PostMapping(value = "/checkLogin")
	public GetDataResponse checkLogin(@RequestParam String username, @RequestParam String password,
			@RequestParam String logincode) {
		GetDataResponse getDataResponse = new GetDataResponse();
		String charset = "utf-8";
		String lt = null;
		String execution = null;
		String httpOrgCreateTest = null;
		
		//获取当前的年月日
		Calendar nowTime=Calendar.getInstance();
		int nowYear=nowTime.get(Calendar.YEAR);
		int nowMonth=nowTime.get(Calendar.MONTH)+1;
		int nowDay=nowTime.get(Calendar.DATE);
		//网站更新日期（日）
		int webUpdateDay=Integer.valueOf(webUpdateTime);
		logger.info("网站更新日期（日）"+webUpdateDay);
		//通过上次的更新时间判断是否需要更新
		MongoCollection<org.bson.Document> collection = MongoUtil.getDatabase().getCollection("c_updateTime");
		MongoCollection<org.bson.Document> collectionId = MongoUtil.getDatabase().getCollection("c_"+username);
		try {
			FindIterable<org.bson.Document> findIterable = collection.find(Filters.eq("companyId",username));
			List<org.bson.Document> list=new ArrayList<>();
			MongoCursor<org.bson.Document>  mongoCursor = findIterable.iterator();
			JSONObject jsonObject=null;
			while (mongoCursor.hasNext()) {
				String jsons=JSON.serialize(mongoCursor.next());
				jsonObject=JsonArrayUtils.objectToJson(jsons);
			}
			String updateTime=jsonObject.getString("updateTime");
			int lastUpdateYear=Integer.valueOf(updateTime.substring(0, 4));
			int lastUpdateMonth=Integer.valueOf(updateTime.substring(4,6));
			int lastUpdateDay=Integer.valueOf(updateTime.substring(6));
			int lastMonthNum=TimeUtils.getDaysByYearMonth(lastUpdateYear, lastUpdateMonth);
			logger.info("上次更新时间的月份"+lastUpdateMonth);
			//在不同的月份中两个时间段必须超过28天||在相同的月份中当前时间必须超过网站更新时间
			if(((nowMonth>lastUpdateMonth)&&(lastMonthNum-lastUpdateDay+nowDay)>28)||((nowMonth==lastUpdateMonth)&&(nowDay>webUpdateDay))){
				collection.deleteMany(Filters.eq("companyId",username));
				collectionId.drop();
			}
		} catch (Exception e) {
			logger.info("从未抓取过该公司", e);
		}
		
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		try {
			String response = TaxConstants.getMes(hmap.get("httpclient"),
					"https://soluia.sinosure.com.cn/cas/login?service=https%3A%2F%2Fsol.sinosure.com.cn%2Fbiz%2Fssologin.do%3Fmethod%3DssoLogin&systemtype=1");
			Document doc = Jsoup.parse(response);
			lt = doc.getElementsByAttributeValue("name", "lt").get(0).val();
			execution = doc.getElementsByAttributeValue("name", "execution").get(0).val();
			httpOrgCreateTest = "https://soluia.sinosure.com.cn" + doc.getElementById("fm1").attr("action");
		} catch (Exception e) {
			getDataResponse.setCode("400");
			getDataResponse.setMessage("登入失败");
			return getDataResponse;
		}

		Map<String, String> createMap = new HashMap<String, String>();
		createMap.put("username", username);
		createMap.put("password", password);
		createMap.put("logincode", logincode);
		createMap.put("signDataBase64", "");
		createMap.put("loginmethod", "");
		createMap.put("lt", lt);
		createMap.put("logintype", "1");
		createMap.put("execution", execution);
		createMap.put("_eventId", "submit");
		String response1 = httpClientUtil.doPost(hmap.get("httpclient"), httpOrgCreateTest, createMap, charset);
		response1 = TaxConstants.getMes(hmap.get("httpclient"),
				"https://sol.sinosure.com.cn/biz/mainFrame/index.jsp?investid=null");
		Document doc1 = Jsoup.parse(response1);
		Element welcome = doc1.getElementById("welcome");
		if ((response1.indexOf("出运") != -1) && (response1.indexOf("限额") != -1)) {
			getDataResponse.setCode("200");
			getDataResponse.setMessage("登入成功");
		} else {
			getDataResponse.setCode("400");
			getDataResponse.setMessage("登入失败");
		}

		return getDataResponse;
	}

	/**
	 * 数据抓取
	 * 
	 * @param httpclient
	 * @return
	 */
	public String addData(CloseableHttpClient httpclient,String username) {
		Map<String, Object> map=new HashMap<>();
		Calendar nowTime=Calendar.getInstance();
		int nowYear=nowTime.get(Calendar.YEAR);
		int nowMonth=nowTime.get(Calendar.MONTH)+1;
		int nowDay=nowTime.get(Calendar.DATE);
		String monthStr="";
		if(nowMonth<10){
			monthStr ="0"+String.valueOf(nowMonth);
		}else {
			monthStr=String.valueOf(nowMonth);
		}
		//更新时间
		String updateTimeStr=String.valueOf(nowYear)+monthStr+String.valueOf(nowDay);
		//创建更新时间
		MongoCollection<org.bson.Document> collection = MongoUtil.getDatabase().getCollection("c_updateTime");
		map.put("companyId", username);
		map.put("updateTime", updateTimeStr);
		collection.insertOne(new org.bson.Document(map));
		new QuotaQuery().getApprovedQuota(httpclient,username); 			// 批复限额 14001
		new ShipmentDeclarationQuery().getAcceptedDeclare(httpclient);		// 出运_已受理申报查询 14002   有信需要（投保金额  收汇金额）
		new SettlementQuery().getDamageClaim(httpclient,username); 			// 理赔 可损申请查询 14003
		new ShipmentQuery().getShipmentQuota(httpclient,username);			// 出运_出运查询 14004
		new QuotaBalanceQuery().getBalanceQuota(httpclient,username);	    // 限额_限额余额查询 14005
		return "success";
	}

	// ,@RequestParam String pathStr
	/**
	 * 对外提供数据接口
	 * 
	 * @param username 用户名
	 * @param password 密码
	 * @param logincode 验证码
	 * @param type	类型
	 * @return
	 */
	@GetMapping(value = "/getData")
	public GetDataResponse getData(@RequestParam String username, @RequestParam String password,
			@RequestParam String logincode, @RequestParam String type, @RequestParam String pathStr) {
		GetDataResponse getDataResponse = new GetDataResponse();
		if (cmap.get(username) == null) {
			synchronized (cmap) {
				if (cmap.get(username) == null) {
					getDataResponse = checkLogin(username, password, logincode);
					cmap.put(username, new AtomicBoolean(true));
				}
			}
		} else if (cmap.get(username).get() && getDataResponse.getCode() != null) {
			getDataResponse.setCode("200");
			getDataResponse.setMessage("processing");
			return getDataResponse;
		} else if (!cmap.get(username).get()) {
			getDataResponse.setCode("200");
			getDataResponse.setData(companyDataDao.getDataByType(username, type));
			cmap.remove(username);
			hmap.remove("httpclient");
			return getDataResponse;
		}

		if (getDataResponse.getCode() != null || ("200").equals(getDataResponse.getCode())) {
			List<org.bson.Document> data = companyDataDao.getDataByType(username, type);
			if (!data.isEmpty()) {
				for (org.bson.Document document : data) {
					Set<String> keys = document.keySet();
					for (String key : keys) {
						if (document.get(key) != null && document.get(key) instanceof List
								&& !((List) document.get(key)).isEmpty()) {
							getDataResponse.setData(data);
							if (cmap.get(username) != null) {
								cmap.get(username).set(false);
								// cmap.remove(username);
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
							if ("success".equals(addData(hmap.get("httpclient"),username))) {
								List<org.bson.Document> data = companyDataDao.getDataByType(username, type);
								JSONArray json = JsonArrayUtils.objectToArrray(data);
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("jsonStr", json.toString());
								// 回调给对方 上线时需要将这个接口地址改改
								String result = TaxConstants.postMes(HttpClients.createDefault(),
										dataGrabUrl + pathStr + "/callBack/" + username + "?token="+token,
										map);
								System.out.println(result);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (cmap.get(username) != null) {
								cmap.get(username).set(false);
								hmap.remove("httpclient");
							}
						}
					}
				}).start();
				getDataResponse.setMessage("processing");
				return getDataResponse;
			}
		} else {
			if (cmap.get(username) != null) {
				cmap.get(username).set(false);
				cmap.remove(username);
				hmap.remove("httpclient");
			}
		}
		return getDataResponse;
	}
}
