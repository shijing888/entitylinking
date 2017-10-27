package com.entitylinking_dbpedia.linking.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import com.entitylinking_dbpedia.lucene.IndexFile;
import com.entitylinking_dbpedia.task.Main;
import com.entitylinking_dbpedia.linking.bean.Entity;
import com.entitylinking_dbpedia.linking.bean.RELRWParameterBean;
import com.entitylinking_dbpedia.utils.CommonUtils;
import com.entitylinking_dbpedia.utils.NormalizeMention;

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
	/**wiki目标实体*/
	private String wikiObjectEntity;
	/**dbpedia目标实体*/
	private String dbpediaObjectEntity;
	/**yago目标实体*/
	private String yagoObjectEntity;
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

	public String getWikiObjectEntity() {
		return wikiObjectEntity;
	}
	public void setWikiObjectEntity(String wikiObjectEntity) {
		this.wikiObjectEntity = wikiObjectEntity;
	}
	public String getDbpediaObjectEntity() {
		return dbpediaObjectEntity;
	}
	public void setDbpediaObjectEntity(String dbpediaObjectEntity) {
		this.dbpediaObjectEntity = dbpediaObjectEntity;
	}
	public String getYagoObjectEntity() {
		return yagoObjectEntity;
	}
	public void setYagoObjectEntity(String yagoObjectEntity) {
		this.yagoObjectEntity = yagoObjectEntity;
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

	public void setMentionContext(Set<String> mentionContext) {
		this.mentionContext = mentionContext;
	}
	
	/**
	 * 查找mention对应的候选实体
	 * @param mention
	 * @return
	 */
	public List<Entity> obtainCandidate(Map<String, Set<String>> additiveEntityContextDict,
										Map<String, Set<String>> additiveEntityCategoryDict){
		String mentionStr = NormalizeMention.getNormalizeMention(this.mentionName,true);
		Set<String> candidateSet = new HashSet<>();
		List<Entity> entities = new ArrayList<>();
		Document document;
		//先从同义词典中寻找
		document = IndexFile.queryDocument(mentionStr, RELRWParameterBean.getSynonymsDictField1(),
				PathBean.getSynonymsDictPath());
		if(document != null){
			String synonymsValue = document.get(RELRWParameterBean.getSynonymsDictField2());
			candidateSet.add(mentionStr);
			candidateSet.add(synonymsValue);
			
			/*容易引入很多无关候选，如ministry_of_defense_(moldova)，会把moldova的很多项添加进来*/
//			document = IndexFile.queryDocument(synonymsValue, RELRWParameterBean.getAmbiguationDictField1(),
//					PathBean.getAmbiguationDictPath());
//			if(document != null){
//				String[] candidates = document.get(RELRWParameterBean.getAmbiguationDictField2()).split("\t\\|\t");
//				candidateSet.addAll(Arrays.asList(candidates));
//			}
		}
		//再从歧义词典中寻找
		document = IndexFile.queryDocument(mentionStr, RELRWParameterBean.getAmbiguationDictField1(),
				PathBean.getAmbiguationDictPath());
		if(document != null){
			String[] candidates = document.get(RELRWParameterBean.getAmbiguationDictField2()).split("\t\\|\t");
			candidateSet.addAll(Arrays.asList(candidates));
		}
		
		//再通过Lucene寻找
		candidateSet.addAll(IndexFile.queryCandidateLabel(mentionStr, RELRWParameterBean.getDbpediaLabelField(),
				PathBean.getDbpediaLabelNamePath()));
		
		logger.info("candidateSet size:"+candidateSet.size());
		logger.info(mentionStr+"的candidateSet:"+ StringUtils.join(candidateSet, "\t"));
		
		//candidateEntitySet用于消除重复实体
		Set<String> candidateEntitySet = new HashSet<>();
		String normEntityName;
		
		for(String entityStr:candidateSet){
			if(entityStr.contains(disambiguationStr)){
				continue;
			}
			
			normEntityName = NormalizeMention.getNormalizeMention(entityStr, true);
			if(normEntityName == null || candidateEntitySet.contains(normEntityName)){
				continue;
			}
			
			//通过dbpedia标签文件判断是否为实体
			document = IndexFile.queryDocument(normEntityName, RELRWParameterBean.getDbpediaLabelField(),
					PathBean.getDbpediaLabelNamePath());
			if(document == null){
				continue;
			}
			
			try {
				//获取实体名称、流行度及上下文信息
				Entity entity = new Entity();
				entity = entity.getEntityPageInfo(normEntityName,additiveEntityContextDict,additiveEntityCategoryDict);
				if(entity == null){
					continue;
				}
				entity.setScore(CommonUtils.commonWords(this, entity));
//				logger.info("entity title:"+normEntityName);
				candidateEntitySet.add(normEntityName);
				entities.add(entity);
			} catch (Exception e) {
				// TODO: handle exception
				logger.info(normEntityName+" error!");
				continue;
			}
			
		}
		
		List<Entity> entities2 = new ArrayList<>(entities);
		List<Entity> entities4 = new ArrayList<>(entities);
		List<Entity> entities3 = new ArrayList<>();
		//对候选实体按上下文相似性进行降序
		CommonUtils.sortListByContextSimliarity(entities, true);
		//对候选实体按流行度进行降序
		CommonUtils.sortListByPopularity(entities2, true);
		//对候选实体按字面量相似性排序
		CommonUtils.sortListByEditDistance(mentionStr, entities4, false);
		//对候选按照流行度和上下文相似性进行剪枝
		int index = RELRWParameterBean.getCandidateEntityNumThresh();
		Set<String> tempSet = new HashSet<>();
		int i=0;
		while(entities3.size() < index){
			if(i < entities.size()){
				//按流行度添加候选
				if(!tempSet.contains(entities2.get(i).getEntityName())){
					entities3.add(entities2.get(i));
					tempSet.add(entities2.get(i).getEntityName());
				}
				//按字面量相似性添加候选
				if(!tempSet.contains(entities4.get(i).getEntityName())){
					entities3.add(entities4.get(i));
					tempSet.add(entities4.get(i).getEntityName());
				}
				//按上下文相似性添加候选
				if(entities.get(i).getScore() > 0 && !tempSet.contains(entities.get(i).getEntityName())){
					entities3.add(entities.get(i));
					tempSet.add(entities.get(i).getEntityName());
				}
				i++;
			}else{
				break;
			}
		}
		
		//从训练集中获得
		if(!tempSet.contains(this.dbpediaObjectEntity)){
			//获取实体名称、流行度及上下文信息
			Entity entity = new Entity();
			entity = entity.getEntityPageInfo(this.dbpediaObjectEntity,additiveEntityContextDict,
					additiveEntityCategoryDict);
			
			if(entity != null){
				entity.setScore(CommonUtils.commonWords(this, entity));
				entities3.add(entity);
			}
			
		}
		
		//统计候选覆盖率
//		logger.info(this.mentionName+"的目标实体为："+this.dbpediaObjectEntity);
//		if(!this.dbpediaObjectEntity.equals("nil")){
//			if(tempSet.contains(this.dbpediaObjectEntity)){
//				Main.containEntityCounts++;
//			}
//			Main.mentionCounts++;
//		}
			
		
		
		logger.info(mentionStr+"的candidateList size:"+entities3.size());		
		for(Entity entity:entities3){
			logger.info(mentionStr+"的candidate:"+ entity.getEntityName());
		}
	
		return entities3;
	}
	
}