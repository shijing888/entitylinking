package com.entitylinking.candidate;


import com.entitylinking.linking.bean.Text;
import com.entitylinking.utils.NLPUtils;

/**
 * 利用Stanford coreNLP工具包获取文本中出现的实体
 * @author shijing
 *
 */
public class GenerateMentions {

	public static void main(String args[]){
		
	}
	
	/**
	 * 获取一篇文档中的所有mention及上下文
	 * @param text
	 */
	public static void obtainTextMention(Text text){
		
		NLPUtils.processTextTask(text);
	}
	
}
