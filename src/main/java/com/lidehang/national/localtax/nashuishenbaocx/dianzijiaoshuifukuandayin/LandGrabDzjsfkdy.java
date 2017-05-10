package com.lidehang.national.localtax.nashuishenbaocx.dianzijiaoshuifukuandayin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;

import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.dataInterface.model.constant.JsonArrayUtils;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 地税--纳税申报--电子缴税付款凭证打印
 * @author Hobn
 *
 */
public class LandGrabDzjsfkdy {
	public String selectLandTaxByDate(CloseableHttpClient httpclient,String userId){
		/*CloseableHttpClient httpclient = HttpClients.createDefault();
		String imgCode = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/home/auth/imgcode?sid="+Math.random());
		CreateImgCodeUtil.createImgCode(imgCode);
		Scanner in = new Scanner(System.in);
		String code = in.next();
		System.out.println(code);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("imgCode", code);
		map.put("mmqrdbj", "0");
		map.put("mobile", "3892");
		map.put("password", MD5Util.MD5("changtai1836"));
		map.put("username", "杭州烁云科技有限公司");
		String response = TaxConstants.postMes(httpclient,"http://www.zjds-etax.cn/wsbs/api/home/auth/login",map);
		JSONObject json = JsonArrayUtils.objectToJson(response);
		String userId = json.getString("USERID");
		//http://www.zjds-etax.cn/wsbs/api/sscx/nssb/kkjg?kkqsrq=2016-01-01&kkzzrq=2016-12-31
		String response = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/sscx/nssb/kkjg?kkqsrq=2016-01-01&kkzzrq=2016-12-31",userId);
		JSONObject json = JsonArrayUtils.objectToJson(response);
		String userId = json.getString("USERID");
		// http://www.zjds-etax.cn/wsbs/api/sscx/nssb/kkjg?kkqsrq=2016-01-01&kkzzrq=2016-12-31
	*/  List<org.bson.Document> list = new ArrayList<>();
		String response = TaxConstants.getMes(httpclient,
				"http://www.zjds-etax.cn/wsbs/api/sscx/nssb/kkjg?kkqsrq=2016-01-01&kkzzrq=2016-12-31", userId);
		JSONArray array = JsonArrayUtils.objectToArrray(response);
		for (Object object : array) {
			JSONObject json1 = JsonArrayUtils.objectToJson(object);
			Map<String, Object> baseMap = parseLssb(json1);
			String str = StringUtils.repXg(json1.getString("KKRQ"));
			String kkrq = str.substring(0, str.indexOf(" "));
			// http://www.zjds-etax.cn/wsbs/api/sscx/nssb/kkpz?kkrq=2016-11-14
			response = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/sscx/nssb/kkpz?kkrq=" + kkrq,
					userId);
			Map<String, Object> map1 = parseLSSB(response);
			list.add(CompanyDataUtil.toDocument(baseMap, map1));
			new CompanyDataDaoImpl().addData("91330110583235134A", "11017", list);
			// System.out.println(response);
		}
		return null;
	}

	private Map<String, Object> parseDakuohao(String str, String index) {
		JSONObject json = JsonArrayUtils.objectToJson(str);
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<Object> it = json.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			map.put(index, json.getString(key));
			index = String.valueOf((Long.parseLong(index) + 1));
		}
		return map;
	}

	private List<Object> parseZhongkuohao(String str, String index) {
		JSONArray array = JsonArrayUtils.objectToArrray(str.substring(1, str.length() - 1));
		List<Object> list = new ArrayList<Object>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		for (Object object : array) {
			Map<String, Object> map = new HashMap<String, Object>();
			JSONObject json = JsonArrayUtils.objectToJson(object);
			Iterator<Object> it = json.keys();
			String index1 = index;
			while (it.hasNext()) {
				String key = it.next().toString();
				map.put(index1, json.getString(key));
				index1 = String.valueOf((Long.parseLong(index1) + 1));
			}
			if (map.size() > 0) {
				list.add(map);
			}
		}
		return list;
	}

	private Map<String, Object> parseLssb(JSONObject json) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("11017001", json.getString("KKRQ"));
		map.put("serialNumber", "11017");
		return map;
	}

	private Map<String, Object> parseLSSB(String str) {
		JSONObject json = JsonArrayUtils.objectToJson(str);
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map1 = new HashMap<>();
		Iterator it = json.keys();
		String index = "11017002";
		String index1 = "11017002001";
		while (it.hasNext()) {
			String key = it.next().toString();
			String value = json.getString(key);
			if (value.startsWith("{")) {
				Map<String, Object> map10 = new HashMap<>();
				String index2 = String.valueOf((Long.parseLong(index1) * 1000 + 001));
				map10 = parseDakuohao(value, index2);
				map.put(index1, map10);
			} else if (value.startsWith("[")) {
				List<Object> list1 = new ArrayList<>();
				String index2 = String.valueOf((Long.parseLong(index1) * 1000 + 001));
				list1 = parseZhongkuohao(value, index2);
				map.put(index1, list1);
			} else {
				map.put(index1, json.getString(key));
			}
			index1 = String.valueOf((Long.parseLong(index1) + 1));
		}
		/*
		 * if (map.size() > 0) { list.add(map); }
		 */
		map1.put(index, map);
		return map1;
	}
}

