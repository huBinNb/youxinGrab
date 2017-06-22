package com.lidehang.national.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * SHA-256加密
 * @author Hobn
 *
 */
public class SHA256Util {
		public static String SHA_256(String str){
	           String s=Encrypt(str,"SHA-256");
	            return s;
	        }
	        public static String Encrypt(String strSrc, String encName) {
	            MessageDigest md = null;
	            String strDes = null;
	            byte[] bt = strSrc.getBytes();
	            try {
	                md = MessageDigest.getInstance(encName);
	                md.update(bt);
	                strDes = bytes2Hex(md.digest()); // to HexString
	            } catch (NoSuchAlgorithmException e) {
	                System.out.println("签名失败！");
	                return null;
	            }
	            return strDes;
	        }
	        public static String bytes2Hex(byte[] bts) {
	            String des = "";
	            String tmp = null;
	            for (int i = 0; i < bts.length; i++) {
	                tmp = (Integer.toHexString(bts[i] & 0xFF));
	                if (tmp.length() == 1) {
	                    des += "0";
	                }
	                des += tmp;
	            }
	            return des;
	        }
	public static void main(String[] args) {
		System.out.println(SHA_256("111111{320200570369184}"));
	//	4e1cd749a3d0fe6bbb5467172ab292e5104572f9114c2e1a70463f066f7c0abf
	//	4e1cd749a3d0fe6bbb5467172ab292e5104572f9114c2e1a70463f066f7c0abf
		
	}
}
