package com.entitylinking_dbpedia.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.entitylinking_dbpedia.linking.bean.Entity;
import com.entitylinking_dbpedia.linking.bean.Mention;
import com.entitylinking_dbpedia.linking.bean.RELRWParameterBean;

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
	 * 对list按字面量相似性排序
	 * @param list
	 * @param isDesc
	 */
	public static void sortListByEditDistance(String str,List<Entity> list, Boolean isDesc){
		int len = list.size();
		Map<String, Integer> editDistanceMap = new HashMap<String, Integer>();
		int editDistance = 0;
		String entityName;
		for(int i=0;i<len;i++){
			entityName = list.get(i).getEntityName();
			editDistance = EditDistance.getEditDistance(str, entityName);
			editDistanceMap.put(entityName, editDistance);
//			System.out.println(entityName+"的编辑距离为:"+editDistance);
		}
		//对候选实体按照流行度降序排序
		Collections.sort(list, new Comparator<Entity>() {
			//a>b升序，b>a降序
			@Override
			public int compare(Entity entity1, Entity entity2) {
				// TODO Auto-generated method stub
				if(isDesc){
					return editDistanceMap.get(entity2.getEntityName()) 
							- editDistanceMap.get(entity1.getEntityName());
				}else{
					return editDistanceMap.get(entity1.getEntityName()) 
							- editDistanceMap.get(entity2.getEntityName());
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
		Set<String> entityContext = entity.getEntityContext();
		
		if(entityContext != null){
			for(String item:entity.getEntityContext()){
				if(mention.getMentionContext().contains(item)){
					count++;
				}
			}
		}
		
		return count;
	}
	
	/**
	 * 将文件中出现的连接符"-"替换为"_"
	 * @param rpath
	 * @param wpath
	 */
	public static void replaceConnector(String rpath,String wpath){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(rpath)), "utf-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(wpath)), "utf-8"));
			
			String line;
			while((line = reader.readLine()) != null){
				line = line.replace("-", "_");
				writer.write(line + "\n");
			}
			
			reader.close();
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 将字符串中出现的连接符"-"替换为"_"
	 * @param rpath
	 * @param wpath
	 */
	public static String replaceConnector(String line){
		return line.replace("-", "_");
	}
	
	/**
	 * 杰卡德相似性计算两个集合的相似性
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static double jaccardOfSet(Set<String> set1,Set<String>set2){
		
		if(set1.isEmpty() || set2.isEmpty()){
			return 0;
		}else{
			int commonCount = 0;
			int allCount = 0;
			for(String str:set1){
				if(set2.contains(str)){
					commonCount++;
				}
				allCount++;
			}
			if(commonCount == 0)
				return 0;
			for(String str:set2){
				if(!set1.contains(str)){
					allCount++;
				}
			}
			return (double)commonCount / allCount;
		}
	}
	public static void main(String args[]){
		String men = "abc";
		String e1 = "abcd";
		String e2 = "abcde";
		String e3 = "abcdef";
		String e4 = "abcdefg";
		String e5 = "cba";
		String e6 = "abc";
		List<Entity> list = new ArrayList<Entity>();
		list.add(new Entity(e1));
		list.add(new Entity(e2));
		list.add(new Entity(e3));
		list.add(new Entity(e4));
		list.add(new Entity(e5));
		list.add(new Entity(e6));
		for(Entity entity:list){
			System.out.println(entity.getEntityName());
		}
		System.out.println("------------------------");
		sortListByEditDistance(men, list, false);
		for(Entity entity:list){
			System.out.println(entity.getEntityName());
		}
	}
}
