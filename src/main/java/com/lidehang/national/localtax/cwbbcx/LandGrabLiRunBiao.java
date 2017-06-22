package com.lidehang.national.localtax.cwbbcx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.extensions.compactnotation.CompactData;

import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.action.DsAction;
import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.national.util.CreateImgCodeUtil;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.TaxConstants;
import com.lidehang.national.util.TimeUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * 地税--财务报表--利润表
 * 
 * @author Hobn
 *
 */
public class LandGrabLiRunBiao {
	private static Logger logger = Logger.getLogger(LandGrabLiRunBiao.class);
	public String selectLandTaxByDate(CloseableHttpClient httpclient, String userId,String username) {
		logger.info("地税--财务报表--利润表抓取");
		List<org.bson.Document> list = new ArrayList<>();

		// 起始日期
		/*
		 * String date1="2016"; String date2="09";
		 */
		String date1 = "2016";
		String date2 = "09";
		String startTime = date1 + date2;
		Long startTime1 = Long.parseLong(startTime);

		// 当前年月
		Calendar nowTime = Calendar.getInstance();
		int year = nowTime.get(Calendar.YEAR);
		int month = nowTime.get(Calendar.MONTH);// 正常是5月，但是calendar是4月的，但是地税少一个月
		String year1 = String.valueOf(year);
		String month1 = String.valueOf(month);
		if (month < 10) {
			month1 = "0" + month1;
		}
		String nowTime1 = year1 + month1;
		Long nowTime2 = Long.parseLong(nowTime1);
		for (; startTime1 < nowTime2;) {
			List<org.bson.Document> list1 = new ArrayList<>();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("serialNumber", "11027");
			String index = "11027001";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			try {

				String url = "http://www.zjds-etax.cn/wsbs/api/cwbb?ssny=" + startTime;
				String response = TaxConstants.getMes(httpclient, url, userId);
				JSONArray json = JsonArrayUtils.objectToArrray(response);
				JSONObject json1 = JsonArrayUtils.objectToJson(json.get(1));
				String bbdm = (String) json1.get("bbdm");
				String zlbscjuuid = (String) json1.get("zlbscjuuid");
				String cwbbUrl = "http://www.zjds-etax.cn/wsbs/api/cwbb/" + bbdm + "?zlbscjuuid=" + zlbscjuuid;
				response = TaxConstants.getMes(httpclient, cwbbUrl, userId);
				JSONObject json2 = JsonArrayUtils.objectToJson(response);
				String header = response.substring(response.indexOf("header") + 8, response.indexOf("content") - 2);
				JSONObject headerJson = JsonArrayUtils.objectToJson(header);
				Set<String> keys = headerJson.keySet();
				for (String key : keys) {
					String value = (String) headerJson.get(key);
					resultMap.put(index, value);
					index = String.valueOf(Long.parseLong(index) + 1);
				}
				String content = response.substring(response.indexOf("content") + 9, response.indexOf("]}") + 1);
				JSONArray contentArray = JsonArrayUtils.objectToArrray(content);
				List<Object> listPiece = new ArrayList<Object>();
				for (Object piece : contentArray) {
					Map<String, Object> listMap = new HashMap<String, Object>();
					String index1 = String.valueOf(Long.parseLong(index) * 1000 + 1);
					JSONObject pieceJson = JsonArrayUtils.objectToJson(piece);
					Set<String> keyps = pieceJson.keySet();
					for (String keyp : keyps) {
						String value = (String) pieceJson.get(keyp);
						listMap.put(index1, value);
						index1 = String.valueOf(Long.parseLong(index1) + 1);
					}
					listPiece.add(listMap);
				}
				resultMap.put(index, listPiece);
				list1.add(CompanyDataUtil.toDocument(resultMap));
				// 月份加一
				Date start = sdf.parse(startTime);
				Calendar oldTime = Calendar.getInstance();
				oldTime.setTime(start);
				oldTime.add(Calendar.MONTH, 1);
				startTime = sdf.format(oldTime.getTime());
				startTime1 = Long.parseLong(startTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			list.addAll(list1);
		}
		new CompanyDataDaoImpl().addData(username, "11027", list);
		return null;
	}
}