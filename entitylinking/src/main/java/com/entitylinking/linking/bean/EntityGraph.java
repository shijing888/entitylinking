package com.entitylinking.linking.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.entitylinking.utils.NLPUtils;

/**
 * 一篇文档生成一个实体子图
 * @author HP
 *
 */
public class EntityGraph {

	/*实体list的size*/
	private int entityLen;/////////////////////////////
	/*该图中所有的实体*/
	private List<Entity> entities;/////////////////////////////
	/*该图中所有的mention*/
	private List<Mention> mentions;/////////////////////////////
	/*用于记录每个mention对应的实体在entities中的起始位置，特殊mention(OTHER)标注的是扩展实体的起始位置*/
	private Map<String, Integer> entityIndex;////////////////////
	/*mention-候选实体map*/
	private Map<Mention, List<String>> entityMap ;
	/*文档的语义签名向量*/
	private double[] semantitcSignatureOfDocument;
	/*实体的语义签名向量*/
	private double[] semantitcSignatureOfEntity;
	/*文档的偏好向量*/
	private double[] preferVectorOfDocument;
	/*实体的偏好向量*/
	private double[] preferVectorOfEntity;
	/*实体间的转移矩阵*/
	private double[][] transferMatrix;
	/*实体图中边的权重*/
	private double[][] edgesWeights;
	/*实体图对象*/
	private EntityGraph entityGraph;
	
	public EntityGraph getEntityGraph() {
		return entityGraph;
	}

	public void setEntityGraph(EntityGraph entityGraph) {
		this.entityGraph = entityGraph;
	}

	public Map<String, Integer> getEntityIndex() {
		return entityIndex;
	}

	public List<Mention> getMentions() {
		return mentions;
	}

	public void setMentions(List<Mention> mentions) {
		this.mentions = mentions;
	}

	public int getEntityLen() {
		return entityLen;
	}

	public void setEntityLen(int len) {
		this.entityLen = len;
	}

	public void setEntityIndex(Map<String, Integer> entityIndex) {
		this.entityIndex = entityIndex;
	}

	public Map<Mention, List<String>> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<Mention, List<String>> entityMap) {
		this.entityMap = entityMap;
	}

	public double[] getSemantitcSignatureOfDocument() {
		return semantitcSignatureOfDocument;
	}

	public void setSemantitcSignatureOfDocument(double[] semantitcSignatureOfDocument) {
		this.semantitcSignatureOfDocument = semantitcSignatureOfDocument;
	}

	public double[] getSemantitcSignatureOfEntity() {
		return semantitcSignatureOfEntity;
	}

	public void setSemantitcSignatureOfEntity(double[] semantitcSignatureOfEntity) {
		this.semantitcSignatureOfEntity = semantitcSignatureOfEntity;
	}

	public double[] getPreferVectorOfDocument() {
		return preferVectorOfDocument;
	}

	public void setPreferVectorOfDocument(double[] preferVectorOfDocument) {
		this.preferVectorOfDocument = preferVectorOfDocument;
	}

	public double[] getPreferVectorOfEntity() {
		return preferVectorOfEntity;
	}

	public void setPreferVectorOfEntity(double[] preferVectorOfEntity) {
		this.preferVectorOfEntity = preferVectorOfEntity;
	}

	public double[][] getEdgesWeights() {
		return edgesWeights;
	}

	public void setEdgesWeights(double[][] edgesWeights) {
		this.edgesWeights = edgesWeights;
	}

	public List<Entity> getEntities() {
		return entities;
	}
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
	public double[][] getTransferMatrix() {
		return transferMatrix;
	}
	public void setTransferMatrix(double[][] transferMatrix) {
		this.transferMatrix = transferMatrix;
	}
	
}
