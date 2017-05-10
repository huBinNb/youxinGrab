package com.lidehang.data.collection.service.gs.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lidehang.data.collection.constant.SiteStatus;
import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.exception.SiteLoginFailedException;
import com.lidehang.data.collection.service.gs.GSModuleBase;
import com.lidehang.data.collection.service.gs.site.GSSiteHandler;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.national.util.StringUtils;

/**
 * 获取解析存储申报信息 --增值税申报表
 */
public class SbbHandler implements GSModuleBase<GSSiteHandler> {
	
//	@Autowired
//	CompanyDataDao companyDataDao;
	
	@Override
	public SiteStatus start(GSSiteHandler siteHandler) throws SiteLoginFailedException {
		List<org.bson.Document> list = new ArrayList<>();
		//获取增值税页面数据
		String zzsListHtml = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/sbtj_ysbcx.jsp?sssq_q="+siteHandler.params.getStartTimeStr()+"&sssq_z="+siteHandler.params.getEndTimeStr()+"&zsxm_dm=01");
//		System.out.println(zzsListHtml);
		Document document = Jsoup.parse(StringUtils.rpAll(zzsListHtml));
		Elements trElements = document.select(".unnamed1 tr");
		for(int i=1;i<trElements.size();i++){
			Element tr = trElements.get(i);
			
			if(tr == null||"".equals(tr.text())){
				break;
			}
			Map<String,Object> baseMap = parseLssb(tr);
			
			String dymxListHtml = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/"+tr.select("a").attr("href"));
			Document dyxmDocument = Jsoup.parse(StringUtils.rpAll(dymxListHtml));
			Elements aDyxm = dyxmDocument.select(".unnamed1 A");
			boolean flag = true;
			for(Element b:aDyxm){
				if(b.attr("href").startsWith("print_zzs_ybnsr")){
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/"+b.attr("href"));
					
					Map<String,Object> map = parseSBB(response2);
					
					list.add(CompanyDataUtil.toDocument(baseMap,map));
					flag =false;
				}
			}
			if(flag){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("10001004", "");
				list.add(CompanyDataUtil.toDocument(baseMap,map));
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10001", list);
		return SiteStatus.success;
	}
	
	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String,Object> parseLssb(Element tr){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10001001", StringUtils.StringFormat(tr.child(2).child(0).text()));
		map.put("10001002", StringUtils.StringFormat(tr.child(3).child(0).text()));
		map.put("10001003", StringUtils.StringFormat(tr.child(4).child(0).text()));
		map.put("serialNumber", "10001");
		return map;
	}
	
	/**
	 * 解析增值税申报表
	 */
	private Map<String,Object> parseSBB(String html){
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		List<Object> list3 = new ArrayList<Object>();
		Map<String,Object> map4 = new HashMap<String,Object>();
		String index6 = "10001004";
		String index2 = "";
		for(int k=1;k<tables.size()-1;k++){
			Elements personTrs1 = tables.get(k).getElementsByTag("tr");
			if(k==1){
				for(int k1=0;k1<personTrs1.size();k1++){
					Elements tds1 = personTrs1.get(k1).getElementsByTag("td");
					if(k1!=2){
						for(int k2=1;k2<tds1.size();){
							String td = StringUtils.StringFormat(tds1.get(k2).text());
							map4.put(index6, td);
							index6 = String.valueOf((Long.parseLong(index6)+1));
							k2+=2;
						}
					}else{
						for(int k2=1;k2<tds1.size();k2++){
							if(k2!=3&&k2!=5){
								String td = StringUtils.StringFormat(tds1.get(k2).text());
								map4.put(index6, td);
								index6 = String.valueOf((Long.parseLong(index6)+1));
							}
						}
					}
				}
			}else{
				for(int k1=2;k1<personTrs1.size()-2;k1++){
					index2 = index6 + "001";
					Map<String,Object> map5 = new HashMap<String,Object>();
					Elements tds1 = personTrs1.get(k1).getElementsByTag("td");
					if(k1==2||k1==12||k1==26){
						for(int k2=1;k2<tds1.size();k2++){
							String td = StringUtils.StringFormat(tds1.get(k2).text());
							map5.put(index2, td);
							index2 = String.valueOf((Long.parseLong(index2)+1));
						}
					}else{
						for(int k2=0;k2<tds1.size();k2++){
							String td = StringUtils.StringFormat(tds1.get(k2).text());
							map5.put(index2, td);
							index2 = String.valueOf((Long.parseLong(index2)+1));
						}
					}
					if(map5.size()>0){
						list3.add(map5);	
					}
				}
				map4.put(index6, list3);
			}
			
		}
		return map4;
		
	}

}
