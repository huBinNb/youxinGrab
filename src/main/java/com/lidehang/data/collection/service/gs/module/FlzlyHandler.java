package com.lidehang.data.collection.service.gs.module;

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
 * 获取解析存储增值税申报表 
 */
public class FlzlyHandler implements GSModuleBase<GSSiteHandler> {
	
//	@Autowired
//	CompanyDataDao companyDataDao;
	
	@Override
	public SiteStatus start(GSSiteHandler siteHandler) throws SiteLoginFailedException {
		List<org.bson.Document> list = new ArrayList<>();
		//获取增值税页面数据
		String zzsListHtml = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/sbtj_ysbcx.jsp?sssq_q="+siteHandler.params.getStartTimeStr()+"&sssq_z="+siteHandler.params.getEndTimeStr()+"&zsxm_dm=01");
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
				if(b.attr("href").startsWith("print_zzs_flzl1")){
					String response2 = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/"+b.attr("href"));
					
					Map<String,Object> map = parseSBB(response2,b.attr("href"));
					
					list.add(CompanyDataUtil.toDocument(baseMap,map));
					flag =false;
				}
			}
			if(flag){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("10004004", "");
				list.add(CompanyDataUtil.toDocument(baseMap,map));
			}
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10004", list);
		return SiteStatus.success;
	}
	
	/**
	 * 解析增值税历史申报列表中的某一项
	 */
	private Map<String,Object> parseLssb(Element tr){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("10004001", tr.child(2).child(0).text());
		map.put("10004002", tr.child(3).child(0).text());
		map.put("10004003", tr.child(4).child(0).text());
		map.put("serialNumber", "10004");
		return map;
	}
	
	/**
	 * 解析增值税一般纳税人-- 附列资料(表一)
	 * @param html
	 * @param href_
	 * @return
	 */
	private Map<String,Object> parseSBB(String html, String href_){
		Document document = Jsoup.parse(StringUtils.rpAll(html));
		Elements tables = document.getElementsByTag("table");
		Map<String,Object> map2 = new HashMap<String,Object>();
		String index4 = "10004004";
		String index6 = "";
		
		Map<String, Object> map = new HashMap<String,Object>();
		index6 = index4 + "001";
		Elements personTrs1 = tables.get(1).getElementsByTag("tr");
		if(href_.indexOf("print_zzs_flzl1_2014_yzl")!=-1||href_.indexOf("print_zzs_flzl1_201605")!=-1){
			Elements tds1 = personTrs1.get(3).getElementsByTag("td");
			String td = StringUtils.StringFormat(tds1.get(12).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//17%税率的货物及加工修理修配劳务
			
			tds1 = personTrs1.get(4).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(10).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//17%税率的有形动产租赁
			
			tds1 = personTrs1.get(5).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(10).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//13%税率
			
			tds1 = personTrs1.get(6).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(10).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//11%税率
			
			tds1 = personTrs1.get(7).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(10).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//6%税率
			
			tds1 = personTrs1.get(10).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(12).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//6%征收率
			
			if(href_.indexOf("print_zzs_flzl1_2014_yzl")!=-1){
				tds1 = personTrs1.get(11).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(10).text());
				map.put(index6, td);
				index6 = String.valueOf((Long.parseLong(index6)+1));//5%征收率
				
				tds1 = personTrs1.get(12).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(10).text());
				map.put(index6, td);
				index6 = String.valueOf((Long.parseLong(index6)+1));//4%征收率
				
				tds1 = personTrs1.get(13).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(10).text());//3%征收率的货物及加工修理修配劳务
				tds1 = personTrs1.get(14).getElementsByTag("td");
				String td1 = StringUtils.StringFormat(tds1.get(10).text());//3%征收率的应税服务
				if("".equals(td)){
					if("".equals(td1)){
						map.put(index6, "");
					}else{
						map.put(index6, td1);
					}
				}else{
					if("".equals(td1)){
						map.put(index6, td);
					}else{
						map.put(index6, (Double.parseDouble(td)+(Double.parseDouble(td1))));
					}
				}
				index6 = String.valueOf((Long.parseLong(index6)+1));//3%征收率
				
				tds1 = personTrs1.get(18).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(11).text());//即征即退货物及加工修理修配劳务
				tds1 = personTrs1.get(19).getElementsByTag("td");
				td1 = StringUtils.StringFormat(tds1.get(10).text());//即征即退应税服务
				if("".equals(td)){
					if("".equals(td1)){
						map.put(index6, "");
					}else{
						map.put(index6, td1);
					}
				}else{
					if("".equals(td1)){
						map.put(index6, td);
					}else{
						map.put(index6, (Double.parseDouble(td)+(Double.parseDouble(td1))));
					}
				}
				index6 = String.valueOf((Long.parseLong(index6)+1));//即征即退项目
	
				tds1 = personTrs1.get(20).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(11).text());//货物及加工修理修配劳务
				tds1 = personTrs1.get(21).getElementsByTag("td");
				td1 = StringUtils.StringFormat(tds1.get(10).text());//应税服务
				if("".equals(td)){
					if("".equals(td1)){
						map.put(index6, "");
					}else{
						map.put(index6, td1);
					}
				}else{
					if("".equals(td1)){
						map.put(index6, td);
					}else{
						map.put(index6, (Double.parseDouble(td)+(Double.parseDouble(td1))));
					}
				}
				index6 = String.valueOf((Long.parseLong(index6)+1));//免抵退税项目
	
				tds1 = personTrs1.get(22).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(11).text());//货物及加工修理修配劳务
				tds1 = personTrs1.get(23).getElementsByTag("td");
				td1 = StringUtils.StringFormat(tds1.get(10).text());//应税服务
				if("".equals(td)){
					if("".equals(td1)){
						map.put(index6, "");
					}else{
						map.put(index6, td1);
					}
				}else{
					if("".equals(td1)){
						map.put(index6, td);
					}else{
						map.put(index6, (Double.parseDouble(td)+(Double.parseDouble(td1))));//免税项目
					}
				}
			}
			if(href_.indexOf("print_zzs_flzl1_201605")!=-1){
				tds1 = personTrs1.get(11).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(10).text());//5%征收率的货物及加工修理修配劳务
				tds1 = personTrs1.get(12).getElementsByTag("td");
				String td1 = StringUtils.StringFormat(tds1.get(10).text());//5%征收率的服务、不动产和无形资产
				if("".equals(td)){
					if("".equals(td1)){
						map.put(index6, "");
					}else{
						map.put(index6, td1);
					}
				}else{
					if("".equals(td1)){
						map.put(index6, td);
					}else{
						map.put(index6, (Double.parseDouble(td)+(Double.parseDouble(td1))));
					}
				}
				index6 = String.valueOf((Long.parseLong(index6)+1));//5%征收率
				
				tds1 = personTrs1.get(13).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(10).text());
				map.put(index6, td);
				index6 = String.valueOf((Long.parseLong(index6)+1));//4%征收率
				
				tds1 = personTrs1.get(14).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(10).text());//3%征收率的货物及加工修理修配劳务
				tds1 = personTrs1.get(15).getElementsByTag("td");
				td1 = StringUtils.StringFormat(tds1.get(10).text());//3%征收率的应税服务
				if("".equals(td)){
					if("".equals(td1)){
						map.put(index6, "");
					}else{
						map.put(index6, td1);
					}
				}else{
					if("".equals(td1)){
						map.put(index6, td);
					}else{
						map.put(index6, (Double.parseDouble(td)+(Double.parseDouble(td1))));
					}
				}
				index6 = String.valueOf((Long.parseLong(index6)+1));//3%征收率
				
				tds1 = personTrs1.get(19).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(11).text());//即征即退货物及加工修理修配劳务
				tds1 = personTrs1.get(20).getElementsByTag("td");
				td1 = StringUtils.StringFormat(tds1.get(10).text());//即征即退应税服务
				if("".equals(td)){
					if("".equals(td1)){
						map.put(index6, "");
					}else{
						map.put(index6, td1);
					}
				}else{
					if("".equals(td1)){
						map.put(index6, td);
					}else{
						map.put(index6, (Double.parseDouble(td)+(Double.parseDouble(td1))));
					}
				}
				index6 = String.valueOf((Long.parseLong(index6)+1));//即征即退项目
	
				tds1 = personTrs1.get(21).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(11).text());//货物及加工修理修配劳务
				tds1 = personTrs1.get(22).getElementsByTag("td");
				td1 = StringUtils.StringFormat(tds1.get(10).text());//应税服务
				if("".equals(td)){
					if("".equals(td1)){
						map.put(index6, "");
					}else{
						map.put(index6, td1);
					}
				}else{
					if("".equals(td1)){
						map.put(index6, td);
					}else{
						map.put(index6, (Double.parseDouble(td)+(Double.parseDouble(td1))));
					}
				}
				index6 = String.valueOf((Long.parseLong(index6)+1));//免抵退税项目
	
				tds1 = personTrs1.get(23).getElementsByTag("td");
				td = StringUtils.StringFormat(tds1.get(11).text());//货物及加工修理修配劳务
				tds1 = personTrs1.get(24).getElementsByTag("td");
				td1 = StringUtils.StringFormat(tds1.get(10).text());//应税服务
				if("".equals(td)){
					if("".equals(td1)){
						map.put(index6, "");
					}else{
						map.put(index6, td1);
					}
				}else{
					if("".equals(td1)){
						map.put(index6, td);
					}else{
						map.put(index6, (Double.parseDouble(td)+(Double.parseDouble(td1))));//免税项目
					}
				}
			}
		}if(href_.indexOf("print_zzs_flzl1_201208")!=-1||href_.indexOf("print_zzs_flzl1.jsp")!=-1){
			Elements tds1 = personTrs1.get(10).getElementsByTag("td");
			String td = StringUtils.StringFormat(tds1.get(3).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//17%税率的货物及加工修理修配劳务
			
			map.put(index6, "");
			index6 = String.valueOf((Long.parseLong(index6)+1));//17%税率的有形动产租赁
			
//			tds1 = personTrs1.get(10).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(6).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//13%税率

			map.put(index6, "");
			index6 = String.valueOf((Long.parseLong(index6)+1));//11%税率
			
			map.put(index6, "");
			index6 = String.valueOf((Long.parseLong(index6)+1));//6%税率
			
			tds1 = personTrs1.get(20).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(3).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//6%征收率

			map.put(index6, "");
			index6 = String.valueOf((Long.parseLong(index6)+1));//5%征收率
			
			td = StringUtils.StringFormat(tds1.get(6).text());
			map.put(index6, td);
			index6 = String.valueOf((Long.parseLong(index6)+1));//4%征收率
			if(href_.indexOf("print_zzs_flzl1_201208")!=-1){
				td = StringUtils.StringFormat(tds1.get(9).text());
				map.put(index6, td);
				index6 = String.valueOf((Long.parseLong(index6)+1));//3%征收率
			}else{
				map.put(index6, "");
				index6 = String.valueOf((Long.parseLong(index6)+1));//3%征收率
			}
			map.put(index6, "");
			index6 = String.valueOf((Long.parseLong(index6)+1));//即征即退项目
			map.put(index6, "");
			index6 = String.valueOf((Long.parseLong(index6)+1));//免抵退税项目

			tds1 = personTrs1.get(27).getElementsByTag("td");
			td = StringUtils.StringFormat(tds1.get(9).text());
			map.put(index6, td);//免税项目
		}
		map2.put(index4, map);
		
		return map2;
		
	}

}
