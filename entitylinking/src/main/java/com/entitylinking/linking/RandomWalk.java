package com.entitylinking.linking;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;

import com.entitylinking.linking.bean.EntityGraph;
import com.entitylinking.linking.bean.RELRWParameterBean;

/**
 * 随机游走算法
 * @author HP
 *
 */
public class RandomWalk {

	/**
	 * 计算语义签名
	 * @param graph
	 * @param preferVector
	 * @return
	 */
	public double calSignature(EntityGraph graph, double[] preferVector){
		double[] oldSignatureVector = preferVector;
		double[] newSignatureVector = preferVector;
		double[] tempVector;
		double alpha = RELRWParameterBean.getAlpha();
		RealMatrix transferMatrix = new Array2DRowRealMatrix(graph.getTransferMatrix());
		ArrayRealVector realVector;
		ArrayRealVector preferRealVector = new ArrayRealVector(preferVector);
		do {
			tempVector = transferMatrix.preMultiply(oldSignatureVector);
			realVector = new ArrayRealVector(tempVector);
			realVector = (ArrayRealVector) realVector.mapMultiply(alpha);
			realVector = realVector.add(preferRealVector.mapMultiply(1-alpha));
			oldSignatureVector = newSignatureVector;
			newSignatureVector = realVector.getDataRef();
		} while (!isConvergence(oldSignatureVector, newSignatureVector));
		
		return 0;
	}
	
	/**
	 * 判断是否收敛
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public boolean isConvergence(double[] vector1,double[] vector2){
		double result = 0;
		if(vector1.length == vector2.length){
			for(int i =0;i<vector1.length;i++){
				result += Math.abs(vector1[i] - vector2[i]);
			}
		}
		if(result < RELRWParameterBean.getConvergencePrecise()){
			return true;
		}else {
			return false;
		}
	}
	
	
}
