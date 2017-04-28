package com.entitylinking_dbpedia.linking.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.entitylinking_dbpedia.linking.bean.Mention;

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
	private static Map<String, String> specialWordsDict;
	private static Map<String, List<Mention>> mentionDict;
	private static Map<String, HashSet<String>> entityContextDict;
	private static Map<String, HashSet<String>> entityCategoryDict;
	private static Map<String, Integer> entityByDbpeidaPopularityDict;
	private static Map<String, HashSet<String>> unAmbiguaDict;
	
	public static Map<String, HashSet<String>> getUnAmbiguaDict() {
		return unAmbiguaDict;
	}
	public static void setUnAmbiguaDict(Map<String, HashSet<String>> unAmbiguaDict) {
		DictBean.unAmbiguaDict = unAmbiguaDict;
	}
	public static Map<String, Integer> getEntityByDbpeidaPopularityDict() {
		return entityByDbpeidaPopularityDict;
	}
	public static void setEntityByDbpeidaPopularityDict(Map<String, Integer> entityByDbpeidaPopularityDict) {
		DictBean.entityByDbpeidaPopularityDict = entityByDbpeidaPopularityDict;
	}
	public static Map<String, HashSet<String>> getEntityContextDict() {
		return entityContextDict;
	}
	public static void setEntityContextDict(Map<String, HashSet<String>> entityContextDict) {
		DictBean.entityContextDict = entityContextDict;
	}
	public static Map<String, HashSet<String>> getEntityCategoryDict() {
		return entityCategoryDict;
	}
	public static void setEntityCategoryDict(Map<String, HashSet<String>> entityCategoryDict) {
		DictBean.entityCategoryDict = entityCategoryDict;
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
	public static Map<String, String> getSpecialWordsDict() {
		return specialWordsDict;
	}
	public static void setSpecialWordsDict(Map<String, String> specialWordsDict) {
		DictBean.specialWordsDict = specialWordsDict;
	}
	public static Map<String, List<Mention>> getMentionDict() {
		return mentionDict;
	}
	public static void setMentionDict(Map<String, List<Mention>> mentionDict) {
		DictBean.mentionDict = mentionDict;
	}

}
