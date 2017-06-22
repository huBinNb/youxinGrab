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
 * 理赔--可损申请查询
 * @author Hobn
 */
public class SettlementQuery {
		//https://sol.sinosure.com.cn/biz/possibleLossQuery.do?method=search
	   private  static Logger logger=Logger.getLogger(SettlementQuery.class);
	   private String url="https://sol.sinosure.com.cn/biz/possibleLossQuery.do?method=search";
	   private String charset = "utf-8";
	   private HttpClientUtil httpClientUtil=null;
	   /**
	    * 获取可损申请
	    * @param httpclient
	    * @return
	    */
       public String  getDamageClaim(HttpClient httpclient,String username){
    	   List<org.bson.Document> list=new ArrayList<org.bson.Document>();
    	   httpClientUtil=new HttpClientUtil();
    	   Map<String, String> quotaMap=new HashMap<String, String>();
    	   quotaMap.put("sortcode","plNoticeQuery");                                                  
    	   quotaMap.put("__SORT_FIELD","");                                                           
    	   quotaMap.put("__SORT_METHOD","");                                                          
    	   quotaMap.put("objectname","VB_plNotice");                                                  
    	   quotaMap.put("bizBuyerNo","");                                                             
    	   quotaMap.put("bizBuyerNo_hide","2");                                                       
    	   quotaMap.put("bizBuyerNo_datatype","1");                                                   
    	   quotaMap.put("bizBuyerNo_changetype","1");                                                 
    	   quotaMap.put("buyerEngName","");                                                           
    	   quotaMap.put("buyerEngName_hide","2");                                                     
    	   quotaMap.put("buyerEngName_datatype","1");                                                 
    	   quotaMap.put("buyerEngName_changetype","1");                                               
    	   quotaMap.put("caseNo","");                                                                 
    	   quotaMap.put("caseNo_hide","2");                                                           
    	   quotaMap.put("caseNo_datatype","1");                                                       
    	   quotaMap.put("caseNo_changetype","0");                                                     
    	   quotaMap.put("webApplyNo","");                                                             
    	   quotaMap.put("webApplyNo_hide","2");                                                       
    	   quotaMap.put("webApplyNo_datatype","1");                                                   
    	   quotaMap.put("webApplyNo_changetype","0");                                                 
    	   quotaMap.put("countryCode","");                                                            
    	   quotaMap.put("countryCode_hide","1");                                                      
    	   quotaMap.put("countryCode_datatype","1");                                                  
    	   quotaMap.put("countryCode_changetype","0");                                                
    	   quotaMap.put("applyDate_left","");                                                         
    	   quotaMap.put("applyDate_right","");                                                        
    	   quotaMap.put("applyDate_hide","3");                                                        
    	   quotaMap.put("applyDate_datatype","3");                                                    
    	   quotaMap.put("applyDate_changetype","0");                                                  
    	   quotaMap.put("plReasonCode","");                                                           
    	   quotaMap.put("plReasonCode_hide","1");                                                     
    	   quotaMap.put("plReasonCode_datatype","1");                                                 
    	   quotaMap.put("plReasonCode_changetype","0");                                               
    	   quotaMap.put("bnsapplystage","");                                                          
    	   quotaMap.put("bnsapplystage_hide","1");                                                    
    	   quotaMap.put("bnsapplystage_datatype","1");                                                
    	   quotaMap.put("bnsapplystage_changetype","0");                                              
		   quotaMap.put("acceptDate_left","");                                                        
		   quotaMap.put("acceptDate_right","");                                                       
		   quotaMap.put("acceptDate_hide","3");                                                       
		   quotaMap.put("acceptDate_datatype","3");                                                   
		   quotaMap.put("acceptDate_changetype","0");                                                 
		                                                                                              
    	   String response=httpClientUtil.doPost(httpclient, url, quotaMap, charset);  
    	   String pageCount=Jsoup.parse(response).select("[name=pagecount]").val();                   
    	   Map<String, String> pageMap=new HashMap<String, String>();
    	   pageMap.put("pagesize","20");                
    	   pageMap.put("pagecount",pageCount);
    	   if(pageCount!=null&& !"".equals(pageCount)){
    	   for (int i = 1; i <=Integer.valueOf(pageCount).intValue(); i++) {
			String currentPage=String.valueOf(i);
			pageMap.put("pagenum",currentPage); 
			quotaMap.putAll(pageMap);
			response=httpClientUtil.doPost(httpclient, url, quotaMap, charset);
		    List<org.bson.Document> listPage= parseQuotaList(response,httpclient);
		    list.addAll(listPage);
		}
    	   }else{
    		   list.addAll(null);
    	   }
    	   new  CompanyDataDaoImpl().addSinosureData(username, "14003", list);
    	   logger.info("信保通  理赔--可损申请查询");
    	   return null;
       }
       
       
       /**
        * 可损申请的查询序列
        * @param response
        * @param httpclient
        * @return
        */
       private List<org.bson.Document> parseQuotaList(String response,HttpClient httpclient) {
    	   List<org.bson.Document> list = new ArrayList<>();
    	   Document doc=Jsoup.parse(StringUtils.rpAll(response));
    	   Element powerTable= doc.getElementById("PowerTable");
           Elements trs = powerTable.select("table").select("tr");
           for(int i = 1;i<trs.size();i++){
        	   Map<String, Object> resultMap=new HashMap<String, Object>();
        	   resultMap.put("serialNumber", "14003");
        	   String index="14003001";
               Elements tds = trs.get(i).select("td");
               String action=tds.select("a").get(0).attr("onclick");//获取申请表url
               String actionUrl=action.substring(action.indexOf("/"), action.length()-2);
               for(int j = 1;j<tds.size();j++){
            	   String value = tds.get(j).text();
            	   if(value.equals(" ")){
            		   value= value.replaceAll(" ", "").trim();
            	   }
            	   resultMap.put(index, value);
            	   index=String.valueOf(Long.parseLong(index)+1);
               }
               String url="https://sol.sinosure.com.cn"+actionUrl;
               String response1=TaxConstants.getMes(httpclient, url);
               list.add(CompanyDataUtil.toDocument(resultMap,parseDeclarationForm(response1, index, httpclient)));
           }
   		return list;
   	}

       /**
        * 可损明细
        * @param response
        * @return
        */
       private Map<String, Object> parseDeclarationForm(String response,String index,HttpClient httpclient) {
    	   List<Object> list=new ArrayList<Object>();
    	   Map<String, Object> formMap1=new HashMap<String,Object>();
    	   Map<String, Object> formMap=new HashMap<String,Object>();
    	   Document doc=Jsoup.parse(StringUtils.rpAll(response));                              
    	Elements myTables = doc.select("[id=myTable]");                                             
    	String index1=String.valueOf(Long.parseLong(index)*1000+001);                         
    	Elements tds= myTables.select("td");                                                  
    	                                                                                      
    	                                                                                      
    	for (int i = 1; i < tds.size(); i++) {                                                
       			String value=tds.get(i).text();                                               
    			formMap.put(index1, value);                                                   
       			index1=String.valueOf(Long.parseLong(index1)+1);                              
       			i++;                                                                          
		}                                                                                     
    	list.add(formMap);
    	formMap1.put(index, list);
   		return formMap1;                                                                       
   	}                                                                                         
}