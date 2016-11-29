package com.entitylinking.linking;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.entitylinking.linking.bean.Entity;
import com.entitylinking.linking.bean.EntityGraph;
import com.entitylinking.linking.bean.Mention;
import com.entitylinking.linking.bean.RELRWParameterBean;
import com.entitylinking.linking.bean.Text;

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
		double[] score = new double[entityGraph.getEntityLen()];
		double[] signatureOfDocument;
		double[] signatureOfEntity;
		double[] preferEntityVector = null;
		//获取初始文档偏好向量
		signatureOfDocument = entityGraph.calSignature(entityGraph.getPreferVectorOfDocument());
//		entityGraph.setSemantitcSignatureOfDocument(signatureOfDocument);
		for(Mention mention:entityGraph.getMentions()){
			if(mention.getCandidateEntity().size() == 0){//无候选实体
				mentionEntityMap.put(mention, null);
			}else if (mention.getCandidateEntity().size() == 1) {//候选实体为1
				mentionEntityMap.put(mention, mention.getCandidateEntity().get(0));
			}else {//候选实体为多个
//				signatureOfDocument = entityGraph.getSemantitcSignatureOfDocument();
				for(int i=0;i<mention.getCandidateEntity().size();i++){
					Entity entity = mention.getCandidateEntity().get(i);
					entityGraph.setPreferVectorOfEntity(i);
					preferEntityVector = entityGraph.getPreferVectorOfEntity();
					signatureOfEntity = entityGraph.calSignature(preferEntityVector);
					logger.info(mention.getMentionName()+"的候选实体"+entity.getEntityName()+"与文档的语义相似度为:");
					score[i] = calSemanticSimilarity(signatureOfEntity, signatureOfDocument);
					logger.info(mention.getMentionName()+"与候选实体"+entity.getEntityName()+"的局部相容性为:");
					score[i] *= calLocalSimilarity(mention, mention.getCandidateEntity().get(i));
					logger.info(mention.getMentionName()+"的候选实体"+entity.getEntityName()+"的流行度得分为:");
					score[i] *= calPopularityScore(entity.getPopularity(), mention.getTotalPopularity());
				}
				int index = maxIndex(score);
				if(!mentionEntityMap.containsKey(mention) ||
						mentionEntityMap.containsKey(mention) &&
						!mentionEntityMap.get(mention).getEntityName()
						.equals(entityGraph.getEntities()[index].getEntityName())){
						mentionEntityMap.put(mention, entityGraph.getEntities()[index]);
						entityGraph.setPreferVectorOfDocument();
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
	public int maxIndex(double[] arrays){
		double max = 0;
		int index = 0;
		for(int i=0;i<arrays.length;i++){
			if(arrays[i] > max){
				max = arrays[i];
				index = i;
			}
		}
		return index;
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
		Set<String> entityContext = entity.getEntityContext();
		int commonCount = 0;
		int allCount = mentionContext.size();
		for(String item:mentionContext){
			if(entityContext.contains(item)){
				commonCount++;
			}
		}
		for(String item2:entityContext){
			if(!mentionContext.contains(item2)){
				allCount++;
			}
		}
		
		return (double)commonCount / allCount;
		
	}
	
	/**
	 * 计算流行度得分
	 * @param popularity
	 * @param totalPopularity
	 * @return
	 */
	public double calPopularityScore(double popularity, double totalPopularity){
		return popularity / totalPopularity;
	}
}
