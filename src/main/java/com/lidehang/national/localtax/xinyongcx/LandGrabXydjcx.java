package com.lidehang.national.localtax.xinyongcx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lidehang.core.util.MongoUtil;
import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.action.DsAction;
import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.national.util.CreateImgCodeUtil;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;
import com.mongodb.DBObject;
import com.mongodb.MongoURI;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import sun.misc.BASE64Decoder;
/**
 * 地税--信用查询--信用等级查询
 * @author Hobn
 */
public class LandGrabXydjcx {
	private static Logger logger = Logger.getLogger(LandGrabXydjcx.class);
	
	public String selectLandTaxByDate(CloseableHttpClient httpclient, String userId,String username){
		logger.info("地税--信用查询--信用等级查询抓取");
		Calendar nowTime=Calendar.getInstance();
		int year=nowTime.get(Calendar.YEAR)-1;
		
		for (int i = year-3; i < year; i++) {
			List<Document> list=new ArrayList<>();
			String response = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/sscx/xydj?pdnd="+String.valueOf(year),userId);
			JSONObject json1=JsonArrayUtils.objectToJson(StringUtils.remZkh(response));
			Map<String, Object> map1=new HashMap<>();
			map1.put("serialNumber", "11001");
			Iterator it = json1.keys();  
			String index="11001001";
			while(it.hasNext()){
				String key=it.next().toString();
				map1.put(index, json1.getString(key));
				index = String.valueOf((Long.parseLong(index) + 1));
			}
			list.add(CompanyDataUtil.toDocument(map1));
			new CompanyDataDaoImpl().addData(username, "11001", list);
		}
		//System.out.println(response);
		return null;
	}
}
