package com.lidehang.national.httpsUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;  
/* 
 * 利用HttpsClient进行post请求的工具类   https post
 */  
public class HttpClientUtil {  
    public String doPost(HttpClient httpClient ,String url,Map<String,String> map,String charset){  
        HttpPost httpPost = null;  
        String result = null;  
        try{  
            httpPost = new HttpPost(url);  
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"); 
          //  httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded"); 
            
            //设置参数  
            List<NameValuePair> list = new ArrayList<NameValuePair>();  
            Iterator iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
                Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));  
            }  
            if(list.size() > 0){  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);  
                httpPost.setEntity(entity);  
            }  
             HttpResponse response = httpClient.execute(httpPost);
             
//             response.setHeader("Content-Type", "text/plain;charset=ISO-8859-1");
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
         
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            }  
        }catch(Exception ex){  
            ex.printStackTrace();  
        }  
        return result;  
    }  
    
    /**
     * 获取个人征信登录页
     * @param httpClient
     * @param url
     * @param map
     * @param charset
     * @return
     */
    public String doPCPost(HttpClient httpClient ,String url,Map<String,String> map,String charset){  
        HttpPost httpPost = null;  
        String result = null;  
        try{  
            httpPost = new HttpPost(url);  
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");  
            httpPost.setHeader("Referer", "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp");  
            //设置参数  
            List<NameValuePair> list = new ArrayList<NameValuePair>();  
            Iterator iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
                Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));  
            }  
            if(list.size() > 0){  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);  
                httpPost.setEntity(entity);  
            }  
             HttpResponse response = httpClient.execute(httpPost);  
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            }  
        }catch(Exception ex){  
            ex.printStackTrace();  
        }  
        return result;  
    }  
    
    /** 
     * 发送get请求   获取个人征信首页
     * @param url       链接地址 
     * @param charset   字符编码，若为null则默认utf-8 
     * @return 
     */      
    public String doPCGet(HttpClient  httpClient,String url,String charset){  
        if(null == charset){  
            charset = "utf-8";  
        }  
        HttpGet httpGet= null;  
        String result = null;  
          
        try {  
//        	httpClient=new SSLClient();
            httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
            httpGet.setHeader("Referer", "https://ipcrs.pbccrc.org.cn/top1.do");
            HttpResponse response = httpClient.execute(httpGet);
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return result;  
    }  
    
    
    
    
    ///测试
    /** 
     * 发送get请求   获取个人征信首页
     * @param url       链接地址 
     * @param charset   字符编码，若为null则默认utf-8 
     * @return 
     */      
    public String doPCGet111(HttpClient  httpClient,String url,String charset,Map<String, String> headerMap ){  
        if(null == charset){  
            charset = "utf-8";  
        }  
        HttpGet httpGet= null;  
        String result = null;  
        try {  
//        	httpClient=new SSLClient();
            httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
            if(headerMap.get("Referer")!=null){
            	httpGet.setHeader("Referer", headerMap.get("Referer"));
            }
            HttpResponse response = httpClient.execute(httpGet);
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
          
        return result;  
    }  
    
    
    /**
     * 获取个人征信登录页
     * @param httpClient
     * @param url
     * @param map
     * @param charset
     * @return
     */
    public String doPCPost111(HttpClient httpClient ,String url,Map<String,String> map,String charset,Map<String,String> headerMap){  
        HttpPost httpPost = null;  
        String result = null;  
        try{  
            httpPost = new HttpPost(url);  
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");  
            if(headerMap.get("Referer")!=null){
            	httpPost.setHeader("Referer", headerMap.get("Referer"));
            }
            if(headerMap.get("Content-Type")!=null){
            	httpPost.setHeader("Content-Type", headerMap.get("Content-Type"));
            }
            if(headerMap.get("Accept")!=null){
            	httpPost.setHeader("Accept", headerMap.get("Accept"));
            }
            //设置参数  
            List<NameValuePair> list = new ArrayList<NameValuePair>();  
            Iterator iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
                Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));  
            }  
            if(list.size() > 0){  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);  
                httpPost.setEntity(entity);  
            }  
             HttpResponse response = httpClient.execute(httpPost);  
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            }  
        }catch(Exception ex){  
            ex.printStackTrace();  
        }  
        return result;  
    }  
    
    
    
    /** 
     * 发送get请求
     * @param url       链接地址 
     * @param charset   字符编码，若为null则默认utf-8 
     * @return 
     */      
    public String doGet222(HttpClient  httpClient,String url,String charset){  
        if(null == charset){  
            charset = "utf-8";  
        }  
        HttpGet httpGet= null;  
        String result = null;  
        try {  
//        	httpClient=new SSLClient();
            httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
            HttpResponse response = httpClient.execute(httpGet);
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return result;  
    }  
 
    

 
    
}  