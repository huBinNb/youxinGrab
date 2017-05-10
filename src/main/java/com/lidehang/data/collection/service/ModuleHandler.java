package com.lidehang.data.collection.service;

import com.lidehang.data.collection.constant.SiteStatus;
import com.lidehang.data.collection.exception.SiteLoginFailedException;

public interface ModuleHandler<S extends SiteHandler> {
	
	/**
	 * 抓取此部分数据
	 * @return
	 * @throws SiteLoginFailedException
	 */
	public abstract SiteStatus start(S siteHandler) throws SiteLoginFailedException;
	
}
