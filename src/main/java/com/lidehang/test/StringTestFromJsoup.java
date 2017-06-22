package com.lidehang.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.lidehang.national.util.StringUtils;

public class StringTestFromJsoup {
	public static void main(String[] args) {

		String response = readString("f://javaTest//personInfo.txt");
		parseSBB(response);
	}
	
	public static Map<String, Object> parseSBB(String html) {
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		Map<String, Object> map2 = new HashMap<String, Object>();
		List<Object> list3 = new ArrayList<Object>();
		String index4 = "10014004";
		String index6 = "";
		// 第二张表中所有的tr
		Elements personTrs1 = tables.get(2).getElementsByTag("tr");
		String str=personTrs1.get(2).select("td").get(0).attr("rowspan");
		for (int k = 2; k < personTrs1.size(); k++) {
			Map<String, Object> map = new HashMap<String, Object>();
			// 第n个tr中所有的td
			Elements tds1 = personTrs1.get(k).getElementsByTag("td");
			index6 = index4 + "001";
			if (k > 2 && k <2+Integer.parseInt(str)) {
				for (int k1 = 0; k1 < tds1.size(); k1++) {
					String td = StringUtils.StringFormat(tds1.get(k1).text());
					map.put(index6,td);
					index6 = String.valueOf((Long.parseLong(index6) + 1));
				}
			} else {
				for (int k1 = 1; k1 < tds1.size(); k1++) {
					String td = StringUtils.StringFormat(tds1.get(k1).text());
					map.put(index6, td);
					index6 = String.valueOf((Long.parseLong(index6) + 1));
				}
			}
			if (map.size() > 0) {
				list3.add(map);
			}
		}
		map2.put(index4, list3);

		return map2;
	}
	
	private static String readString(String FILE_IN)

	{

		int len = 0;

		StringBuffer str = new StringBuffer("");

		File file = new File(FILE_IN);

		try {

			FileInputStream is = new FileInputStream(file);

			InputStreamReader isr = new InputStreamReader(is);

			BufferedReader in = new BufferedReader(isr);

			String line = null;

			while ((line = in.readLine()) != null)

			{

				if (len != 0) // 处理换行符的问题

				{

					str.append("\r\n" + line);

				}

				else

				{

					str.append(line);

				}

				len++;

			}

			in.close();

			is.close();

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		return str.toString();

	}

}
