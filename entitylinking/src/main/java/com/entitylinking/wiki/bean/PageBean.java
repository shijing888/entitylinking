package com.entitylinking.wiki.bean;
/**
 * 与数据库表page对应
 * @author shijing
 *
 */
public class PageBean {

	private Long id;
	private Long pageId;
	private String name;
	private String text;
	private int isDisambiguation;
	private int inDgree;
	
	public PageBean(String name, String text){
		this.name = name;
		this.text = text;
	}
	
	public PageBean(String name){
		this.name = name;
	}
	
	public PageBean(String name, String text, int inDgree){
		this.name = name;
		this.text = text;
		this.inDgree = inDgree;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPageId() {
		return pageId;
	}
	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getIsDisambiguation() {
		return isDisambiguation;
	}
	public void setIsDisambiguation(int isDisambiguation) {
		this.isDisambiguation = isDisambiguation;
	}
	public int getInDgree() {
		return inDgree;
	}

	public void setInDgree(int inDgree) {
		this.inDgree = inDgree;
	}
	
	
}
