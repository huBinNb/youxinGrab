package com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx;

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
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import sun.misc.BASE64Decoder;
/**
 * 地税--纳税申申报表--教育附加申报表
 * @author Hobn
 *
 */
public class LandGrabSbbcxFjs {
	public String selectLandTaxByDate(CloseableHttpClient httpclient, String userId,String username){
		List<org.bson.Document> list = new ArrayList<>();
		//http://www.zjds-etax.cn/wsbs/api/sb/sbb?sbbz=Y&skssqq=2016-10-01&skssqz=2016-12-31&yzpzzlDm=BDA0610678
		String response = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/sb/sbb?sbbz=Y&skssqq=2016-10-01&skssqz=2016-12-31&yzpzzlDm=BDA0610678",userId);
		JSONArray array = JsonArrayUtils.objectToArrray(response);
		for(Object object:array){
			JSONObject json1 = JsonArrayUtils.objectToJson(object);
			Map<String, Object> baseMap=parseLssb(json1);
			//http://www.zjds-etax.cn/wsbs/api/sb/fjssbb?pzxh=10023316000041648640
			response = TaxConstants.getMes(httpclient,"http://www.zjds-etax.cn/wsbs/api/sb/fjssbb?pzxh="+json1.getString("pzxh"),userId);
			Map<String, Object> map1=parseLSSB(response);
			list.add(CompanyDataUtil.toDocument(baseMap,map1));
			new CompanyDataDaoImpl().addData(username, "11003", list);
			//System.out.println(response);
		}
		return null;
	}

	private Map<String, Object> parseLSSB(String str) {
		JSONArray array = JsonArrayUtils.objectToArrray(str);
		List<Object> list=new ArrayList<Object>();
		Map<String, Object> map1=new HashMap<String,Object>();
		String index="11003005";
		for (Object object : array) {
			Map<String, Object> map=new HashMap<String, Object>();
			JSONObject json=JsonArrayUtils.objectToJson(object);
			Iterator<Object> it=json.keys();
			String index1="11003005001";
			while(it.hasNext()){
				String key=it.next().toString();
				map.put(index1, json.getString(key));
				index1=String.valueOf((Long.parseLong(index1)+1));
			}
			if(map.size()>0){
				list.add(map);
			}
		}
		map1.put(index, list);
		return map1;
	}

	private Map<String, Object> parseLssb(JSONObject json) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("11003001", json.getString("yzpzzl"));
		map.put("11003002", json.getString("sbrq"));
		map.put("11003003", json.getString("ybtse"));
		map.put("11003004", json.getString("kkrq"));
		map.put("serialNumber", "11003");
		return map;
	}
}
