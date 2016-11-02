package com.entitylinking.candidate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.entitylinking.linking.bean.DictBean;
import com.entitylinking.linking.bean.Entity;

/**
 * 生成候选实体
 * @author shijing
 *
 */
public class GenerateCandidate {
	
//	private static Logger logger = Logger.getLogger(GenerateCandidate.class);
	private static String disambiguationStr = "(disambiguation)";
	public static void main(String args[]){
		
	}
	/**
	 * 查找mention对应的候选实体
	 * @param mention
	 * @return
	 */
	public static List<Entity> obtainCandidate(String mention){
		Set<String> candidateSet = new HashSet<>();
		List<Entity> entities = new ArrayList<>();
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
		for(String entityStr:candidateSet){
			Entity entity = new Entity(entityStr);
			//为每个实体计算流行度及上下文，待完成
			entities.add(entity);
		}
		//对候选实体按照流行度降序排序
		Collections.sort(entities, new Comparator<Entity>() {
			//a>b升序，b>a降序
			@Override
			public int compare(Entity entity1, Entity entity2) {
				// TODO Auto-generated method stub
				return entity2.getPopularity() - entity1.getPopularity()>0?1:-1;
			}
			
		});
		return entities;
	}
}
