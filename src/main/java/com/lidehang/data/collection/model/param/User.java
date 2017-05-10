package com.lidehang.data.collection.model.param;

import org.springframework.data.annotation.Id;

public class User {
	
	@Id
	private String cId;
	
	private String name;
	
	private String mobile;

	public String getcId() {
		return cId;
	}

	public void setcId(String cId) {
		this.cId = cId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
}
