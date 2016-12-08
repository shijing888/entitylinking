package com.entitylinking.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.entitylinking.linking.bean.Entity;
import com.entitylinking.linking.bean.Mention;
import com.entitylinking.linking.bean.RELRWParameterBean;

public class CommonUtils {

	/**
	 * 计算tfidf值
	 * @param tf，词频
	 * @param df，文档频
	 * @param len，文档长度
	 * @return
	 */
	public static double calTfidf(int tf,int df,int len){
		return (double)tf/len * (Math.log(RELRWParameterBean.getTotalDocument()/(double)df) + 1);
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
	
	/**
	 * 对list按照实体的流行度排序
	 * @param list
	 * @param isDesc
	 */
	public static void sortListByPopularity(List<Entity> list, Boolean isDesc){
		//对候选实体按照流行度降序排序
		Collections.sort(list, new Comparator<Entity>() {
			//a>b升序，b>a降序
			@Override
			public int compare(Entity entity1, Entity entity2) {
				// TODO Auto-generated method stub
				if(isDesc){
					return (int)(entity2.getPopularity() - entity1.getPopularity());
				}else{
					return (int)(entity1.getPopularity() - entity2.getPopularity());
				}
			}
		});
	}
	
	/**
	 * 对候选实体按照上下文相似性降序排序
	 * @param list
	 * @param isDesc
	 */
	public static void sortListByContextSimliarity(List<Entity> list,Boolean isDesc){
		//对候选实体按照上下文相似性降序排序
		Collections.sort(list, new Comparator<Entity>() {
			//a>b升序，b>a降序
			@Override
			public int compare(Entity entity1, Entity entity2) {
				// TODO Auto-generated method stub
				if(isDesc){
					return (int)(entity2.getScore() - entity1.getScore());
				}else{
					return (int)(entity1.getScore() - entity2.getScore());
				}
			}
		});
	}
	
	/**
	 * 统计mention与候选实体上下文中出现的相同词的个数
	 * @param entity
	 * @return
	 */
	public static int commonWords(Mention mention,Entity entity){
		int count = 0;
		for(String item:entity.getEntityContext()){
			if(mention.getMentionContext().contains(item)){
				count++;
			}
		}
		return count;
	}
}
