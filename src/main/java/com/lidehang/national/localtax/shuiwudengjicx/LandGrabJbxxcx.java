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
import java.util.Set;

import javax.print.DocFlavor.STRING;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.core.util.JsonArrayUtils;
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
	public String selectLandTaxByDate(CloseableHttpClient httpclient,String userId,String username){
		List<org.bson.Document> list=new ArrayList<>();
	 /*  String response = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/dj/qy",userId);*/
		String response1 = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/zs/sfxy",userId);
		/*String response2 = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/rd/sfzrdxx",userId);*/
		Map<String, Object> baseMap=parseLssb(response1);
		list.add(CompanyDataUtil.toDocument(baseMap));
		new CompanyDataDaoImpl().addData(username, "11026", list);
		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> parseLssb(String response1) {
		String index = "11026001";
		Map<String, Object> map = new HashMap<>();
		map.put("serialNumber", "11026");
		
		JSONObject  jsonObject=JsonArrayUtils.objectToJson(response1);
		 Set<String> keys= jsonObject.keySet();
		 for (String key : keys) {
			 String value=jsonObject.getString(key); 
			map.put(index, value);
			index= String.valueOf((Long.parseLong(index) + 1));
		}
		return map;
	}
	
}
