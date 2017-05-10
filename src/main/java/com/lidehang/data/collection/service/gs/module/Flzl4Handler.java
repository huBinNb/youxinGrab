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
 * 获取解析存储申报信息 -- 附列资料(表四)
 */
public class Flzl4Handler implements GSModuleBase<GSSiteHandler> {

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
		for (int i = 0; i < trElements.size(); i++) {
			Element tr = trElements.get(i);
			if (tr == null || "".equals(tr.text())) {
				break;
			}
			Map<String, Object> baseMap = parseLssb(tr);
			//http://100.0.0.1:8001/ctais2/wssb/sjcx/print_zzs_flzl4_2012.jsp?k=31
			//http://100.0.0.1:8001/ctais2/wssb/sjcx/print_zzs_flzl4_201605.jsp?k=57
			String dymxListHtml = siteHandler
					.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + tr.select("a").attr("href"));
			Document dyxmDocument = Jsoup.parse(StringUtils.rpAll(dymxListHtml));
			Elements aDyxm = dyxmDocument.select(".unnamed1 A");
			boolean flag = true;
			for (Element b : aDyxm) {
				// print_zzs_flzl2_2013.jsp?k=12
				if (b.attr("href").startsWith("print_zzs_flzl4_")) {
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + b.attr("href"));
					//System.out.println(b.attr("href"));
					Map<String, Object> map = parseSBB(response2, b.attr("href"));
					list.add(CompanyDataUtil.toDocument(baseMap, map));
					flag = false;
				}
			}
			if (flag) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("10016004", "");
				list.add(CompanyDataUtil.toDocument(baseMap, map));
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10016", list);
		return SiteStatus.success;
	}

	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String, Object> parseLssb(Element tr) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10016001", StringUtils.StringFormat(tr.child(2).child(0).text()));
		map.put("10016002", StringUtils.StringFormat(tr.child(3).child(0).text()));
		map.put("10016003", StringUtils.StringFormat(tr.child(4).child(0).text()));
		map.put("serialNumber", "10016");
		return map;
	}

	/**
	 * 解析增值税一般纳税人-- 附列资料(表四)
	 * 
	 * @param html
	 * @param href_
	 * @return
	 */
	private Map<String, Object> parseSBB(String html, String href_) {
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		Map<String, Object> map2 = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		String index4 = "10016004";
		String index6 = "";

		Elements personTrs1 = tables.get(1).getElementsByTag("tr");
			for (int i = 2; i < personTrs1.size(); i++) {
				Elements tds1 = personTrs1.get(i).getElementsByTag("td");
				Map<String, Object> map = new HashMap<String, Object>();
				index6 = index4 + "001";
				for (int j = 1; j < tds1.size(); j++) {
					String td = StringUtils.StringFormat(tds1.get(j).text());
					if("".equals(td)){
						break;
					}
					map.put(index6, td);
					index6 = String.valueOf((Long.parseLong(index6) + 1));
				}
				if (map.size() > 0) {
					list.add(map);
				}
			}
		map2.put(index4, list);
		return map2;
	}
}
