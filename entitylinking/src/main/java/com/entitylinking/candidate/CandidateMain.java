package com.entitylinking.candidate;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.entitylinking.linking.bean.Entity;
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
	private static CandidateMain candidateMain = null;
	
	private CandidateMain(){
		initDict();
	}
	
	public static void main(String args[]){
		CandidateMain candidateMain = new CandidateMain();
		candidateMain.initDict();
		candidateMain.candidatesOfMention("MJ");
		
	}
	
	/**
	 * 获取mention的候选实体
	 * @param mention
	 * @return
	 */
	public List<Entity> candidatesOfMention(String mention){
		mention = NormalizeMention.getNormalizeMention(mention,true);
		List<Entity> candidateList = GenerateCandidate.obtainCandidate(mention);
		logger.info(mention+" candidates size:"+ candidateList.size());
		logger.info(mention+" candidates are:"+ StringUtils.join(candidateList, "\t"));
		return candidateList;
	}
	
	/**
	 * 用于加载同义歧义词典
	 */
	public void initDict(){
		Parameters parameters = new Parameters();
		parameters.loadDictFromXML("./xml/dictPath.xml");
	}
	
	/**
	 * 使用单例模式返回对象
	 * @return
	 */
	public static CandidateMain getCandidateMain(){
		if(candidateMain == null){
			candidateMain = new CandidateMain();
		}
		return candidateMain;
	}
}
