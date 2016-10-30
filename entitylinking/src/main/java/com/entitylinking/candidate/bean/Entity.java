package com.entitylinking.candidate.bean;
/**
 * 实体的数据结构
 * @author HP
 *
 */
public class Entity {

	private String entityName;
	private double popularity;
	
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public double getPopularity() {
		return popularity;
	}
	public void setPopularity(double popularity) {
		this.popularity = popularity;
	}
	
	
}
