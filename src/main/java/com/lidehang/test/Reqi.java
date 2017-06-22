package com.lidehang.test;

import java.util.Calendar;

import org.apache.log4j.Logger;

public class Reqi {
	private static Logger logger=Logger.getLogger(Reqi.class);
	 public static void main(String[] args) {
		Calendar nowTime=Calendar.getInstance();
		int nowYear=nowTime.get(Calendar.YEAR);
		int nowMonth=nowTime.get(Calendar.MONTH)+1;
		int nowDay= nowTime.get(Calendar.DATE);
		logger.info(nowTime);
		System.out.println(nowTime);
		logger.info(nowTime.getTime());
		
	}
}
