package com.entitylinking.candidate.ner;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.entitylinking.utils.NormalizeMention;
import com.entitylinking.utils.Parameters;

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
	public static void main(String args[]){
		CandidateMain candidateMain = new CandidateMain();
		candidateMain.initDict();
		String mention = "MJ";
		mention = NormalizeMention.getNormalizeMention(mention,true);
		List<String> candidateList = GenerateCandidate.obtainCandidate(mention);
		logger.info(mention+" candidates size"+ candidateList.size());
		logger.info(mention+" candidates are:"+ StringUtils.join(candidateList, "\t"));
	}
	
	/**
	 * 获取一篇文本的所有mention的候选实体
	 * @param text
	 * @return
	 */
	public List<String> candidatesOfText(String text){
		return null;
	}
	
	/**
	 * 用于加载同义歧义词典
	 */
	public void initDict(){
		Parameters parameters = new Parameters();
		parameters.loadDictFromXML("./xml/dictPath.xml");
	}
}
