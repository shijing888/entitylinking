package com.entitylinking_dbpedia.linking;

//import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.apache.commons.lang.ArrayUtils;
//import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.entitylinking_dbpedia.linking.bean.Entity;
import com.entitylinking_dbpedia.linking.bean.EntityGraph;
import com.entitylinking_dbpedia.linking.bean.Mention;
import com.entitylinking_dbpedia.linking.bean.RELRWParameterBean;
import com.entitylinking_dbpedia.linking.bean.Text;
import com.entitylinking_dbpedia.utils.CommonUtils;
import com.entitylinking_dbpedia.utils.EditDistance;

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
		double literalSimWeight = RELRWParameterBean.getLiteralSimWeight();
		
		for(Mention mention:entityGraph.getMentions()){
			if(mention.getCandidateEntity().size() == 0){//无候选实体
				Entity entity = new Entity();
				entity.setEntityName(RELRWParameterBean.getNil());
				mentionEntityMap.put(mention, entity);
				entityGraph.setDisambiguationMap(mentionEntityMap);
			}else if (mention.getCandidateEntity().size() == 1) {//候选实体为1
				Entity entity = mention.getCandidateEntity().get(0);
				mentionEntityMap.put(mention, entity);
				entityGraph.setDisambiguationMap(mentionEntityMap);
				entityGraph.updatePreferVectorOfDocument(mention, entity);
			}else {//候选实体为多个
				List<Entity> candidateList = mention.getCandidateEntity();
				int len = candidateList.size();
				int maxScoreIndex = 0;
				double score,maxScore =0,maxSemanticScore=0,maxContextScore=0,
						maxPopularityScore=0,maxLiteralScore=0;
				double semanticScore=0,contextScore=0,popularityScore=0,literalScore=0;
				double[] semanticScores = new double[len];
				double[] contextScores = new double[len];
				double[] popularityScores = new double[len];
				double[] literalScores = new double[len];
				//获取文档语义签名
				signatureOfDocument = entityGraph.calSignature(entityGraph.getPreferVectorOfDocument());
				
				for(int i=0;i<len;i++){
					Entity entity = candidateList.get(i);
					entityGraph.setPreferVectorOfEntity(entityGraph.getEntityIndex().get(entity.getEntityName()));
					preferEntityVector = entityGraph.getPreferVectorOfEntity();
					signatureOfEntity = entityGraph.calSignature(preferEntityVector);
					semanticScores[i] = calSemanticSimilarity(signatureOfEntity, signatureOfDocument);
					if(maxSemanticScore < semanticScores[i]){
						maxSemanticScore = semanticScores[i];
					}
					contextScores[i] = calLocalSimilarity(mention, candidateList.get(i));
					if(maxContextScore < contextScores[i]){
						maxContextScore = contextScores[i];
					}
					popularityScores[i] = calPopularityScore(entity.getPopularity(), mention.getTotalPopularity());
					if(maxPopularityScore < popularityScores[i]){
						maxPopularityScore = popularityScores[i];
					}
					literalScores[i] = calLiteralScore(mention.getMentionName(), 
							entity.getEntityName(), RELRWParameterBean.getSigmoidParameter());
					if(maxLiteralScore < literalScores[i]){
						maxLiteralScore = literalScores[i];
					}
				}
				logger.info("maxSemanticScore:"+maxSemanticScore+"\tmaxContextScore:"+maxContextScore+
						"\tmaxPopularityScore:"+maxPopularityScore+"\tmaxLiteralScore:"+maxLiteralScore);
				for(int i=0;i<len;i++){
					score = 0;
					if(maxSemanticScore > 0){
						semanticScore = semanticSimWeight * (semanticScores[i] / maxSemanticScore);
						score += semanticScore;
					}
					if(maxContextScore > 0){
						contextScore = contextSimWeight * (contextScores[i] / maxContextScore);
						score += contextScore;
					}
					if(maxPopularityScore > 0){
						popularityScore = popularityWeight * (popularityScores[i] / maxPopularityScore);
						score += popularityScore;
					}
					if(maxLiteralScore > 0){
						literalScore = literalSimWeight * (literalScores[i] / maxLiteralScore);
						score += literalScore;
					}
//					//若候选实体与mention字面量完全一致且score大于阈值则认为其是目标实体
//					if(candidateList.get(i).getEntityName().equals(mention.getMentionName())){
//						if(score >= RELRWParameterBean.getNilThres()){
//							score = 1;
//						}
//					}
					candidateList.get(i).setScore(score);
					logger.info(mention.getMentionName()+"的候选实体"+candidateList.get(i).getEntityName()
							+"的四项得分归一化前各为:"+semanticScores[i]+"\t"+contextScores[i]+"\t"
							+popularityScores[i]+"\t"+literalScores[i]);
					logger.info(mention.getMentionName()+"的候选实体"+candidateList.get(i).getEntityName()
							+"的四项得分归一化后各为:"+semanticScore+"\t"+contextScore+"\t"+popularityScore+"\t"
							+literalScore);
					logger.info(candidateList.get(i).getEntityName()+" 加权总得分为:"+score);
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
			if(signatureOfEntity[i] == 0){
				continue;
			}
			if(signatureOfDocument[i] == 0){
				result += signatureOfEntity[i] * RELRWParameterBean.getGamma();
			}else{
				result += signatureOfEntity[i] * Math.log(signatureOfEntity[i] / signatureOfDocument[i]);
			}
		}
		
		if(result != 0){
			result = 1 / result;
		}
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
			return (double)commonCount / allCount;
		}else{
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
//		logger.info(popularity / totalPopularity);
		if(totalPopularity > 0)
			return popularity / totalPopularity;
		else
			return 0;
	}

	/**
	 * 计算字面量得分
	 * @param s1
	 * @param s2
	 * @param a
	 * @return
	 */
	public double calLiteralScore(String s1,String s2,double a){
		int dis = EditDistance.getEditDistance(s1, s2);
		return 1 /(1+(Math.exp(dis - 3)));
	}
	
}
