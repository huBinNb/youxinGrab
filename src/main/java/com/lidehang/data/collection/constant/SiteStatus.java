package com.lidehang.data.collection.constant;

public enum SiteStatus {
	success("success", "执行成功"), fail("fail", "读取失败");

	private String id;
	private String type;

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	private SiteStatus(String id, String type) {
		this.id = id;
		this.type = type;
	}
}
