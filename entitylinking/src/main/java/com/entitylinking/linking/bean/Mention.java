package com.entitylinking.linking.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	/**mention的上下文*/
	private Set<String> mentionContext;
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

	public Set<String> getMentionContext() {
		return mentionContext;
	}

	/**
	 * 构造mention的上下文集合
	 * @param textContext，上下文map
	 * @param textContextIndex，上下文词对应的index
	 */
	public void initMentionContext(Map<Integer, String> textContext, List<Integer> textContextIndex){
		mentionContext = new HashSet<>();
		int size = RELRWParameterBean.getContextWindow();
		int i = 0;
		int mentionIndex = 0;
		while(i < size){
			//对文中出现mention的所有地方收集上下文
			for(Integer index:this.mentionIndex){
				if(textContextIndex.contains(index)){
					//找到mention在集合中所处的位置
					mentionIndex = textContextIndex.indexOf(index);
					//mention上文
					if(mentionIndex >= i){
						mentionContext.add(textContext.get(textContextIndex.get(mentionIndex - i)));
					}
					//mention下文
					if(mentionIndex + i < textContextIndex.size()){
						mentionContext.add(textContext.get(textContextIndex.get(mentionIndex + i)));
					}
					i++;
				}
				
			}
		}
	}
}
