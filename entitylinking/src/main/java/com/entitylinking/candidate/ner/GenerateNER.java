package com.entitylinking.candidate.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import difflib.StringUtills;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

/**
 * 利用Stanford coreNLP工具包获取文本中出现的实体
 * @author shijing
 *
 */
public class GenerateNER {

	//存放用于发现实体的类别
	private static String[] nerCategory = new String[]{"PERSON","LOCATION","ORGANIZATION","DATE"};
	
	public static void main(String args[]){
		String path = "./data\\ace2004\\RawTexts\\chtb_165.eng";
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "utf-8"));
			StringBuilder text = new StringBuilder();
			String line;
			while((line=bReader.readLine())!=null){
				text.append(line);
			}
			bReader.close();
			Map<String,List<Integer>> nerMap = obtainNER(text.toString());
			for(Entry<String, List<Integer>> entry:nerMap.entrySet()){
				System.out.println(entry.getKey()+"\t"+StringUtills.join(entry.getValue(), "|"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Map<String,List<Integer>> obtainNER(String text){
		List<String> mentionList = new ArrayList<String>();
		Map<String,List<Integer>> nerMap= new HashMap<>();
		Document doc = new Document(text);
		String tempText;
		int index = 0;
        for (Sentence sent : doc.sentences()) {
        	for(String cat:nerCategory){
        		mentionList.addAll(sent.mentions(cat));
        	}
        }
        
        for(String mention:mentionList){
        	List<Integer> mentionIndex = new ArrayList<>();
        	tempText = text;
        	index = tempText.indexOf(mention);
        	while(index!=-1){
        		mentionIndex.add(index);
        		tempText = tempText.substring(index + mention.length());
        		index = tempText.indexOf(mention);
        	
        	}
        	nerMap.put(mention, mentionIndex);
        }
        return nerMap;
	} 
}
