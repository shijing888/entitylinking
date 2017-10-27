package com.entitylinking.entitylinking;


import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;  
import org.apache.commons.math3.linear.RealMatrix;  
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;  
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;  
import org.apache.commons.math3.stat.descriptive.moment.Mean;  
import org.apache.commons.math3.stat.descriptive.moment.Skewness;  
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;  
import org.apache.commons.math3.stat.descriptive.moment.Variance;  
import org.apache.commons.math3.stat.descriptive.rank.Max;  
import org.apache.commons.math3.stat.descriptive.rank.Min;  
import org.apache.commons.math3.stat.descriptive.rank.Percentile;  
import org.apache.commons.math3.stat.descriptive.summary.Product;  
import org.apache.commons.math3.stat.descriptive.summary.Sum;  
import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;
import org.apache.jena.sparql.function.library.leviathan.e;

import com.entitylinking_dbpedia.linking.bean.RELRWParameterBean;  

/** 
*  
* @ClassName: TestMathUserage  
* @Description: math组件用法实例  
* @author zengfh  
* @date 2014年11月21日 下午1:25:24  
* 
*/  
public class TestMathUserage {  
  public static void main(String[] args) {  
//      double[] values = new double[] { 0.33, 1.33, 0.27333, 0.3, 0.501,  
//              0.444, 0.44, 0.34496, 0.33, 0.3, 0.292, 0.667 };  
//      double[] values = new double[]{0.13282149884807953,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
    double[] values = new double[]{1,0,0};
    double[] val = values;
    val[1] = 1;
    System.out.println(Arrays.toString(values));
	  /* 
       * System.out.println( "min: " + StatUtils.min( values ) ); 
       * System.out.println( "max: " + StatUtils.max( values ) ); 
       * System.out.println( "mean: " + StatUtils.mean( values ) ); // Returns 
       * the arithmetic mean of the entries in the input array, or Double.NaN 
       * if the array is empty System.out.println( "product: " + 
       * StatUtils.product( values ) ); //Returns the product of the entries 
       * in the input array, or Double.NaN if the array is empty. 
       * System.out.println( "sum: " + StatUtils.sum( values ) ); //Returns 
       * the sum of the values in the input array, or Double.NaN if the array 
       * is empty. System.out.println( "variance: " + StatUtils.variance( 
       * values ) ); // Returns the variance of the entries in the input 
       * array, or Double.NaN if the array is empty. 
       */  

      Min min = new Min();  
      Max max = new Max();  
        
      Mean mean = new Mean(); // 算术平均值  
      Product product = new Product();//乘积  
      Sum sum = new Sum();  
      Variance variance = new Variance();//方差  
      System.out.println("min: " + min.evaluate(values));  
      System.out.println("max: " + max.evaluate(values));  
      System.out.println("mean: " + mean.evaluate(values));  
      System.out.println("product: " + product.evaluate(values));  
      System.out.println("sum: " + sum.evaluate(values));  
      System.out.println("variance: " + variance.evaluate(values));  

//      Percentile percentile = new Percentile(); // 百分位数  
//      GeometricMean geoMean = new GeometricMean(); // 几何平均数,n个正数的连乘积的n次算术根叫做这n个数的几何平均数  
//      Skewness skewness = new Skewness(); // Skewness();  
//      Kurtosis kurtosis = new Kurtosis(); // Kurtosis,峰度  
//      SumOfSquares sumOfSquares = new SumOfSquares(); // 平方和  
//      StandardDeviation StandardDeviation = new StandardDeviation();//标准差  
//      System.out.println("80 percentile value: "  
//              + percentile.evaluate(values, 80.0));  
//      System.out.println("geometric mean: " + geoMean.evaluate(values));  
//      System.out.println("skewness: " + skewness.evaluate(values));  
//      System.out.println("kurtosis: " + kurtosis.evaluate(values));  
//      System.out.println("sumOfSquares: " + sumOfSquares.evaluate(values));  
//      System.out.println("StandardDeviation: " + StandardDeviation.evaluate(values));  
//        
//      System.out.println("-------------------------------------");  
//      // Create a real matrix with two rows and three columns  
      double[][] data1 = {{1,2,3}};
      double[][] data2 ={{1,2},{2,3},{3,4}};
      RealMatrix m = new Array2DRowRealMatrix(data1);  
      RealMatrix n = new Array2DRowRealMatrix(data2);    
      RealMatrix p = m.multiply(n);  
      
      System.out.println("m:"+m);
      System.out.println("n:"+n);
      System.out.println("p:"+p);  
    
      double[] realMatrix = n.preMultiply(values);
      ArrayRealVector realVector = new ArrayRealVector(realMatrix);
		realVector = (ArrayRealVector) realVector.mapMultiply(2);
		realVector = realVector.add(realVector.mapMultiply(0.5));
      System.out.println(StringUtils.join(ArrayUtils.toObject(realVector.getDataRef()), " "));
      
      realVector = new ArrayRealVector(values);
      realVector = (ArrayRealVector) realVector.mapMultiply(2);
      double[] vec = n.preMultiply(realVector.getDataRef());
      System.out.println(StringUtils.join(ArrayUtils.toObject(vec), " "));
//    calSignature(values);
    
//      double[][] matrixData = { {1d,2d,3d}, {2d,5d,3d}};  
//      RealMatrix m = new Array2DRowRealMatrix(matrixData);  
//      System.out.println(m);  
////      // One more with three rows, two columns  
//      double[][] matrixData2 = { {1d,2d}, {2d,5d}, {1d, 7d}};  
//      RealMatrix n = new Array2DRowRealMatrix(matrixData2);          
////      // Note: The constructor copies  the input double[][] array.           
////      // Now multiply m by n  
//      RealMatrix p = m.multiply(n);  
//      System.out.println("p:"+p);  
//      System.out.println(p.getRowDimension());    // 2  
//      System.out.println(p.getColumnDimension()); // 2           
//      // Invert p, using LU decomposition  
//      RealMatrix pInverse = new LUDecomposition(p).getSolver().getInverse();  
//      System.out.println(pInverse);  
  }  
  
  /**
	 * 带重启的随机游走计算语义签名
	 * @param graph
	 * @param beginIndex,文档的语义签名按
	 * @param preferVector
	 * @return newSignatureVector
	 */
	public static double[] calSignature(double[] preferVector){
		int len = preferVector.length;
		double[] oldSignatureVector = new double[len];
		double[] newSignatureVector = new double[len];
		for(int i=0;i<len;i++){
			oldSignatureVector[i] = 1;
			newSignatureVector[i] = 1;
		}
		double[] tempVector;
		double alpha = 0.85;
		double[][] matrix = getTransferMatrix(len);
		double[] sum = new double[len];
		for(int i=0;i<len;i++){
			for(int j=0;j<len;j++){
				sum[j] += matrix[i][j];
			}
		}
		for(int i=0;i<len;i++){
			for(int j=0;j<len;j++){
				if(sum[j] > 0){
					matrix[i][j] /= sum[j];
				}else{
					matrix[i][j] = 0;
				}
				
			}
		}
		RealMatrix transferMatrix = new Array2DRowRealMatrix(matrix);
//		System.out.println(" before transferMatrix:"+transferMatrix);
//		transferMatrix = transferMatrix.transpose();
		System.out.println("alpha:"+alpha);
		System.out.println("transferMatrix:"+transferMatrix);
		System.out.println("after transferMatrix:"+transferMatrix.transpose());
		ArrayRealVector realVector;
		ArrayRealVector preferRealVector = new ArrayRealVector(preferVector);
//		logger.info("初始向量:"+StringUtils.join(ArrayUtils.toObject(preferVector), "\t"));
//		long time1,time2;
//		time1 = System.currentTimeMillis();
		int i=0;
		do {
			oldSignatureVector = Arrays.copyOf(newSignatureVector, len);
			tempVector = transferMatrix.preMultiply(oldSignatureVector);
//			System.out.println(StringUtils.join(ArrayUtils.toObject(oldSignatureVector), "\t"));
//			System.out.println(StringUtils.join(ArrayUtils.toObject(tempVector), "\t"));
			realVector = new ArrayRealVector(tempVector);
//			System.out.println(StringUtils.join(ArrayUtils.toObject(realVector.getDataRef()), "\t"));
			realVector = (ArrayRealVector) realVector.mapMultiply(alpha);
//			System.out.println(StringUtils.join(ArrayUtils.toObject(realVector.getDataRef()), "\t"));
//			System.out.println(StringUtils.join(ArrayUtils.toObject(((ArrayRealVector) preferRealVector.mapMultiply(1-alpha)).getDataRef()), "\t"));
			realVector = realVector.add(preferRealVector.mapMultiply(1-alpha));
//			System.out.println(StringUtils.join(ArrayUtils.toObject(realVector.getDataRef()), "\t"));
			newSignatureVector = realVector.getDataRef();
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.out.println("oldSignatureVector:"+StringUtils.join(ArrayUtils.toObject(oldSignatureVector), "\t"));
//			System.out.println("newSignatureVector:"+StringUtils.join(ArrayUtils.toObject(newSignatureVector), "\t"));
//			System.out.println("收敛后的向量:"+StringUtils.join(ArrayUtils.toObject(newSignatureVector), "\t"));
		} while (!isConvergence(oldSignatureVector, newSignatureVector) && i++ < 9999);
		
//		time2 = System.currentTimeMillis();
//		logger.info("收敛后的向量:"+StringUtils.join(ArrayUtils.toObject(newSignatureVector), "\t"));
		System.out.println("收敛后的向量:"+StringUtils.join(ArrayUtils.toObject(newSignatureVector), "\t"));
//		logger.info("随机游走花费时间:"+(time2 - time1)/1000.0+"秒");
		return newSignatureVector;
	}
	
	/**
	 * 判断是否收敛
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static boolean isConvergence(double[] vector1,double[] vector2){
		System.out.println("old:"+StringUtils.join(ArrayUtils.toObject(vector1), "\t"));
		System.out.println("new:"+StringUtils.join(ArrayUtils.toObject(vector2), "\t"));
		double result = 0;
		if(vector1.length == vector2.length){
			for(int i =0;i<vector1.length;i++){
				result += Math.abs(vector1[i] - vector2[i]);
			}
		}
		System.out.println("result:"+result);
		if(result < 0.001){
			return true;
		}else {
			return false;
		}
	}
	
	public static double[][] getTransferMatrix(int len){
		double[][] transferArray = new double[len][len];
		for(int i=0;i<len;i++){
			for(int j=0;j<len;j++){
				transferArray[i][j] = 0.001 * (i+1) *(j+1);
//				transferArray[i][j] = 0.01 * (i) *(j);
			}
			
		}
		
		return transferArray;
	}
}  