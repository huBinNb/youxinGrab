package com.lidehang.national.foreignCurrency;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.ls.LSInput;

import com.lidehang.national.util.TaxConstants;

import net.minidev.json.JSONObject;

public class SinosureGrab {
	/**
	 * @param ins
	 * 通过输入流在本地生成验证码图片
	 */
	public static void createImgCode(InputStream ins) {
		File file = new File("D:\\SinosureCode\\imgCode.jpg");
        try {
	        FileOutputStream fileout = new FileOutputStream(file);  
	        /** 
	         * 根据实际运行效果 设置缓冲区大小 
	         */  
	        byte[] buffer=new byte[1024];  
	        int ch = 0;  
			while ((ch = ins.read(buffer)) != -1) {  
			    fileout.write(buffer,0,ch);  
			}
			ins.close();  
	        fileout.flush();  
	        fileout.close();  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	 }
}
