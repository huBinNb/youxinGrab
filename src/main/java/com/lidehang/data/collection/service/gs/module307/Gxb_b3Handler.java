package com.lidehang.data.collection.service.gs.module307;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lidehang.data.collection.constant.SiteStatus;
import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.exception.SiteLoginFailedException;
import com.lidehang.data.collection.service.gs.GSModuleBase;
import com.lidehang.data.collection.service.gs.site.GSSiteHandler;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.national.util.StringUtils;

/**
 * 企业所得税年度关联企业申报表-- 购销表（表三）
 */
public class Gxb_b3Handler implements GSModuleBase<GSSiteHandler> {
	
//	@Autowired
//	CompanyDataDao companyDataDao;
	
	@Override
	public SiteStatus start(GSSiteHandler siteHandler) throws SiteLoginFailedException {
		List<org.bson.Document> list = new ArrayList<>();
		//获取增值税页面数据
		String zzsListHtml = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/sbtj_ysbcx.jsp?sssq_q="+siteHandler.params.getStartTimeStr()+"&sssq_z="+siteHandler.params.getEndTimeStr()+"&zsxm_dm=307");
//		System.out.println(zzsListHtml);
		Document document = Jsoup.parse(StringUtils.rpAll(zzsListHtml));
		Elements trElements = document.select(".unnamed1 tr");
		for(int i=1;i<trElements.size();i++){
			Element tr = trElements.get(i);
			if(tr == null||"".equals(tr.text())){
				break;
			}
			Map<String,Object> baseMap = parseLssb(tr);
			
			String dymxListHtml = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/"+tr.select("a").attr("href"));
			Document dyxmDocument = Jsoup.parse(StringUtils.rpAll(dymxListHtml));
			Elements aDyxm = dyxmDocument.select(".unnamed1 A");
			boolean flag = true;
			for(Element b:aDyxm){
				//http://100.0.0.1:8001/ctais2/wssb/sjcx/print_ndywwl_b3.jsp?k=0
				if(b.attr("href").startsWith("print_ndywwl_b3")){
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/"+b.attr("href"));
					
					Map<String,Object> map = parseSBB(response2,b.attr("href"));
					
					list.add(CompanyDataUtil.toDocument(baseMap,map));
					flag =false;
				}
			}
			if(flag){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("10029004", "");
				list.add(CompanyDataUtil.toDocument(baseMap,map));
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10029", list);
		return SiteStatus.success;
	}
	
	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String,Object> parseLssb(Element tr){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10029001", StringUtils.StringFormat(tr.child(2).child(0).text()));
		map.put("10029002",StringUtils.StringFormat( tr.child(3).child(0).text()));
		map.put("10029003", StringUtils.StringFormat(tr.child(4).child(0).text()));
		map.put("serialNumber", "10029");
		return map;
	}
	
	/**
	 * 企业所得税年度关联企业申报表-- 购销表（表三）
	 * @param html
	 * @param href_
	 * @return
	 */
	private Map<String,Object> parseSBB(String html, String href_){
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		Map<String,Object> map2 = new HashMap<String,Object>();
		String index4 = "10029004";
		String index6 = "";
		
		Map<String, Object> map = new HashMap<String,Object>();
		index6 = index4 + "001";
		
		Elements personTrs1 = tables.get(1).getElementsByTag("tr");
		
			Elements tds1 = personTrs1.get(2).getElementsByTag("td");
			String td = StringUtils.StringFormat(tds1.get(1).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//总购销 购入总额
			
			tds1 = personTrs1.get(2).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(3).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//总购销 销售总额
			
			tds1 = personTrs1.get(3).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(2).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//其中 进口购入
			
			tds1 = personTrs1.get(3).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(5).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//其中 出口销售
			
			tds1 = personTrs1.get(6).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(1).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//其中 国内购入
			
			tds1 = personTrs1.get(6).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(3).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//其中 国内销售
			
			tds1 = personTrs1.get(11).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(0).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//来料加工关联金额
			
			tds1 = personTrs1.get(11).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(1).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//来料加工非关联金额
			
			tds1 = personTrs1.get(11).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(2).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//其他贸易方式关联金额
			
			tds1 = personTrs1.get(11).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(3).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//其他贸易方式非关联金额
			
			tds1 = personTrs1.get(14).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(0).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//占出口销售10%以上的境外销售 境外关联方名称	
			
			tds1 = personTrs1.get(14).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(2).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//占出口销售10%以上的境外销售 境外关联方交易金额
			
			tds1 = personTrs1.get(18).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(0).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//占出口销售10%以上的境外销售 非境外关联方名称
			
			tds1 = personTrs1.get(18).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(2).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//占出口销售10%以上的境外销售 非境外关联交易金额
			
			
			tds1 = personTrs1.get(23).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(0).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//占进口销售10%以上的境外采购 境外关联方名称
			
			tds1 = personTrs1.get(23).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(2).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//占进口销售10%以上的境外采购 境外关联方交易金额
		
			tds1 = personTrs1.get(27).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(0).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//占进口销售10%以上的境外采购 非境外关联方名称
			
			tds1 = personTrs1.get(27).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(2).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//占进口销售10%以上的境外采购 非境外关联交易金额	
		
		map2.put(index4, map);
		
		return map2;
		
	}

}
