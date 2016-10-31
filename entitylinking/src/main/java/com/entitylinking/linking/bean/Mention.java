package com.entitylinking.linking.bean;

import java.util.List;

/**
 * mention的数据结构
 * @author HP
 *
 */
public class Mention {

	private String mentionName;
	private double tfidfValue;
	private List<String> candidateEntity;
	
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
	public List<String> getCandidateEntity() {
		return candidateEntity;
	}
	public void setCandidateEntity(List<String> candidateEntity) {
		this.candidateEntity = candidateEntity;
	}
	
	
}
