package com.lidehang.national.localtax.nashuishenbaocx.shenbaocx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.lidehang.core.util.JsonArrayUtils;
import com.lidehang.national.util.CreateImgCodeUtil;
import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.TaxConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import sun.misc.BASE64Decoder;
/**
 * 地税--纳税申报查询--申报查询
 * @author Hobn
 *
 */
public class LandGrabSbcx {
	public String selectLandTaxByDate(CloseableHttpClient httpclient,String userId){
		/*CloseableHttpClient httpclient = HttpClients.createDefault();
		String imgCode = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/home/auth/imgcode?sid="+Math.random());
		CreateImgCodeUtil.createImgCode(imgCode);
		Scanner in = new Scanner(System.in);
		String code = in.next();
		System.out.println(code);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("imgCode", code);
		map.put("mmqrdbj", "0");
		map.put("mobile", "3892");
		map.put("password", MD5Util.MD5("changtai1836"));
		map.put("username", "杭州烁云科技有限公司");
		String response = TaxConstants.postMes(httpclient,"http://www.zjds-etax.cn/wsbs/api/home/auth/login",map);
		JSONObject json = JsonArrayUtils.objectToJson(response);
		String userId = json.getString("USERID");
*/		//http://www.zjds-etax.cn/wsbs/api/sscx/nssb/sb?cxbj=0&qsrq=2016-01-01&rqbj=1&zsdlfsDm=9&zsdlfsMc=%E5%85%B6%E4%BB
		//%96%E5%BE%81%E6%94%B6%E4%BB%A3%E7%90%86%E6%96%B9%E5%BC%8F&zsxmDm=30200&zsxmMc=
//	    %E4%B8%93%E9%A1%B9%E6%94%B6%E5%85%A5&zzrq=2016-12-31
		
		String[] RQBJ={"0","1"};
		String[] CXBJ={"0","1"};
		String[] ZSXMMC={"税收收入","社会保险基金收入","非税收入","政府性基金收入","专项收入","行政事业性收费收入","罚没收入","国有资源（资产）有偿使用收入","其他收入"};
		String[] ZSXMDM={"10100","10200","10300","30100","30200","30400","30500","30700","39900"};  //zsxmDm  
		String[] ZSDLFSMC={"自行申报","代收代缴","代扣代缴","委托代征","其他征收代理方式"};
		String[] ZSDLFSDM={"0","1","2","3","9"}; //zsdlfsDm 
		
		//&zsxmDm=30200&zsdlfsDm=9&zsxmMc=E9%A1%B9%E6%94%B6%E5%85%A5&zsdlfsMc=%E5%85%B6%E4%BB%96%E5%BE%81%E6%94%B6%E4%BB%A3%E7%90%86%E6%96%B9%E5%BC%8F
		/*response=TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/sscx/nssb/sb?cxbj=0&qsrq=2016-01-01&rqbj=1"
				+ "+&zzrq=2016-12-31&zsxmDm=30200&zsdlfsDm=9",userId);
		System.out.println(response);*/
		
		/*-降低抓取频率，时间设置长一些，访问时间采用随机数  
		-频繁切换UserAgent（模拟浏览器访问）  
		-多页面数据，随机访问然后抓取数据  
		-更换用户IP  
		-网站提供API，减少风险  
		-多线程   */
		int i=0;
		for (String rqbj : RQBJ) {
			for (String cxbj : CXBJ) {
				for (String zsxmDm : ZSXMDM) {
					for (String zsdlfsDm : ZSDLFSDM) {
						if(i==40){
							try {
								Thread.sleep(300000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							i=0;
						}
					String response = TaxConstants.getMes(httpclient, "http://www.zjds-etax.cn/wsbs/api/sscx/nssb/sb?cxbj="+cxbj+"&qsrq=2016-01-01&rqbj="+rqbj+"&zsxmDm="+zsxmDm+"&zsdlfsDm="+zsdlfsDm+"&zzrq=2016-12-31",userId);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(response);
					System.out.println(i+"  hubbbbbbbbb");
					i++;
					}
				}
			}
		}
		return null;
	}
}
