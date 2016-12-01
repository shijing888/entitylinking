package com.entitylinking.linking;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.entitylinking.linking.bean.Entity;
import com.entitylinking.linking.bean.EntityGraph;
import com.entitylinking.linking.bean.Mention;
import com.entitylinking.linking.bean.RELRWParameterBean;
import com.entitylinking.linking.bean.Text;
import com.entitylinking.utils.CommonUtils;

/**
 * 实体链接到知识库的操作
 * @author HP
 *
 */
public class LinkingKB {
	static Logger logger = Logger.getLogger(LinkingKB.class);
	/**
	 * 链接知识库过程，
	 * @param text
	 * @return，返回mention-entity对
	 */
	public void obtainmentionEntityPairs(Text text){
		EntityGraph entityGraph = text.getEntityGraph();
		Map<Mention, Entity> mentionEntityMap = entityGraph.getDisambiguationMap();
		double[] signatureOfDocument;
		double[] signatureOfEntity;
		double[] preferEntityVector = null;
		//获取初始文档偏好向量
		signatureOfDocument = entityGraph.calSignature(entityGraph.getPreferVectorOfDocument());
		//用于保存实体的score
//		Map<Entity, Double> entityScoreMap = new HashMap<Entity, Double>();
		for(Mention mention:entityGraph.getMentions()){
			if(mention.getCandidateEntity().size() == 0){//无候选实体
				mentionEntityMap.put(mention, null);
			}else if (mention.getCandidateEntity().size() == 1) {//候选实体为1
				mentionEntityMap.put(mention, mention.getCandidateEntity().get(0));
			}else {//候选实体为多个
				List<Entity> candidateList = mention.getCandidateEntity();
				double score;
//				entityScoreMap.clear();
				for(int i=0;i<candidateList.size();i++){
					score = 0;
					Entity entity = candidateList.get(i);
					entityGraph.setPreferVectorOfEntity(i);
					preferEntityVector = entityGraph.getPreferVectorOfEntity();
					signatureOfEntity = entityGraph.calSignature(preferEntityVector);
					logger.info("候选实体"+entity.getEntityName()+"的语义签名向量:"+StringUtils.join(Arrays.asList(ArrayUtils.toObject(signatureOfEntity)), "\t"));
					logger.info("  文档"+entity.getEntityName()+"的语义签名向量:"+StringUtils.join(Arrays.asList(ArrayUtils.toObject(signatureOfDocument)), "\t"));
					logger.info(mention.getMentionName()+"的候选实体"+entity.getEntityName()+"与文档的语义相似度为:");
					score = Math.log(1+calSemanticSimilarity(signatureOfEntity, signatureOfDocument));
					logger.info(mention.getMentionName()+"与候选实体"+entity.getEntityName()+"的局部相容性为:");
					score += Math.log(1+calLocalSimilarity(mention, candidateList.get(i)));
					logger.info(mention.getMentionName()+"的候选实体"+entity.getEntityName()+"的流行度得分为:");
					score += Math.log(1+calPopularityScore(entity.getPopularity(), mention.getTotalPopularity()));
					logger.info(mention.getMentionName()+"的候选实体"+entity.getEntityName()+"的总得分为:"+score);
					entity.setScore(score);
				}
				Entity maxScoreEntity = maxScore(candidateList);
				if(!mentionEntityMap.containsKey(mention) ||
						mentionEntityMap.containsKey(mention) &&
						!mentionEntityMap.get(mention).getEntityName()
						.equals(maxScoreEntity.getEntityName())){
						mentionEntityMap.put(mention, maxScoreEntity);
						entityGraph.setDisambiguationMap(mentionEntityMap);
						entityGraph.setPreferVectorOfDocument(false);
						signatureOfDocument = entityGraph.calSignature(entityGraph.getPreferVectorOfDocument());
				}
			}
		}
		
	}
	
	/**
	 * 计算数组中最大值对应的下标
	 * @param arrays
	 * @return
	 */
	public Entity maxScore(List<Entity> entityList){
		double max = 0;
		Entity maxEntity = null;
		for(Entity entity:entityList){
			if(entity.getScore() > max){
				max = entity.getScore();
				maxEntity = entity;
			}
		}
		return maxEntity;
	}
	/**
	 * 计算语义相似度
	 * @param signatureOfEntity
	 * @param signatureOfDocument
	 * @return
	 */
	public double calSemanticSimilarity(double[] signatureOfEntity, double[]signatureOfDocument){
		double result = 0;
		for(int i=0;i<signatureOfEntity.length;i++){
			if(signatureOfDocument[i] == 0){
				result += signatureOfEntity[i] * RELRWParameterBean.getGamma();
			}else{
				result += signatureOfEntity[i] * Math.log(signatureOfEntity[i] / signatureOfDocument[i]);
			}
		}
		result = 1 / result;
		logger.info(result);
		return result;
	}
	
	/**
	 * 计算mentio与候选实体的局部相容性，此处用上下文相似性来度量
	 * @param mention
	 * @param entity
	 * @return
	 */
	public double calLocalSimilarity(Mention mention, Entity entity){
		Set<String> mentionContext = mention.getMentionContext();
		int commonCount = CommonUtils.commonWords(mention, entity);
		int allCount = mentionContext.size();
		if(commonCount < allCount){
			logger.info((double)commonCount / allCount);
			return (double)commonCount / allCount;
		}else{
			logger.info(1);
			return 1;
		}
		
	}
	
	/**
	 * 计算流行度得分
	 * @param popularity
	 * @param totalPopularity
	 * @return
	 */
	public double calPopularityScore(double popularity, double totalPopularity){
		logger.info(popularity / totalPopularity);
		return popularity / totalPopularity;
	}
	
	@Test
	public void test(){
		double[] dd = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,0.10,0.11,0.12,0.13,0.14,0.15,0.16,0.17,0.18};
		double[] ee = {0.18,0.17,0.16,0.15,0.14,0.13,0.12,0.11,0.10,0.9,0.8,0.7,0.6,0.5,0.4,0.3,0.2,0.1};
		System.out.println(calSemanticSimilarity(dd, ee));
	}
}
