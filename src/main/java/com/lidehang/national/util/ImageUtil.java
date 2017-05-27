package com.lidehang.national.util;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;


//import sun.misc.BASE64Decoder;  
//import sun.misc.BASE64Encoder; 

import org.apache.commons.codec.binary.Base64;

/** 
 * @author  作者 E-mail: lcm
 * @date 创建时间：2016年10月8日 下午4:06:59 
 * @version 1.0 
 * @parameter  
 * @since  
 * @return  
 */
public class ImageUtil {
	
	
	/** 
     * 将网络图片进行Base64位编码 
     *  
     * @param imgUrl 
     *            图片的url路径，如http://.....xx.jpg 
     * @return 
     */  
    public static String encodeImgageToBase64(URL imageUrl) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理  
        ByteArrayOutputStream outputStream = null;  
        try {  
            BufferedImage bufferedImage = ImageIO.read(imageUrl);  
            outputStream = new ByteArrayOutputStream();  
            ImageIO.write(bufferedImage, "jpg", outputStream);  
        } catch (MalformedURLException e1) {  
            e1.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        // 对字节数组Base64编码  
//        BASE64Encoder encoder = new BASE64Encoder();  
//        return encoder.encode(outputStream.toByteArray());// 返回Base64编码过的字节数组字符串  
        return Base64.encodeBase64String(outputStream.toByteArray());
    }  
  
    /** 
     * 将本地图片进行Base64位编码 
     *  
     * @param imgUrl 
     *            图片的url路径，如http://.....xx.jpg 
     * @return 
     */  
    public static String encodeImgageToBase64(File imageFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理  
        ByteArrayOutputStream outputStream = null;  
        try {  
            BufferedImage bufferedImage = ImageIO.read(imageFile);  
            outputStream = new ByteArrayOutputStream();  
            ImageIO.write(bufferedImage, "jpg", outputStream);  
        } catch (MalformedURLException e1) {  
            e1.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        // 对字节数组Base64编码  
//        BASE64Encoder encoder = new BASE64Encoder();  
//        return encoder.encode(outputStream.toByteArray());// 返回Base64编码过的字节数组字符串  
        return Base64.encodeBase64String(outputStream.toByteArray());
    }  
  
    /** 
     * 将Base64位编码的图片进行解码，并保存到指定目录
     *  
     * @param base64 
     *            base64编码的图片信息 
     * @return 
     */  
    public static void decodeBase64ToImage(String base64, String path,  
            String imgName) {  
//        BASE64Decoder decoder = new BASE64Decoder();  
        try {  
            FileOutputStream write = new FileOutputStream(new File(path  
                    +"/"+ imgName));  
//            byte[] decoderBytes = decoder.decodeBuffer(base64);
            byte[] decoderBytes = Base64.decodeBase64(base64);
            write.write(decoderBytes);  
            write.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
    

    /**
     * 将图片输入流转化成字符串
     * @param input  
     * @return
     */
    public static String encodeImgageToBase64(InputStream input) {// 将输入流转化为字节数组字符串，并对其进行Base64编码处理  
        ByteArrayOutputStream outputStream = null;  
        try {  
            BufferedImage bufferedImage = ImageIO.read(input);
            outputStream = new ByteArrayOutputStream();  
            ImageIO.write(bufferedImage, "jpg", outputStream);  
        } catch (MalformedURLException e1) {  
            e1.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        // 对字节数组Base64编码  

        return Base64.encodeBase64String(outputStream.toByteArray());
    }  
  

    //encodeImgageToBase64
    public static void main(String[] args) {
    	System.out.println(encodeImgageToBase64(new File("D:\\photo\\1.jpg")));
	}

}


