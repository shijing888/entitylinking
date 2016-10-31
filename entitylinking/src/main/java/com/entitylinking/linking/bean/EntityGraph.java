package com.entitylinking.linking.bean;

import java.util.List;
import java.util.Map;

import de.fau.cs.osr.ptk.common.EntityMap;

/**
 * 实体子图
 * @author HP
 *
 */
public class EntityGraph {

	private List<Entity> entities;
	private Map<String, List<String>> entityMap ;
	private Map<String, Mention> mentionMap;
	private Map<String, Double> entityEdges;
	private List<Double> semantitcSignature;
	private double[][] transferMatrix;
	
	public EntityGraph(Map<String, List<String>> entityMap){
		this.entityMap = entityMap;
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	public Map<String, List<String>> getEntityMap() {
		return entityMap;
	}
	public void setEntityMap(Map<String, List<String>> entityMap) {
		this.entityMap = entityMap;
	}
	public Map<String, Double> getEntityEdges() {
		return entityEdges;
	}
	public void setEntityEdges(Map<String, Double> entityEdges) {
		this.entityEdges = entityEdges;
	}
	public List<Double> getSemantitcSignature() {
		return semantitcSignature;
	}
	public void setSemantitcSignature(List<Double> semantitcSignature) {
		this.semantitcSignature = semantitcSignature;
	}
	public Map<String, Mention> getMentionMap() {
		return mentionMap;
	}
	public void setMentionMap(Map<String, Mention> mentionMap) {
		this.mentionMap = mentionMap;
	}
	public double[][] getTransferMatrix() {
		return transferMatrix;
	}
	public void setTransferMatrix(double[][] transferMatrix) {
		this.transferMatrix = transferMatrix;
	}
	
	/**
	 * 构造关于该篇文档的密度子图
	 * @return
	 */
	public EntityGraph generateDensityGraph(Text text){
		return this;
	}
}
