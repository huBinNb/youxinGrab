package com.lidehang.national.foreignCurrency;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;
import com.lidehang.national.util.TimeUtils;

/**
 * 外汇 企业档案信息--企业档案信息
 * 
 * @author Hobn
 *
 */
public class ForeignBasicInfo {
	private static Logger logger=Logger.getLogger(ForeignDeclaration.class);
	// 基础档案管理--企业档案信息
	public String getDeclareQuota(HttpClient httpclient, String organizationCode) {
		String url = "http://bopcom.safesvc.gov.cn/BizforCustomerWeb/servlet/customerSearch?current_appCode=BZCN&asone_addr=asone.safesvc.gov.cn:80";
		String response = TaxConstants.getMes(httpclient, url);
		List<org.bson.Document> list = new ArrayList<>();
		Document doc = Jsoup.parse(response);
		Element info = doc.getElementById("primaryinfo");
		Elements valigns = info.select("[valign=middle]");
		Elements trs = valigns.get(0).select("tr");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("serialNumber", "15002");
		String index = "15002001";
		resultMap.put(index, organizationCode);
		index = String.valueOf(Long.parseLong(index) + 1);
		for (int i = 1; i < 23; i++) {
			String value = null;
			if (i == 2 || i == 3 || i == 5 || (i >= 7 && i <= 14) || i == 16 || i == 18 || i == 19 || i == 21)
				continue;
			Elements tds = trs.get(i).select("td");
			int count = tds.size();
			if (count == 2) {
				for (int j = 1; j < count; j++) {
					value = tds.get(j).select("input").attr("value");
					resultMap.put(index, value);
					index = String.valueOf(Long.parseLong(index) + 1);
				}
			} else if (count == 3) {
				for (int j = 1; j < count; j++) {
					value = tds.get(j).select("input").attr("value");
					resultMap.put(index, value);
					index = String.valueOf(Long.parseLong(index) + 1);
				}
			} else {
				for (int j = 1; j < count; j++) {
					value = tds.get(j).select("input").attr("value");
					resultMap.put(index, value);
					index = String.valueOf(Long.parseLong(index) + 1);
					j++;
				}
			}
		}
		list.add(CompanyDataUtil.toDocument(resultMap));
		new CompanyDataDaoImpl().addData(organizationCode, "15002", list);
		logger.info("外汇 企业档案信息--企业档案信息抓取");
		return null;
	}
}
