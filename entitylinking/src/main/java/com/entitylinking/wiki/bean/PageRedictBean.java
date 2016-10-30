package com.entitylinking.wiki.bean;

/**
 * 重定向词条对，用于存储hql查询结果
 * @author shijing
 *
 */
public class PageRedictBean {

	private String source;
	private String dest;
	
	public PageRedictBean(String source, String dest){
		this.source = source;
		this.dest = dest;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	
	
	
}
