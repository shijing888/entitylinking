package com.entitylinking.linking.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.entitylinking.utils.NormalizeMention;

import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

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
	
	/**
	 * 获取mention的候选实体
	 * @param mention
	 * @return
	 */
	public List<Entity> candidatesOfMention(String mention){
		mention = NormalizeMention.getNormalizeMention(mention,true);
		List<Entity> candidateList = obtainCandidate(mention);
		logger.info(mention+" candidates size:"+ candidateList.size());
		logger.info(mention+" candidates are:"+ StringUtils.join(candidateList, "\t"));
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
		if(DictBean.getSynonymsDict().containsKey(mention))
			candidateSet.add(DictBean.getSynonymsDict().get(mention));
		//再从歧义词典中寻找
		if(DictBean.getAmbiguationDict().containsKey(mention))
			candidateSet.addAll(DictBean.getAmbiguationDict().get(mention));
		//若候选本身为歧义，则将其歧义项加进来
		
		for(String candidate : new ArrayList<>(candidateSet)){
			if(DictBean.getAmbiguationDict().containsKey(candidate)){
				candidateSet.addAll(DictBean.getAmbiguationDict().get(candidate));
				if(candidate.contains(disambiguationStr))
					candidateSet.remove(candidate);
			}
		}
		for(String entityStr:candidateSet){
			Entity entity = new Entity();
			try {
				entity.getEntityPageInfo(entityStr);
			} catch (WikiApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//为每个实体计算流行度及上下文
			entities.add(entity);
		}
		//对候选实体按照流行度降序排序
		Collections.sort(entities, new Comparator<Entity>() {
			//a>b升序，b>a降序
			@Override
			public int compare(Entity entity1, Entity entity2) {
				// TODO Auto-generated method stub
				return entity2.getPopularity() - entity1.getPopularity()>0?1:-1;
			}
			
		});
		
		return entities;
	}
}
