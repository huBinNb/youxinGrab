package com.lidehang.data.collection.model.param;

public class GSSiteParams {
	
	//企业ID
	private String companyId;
	//VPN帐号
	private String vpnUser;
	//VPN密码
	private String vpnPwd;
	
	//国税系统帐号
	private String nationalTaxUser;
	//国税系统密码
	private String nationalTaxPwd;
	
	//开始时间
	private String startTimeStr;
	//结束时间
	private String endTimeStr;
	
	
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getVpnUser() {
		return vpnUser;
	}
	public void setVpnUser(String vpnUser) {
		this.vpnUser = vpnUser;
	}
	public String getVpnPwd() {
		return vpnPwd;
	}
	public void setVpnPwd(String vpnPwd) {
		this.vpnPwd = vpnPwd;
	}
	public String getNationalTaxUser() {
		return nationalTaxUser;
	}
	public void setNationalTaxUser(String nationalTaxUser) {
		this.nationalTaxUser = nationalTaxUser;
	}
	public String getNationalTaxPwd() {
		return nationalTaxPwd;
	}
	public void setNationalTaxPwd(String nationalTaxPwd) {
		this.nationalTaxPwd = nationalTaxPwd;
	}
	public String getStartTimeStr() {
		return startTimeStr;
	}
	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}
	public String getEndTimeStr() {
		return endTimeStr;
	}
	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}
	
	
}
