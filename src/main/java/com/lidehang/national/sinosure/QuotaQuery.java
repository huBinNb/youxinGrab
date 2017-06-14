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
 * 批复限额查询
 * @author Hobn
 */
public class QuotaQuery {
	   private static Logger logger=Logger.getLogger(QuotaQuery.class);
	   private String url="https://sol.sinosure.com.cn/biz/buyerquota.do?method=search";
	   private String charset = "utf-8";
	   private HttpClientUtil httpClientUtil=null;
	   /**
	    * 获取已批复限额
	    * @param httpclient
	    * @return
	    */
       public String  getApprovedQuota(HttpClient httpclient,String username){
    	   List<org.bson.Document> list=new ArrayList<org.bson.Document>();
    	   httpClientUtil=new HttpClientUtil();
    	   Map<String, String> quotaMap=new HashMap<String, String>();
    	   quotaMap.put("sortcode","buyerquatoquery");                                                                    
    	   quotaMap.put("buyerid","");                                                                                    
    	   quotaMap.put("flag","null");                                                                                   
    	   quotaMap.put("whichFlag","null");                                                                              
    	   quotaMap.put("__SORT_FIELD","");                                                                               
    	   quotaMap.put("__SORT_METHOD","");                                                                              
    	   quotaMap.put("objectname","Vb_Myquota");                                                                       
    	   quotaMap.put("BUYERENGNAME","");                                                                               
    	   quotaMap.put("BUYERENGNAME_hide","2");                                                                         
    	   quotaMap.put("BUYERENGNAME_datatype","1");                                                                     
    	   quotaMap.put("BUYERENGNAME_changetype","1");                                                                   
    	   quotaMap.put("BIZBUYERNO","");                                                                                 
    	   quotaMap.put("BIZBUYERNO_hide","2");                                                                           
    	   quotaMap.put("BIZBUYERNO_datatype","1");                                                                       
    	   quotaMap.put("BIZBUYERNO_changetype","1");                                                                     
    	   quotaMap.put("POLICYNO","");                                                                                   
    	   quotaMap.put("POLICYNO_hide","2");                                                                             
    	   quotaMap.put("POLICYNO_datatype","1");                                                                         
    	   quotaMap.put("POLICYNO_changetype","0");                                                                       
    	   quotaMap.put("BUYERQUOTANO","");                                                                               
    	   quotaMap.put("BUYERQUOTANO_hide","2");                                                                         
    	   quotaMap.put("BUYERQUOTANO_datatype","1");                                                                     
    	   quotaMap.put("BUYERQUOTANO_changetype","0");                                                                   
    	   quotaMap.put("WEBAPPLYNO","");                                                                                 
    	   quotaMap.put("WEBAPPLYNO_hide","2");                                                                           
    	   quotaMap.put("WEBAPPLYNO_datatype","1");                                                                       
    	   quotaMap.put("WEBAPPLYNO_changetype","0");                                                                     
    	   quotaMap.put("PAYMODE","");                                                                                    
    	   quotaMap.put("PAYMODE_hide","1");                                                                              
    	   quotaMap.put("PAYMODE_datatype","1 ");                                                                         
    	   quotaMap.put("PAYMODE_changetype","0");                                                                        
    	   quotaMap.put("AUDITDATE_left","");                                                                             
    	   quotaMap.put("AUDITDATE_right","");                                                                            
    	   quotaMap.put("AUDITDATE_hide","3");                                                                            
    	   quotaMap.put("AUDITDATE_datatype","3");                                                                        
    	   quotaMap.put("AUDITDATE_changetype","0");                                                                      
    	   quotaMap.put("effectDate_left","");                                                                            
		   quotaMap.put("effectDate_right","");                                                                           
		   quotaMap.put("effectDate_hide","3");                                                                           
		   quotaMap.put("effectDate_datatype","3");                                                                       
		   quotaMap.put("effectDate_changetype","0");                                                                     
		   //限额状态  1：有效    0：无效                                                                                 
		   quotaMap.put("QUOTASTATE","");                                                                                 
		   quotaMap.put("QUOTASTATE_hide","1");                                                                           
		   quotaMap.put("QUOTASTATE_datatype","1");                                                                       
		   quotaMap.put("QUOTASTATE_changetype","0");                                                                     
		   quotaMap.put("BANKNO","");                                                                                     
		   quotaMap.put("BANKNO_hide","2");                                                                               
		   quotaMap.put("BANKNO_datatype","1");                                                                           
		   quotaMap.put("BANKNO_changetype","1");                                                                         
		   quotaMap.put("BANKENGNAME","");                                                                                
		   quotaMap.put("BANKENGNAME_hide","2");                                                                          
		   quotaMap.put("BANKENGNAME_datatype","1");                                                                      
		   quotaMap.put("BANKENGNAME_changetype","1");                                                                    
		   quotaMap.put("quotasum_hide","1");                                                                             
		   quotaMap.put("quotasum_datatype","1");                                                                         
		   quotaMap.put("quotasum_changetype","0");                                                                       
		   quotaMap.put("lapsetype_hide","1");                                                                            
		   quotaMap.put("lapsetype_datatype","1");                                                                        
		   quotaMap.put("lapsetype_changetype","0");                                                                      
		   quotaMap.put("deptorempname","");                                                                              
		   quotaMap.put("changecorp","null");                                                                             
		   /*quotaMap.put("pagenum","1");                                                                                 
		   quotaMap.put("pagesize","20");                                                                                 
		   quotaMap.put("pagecount","14");*/            
    	   String response=httpClientUtil.doPost(httpclient, url, quotaMap, charset);
//    	   System.out.println(response);
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
    	   new  CompanyDataDaoImpl().addSinosureData(username, "14001", list);
    	   logger.info("信保通——批复限额查询抓取");
    	   return null;
       }
       
       
       /**
        * 批复限额的查询列表
        * @param response
        * @param httpclient
        * @return
        */
       private List<org.bson.Document> parseQuotaList(String response,HttpClient httpclient) {
    	   List<org.bson.Document> list = new ArrayList<>();
    	   Document doc=Jsoup.parse(response);
    	   Element powerTable= doc.getElementById("PowerTable");
           Elements trs = powerTable.select("table").select("tr");
           for(int i = 1;i<trs.size();i++){
        	   Map<String, Object> resultMap=new HashMap<String, Object>();
        	   resultMap.put("serialNumber", "14001");
        	   String index="14001001";
               Elements tds = trs.get(i).select("td");
               for(int j = 1;j<tds.size();j++){
            	   String value = tds.get(j).text();
            	   if(value.equals(" ")){
            		   value= value.replaceAll(" ", "").trim();
            	   }
            	   resultMap.put(index, value);
            	   index=String.valueOf(Long.parseLong(index)+1);
               }
               list.add(CompanyDataUtil.toDocument(resultMap));
           }
   		return list;
   	}
}