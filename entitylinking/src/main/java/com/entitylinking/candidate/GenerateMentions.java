package com.entitylinking.candidate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.entitylinking.linking.bean.Mention;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

/**
 * 利用Stanford coreNLP工具包获取文本中出现的实体
 * @author shijing
 *
 */
public class GenerateMentions {

	//存放用于发现实体的类别
	private static String[] nerCategory = new String[]{"PERSON","LOCATION","ORGANIZATION","DATE"};
	private static CandidateMain candidateMain =CandidateMain.getCandidateMain();
	
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
			List<Mention> mentions = obtainTextMention(text.toString());
			for(Mention mention:mentions){
				System.out.println(mention);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 获取一篇文档中的所有mention
	 * @param text
	 * @return
	 */
	public static List<Mention> obtainTextMention(String text){
		List<String> mentionList = new ArrayList<String>();
		List<Mention> mentions = new ArrayList<>();
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
        	//初始化mention对象
        	Mention mentionObj = new Mention(mention);
        	mentionObj.setMentionIndex(mentionIndex);
        	mentionObj.setCandidateEntity(candidateMain.candidatesOfMention(mention));
        	mentions.add(mentionObj);
        }
        
        return mentions;
	} 
	
}
