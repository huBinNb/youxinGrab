package com.lidehang.national.foreignCurrency;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.util.TaxConstants;
import com.lidehang.national.util.TimeUtils;

/** 外汇
 * 涉外收入申报表
 * 
 * @author Hobn
 *
 */
public class ForeignDeclaration {
	   //已申报（已审核）信息查询
       public String  getDeclareQuota(HttpClient httpclient,String organizationCode){
    	   String url="http://bopcom.safesvc.gov.cn/BizforCustomerWeb/servlet/raAuditedSearch?current_appCode=BZCN&asone_addr=asone.safesvc.gov.cn:80";
  	       String response=TaxConstants.getMes(httpclient, url);
//  	       System.out.println(response);
  	       String jumpUrl= response.substring(response.indexOf("var url = escape("));
  	       jumpUrl = jumpUrl.substring(jumpUrl.indexOf("\"")+1, jumpUrl.indexOf("\")"));
  	       String action="http://bopcom.safesvc.gov.cn"+jumpUrl;
  	       response= TaxConstants.getMes(httpclient, action);
  	       List<org.bson.Document>  list= parseList(httpclient,jumpUrl);
  	       new CompanyDataDaoImpl().addData(organizationCode, "15001", list);
  	       return null;
       }
       
       
       /**
        * 已申报（已审核）信息查询   列表
        * @param response
        * @param httpclient
        * @return
        */
       private List<org.bson.Document> parseList(HttpClient httpclient,String jumpUrl) {
    	 List<org.bson.Document> list = new ArrayList<>();
    	 String charset = "utf-8";
    	 HttpClientUtil httpClientUtil=new HttpClientUtil();  
    	 String action1="http://bopcom.safesvc.gov.cn/BizforCustomerWeb/servlet/raAuditedSearch";
 	    //当前时间  年 月
 	     Calendar nowTime=Calendar.getInstance();
 	     int year=nowTime.get(Calendar.YEAR);   
 	     int month=nowTime.get(Calendar.MONTH);  
 	     //起始日期
 	     String date1="2000";
 	     String date2="01";
 	     String date3="01";
 	     String startTime=date1+"-"+date2+"-"+date3;
 	     int dateInt1=Integer.parseInt(date1);  
	     int dateInt2=Integer.parseInt(date2)-1;    
 	     int dateInt3=Integer.parseInt(date3);    
 	     //每年    2000         2017
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
 	    		 //从2000-01-01开始         20   00    01    01   2000-1900   0:一月
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
   	}
       

       /**
        * 列表详情
        * @param response
        * @return
        */
       private Map<String, Object> parseListDetails(String index,HttpClient httpclient,String jumpUrl,String reportNO) {
    	String url="http://bopcom.safesvc.gov.cn/BizforCustomerWeb/servlet/raAuditedSearchDetail?rptNO="+reportNO+"&backUrl=" +jumpUrl;
    	String response=TaxConstants.getMes(httpclient, url);
    	Document doc=Jsoup.parse(response);
    	Elements tables=doc.select("[width=97%]");
    	Map<String, Object> detailMap=new HashMap<>();//需要把map放入index中
    	for (int i = 0; i < tables.size(); i++) {
    		Element table=tables.get(i);
    		Elements trs=table.select("tr");
    		String value="";
    		Map<String, Object> resultMap=new HashMap<>();//需要把map放入index中
    		String index1=String.valueOf((Long.parseLong(index) * 1000 + 001));
			switch (i) {
			case 0:
				for (int j = 0; j < trs.size(); j++) {
					Element tr=trs.get(j);
					Elements tds=tr.select("td");
					for (int k = 1; k <tds.size(); k++) {
						Elements inputs=tds.get(k).getElementsByTag("input");
						if(inputs.size()>1){
							for (Element element : inputs) {
								value+=element.val()+" ";
							}
						}else{
							value=inputs.get(0).val();
						}
						resultMap.put(index1, value);
						index1=String.valueOf(Long.parseLong(index1)+1);
						k++;
					}
					j++;
				}
				break;
            case 1:
				Elements trChilds=trs.select("td").select("tr");
				for (int j = 0; j < trChilds.size(); j++) {
					Element tr=trChilds.get(j);
					Elements tds=tr.select("td");
					for (int k = 0; k < tds.size(); k++) {
						Element td=tds.get(k);
						if(k==0&&j==0){
							String str=td.toString();
							Elements checkeds=td.select("[checked=checked]");
							if(checkeds.size()!=0){
							for (int l = 0; l < checkeds.size(); l++){
								Element checked=checkeds.get(l);
								String str1=checked.toString();
								String str2=str.substring(str.indexOf(str1)+6);
								str2=str2.substring(str.indexOf("/input>")+7, str.indexOf("<input"));
								value+=str2+" ";
							   }
							} else{
								value="";
							}  //j=0 k=2    j=1 k=1
						}else if((j==0&&k==2)||(j==1&&k==0)){
							continue;
						}else{
							Elements inputs=td.getElementsByTag("input");
							value=inputs.get(0).val();
						}
						resultMap.put(index1, value);
						index1=String.valueOf(Long.parseLong(index1)+1);
					}
				}
				break;
            case 2:
            	Elements tds=trs.select("td");
				for (int j = 1; j < tds.size(); j++) {
					Element td=tds.get(j);
					String str=td.toString();
					if(str.contains("checked")){
						String str1=str.substring(str.indexOf("checked"));
						String str2=str1.substring(str1.indexOf(">")+1, str1.indexOf("<"));
						value+=str2;
					}
					resultMap.put(index1, value);
					index1=String.valueOf(Long.parseLong(index1)+1);
				}
				break;
            case 3:
            	for (int j = 0; j < trs.size(); j++) {
					Element tr=trs.get(j);
					Elements tdw=tr.select("td");
					for (int k = 2; k <tdw.size(); k++) {
						Elements inputs=tdw.get(k).getElementsByTag("input");
						value="";
						if(inputs.size()>1){
								for (Element element : inputs) {
									value+=element.val()+"-";
								}
								value=value.substring(0, value.length()-1);
							}else{
							value=inputs.get(0).val();
						}
						resultMap.put(index1, value);
						index1=String.valueOf(Long.parseLong(index1)+1);
						k++;
					}
				}
				break;
            case 4:
            	Elements td4=trs.select("td");
				for (int j = 1; j < td4.size(); j++) {
					Element td=td4.get(j);
					Elements inputs=td.getElementsByTag("input");
					value=inputs.get(0).val();
					resultMap.put(index1, value);
					index1=String.valueOf(Long.parseLong(index1)+1);
				}
				break;
            case 5:
            	String str=trs.toString();
            	if(str.contains("checked")){
					String str1=str.substring(str.indexOf("checked"));
					String str2=str1.substring(str1.indexOf(">")+1, str1.indexOf("<"));
					value+=str2;
				}else{
					value+=" ";
				}
				resultMap.put(index1, value);
				index1=String.valueOf(Long.parseLong(index1)+1);
				break;
            case 6:
				for (int j = 1; j <trs.size(); j++) {
					Element tr=trs.get(j);
					Elements td5=tr.select("td");
					for (int k = 1; k < td5.size(); k++) {
						Elements inputs=td5.get(k).getElementsByTag("input");
						value="";
						if(inputs.size()>1){
								for (Element element : inputs) {
									value+=element.val()+"-";
								}
								value=value.substring(0, value.length()-1);
							}else{
							value=inputs.get(0).val();
						}
						resultMap.put(index1, value);
						index1=String.valueOf(Long.parseLong(index1)+1);
					}
				}
				break;
            case 7:
            	for (int j = 0; j < trs.size(); j++) {
					Element tr=trs.get(j);
					Elements td7=tr.select("td");
					for (int k = 1; k < td7.size(); k++) {
						String str1=td7.get(k).toString();
						if(str1.contains("checked")){
							value=" ";
							String str2=str1.substring(str1.indexOf("checked"));
							String str3=str2.substring(str2.indexOf(">")+1,str2.indexOf("<"));
							value+=str3;
						}
						k++;
						resultMap.put(index1, value);
						index1=String.valueOf(Long.parseLong(index1)+1);
					}
				}
				break;
            case 8:
            	for (int j = 0; j < trs.size(); j++) {
				    Element tr=trs.get(j);
				    Elements td8=tr.select("td");
				    for (int k = 1; k < td8.size(); k++) {
						Elements inputs=td8.get(k).getElementsByTag("input");
						value+=inputs.get(0).val();
						resultMap.put(index1, value);
						index1=String.valueOf(Long.parseLong(index1)+1);
					}
				}
				break;
            case 9:
            	for (int j = 1; j < trs.size(); j++) {
				    Element tr=trs.get(j);
				    Elements td9=tr.select("td");
				    for (int k = 0; k < td9.size(); k++) {
				    	value="";
						Elements inputs=td9.get(k).getElementsByTag("input");
						value+=inputs.get(0).val();
						resultMap.put(index1, value);
						index1=String.valueOf(Long.parseLong(index1)+1);
					}
				}
				break;
            case 10:
            	for (int j = 1; j < trs.size(); j++) {
				    Element tr=trs.get(j);
				    Elements td10=tr.select("td");
				    for (int k = 2; k < td10.size(); k++) {
				    	value="";
						Elements inputs=td10.get(k).getElementsByTag("input");
						value+=inputs.get(0).val();
						resultMap.put(index1, value);
						index1=String.valueOf(Long.parseLong(index1)+1);
						k++;
				    }
				}
				break;
            case 11:
            	//需测试
            	for (int j = 0; j < trs.size(); j++) {
				    Element tr=trs.get(j);
				    Elements td11=tr.select("td");
				    for (int k = 2; k < td11.size(); k++) {
				    	value="";
						Elements inputs=td11.get(k).getElementsByTag("input");
						value+=inputs.get(0).val();
						resultMap.put(index1, value);
						index1=String.valueOf(Long.parseLong(index1)+1);
						k++;
				    }
				}
				break;
            case 12:
            	for (int j = 1; j < trs.size(); j++) {
				    Element tr=trs.get(j);
				    Elements td12=tr.select("td");
				    for (int k = 1; k < td12.size(); k++) {
				    	value="";
						Elements inputs=td12.get(k).getElementsByTag("textarea");
						value+=inputs.get(0).text();
						resultMap.put(index1, value);
						index1=String.valueOf(Long.parseLong(index1)+1);
						k++;
				    }
				}
				break;
            case 13:
            	for (int j = 0; j < trs.size(); j++) {
				    Element tr=trs.get(j);
				    Elements td12=tr.select("td");
				    for (int k = 1; k < td12.size(); k++) {
				    	value="";
						Elements inputs=td12.get(k).getElementsByTag("textarea");
						value+=inputs.get(0).text();
						resultMap.put(index1, value);
						index1=String.valueOf(Long.parseLong(index1)+1);
						k++;
				    }
				    j++;
				}
				break;
				
			default:
				break;
			}
			detailMap.put(index, resultMap);
			index=String.valueOf(Long.parseLong(index)+1);
		}
   		return detailMap;
   	}
}








