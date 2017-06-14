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
 * 获取解析存储申报信息 -- 固定资产进项税额抵扣明细表
 */
public class GdzcjxsedkmxHandler implements GSModuleBase<GSSiteHandler> {

	// @Autowired
	// CompanyDataDao companyDataDao;
	private static Logger logger =Logger.getLogger(GdzcjxsedkmxHandler.class);
	@Override
	public SiteStatus start(GSSiteHandler siteHandler) throws SiteLoginFailedException {
		logger.info("国税--获取解析存储申报信息 -- 固定资产进项税额抵扣明细表抓取");
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

			// http://100.0.0.1:8001/ctais2/wssb/sjcx/print_zzs_flzl5_mx.jsp?k=2
			String dymxListHtml = siteHandler
					.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + tr.select("a").attr("href"));
			Document dyxmDocument = Jsoup.parse(StringUtils.rpAll(dymxListHtml));
			Elements aDyxm = dyxmDocument.select(".unnamed1 A");
//			boolean flag = true;
			for (Element b : aDyxm) {
				if (b.attr("href").startsWith("print_zzs_flzl5_mx.jsp")) {
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + b.attr("href"));
					Map<String, Object> map = parseSBB(response2, b.attr("href"));
					list.add(CompanyDataUtil.toDocument(baseMap, map));
//					flag = false;
				}
			}
			/*if (flag) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("10014004", "");
				list.add(CompanyDataUtil.toDocument(baseMap, map));// sign生成
			}*/
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10014", list);
		return SiteStatus.success;
	}

	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String, Object> parseLssb(Element tr) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10014001",StringUtils.StringFormat(tr.child(2).child(0).text()));
		map.put("10014002",StringUtils.StringFormat(tr.child(3).child(0).text()));
		map.put("10014003", StringUtils.StringFormat(tr.child(4).child(0).text()));
		map.put("serialNumber", "10014");
		return map;
	}

	/**
	 * 解析增值税一般纳税人-- 固定资产进项税额抵扣明细表
	 * 
	 * @param html
	 * @param href_
	 * @return
	 */
	private Map<String, Object> parseSBB(String html, String href_) {
//		System.out.println(html);
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		Map<String, Object> map2 = new HashMap<String, Object>();
		List<Object> list3 = new ArrayList<Object>();
		String index4 = "10014004";
		String index6 = "";
		// 第二张表中所有的tr
		Elements personTrs1 = tables.get(2).getElementsByTag("tr");
		String num=personTrs1.get(2).select("td").get(0).attr("rowspan");
		int count=Integer.parseInt(num);
		String str= personTrs1.get(2).getElementsByTag("td").get(0).text();
		for (int k = 2; k < personTrs1.size(); k++) {
			Map<String, Object> map = new HashMap<String, Object>();
			// 第n个tr中所有的td
			Elements tds1 = personTrs1.get(k).getElementsByTag("td");
			index6 = index4 + "001";
			if (k > 2 && k < 2+count) {
				for (int k1 = 0; k1 < tds1.size(); k1++) {
					if(k1 ==0){
						map.put(index6,str);
						index6 = String.valueOf((Long.parseLong(index6) + 1));
					}
					String td = StringUtils.StringFormat(tds1.get(k1).text());
					map.put(index6,td);
					index6 = String.valueOf((Long.parseLong(index6) + 1));
				}
			} else {
              		for (int k1 = 0; k1 < tds1.size(); k1++) {
					String td = StringUtils.StringFormat(tds1.get(k1).text());
					map.put(index6, td);
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
