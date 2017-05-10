package com.lidehang.data.collection.dao;

import java.util.List;

import org.bson.Document;

public interface CompanyDataDao {
	
	public void addData(String companyId,String serialNumber,List<Document> documents);
	
	public List<Document> getData(String companyId,String serialNumber);
	
	public List<Document> getDataByType(String companyId,String type);
	
	public void addSinosureData(String userName,String serialNumber,List<Document> documents);
	
}
