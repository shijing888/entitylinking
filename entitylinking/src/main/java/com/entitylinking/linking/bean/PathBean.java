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
	public static void setRelParameterPath(String relParameterPath) {
		PathBean.relParameterPath = relParameterPath;
	}
	public static String getEntityRelationPath() {
		return entityRelationPath;
	}
	public static void setEntityRelationPath(String entityRelationPath) {
		PathBean.entityRelationPath = entityRelationPath;
	}
	
}
