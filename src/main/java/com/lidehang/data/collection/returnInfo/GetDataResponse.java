package com.lidehang.data.collection.returnInfo;

import java.util.List;

import org.bson.Document;

public class GetDataResponse extends ApiReturn{

	private List<Document> data;

	public List<Document> getData() {
		return data;
	}

	public void setData(List<Document> data) {
		this.data = data;
	}

	
 
	
	
}
