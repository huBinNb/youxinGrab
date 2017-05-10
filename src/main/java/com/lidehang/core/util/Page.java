package com.lidehang.core.util;

import java.util.List;

public class Page<E> {
    // 结果集
    private List<E> list;

    // 查询记录总数
    private int totalPage;

    // 每页多少条记录
    private int pageSize;

    // 第几页
    private int currentPage;
    
    
    public Page(int currentPage,int pageSize){
    	this.currentPage = currentPage;
    	this.pageSize = pageSize;
    }
    /**
     * @return 总页数
     * */
    public void setTotalRecords(int totalRecords){
    	totalPage = (totalRecords+pageSize-1)/pageSize;
    }
    
    /**
     * 计算当前页开始记录
     * @param pageSize 每页记录数
     * @param currentPage 当前第几页
     * @return 当前页开始记录号
     */
    public int countOffset(){
        return pageSize*(currentPage-1);
    }

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	
    
}