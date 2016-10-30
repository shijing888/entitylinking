package com.entitylinking.entitylinking;

import java.util.HashSet;

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
}
