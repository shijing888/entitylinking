package com.entitylinking.linking.bean;

import java.util.HashSet;
import java.util.Map;

/**
 * 同义歧义词典
 * @author shijing
 *
 */
public class DictBean {

	private static String synonymsDictPath;
	private static String ambiguationDictPath;
	
	private static Map<String, String> synonymsDict;
	private static Map<String, HashSet<String>> ambiguationDict;
	
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
	

	
	
}
