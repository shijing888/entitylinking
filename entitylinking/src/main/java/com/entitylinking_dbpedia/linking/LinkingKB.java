package com.entitylinking_dbpedia.linking;

//import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.ArrayUtils;
//import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.entitylinking_dbpedia.linking.bean.DictBean;
import com.entitylinking_dbpedia.linking.bean.Entity;
import com.entitylinking_dbpedia.linking.bean.EntityGraph;
import com.entitylinking_dbpedia.linking.bean.Mention;
import com.entitylinking_dbpedia.linking.bean.RELRWParameterBean;
import com.entitylinking_dbpedia.linking.bean.Text;
import com.entitylinking_dbpedia.utils.CommonUtils;
import com.entitylinking_dbpedia.utils.StringDistance;

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
			if(DictBean.getSpecialWordsDict().containsKey(mention.getMentionName())){
				Entity entity = new Entity();
				entity.setEntityName(DictBean.getSpecialWordsDict().get(mention.getMentionName()));
				entity.setScore(1);
				mentionEntityMap.put(mention, entity);
				entityGraph.setDisambiguationMap(mentionEntityMap);
				entityGraph.updatePreferVectorOfDocument(mention, entity);
				continue;
			}
			if(mention.getCandidateEntity().size() == 0){//无候选实体
				Entity entity = new Entity();
				entity.setEntityName(RELRWParameterBean.getNil());
				mentionEntityMap.put(mention, entity);
				entityGraph.setDisambiguationMap(mentionEntityMap);
			}else if (mention.getCandidateEntity().size() == 1) {//候选实体为1
				Entity entity = mention.getCandidateEntity().get(0);
				entity.setScore(1);
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
//					logger.info(entity.getEntityName()+"的语义签名:" + StringUtils.join(ArrayUtils.toObject(signatureOfEntity), "\t"));
//					logger.info("文档的语义签名:" + StringUtils.join(ArrayUtils.toObject(signatureOfDocument), "\t"));
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
//					literalScores[i] = calLiteralScore(mention.getMentionName(), 
//							entity.getEntityName());
//					literalScores[i] = calLiteralScore(mention.getMentionName(), 
//							entity.getEntityName(), RELRWParameterBean.getSigmoidParameter(),"averge of ed and lcs");
					if(maxLiteralScore < literalScores[i]){
						maxLiteralScore = literalScores[i];
					}
				}
				logger.error("maxSemanticScore:"+maxSemanticScore+"\tmaxContextScore:"+maxContextScore+
						"\tmaxPopularityScore:"+maxPopularityScore+"\tmaxLiteralScore:"+maxLiteralScore);
				for(int i=0;i<len;i++){
					score = 0;
					if(maxSemanticScore > 0){
						semanticScore = semanticSimWeight * (semanticScores[i] / maxSemanticScore);
						score += semanticScore;
					}
					if(maxPopularityScore > 0){
						popularityScore = popularityWeight * (popularityScores[i] / maxPopularityScore);
						score += popularityScore;
					}
					if(maxLiteralScore > 0){
						literalScore = literalSimWeight * (literalScores[i] / maxLiteralScore);
						score += literalScore;
					}
					if(maxContextScore > 0){
						contextScore = contextSimWeight * (contextScores[i] / maxContextScore);
						score += contextScore;
					}
					
					candidateList.get(i).setScore(score);
					logger.error(mention.getMentionName()+"的候选实体"+candidateList.get(i).getEntityName()
							+"的四项得分归一化前各为:"+semanticScores[i]+"\t"+contextScores[i]+"\t"
							+popularityScores[i]+"\t"+literalScores[i]);
					logger.error(mention.getMentionName()+"的候选实体"+candidateList.get(i).getEntityName()
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
					mentionEntityMap.put(mention, maxScoreEntity);
				}else {
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
//		logger.error("-----------------------------------------------------------------------");
		for(int i=0;i<signatureOfEntity.length;i++){
			if(signatureOfEntity[i] == 0){
				continue;
			}
			if(signatureOfDocument[i] == 0){
				result += signatureOfEntity[i] * RELRWParameterBean.getGamma();
//				logger.error("result:"+result);
			}else{
				double log = Math.log(signatureOfEntity[i] / signatureOfDocument[i]);
				double res = signatureOfEntity[i] * log;
				result += res;
//				logger.error("语义签名计算:"+result+"\t"+signatureOfEntity[i]+"\t"+signatureOfDocument[i]
//						+"\t"+log +"\t"+ res);
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
	 * 编辑距离
	 * 最长公共子序列
	 * 编辑距离+最长公共子序列
	 * @param s1
	 * @param s2
	 * @param a
	 * @return
	 */
	public double calLiteralScore(String s1,String s2,double a){
		int dis = StringDistance.getEditDistance(s1, s2);
		return 1 /(1+(Math.exp(dis - a)));
	}
	
	public double calLiteralScore(String s1,String s2){
		int dis = StringDistance.getLCSDistance(s1, s2);
		return (double)dis / s2.length();
	}
	
	public double calLiteralScore(String s1,String s2,double a,String category){
		double score1 = calLiteralScore(s1, s2, a);
		double score2 = calLiteralScore(s1, s2);
		return (score1 + score2) / 2;
	}
	
//	@Test
//	public void test(){
//		double[] d1 = {0.00000000000000001,0,0,0,0,0,0,0.000000000000000000000008};
//		double[] d2 = {8,7,6,5,4,3,2,1};
//		System.out.println(StringUtils.join(ArrayUtils.toObject(d1)," "));
//		d1 = vectorNormalization(d1);
//		System.out.println(StringUtils.join(ArrayUtils.toObject(d1)," "));
//		System.out.println(StringUtils.join(ArrayUtils.toObject(d2)," "));
//		d2 = vectorNormalization(d2);
//		System.out.println(StringUtils.join(ArrayUtils.toObject(d2)," "));
//		double res = calSemanticSimilarity(d1, d2);
//		System.out.println(res);
//	}
//	/**
//	 * 对向量做归一化处理
//	 * @param vector
//	 * @return
//	 */
//	public double[] vectorNormalization(double[] vector){
//		if(vector==null || vector.length==0)
//			return vector;
//		double sum = 0;
//		for(int i=0;i<vector.length;i++){
//			sum += vector[i];
//		}
//		if(sum > 0){
//			for(int i=0;i<vector.length;i++){
//				vector[i] /= sum;
//			}
//			return vector;
//		}
//		return vector;
//				
//	}
}
