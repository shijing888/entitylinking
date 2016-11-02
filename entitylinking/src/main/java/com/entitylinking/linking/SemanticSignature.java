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

	public static void main(String args[]){
		
	}
	/**
	 * 计算实体与文档的语义签名
	 * @param entityGraph，实体子图
	 * @return 实体或文档的语义签名
	 */
	public double[] calSemanticSignature(EntityGraph entityGraph){
		double[] priorSemanticSignature = null;
		double[] currentSemanticSignature = null;
		while(!isConvergence(priorSemanticSignature, currentSemanticSignature)){
			currentSemanticSignature = oneIteratorOfSemanticSignatur(priorSemanticSignature);
			priorSemanticSignature = currentSemanticSignature;
		}
		return null;
	}
	
	public double[] oneIteratorOfSemanticSignatur(double[] currentSemanticSignature){
		return null;
	}
	public boolean isConvergence(double[] priorSemanticSignature, double[] currentSemanticSignature){
		return true;
	}
}
