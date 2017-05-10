package com.lidehang.national.sinosure;

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
import com.lidehang.dataInterface.model.constant.JsonArrayUtils;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;
import net.sf.json.JSONObject;

/**
 * 批复限额查询
 * @author Hobn
 */
public class QuotaQuery {
	   private String url="https://sol.sinosure.com.cn/biz/buyerquota.do?method=search";
	   private String charset = "utf-8";
	   private HttpClientUtil httpClientUtil=null;
       public String  getApprovedQuota(HttpClient httpclient){
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
		   quotaMap.put("QUOTASTATE","1");             
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
    	   
    	   String pageCount=Jsoup.parse(response).select("[name=pagecount]").val();
    	   Map<String, String> pageMap=new HashMap<String, String>();
    	   pageMap.put("pagesize","20");                
    	   pageMap.put("pagecount",pageCount);
    	   for (int i = 0; i <=Integer.valueOf(pageCount).intValue(); i++) {
			String currentPage=String.valueOf(i);
			pageMap.put("pagenum",currentPage); 
			quotaMap.putAll(pageMap);
			response=httpClientUtil.doPost(httpclient, url, quotaMap, charset);
		    List<org.bson.Document> listPage= parseQuota(response,httpclient);
		    list.addAll(listPage);
		}
    	   new  CompanyDataDaoImpl().addSinosureData("101676huwq", "14001", list);
    	   return null;
       }
       
       
       /**
        * 批复的查询列表
        * @param response
        * @param httpclient
        * @return
        */
       private List<org.bson.Document> parseQuota(String response,HttpClient httpclient) {
    	   List<org.bson.Document> list = new ArrayList<>();
    	   Document doc=Jsoup.parse(response);
    	   Element powerTable= doc.getElementById("PowerTable");
           Elements trs = powerTable.select("table").select("tr");
           for(int i = 1;i<trs.size();i++){
        	   Map<String, Object> resultMap=new HashMap<String, Object>();
        	   resultMap.put("serialNumber", "14001");
        	   String index="14001001";
               Elements tds = trs.get(i).select("td");
               String action=tds.select("a").get(1).attr("href");//获取申请表url
               for(int j = 1;j<tds.size();j++){
            	   String value = tds.get(j).text();
            	   if(value.equals(" ")){
            		   value= value.replaceAll(" ", "").trim();
            	   }
            	   resultMap.put(index, value);
            	   index=String.valueOf(Long.parseLong(index)+1);
               }
//               System.out.println(resultMap);
               String url="https://sol.sinosure.com.cn"+action;
//               System.out.println(action);
               String response1=TaxConstants.getMes(httpclient, url);
               doc=Jsoup.parse(response1);
               String act= doc.getElementById("subframe").attr("src");
               if(act.contains("&bnscode=")){
            	   act=action;
               }
//               System.out.println(act);
               list.add(CompanyDataUtil.toDocument(resultMap,parseDeclarationForm(act, index,httpclient)));
           }
   		return list;
   	}

       /**
        * 申请表详情
        * @param response
        * @return
        */
       private Map<String, Object> parseDeclarationForm(String action,String index,HttpClient httpclient) {
    	String url="https://sol.sinosure.com.cn"+action;
    	List<Object> list=new ArrayList<Object>();
    	Map<String, Object> formMap=new HashMap<String,Object>();
    	String response=TaxConstants.getMes(httpclient, url);
    	Document doc=Jsoup.parse(response);
    	Elements tables=doc.select("form").select("table");
    	for(int i=3;i<6;i++){//3
    		Map<String, String> aloneMap=new HashMap<String,String>();
    		Element table=tables.get(i);
    		Elements trs=table.select("tr");
    		Elements tdS=trs.select("td");
    		if(tdS.size()==1) continue;
    		int tableSize=tables.size();
    		int tdSize=tdS.size();
    		int trSize=trs.size();
    		String index1=String.valueOf((Long.parseLong(index) * 1000 + 001));
    		for (int j = 0; j < trs.size(); j++) {//0
    			Elements tds=trs.get(j).select("td");
				for (int k = 1; k < tds.size(); k++) {
					Element td=tds.get(k);
					String  value=null;
					Elements inputs=td.getElementsByClass("input3");
					Elements inputs2=td.getElementsByClass("input2");
					Elements textareas=td.getElementsByTag("textarea");
					Elements options=td.getElementsByTag("option");
					Elements checkeds=td.getElementsByAttribute("checked");
					Elements disableds=td.getElementsByAttribute("disabled");
					if(inputs.size()>0){
						value=inputs.attr("value");
					}else if (inputs2.size()>0) {
						String goodsName=inputs2.get(0).attr("value");
						//String goodsType=inputs2.get(1).attr("value")+" "+inputs2.get(2).attr("value");
						value=goodsName;
					}else if (textareas.size()>0) {
						value=textareas.get(0).val();
					}else if (options.size()>0) {
						value=options.get(0).text();
					}else if(checkeds.size()>0){
						String tdChecked=td.toString();
						int a=tdChecked.length();
						String checked=checkeds.get(0).toString();
						int b=checked.length();
						int c=tdChecked.indexOf(checked);
						value=tdChecked.substring(c+b, c+b+2);
					}else if(disableds.size()>0){
						value=null;
					}else{
						value=td.text();
					}
					aloneMap.put(index1, value);
					index1=String.valueOf(Long.parseLong(index1)+1);
					k++;
				}
			}
    		formMap.put(index, aloneMap);
    		index=String.valueOf(Long.parseLong(index)+1);
    	}
//    	System.out.println(formMap);
   		return formMap;
   	}
}