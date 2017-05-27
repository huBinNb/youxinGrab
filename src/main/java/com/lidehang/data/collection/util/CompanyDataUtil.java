package com.lidehang.data.collection.util;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.lidehang.national.util.StringUtils;

/**
 * 企业数据工具类(统一进行sign生成)
 */
public class CompanyDataUtil {

	/**
	 * 整合输入的多个map并生成sign
	 * @return
	 */
	public static Document toDocument(Map<String,Object>... maps){
		Map<String, Object> map = new HashMap<>();
		for(Map<String, Object> m:maps){
			map.putAll(m);
		}
		if(map.get("serialNumber")!=null){
			if("10001".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10001001") +" "+ (String)map.get("10001002") +" "+ (String)map.get("10001003");
				map.put("sign",sign);
			}
			if("10002".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10002001") +" "+ (String)map.get("10002002") +" "+ (String)map.get("10002003");
				map.put("sign",sign);
			}
			if("10003".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10003001") +" "+ (String)map.get("10003002")+" "+ (String)map.get("10003003");
				map.put("sign",sign);
			}
			if("10004".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10004001") +" "+(String)map.get("10004002") +" "+ (String)map.get("10004003");
				map.put("sign",sign);
			}
			if("10005".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10005001") +" "+ (String)map.get("10005002") +" "+ (String)map.get("10005003");
				map.put("sign",sign);
			}
			if("10006".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10006001") +" "+ (String)map.get("10006002") +" "+ (String)map.get("10006003");
				map.put("sign",sign);
			}
			if("10007".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10007001") +" "+(String)map.get("10007002") +" "+ (String)map.get("10007003");
				map.put("sign",sign);
			}
			if("10008".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10008001") +" "+ (String)map.get("10008002") +" "+ (String)map.get("10008003");
				map.put("sign",sign);
			}
			if("10009".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10009001") +" "+ (String)map.get("10009002") +" "+ (String)map.get("10009003");
				map.put("sign",sign);
			}
			if("10010".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10010001") +" "+ (String)map.get("10010002") + (String)map.get("10010003");
				map.put("sign",sign);
			}
			if("10011".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10011001") +" "+(String)map.get("10011002") +" "+ (String)map.get("10011003");
				map.put("sign",sign);
			}
			if("10012".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10012001") +" "+ (String)map.get("10012002") +" "+ (String)map.get("10012003");
				map.put("sign",sign);
			}
			if("10013".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10013001")+" "+ (String)map.get("10013002")+" "+(String)map.get("10013003");
				map.put("sign",sign);
			}
			if("10014".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10014001") +" "+(String)map.get("10014002")+" "+(String)map.get("10014003");
				map.put("sign",sign);
			}
			if("10015".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10015001") +" "+(String)map.get("10015002")+" "+(String)map.get("10015003");
				map.put("sign",sign);
			}
			if("10016".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10016001") +" "+(String)map.get("10016002")+" "+(String)map.get("10016003");
				map.put("sign",sign);
			}
			if("10017".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10017001") +" "+(String)map.get("10017002")+" "+(String)map.get("10017003");
				map.put("sign",sign);
			}
			if("10018".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10018001") +" "+(String)map.get("10018002")+" "+(String)map.get("10018003");
				map.put("sign",sign);
			}
			if("10019".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10019001") +" "+(String)map.get("10019002")+" "+(String)map.get("10019003");
				map.put("sign",sign);
			}
			if("10020".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10020001") +" "+(String)map.get("10020002")+" "+(String)map.get("10020003");
				map.put("sign",sign);
			}
			if("10021".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10021001") +" "+(String)map.get("10021002")+" "+(String)map.get("10021003");
				map.put("sign",sign);
			}
			if("10022".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10022001") +" "+(String)map.get("10022002")+" "+(String)map.get("10022003");
				map.put("sign",sign);
			}
			if("10023".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10023001") +" "+(String)map.get("10023002")+" "+(String)map.get("10023003");
				map.put("sign",sign);
			}
			if("10024".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10024001") +" "+(String)map.get("10024002")+" "+(String)map.get("10024003");
				map.put("sign",sign);
			}
			if("10025".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10025001") +" "+(String)map.get("10025002")+" "+(String)map.get("10025003");
				map.put("sign",sign);
			}
			if("10026".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10026001") +" "+(String)map.get("10026002")+" "+(String)map.get("10026003");
				map.put("sign",sign);
			}
			if("10027".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10027001") +" "+(String)map.get("10027002")+" "+(String)map.get("10027003");
				map.put("sign",sign);
			}
			if("10028".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10028001") +" "+(String)map.get("10028002")+" "+(String)map.get("10028003");
				map.put("sign",sign);
			}
			if("10029".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10029001") +" "+(String)map.get("10029002")+" "+(String)map.get("10029003");
				map.put("sign",sign);
			}
			if("10030".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10030001") +" "+(String)map.get("10030002")+" "+(String)map.get("10030003");
				map.put("sign",sign);
			}
			if("10031".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10031001") +" "+(String)map.get("10031002")+" "+(String)map.get("10031003");
				map.put("sign",sign);
			}
			if("10032".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10032001") +" "+(String)map.get("10032002")+" "+(String)map.get("10032003");
				map.put("sign",sign);
			}
			if("10033".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10033001") +" "+(String)map.get("10033002")+" "+(String)map.get("10033003");
				map.put("sign",sign);
			}
			if("10034".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10034001") +" "+(String)map.get("10034002")+" "+(String)map.get("10034003");
				map.put("sign",sign);
			}
			if("10035".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10035001") +" "+(String)map.get("10035002")+" "+(String)map.get("10035003");
				map.put("sign",sign);
			}
			if("10036".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10036001") +" "+(String)map.get("10036002")+" "+(String)map.get("10036003");
				map.put("sign",sign);
			}
			if("10037".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10037001") +" "+(String)map.get("10037002")+" "+(String)map.get("10037003");
				map.put("sign",sign);
			}
			if("10038".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10038001") +" "+(String)map.get("10038002")+" "+(String)map.get("10038003");
				map.put("sign",sign);
			}
			if("10039".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10039001") +" "+(String)map.get("10039002")+" "+(String)map.get("10039003");
				map.put("sign",sign);
			}
			if("10040".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10040001") +" "+(String)map.get("10040002")+" "+(String)map.get("10040003");
				map.put("sign",sign);
			}
			if("10041".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10041001") +" "+(String)map.get("10041002")+" "+(String)map.get("10041003");
				map.put("sign",sign);
			}
			if("10042".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10042001") +" "+(String)map.get("10042002")+" "+(String)map.get("10042003");
				map.put("sign",sign);
			}
			if("10043".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10043001") +" "+(String)map.get("10043002")+" "+(String)map.get("10043003");
				map.put("sign",sign);
			}
			if("10044".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10044001") +" "+(String)map.get("10044002")+" "+(String)map.get("10044003");
				map.put("sign",sign);
			}
			if("10045".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10045001") +" "+(String)map.get("10045002")+" "+(String)map.get("10045003");
				map.put("sign",sign);
			}
			if("10046".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10046001") +" "+(String)map.get("10046002")+" "+(String)map.get("10046003");
				map.put("sign",sign);
			}
			if("10047".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10047001") +" "+(String)map.get("10047002")+" "+(String)map.get("10047003");
				map.put("sign",sign);
			}
			if("10048".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10048001") +" "+(String)map.get("10048002")+" "+(String)map.get("10048003");
				map.put("sign",sign);
			}
			if("10049".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10049001") +" "+(String)map.get("10049002")+" "+(String)map.get("10049003");
				map.put("sign",sign);
			}
			if("10050".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10050001") +" "+(String)map.get("10050002")+" "+(String)map.get("10050003");
				map.put("sign",sign);
			}
			if("10051".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10051001") +" "+(String)map.get("10051002")+" "+(String)map.get("10051003");
				map.put("sign",sign);
			}
			if("10052".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10052001") +" "+(String)map.get("10052002")+" "+(String)map.get("10052003");
				map.put("sign",sign);
			}
			if("10053".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10053001") +" "+(String)map.get("10053002")+" "+(String)map.get("10053003");
				map.put("sign",sign);
			}
			if("10054".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10054001") +" "+(String)map.get("10054002")+" "+(String)map.get("10054003");
				map.put("sign",sign);
			}
			if("10055".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10055001") +" "+(String)map.get("10055002")+" "+(String)map.get("10055003");
				map.put("sign",sign);
			}
			if("10056".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10056001") +" "+(String)map.get("10056002")+" "+(String)map.get("10056003");
				map.put("sign",sign);
			}
			if("10057".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10057001") +" "+(String)map.get("10057002")+" "+(String)map.get("10057003");
				map.put("sign",sign);
			}
			if("10058".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10058001") +" "+(String)map.get("10058002")+" "+(String)map.get("10058003");
				map.put("sign",sign);
			}
			if("10059".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10059001") +" "+(String)map.get("10059002")+" "+(String)map.get("10059003");
				map.put("sign",sign);
			}
			if("10060".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10060001") +" "+(String)map.get("10060002")+" "+(String)map.get("10060003");
				map.put("sign",sign);
			}
			if("10061".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10061001") +" "+(String)map.get("10061002")+" "+(String)map.get("10061003");
				map.put("sign",sign);
			}
			if("10062".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10062001") +" "+(String)map.get("10062002")+" "+(String)map.get("10062003");
				map.put("sign",sign);
			}
			if("10063".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10063001") +" "+(String)map.get("10063002")+" "+(String)map.get("10063003");
				map.put("sign",sign);
			}
			if("10064".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10064001") +" "+(String)map.get("10064002")+" "+(String)map.get("10064003");
				map.put("sign",sign);
			}
			//国税信用
			if("10065".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10065001");
				map.put("sign",sign);
			}
			
			//电子缴费凭证查询
			if("10066".equals(map.get("serialNumber"))){
				String sign = (String)map.get("10066001");
				map.put("sign",sign);
			}
			
			
			if("11002".equals(map.get("serialNumber"))){
				String sign = (String)map.get("11002002");
				map.put("sign",sign);
			}
			if("11003".equals(map.get("serialNumber"))){
				String sign = (String)map.get("11003002");
				map.put("sign",sign);
			}
			if("11004".equals(map.get("serialNumber"))){
				String sign = (String)map.get("11004002");
				map.put("sign",sign);
			}
			
			if("11005".equals(map.get("serialNumber"))){
				String sign = (String)map.get("11005002");
				map.put("sign",sign);
			}
			if("11006".equals(map.get("serialNumber"))){
				String sign = (String)map.get("11006002");
				map.put("sign",sign);
			}
			
			if("11007".equals(map.get("serialNumber"))){
				String sign = (String)map.get("11007001");
				map.put("sign",sign);
			}
			if("11017".equals(map.get("serialNumber"))){
				String sign = (String)map.get("11017001");
				map.put("sign",sign);
			}
			if("11026".equals(map.get("serialNumber"))){
				String sign = (String)map.get("11026001");
				map.put("sign",sign);
			}
			if("11027".equals(map.get("serialNumber"))){
				String sign = (String)map.get("11027002");
				map.put("sign",sign);
			}
		/*	
			if("11026".equals(map.get("serialNumber"))){
				String sign = (String)map.get("11026001003");
				map.put("sign",sign);
			}
			*/
			
			
			//保理通 批复限额
			if("14001".equals(map.get("serialNumber"))){
				String sign = (String)map.get("14001001");
				map.put("sign",sign);
			}
			//保理通  受理申报
			if("14002".equals(map.get("serialNumber"))){
				String sign = (String)map.get("14002001");
				map.put("sign",sign);
			}
			//保理通   理赔   可损申请查询
			if("14003".equals(map.get("serialNumber"))){
				String sign = (String)map.get("14003001");
				map.put("sign",sign);
			}
			
			
			
			//外汇  已申报（已审核）申报
			if("15001".equals(map.get("serialNumber"))){
				String sign = (String)map.get("15001002");
				map.put("sign",sign);
			}
			//外汇  企业档案信息 
			if("15002".equals(map.get("serialNumber"))){
				String sign = (String)map.get("15002001");
				map.put("sign",sign);
			}
			
			//学信网 学籍查询
			if("20001".equals(map.get("serialNumber"))){
				String sign = (String)map.get("20001021");
				map.put("sign",sign);
			}
			//学信网 学历查询
			if("20002".equals(map.get("serialNumber"))){
				String sign = (String)map.get("20002016");
				map.put("sign",sign);
			}
			
			//个人征信  个人信用报告
			if("21001".equals(map.get("serialNumber"))){
				String sign=(String) map.get("21001001");
				map.put("sign", sign);
				
				
			}
		}
		return new Document(map);
	}
}
