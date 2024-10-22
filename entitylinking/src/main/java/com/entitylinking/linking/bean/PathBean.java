package com.entitylinking.linking.bean;

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
	/**robust entity linking 参数xml路径*/
	private static String relParameterPath;
	/**entityRelation 索引文件夹路径*/
	private static String entityRelationPath;
	/**mention集合的xml文件*/
	private static String mentionDictPath;
	/**实体链接结果文件夹*/
	private static String resultDirPath;
	/**实体上下文文件路径*/
	private static String entityContextPath;
	/**mention上下文文件夹路径*/
	private static String mentionContextDirPath;
	
	public static String getEntityContextPath() {
		return entityContextPath;
	}
	public static void setEntityContextPath(String entityContextPath) {
		PathBean.entityContextPath = entityContextPath;
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
	public static String getRelParameterPath() {
		return relParameterPath;
	}
	public static String getResultDirPath() {
		return resultDirPath;
	}
	public static void setResultDirPath(String resultDirPath) {
		PathBean.resultDirPath = resultDirPath;
	}
	public static void setRelParameterPath(String relParameterPath) {
		PathBean.relParameterPath = relParameterPath;
	}
	public static String getEntityRelationPath() {
		return entityRelationPath;
	}
	public static void setEntityRelationPath(String entityRelationPath) {
		PathBean.entityRelationPath = entityRelationPath;
	}
	public static String getMentionDictPath() {
		return mentionDictPath;
	}
	public static void setMentionDictPath(String mentionDictPath) {
		PathBean.mentionDictPath = mentionDictPath;
	}
	
}
