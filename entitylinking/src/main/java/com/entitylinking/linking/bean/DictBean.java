package com.entitylinking.linking.bean;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 同义歧义词典
 * @author shijing
 *
 */
public class DictBean {

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
	
	private static Map<String, String> synonymsDict;
	private static Map<String, HashSet<String>> ambiguationDict;
	private static Set<String> posDict;
	private static Set<String> stopWordDict;
	private static Map<String, Integer> dfDict;
	
	public static String getSynonymsDictPath() {
		return synonymsDictPath;
	}
	public static void setSynonymsDictPath(String synonymsDictPath) {
		DictBean.synonymsDictPath = synonymsDictPath;
	}
	public static String getAmbiguationDictPath() {
		return ambiguationDictPath;
	}
	public static void setAmbiguationDictPath(String ambiguationDictPath) {
		DictBean.ambiguationDictPath = ambiguationDictPath;
	}
	public static Map<String, String> getSynonymsDict() {
		return synonymsDict;
	}
	public static void setSynonymsDict(Map<String, String> synonymsDict) {
		DictBean.synonymsDict = synonymsDict;
	}
	public static Map<String, HashSet<String>> getAmbiguationDict() {
		return ambiguationDict;
	}
	public static void setAmbiguationDict(Map<String, HashSet<String>> ambiguationDict) {
		DictBean.ambiguationDict = ambiguationDict;
	}
	public static String getPosDictPath() {
		return posDictPath;
	}
	public static void setPosDictPath(String posDictPath) {
		DictBean.posDictPath = posDictPath;
	}
	public static String getStopWordDictPath() {
		return stopWordDictPath;
	}
	public static void setStopWordDictPath(String stopWordDictPath) {
		DictBean.stopWordDictPath = stopWordDictPath;
	}
	public static Set<String> getPosDict() {
		return posDict;
	}
	public static void setPosDict(Set<String> posDict) {
		DictBean.posDict = posDict;
	}
	public static Set<String> getStopWordDict() {
		return stopWordDict;
	}
	public static void setStopWordDict(Set<String> stopWordDict) {
		DictBean.stopWordDict = stopWordDict;
	}
	public static String getDfDictPath() {
		return dfDictPath;
	}
	public static void setDfDictPath(String dfDictPath) {
		DictBean.dfDictPath = dfDictPath;
	}
	public static Map<String, Integer> getDfDict() {
		return dfDict;
	}
	public static void setDfDict(Map<String, Integer> dfDict) {
		DictBean.dfDict = dfDict;
	}

	
}
