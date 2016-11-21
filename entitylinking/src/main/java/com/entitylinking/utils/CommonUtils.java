package com.entitylinking.utils;

public class CommonUtils {

	/**
	 * 计算tfidf值
	 * @param tf，词频
	 * @param df，文档频
	 * @param len，文档长度
	 * @return
	 */
	public static double calTfidf(int tf,int df,int len){
		return tf * (Math.log(len/(double)df) + 1);
	}
}
