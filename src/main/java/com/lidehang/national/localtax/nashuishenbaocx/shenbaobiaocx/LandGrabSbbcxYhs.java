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
import com.lidehang.dataInterface.model.constant.JsonArrayUtils;
import com.lidehang.national.util.CreateImgCodeUtil;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.TaxConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import sun.misc.BASE64Decoder;
/**
 * 地税--纳税申申报表--印花税纳税申报（报告）表
 * @author Hobn
 *
 */
public class LandGrabSbbcxYhs {
	public String selectLandTaxByDate(CloseableHttpClient httpclient,String userId){
		List<org.bson.Document> list = new ArrayList<>();
		//http://www.zjds-etax.cn/wsbs/api/sb/sbb?sbbz=Y&skssqq=2016-10-01&skssqz=2016-12-31&yzpzzlDm=BDA0610794
		String response = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/sb/sbb?sbbz=Y&skssqq=2016-10-01&skssqz=2016-12-31&yzpzzlDm=BDA0610794",userId);
		JSONArray array = JsonArrayUtils.objectToArrray(response);
		for(Object object:array){
			JSONObject json1 = JsonArrayUtils.objectToJson(object);
			Map<String,Object> baseMap=parseLssb(json1);
			//http://www.zjds-etax.cn/wsbs/api/sb/yhsnssbb?pzxh=10023316000041653564
			response = TaxConstants.getMes(httpclient,"http://www.zjds-etax.cn/wsbs/api/sb/yhsnssbb?pzxh="+json1.getString("pzxh"),userId);
			Map<String, Object> map1=parseLSSB(response);
			list.add(CompanyDataUtil.toDocument(baseMap,map1));
			new CompanyDataDaoImpl().addData("91330110583235134A", "11006", list);
//			System.out.println(response);
		}
		return null;
	}
	
	private Map<String, Object> parseDakuohao(String str,String index) {
		JSONObject json=JsonArrayUtils.objectToJson(str);
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<Object> it=json.keys();
		while(it.hasNext()){
			String key=it.next().toString();
			map.put(index, json.getString(key));
			index=String.valueOf((Long.parseLong(index)+1));
		}
		return map;
	}
	
	private List<Object> parseZhongkuohao(String str,String index) {
		JSONArray array = JsonArrayUtils.objectToArrray(str);
		List<Object> list=new ArrayList<Object>();
		Map<String, Object> map1=new HashMap<String,Object>();
		for (Object object : array) {
			Map<String, Object> map=new HashMap<String, Object>();
			JSONObject json=JsonArrayUtils.objectToJson(object);
			Iterator<Object> it=json.keys();
			String index1=index;
			while(it.hasNext()){
				String key=it.next().toString();
				map.put(index1, json.getString(key));
				index1=String.valueOf((Long.parseLong(index1)+1));
			}
			if(map.size()>0){
				list.add(map);
			}
		}
		return list;
	}

	private Map<String, Object> parseLssb(JSONObject json) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("11006001", json.getString("yzpzzl"));
		map.put("11006002", json.getString("sbrq"));
		map.put("11006003", json.getString("ybtse"));
		map.put("11006004", json.getString("kkrq"));
		map.put("serialNumber", "11006");
		return map;
	}

	private Map<String, Object> parseLSSB(String str) {
		JSONObject json = JsonArrayUtils.objectToJson(str);
		List<Object> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map1 = new HashMap<>();
		Iterator it = json.keys();
		String index = "11006005";
		String index1 = "11006005001";
		while (it.hasNext()) {
			String key = it.next().toString();
			String value=json.getString(key);
			if(value.startsWith("{")){
				Map<String, Object> map10=new HashMap<>();
				String index2 = String.valueOf((Long.parseLong(index1) *1000+ 001));
				map10=parseDakuohao(value,index2);
				map.put(index1 , map10 );
			}else if(value.startsWith("[{")){
				List<Object> list1=new ArrayList<>();
				String index2 = String.valueOf((Long.parseLong(index1) *1000+ 001));
				list1=parseZhongkuohao(value,index2);
				map.put(index1, list1);
			}else{
				map.put(index1, json.getString(key));
			}
			index1= String.valueOf((Long.parseLong(index1) + 1));
		}
		/*if (map.size() > 0) {
			list.add(map);
		}*/
		map1.put(index, map);
		return map1;
	}
}
