package com.entitylinking_dbpedia.dbpedia.preprocess;

import java.util.Set;

public class EdgeType {

	private int TypeNumber;
	private int inEdgeNumber;
	private int outEdgeNumber;
	private Set<Integer> inEdgeEntity;
	private Set<Integer> outEdgeEntity;
	public int getTypeNumber() {
		return TypeNumber;
	}
	public void setTypeNumber(int typeNumber) {
		TypeNumber = typeNumber;
	}
	public int getInEdgeNumber() {
		return inEdgeNumber;
	}
	public void setInEdgeNumber(int inEdgeNumber) {
		this.inEdgeNumber = inEdgeNumber;
	}
	public int getOutEdgeNumber() {
		return outEdgeNumber;
	}
	public void setOutEdgeNumber(int outEdgeNumber) {
		this.outEdgeNumber = outEdgeNumber;
	}
	public Set<Integer> getInEdgeEntity() {
		return inEdgeEntity;
	}
	public void setInEdgeEntity(Set<Integer> inEdgeEntity) {
		this.inEdgeEntity = inEdgeEntity;
	}
	public Set<Integer> getOutEdgeEntity() {
		return outEdgeEntity;
	}
	public void setOutEdgeEntity(Set<Integer> outEdgeEntity) {
		this.outEdgeEntity = outEdgeEntity;
	}
	
	
}
