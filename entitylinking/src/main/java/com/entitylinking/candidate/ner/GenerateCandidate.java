package com.entitylinking.candidate.ner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import com.entitylinking.candidate.bean.DictBean;

/**
 * 生成候选实体
 * @author shijing
 *
 */
public class GenerateCandidate {
	
	private static Logger logger = Logger.getLogger(GenerateCandidate.class);
	private static String disambiguationStr = "(disambiguation)";
	public static void main(String args[]){
		
	}
	/**
	 * 查找mention对应的候选实体
	 * @param mention
	 * @return
	 */
	public static List<String> obtainCandidate(String mention){
		Set<String> candidateSet = new HashSet<>();
		//先从同义词典中寻找
		if(DictBean.getSynonymsDict().containsKey(mention))
			candidateSet.add(DictBean.getSynonymsDict().get(mention));
		//再从歧义词典中寻找
		if(DictBean.getAmbiguationDict().containsKey(mention))
			candidateSet.addAll(DictBean.getAmbiguationDict().get(mention));
		//若候选本身为歧义，则将其歧义项加进来
		
		for(String candidate : new ArrayList<>(candidateSet)){
			if(DictBean.getAmbiguationDict().containsKey(candidate)){
				candidateSet.addAll(DictBean.getAmbiguationDict().get(candidate));
				if(candidate.contains(disambiguationStr))
					candidateSet.remove(candidate);
			}
		}
		logger.info("候选实体个数为:"+candidateSet.size());
		return new ArrayList<>(candidateSet);
	}
}
