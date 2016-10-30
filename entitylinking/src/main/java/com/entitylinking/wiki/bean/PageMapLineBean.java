package com.entitylinking.wiki.bean;
/**
 * 与数据库表pagemapline对应
 * @author shijing
 *
 */
public class PageMapLineBean {

	private Long id;
	private String name;
	private Long pageID;
	private String stem;
	private String lemma;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getPageID() {
		return pageID;
	}
	public void setPageID(Long pageID) {
		this.pageID = pageID;
	}
	public String getStem() {
		return stem;
	}
	public void setStem(String stem) {
		this.stem = stem;
	}
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	
	
	
}
