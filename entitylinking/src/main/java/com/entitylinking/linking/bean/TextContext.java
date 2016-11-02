package com.entitylinking.linking.bean;

import java.util.List;

/**
 * 文档上下文
 * @author HP
 *
 */
public class TextContext {

	/**保存一篇文档中的有效词*/
	private static List<String> contextWords;
	/**保存词在文档中的位置*/
	private static List<Integer>contextWordsIndex;
	public static List<String> getContextWords() {
		return contextWords;
	}
	public static void setContextWords(List<String> contextWords) {
		TextContext.contextWords = contextWords;
	}
	public static List<Integer> getContextWordsIndex() {
		return contextWordsIndex;
	}
	public static void setContextWordsIndex(List<Integer> contextWordsIndex) {
		TextContext.contextWordsIndex = contextWordsIndex;
	}
	
}
