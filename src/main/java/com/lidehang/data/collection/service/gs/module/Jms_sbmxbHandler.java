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
 * 获取解析存储申报信息 -- 减免税申报明细表
 */
public class Jms_sbmxbHandler implements GSModuleBase<GSSiteHandler> {

	// @Autowired
	// CompanyDataDao companyDataDao;

	@Override
	public SiteStatus start(GSSiteHandler siteHandler) throws SiteLoginFailedException {
		List<org.bson.Document> list = new ArrayList<>();
		// 获取增值税页面数据
		String zzsListHtml = siteHandler.getPage(
				"http://100.0.0.1:8001/ctais2/wssb/sjcx/sbtj_ysbcx.jsp?sssq_q=" + siteHandler.params.getStartTimeStr()
						+ "&sssq_z=" + siteHandler.params.getEndTimeStr() + "&zsxm_dm=01");
		// System.out.println(zzsListHtml);
		Document document = Jsoup.parse(StringUtils.rpAll(zzsListHtml));
		Elements trElements = document.select(".unnamed1 tr");
		for (int i = 1; i < trElements.size(); i++) {
			Element tr = trElements.get(i);
			if (tr == null || "".equals(tr.text())) {
				break;
			}
			Map<String, Object> baseMap = parseLssb(tr);
			// http://100.0.0.1:8001/ctais2/wssb/sjcx/print_zzs_flzl5_2013.jsp?k=42
			String dymxListHtml = siteHandler
					.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + tr.select("a").attr("href"));
			Document dyxmDocument = Jsoup.parse(StringUtils.rpAll(dymxListHtml));
			Elements aDyxm = dyxmDocument.select(".unnamed1 A");
			boolean flag = true;
			for (Element b : aDyxm) {
				// http://100.0.0.1:8001/ctais2/wssb/sjcx/print_zzs_jms_sbmxb.jsp?k=44
				if (b.attr("href").startsWith("print_zzs_jms_sbmxb")) {
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + b.attr("href"));
					// b.attr("href")=print_zzs_flzl2 _2013.jsp
					//System.out.println(b.attr("href"));
					Map<String, Object> map = parseSBB(response2, b.attr("href"));
					list.add(CompanyDataUtil.toDocument(baseMap, map));
					flag = false;
				}
			}
			if (flag) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("10018004", "");
				list.add(CompanyDataUtil.toDocument(baseMap, map));
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10018", list);
		return SiteStatus.success;
	}

	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String, Object> parseLssb(Element tr) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10018001", StringUtils.StringFormat(tr.child(2).child(0).text()));
		map.put("10018002", StringUtils.StringFormat(tr.child(3).child(0).text()));
		map.put("10018003", StringUtils.StringFormat(tr.child(4).child(0).text()));
		map.put("serialNumber", "10018");
		return map;
	}

	/**
	 * 解析增值税一般纳税人-- 减免税申报明细表
	 * 
	 * @param html
	 * @param href_
	 * @return
	 */
	private Map<String, Object> parseSBB(String html, String href_) {
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		// table有两张表面：增值税纳税申报表附列资料（表二） 还有一张是unnamed1
		Elements tables = document.getElementsByTag("table");
	
		Map<String, Object> map1 = new HashMap<String, Object>();
		
		Map<String, Object> map2 = new HashMap<String, Object>();
		Map<String, Object> map3 = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		String index4 = "10018004";
		String index5 = "10018004001";
		String index6 = "";
		int index7 = 0;

		//table中所有的tr
		Elements personTrs1 = tables.get(1).getElementsByTag("tr");
		
			
		for (int i = 3; i < personTrs1.size(); i++) {
			//取第i行的tr中所有的td  3 4 5  6 7 8 9
			Elements tds1 = personTrs1.get(i).getElementsByTag("td");
			
			index6 = index5 + "001";
			//第0个td的值
			String td = StringUtils.StringFormat(tds1.get(0).text());
			if("".equals(td)){
				continue;
			}else{
				
			for (int j = 0; j < tds1.size(); j++) {
				
				if (tds1.size() == 1) {
					if (map1.size() > 0) {
						// 10018004001
						map3.put(index5, list);
						map1 = new HashMap<String, Object>();
						list = new ArrayList<Object>();
					}
					index7 = i;// +3 5
					index5 = String.valueOf((Long.parseLong(index5) + 1));
				}
				
				if (i >= index7 && i <= (index7 + 2)) {
					continue;
				}
				
				
				td = StringUtils.StringFormat(tds1.get(j).text());
				map1.put(index6, td);
				
				index6 = String.valueOf((Long.parseLong(index6) + 1));
				
			
			}
			
			
			}
		
			if (map1.size() > 0) {
				list.add(map1);
			}
			
		}
		map3.put(index5, list);
		map2.put(index4, map3);
		return map2;
	}
}
