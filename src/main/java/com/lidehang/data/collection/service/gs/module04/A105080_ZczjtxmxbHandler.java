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
 *企业所得税和非居民企业所得税(包括季报和年报)--资产折旧、摊销情况及纳税调整明细表 --A105080
 */
public class A105080_ZczjtxmxbHandler implements GSModuleBase<GSSiteHandler> {

	// CompanyDataDao companyDataDao;

	@Override
	public SiteStatus start(GSSiteHandler siteHandler) throws SiteLoginFailedException {
		List<org.bson.Document> list = new ArrayList<>();
		// 获取增值税页面数据
		String zzsListHtml = siteHandler.getPage(
				"http://100.0.0.1:8001/ctais2/wssb/sjcx/sbtj_ysbcx.jsp?sssq_q=" + siteHandler.params.getStartTimeStr()
						+ "&sssq_z=" + siteHandler.params.getEndTimeStr() + "&zsxm_dm=04");
		// System.out.println(zzsListHtml);
		Document document = Jsoup.parse(StringUtils.rpAll(zzsListHtml));
		Elements trElements = document.select(".unnamed1 tr");
		for (int i =1; i < trElements.size(); i++) {
			Element tr = trElements.get(i);
			if (tr == null || "".equals(tr.text())) {
				break;
			}
			Map<String, Object> baseMap = parseLssb(tr);
			//http://100.0.0.1:8001/ctais2/wssb/sjcx/print_ndsds_2014_nstz_zjtx.jsp?k=17
			String dymxListHtml = siteHandler
					.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + tr.select("a").attr("href"));
			Document dyxmDocument = Jsoup.parse(StringUtils.rpAll(dymxListHtml));
			Elements aDyxm = dyxmDocument.select(".unnamed1 A");
			boolean flag = true;
			for (Element b : aDyxm) {
				if (b.attr("href").startsWith("print_ndsds_2014_nstz_zjtx.jsp")) {
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + b.attr("href"));
					Map<String, Object> map = parseSBB(response2);
					list.add(CompanyDataUtil.toDocument(baseMap, map));
					flag = false;
				}
			}
			if (flag) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("10060004", "");
				list.add(CompanyDataUtil.toDocument(baseMap, map));
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10060", list);
		return SiteStatus.success;
	}
	
	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String, Object> parseLssb(Element tr) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10060001", StringUtils.StringFormat(tr.child(2).child(0).text()));
		map.put("10060002", StringUtils.StringFormat(tr.child(3).child(0).text()));
		map.put("10060003", StringUtils.StringFormat(tr.child(4).child(0).text()));
		map.put("serialNumber", "10060");
		return map;
	}

	/**
	 *企业所得税和非居民企业所得税(包括季报和年报)--资产折旧、摊销情况及纳税调整明细表 --A105080
	 * @param html
	 * @return
	 */
	private Map<String, Object> parseSBB(String html) {
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		Map<String, Object> map2 = new HashMap<String, Object>();
		List<Object> list3 = new ArrayList<Object>();
		String index4 = "10060004";
		String index6 = "";
		String td1="";
		Elements personTrs1 = tables.get(2).getElementsByTag("tr");
		for (int k =9; k < personTrs1.size(); k++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Elements tds1 = personTrs1.get(k).getElementsByTag("td");
			index6 = index4 + "001";
				for (int i = 1; i < tds1.size(); i++) {
					String[] arr=null;
					String td = StringUtils.StringFormat(tds1.get(i).text());
					if(i==1||td.equals("*")){
						map.put(index6, td);
					}else {
						arr=StringUtils.getJsNum1(html, i-1,"col");
						String aa= StringUtils.remSe(arr[k-9]);
						map.put(index6,aa);
					}
					index6 = String.valueOf((Long.parseLong(index6) + 1));
				}
			if (map.size() > 0) {
				list3.add(map);
			}
		}
		map2.put(index4, list3);
		return map2;
	}
}
