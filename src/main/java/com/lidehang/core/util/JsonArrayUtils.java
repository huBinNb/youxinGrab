package com.lidehang.core.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonArrayUtils {

	public static JSONObject objectToJson(Object data){
		JSONObject json=JSONObject.fromObject(data);
		return json;
	}
	
	public static JSONArray objectToArrray(Object object){
		JSONArray  array=JSONArray.fromObject(object);
		return array;
	}
	
	public static String objectToString(Object object){
		JSONArray  array=objectToArrray(object);
		String st="";
		for(Object ob:array){
			st += (String)ob+",";
		}
		if(array!=null){
			st = st.substring(0, st.length()-1);
		}
		return st;
	}
}
