package com.lidehang.data.collection.service.gs.module;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;

import net.sf.json.JSONObject;

/**
 * 获取解析存储申报信息 --本期抵扣进项税额结构明细表
 */
public class DkjxsemxHandler implements GSModuleBase<GSSiteHandler> {

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
			// http://100.0.0.1:8001/ctais2/wssb/sjcx/sb_zzs_dkjxsemx_201605.jsp?jspLogic=print&k=54
			String dymxListHtml = siteHandler
					.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + tr.select("a").attr("href"));
			Document dyxmDocument = Jsoup.parse(StringUtils.rpAll(dymxListHtml));
			Elements aDyxm = dyxmDocument.select(".unnamed1 A");
			boolean flag = true;
			for (Element b : aDyxm) {
				if (b.attr("href").startsWith("sb_zzs_dkjxsemx_201605")) {
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + b.attr("href"));
					Map<String, Object> map = parseSBB(response2, b.attr("href"));
					list.add(CompanyDataUtil.toDocument(baseMap, map));
					flag = false;
				}

			}
			if (flag) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("10020004", "");
				list.add(CompanyDataUtil.toDocument(baseMap, map));
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10020", list);
		return SiteStatus.success;
	}

	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String, Object> parseLssb(Element tr) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10020001", StringUtils.StringFormat(tr.child(2).child(0).text()));
		map.put("10020002", StringUtils.StringFormat(tr.child(3).child(0).text()));
		map.put("10020003", StringUtils.StringFormat(tr.child(4).child(0).text()));
		map.put("serialNumber", "10020");
		return map;
	}

	/**
	 * 解析增值税申报表--本期抵扣进项税额结构明细表
	 */
	private Map<String, Object> parseSBB(String html, String href_) {
		//获取json数据
		CloseableHttpClient httpclient = HttpClients.createDefault();
		List<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
		values.add(new BasicNameValuePair("proceduresName", "zwb_multi_get_sbb_cx"));
		values.add(new BasicNameValuePair("paramLength", "3"));
		values.add(new BasicNameValuePair("param1", "330016999076177109"));
		values.add(new BasicNameValuePair("param2", "105"));
		values.add(new BasicNameValuePair("param3", "26"));
		values.add(new BasicNameValuePair("withColName", "true"));
		String response = TaxConstants.postMes(httpclient, "http://100.0.0.1:8001/ctais2/wssb/QueryJsonString_cx.jsp",
				values);
		JSONObject jsonA = JSONObject.fromObject(response);
		Iterator iter = jsonA.keySet().iterator();
		Map<String, String> map10 = new HashMap<String, String>();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = jsonA.getString(key);
			map10.put(key, value);
		}
		map10.get("rows");
		String response1 = map10.get("rows").substring(1, map10.get("rows").length() - 1);
		JSONObject jsonB = JSONObject.fromObject(response1);
		Iterator iter1 = jsonB.keySet().iterator();
		Map<String, String> map11 = new HashMap<String, String>();
		while (iter1.hasNext()) {
			String key = (String) iter1.next();
			String value = jsonB.getString(key);
			map11.put(key, value);
		}
		
		
		//解析
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		Map<String, Object> map2 = new HashMap<String, Object>();
		List<Object> list3 = new ArrayList<Object>();
		String index4 = "10020004";
		String index6 = "";
		Elements personTrs1 = tables.get(1).getElementsByTag("tr");
		for (int k = 1; k < personTrs1.size(); k++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Elements tds1 = personTrs1.get(k).getElementsByTag("td");
			if (tds1.size() == 1) {
				continue;
			} else {
				index6 = index4 + "001";
				for (int k1 = 0; k1 < tds1.size(); k1++) {
					String td = "";
					if (k1 > 1) {
						Element a = tds1.get(k1);
						Element b = a.select("input").first();
						String v = b.attr("name");

						Iterator mapKey = map11.entrySet().iterator();
						while (mapKey.hasNext()) {
							Map.Entry e = (Map.Entry) mapKey.next();
							if (e.getKey() .equals(v) ) {
								td = (String) e.getValue();
							}
						}

					} else {
						td = StringUtils.StringFormat(tds1.get(k1).text());
					}
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
