package com.lidehang.action;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.http.HttpServlet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lidehang.data.collection.dao.CompanyDataDao;
import com.lidehang.data.collection.dao.impl.CompanyDataDaoImpl;
import com.lidehang.data.collection.util.CompanyDataUtil;
import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.national.foreignCurrency.ForeignCurrencyGrab;
import com.lidehang.national.localtax.fapiaocx.LandGrabKaijuFapiaocx;
import com.lidehang.national.localtax.fapiaocx.LandGrabShoudaoFapiaocx;
import com.lidehang.national.localtax.nashuishenbaocx.dianzijiaoshuifukuandayin.LandGrabDzjsfkdy;
import com.lidehang.national.localtax.nashuishenbaocx.koukuancx.LandGrabKkcx;
import com.lidehang.national.localtax.nashuishenbaocx.qianshuicx.LandGrabQscxFmsr;
import com.lidehang.national.localtax.nashuishenbaocx.qianshuicx.LandGrabQscxFssr;
import com.lidehang.national.localtax.nashuishenbaocx.qianshuicx.LandGrabQscxGyzcsysr;
import com.lidehang.national.localtax.nashuishenbaocx.qianshuicx.LandGrabQscxQtsr;
import com.lidehang.national.localtax.nashuishenbaocx.qianshuicx.LandGrabQscxShbxsr;
import com.lidehang.national.localtax.nashuishenbaocx.qianshuicx.LandGrabQscxSssr;
import com.lidehang.national.localtax.nashuishenbaocx.qianshuicx.LandGrabQscxXzsyxsr;
import com.lidehang.national.localtax.nashuishenbaocx.qianshuicx.LandGrabQscxZfxjjsr;
import com.lidehang.national.localtax.nashuishenbaocx.qianshuicx.LandGrabQscxZxsr;
import com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx.LandGrab;
import com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx.LandGrabSbbcxCjrb;
import com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx.LandGrabSbbcxFjs;
import com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx.LandGrabSbbcxTy;
import com.lidehang.national.localtax.nashuishenbaocx.shenbaobiaocx.LandGrabSbbcxYhs;
import com.lidehang.national.localtax.shuiwudengjicx.LandGrabJbxxcx;
import com.lidehang.national.localtax.shuiyuanbaobiaocx.LandGrabSyqycwxxb;
import com.lidehang.national.localtax.shuiyuanbaobiaocx.LandGrabSyqydcb;
import com.lidehang.national.localtax.shuiyuanbaobiaocx.LandGrabSyqyssxx;
import com.lidehang.national.localtax.weizhangcx.LandGrabWeizhangWeiguicx;
import com.lidehang.national.localtax.wenshucx.LandGrabWenshucx;
import com.lidehang.national.localtax.xinyongcx.LandGrabXydjcx;
import com.lidehang.national.util.CreateImgCodeUtil;
import com.lidehang.national.util.ImageUtil;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;

import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 国税信用查询
 * @author Hobn
 *
 */
@RestController
@RequestMapping(value = "/GsxyAction")
public class GsxyAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CompanyDataDao companyDataDao;
	
	private CloseableHttpClient httpclient=HttpClients.createDefault();
	/**
	 * 
	 * @param unifiedSocialCredit 用户名（统一信用代码）
	 * @param corporationIdCard   密码（身份证后8位）
	 * @return
	 */
	@PostMapping(value = "/addData")
	public String addData(@RequestParam String corporationIdCard,@RequestParam String unifiedSocialCredit) {
		List<BasicNameValuePair> values=new ArrayList<BasicNameValuePair>();
		List<org.bson.Document> list=new ArrayList<>();
		Map<String, Object> creditInformationMap=new HashMap<>();
		values.add(new BasicNameValuePair("loginType", "creditID"));    
		values.add(new BasicNameValuePair("show2FA", "false"));          
		values.add(new BasicNameValuePair("loginKey", unifiedSocialCredit));//loginKey:91330110583235134A
		values.add(new BasicNameValuePair("password", corporationIdCard.substring(corporationIdCard.length()-8)));//password:04225017
		values.add(new BasicNameValuePair("returnUrl", ""));
		String respose=TaxConstants.postMes(httpclient, "http://www.zjtax.gov.cn/ids/admin/do_login.jsp", values);
		respose=TaxConstants.getMes(httpclient, "http://www.zjtax.gov.cn/ids/account/main.jsp");
		values.clear();
		values.add(new BasicNameValuePair("TAXPAYERID", unifiedSocialCredit));
	    respose=TaxConstants.postMes(httpclient, "http://www.zjtax.gov.cn/wcm/xmytax/sscx/xingyong.jsp?status=1", values);
		Document doc= Jsoup.parse(StringUtils.rpAll(respose));
		Elements creditInformations= doc.getElementsByClass("gkbk1");
		String index="10065";
		String key=String.valueOf(Long.parseLong(index)*1000+001);
		creditInformationMap.put("serialNumber",index);
		for (int i = 4; i < creditInformations.size()-1; i++) {
			String value=creditInformations.get(i).text();
			creditInformationMap.put(key, value);
			key=String.valueOf(Long.parseLong(key)+1);
		}
		list.add(CompanyDataUtil.toDocument(creditInformationMap));
		new CompanyDataDaoImpl().addData("91330110583235134A", index, list);
		return "success";
	}
	
	@GetMapping(value="/getData")
	@ResponseBody
	public List<org.bson.Document> getData(@RequestParam String corporationIdCard,@RequestParam String unifiedSocialCredit,@RequestParam String type){
		List<org.bson.Document>  data=companyDataDao.getDataByType(unifiedSocialCredit, type);
		if(!data.contains(unifiedSocialCredit)){
			if("success".equals(addData(corporationIdCard, unifiedSocialCredit))){
			data=companyDataDao.getDataByType(unifiedSocialCredit, type);
			}
		}
		return data;
	}
	
	
}
