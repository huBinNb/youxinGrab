package com.lidehang.national.util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	//获取指定月份的天数  
    public static int getDaysByYearMonth(int year, int month) {  
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }  
    
    //遍历
    public int dayReport(Date month){
 	   Calendar cal=Calendar.getInstance();
 	   cal.setTime(month);
 	   int year=cal.get(Calendar.YEAR);
 	   int m=cal.get(Calendar.MONTH);
 	   int dayNumOfMonth=getDaysByYearMonth(year, m);
 	   return dayNumOfMonth;
    }
}
