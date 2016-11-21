package com.entitylinking.linking.bean;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 词典
 * @author shijing
 *
 */
public class DictBean {

	private static Map<String, String> synonymsDict;
	private static Map<String, HashSet<String>> ambiguationDict;
	private static Set<String> posDict;
	private static Set<String> stopWordDict;
	private static Map<String, Integer> dfDict;
	
	
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
	public static Map<String, Integer> getDfDict() {
		return dfDict;
	}
	public static void setDfDict(Map<String, Integer> dfDict) {
		DictBean.dfDict = dfDict;
	}

	
}
