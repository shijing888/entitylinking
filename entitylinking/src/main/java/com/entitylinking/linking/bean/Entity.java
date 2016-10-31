package com.entitylinking.linking.bean;

import java.util.List;

/**
 * 实体的数据结构
 * @author HP
 *
 */
public class Entity {

	/**实体名称*/
	private String entityName;
	/**实体流行度*/
	private double popularity;
	/**实体的语义签名*/
	private List<Double> semanticSignature;
	
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
	public List<Double> getSemanticSignature() {
		return semanticSignature;
	}
	public void setSemanticSignature(List<Double> semanticSignature) {
		this.semanticSignature = semanticSignature;
	}
	
	
}
