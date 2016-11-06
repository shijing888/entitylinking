package com.entitylinking.linking.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * mention的数据结构
 * @author HP
 *
 */
public class Mention {

	/**mention名称*/
	private String mentionName;
	/**mention在文档集中的tfidf值*/
	private double tfidfValue;
	/**mention的候选实体集*/
	private List<Entity> candidateEntity;
	/**mention在文档中出现的位置记录*/
	private List<Integer> mentionIndex;
	
	public Mention(String mention){
		this.mentionName = mention;
		candidateEntity = new ArrayList<Entity>();
		mentionIndex = new ArrayList<>();
	}
	
	public List<Integer> getMentionIndex() {
		return mentionIndex;
	}

	public void setMentionIndex(List<Integer> mentionIndex) {
		this.mentionIndex = mentionIndex;
	}

	public String getMentionName() {
		return mentionName;
	}
	public void setMentionName(String mentionName) {
		this.mentionName = mentionName;
	}
	public double getTfidfValue() {
		return tfidfValue;
	}
	public void setTfidfValue(double tfidfValue) {
		this.tfidfValue = tfidfValue;
	}

	public List<Entity> getCandidateEntity() {
		return candidateEntity;
	}

	public void setCandidateEntity(List<Entity> candidateEntity) {
		this.candidateEntity = candidateEntity;
	}
	
	
	
	
}
