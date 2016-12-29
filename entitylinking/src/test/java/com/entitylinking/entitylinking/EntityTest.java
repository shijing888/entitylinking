package com.entitylinking.entitylinking;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import com.entitylinking.config.WikiConfig;
import com.entitylinking.wikidictionary.ExtractPageEntity;
import com.entitylinking.wikidictionary.GenerateDictionary;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

public class EntityTest {

	public static void main(String args[]){
		EntityTest.testEntity("michael jordan");
	}
	
	public static void testEntity(String title){
		Wikipedia wiki = WikiConfig.getWiki();
		 Page page;
		try {
			page = wiki.getPage(title);
			HashSet<String> entitySet = new HashSet<String>();
			ExtractPageEntity.pageEntity(GenerateDictionary.pattern.matcher(page.getText()),entitySet);
			for(String entity:entitySet){
				System.out.println(entity);
			}
//			Map<String,String> map = ExtractInPageEntity.inPageEntity(page.getText());
//			for(Entry<String, String> entry : map.entrySet()){
//				System.out.println(entry.getKey()+"\t"+entry.getValue());
//			}
		} catch (WikiApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
      
	}
	
	@Test
	public void test(){
		int a[] = {1,2,3,4,5,6,7,8,9};
		List<Integer> list = Arrays.asList(ArrayUtils.toObject(a));
		test1(list);
		for(int i: list){
		System.out.print(i+"\t");
	}
	}
	public  void test1(List<Integer> list){
//		int a[] = {1,2,3,4,5,6,7,8,9};
//		List<Integer> list = Arrays.asList(ArrayUtils.toObject(a));
//		for(int i: list){
//			System.out.print(i+"\t");
//		}
//		System.out.println("\n");
		Collections.sort(list, new Comparator<Integer>() {

			@Override
			public int compare(Integer arg0, Integer arg1) {
				// TODO Auto-generated method stub
				return arg1 - arg0;
			}
		});
		
//		for(int i: list){
//			System.out.print(i+"\t");
//		}
//		
//		System.out.println("\n");
//		Collections.sort(list, new Comparator<Integer>() {
//
//			@Override
//			public int compare(Integer arg0, Integer arg1) {
//				// TODO Auto-generated method stub
//				return arg0 - arg1;
//			}
//		});
//		
//		for(int i: list){
//			System.out.print(i+"\t");
//		}
	}
	
}
