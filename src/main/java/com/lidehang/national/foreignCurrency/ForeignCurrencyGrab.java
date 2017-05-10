package com.lidehang.national.foreignCurrency;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.lidehang.national.util.MD5Util;
import com.lidehang.national.util.StringUtils;
import com.lidehang.national.util.TaxConstants;

public class ForeignCurrencyGrab {
	/**
	 * @param ins
	 *            通过输入流在本地生成验证码图片
	 */
	public static void createImgCode(InputStream ins) {
		File file = new File("D:\\ForCurCode\\imgCode.jpg");
		try {
			FileOutputStream fileout = new FileOutputStream(file);
			/**
			 * 根据实际运行效果 设置缓冲区大小
			 */
			byte[] buffer = new byte[1024];
			int ch = 0;
			while ((ch = ins.read(buffer)) != -1) {
				fileout.write(buffer, 0, ch);
			}
			ins.close();
			fileout.flush();
			fileout.close();
			//System.out.println("验证码写到本地！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
