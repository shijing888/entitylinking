package com.entitylinking.linking.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.entitylinking.utils.NormalizeMention;

/**
 * mention的数据结构
 * @author HP
 *
 */
public class Mention {
	static Logger logger = Logger.getLogger(Mention.class);
	private static String disambiguationStr = "(disambiguation)";
	/**mention名称*/
	private String mentionName;
	/**mention在文档集中的tfidf值*/
	private double tfidfValue;
	/**目标实体*/
	private String objectEntity;
	/**mention的候选实体集*/
	private List<Entity> candidateEntity;
	/**mention首次出现的位置*/
	private int mentionOffset;
	/**mention在文中出现的次数*/
	private int occurCounts;
	/**候选实体流行度总和*/
	private double totalPopularity;
	/**mention的上下文*/
	private Set<String> mentionContext;
	public Mention(String mention){
		this.mentionName = mention;
		mentionContext = new HashSet<>();
		
	}
	public double getTotalPopularity() {
		return totalPopularity;
	}
	public void setTotalPopularity() {
		double count = 0;
		for(Entity entity:candidateEntity){
			count += entity.getPopularity();
		}
		this.totalPopularity = count;
	}

	public String getMentionName() {
		return mentionName;
	}
	public int getMentionOffset() {
		return mentionOffset;
	}

	public void setMentionOffset(int mentionOffset) {
		this.mentionOffset = mentionOffset;
	}

	public int getOccurCounts() {
		return occurCounts;
	}

	public void setOccurCounts(int occurCounts) {
		this.occurCounts = occurCounts;
	}

	public void setMentionName(String mentionName) {
		this.mentionName = mentionName;
	}
	public String getObjectEntity() {
		return objectEntity;
	}

	public void setObjectEntity(String objectEntity) {
		this.objectEntity = objectEntity;
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
	 * 获取mention的候选实体
	 * @param mention
	 * @return
	 */
	public List<Entity> candidatesOfMention(String mention){
		mention = NormalizeMention.getNormalizeMention(mention,true);
		List<Entity> candidateList = obtainCandidate(mention);
		return candidateList;
	}
	
	/**
	 * 查找mention对应的候选实体
	 * @param mention
	 * @return
	 */
	public List<Entity> obtainCandidate(String mention){
		Set<String> candidateSet = new HashSet<>();
		List<Entity> entities = new ArrayList<>();
		//先从同义词典中寻找
		if(DictBean.getSynonymsDict().containsKey(mention)){
			candidateSet.add(DictBean.getSynonymsDict().get(mention));
		}
		//再从歧义词典中寻找
		if(DictBean.getAmbiguationDict().containsKey(mention)){
			candidateSet.addAll(DictBean.getAmbiguationDict().get(mention));
		}
		if(DictBean.getAmbiguationDict().containsKey(DictBean.getSynonymsDict().get(mention))){
			candidateSet.addAll(DictBean.getAmbiguationDict().get(DictBean.getSynonymsDict().get(mention)));
		}
		
		//若候选本身为歧义，则将其歧义项加进来
//		Queue<String> seeds = new LinkedList<String>(candidateSet);
//		String candidate;
//		while(!seeds.isEmpty()){
//			candidate = seeds.poll();
////			logger.info("candidate:"+candidate);
//			if(DictBean.getAmbiguationDict().containsKey(candidate)){
////				logger.info("add candidates:"+ StringUtils.join(DictBean.getAmbiguationDict().get(candidate),"\t"));
//				candidateSet.addAll(DictBean.getAmbiguationDict().get(candidate));
//				candidateSet.remove(candidate);
////				seeds.addAll(DictBean.getAmbiguationDict().get(candidate));
//			}
//		}
		
		logger.info("candidateSet size:"+candidateSet.size());
		logger.info(mention+"的candidateSet:"+ StringUtils.join(candidateSet, "\t"));
		
		//candidateEntitySet用于消除重复实体
		Set<String> candidateEntitySet = new HashSet<>();
		String normEntityName;
		
		for(String entityStr:candidateSet){
			if(entityStr.contains(disambiguationStr)){
				continue;
			}
			Entity entity = new Entity();
			normEntityName = entity.getEntityName(entityStr);
			if(normEntityName == null || candidateEntitySet.contains(normEntityName)){
				continue;
			}
			double entityPopularity = entity.getEntityPopularity(normEntityName);
			if(entityPopularity < RELRWParameterBean.getPopularityThresh()){
				continue;
			}
			logger.info("entity title:"+normEntityName);
			candidateEntitySet.add(normEntityName);
			entity.setEntityName(normEntityName);
			entity.setPopularity(entityPopularity);
			entities.add(entity);
		}
		
		//对候选实体按照流行度降序排序，并只选取满足阈值数目的候选实体集合
		Collections.sort(entities, new Comparator<Entity>() {
			//a>b升序，b>a降序
			@Override
			public int compare(Entity entity1, Entity entity2) {
				// TODO Auto-generated method stub
				return (int)(entity2.getPopularity() - entity1.getPopularity());
			}
		});
		//只选择满足阈值数目的候选实体
		if(entities.size() > RELRWParameterBean.getCandidateEntityNumThresh()){
			entities = entities.subList(0, RELRWParameterBean.getCandidateEntityNumThresh());
		}
		for(Entity entity:entities){
			entity.getEntityPageInfo(entity.getEntityName());
		}
		logger.info(mention+"的candidateList size:"+entities.size());		
		for(Entity entity:entities){
			logger.info(mention+"的candidate:"+ entity.getEntityName());
		}
	
		return entities;
	}
}
