package com.lidehang.data.collection.service.gs.moduleDianZiJiaoFei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lidehang.action.GsAction;
import com.lidehang.data.collection.constant.SiteStatus;
import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.exception.SiteLoginFailedException;
import com.lidehang.data.collection.service.gs.GSModuleBase;
import com.lidehang.data.collection.service.gs.site.GSSiteHandler;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;

/**
 * 电子缴款凭证  --增值税  所得税  10066(增值税)   10067（所得税）
 */
public class DzjkpzHandler implements GSModuleBase<GSSiteHandler> {
	
//	@Autowired
//	CompanyDataDao companyDataDao;
	private static Logger logger =Logger.getLogger(DzjkpzHandler.class);
	@Override
	public SiteStatus start(GSSiteHandler siteHandler) throws SiteLoginFailedException {
		logger.info("国税--电子缴款凭证  --增值税  所得税抓取");
		List<org.bson.Document> list = new ArrayList<>();
	    String sssq_q=siteHandler.params.getStartTimeStr();
	    String sssq_z=siteHandler.params.getEndTimeStr();
		int year  = Integer.parseInt(sssq_q.substring(0, 4));
		int year1 = Integer.parseInt(sssq_z.substring(0, 4));
		String response="";
		//list1 = new ArrayList<Object>();
		//获取增值税页面数据   按年查询
		for(int q=year;q<=year1;q++){
			Map<String,Object> map=new HashMap<String,Object>();
			if(q==year1){
				response = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/sbtj_dzjkpz.jsp?sssq_q="+q+"0101"+"&sssq_z="+sssq_z);
			}else{
				response = siteHandler.getPage("http://100.0.0.1:8001/ctais2/wssb/sjcx/sbtj_dzjkpz.jsp?sssq_q="+q+"0101"+"&sssq_z="+q+"1231");
			}
			Document document = Jsoup.parse(StringUtils.rpAll(response));
//			System.out.println("------------开始解析数据！-----------");
			String index="10066";
			String index1 = index + "001";
			Elements basicsDatas=document.getElementsByClass("pup1");
			Elements messages = document.getElementsByClass("unnamed1");
			map.put("serialNumber",index);
			int s =messages.get(0).getElementsByTag("tr").size();
			if(messages.get(0).getElementsByTag("tr").size()<3) continue; 
			//获得基础信息
			for (int j = 0; j < basicsDatas.size()-6; j++) {
				Elements tds=basicsDatas.get(j).select("td");
				for (int p = 0; p < tds.size(); p++) {
					String value=tds.get(p).text();
					if(value.contains("：")){
						value=value.substring(value.indexOf("：")+1);
					}
					map.put(index1, value);
					index1=String.valueOf((Long.parseLong(index1)+1));
				}
			}
			
			//获得列表
			for(Element table:messages){
				Elements personTrs = table.getElementsByTag("tr");
				List<Object> list1=new ArrayList<>();
				for(int i=1;i<personTrs.size()-1;i++){
					Map<String,Object> map1 = new HashMap<String,Object>();
					String index2 = index1 + "001";
					Elements tds = personTrs.get(i).getElementsByTag("td");
					for(int d=2;d<tds.size();d++){
						String value = StringUtils.StringFormat(tds.get(d).text());
						map1.put(index2, value);//查询后列表属性
						index2 = String.valueOf((Long.parseLong(index2)+1));
					}
					list1.add(map1);
					map.put(index1, list1);
				}
			}
			list.add(CompanyDataUtil.toDocument(map));
		}
		new CompanyDataDaoImpl().addData(siteHandler.params.getCompanyId(), "10066", list);
		return SiteStatus.success;
	}
	
}
