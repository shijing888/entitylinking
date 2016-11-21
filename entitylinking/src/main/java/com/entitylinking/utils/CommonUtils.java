package com.entitylinking.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	
	/**
	 * 对list排序
	 * @param list
	 * @param isDesc
	 */
	public static void sortList(List<Integer> list, Boolean isDesc){
		Collections.sort(list, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				if(isDesc){
					return (o2 - o1);
				}else{
					return (o1 - o2);
				}
			}
			
		});
	}
	
}
