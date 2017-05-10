package com.lidehang.national.personalCredit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.ls.LSInput;

import com.lidehang.national.util.TaxConstants;

import net.minidev.json.JSONObject;

public class PersonalCreditGrab {
	/**
	 * @param ins
	 * 通过输入流在本地生成验证码图片
	 */
	public static void createImgCode(InputStream ins) {
		File file = new File("D:\\PersonalCredit\\imgCode.jpg");
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
	        System.out.println("验证码写到本地！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	 }
	
	
	/**
	 * @param httpclient
	 * @param Url
	 * @return get获取个人征信图片
	 * get请求
	 */
	public static InputStream getPCImgCode(HttpClient httpclient,String Url){
		InputStream entity = null;
        try {  
            // 创建httpget.    
            HttpGet httpget = new HttpGet(Url);
            httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko"); 
            httpget.setHeader("Referer", "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp");
            CloseableHttpResponse response = (CloseableHttpResponse) httpclient.execute(httpget);
            try {  
                // 获取响应实体    
                entity = response.getEntity().getContent();  
            } finally {  
               // response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {
        	
        }
		return entity;
	}
	
}
