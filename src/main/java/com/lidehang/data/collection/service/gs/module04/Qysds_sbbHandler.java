package com.lidehang.data.collection.service.gs.module04;

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
 *企业所得税和非居民企业所得税(包括季报和年报)-- 2008、2013年企业所得税（年度）--申报表
 */
public class Qysds_sbbHandler implements GSModuleBase<GSSiteHandler> {

	// @Autowired
	// CompanyDataDao companyDataDao;

	@Override
	public SiteStatus start(GSSiteHandler siteHandler) throws SiteLoginFailedException {
		List<org.bson.Document> list = new ArrayList<>();
		// 获取增值税页面数据
		String zzsListHtml = siteHandler.getPage(
				"http://100.0.0.1:8001/ctais2/wssb/sjcx/sbtj_ysbcx.jsp?sssq_q=" + siteHandler.params.getStartTimeStr()
						+ "&sssq_z=" + siteHandler.params.getEndTimeStr() + "&zsxm_dm=04");
		Document document = Jsoup.parse(StringUtils.rpAll(zzsListHtml));
		Elements trElements = document.select(".unnamed1 tr");
		for (int i =1; i < trElements.size(); i++) {
			Element tr = trElements.get(i);
			if (tr == null || "".equals(tr.text())) {
				break;
			}
			Map<String, Object> baseMap = parseLssb(tr);
			//http://100.0.0.1:8001/ctais2/wssb/sjcx/print_ndsds.jsp?k=7
			//http://100.0.0.1:8001/ctais2/wssb/sjcx/print_ndsds_2013.jsp?k=12
			String dymxListHtml = siteHandler
					.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + tr.select("a").attr("href"));
			Document dyxmDocument = Jsoup.parse(StringUtils.rpAll(dymxListHtml));
			Elements aDyxm = dyxmDocument.select(".unnamed1 A");
			boolean flag = true;
			for (Element b : aDyxm) {
				if (b.attr("href").startsWith("print_ndsds.jsp")||b.attr("href").startsWith("print_ndsds_2013.jsp")) {
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + b.attr("href"));
					Map<String, Object> map = parseSBB(response2);
					list.add(CompanyDataUtil.toDocument(baseMap, map));
					flag = false;
				}
			}
			if (flag) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("10036004", "");
				list.add(CompanyDataUtil.toDocument(baseMap, map));
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10036", list);
		return SiteStatus.success;
	}

	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String, Object> parseLssb(Element tr) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10036001", StringUtils.StringFormat(tr.child(2).child(0).text()));
		map.put("10036002", StringUtils.StringFormat(tr.child(3).child(0).text()));
		map.put("10036003", StringUtils.StringFormat(tr.child(4).child(0).text()));
		map.put("serialNumber", "10036");
		return map;
	}

	/**
	 * 企业所得税和非居民企业所得税(包括季报和年报)-- 2008年企业所得税（年度）--申报表
	 * @param html
	 * @param href_
	 * @return
	 */
	private Map<String, Object> parseSBB(String html) {
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		Map<String, Object> map2 = new HashMap<String, Object>();
		List<Object> list3 = new ArrayList<Object>();
		String index4 = "10036004";
		String index6 = "";
		Elements personTrs1 = tables.get(1).getElementsByTag("tr");
		for (int k =1; k < personTrs1.size()-1; k++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Elements tds1 = personTrs1.get(k).getElementsByTag("td");
			index6 = index4 + "001";
			if(tds1.size()==3){
				for (int k1 = 1; k1 < tds1.size(); k1++) {
					String td = StringUtils.StringFormat(tds1.get(k1).text());
					map.put(index6,td);
					index6 = String.valueOf((Long.parseLong(index6) + 1));
				}
			}else {
				for (int k1 = 2; k1 < tds1.size(); k1++) {
					String td = StringUtils.StringFormat(tds1.get(k1).text());
					map.put(index6,td);
					index6 = String.valueOf((Long.parseLong(index6) + 1));
				}
			}
			if (map.size() > 0) {
				list3.add(map);
			}
		}
		map2.put(index4, list3);
		return map2;
	}
}
