package com.entitylinking.candidate.ner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.entitylinking.utils.FileUtils;
import com.entitylinking.utils.NormalizeMention;
import com.entitylinking.utils.Parameters;

import difflib.StringUtills;

/**
 * 生成候选的main函数入口
 * @author HP
 *
 */
public class CandidateMain {

	static Logger logger = Logger.getLogger(CandidateMain.class);
	static{
		PropertyConfigurator.configure("log4j.properties");
	}
	public CandidateMain(){
		initDict();
	}
	public static void main(String args[]){
		CandidateMain candidateMain = new CandidateMain();
		candidateMain.initDict();
		String path = "./data/ace2004/RawTexts/chtb_165.eng";
		 candidateMain.candidatesOfText(path);
		
	}
	
	/**
	 * 获取一篇文本的所有mention的候选实体
	 * @param text
	 * @return
	 */
	public Map<String,List<String>> candidatesOfText(String path){
		Map<String, List<String>> candidateMap = new HashMap<>();
		Map<String,List<Integer>> mentionsMap = GenerateNER.obtainNER(FileUtils.readFileContent(path));
		
		for(String mention:mentionsMap.keySet()){
			mention = NormalizeMention.getNormalizeMention(mention,true);
			List<String> candidateList = GenerateCandidate.obtainCandidate(mention);
			candidateMap.put(mention, candidateList);
			logger.info(mention+" candidates size:"+ candidateList.size());
			logger.info(mention+" candidates are:"+ StringUtils.join(candidateList, "\t"));
		}
		return candidateMap;
	}
	
	/**
	 * 用于加载同义歧义词典
	 */
	public void initDict(){
		Parameters parameters = new Parameters();
		parameters.loadDictFromXML("./xml/dictPath.xml");
	}
}
