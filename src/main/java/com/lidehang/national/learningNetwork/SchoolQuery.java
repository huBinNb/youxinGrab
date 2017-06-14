package com.lidehang.national.learningNetwork;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.national.util.ImageUtil;
import com.lidehang.national.util.TaxConstants;

/**
 * 学籍查询
 * 
 * @author Hobn
 *
 */
public class SchoolQuery {
	private static Logger logger=Logger.getLogger(SchoolQuery.class);
	public String getInformation(HttpClient httpclient, String userName, String response) {
		logger.info("学籍查询");
		List<org.bson.Document> list = new ArrayList<org.bson.Document>();
		String initData = response.substring(response.indexOf("initDataInfo"));
		initData = initData.substring(0, initData.indexOf("</script"));
		String initDataNew = initData;
		Document doc = Jsoup.parse(response);
		Elements pics = doc.getElementsByClass("pic");
		Element table = doc.getElementsByClass("mb-table").get(0);
		Elements tds = table.select("td");
		String index = "20001001";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("serialNumber", "20001");

		for (int j = 0; j < tds.size(); j++) {
			initData = initDataNew;
			String value = "";
			String classValue = tds.get(j).attr("class");
			if (classValue != "") {
				classValue = classValue.substring(classValue.length() - 2);
				initData = initData.substring(initData.indexOf(classValue));
				int a = initData.indexOf(classValue) + 3;
				value = initData.substring(6, initData.indexOf("\");"));
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
				/*String name = (String) resultMap.get("20001001");
				LearningGrab.createImgCodeSchool(photo, name);*/
				photo1 = ImageUtil.encodeImgageToBase64(photo);
			}
			resultMap.put(index, photo1);
			index = String.valueOf(Long.parseLong(index) + 1);
		}
		resultMap.put(index, userName);
		list.add(CompanyDataUtil.toDocument(resultMap));
		new CompanyDataDaoImpl().addData((String) resultMap.get("20001021"), "20001", list);
		return null;
	}

}