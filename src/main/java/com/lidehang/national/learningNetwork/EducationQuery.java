package com.lidehang.national.learningNetwork;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.national.util.ImageUtil;
import com.lidehang.national.util.TaxConstants;

/**ttps://my.chsi.com.cn/archive/gdjy/xl/show.action?trnd=1492763442414
 * 学历查询
 * @author Hobn
 */
public class EducationQuery {
//private String url = "https://my.chsi.com.cn/archive/gdjy/xj/show.action?trnd=1492755025037";
private String url = "https://my.chsi.com.cn/archive/gdjy/xl/show.action?trnd=1492763442414";

public String getInformation(HttpClient httpclient,String userName) {
	List<org.bson.Document> list = new ArrayList<org.bson.Document>();
	String response = TaxConstants.getMes(httpclient, url);
	String initData = response.substring(response.indexOf("initDataInfo"));
	initData = initData.substring(0, initData.indexOf("</script"));
	String initDataNew=initData;
	Document doc = Jsoup.parse(response);
	Elements pics = doc.getElementsByClass("pic");
	Element table = doc.getElementsByClass("mb-table").get(0);
	Elements tds = table.select("td");
	String index = "20002001";
	Map<String, Object> resultMap = new HashMap<String, Object>();
	resultMap.put("serialNumber", "20002"); 
	
	for (int j = 0; j < tds.size(); j++) {
		initData=initDataNew;
		String value = "";
		String classValue = tds.get(j).attr("class");
		if (classValue != "") {
			classValue = classValue.substring(classValue.length() - 3);
			initData=initData.substring(initData.indexOf(classValue));
			int a=initData.indexOf(classValue) + 3;
			value = initData.substring(7, initData.indexOf("\");"));
		} else {
			value = tds.get(j).text();
		}
		resultMap.put(index, value);
		index = String.valueOf(Long.parseLong(index) + 1);
	}
	for (int i = 0; i < pics.size(); i++) {
		String photo1;
		String picSrc = pics.get(i).getElementsByTag("img").attr("src");
		String url = "https://my.chsi.com.cn" + picSrc;
		if (url.endsWith("no-photo.png")) {
			photo1 = "";
		} else {
			InputStream photo = TaxConstants.getImgCode(httpclient, url);
			String name = (String) resultMap.get("20002001");
			LearningGrab.createImgCodeEducation(photo,name);
			photo1=ImageUtil.encodeImgageToBase64(new File("D://LearningNetwork//education//"+name+".jpg"));
				}
		resultMap.put(index, photo1);
		index = String.valueOf(Long.parseLong(index) + 1);
	}
	resultMap.put(index,userName);
	list.add(CompanyDataUtil.toDocument(resultMap));
	new CompanyDataDaoImpl().addData((String)resultMap.get("20002016"), "20002", list);
	return null;
}


}