package com.entitylinking.linking.bean;

import java.util.List;

/**
 * 文档上下文
 * @author HP
 *
 */
public class TextContext {

	/**上下文*/
	List<Integer> contextIndex;
	List<String> contextMention;
	public List<Integer> getContextIndex() {
		return contextIndex;
	}
	public void setContextIndex(List<Integer> contextIndex) {
		this.contextIndex = contextIndex;
	}
	public List<String> getContextMention() {
		return contextMention;
	}
	public void setContextMention(List<String> contextMention) {
		this.contextMention = contextMention;
	}
	
	
	
}
