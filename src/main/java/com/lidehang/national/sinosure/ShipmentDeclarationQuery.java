package com.lidehang.national.sinosure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
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
 * 出运_已受理申报查询
 * 
 * @author Hobn
 *
 */
public class ShipmentDeclarationQuery {
	   private  static Logger logger=Logger.getLogger(ShipmentDeclarationQuery.class);
	   private String url="https://sol.sinosure.com.cn/biz/declareQuery.do?method=search&sortcode=batchQueryend";
	   private String jumpUrl="https://sol.sinosure.com.cn/biz/declareQuery.do?method=search&pageno=";
	   private String charset = "utf-8";
	   private HttpClientUtil httpClientUtil=null;
	   //受理的申报列表
       public String  getAcceptedDeclare(HttpClient httpclient){
    	   List<org.bson.Document> list=new ArrayList<org.bson.Document>();
    	   httpClientUtil=new HttpClientUtil();
    	   String response = TaxConstants.getMes(httpclient, url);
    	   Document doc=Jsoup.parse(response);
    	   Elements d=doc.select("[name=pagecount]");
    	   String pagecount=d.val();
    	   
    	   Map<String, String> quotaMap=new HashMap<String, String>();
    	   quotaMap.put("sortcode","batchQueryend");   
    	   quotaMap.put("__SORT_FIELD","webapplyno");           
    	   quotaMap.put("__SORT_METHOD","desc");         
    	   quotaMap.put("objectname","vb_batch");                                  
    	   quotaMap.put("policyno","");                                            
    	   quotaMap.put("policyno_hide","2");                                      
    	   quotaMap.put("policyno_datatype","1");                                  
    	   quotaMap.put("policyno_changetype","0");                           
    	   quotaMap.put("webapplyno","");                                         
    	   quotaMap.put("webapplyno_hide","2");                                    
    	   quotaMap.put("webapplyno_datatype","1");                                
    	   quotaMap.put("webapplyno_changetype","0");                              
    	   quotaMap.put("invoiceNo","");                                           
    	   quotaMap.put("invoiceNo_hide","2");                                     
    	   quotaMap.put("invoiceNo_datatype","1");                                 
    	   quotaMap.put("invoiceNo_changetype","0");                               
    	   quotaMap.put("deptorempname","");                                      
//    	   quotaMap.put("pagenum","2");                                            
    	   quotaMap.put("pagesize","20");                                          
    	   quotaMap.put("pagecount",pagecount);
    	   int a=Integer.valueOf(pagecount).intValue()/2;
    		   for (int i = 1; i <=a; i++) {
    			   String newUrl=jumpUrl+i;
    			   response=httpClientUtil.doPost(httpclient, newUrl, quotaMap, charset);
    			   List<org.bson.Document>  listPage= parseList(response,httpclient);
    			   list.addAll(listPage);
    		   }
    	   new  CompanyDataDaoImpl().addSinosureData("101676huwq", "14002", list);
    	   logger.info("信保通——出运_已受理申报查询抓取");
    	   return null;
       }
       
       
       /**
        * 批复的查询列表
        * @param response
        * @param httpclient
        * @return
        */
       private List<org.bson.Document> parseList(String response,HttpClient httpclient) {
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
   	}

       /**
        * 出运明细解析
        * @param response
        * @return
        */
       private Map<String, Object> parseShipmentDetails(String viewUrl,String index,HttpClient httpclient) {
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
       
       /**
        * 保费公司明细
        * @param href
        * @param index
        * @param httpclient
        * @return
        */
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
      	}
}