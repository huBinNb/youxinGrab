package com.lidehang.national.sinosure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.national.httpsUtil.HttpClientUtil;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;
import net.sf.json.JSONObject;

/**
 * 限额余额查询
 * @author Hobn
 */
public class QuotaBalanceQuery {
    private  static Logger logger=Logger.getLogger(QuotaBalanceQuery.class);
	private String url = "https://sol.sinosure.com.cn/biz/quotaBalanceQueryAction.do?method=search";
	private String charset = "utf-8";
	private HttpClientUtil httpClientUtil = null;
	/**
	 * 获取限余额查询
	 * @param httpclient
	 * @return
	 */
	public String getBalanceQuota(HttpClient httpclient,String username) {
		logger.info("信保通  限额余额查询开始");
		List<org.bson.Document> list = new ArrayList<org.bson.Document>();
		httpClientUtil = new HttpClientUtil();
		Map<String, String> quotaMap = new HashMap<String, String>();
		quotaMap.put("sortcode", "QuotaBalanceQuery");
		quotaMap.put("__SORT_FIELD", "");
		quotaMap.put("__SORT_METHOD", "");
		quotaMap.put("objectname", "Vb_Myquota");
		quotaMap.put("BUYERENGNAME", "");
		quotaMap.put("BUYERENGNAME_hide", "2");
		quotaMap.put("BUYERENGNAME_datatype", "1");
		quotaMap.put("BUYERENGNAME_changetype", "1");
		quotaMap.put("BIZBUYERNO", "");
		quotaMap.put("BIZBUYERNO_hide", "2");
		quotaMap.put("BIZBUYERNO_datatype", "1");
		quotaMap.put("BIZBUYERNO_changetype", "1");
		quotaMap.put("countryCode", "");
		quotaMap.put("countryCode_hide", "1");
		quotaMap.put("countryCode_datatype", "1");
		quotaMap.put("countryCode_changetype", "0");
		quotaMap.put("BankNo", "");
		quotaMap.put("BankNo_hide", "2");
		quotaMap.put("BankNo_datatype", "1");
		quotaMap.put("BankNo_changetype", "1");
		quotaMap.put("PAYMODE", "");
		quotaMap.put("PAYMODE_hide", "1");
		quotaMap.put("PAYMODE_datatype", "1");
		quotaMap.put("PAYMODE_changetype", "0");
		quotaMap.put("ifrepeat_hide", "1");
		quotaMap.put("ifrepeat_datatype", "1");
		quotaMap.put("ifrepeat_changetype", "0");
		quotaMap.put("deptorempname", "");
		String response = httpClientUtil.doPost(httpclient, url, quotaMap, charset);
		String pageCount = Jsoup.parse(response).select("[name=pagecount]").val();
		Map<String, String> pageMap = new HashMap<String, String>();
		pageMap.put("pagesize", "20");
		pageMap.put("pagecount", pageCount);
		if (pageCount != null && !"".equals(pageCount)) {
			for (int i = 1; i <= Integer.valueOf(pageCount).intValue(); i++) {
				String currentPage = String.valueOf(i);
				pageMap.put("pagenum", currentPage);
				quotaMap.putAll(pageMap);
				response = httpClientUtil.doPost(httpclient, url, quotaMap, charset);
				List<org.bson.Document> listPage = parseQuotaList(response, httpclient);
				list.addAll(listPage);
			}
		}/*else{
 		   list.addAll(null);
 	   }*/
		new CompanyDataDaoImpl().addSinosureData(username, "14005", list);
		logger.info("信保通  限额余额查询结束");
		return null;
	}

	/**
	 * 限额余额的查询列表
	 * @param response
	 * @param httpclient
	 * @return
	 */
	private List<org.bson.Document> parseQuotaList(String response, HttpClient httpclient) {
		List<org.bson.Document> list = new ArrayList<>();
		Document doc = Jsoup.parse(response);
		Element powerTable = doc.getElementById("PowerTable");
		Elements trs = powerTable.select("table").select("tr");
		for (int i = 1; i < trs.size(); i++) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("serialNumber", "14005");
			String index = "14005001";
			Elements tds = trs.get(i).select("td");
			for (int j = 1; j < tds.size(); j++) {
				String value = tds.get(j).text();
				if (value.equals(" ")) {
					value = value.replaceAll(" ", "").trim();
				}
				resultMap.put(index, value);
				index = String.valueOf(Long.parseLong(index) + 1);
			}
			list.add(CompanyDataUtil.toDocument(resultMap));
		}
		return list;
	}
}