package com.lidehang.national.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author lidehang-IT
 *String工具类
 */
public class StringUtils {
	public static String rpAll(String str){
		str = str.replaceAll("&nbsp;", "");
		return str;
	}
	/**
	 * @param str
	 * @param i
	 * @return
	 * 数字前面补零i位
	 */
	public static String NumFormat(String str,int i){
		while (str.length()<i) {
			str = "0" + str;
		}
		return str;
	}
	/**
	 * @param str
	 * @return
	 * 替换空
	 */
	public static String StringFormat(String str){
		return str.replaceAll("　", "");
	}
	
	/**
	 * @param html
	 * @return
	 * 获取单点登入信息
	 */
	public static String getDjxh(String html){
		String djxh = html.substring(html.indexOf("function opensbns()"), html.indexOf("getpages.jsp?node=sbns&djxh="));
		String djxh1 = djxh.substring(djxh.indexOf("'")+1,djxh.lastIndexOf("'"));
		return djxh1;
	}
	/**
	 * @param str
	 * @param i
	 * @return
	 * 自动补位i位
	 */
	public static Double NumFormat(Double str,int i){
		BigDecimal   b   =   new   BigDecimal(str);  
		return b.setScale(i,   BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	
	/**
	 * @param str
	 * @return
	 * 百分号去除
	 */
	public static String StringPercent (String str){
		if("-".equals(str)){
			return "0";
		}
		return str.replaceAll("%", "");
	}
	
	/**
	 * @param data
	 * @return 将数字每三位用逗号隔开
	 */
	public static String formatTosepara(double data) {
        DecimalFormat df = new DecimalFormat("#,###"); 
        return df.format(data);
    }
	
	/**
	 * @param str
	 * @return 去除分隔符
	 */
	public static String strFormat(String str){
		if(str!=null&&!"".equals(str)){
			str = str.replaceAll(",", "");
			return str;
		}
		return "";
	}
	
	/**
	 * @param str
	 * @return 链接中替换&amp
	 */
	public static String ampformat(String str) {
		str = str.replace("&", "?");
		return str;
	}
	
	
	/**
	 * 截取字符串成数组
	 * @param html
	 * @param i
	 * @return
	 */
	public static String[] getJsNum(String html,int i){
		String[] array= null;
		String a = html.substring(html.indexOf("cloArray"+i+"= new Array("),html.indexOf("jsonArrayObj.row"+i+"=cloArray"+i));
		a = a.substring(a.indexOf("(")+1,a.indexOf(")"));
		array = a.split(",");
		return array;
	}
	
	
	
	public static String[] getJsNum1(String html,int i,String str){
		String[] array= null;
		String arr = html.substring(html.indexOf("Array"+i+"= new Array("),html.indexOf("jsonArrayObj."+str+i));
		arr = arr.substring(arr.indexOf("(")+1,arr.indexOf(")"));
		array = arr.split(",");
		return array;
	}
	/**
	 * 去掉"
	 * @param str
	 * @return
	 */
	public static String remSe(String str){
		str = str.replace("\"","");
		return str;
	}
	
	/**
	 * 去掉） 
	 * @param str
	 * @return
	 */
	public static String remKh(String str){
		if(str.contains("）")){
			str=str.replace("）", "");
		}
		if(str.contains(" ")){
			str=str.replace(" ", "");
		}
		return str;
	}

	/**
	 * 把/替换成-
	 * @param str
	 * @return
	 */
	public static String repXg(String str){
		str=str.replace("/", "-");
		return str;
	}

	/**
	 * 去掉[]
	 * @param str
	 * @return
	 */
	public static String remZkh(String str){
		str=str.substring(1,str.length()-1);
		return str;
	}
	
    public static void main(String[] args) throws Exception {
    	
    	/*String html = "是（境内<span id=\"id_1\"></span>境外<span id=\"id_2\"></span>）    否<span id=\"id_3\">?</span>";
		Document doc = Jsoup.parse(html);
		Elements  spanEl = doc.select("span");
		for (Element span : spanEl) {
			System.out.println(span.wrap("<span>|</span>"));
		}
        System.out.println(doc);
        System.out.println(doc.text() );*/
    	
    	String a="dfdfd ";
    	System.out.println(a.length());
    	a.trim();
    	System.out.println(a.trim().length());
    	
    	
    /*	String arr="9133010832186722X6@hzgs";
    	String aa=arr.substring(arr.indexOf("@")+1);*/
    	//System.out.println(arr.);
    //	System.out.println("国税".equals("国税")+"  dfdfdf");
    	//System.out.println("aaa".substring(0, 2));
    	//System.out.println(aa);
    	
    }
}
