package com.lidehang.national.localtax.weizhangcx;

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

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.national.util.CreateImgCodeUtil;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.TaxConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import sun.misc.BASE64Decoder;
/**
 * 地税--违章查询--违章违规综合查询
 * @author Hobn
 *
 */
public class LandGrabWeizhangWeiguicx {
	public String selectLandTaxByDate(CloseableHttpClient httpclient,String userId){
		List<org.bson.Document> list=new ArrayList<>();
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
		*///http://www.zjds-etax.cn/wsbs/api/sscx/wfwz?djrqq=2016-01-01&djrqz=2016-12-31
		String response = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/sscx/wfwz?djrqq=2016-01-01&djrqz=2016-12-31",userId);
		JSONObject jsonVO=JsonArrayUtils.objectToJson(response);
		JSONArray array=JsonArrayUtils.objectToArrray(jsonVO.getString("fZWfwzxxVO"));
		for (Object object : array) {
			JSONObject json1 = JsonArrayUtils.objectToJson(object);
			Map<String, Object> baseMap = parseLssb(json1);
			list.add(CompanyDataUtil.toDocument(baseMap));
			new CompanyDataDaoImpl().addData("91330110583235134A", "11021", list);
		}
		// System.out.println(response);
		return null;
	}
	private Map<String, Object> parseLssb(JSONObject json) {
		JSONArray array = JsonArrayUtils.objectToArrray(json);
		List<Object> list = new ArrayList<>();
		String index = "11021001";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("serialNumber", "11021");
		for (Object object : array) {
			JSONObject json1 = JsonArrayUtils.objectToJson(object);
			Iterator<Object> it = json1.keys();
			while (it.hasNext()) {
				String key = it.next().toString();
				String value = json.getString(key);
				map.put(index, value);
				index = String.valueOf((Long.parseLong(index) + 1));
			}
		}
		return map;
	}
}
