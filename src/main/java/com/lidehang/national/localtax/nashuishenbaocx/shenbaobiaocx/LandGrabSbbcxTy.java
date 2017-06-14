package com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.action.DsAction;
import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.national.util.CreateImgCodeUtil;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;
import com.lidehang.national.util.TimeUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import sun.misc.BASE64Decoder;
/**
 * 地税--纳税申申报表--通用申报表
 * @author Hobn
 *
 */
public class LandGrabSbbcxTy {
	private static Logger logger = Logger.getLogger(LandGrabSbbcxTy.class);
	public String selectLandTaxByDate(CloseableHttpClient httpclient, String userId,String username){
		logger.info("地税--纳税申申报表--通用申报表抓取");
		List<org.bson.Document> list = new ArrayList<>();
		//&yzpzzlDm=BDA0610100   &yzpzzlMc=《通用申报表》      http://www.zjds-etax.cn/wsbs/api/sb/sbb?sbbz=Y&skssqq=2016-10-01&skssqz=2016-12-31&yzpzzlDm=BDA0610100
		String response = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/sb/sbb?sbbz=Y&skssqq=2017-01-01&skssqz=2017-05-16&yzpzzlDm=BDA0610100",userId);
		JSONArray array = JsonArrayUtils.objectToArrray(response);
	/*	 //当前时间  年 月
	     Calendar nowTime=Calendar.getInstance();
	     int year=nowTime.get(Calendar.YEAR);   
	     int month=nowTime.get(Calendar.MONTH);  
	     //起始日期
	     String date1="2016";
	     String date2="10";
	     String date3="01";
	     String startTime=date1+"-"+date2+"-"+date3;
	     int dateInt1=Integer.parseInt(date1);  
	     int dateInt2=Integer.parseInt(date2)-1;    
	     int dateInt3=Integer.parseInt(date3);    
	     //每年    2016         2017
	     for(int q=dateInt1;q<=year;q++){
	    	 date2="01";
	    	 int changeMonth=0;
	    	 if(q==year){   
	    		 changeMonth=month;  
	    	 }else{  
	    		 changeMonth=12;     
	    	 }
	    	 //每月
	    	 for(int p=dateInt2;p<changeMonth;p++){
	    		 //从2016-10-01开始         20   16    10    01   2000-1900   0:一月
	     	     Date startTimeF = new Date(Integer.parseInt(date1),Integer.parseInt(date2),1);
	     	     int days=new TimeUtils().dayReport(startTimeF);
	    	     date3=String.valueOf(days);  //最大天数
	    	     //2000-01
	    	     String beginDate=date1+"-"+date2+"-"+"01";
	    	     String endDate=date1+"-"+date2+"-"+date3;
	    	     
	    	    Map<String, String> quotaMap=new HashMap<String, String>();
	    	    quotaMap.put("beginDate",beginDate);  
	  	        quotaMap.put("endDate",endDate);                 
	  	        quotaMap.put("curPageNum","");      
	  	   //统计笔数 用于分页
	   	     Map<String, String> countMap=new HashMap<String, String>();
	   	     countMap.put("method", "count");
	   	     countMap.putAll(quotaMap);
	   	     String numberAndAmount=httpClientUtil.doPost(httpclient, action1, countMap, charset);
	   	     String number=Jsoup.parse(numberAndAmount).getElementsByClass("listdata").get(1).text();
	   	     int count=Integer.valueOf(number).intValue();
	   	     if (count==0)  continue;
	   	     int pageNum=count/10+1;
	   	     for (int i = 0; i < pageNum; i++) {
	   	    	 quotaMap.put("curPageNum",String.valueOf(i));  
	   	    	String declarationList=httpClientUtil.doPost(httpclient, action1, quotaMap, charset);
	   	   	      Document doc=Jsoup.parse(declarationList);
	   	   	      Element lists= doc.getElementById("listTable");
	   	   	      Elements trs=lists.select("tr");
	   	   	      for (int k = 1; k < trs.size(); k++) {
	   	   	    	  Element td=trs.get(k);
	   	   	    	  Elements tds=td.select("td");
	   	   	    	  Map<String, Object> resultMap=new HashMap<String,Object>();
	   	   	          resultMap.put("serialNumber", "15001");
	   	   	    	  String index="15001001";
	   	 			for (int j = 0; j < tds.size(); j++) {
	   	 				String value=tds.get(j).text();
	   	 				resultMap.put(index, value);
	   	 				index=String.valueOf(Long.parseLong(index)+1);
	   	 			  }
	   	 			String reportNO= (String) resultMap.get("15001002");
	   	 			list.add(CompanyDataUtil.toDocument(resultMap,parseListDetails(index, httpclient, jumpUrl,reportNO)));
	   	 		  }
	   	     }
	    	     date2=String.valueOf(Integer.parseInt(1+date2)+1).substring(1, 3);
	    	 }
	    	 date1= String.valueOf((Integer.parseInt(date1)+1)); 
	     }
  		return list;
		*/
		for(Object object:array){
			JSONObject json1 = JsonArrayUtils.objectToJson(object);
			Map<String, Object> baseMap=parseLssb(json1);
			//http://www.zjds-etax.cn/wsbs/api/sb/tysbb?pzxh=10023316000026920932
			response = TaxConstants.getMes(httpclient,"http://www.zjds-etax.cn/wsbs/api/sb/tysbb?pzxh="+json1.getString("pzxh"),userId);
			Map<String, Object> map1=parseLSSB(response);
			list.add(CompanyDataUtil.toDocument(baseMap,map1));
			new CompanyDataDaoImpl().addData(username, "11002", list);
		}
		//System.out.println(response);
		return null;
	}

	private Map<String, Object> parseLSSB(String str) {
		JSONArray array = JsonArrayUtils.objectToArrray(str);
		List<Object> list=new ArrayList<Object>();
		Map<String, Object> map1=new HashMap<String,Object>();
		String index="11002005";
		for (Object object : array) {
			Map<String, Object> map=new HashMap<String, Object>();
			JSONObject json=JsonArrayUtils.objectToJson(object);
			Iterator<Object> it=json.keys();
			String index1="11002005001";
			while(it.hasNext()){
				String key=it.next().toString();
				map.put(index1, json.getString(key));
				index1=String.valueOf((Long.parseLong(index1)+1));
			}
			if(map.size()>0){
				list.add(map);
			}
		}
		map1.put(index, list);
		return map1;
	}

	private Map<String, Object> parseLssb(JSONObject json) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("11002001", json.getString("yzpzzl"));
		map.put("11002002", json.getString("sbrq"));
		map.put("11002003", json.getString("ybtse"));
		map.put("11002004", json.getString("kkrq"));
		map.put("serialNumber", "11002");
		return map;
	}
}
