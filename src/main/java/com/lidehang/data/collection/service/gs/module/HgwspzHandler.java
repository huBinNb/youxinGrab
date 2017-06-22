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
 * 获取解析存储申报信息 -- 海关完税凭证抵扣清单
 */
public class HgwspzHandler implements GSModuleBase<GSSiteHandler> {
	
//	@Autowired
//	CompanyDataDao companyDataDao;
	private static Logger logger =Logger.getLogger(HgwspzHandler.class);
	@Override
	public SiteStatus start(GSSiteHandler siteHandler) throws SiteLoginFailedException {
		logger.info("国税--获取解析存储申报信息 -- 海关完税凭证抵扣清单抓取");
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
			for(Element b:aDyxm){
				// /ctais2/wssb/sjcx/print_hgws.jsp
				if(b.attr("href").startsWith("print_hgws.jsp")){
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/"+b.attr("href"));
					Map<String,Object> map = parseSBB(response2);
					list.add(CompanyDataUtil.toDocument(baseMap,map));
				}
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10067", list);
		return SiteStatus.success;
	}
	
	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String,Object> parseLssb(Element tr){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10067001", StringUtils.strFormat(tr.child(2).child(0).text()));
		map.put("10067002", StringUtils.strFormat(tr.child(3).child(0).text()));
		map.put("10067003", StringUtils.strFormat(tr.child(4).child(0).text()));
		map.put("serialNumber", "10067");
		return map;
	}
	
	/**
	 * 解析增值税一般纳税人--  海关完税凭证抵扣清单
	 */
	private Map<String,Object> parseSBB(String html){
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements pageEnd = document.getElementsByClass("pageEnd");
		Map<String,Object> map2 = new HashMap<String,Object>();
		List<Object> list3 = new ArrayList<Object>();
		String index4 = "10067004";
		String index6 = "";
//		tohere:
		Elements personTrs1 = pageEnd.get(0).getElementsByTag("table").get(1).select("tr");
				for(int k=1;k<personTrs1.size();k++){
					index6 = index4 + "001";
					Map<String,Object> map4 = new HashMap<String,Object>();
					Elements tds1 = personTrs1.get(k).getElementsByTag("td");
						for(int k2=0;k2<tds1.size();k2++){
							String td = StringUtils.StringFormat(tds1.get(k2).text());
							/*if("".equals(StringUtils.StringFormat(tds1.get(2).text()))){
//								break tohere;
							}else{*/
								map4.put(index6, td);
							index6 = String.valueOf((Long.parseLong(index6)+1));
						}
				
					if(map4.size()>0){
						list3.add(map4);	
					}
			}
			map2.put(index4, list3);
		return map2;
		
	}

}
