package com.lidehang.national.action;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.http.HttpServlet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lidehang.data.collection.dao.CompanyDataDao;
import com.lidehang.national.foreignCurrency.ForeignCurrencyGrab;
import com.lidehang.national.foreignCurrency.ForeignDeclaration;
import com.lidehang.national.util.ImageUtil;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 外汇
 * @author Hobn
 */
@RestController
@RequestMapping(value = "/WhAction")
public class WhAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private CompanyDataDao companyDataDao;
	
	private CloseableHttpClient httpclient=HttpClients.createDefault();
	
	
	
	@ApiOperation(value="获得验证码",notes="根据链接获取验证码")
	@GetMapping(value="/getCode")
	//@ResponseBody
	public String  getCode(){
		InputStream imgcode=TaxConstants.getImgCode(httpclient, "http://asone.safesvc.gov.cn/asone/jsp/code.jsp?refresh=" + Math.random());
		ForeignCurrencyGrab.createImgCode(imgcode);
		String code=ImageUtil.encodeImgageToBase64(new File("D:\\ForCurCode\\imgCode.jpg"));
		/*Scanner in=new Scanner(System.in);
		String code=in.next();*/
		return code;
	}

	
	
	//@ResponseBody
	//@RequestParam String orgCode,@RequestParam String userCode,@RequestParam String pwd,@RequestParam String code
    //@GetMapping(value = "/addData")
	//怎么把验证码传给后台
	@ApiOperation(value="抓取外汇数据",notes="模拟用户请求")
	@ApiImplicitParams({
		 @ApiImplicitParam(name = "orgCode", value = "组织机构代码", required = true, dataType = "String"),
         @ApiImplicitParam(name = "userCode", value = "用户代码", required = true, dataType = "String"),
		 @ApiImplicitParam(name = "pwd", value = "密码", required = true, dataType = "String"),
		 @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String")
 })
	@PostMapping(value="/addData")
	public String addData(@RequestParam String orgCode,@RequestParam String userCode,@RequestParam String pwd,@RequestParam String code) {
		//CloseableHttpClient httpclient = HttpClients.createDefault();
	/*	InputStream imgCode = TaxConstants.getImgCode(httpclient,
				"http://asone.safesvc.gov.cn/asone/jsp/code.jsp?refresh=" + Math.random());
		ForeignCurrencyGrab.createImgCode(imgCode);
		Scanner in = new Scanner(System.in);
		String code = in.next();*/
		List<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
	/*	values.add(new BasicNameValuePair("orgCode", "716103128"));
		values.add(new BasicNameValuePair("userCode", "001"));
		values.add(new BasicNameValuePair("pwd", MD5Util.MD5("88065212Zl")));
		values.add(new BasicNameValuePair("check", code));*/
		values.add(new BasicNameValuePair("orgCode", orgCode));
		values.add(new BasicNameValuePair("userCode", userCode));
		values.add(new BasicNameValuePair("pwd", MD5Util.MD5(pwd)));
		values.add(new BasicNameValuePair("check", code));
		// 组织机构代码
		String organizationCode = values.get(0).getValue();
		String response = TaxConstants.postMes(httpclient, "http://asone.safesvc.gov.cn/asone/jsp/checkCode.jsp",
				values);
		values.clear();
		Document document = Jsoup.parse(StringUtils.rpAll(response));
		String safeValidateCode = document.getElementsByAttributeValue("name", "safeValidateCode").val();
		String backUrl = document.getElementsByAttributeValue("name", "backUrl").val();
		String enterUrl = document.getElementsByAttributeValue("name", "enterUrl").val();
		String userCodeNew = document.getElementsByAttributeValue("name", "userCode").val();
		String pwdNew  = document.getElementsByAttributeValue("name", "pwd").val();
		String orgCodeNew  = document.getElementsByAttributeValue("name", "orgCode").val();
		values.add(new BasicNameValuePair("safeValidateCode", safeValidateCode));
		values.add(new BasicNameValuePair("backUrl", backUrl));
		values.add(new BasicNameValuePair("enterUrl", enterUrl));
		values.add(new BasicNameValuePair("userCode", userCodeNew));
		values.add(new BasicNameValuePair("pwd", pwdNew));
		values.add(new BasicNameValuePair("orgCode", orgCodeNew));
		response = TaxConstants.postMes(httpclient, "http://asone.safesvc.gov.cn/asone/servlet/AuthorityServlet",values);
		new ForeignDeclaration().getDeclareQuota(httpclient, organizationCode);
		return "success";
	}
	
	@ApiOperation(value="获取数据",notes="从mongodb中获取数据")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "companyId", value = "组织机构代码", required = true, dataType = "String"),
        @ApiImplicitParam(name = "type", value = "类型", required = true, dataType = "String")
	})
	@GetMapping(value="/getData")
	public List<org.bson.Document> getData(@RequestParam String orgCode,@RequestParam String type,@RequestParam String userCode,@RequestParam String pwd,@RequestParam String code){
		List<org.bson.Document> data=companyDataDao.getDataByType(orgCode, type);
		if(!data.contains(orgCode)){
			if("success".equals(addData(orgCode, userCode, pwd, code))){
				data=companyDataDao.getDataByType(orgCode, type);
			}
		}
		return data;
	}
	
	
}
