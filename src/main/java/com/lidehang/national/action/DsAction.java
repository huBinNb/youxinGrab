package com.lidehang.national.action;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.http.HttpServlet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lidehang.data.collection.dao.CompanyDataDao;
import com.lidehang.dataInterface.model.constant.JsonArrayUtils;
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
import com.lidehang.national.util.TaxConstants;

import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "/DsAction")
public class DsAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CompanyDataDao companyDataDao;
	
	private CloseableHttpClient httpclient=HttpClients.createDefault();
	
	@GetMapping(value="/getCode")
	public String  getCode(){
		Double dd=Math.random();
		String imgCode = TaxConstants.getMes(httpclient,
				"http://www.zjds-etax.cn/wsbs/api/home/auth/imgcode?sid=" + Math.random());
		CreateImgCodeUtil.createImgCode(imgCode);
		String code=ImageUtil.encodeImgageToBase64(new File("D:\\LandCode\\imgCode.jpg"));
		return code;  
	} 
	
	
	@PostMapping(value = "/addData")
	public String addData(@RequestParam String username,@RequestParam String password,@RequestParam String mobile,@RequestParam String code) {
		/*CloseableHttpClient httpclient = HttpClients.createDefault();
		String imgCode = TaxConstants.getMes(httpclient,
				"http://www.zjds-etax.cn/wsbs/api/home/auth/imgcode?sid=" + Math.random());
		CreateImgCodeUtil.createImgCode(imgCode);
		Scanner in = new Scanner(System.in);
		String code = in.next();
		System.out.println(code);*/
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("mmqrdbj", "0");
		maps.put("imgCode", code);
		maps.put("mobile", mobile);
		maps.put("password", MD5Util.MD5(password));
		maps.put("username", username);
		/*maps.put("mobile", "3892");
		maps.put("password", MD5Util.MD5("changtai1836"));
		maps.put("username", "杭州烁云科技有限公司");*/
		String response = TaxConstants.postMes(httpclient,"http://www.zjds-etax.cn/wsbs/api/home/auth/login",maps);
		JSONObject json = JsonArrayUtils.objectToJson(response);
		//账户输入错误  userID为空
		String userId = json.getString("USERID");
		 new LandGrabXydjcx().selectLandTaxByDate(httpclient, userId); //纳税申报 信用等级查询 11001
		 new LandGrabSbbcxTy().selectLandTaxByDate(httpclient, userId); //纳税申报 申报表查询 通用申报表 11002
		 new LandGrabSbbcxFjs().selectLandTaxByDate(httpclient, userId); //纳税申报 申报表查询 教育费附加申报表
		// 11003
		 new LandGrabSbbcxCjrb().selectLandTaxByDate(httpclient, userId); //纳税申报 申报表查询
		// 残疾人就业保障金缴费申报表 11004
		 new LandGrab().selectLandTaxByDate(httpclient, userId); //纳税申报 申报表查询
		// 社会保险费缴费申报表（适用单位缴费人） 11005
		 new LandGrabSbbcxYhs().selectLandTaxByDate(httpclient, userId); //纳税申报 申报表查询
		// 印花税纳税申报（报告）表 11006
		 new LandGrabKkcx().selectLandTaxByDate(httpclient, userId); //纳税申报 扣款查询 11007
		 new LandGrabQscxSssr().selectLandTaxByFeestaxes(httpclient, userId); //纳税申报 欠税查询 税收收入
		// 11008
		 new LandGrabQscxShbxsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询
		// 社会保险基金收入 11009 无数据
		 new LandGrabQscxFssr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 非税收入
		// 11010
		 new LandGrabQscxZfxjjsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询
		// 政府性基金收入 11011
		 new LandGrabQscxZxsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 专项收入
		// 11012
		 new LandGrabQscxXzsyxsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询
		// 行政事业性收费收入 11013
		 new LandGrabQscxFmsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 罚没收入
		// 11014
		 new LandGrabQscxGyzcsysr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询
		// 国有资源（资产）有偿使用收入 11015
		 new LandGrabQscxQtsr().selectLandTaxByFeestaxes(httpclient, userId); // 纳税申报 欠税查询 其他收入
		// 11016
		 new LandGrabDzjsfkdy().selectLandTaxByDate(httpclient, userId); // 纳税申报 电子缴税付款凭证打印
	// 11017
		 new LandGrabShoudaoFapiaocx().selectLandTaxByDate(httpclient, userId); //发票查询 收到的电子发票查询
	 	// 11018
		 new LandGrabKaijuFapiaocx().selectLandTaxByDate(httpclient, userId); //发票查询 开具的电子发票查询
		// 11019
		 new LandGrabWenshucx().selectLandTaxByDate(httpclient, userId); //文书查询 11020
		 new LandGrabWeizhangWeiguicx().selectLandTaxByDate(httpclient, userId); //违章违规综合查询
		// 11021
		 new LandGrabSyqyssxx().selectLandTaxByDate(httpclient, userId); //税源报表查询 重点税源企业税收信息
		// 11022
		 new LandGrabSyqydcb().selectLandTaxByDate(httpclient, userId); //税源报表查询 11023
		// 重点税源企业景气调查问卷（月报）表
		 new LandGrabSyqycwxxb().selectLandTaxByDate(httpclient, userId); //税源报表查询 11024
		// 重点税源企业财务信息（季报）表
        // new LandGrabSyfdcqyxxb().selectLandTaxByDate("201601", "201702");//税源报表查询
		// 重点税源房地产企业开发经营信息（季报）表 11025
		new LandGrabJbxxcx().selectLandTaxByDate(httpclient, userId); //税务登记查询 基本信息查询 11026
		//未完成
		 /*new LandGrabSbcx().selectLandTaxByDate();//纳税申报 申报查询 多项选择
		 new LandGrabRukucx().selectLandTaxByDate();//纳税申报 入库查询 多项选择
		  */	
		return "success";
	}
	
	@GetMapping(value="/getData")
	@ResponseBody
	public List<Document> getData(@RequestParam String username,@RequestParam String type,@RequestParam String password,@RequestParam String mobile,@RequestParam String code){
		List<Document> data=companyDataDao.getDataByType(username, type);
		if(!data.contains(username)){
			if("success".equals(addData(username, password, mobile, code))){
				data=companyDataDao.getDataByType(username, type);
			}
		}
		return data;
	}

}
