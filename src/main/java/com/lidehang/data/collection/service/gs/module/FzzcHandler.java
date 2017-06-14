package com.lidehang.data.collection.service.gs.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lidehang.action.GsAction;
import com.lidehang.data.collection.constant.SiteStatus;
import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.exception.SiteLoginFailedException;
import com.lidehang.data.collection.service.gs.GSModuleBase;
import com.lidehang.data.collection.service.gs.site.GSSiteHandler;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.national.util.StringUtils;

/**
 * 获取解析存储增值税申报表 
 */
public class FzzcHandler implements GSModuleBase<GSSiteHandler> {
	
//	@Autowired
//	CompanyDataDao companyDataDao;
	private static Logger logger =Logger.getLogger(FzzcHandler.class);
	@Override
	public SiteStatus start(GSSiteHandler siteHandler) throws SiteLoginFailedException {
		logger.info("国税--获取解析存储增值税申报表 抓取");
		List<org.bson.Document> list = new ArrayList<>();
		//获取增值税页面数据
		String zzsListHtml = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/sbtj_ysbcx.jsp?sssq_q="+siteHandler.params.getStartTimeStr()+"&sssq_z="+siteHandler.params.getEndTimeStr()+"&zsxm_dm=90");
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
				if(b.attr("href").startsWith("print_cwbb_xqy_zcfzb.jsp")){
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/"+b.attr("href"));
					
					Map<String,Object> map = parseSBB(response2);
					
					list.add(CompanyDataUtil.toDocument(baseMap,map));
					flag =false;
				}
			}
			if(flag){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("10005004", "");
				list.add(CompanyDataUtil.toDocument(baseMap,map));
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10005", list);
		return SiteStatus.success;
	}
	
	/**
	 * 解析财务报表2013历史申报列表中的某一项
	 */
	private Map<String,Object> parseLssb(Element tr){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10005001", tr.child(2).child(0).text());
		map.put("10005002", tr.child(3).child(0).text());
		map.put("10005003", tr.child(4).child(0).text());
		map.put("serialNumber", "10005");
		return map;
	}
	
	/**
	 * 解析财务报表2013--资产负债表
	 */
	private Map<String,Object> parseSBB(String html){
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		List<Object> list3 = new ArrayList<Object>();
		Map<String,Object> map2 = new HashMap<String,Object>();
		String index4 = "10005004";
		String index6 = "";
		for(int k=2;k<tables.size();k++){
			Elements personTrs1 = tables.get(k).getElementsByTag("tr");
			for(int k1=1;k1<personTrs1.size();k1++){
				index6 = index4 + "001";
				Map<String,Object> map4 = new HashMap<String,Object>();
				Elements tds1 = personTrs1.get(k1).getElementsByTag("td");
				for(int k2=0;k2<4;k2++){
					String td = StringUtils.StringFormat(tds1.get(k2).text());
					map4.put(index6, td);
					index6 = String.valueOf((Long.parseLong(index6)+1));
				}
				if(map4.size()>0){
					list3.add(map4);	
				}
			}
			for(int k1=1;k1<personTrs1.size();k1++){
				index6 = index4 + "001";
				Map<String,Object> map4 = new HashMap<String,Object>();
				Elements tds1 = personTrs1.get(k1).getElementsByTag("td");
				for(int k2=4;k2<tds1.size();k2++){
					String td = StringUtils.StringFormat(tds1.get(k2).text());
					if("".equals(StringUtils.StringFormat(tds1.get(4).text()))){
						continue;
					}else{
						map4.put(index6, td);
					}
					index6 = String.valueOf((Long.parseLong(index6)+1));
				}
				if(map4.size()>0){
					list3.add(map4);	
				}
			}
		}
		map2.put(index4, list3);
		return map2;
		
	}

}
