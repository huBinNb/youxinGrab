package com.lidehang.data.collection.service.gs.module04;

import java.util.ArrayList;
import java.util.Arrays;
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
 * 企业所得税和非居民企业所得税(包括季报和年报)--企业基础信息表（表一）--A000000
 */
public class A000000_Qyxxjcb1Handler implements GSModuleBase<GSSiteHandler> {

	// @Autowired
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
		for (int i = 17; i < trElements.size(); i++) {
			Element tr = trElements.get(i);
			if (tr == null || "".equals(tr.text())) {
				break;
			}
			Map<String, Object> baseMap = parseLssb(tr);
			// http://100.0.0.1:8001/ctais2/wssb/sjcx/print_ndsds_2014_jcxx.jsp?k=23
			String dymxListHtml = siteHandler
					.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + tr.select("a").attr("href"));
			Document dyxmDocument = Jsoup.parse(StringUtils.rpAll(dymxListHtml));
			Elements aDyxm = dyxmDocument.select(".unnamed1 A");
			boolean flag = true;
			for (Element b : aDyxm) {
				if (b.attr("href").startsWith("print_ndsds_2014_jcxx.jsp")) {
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/" + b.attr("href"));
					Map<String, Object> map = parseSBB(response2);
					list.add(CompanyDataUtil.toDocument(baseMap, map));
					flag = false;
				}
			}
			if (flag) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("10051004", "");
				list.add(CompanyDataUtil.toDocument(baseMap, map));
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10051", list);
		return SiteStatus.success;
	}

	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String, Object> parseLssb(Element tr) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10051001", StringUtils.StringFormat(tr.child(2).child(0).text()));
		map.put("10051002", StringUtils.StringFormat(tr.child(3).child(0).text()));
		map.put("10051003", StringUtils.StringFormat(tr.child(4).child(0).text()));
		map.put("serialNumber", "10051");
		return map;
	}

	/**
	 * 企业所得税和非居民企业所得税(包括季报和年报)--企业基础信息表（表一）--A000000
	 * 
	 * @param html
	 * @return
	 */
	private Map<String, Object> parseSBB(String html) {
		String[] choseArr = { "sblx_1", "hznszjg_0", "jwjmqy_n", "csxzhy_y", "czgljy_n", "ssgs_3", "sykjzd_20",
				"jzbwb_1", "kjfsbh_n", "zczjff_01", "chjjff_03", "hzhsff_2", "sdshsff_9" };
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		Map<String, Object> map2 = new HashMap<String, Object>();
		List<Object> list3 = new ArrayList<Object>();
		String index4 = "10051004";
		String index6 = "";
		Elements personTrs1 = tables.get(2).getElementsByTag("tr");
		for (int k = 0; k < personTrs1.size(); k++) {
			String[] arr = null;
			Map<String, Object> map = new HashMap<String, Object>();
			Elements tds1 = personTrs1.get(k).getElementsByTag("td");
			index6 = index4 + "001";
			switch (tds1.size()) {
			case 1:
				break;
			case 2:
				for (int i = 0; i < tds1.size(); i++) {
					String td12="";
					if(i==0){
						td12=tds1.get(i).text();
					}else{
					String text = tds1.get(i).html();
					String htmlA = "";
					Elements spanS = tds1.get(i).select("span");
					for (Element span : spanS) {
						for (int j = 0; j < choseArr.length; j++) {
							if (choseArr[j].equals(span.attr("id"))) {
								String spanA = "<span id=\"" + span.attr("id") + "\"></span>";
								String spanB = "<span id=\"" + span.attr("id") + "\">√</span>";
								htmlA = text.replace(spanA, spanB);
								break;
							}
						}
					}
					Document doc = Jsoup.parse(htmlA);
					Elements spanE = doc.select("span");
					for (Element span : spanE) {
						span.wrap("<span>,</span>");
					}
					String docText = doc.text();
					arr = docText.split(",");
					for (int j = 0; j < arr.length; j++) {
						if (arr[j].indexOf("√")!=-1) {
							td12 = arr[j-1];
							break;
						}
					}
				}
					map.put(index6,StringUtils.remKh(td12));
					index6 = String.valueOf((Long.parseLong(index6) + 1));
				}
				break;
			case 3:
				for (int i = 0; i < tds1.size(); i++) {
					String td13="";
					if(i==0){
					map.put(index6, "申报方式");
					index6 = String.valueOf((Long.parseLong(index6) + 1));
					}
					String text = tds1.get(i).html();
					String htmlA = "";
					Elements spanS = tds1.get(i).select("span");
					for (Element span : spanS) {
						for (int j = 0; j < choseArr.length; j++) {
							if (choseArr[j].equals(span.attr("id"))) {
								String spanA = "<span id=\"" + span.attr("id") + "\"></span>";
								String spanB = "<span id=\"" + span.attr("id") + "\">√</span>";
								htmlA = text.replace(spanA, spanB);
								break;
							}
						}
					}
					Document doc = Jsoup.parse(htmlA);
					Elements spanE = doc.select("span");
					for (Element span : spanE) {
						span.wrap("<span>,</span>");
					}
					String docText = doc.text();
					arr = docText.split(",");
					for (int j = 0; j < arr.length; j++) {
						if (arr[j].indexOf("√")!=-1) {
							td13 = arr[j - 1];
							break;
						}
					}
					map.put(index6,StringUtils.remKh(td13));
					index6 = String.valueOf((Long.parseLong(index6) + 1));
					if(td13!=null){
						break;
					}
				}
				break;
			default:
				for (int i = 0; i < tds1.size(); i++) {
					String td14="";
					String text = tds1.get(i).html();
					String htmlA = "";
					Elements spanS = tds1.get(i).select("span");
					Element spanFirst=spanS.first();
					if(null==spanFirst){
						td14=text;
					}
					for (Element span : spanS) {
						for (int j = 0; j < choseArr.length; j++) {
							if (choseArr[j].equals(span.attr("id"))) {
								String spanA = "<span id=\"" + span.attr("id") + "\"></span>";
								String spanB = "<span id=\"" + span.attr("id") + "\">√</span>";
								htmlA = text.replace(spanA, spanB);
								break;
							}
						}
					}
					Document doc = Jsoup.parse(htmlA);
					Elements spanE = doc.select("span");
					for (Element span : spanE) {
						span.wrap("<span>,</span>");
					}
					String docText = doc.text();
					arr = docText.split(",");
					for (int j = 0; j < arr.length; j++) {
						if (arr[j].indexOf("√")!=-1) {
							td14 = arr[j - 1];
							break;
						}
					}
					map.put(index6, StringUtils.remKh(td14));
					index6 = String.valueOf((Long.parseLong(index6) + 1));
				}
				break;
			}
				if (map.size() > 0) {
				list3.add(map);
			}
		}
		map2.put(index4, list3);
		return map2;
	}
}
