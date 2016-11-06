package com.entitylinking.linking.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 文档上下文
 * @author HP
 *
 */
public class TextContext {

	/**上下文map*/
	Map<Integer, String> context;
	Map<String, Double> wordTfidf;
	
	public TextContext(){
		context = new HashMap<Integer, String>();
		wordTfidf = new HashMap<>();
	}
	public Map<Integer, String> getContext() {
		return context;
	}
	public Map<String, Double> getWordTfidf() {
		return wordTfidf;
	}
	public void setWordTfidf(Map<String, Double> wordTfidf) {
		this.wordTfidf = wordTfidf;
	}
	public void setContext(Map<Integer, String> context) {
		this.context = context;
	}
	
}
