package com.lidehang.action;


import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServlet;

import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lidehang.data.collection.dao.CompanyDataDao;
import com.lidehang.data.collection.model.param.GSSiteParams;
import com.lidehang.data.collection.returnInfo.GetDataResponse;
import com.lidehang.data.collection.service.gs.GSModuleBase;
import com.lidehang.data.collection.service.gs.module.BdcfqdkHandler;
import com.lidehang.data.collection.service.gs.module.CglHandler;
import com.lidehang.data.collection.service.gs.module.Dkdj_dkqdHandler;
import com.lidehang.data.collection.service.gs.module.DkjxsemxHandler;
import com.lidehang.data.collection.service.gs.module.DklHandler;
import com.lidehang.data.collection.service.gs.module.Flzl1Handler;
import com.lidehang.data.collection.service.gs.module.Flzl2Handler;
import com.lidehang.data.collection.service.gs.module.Flzl3Handler;
import com.lidehang.data.collection.service.gs.module.Flzl4Handler;
import com.lidehang.data.collection.service.gs.module.FwjcqdHandler;
import com.lidehang.data.collection.service.gs.module.FzzcHandler;
import com.lidehang.data.collection.service.gs.module.GdzcjxsedkmxHandler;
import com.lidehang.data.collection.service.gs.module.GdzcjxsedkqkHandler;
import com.lidehang.data.collection.service.gs.module.HgwspzHandler;
import com.lidehang.data.collection.service.gs.module.Jms_sbmxbHandler;
import com.lidehang.data.collection.service.gs.module.LrbHandler;
import com.lidehang.data.collection.service.gs.module.SbbHandler;
import com.lidehang.data.collection.service.gs.module.Xgmsbb2005Handler;
import com.lidehang.data.collection.service.gs.module.XgmsybHandler;
import com.lidehang.data.collection.service.gs.module.XgmzcfzbHandler;
import com.lidehang.data.collection.service.gs.module.ZsgrlxsdsHandler;
import com.lidehang.data.collection.service.gs.module.ZsqysdsHandler;
import com.lidehang.data.collection.service.gs.module.ZswqsdsHandler;
import com.lidehang.data.collection.service.gs.module.ZsxfsHandler;
import com.lidehang.data.collection.service.gs.module.ZszzsHandler;
import com.lidehang.data.collection.service.gs.module04.A000000_Qyxxjcb1Handler;
import com.lidehang.data.collection.service.gs.module04.A000000_Qyxxjcb2Handler;
import com.lidehang.data.collection.service.gs.module04.A000000_Qyxxjcb3Handler;
import com.lidehang.data.collection.service.gs.module04.A100000_SdsnssbmHandler;
import com.lidehang.data.collection.service.gs.module04.A101010_YbqysrmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A102010_YbqyzcmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A104000_QjfymxbHandler;
import com.lidehang.data.collection.service.gs.module04.A105000_NstzmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A105050_ZgxctzmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A105080_ZczjtxmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A105081_GdzczjmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A106000_QysdsmbmsbHandler;
import com.lidehang.data.collection.service.gs.module04.A107020_SdjmyhmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A107040_JmsdsyhmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Bzssjlmxb_fb1Handler;
import com.lidehang.data.collection.service.gs.module04.Fb10_zcjztzmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb11_gqssmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb1_srmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb2_cbfymxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb3_nstzmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb4_qymbksmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb5_ssyhmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb6_jwdmmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb7_zcnstzbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb8_kndnstzbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb9_zjnstzmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Gdzczjtjb_fb2Handler;
import com.lidehang.data.collection.service.gs.module04.Jmsdsmxb_fb3Handler;
import com.lidehang.data.collection.service.gs.module04.Nzqysds_jdAlHandler;
import com.lidehang.data.collection.service.gs.module04.Qysds_sbbHandler;
import com.lidehang.data.collection.service.gs.module307.Dwzfkxqkb_b9Handler;
import com.lidehang.data.collection.service.gs.module307.Gdzcb_b6Handler;
import com.lidehang.data.collection.service.gs.module307.Glgxb_b1Handler;
import com.lidehang.data.collection.service.gs.module307.Gljyhzb_b2Handler;
import com.lidehang.data.collection.service.gs.module307.Gxb_b3Handler;
import com.lidehang.data.collection.service.gs.module307.Lwb_b4Handler;
import com.lidehang.data.collection.service.gs.module307.Rtzjb_b7Handler;
import com.lidehang.data.collection.service.gs.module307.Wxzcb_b5Handler;
import com.lidehang.data.collection.service.gs.module309.Zcssqd_sbmxHandler;
import com.lidehang.data.collection.service.gs.module309.Zcsszx_sbmxHandler;
import com.lidehang.data.collection.service.gs.moduleDianZiJiaoFei.DzjkpzHandler;
import com.lidehang.data.collection.service.gs.site.GSSiteHandler;
import com.lidehang.data.collection.util.VPNSuffixUtil;
import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.core.util.MongoUtil;
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
 * 国税数据抓取
 * @author Hobn
 *
 */
@RestController
@RequestMapping(value="/GsAction")
public class GsAction extends HttpServlet{
	@Value("${youxin.dataGrab}")
	private String dataGrabUrl;
	
	@Value("${youxin.token}")
	private String token;
	//国税网站的更新日期
	@Value("${youxin.gs.webUpdateTime}")
	private String webUpdateTime;
	
	private static Logger logger =Logger.getLogger(GsAction.class);

	private static final long serialVersionUID = 1L;
	
	private ConcurrentHashMap<String, AtomicBoolean> cmap=new ConcurrentHashMap<String,AtomicBoolean>();

	@Autowired
	private CompanyDataDao companyDataDao;
	
	/**
	 * 账户密码验证
	 * @param companyId			纳税人识别号
	 * @param nationalTaxPwd 	原网上申报密码
	 * @param vpnAccount 	    VPN账户
	 * @param vpnPwd			VPN密码
	 * @return
	 */
	@GetMapping(value="/checkLogin")
	@ResponseBody
	public String checkLogin(@RequestParam String companyId,@RequestParam String nationalTaxPwd,@RequestParam String vpnAccount,@RequestParam String vpnPwd){
		GSSiteParams params= new GSSiteParams();
//		String vpnUser=VPNSuffixUtil.getVpnUser(companyId);
		String nationalTaxUser=companyId.toUpperCase();
		//这边传的是日期  需要改变 最初的起始日期
		String startTimeStr="20100101";
		//获取当前的年月日
		Calendar nowTime=Calendar.getInstance();
		int nowYear=nowTime.get(Calendar.YEAR);
		int nowMonth=nowTime.get(Calendar.MONTH)+1;
		int nowDay= nowTime.get(Calendar.DATE);
		String monthStr="";
		if(nowMonth<10){
			monthStr="0"+String.valueOf(nowMonth);
		}else {
			monthStr=String.valueOf(nowMonth);
		}
		//获取本月的最大天数
		int dayNum=TimeUtils.getDaysByYearMonth(nowYear, nowMonth);
		//网站更新日期（日）
		int webUpdateDay=Integer.valueOf(webUpdateTime);
		logger.info("网站更新日期："+webUpdateDay);
		//终止日期
		String endTimeStr=String.valueOf(nowYear)+monthStr+String.valueOf(dayNum);
		//更新日期 看看nowday 03 08
		String updateTimeStr = String.valueOf(nowYear)+monthStr+String.valueOf(nowDay);;
		//通过上次的更新时间判断是否需要更新
			MongoCollection<Document> collection = MongoUtil.getDatabase().getCollection("c_updateTime");
			MongoCollection<Document> collectionId = MongoUtil.getDatabase().getCollection("c_"+companyId);
			try {
				FindIterable<Document> findIterable = collection.find(Filters.eq("companyId", companyId));
				List<Document> list = new ArrayList<>();
				MongoCursor<Document> mongoCursor = findIterable.iterator();  
				JSONObject jsonObject=null;
				while(mongoCursor.hasNext()){
					String jsons=JSON.serialize(mongoCursor.next());
					jsonObject= JsonArrayUtils.objectToJson(jsons);
					logger.info(jsonObject);
				}
				logger.info("jsonObject:"+jsonObject.toString());
				String updateTime=jsonObject.getString("updateTime");
				logger.info("updateTime:"+updateTime);
				int lastUpdateYear=Integer.valueOf(updateTime.substring(0, 4));
				int lastUpdateMonth=Integer.valueOf(updateTime.substring(4, 6));
				int lastUpdateDay=Integer.valueOf(updateTime.substring(6));
				int lastMonthDayNum=TimeUtils.getDaysByYearMonth(lastUpdateYear, lastUpdateMonth);
				
				logger.info(lastUpdateMonth);
				//在不同的月份中 两个时间段必须超过28天 ||  在相同的月份中当前时间必须超过网站更新时间
				if((nowMonth>lastUpdateMonth&&((lastMonthDayNum-lastUpdateDay+nowDay)>30)) || (nowMonth==lastUpdateMonth&&nowDay>webUpdateDay)){
					collection.deleteMany(Filters.eq("companyId", companyId));
					collectionId.drop();
				}
				
			} catch (Exception e) {
				logger.info("从未抓取过该公司", e);
			}
		params.setCompanyId(companyId);
		params.setVpnUser(vpnAccount);
		params.setVpnPwd(vpnPwd);
		params.setNationalTaxUser(nationalTaxUser);
		params.setNationalTaxPwd(nationalTaxPwd);
		params.setStartTimeStr(startTimeStr);
		params.setEndTimeStr(endTimeStr);
     	GSSiteHandler handler = new GSSiteHandler();
		handler.init(params, null);
		return handler.doCheck();
	}
	
	/**
	 * 数据抓取
	 * @param companyId			纳税人识别号
	 * @param nationalTaxPwd 	原网上申报密码
	 * @param vpnAccount		VPN账户
	 * @param vpnPwd			VPN密码
	 * @return
	 */
	@GetMapping(value="/addData")
	@ResponseBody
	public String addData(@RequestParam String companyId,@RequestParam String nationalTaxPwd,@RequestParam String vpnAccount,@RequestParam String vpnPwd){
		Map<String, Object> map=new HashMap<String,Object>();
		GSSiteParams params= new GSSiteParams();
//		String vpnUser=VPNSuffixUtil.getVpnUser(companyId);
		String nationalTaxUser=companyId.toUpperCase();
		//这边传的是日期  需要改变
		String startTimeStr="20100101";
//		String startTimeStr="20130101";
		Calendar nowTime=Calendar.getInstance();
		int nowYear=nowTime.get(Calendar.YEAR);
		int nowMonth=nowTime.get(Calendar.MONTH)+1;
		int nowDay= nowTime.get(Calendar.DATE);
		String monthStr="";
		if(nowMonth<10){
			monthStr="0"+String.valueOf(nowMonth);
		}else {
			monthStr=String.valueOf(nowMonth);
		}
		int dayNum=TimeUtils.getDaysByYearMonth(nowYear, nowMonth);
		String endTimeStr=String.valueOf(nowYear)+monthStr+String.valueOf(dayNum);
		//更新日期
	    String updateTimeStr = String.valueOf(nowYear)+monthStr+String.valueOf(nowDay);
		//创建updateTime
		MongoCollection<Document> collection = MongoUtil.getDatabase().getCollection("c_updateTime");
		map.put("companyId", companyId);
		map.put("updateTime", updateTimeStr);
		collection.insertOne(new Document(map));
		
		params.setCompanyId(companyId);
		params.setVpnUser(vpnAccount);
		params.setVpnPwd(vpnPwd);
		params.setNationalTaxUser(nationalTaxUser);
		params.setNationalTaxPwd(nationalTaxPwd);
		params.setStartTimeStr(startTimeStr);
		params.setEndTimeStr(endTimeStr);
		List<GSModuleBase<GSSiteHandler>> list=new ArrayList<>();
	    list.add(new SbbHandler());                     //10001   	获取解析存储申报信息 --增值税申报表      销售额
		list.add(new DklHandler());                     //10002   	增值税一般纳税人-- 抵扣联明细【最近六个月销项明细表】
		list.add(new CglHandler());                     //10003   	增值税一般纳税人-- 存根联明细【最近六个月销项明细表】
//    list.add(new Flzl1Handler());                     //10004
		list.add(new FzzcHandler());                    //10005               资产负债表
	    list.add(new LrbHandler());                     //10006             （净）利润表
        /*list.add(new ZszzsHandler());                 //10007
		list.add(new ZsxfsHandler());                   //10008
		list.add(new ZsqysdsHandler());                 //10009
		list.add(new ZswqsdsHandler());                 //10010
		list.add(new ZsgrlxsdsHandler());      			//10011
		list.add(new Flzl2Handler());                   //10012
		list.add(new GdzcjxsedkqkHandler());            //10013   */        
		list.add(new GdzcjxsedkmxHandler());            //10014		  固定资产进项税额抵扣明细表 
	/*	list.add(new Flzl3Handler());                   //10015
		list.add(new Flzl4Handler());                   //10016
		list.add(new Dkdj_dkqdHandler());               //10017    */
		list.add(new Jms_sbmxbHandler());  				//10018               出口免税（增值税减免税申报表的免税项目）
	/*	list.add(new BdcfqdkHandler());                 //10019
		list.add(new DkjxsemxHandler());     			//10020 
		list.add(new FwjcqdHandler());					//10021 
		list.add(new Xgmsbb2005Handler());  			//10022 
		list.add(new XgmzcfzbHandler());             	//10023 */
		list.add(new XgmsybHandler());      		    //10024           损益表（利润表）
	/*	list.add(new Zcssqd_sbmxHandler()); 			//10025  
		list.add(new Zcsszx_sbmxHandler());				//10026 
		list.add(new Glgxb_b1Handler());        		//10027 
		list.add(new Gljyhzb_b2Handler());      		//10028 
		list.add(new Gxb_b3Handler());     				//" "
		list.add(new Lwb_b4Handler());              	//10030 
		list.add(new Wxzcb_b5Handler());        	    //使用权  所有权
		list.add(new Gdzcb_b6Handler());                //10032 
		list.add(new Rtzjb_b7Handler());                //10033 
		list.add(new Dwzfkxqkb_b9Handler());            //10034
	    list.add(new Nzqysds_jdAlHandler());            //10035
	    list.add(new Qysds_sbbHandler()); 		        //10036 */
		list.add(new Fb1_srmxbHandler());    			//10037      企业所得税和非居民企业所得税(包括季报和年报)--附表一（1）——收入明细表
		list.add(new Fb2_cbfymxbHandler());				//10038       企业所得税和非居民企业所得税(包括季报和年报)--附表二（1）——成本费用明细表
	/*	list.add(new Fb3_nstzmxbHandler());				//10039
		list.add(new Fb4_qymbksmxbHandler());			//10040   
		list.add(new Fb5_ssyhmxbHandler());				//10041
		list.add(new Fb6_jwdmmxbHandler());				//10042
		list.add(new Fb7_zcnstzbHandler());				//10043
		list.add(new Fb8_kndnstzbHandler());			//10044
		list.add(new Fb9_zjnstzmxbHandler());			//10045
		list.add(new Fb10_zcjztzmxbHandler());			//10046
		list.add(new Fb11_gqssmxbHandler());			//10047
	    list.add(new Bzssjlmxb_fb1Handler());			//10048
		list.add(new Jmsdsmxb_fb3Handler());			//10049
		list.add(new Gdzczjtjb_fb2Handler());		    //10050
		list.add(new A000000_Qyxxjcb1Handler());        //10051 
		list.add(new A000000_Qyxxjcb2Handler());        //10052
		list.add(new A000000_Qyxxjcb3Handler());        //10053
		list.add(new A100000_SdsnssbmHandler());   	    //10054 
		list.add(new A101010_YbqysrmxbHandler());	    //10055
		list.add(new A102010_YbqyzcmxbHandler());	    //10056
		list.add(new A104000_QjfymxbHandler());			//10057
		list.add(new A105000_NstzmxbHandler());		    //10058
		list.add(new A105050_ZgxctzmxbHandler());		//10059
	    list.add(new A105080_ZczjtxmxbHandler());		//10060
		list.add(new A105081_GdzczjmxbHandler());		//10061
	    list.add(new A106000_QysdsmbmsbHandler());		//10062
	 	list.add(new A107020_SdjmyhmxbHandler());		//10063
		list.add(new A107040_JmsdsyhmxbHandler());		//10064
*/		
		list.add(new DzjkpzHandler());		//10066                 所得税 增值税
		list.add(new HgwspzHandler());      //10067                海关完税凭证抵扣清单
     	GSSiteHandler handler = new GSSiteHandler();
		handler.init(params, list);
		return handler.doGet();
	}
	
	//,@RequestParam String pathStr
	/**
	 * 对外提供数据接口
	 * @param companyId			纳税人识别号
	 * @param nationalTaxPwd 	原网上申报密码
	 * @param vpnAccount		VPN账户
	 * @param vpnPwd			VPN密码
	 * @param type              类型
	 * @param pathStr           解析的表
	 * @return
	 */
	@GetMapping(value = "/getData")
	@ResponseBody   
		public GetDataResponse getData(@RequestParam String companyId,@RequestParam String nationalTaxPwd,@RequestParam String vpnAccount,@RequestParam String vpnPwd,@RequestParam String type,@RequestParam String pathStr) {
		GetDataResponse getDataResponse=new GetDataResponse();
		if(cmap.get(companyId)==null){
			synchronized (cmap) {
				if(cmap.get(companyId)==null){
					cmap.put(companyId, new AtomicBoolean(true));
				}
			}
		}else if(cmap.get(companyId).get()){
			getDataResponse.setCode("200");
			getDataResponse.setMessage("processing");
			return getDataResponse;
		}else if(!cmap.get(companyId).get()){
			cmap.remove(companyId);
		}
		
		String msg=checkLogin(companyId, nationalTaxPwd,vpnAccount, vpnPwd);
		getDataResponse.setMessage(msg);
		if(msg.endsWith("失败")){
			getDataResponse.setCode("400");
		}else{
			getDataResponse.setCode("200");
		}
		
		//对方回调我   cmap.get(companyId).get()  为true
		if(msg.startsWith("登入成功")){
		List<Document> data=companyDataDao.getDataByType(companyId,type);
		if(!data.isEmpty()){
		for (Document document : data) {
			Set<String> keys= document.keySet();
			for (String key : keys) {
				if(document.get(key)!=null&& document.get(key) instanceof List && !((List)document.get(key)).isEmpty()){
					getDataResponse.setData(data);
					if(cmap.get(companyId)!=null){
						cmap.get(companyId).set(false);
					}
					return getDataResponse;
				}
			}
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String stateCode=addData(companyId, nationalTaxPwd, vpnAccount, vpnPwd);
					if("success".equals(stateCode)){
						List<Document> data=companyDataDao.getDataByType(companyId,type);
						JSONArray json=JsonArrayUtils.objectToArrray(data);
						Map<String, Object> map=new HashMap<String,Object>();
						map.put("jsonStr", json.toString());
						//dcsCgsBalanceDetails  回调对方的接口
						String aurlllsss=dataGrabUrl+pathStr+"/callBack/"+companyId+"?token="+token;
						logger.info(aurlllsss);
						String response=TaxConstants.postMes(HttpClients.createDefault(), dataGrabUrl+pathStr+"/callBack/"+companyId+"?token="+token, map);
						logger.info("callBack:"+response);
					}else{
						//这是多线程  通过回调给对方值
						Map<String, Object> map=new HashMap<String,Object>();
						map.put("code", 0004);
						map.put("msg", stateCode);//国税系统维护中
						//dcsCgsBalanceDetails  回调对方的接口 上线时需要将这个接口地址改改
//						String aurlll=dataGrabUrl+pathStr+"/callBack/"+companyId+"?token="+token;
						String response=TaxConstants.postMes(HttpClients.createDefault(), dataGrabUrl+pathStr+"/callBack/"+companyId+"?token="+token, map);
						System.out.println(response);
					}
				} catch (Exception e) {
					logger.error("抓取异常", e);
				}finally {
					if(cmap.get(companyId)!=null){
						cmap.get(companyId).set(false);
						cmap.remove(companyId);
					}
				}
			}
		}).start();
		getDataResponse.setMessage("processing");
		return getDataResponse;
		}
	}else{
		if(cmap.get(companyId)!=null){
			cmap.get(companyId).set(false);
		}
	}
		return getDataResponse;
	}
}
	
