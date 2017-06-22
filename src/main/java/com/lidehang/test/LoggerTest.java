package com.lidehang.test;

import java.util.logging.Logger;

public class LoggerTest {
	  
//    static String strClassName = LoggerTest.class.getName();  
    static Logger logger = Logger.getLogger(LoggerTest.class.getName());
      
    public static double division(int value1, int value2) {  
        double result = 0;  
        try {  
            result = value1 / value2;  
        } catch(ArithmeticException e) {  
        	 logger.severe("[severe]除数不能为0.");  
             logger.warning("[warning]除数不能为0.");  
             logger.info("[info]除数不能为0.");  
             logger.config("[config]除数不能为0.");  
             logger.fine("[fine]除数不能为0.");  
             logger.finer("[finer]除数不能为0.");  
             logger.finest("[finest]除数不能为0.");  
            e.printStackTrace();  
        }  
        return result;  
    }  
  
    public static void main(String[] args) {  
        System.out.println(division(5, 0));  
        System.out.println("dfdfd");
    }  
}
