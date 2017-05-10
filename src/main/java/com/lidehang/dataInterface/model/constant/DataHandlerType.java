package com.lidehang.dataInterface.model.constant;

public enum DataHandlerType {
	company("company","企业"),
	copyRight("copyRight","著作权"),
	patentsDetail("patentsDetail","公司专利详细列表"),
	softwareCr("softwareCr","软件著作权"),
	court("court","失信和被执行人信息"),
	announcement("announcement","法院公告"),
	companyDetail("companyDetail","企业信息精简版"),
	companyDetailMore("companyDetailMore","企业信息详细版"),
	companyRelation("companyRelation","企业宗谱查询"),
	companyException("companyException","企业经营异常信息"),
	countryOrgCode ("countryOrgCode","全国组织代码查询"),
	enterprise("enterprise","企业详情");
	
	
	private String id;
	private String lable;

	public String getId() {
		return id;
	}

	public String getLable() {
		return lable;
	}

	private DataHandlerType(String id, String lable) {
        this.id = id;
        this.lable = lable;
    }
}
