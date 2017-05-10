package com.lidehang.core.util;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoUtil {
	public static MongoDatabase getDatabase() {
		return ((MongoClient) SpringUtil.getBean(MongoClient.class)).getDatabase("core");
	}
}
