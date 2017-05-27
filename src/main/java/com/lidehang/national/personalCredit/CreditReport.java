package com.lidehang.national.personalCredit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;
import net.sf.json.JSONObject;

/**
 * 个人信用信息服务平台      需要测试  
 * @author Hobn
 *
 */
public class CreditReport {
	   //个人信用报告
       public String  getPersonalCreditReport(String creditPage,String userName){
    	   List<org.bson.Document> list=new ArrayList<org.bson.Document>();
    	   Map<String, Object> personalCreditMap=new HashMap<>();
    	   
    	   String index="21001001";
    	   Document doc= Jsoup.parse(creditPage);
    	   
    	   
    	   //基础数据
    	   Elements basicData=doc.getElementsByClass("p");
    	   for (int i = 0; i < basicData .size()-51; i++) {
			String value=basicData.get(i).text();
    		   if(value.indexOf(":")!=-1){
    			   value=value.substring(value.indexOf(":"));
    		   }
    	     personalCreditMap.put(index, value);
    	     index=String.valueOf(Long.parseLong(index)+1);
		}
    	   
    	  //信息概要
    	   Elements informationSummary=doc.select("[height=155]");
    	   Elements trs= informationSummary.select("tr");
    	   List<Object> summaryList=new ArrayList<>();
    	   for (int j = 1; j < trs.size(); j++) {
    		Map<String, String> summaryMap=new HashMap<>();   
			Elements tds=trs.get(j).select("td");
			String index1=String.valueOf((Long.parseLong(index)*1000+001));
    		   for (int k = 0; k < tds.size(); k++) {
				String value=tds.get(k).text();
				summaryMap.put(index1, value);
    			index1=String.valueOf(Long.parseLong(index1)+1);
			}
    		  summaryList.add(summaryMap);
		}
    	   personalCreditMap.put(index, summaryList);
    	   index=String.valueOf(Long.parseLong(index)+1);
    	   
    	   
    	 //机构查询记录明细
    	   Elements recordDetails=doc.select("[style=margin-top: 12px]");
    	   Elements trs1= recordDetails.select("tr");
    	   List<Object> detailsList=new ArrayList<>();
    	   for (int q = 0; q < trs1.size(); q++) {
    		Map<String, String> detailsMap=new HashMap<>();
    	   	Elements tds=trs1.get(q).select("td");
    	   	String index1=String.valueOf((Long.parseLong(index)*1000+001));
    	   	for (int p = 0; p < tds.size(); p++) {
    	   		String value=tds.get(p).text();
    	   		detailsMap.put(index1, value);
    	   	}
    	   	detailsList.add(detailsMap);
    	   }
    	   personalCreditMap.put(index, detailsList);
    	   
    	   
    	   
    	   list.add(CompanyDataUtil.toDocument(personalCreditMap));
    	   
    	
			
    	   new  CompanyDataDaoImpl().addSinosureData(userName, "21001", list);
    	   return null;
       }
       

       /**
        * 批复的查询列表
        * @param response
        * @param httpclient
        * @return
        */
       /*private List<org.bson.Document> parseList(String response,HttpClient httpclient) {
        * 
        *   
        * 
        * 
        * 
        * 
        * 
        * 
    	   List<org.bson.Document> list = new ArrayList<>();
    	   Document doc=Jsoup.parse(response);
    	   Element powerTable= doc.getElementById("PowerTable");
           Elements trs = powerTable.select("table").select("tr");
           for(int i = 1;i<trs.size();i++){
        	   Map<String, Object> resultMap=new HashMap<String, Object>();
        	   resultMap.put("serialNumber", "14002");
        	   String index="14002001";
               Elements tds = trs.get(i).select("td");
               String action=tds.select("a").get(0).attr("onClick");//查看的url
               String viewUrl=action.substring(action.indexOf("('")+2,action.indexOf("')"));
               for(int j = 1;j<tds.size();j++){
            	   String value = tds.get(j).text();
            	   if(value.equals(" ")){
            		   value= value.replaceAll(" ", "").trim();
            	   }
            	   resultMap.put(index, value);
            	   index=String.valueOf(Long.parseLong(index)+1);
               }
               list.add(CompanyDataUtil.toDocument(resultMap,parseShipmentDetails(viewUrl, index,httpclient)));
           }
   		return list;
   	}*/
/*
       *//**
        * 出运明细解析
        * @param response
        * @return
        *//*
       private Map<String, Object> parseShipmentDetails(String viewUrl,String index,HttpClient httpclient){
    	String url="https://sol.sinosure.com.cn"+viewUrl;
    	List<Object> list=new ArrayList<Object>();
    	Map<String, Object> detailsMap=new HashMap<String,Object>();
    	String response=TaxConstants.getMes(httpclient, url);
    	Document doc=Jsoup.parse(response);
    	Elements tables=doc.select("[bordercolordark=#FFFFFF]");
    	for(int i=0;i<tables.size();i++){
    		Map<String, Object> aloneMap=new HashMap<String,Object>();
    		String index1=String.valueOf((Long.parseLong(index) * 1000 + 001));
    		String index2=index1;
    		Element table=tables.get(i);
    		if(i==0){  //14002008
    			Elements trS=table.select("tr");
    			for (int j = 0; j < trS.size(); j++) {
        			Elements tds=trS.get(j).select("td");
        			for (int j2 = 1; j2 < tds.size(); j2++) {
        				String value=tds.get(j2).text();
        				if(value.equals(" ")){
                 		   value= value.replaceAll(" ", "").trim();
                 	   }
        				aloneMap.put(index1, value);
        				index1=String.valueOf(Long.parseLong(index1)+1);
        				j2++;
        			}
        		}
    			detailsMap.put(index, aloneMap);
    		}else{  //14002009
    			Elements trS=table.getElementsByClass("jp_2");
    			for (int j = 0; j < trS.size(); j++) {
    				index1=index2;
        			Elements tds=trS.get(j).select("td");
        			aloneMap=new HashMap<String,Object>();
        			for (int j2 = 1; j2 < tds.size(); j2++) {
        				if(j2==tds.size()-1){
            				Elements hrefs=tds.get(j2).getElementsByTag("a");
        					String href=hrefs.get(0).attr("href");
        					aloneMap.putAll(parsePremium(href,index1,httpclient));
        					continue;
            			}
        				String value=tds.get(j2).text();
        				if(value.equals(" ")){
                 		   value= value.replaceAll(" ", "").trim();
                 	   }
        				aloneMap.put(index1, value);
        				index1=String.valueOf(Long.parseLong(index1)+1);
        			}
        			list.add(aloneMap);
        		}
    			detailsMap.put(index, list);
    		}
    		index=String.valueOf(Long.parseLong(index)+1);
    	}
    	
    	
    	
   		return detailsMap;
   	}
       
       *//**
        * 保费公司明细
        * @param href
        * @param index
        * @param httpclient
        * @return
        *//*
       private Map<String, Object> parsePremium(String href,String index,HttpClient httpclient){
       	String url="https://sol.sinosure.com.cn"+href;
       	Map<String, Object> aloneMap=new HashMap<String,Object>();
     	Map<String, Object> detailsMap=new HashMap<String,Object>();
       	String response=TaxConstants.getMes(httpclient, url);
//       	System.out.println(response);
       	Document doc=Jsoup.parse(response);
       	Elements tds=doc.select("[id=myTable]").select("td");
       	String index1=String.valueOf((Long.parseLong(index)*1000+001));
       	for (int i = 1; i < tds.size(); i++) {
       		if(i>=41&&i<51){
       			continue;
       		}
       		String value=tds.get(i).text();
       		if(value.equals(" ")){
     		   value= value.replaceAll(" ", "").trim();
     	   }
       		aloneMap.put(index1, value);
			index1=String.valueOf(Long.parseLong(index1)+1);
			i++;
		}
       	    detailsMap.put(index, aloneMap);
      		return detailsMap;
      	}*/
}