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
 * 获取解析存储申报信息 -- 抵扣联明细
 */
public class DklHandler implements GSModuleBase<GSSiteHandler> {
	
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
			boolean flag = true ;
			for(Element b:aDyxm){
				if(b.attr("href").startsWith("print_zzs_flzl3.jsp")){
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/"+b.attr("href"));
					
					Map<String,Object> map = parseSBB(response2);
					
					list.add(CompanyDataUtil.toDocument(baseMap,map));
					flag = false ;
				}
			}
			if(flag){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("10002004", "");
				list.add(CompanyDataUtil.toDocument(baseMap,map));
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10002", list);
		return SiteStatus.success;
	}
	
	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String,Object> parseLssb(Element tr){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10002001", StringUtils.strFormat(tr.child(2).child(0).text()));
		map.put("10002002", StringUtils.strFormat(tr.child(3).child(0).text()));
		map.put("10002003", StringUtils.strFormat(tr.child(4).child(0).text()));
		map.put("serialNumber", "10002");
		return map;
	}
	
	/**
	 * 解析增值税一般纳税人-- 抵扣联明细
	 */
	private Map<String,Object> parseSBB(String html){
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		Map<String,Object> map2 = new HashMap<String,Object>();
		List<Object> list3 = new ArrayList<Object>();
		String index4 = "10002004";
		String index6 = "";
		tohere:
			for(int k=1;k<tables.size()-1;k++){
				Elements personTrs1 = tables.get(k).getElementsByTag("tr");
				for(int k1=1;k1<personTrs1.size();k1++){
					index6 = index4 + "001";
					Map<String,Object> map4 = new HashMap<String,Object>();
					Elements tds1 = personTrs1.get(k1).getElementsByTag("td");
					if(k1==1){
						for(int k2=2;k2<tds1.size();k2++){
							String td = StringUtils.StringFormat(tds1.get(k2).text());
							if("".equals(StringUtils.StringFormat(tds1.get(2).text()))){
								break tohere;//
							}else{
								map4.put(index6, td);
							}
							index6 = String.valueOf((Long.parseLong(index6)+1));
						}
					}else{
						for(int k2=1;k2<tds1.size();k2++){
							String td = StringUtils.StringFormat(tds1.get(k2).text());
							if("".equals(StringUtils.StringFormat(tds1.get(1).text()))){
								continue;//
							}else{
								map4.put(index6, td);
							}
							index6 = String.valueOf((Long.parseLong(index6)+1));
						}
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
