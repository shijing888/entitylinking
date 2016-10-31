package com.entitylinking.linking;

import java.util.ArrayList;
import java.util.List;

import com.entitylinking.linking.bean.Entity;
import com.entitylinking.linking.bean.EntityGraph;
import com.entitylinking.linking.bean.Text;

/**
 * 计算语义签名
 * @author HP
 *
 */
public class SemanticSignature {

	private final int ITERATORNUM = 10;
	public static void main(String args[]){
		
	}
	/**
	 * 计算实体与文档的语义签名
	 * @param entityGraph，实体子图
	 * @param entity，若为null则计算文档的语义签名，否则计算实体的语义签名
	 * @return 实体或文档的语义签名
	 */
	public List<Double> calSemanticSignature(EntityGraph entityGraph, Entity entity){
		//语义签名向量
		List<Double> semanticSignature = new ArrayList<>();
		//初始化偏好向量
		List<Double> preferenceVector;
		if(entity == null){
			preferenceVector = initDocumentPreferenceVector(entityGraph);
		}else{
			preferenceVector = new ArrayList<>();
			int index = entityGraph.getEntities().indexOf(entity);
			for(int i=0;i<entityGraph.getEntities().size();i++){
				if(index == i)
					preferenceVector.add(1.0);
				else
					preferenceVector.add(0.0);
			}
		}
		
		return semanticSignature;
	}
	
	public List<Double> initDocumentPreferenceVector(EntityGraph entityGraph){
		List<Double> vectorList = new ArrayList<>();
		double value;
		for(int i=0;i<entityGraph.getEntities().size();i++){
			value = entityGraph.getMentionMap().get(entityGraph.getEntities().get(i)).getTfidfValue() 
					* entityGraph.getEntities().get(i).getPopularity();
			vectorList.add(value);
		}
		
		return vectorList;
	}
}
