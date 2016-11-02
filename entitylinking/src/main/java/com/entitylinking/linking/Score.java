package com.entitylinking.linking;

import com.entitylinking.linking.bean.Entity;
import com.entitylinking.linking.bean.Mention;

/**
 * 用于计算分配函数得分
 * @author HP
 *
 */
public class Score {

	/**
	 * phi函数，用于度量mention与候选实体的局部相容性
	 * 主要使用上下午相似性
	 */
	public double phiFunction(Mention mention,Entity entity){
		return 0;
	}
	
	/**
	 * psi函数，用于度量候选实体entity与全文的全局相容性
	 * 使用零KL散度来计算
	 * @param sse,实体语义签名
	 * @param ssd,文档语义签名
	 * @return
	 */
	public double psiFunction(double[] sse, double[] ssd){
		return 0;
	}
	
	
}
