package com.entitylinking_dbpedia.linking.bean;

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
	/**文档总数*/
	private static int totalDocument;
	/**候选实体剪枝阈值*/
	private static int candidateEntityNumThresh;
	/**流行度阈值*/
	private static int popularityThresh;
	/**实体共现次数阈值*/
	private static int cooccurenceThresh;
	/**语义相似性权值*/
	private static double semanticSimWeight;
	/**上下文相似性权值*/
	private static double contextSimWeight;
	/**先验权值*/
	private static double popularityWeight;
	/**字面量相似性权值*/
	private static double literalSimWeight;
	/**空值阈值*/
	private static double nilThres;
	/**路径跳数*/
	private static int skipNums;
	/**基于路径的衰减系数*/
	private static double pathAlpha;
	/**选取的最短路径跳数*/
	private static int topK;
	/**空值字符串表示*/
	private static String nil;
	/**sigmoid函数参数*/
	private static double sigmoidParameter;
	/**实体关系域*/
	private static String entityRelationField1;
	private static String entityRelationField2;
	private static String entityRelationField3;
	/**短摘要索引域*/
	private static String shortAbstractField1;
	private static String shortAbstractField2;
	/**同义词典索引域*/
	private static String synonymsDictField1;
	private static String synonymsDictField2;
	/**歧义词典索引域*/
	private static String ambiguationDictField1;
	private static String ambiguationDictField2;
	/**dbpedia实体标签*/
	private static String dbpediaLabelField;
	public static double getLiteralSimWeight() {
		return literalSimWeight;
	}
	public static void setLiteralSimWeight(double literalSimWeight) {
		RELRWParameterBean.literalSimWeight = literalSimWeight;
	}
	public static int getTopK() {
		return topK;
	}
	public static void setTopK(int topK) {
		RELRWParameterBean.topK = topK;
	}
	public static int getSkipNums() {
		return skipNums;
	}
	public static void setSkipNums(int skipNums) {
		RELRWParameterBean.skipNums = skipNums;
	}
	public static double getPathAlpha() {
		return pathAlpha;
	}
	public static void setPathAlpha(double pathAlpha) {
		RELRWParameterBean.pathAlpha = pathAlpha;
	}
	public static double getSigmoidParameter() {
		return sigmoidParameter;
	}
	public static void setSigmoidParameter(double sigmoidParameter) {
		RELRWParameterBean.sigmoidParameter = sigmoidParameter;
	}
	public static String getSynonymsDictField1() {
		return synonymsDictField1;
	}
	public static void setSynonymsDictField1(String synonymsDictField1) {
		RELRWParameterBean.synonymsDictField1 = synonymsDictField1;
	}
	public static String getSynonymsDictField2() {
		return synonymsDictField2;
	}
	public static void setSynonymsDictField2(String synonymsDictField2) {
		RELRWParameterBean.synonymsDictField2 = synonymsDictField2;
	}
	public static String getAmbiguationDictField1() {
		return ambiguationDictField1;
	}
	public static void setAmbiguationDictField1(String ambiguationDictField1) {
		RELRWParameterBean.ambiguationDictField1 = ambiguationDictField1;
	}
	public static String getAmbiguationDictField2() {
		return ambiguationDictField2;
	}
	public static void setAmbiguationDictField2(String ambiguationDictField2) {
		RELRWParameterBean.ambiguationDictField2 = ambiguationDictField2;
	}
	public static String getDbpediaLabelField() {
		return dbpediaLabelField;
	}
	public static void setDbpediaLabelField(String dbpediaLabelField) {
		RELRWParameterBean.dbpediaLabelField = dbpediaLabelField;
	}
	public static String getShortAbstractField1() {
		return shortAbstractField1;
	}
	public static void setShortAbstractField1(String shortAbstractField1) {
		RELRWParameterBean.shortAbstractField1 = shortAbstractField1;
	}
	public static String getShortAbstractField2() {
		return shortAbstractField2;
	}
	public static void setShortAbstractField2(String shortAbstractField2) {
		RELRWParameterBean.shortAbstractField2 = shortAbstractField2;
	}
	public static String getNil() {
		return nil;
	}
	public static void setNil(String nil) {
		RELRWParameterBean.nil = nil;
	}
	public static int getTotalDocument() {
		return totalDocument;
	}
	public static void setTotalDocument(int totalDocument) {
		RELRWParameterBean.totalDocument = totalDocument;
	}
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
	public static double getSemanticSimWeight() {
		return semanticSimWeight;
	}
	public static void setSemanticSimWeight(double semanticSimWeight) {
		RELRWParameterBean.semanticSimWeight = semanticSimWeight;
	}
	public static double getContextSimWeight() {
		return contextSimWeight;
	}
	public static void setContextSimWeight(double contextSimWeight) {
		RELRWParameterBean.contextSimWeight = contextSimWeight;
	}
	public static double getPopularityWeight() {
		return popularityWeight;
	}
	public static void setPopularityWeight(double popularityWeight) {
		RELRWParameterBean.popularityWeight = popularityWeight;
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
	public static double getNilThres() {
		return nilThres;
	}
	public static void setNilThres(double nilThres) {
		RELRWParameterBean.nilThres = nilThres;
	}
	
}
