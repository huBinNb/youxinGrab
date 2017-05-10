package com.lidehang.national.localtax.shuiwudengjicx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.print.DocFlavor.STRING;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.dataInterface.model.constant.JsonArrayUtils;
import com.lidehang.national.util.CreateImgCodeUtil;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.TaxConstants;
import com.mongodb.util.JSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import sun.misc.BASE64Decoder;
/**
 * 地税--税务登记查询--基本信息查询
 * @author Hobn
 *
 */
public class LandGrabJbxxcx {
	public String selectLandTaxByDate(CloseableHttpClient httpclient,String userId){
		List<org.bson.Document> list=new ArrayList<>();
		JSONArray array=new JSONArray();
		//http://www.zjds-etax.cn/wsbs/api/dj/qy
	    String response = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/dj/qy",userId);
		array.add(response);
		//http://www.zjds-etax.cn/wsbs/api/zs/sfxy
		String response1 = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/zs/sfxy",userId);
		array.add(response1);
		//http://www.zjds-etax.cn/wsbs/api/rd/sfzrdxx
		String response2 = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/rd/sfzrdxx",userId);
		array.add(response2);
		Map<String, Object> baseMap=parseLssb(array);
		list.add(CompanyDataUtil.toDocument(baseMap));
		new CompanyDataDaoImpl().addData("91330110583235134A", "11026", list);
		return null;
	}

	private Map<String, Object> parseLssb(JSONArray array) {
		String index = "11026001";
		Map<String, Object> map = new HashMap<>();
		map.put("serialNumber", "11026");
		for (Object object : array) {
			String str=object.toString();
			if (str.startsWith("{")) {
				Map<String, Object> map10 = new HashMap<>();
				String index1 = String.valueOf((Long.parseLong(index) * 1000 + 001));
				map10 = parseDakuohao(str, index1);
				map.put(index, map10);
			} else if (str.startsWith("[")) {
				List<Object> list = new ArrayList<>();
				String index1 = String.valueOf((Long.parseLong(index) * 1000 + 001));
				list = parseZhongkuohao(str, index1);
				map.put(index, list);
			} else {
				map.put(index, str);
			}
			index = String.valueOf((Long.parseLong(index) + 1));
		}
		return map;
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
		JSONArray array = JsonArrayUtils.objectToArrray(str);
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
}
