package com.lidehang.data.collection.action;


import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServlet;

import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lidehang.data.collection.dao.CompanyDataDao;
import com.lidehang.data.collection.model.param.GSSiteParams;
import com.lidehang.data.collection.service.gs.GSModuleBase;
import com.lidehang.data.collection.service.gs.module.BdcfqdkHandler;
import com.lidehang.data.collection.service.gs.module.CglHandler;
import com.lidehang.data.collection.service.gs.module.Dkdj_dkqdHandler;
import com.lidehang.data.collection.service.gs.module.DkjxsemxHandler;
import com.lidehang.data.collection.service.gs.module.DklHandler;
import com.lidehang.data.collection.service.gs.module.Flzl1Handler;
import com.lidehang.data.collection.service.gs.module.Flzl2Handler;
import com.lidehang.data.collection.service.gs.module.Flzl3Handler;
import com.lidehang.data.collection.service.gs.module.Flzl4Handler;
import com.lidehang.data.collection.service.gs.module.FwjcqdHandler;
import com.lidehang.data.collection.service.gs.module.FzzcHandler;
import com.lidehang.data.collection.service.gs.module.GdzcjxsedkmxHandler;
import com.lidehang.data.collection.service.gs.module.GdzcjxsedkqkHandler;
import com.lidehang.data.collection.service.gs.module.Jms_sbmxbHandler;
import com.lidehang.data.collection.service.gs.module.LrbHandler;
import com.lidehang.data.collection.service.gs.module.SbbHandler;
import com.lidehang.data.collection.service.gs.module.Xgmsbb2005Handler;
import com.lidehang.data.collection.service.gs.module.XgmsybHandler;
import com.lidehang.data.collection.service.gs.module.XgmzcfzbHandler;
import com.lidehang.data.collection.service.gs.module.ZsgrlxsdsHandler;
import com.lidehang.data.collection.service.gs.module.ZsqysdsHandler;
import com.lidehang.data.collection.service.gs.module.ZswqsdsHandler;
import com.lidehang.data.collection.service.gs.module.ZsxfsHandler;
import com.lidehang.data.collection.service.gs.module.ZszzsHandler;
import com.lidehang.data.collection.service.gs.module04.A000000_Qyxxjcb1Handler;
import com.lidehang.data.collection.service.gs.module04.A000000_Qyxxjcb2Handler;
import com.lidehang.data.collection.service.gs.module04.A000000_Qyxxjcb3Handler;
import com.lidehang.data.collection.service.gs.module04.A100000_SdsnssbmHandler;
import com.lidehang.data.collection.service.gs.module04.A101010_YbqysrmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A102010_YbqyzcmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A104000_QjfymxbHandler;
import com.lidehang.data.collection.service.gs.module04.A105000_NstzmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A105050_ZgxctzmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A105080_ZczjtxmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A105081_GdzczjmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A106000_QysdsmbmsbHandler;
import com.lidehang.data.collection.service.gs.module04.A107020_SdjmyhmxbHandler;
import com.lidehang.data.collection.service.gs.module04.A107040_JmsdsyhmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Bzssjlmxb_fb1Handler;
import com.lidehang.data.collection.service.gs.module04.Fb10_zcjztzmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb11_gqssmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb1_srmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb2_cbfymxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb3_nstzmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb4_qymbksmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb5_ssyhmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb6_jwdmmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb7_zcnstzbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb8_kndnstzbHandler;
import com.lidehang.data.collection.service.gs.module04.Fb9_zjnstzmxbHandler;
import com.lidehang.data.collection.service.gs.module04.Gdzczjtjb_fb2Handler;
import com.lidehang.data.collection.service.gs.module04.Jmsdsmxb_fb3Handler;
import com.lidehang.data.collection.service.gs.module04.Nzqysds_jdAlHandler;
import com.lidehang.data.collection.service.gs.module04.Qysds_sbbHandler;
import com.lidehang.data.collection.service.gs.module307.Dwzfkxqkb_b9Handler;
import com.lidehang.data.collection.service.gs.module307.Gdzcb_b6Handler;
import com.lidehang.data.collection.service.gs.module307.Glgxb_b1Handler;
import com.lidehang.data.collection.service.gs.module307.Gljyhzb_b2Handler;
import com.lidehang.data.collection.service.gs.module307.Gxb_b3Handler;
import com.lidehang.data.collection.service.gs.module307.Lwb_b4Handler;
import com.lidehang.data.collection.service.gs.module307.Rtzjb_b7Handler;
import com.lidehang.data.collection.service.gs.module307.Wxzcb_b5Handler;
import com.lidehang.data.collection.service.gs.module309.Zcssqd_sbmxHandler;
import com.lidehang.data.collection.service.gs.module309.Zcsszx_sbmxHandler;
import com.lidehang.data.collection.service.gs.moduleDianZiJiaoFei.DzjkpzHandler;
import com.lidehang.data.collection.service.gs.site.GSSiteHandler;
import com.lidehang.data.collection.util.VPNSuffixUtil;
import com.lidehang.dataInterface.model.constant.JsonArrayUtils;
import com.lidehang.national.util.TaxConstants;

import net.sf.json.JSONArray;


@Controller
@RequestMapping(value="/GsAction")
public class GsAction extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private ConcurrentHashMap<String, AtomicBoolean> cmap=new ConcurrentHashMap<String,AtomicBoolean>();

	@Autowired
	private CompanyDataDao companyDataDao;
	
	@GetMapping(value="/checkLogin")
	@ResponseBody
	public String checkLogin(@RequestParam String companyId,@RequestParam String vpnPwd,@RequestParam String nationalTaxPwd){
		GSSiteParams params= new GSSiteParams();
		String vpnUser=VPNSuffixUtil.getVpnUser(companyId);
		String nationalTaxUser=companyId;
		//这边传的是日期  需要改变
		String startTimeStr="20100101";
		Calendar nowTime=Calendar.getInstance();
		int year=nowTime.get(Calendar.YEAR);
		int month=nowTime.get(Calendar.MONTH)+1;
		String monthStr="";
		if(month<10){
			monthStr="0"+String.valueOf(month);
		}else {
			monthStr=String.valueOf(month);
		}
		String endTimeStr=String.valueOf(year)+monthStr+"31";
		params.setCompanyId(companyId);
		params.setVpnUser(vpnUser);
		params.setVpnPwd(vpnPwd);
		params.setNationalTaxUser(nationalTaxUser);
		params.setNationalTaxPwd(nationalTaxPwd);
		params.setStartTimeStr(startTimeStr);
		params.setEndTimeStr(endTimeStr);
		List<GSModuleBase<GSSiteHandler>> list=new ArrayList<>();
	    list.add(new SbbHandler());                     //10001
		list.add(new LrbHandler());                     //10006
		list.add(new DzjkpzHandler());		            //10066   10067
     	GSSiteHandler handler = new GSSiteHandler();
		handler.init(params, list);
		return handler.doCheck();
	}
	
	@GetMapping(value="/addData")
	@ResponseBody
	public String addData(@RequestParam String companyId,@RequestParam String vpnPwd,@RequestParam String nationalTaxPwd){
		GSSiteParams params= new GSSiteParams();
		/*params.setCompanyId("91330110583235134A");
		params.setVpnUser("91330110583235134A@hzgs");
		params.setVpnPwd("123456");
		params.setNationalTaxUser("91330110583235134A");
		params.setNationalTaxPwd("000000");*/
		
		/*String companyId="9133088174982751X5";
		String vpnUser=VPNSuffixUtil.getVpnUser("9133088174982751X5");
		String vpnPwd="82751X";
		String nationalTaxUser="9133088174982751X5";
		String nationalTaxPwd="423282";*/
		
		/*String companyId="91330110583235134A";
		String vpnUser=VPNSuffixUtil.getVpnUser("91330110583235134A");
		String vpnPwd="123456";
		String nationalTaxUser="91330110583235134A";
		String nationalTaxPwd="000000";*/
		String vpnUser=VPNSuffixUtil.getVpnUser(companyId);
		String nationalTaxUser=companyId;
		//这边传的是日期  需要改变
		String startTimeStr="20100101";
		Calendar nowTime=Calendar.getInstance();
		int year=nowTime.get(Calendar.YEAR);
		int month=nowTime.get(Calendar.MONTH)+1;
		String monthStr="";
//		String endTimeStr="20170101";
		if(month<10){
			monthStr="0"+String.valueOf(month);
		}else {
			monthStr=String.valueOf(month);
		}
		String endTimeStr=String.valueOf(year)+monthStr+"31";
		params.setCompanyId(companyId);
		params.setVpnUser(vpnUser);
		params.setVpnPwd(vpnPwd);
		params.setNationalTaxUser(nationalTaxUser);
		params.setNationalTaxPwd(nationalTaxPwd);
		params.setStartTimeStr(startTimeStr);
		params.setEndTimeStr(endTimeStr);
		List<GSModuleBase<GSSiteHandler>> list=new ArrayList<>();
	    list.add(new SbbHandler());                     //10001
		/*list.add(new DklHandler());                   //10002
		list.add(new CglHandler());                     //10003
		list.add(new Flzl1Handler());                   //10004
		list.add(new FzzcHandler());                    //10005
*/		list.add(new LrbHandler());                     //10006
        /*list.add(new ZszzsHandler());                 //10007
		list.add(new ZsxfsHandler());                   //10008
		list.add(new ZsqysdsHandler());                 //10009
		list.add(new ZswqsdsHandler());                 //10010
		list.add(new ZsgrlxsdsHandler());      			//10011
		list.add(new Flzl2Handler());                   //10012
		list.add(new GdzcjxsedkqkHandler());            //10013
		list.add(new GdzcjxsedkmxHandler());            //10014
		list.add(new Flzl3Handler());                   //10015
		list.add(new Flzl4Handler());                   //10016
		list.add(new Dkdj_dkqdHandler());               //10017
//		list.add(new Jms_sbmxbHandler());  				//mapfugai
		list.add(new BdcfqdkHandler());                 //10019
		list.add(new DkjxsemxHandler());     			//10020 
		list.add(new FwjcqdHandler());					//10021 
		list.add(new Xgmsbb2005Handler());  			//10022 
		list.add(new XgmzcfzbHandler());             	//10023 
		list.add(new XgmsybHandler());      		    //10024
		list.add(new Zcssqd_sbmxHandler()); 			//10025 
		list.add(new Zcsszx_sbmxHandler());				//10026 
		list.add(new Glgxb_b1Handler());        		//10027 
		list.add(new Gljyhzb_b2Handler());      		//10028 
		list.add(new Gxb_b3Handler());     				//" "
		list.add(new Lwb_b4Handler());              	//10030 
		list.add(new Wxzcb_b5Handler());        	    //使用权  所有权
		list.add(new Gdzcb_b6Handler());                //10032 
		list.add(new Rtzjb_b7Handler());                //10033 
		list.add(new Dwzfkxqkb_b9Handler());            //10034
	    list.add(new Nzqysds_jdAlHandler());            //10035
	    list.add(new Qysds_sbbHandler()); 		        //10036 
		list.add(new Fb1_srmxbHandler());    			//10037
		list.add(new Fb2_cbfymxbHandler());				//10038
		list.add(new Fb3_nstzmxbHandler());				//10039
		list.add(new Fb4_qymbksmxbHandler());			//10040   
		list.add(new Fb5_ssyhmxbHandler());				//10041
		list.add(new Fb6_jwdmmxbHandler());				//10042
		list.add(new Fb7_zcnstzbHandler());				//10043
		list.add(new Fb8_kndnstzbHandler());			//10044
		list.add(new Fb9_zjnstzmxbHandler());			//10045
		list.add(new Fb10_zcjztzmxbHandler());			//10046
		list.add(new Fb11_gqssmxbHandler());			//10047
	    list.add(new Bzssjlmxb_fb1Handler());			//10048
		list.add(new Jmsdsmxb_fb3Handler());			//10049
		list.add(new Gdzczjtjb_fb2Handler());		    //10050
		list.add(new A000000_Qyxxjcb1Handler());        //10051 
		list.add(new A000000_Qyxxjcb2Handler());        //10052
		list.add(new A000000_Qyxxjcb3Handler());        //10053
		list.add(new A100000_SdsnssbmHandler());   	    //10054 
		list.add(new A101010_YbqysrmxbHandler());	    //10055
		list.add(new A102010_YbqyzcmxbHandler());	    //10056
		list.add(new A104000_QjfymxbHandler());			//10057
		list.add(new A105000_NstzmxbHandler());		    //10058
		list.add(new A105050_ZgxctzmxbHandler());		//10059
	    list.add(new A105080_ZczjtxmxbHandler());		//10060
		list.add(new A105081_GdzczjmxbHandler());		//10061
	    list.add(new A106000_QysdsmbmsbHandler());		//10062
	 	list.add(new A107020_SdjmyhmxbHandler());		//10063
		list.add(new A107040_JmsdsyhmxbHandler());		//10064
*/		
		list.add(new DzjkpzHandler());		//10066   10067
     	GSSiteHandler handler = new GSSiteHandler();
		handler.init(params, list);
		handler.doGet();
		return "success";
	}
	
	
	@GetMapping(value = "/getData")
	@ResponseBody
		public Object getData(@RequestParam String companyId,@RequestParam String vpnPwd,@RequestParam String nationalTaxPwd,@RequestParam String type) {
		List<Document> data=companyDataDao.getDataByType(companyId,type);

		if(!data.isEmpty()){
		for (Document document : data) {
			Set<String> keys= document.keySet();
			System.out.println(keys);
			for (String key : keys) {
				System.out.println(document.get(key));
				if(document.get(key)!=null&& document.get(key) instanceof List && ((List)document.get(key)).isEmpty()){
					return data;
				}
			}
		}
		
		if(cmap.get(companyId)==null){
			synchronized (cmap) {
				if(cmap.get(companyId)==null){
					cmap.put(companyId, new AtomicBoolean(true));
				}
			}
		}else if(cmap.get(companyId).get()){
			return "processing";
		}else if(!cmap.get(companyId).get()){
			cmap.remove(companyId);
			return "success";
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if("success".equals(addData(companyId, vpnPwd, nationalTaxPwd))){
						List<Document> data=companyDataDao.getDataByType(companyId,type);
						JSONArray json=JsonArrayUtils.objectToArrray(data);
						Map<String, Object> map=new HashMap<String,Object>();
						map.put("jsonStr", json.toString());
						String result=TaxConstants.postMes(HttpClients.createDefault(), "",map);
						System.out.println(result);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					cmap.get(companyId).set(false);
				}
			}
		}).start();
		return "processing";
		}
		return data;
	}
}
	
