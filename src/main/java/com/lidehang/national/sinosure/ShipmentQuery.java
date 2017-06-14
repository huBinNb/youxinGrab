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
 * 出运_出运查询
 * @author Hobn
 *
 */
public class ShipmentQuery {
//	https://sol.sinosure.com.cn/biz/shipmentQuery.do?method=search
	   private  static Logger logger=Logger.getLogger(ShipmentQuery.class);
	   private String url="https://sol.sinosure.com.cn/biz/shipmentQuery.do?method=search";
	   private String charset = "utf-8";
	   private HttpClientUtil httpClientUtil=null;
	   /**
	    * 获取出运查询
	    * @param httpclient
	    * @return
	    */   
       public String  getShipmentQuota(HttpClient httpclient,String username){
    	   List<org.bson.Document> list=new ArrayList<org.bson.Document>();
    	   httpClientUtil=new HttpClientUtil();
    	   Map<String, String> quotaMap=new HashMap<String, String>();
    	   quotaMap.put("sortcode","shipmentQuery");         
    	   quotaMap.put("__SORT_FIELD","");                      
    	   quotaMap.put("__SORT_METHOD","");                     
    	   quotaMap.put("objectname","vb_shipment");                   
    	   quotaMap.put("invoiceNo","");                         
    	   quotaMap.put("invoiceNo_hide","2");                   
    	   quotaMap.put("invoiceNo_datatype","1");               
    	   quotaMap.put("invoiceNo_changetype","1");                    
    	   quotaMap.put("bizBuyerNo","");                        
    	   quotaMap.put("bizBuyerNo_hide","2");                  
    	   quotaMap.put("bizBuyerNo_datatype","1");              
    	   quotaMap.put("bizBuyerNo_changetype","1");                        
    	   quotaMap.put("buyerEngName","");                      
    	   quotaMap.put("buyerEngName_hide","2");             
    	   quotaMap.put("buyerEngName_datatype","1");         
    	   quotaMap.put("buyerEngName_changetype","1");             
    	   quotaMap.put("policyNo","");                      
    	   quotaMap.put("policyNo_hide","2");                 
    	   quotaMap.put("policyNo_datatype","1");                
    	   quotaMap.put("policyNo_changetype","0");                  
    	   quotaMap.put("webApplyNo","");                        
    	   quotaMap.put("webApplyNo_hide","2");                  
    	   quotaMap.put("webApplyNo_datatype","1");              
    	   quotaMap.put("webApplyNo_changetype","0");            
    	   quotaMap.put("corpBuyerNo","");                      
    	   quotaMap.put("corpBuyerNo_hide","2");                 
    	   quotaMap.put("corpBuyerNo_datatype","1");             
    	   quotaMap.put("corpBuyerNo_changetype","0");             
    	   quotaMap.put("countryCode","");                       
    	   quotaMap.put("countryCode_hide","1");                 
    	   quotaMap.put("countryCode_datatype","1");             
    	   quotaMap.put("countryCode_changetype","0");             
    	   quotaMap.put("feepaymode","");                       
    	   quotaMap.put("feepaymode_hide","1");                  
    	   quotaMap.put("feepaymode_datatype","1");              
    	   quotaMap.put("feepaymode_changetype","0");            
    	   quotaMap.put("deptorempname","");                   
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
    	   new  CompanyDataDaoImpl().addSinosureData(username, "14004", list);
    	   logger.info("信保通  出运_出运查询");
    	   return null;
       }
       
       
       /**
        * 出运的查询列表
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
        	   resultMap.put("serialNumber", "14004");
        	   String index="14004001";
               Elements tds = trs.get(i).select("td");
               for(int j = 2;j<tds.size();j++){
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