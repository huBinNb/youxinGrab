package com.lidehang.data.collection.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.springframework.stereotype.Service;

import com.lidehang.core.util.MongoUtil;
import com.lidehang.data.collection.dao.CompanyDataDao;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

@Service
public class CompanyDataDaoImpl implements CompanyDataDao {

	@Override
	public void addData(String companyId,String serialNumber, List<Document> documents) {
		Set<String> set = new HashSet<>();		
//		List<Document> existed = getData(companyId);
		List<Document> existed = getData(companyId,serialNumber);
		for(Document doc:existed){
			set.add(doc.getString("sign"));
		}
		List<Document> addList = new ArrayList<>();
		for(Document doc:documents){
			if(!set.contains(doc.getString("sign"))){
				addList.add(doc);
			}
		}
		
		if (addList.size() > 0) {
			MongoCollection<Document> collection = MongoUtil.getDatabase().getCollection("c_" + companyId);
			collection.insertMany(addList);
		}
	}

	@Override
	public List<Document> getData(String companyId, String serialNumber) {
		MongoCollection<Document> collection = MongoUtil.getDatabase().getCollection("c_"+companyId);
		//Filters.eq("serialNumber", serialNumber)
		FindIterable<Document> findIterable = collection.find(Filters.eq("serialNumber", serialNumber));
		List<Document> list = new ArrayList<>();
		MongoCursor<Document> mongoCursor = findIterable.iterator();  
		while(mongoCursor.hasNext()){
			list.add(mongoCursor.next());
		}
		return list;
	}

	@Override
	public List<Document> getDataByType(String companyId, String type) {
		MongoCollection<Document> collection = MongoUtil.getDatabase().getCollection("c_"+companyId);
		//Filters.eq("serialNumber", serialNumber)
		String serialNumber1=null;
		String serialNumber2=null;
		int start=0;
		int end=0;
		//国税：1     地税：2		保理通：3		外汇：4		学籍学历：5
		if("国税".equals(type)){
			serialNumber1="10000";
			serialNumber2="11000";
			start=10001;
			end=10068;
		}else if ("地税".equals(type)) {
			serialNumber1="11000";
			serialNumber2="12000";
			start=11001;
			end=11028;
		}else if ("保理通".equals(type)) {
			serialNumber1="14000";
			serialNumber2="15000";
			start=14001;
			end=14006;
		}else if ("外汇".equals(type)) {
			serialNumber1="15000";
			serialNumber2="16000";
			start=15001;
			end=15005;
		}else if ("学籍学历".equals(type)) {
			serialNumber1="20000";
			serialNumber2="21000";
			start=20001;
			end=20003;
		}
		String serialNumber=null;
		Document document=new Document();
		List<Document> list = new ArrayList<>();
		for (int i = start; i <end; i++) {
			serialNumber=String.valueOf(i);
			FindIterable<Document> findIterable = collection.find(Filters.and(Filters.gt("serialNumber", serialNumber1),Filters.lt("serialNumber", serialNumber2),Filters.eq("serialNumber", serialNumber)));
			//.lt("serialNumber", serialNumber));
			List<Document> list1 = new ArrayList<>();
			MongoCursor<Document> mongoCursor = findIterable.iterator();  
			while(mongoCursor.hasNext()){
				list1.add(mongoCursor.next());
			}
			document.append(serialNumber, list1);
		}
		list.add(document);
		return list;
	}

	@Override
	public void addSinosureData(String userName, String serialNumber, List<Document> documents) {
		Set<String> set = new HashSet<>();		
		List<Document> existed = getData(userName,serialNumber);
		for(Document doc:existed){
			set.add(doc.getString("sign"));
		}
		List<Document> addList = new ArrayList<>();
		for(Document doc:documents){
			if(!set.contains(doc.getString("sign"))){
				addList.add(doc);
			}
		}
		if (addList.size() > 0) {
			MongoCollection<Document> collection = MongoUtil.getDatabase().getCollection("c_" + userName);
			collection.insertMany(addList);
		}
	}
}
