package com.entitylinking.linking;

//import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.apache.commons.lang.ArrayUtils;
//import org.apache.commons.lang.StringUtils;
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
		
		double semanticSimWeight = RELRWParameterBean.getSemanticSimWeight();
		double contextSimWeight = RELRWParameterBean.getContextSimWeight();
		double popularityWeight = RELRWParameterBean.getPopularityWeight();
		
		//获取初始文档偏好向量
		signatureOfDocument = entityGraph.calSignature(entityGraph.getPreferVectorOfDocument());

		for(Mention mention:entityGraph.getMentions()){
			if(mention.getCandidateEntity().size() == 0){//无候选实体
				Entity entity = new Entity();
				entity.setEntityName("nil");
				mentionEntityMap.put(mention, entity);
			}else if (mention.getCandidateEntity().size() == 1) {//候选实体为1
				mentionEntityMap.put(mention, mention.getCandidateEntity().get(0));
			}else {//候选实体为多个
				List<Entity> candidateList = mention.getCandidateEntity();
				int len = candidateList.size();
				int maxScoreIndex = 0;
				double score,maxScore =0,maxSemanticScore=0,maxContextScore=0,maxPopularityScore=0;
				double[] semanticScore = new double[len];
				double[] contextScore = new double[len];
				double[] popularityScore = new double[len];
				for(int i=0;i<len;i++){
					Entity entity = candidateList.get(i);
					entityGraph.setPreferVectorOfEntity(i);
					preferEntityVector = entityGraph.getPreferVectorOfEntity();
					signatureOfEntity = entityGraph.calSignature(preferEntityVector);
//					logger.info("候选实体"+entity.getEntityName()+"的语义签名向量:"+StringUtils.join(Arrays.asList(ArrayUtils.toObject(signatureOfEntity)), "\t"));
//					logger.info("  文档"+text.getTextName()+"的语义签名向量:"+StringUtils.join(Arrays.asList(ArrayUtils.toObject(signatureOfDocument)), "\t"));
					semanticScore[i] = Math.log(1+calSemanticSimilarity(signatureOfEntity, signatureOfDocument));
					if(maxSemanticScore < semanticScore[i]){
						maxSemanticScore = semanticScore[i];
					}
					contextScore[i] = Math.log(1+calLocalSimilarity(mention, candidateList.get(i)));
					if(maxContextScore < contextScore[i]){
						maxContextScore = contextScore[i];
					}
					popularityScore[i] = Math.log(1+calPopularityScore(entity.getPopularity(), mention.getTotalPopularity()));
					if(maxPopularityScore < popularityScore[i]){
						maxPopularityScore = popularityScore[i];
					}
					logger.info(mention.getMentionName()+"的候选实体"+entity.getEntityName()+"的三项得分各为:"
							+semanticScore[i]+"\t"+contextScore[i]+"\t"+popularityScore[i]);
				}
				
				for(int i=0;i<len;i++){
					score = 0;
					score += semanticSimWeight * (semanticScore[i] / maxSemanticScore);
					score += contextSimWeight * (contextScore[i] / maxContextScore);
					score += popularityWeight * (popularityScore[i] / maxPopularityScore);
					
					if(maxScore < score){
						maxScore = score;
						maxScoreIndex = i;
					}
				}
				
				Entity maxScoreEntity = candidateList.get(maxScoreIndex);
				if(maxScore < RELRWParameterBean.getNilThres()){
					maxScoreEntity.setEntityName(RELRWParameterBean.getNil());
				}
				if(!mentionEntityMap.containsKey(mention) ||
						mentionEntityMap.containsKey(mention) &&
						!mentionEntityMap.get(mention).getEntityName()
						.equals(maxScoreEntity.getEntityName())){
						mentionEntityMap.put(mention, maxScoreEntity);
						entityGraph.setDisambiguationMap(mentionEntityMap);
						entityGraph.updatePreferVectorOfDocument(mention, maxScoreEntity);
						signatureOfDocument = entityGraph.calSignature(entityGraph.getPreferVectorOfDocument());
				}
			}
		}
		
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
			if(signatureOfEntity[i] == 0 || signatureOfDocument[i] < 0){
				continue;
			}
			if(signatureOfDocument[i] == 0){
				result += signatureOfEntity[i] * RELRWParameterBean.getGamma();
			}else{
				result += signatureOfEntity[i] * Math.log(signatureOfEntity[i] / signatureOfDocument[i]);
			}
//			logger.info(i+"\t"+result+"\t"+signatureOfEntity[i]+"\t"+signatureOfDocument[i]);
		}
//		logger.info(result);
		if(result > 0){
			result = 1 / result;
		}
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
