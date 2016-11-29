package com.entitylinking.linking.bean;

/**
 * REL-RW算法中的配置参数
 * @author HP
 *
 */
public class RELRWParameterBean {

	/**源文件路径*/
	private static String sourceFileDirPath;
	/**上下文的窗口大小*/
	private static int contextWindow;
	/**alpha*/
	private static double alpha;
	/**收敛精度*/
	private static double convergencePrecise;
	/**散度gamma值*/
	private static double gamma;
	/**限制实体正文字符长度*/
	private static int entityContentLen;
	/**候选实体剪枝阈值*/
	private static int candidateEntityNumThresh;
	/**流行度阈值*/
	private static int popularityThresh;
	/**实体共现次数阈值*/
	private static int cooccurenceThresh;
	/**实体关系域*/
	private static String entityRelationField1;
	private static String entityRelationField2;
	private static String entityRelationField3;
	
	public static String getSourceFileDirPath() {
		return sourceFileDirPath;
	}
	public static void setSourceFileDirPath(String sourceFileDirPath) {
		RELRWParameterBean.sourceFileDirPath = sourceFileDirPath;
	}
	public static int getContextWindow() {
		return contextWindow;
	}
	public static double getAlpha() {
		return alpha;
	}
	public static void setAlpha(double alpha) {
		RELRWParameterBean.alpha = alpha;
	}
	public static double getConvergencePrecise() {
		return convergencePrecise;
	}
	public static void setConvergencePrecise(double convergencePrecise) {
		RELRWParameterBean.convergencePrecise = convergencePrecise;
	}
	public static void setContextWindow(int contextWindow){
		RELRWParameterBean.contextWindow = contextWindow;
	}
	public static double getGamma() {
		return gamma;
	}
	public static void setGamma(double gamma) {
		RELRWParameterBean.gamma = gamma;
	}
	public static int getEntityContentLen() {
		return entityContentLen;
	}
	public static void setEntityContentLen(int entityContentLen) {
		RELRWParameterBean.entityContentLen = entityContentLen;
	}
	public static int getCandidateEntityNumThresh() {
		return candidateEntityNumThresh;
	}
	public static void setCandidateEntityNumThresh(int candidateEntityNumThresh) {
		RELRWParameterBean.candidateEntityNumThresh = candidateEntityNumThresh;
	}
	public static String getEntityRelationField1() {
		return entityRelationField1;
	}
	public static void setEntityRelationField1(String entityRelationField1) {
		RELRWParameterBean.entityRelationField1 = entityRelationField1;
	}
	public static String getEntityRelationField2() {
		return entityRelationField2;
	}
	public static void setEntityRelationField2(String entityRelationField2) {
		RELRWParameterBean.entityRelationField2 = entityRelationField2;
	}
	public static String getEntityRelationField3() {
		return entityRelationField3;
	}
	public static void setEntityRelationField3(String entityRelationField3) {
		RELRWParameterBean.entityRelationField3 = entityRelationField3;
	}
	public static int getPopularityThresh() {
		return popularityThresh;
	}
	public static void setPopularityThresh(int popularityThresh) {
		RELRWParameterBean.popularityThresh = popularityThresh;
	}
	public static int getCooccurenceThresh() {
		return cooccurenceThresh;
	}
	public static void setCooccurenceThresh(int cooccurenceThresh) {
		RELRWParameterBean.cooccurenceThresh = cooccurenceThresh;
	}
	
}
