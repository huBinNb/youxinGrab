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
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.lidehang.data.collection.constant.SiteStatus;
import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.service.SiteHandler;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.dataInterface.model.constant.JsonArrayUtils;
import com.lidehang.national.util.CreateImgCodeUtil;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import sun.misc.BASE64Decoder;

/**
 * 地税--纳税申申报表--社会保险费缴费申报表（适用单位缴费人）
 * 
 * @author Hobn
 *
 */
public class LandGrab {
	public String selectLandTaxByDate(CloseableHttpClient httpclient,String userId) {
		List<org.bson.Document> list = new ArrayList<>();
		// http://www.zjds-etax.cn/wsbs/api/sb/sbb?sbbz=Y&skssqq=2016-10-01&skssqz=2016-12-31&yzpzzlDm=BDA0610100
		String response = TaxConstants.getMes(httpclient,
				"http://www.zjds-etax.cn/wsbs/api/sb/sbb?sbbz=Y&skssqq=2016-10-01&skssqz=2016-12-31&yzpzzlDm=BDA0610222",
				userId);
		JSONArray array = JsonArrayUtils.objectToArrray(response);
		for (Object object : array) {
			JSONObject json1 = JsonArrayUtils.objectToJson(object);
			Map<String, Object> baseMap = parseLssb(json1);
			response = TaxConstants.getMes(httpclient,
					"http://www.zjds-etax.cn/wsbs/api/sb/sbfsbb?fbbz=0&pzxh=" + json1.getString("pzxh"), userId);
			Map<String, Object> map1 = parseLSSB(response);
			list.add(CompanyDataUtil.toDocument(baseMap, map1));
			new CompanyDataDaoImpl().addData("91330110583235134A", "11005", list);
//			System.out.println(response);
		}
		return "success";
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
		map.put("11005001", json.getString("yzpzzl"));
		map.put("11005002", json.getString("sbrq"));
		map.put("11005003", json.getString("ybtse"));
		map.put("11005004", json.getString("kkrq"));
		map.put("serialNumber", "11005");
		return map;
	}

	private Map<String, Object> parseLSSB(String str) {
		JSONObject json = JsonArrayUtils.objectToJson(str);
		List<Object> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map1 = new HashMap<>();
		Iterator it = json.keys();
		String index = "11005005";
		String index1 = "11005005001";
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
		map1.put(index, map);
		return map1;
	}

	/**
	 * @param imgCode
	 *            通过base64加密的字符串解密生成图片保存在本地
	 */
	public static void createImgCode(String imgCode) {
		JSONObject json = JsonArrayUtils.objectToJson(imgCode);
		BASE64Decoder decoder = new BASE64Decoder();
		String imgStr = json.getString("imgCode");
		File file = new File("D:\\LandCode\\imgCode.jpg");
		try {
			byte[] bytes = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < bytes.length; ++i) {
				if (bytes[i] < 0) {// 调整异常数据
					bytes[i] += 256;
				}
			}
			OutputStream out = new FileOutputStream(file);
			out.write(bytes);
			out.flush();
			out.close();
			System.out.println("验证码写到本地！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getImgCode(CloseableHttpClient httpclient) {
		String imgCode = TaxConstants.getMes(httpclient,
				"http://www.zjds-etax.cn/wsbs/api/home/auth/imgcode?sid=" + Math.random());
		return imgCode;
	}
}
