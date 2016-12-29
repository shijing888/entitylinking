package com.entitylinking.linking.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 词典
 * @author shijing
 *
 */
public class DictBean {

	private  Map<String, String> synonymsDict;
	private  Map<String, HashSet<String>> ambiguationDict;
	private static Set<String> posDict;
	private static Set<String> stopWordDict;
	private static Map<String, Integer> dfDict;
	private static Map<String, List<Mention>> mentionDict;
	private static Map<String, HashSet<String>> entityContextDict;
	
	public static Map<String, HashSet<String>> getEntityContextDict() {
		return entityContextDict;
	}
	public static void setEntityContextDict(Map<String, HashSet<String>> entityContextDict) {
		DictBean.entityContextDict = entityContextDict;
	}
	public Map<String, String> getSynonymsDict() {
		return synonymsDict;
	}
	public void setSynonymsDict(Map<String, String> synonymsDict) {
		this.synonymsDict = synonymsDict;
	}
	public Map<String, HashSet<String>> getAmbiguationDict() {
		return ambiguationDict;
	}
	public void setAmbiguationDict(Map<String, HashSet<String>> ambiguationDict) {
		this.ambiguationDict = ambiguationDict;
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
	public static Map<String, List<Mention>> getMentionDict() {
		return mentionDict;
	}
	public static void setMentionDict(Map<String, List<Mention>> mentionDict) {
		DictBean.mentionDict = mentionDict;
	}

	
}
