package com.entitylinking.linking.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.entitylinking.utils.CommonUtils;
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
	 * 查找mention对应的候选实体
	 * @param mention
	 * @return
	 */
	public List<Entity> obtainCandidate(DictBean dictBean){
		String mentionStr = NormalizeMention.getNormalizeMention(this.mentionName,true);
		Set<String> candidateSet = new HashSet<>();
		List<Entity> entities = new ArrayList<>();
		//先从同义词典中寻找
		if(dictBean.getSynonymsDict().containsKey(mentionStr)){
			candidateSet.add(dictBean.getSynonymsDict().get(mentionStr));
		}
		//再从歧义词典中寻找
		if(dictBean.getAmbiguationDict().containsKey(mentionStr)){
			candidateSet.addAll(dictBean.getAmbiguationDict().get(mentionStr));
		}
		if(dictBean.getAmbiguationDict().containsKey(dictBean.getSynonymsDict().get(mentionStr))){
			candidateSet.addAll(dictBean.getAmbiguationDict().get(dictBean.getSynonymsDict().get(mentionStr)));
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
		logger.info(mentionStr+"的candidateSet:"+ StringUtils.join(candidateSet, "\t"));
		
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
			//获取实体名称、流行度及上下文信息
			entity.getEntityPageInfo(normEntityName);
			entity.setScore(CommonUtils.commonWords(this, entity));
			logger.info("entity title:"+normEntityName);
			candidateEntitySet.add(normEntityName);
			entities.add(entity);
		}
		
		List<Entity> entities2 = new ArrayList<>(entities);
		List<Entity> entities3 = new ArrayList<>();
		//对候选实体按流行度进行降序
		CommonUtils.sortListByPopularity(entities, true);
		CommonUtils.sortListByContextSimliarity(entities2, true);
		//对候选按照流行度和上下文相似性进行剪枝
		for(int i=0;i<RELRWParameterBean.getCandidateEntityNumThresh();i++){
			if(i < entities.size()){
				if(entities2.get(i).getScore() > 0){
					entities3.add(entities2.get(i));
				}
				entities3.add(entities.get(i));
			}
			
		}
		
		logger.info(mentionStr+"的candidateList size:"+entities3.size());		
		for(Entity entity:entities3){
			logger.info(mentionStr+"的candidate:"+ entity.getEntityName());
		}
	
		return entities3;
	}
	
}
