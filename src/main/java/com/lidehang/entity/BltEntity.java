package com.lidehang.entity;

/**
 * @author hobn
 * @date 2017年10月30日 下午2:11:36
 */
public class BltEntity {
	
	private String username;
	private String password;
	private String logincode;
	private String pathStr;
	private String key;
	private String type;
	private String url;
	public BltEntity() {
		// TODO Auto-generated constructor stub
	}

	
	
	
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}





	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}





	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}





	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}





	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the logincode
	 */
	public String getLogincode() {
		return logincode;
	}

	/**
	 * @param logincode the logincode to set
	 */
	public void setLogincode(String logincode) {
		this.logincode = logincode;
	}

	/**
	 * @return the pathStr
	 */
	public String getPathStr() {
		return pathStr;
	}

	/**
	 * @param pathStr the pathStr to set
	 */
	public void setPathStr(String pathStr) {
		this.pathStr = pathStr;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}





	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BltEntity [username=" + username + ", password=" + password + ", logincode=" + logincode + ", pathStr="
				+ pathStr + ", key=" + key + ", type=" + type + ", url=" + url + "]";
	}

	
	
	
	
	
}
