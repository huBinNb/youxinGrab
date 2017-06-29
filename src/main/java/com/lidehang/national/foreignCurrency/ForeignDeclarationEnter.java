package com.lidehang.national.foreignCurrency;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.PortableInterceptor.LOCATION_FORWARD;

import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.util.TaxConstants;
import com.lidehang.national.util.TimeUtils;

/** 外汇
 * 涉外收入申报表--申报信息录入
 * 
 * @author Hobn
 *
 */
public class ForeignDeclarationEnter{
	
	   private static Logger logger=Logger.getLogger(ForeignDeclarationEnter.class);
	   //申报信息录入
       public String  getInformation(HttpClient httpclient,String organizationCode){
    	   logger.info("外汇-- 涉外收入申报表--申报信息录入抓取开始");
    		//http://asone.safesvc.gov.cn/BizforCustomerWeb/servlet/raBaseInfoSearch?current_appCode=BZCN&asone_addr=asone.safesvc.gov.cn:80
    	   String url="http://asone.safesvc.gov.cn/BizforCustomerWeb/servlet/raBaseInfoSearch?current_appCode=BZCN&asone_addr=asone.safesvc.gov.cn:80";
  	       String response=TaxConstants.getMes(httpclient, url);
//  	       logger.info("相应的值："+response);
  	       List<org.bson.Document>  list= parseList(httpclient,response);
  	       new CompanyDataDaoImpl().addData(organizationCode, "15004", list);
  	       logger.info("外汇-- 涉外收入申报表--申报信息录入抓取结束");
  	       return null;
       }
       
       
       /**
        * 已申报（已审核）信息查询   列表
        * @param response
        * @param httpclient
        * @return
        */
       private List<org.bson.Document> parseList(HttpClient httpclient,String response) {
    	 List<org.bson.Document> list = new ArrayList<>();
 	   	   	      Document doc=Jsoup.parse(response);
 	   	   	      Element lists= doc.getElementById("listTable");
 	   	   	      Elements trs=lists.select("tr");
 	   	   	      for (int k = 1; k < trs.size(); k++) {
 	   	   	    	  Element td=trs.get(k);
 	   	   	    	  Elements tds=td.select("td");
 	   	   	    	  Map<String, Object> resultMap=new HashMap<String,Object>();
 	   	   	          resultMap.put("serialNumber", "15004");
 	   	   	    	  String index="15004001";
 	   	 			for (int j = 0; j < tds.size(); j++) {
 	   	 				String value=tds.get(j).text();
 	   	 				resultMap.put(index, value);
 	   	 				index=String.valueOf(Long.parseLong(index)+1);
 	   	 			  }
 	   	 			list.add(CompanyDataUtil.toDocument(resultMap));
 	   	 		  }
   		return list;
   	}
}








