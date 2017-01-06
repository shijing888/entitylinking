package com.entitylinking_dbpedia.linking.bean;

public class PathBean {

	/**同义词典*/
	private static String synonymsDictPath;
	/**歧义词典*/
	private static String ambiguationDictPath;
	/**词性表*/
	private static String posDictPath;
	/**停用词表*/
	private static String stopWordDictPath;
	/**文档频率词典*/
	private static String dfDictPath;
	/**基于dbpedia的参数xml路径*/
	private static String relParameterByDbpediaPath;
	/**entityByDbpediaRelationPath 索引文件夹路径*/
	private static String entityByDbpediaRelationPath;
	/**短摘要文本索引文件路径*/
	private static String shortAbstractTextPath;
	/**mention集合的xml文件*/
	private static String mentionDictPath;
	/**实体链接结果文件夹*/
	private static String resultDirPath;
	/**实体上下文文件路径*/
	private static String entityContextPath;
	/**mention上下文文件夹路径*/
	private static String mentionContextDirPath;
	/**dbpedia实体上下文文件路径*/
	private static String entityByDbpediaContextPath;
	/**dbpedia实体流行度文件路径*/
	private static String entityByDbpeidaPopularityPath;
	/**dbpedia实体标签*/
	private static String dbpediaLabelNamePath;
	
	public static String getDbpediaLabelNamePath() {
		return dbpediaLabelNamePath;
	}
	public static void setDbpediaLabelNamePath(String dbpediaLabelNamePath) {
		PathBean.dbpediaLabelNamePath = dbpediaLabelNamePath;
	}
	public static String getShortAbstractTextPath() {
		return shortAbstractTextPath;
	}
	public static void setShortAbstractTextPath(String shortAbstractTextPath) {
		PathBean.shortAbstractTextPath = shortAbstractTextPath;
	}
	public static String getEntityByDbpeidaPopularityPath() {
		return entityByDbpeidaPopularityPath;
	}
	public static void setEntityByDbpeidaPopularityPath(String entityByDbpeidaPopularityPath) {
		PathBean.entityByDbpeidaPopularityPath = entityByDbpeidaPopularityPath;
	}
	public static String getEntityByDbpediaRelationPath() {
		return entityByDbpediaRelationPath;
	}
	public static void setEntityByDbpediaRelationPath(String entityByDbpediaRelationPath) {
		PathBean.entityByDbpediaRelationPath = entityByDbpediaRelationPath;
	}
	public static String getEntityByDbpediaContextPath() {
		return entityByDbpediaContextPath;
	}
	public static void setEntityByDbpediaContextPath(String entityByDbpediaContextPath) {
		PathBean.entityByDbpediaContextPath = entityByDbpediaContextPath;
	}
	public static String getEntityContextPath() {
		return entityContextPath;
	}
	public static void setEntityContextPath(String entityContextPath) {
		PathBean.entityContextPath = entityContextPath;
	}
	public static String getRelParameterByDbpediaPath() {
		return relParameterByDbpediaPath;
	}
	public static void setRelParameterByDbpediaPath(String relParameterByDbpediaPath) {
		PathBean.relParameterByDbpediaPath = relParameterByDbpediaPath;
	}
	public static String getMentionContextDirPath() {
		return mentionContextDirPath;
	}
	public static void setMentionContextDirPath(String mentionContextDirPath) {
		PathBean.mentionContextDirPath = mentionContextDirPath;
	}
	public static String getSynonymsDictPath() {
		return synonymsDictPath;
	}
	public static void setSynonymsDictPath(String synonymsDictPath) {
		PathBean.synonymsDictPath = synonymsDictPath;
	}
	public static String getAmbiguationDictPath() {
		return ambiguationDictPath;
	}
	public static void setAmbiguationDictPath(String ambiguationDictPath) {
		PathBean.ambiguationDictPath = ambiguationDictPath;
	}
	public static String getPosDictPath() {
		return posDictPath;
	}
	public static void setPosDictPath(String posDictPath) {
		PathBean.posDictPath = posDictPath;
	}
	public static String getStopWordDictPath() {
		return stopWordDictPath;
	}
	public static void setStopWordDictPath(String stopWordDictPath) {
		PathBean.stopWordDictPath = stopWordDictPath;
	}
	public static String getDfDictPath() {
		return dfDictPath;
	}
	public static void setDfDictPath(String dfDictPath) {
		PathBean.dfDictPath = dfDictPath;
	}

	public static String getResultDirPath() {
		return resultDirPath;
	}
	public static void setResultDirPath(String resultDirPath) {
		PathBean.resultDirPath = resultDirPath;
	}
	
	public static String getMentionDictPath() {
		return mentionDictPath;
	}
	public static void setMentionDictPath(String mentionDictPath) {
		PathBean.mentionDictPath = mentionDictPath;
	}
	
}
