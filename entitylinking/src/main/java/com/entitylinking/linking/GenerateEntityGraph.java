package com.entitylinking.linking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.entitylinking.candidate.GenerateMentions;
import com.entitylinking.linking.bean.Entity;
import com.entitylinking.linking.bean.EntityGraph;
import com.entitylinking.linking.bean.Mention;
import com.entitylinking.linking.bean.Text;

/**
 * 用于生成一个密度子图
 * @author HP
 *
 */
public class GenerateEntityGraph {

	/**
	 * 构造关于该篇文档的密度子图
	 * @return
	 */
	public EntityGraph generateDensityGraph(Text text){
		//生成候选过程
		List<Mention> mentionList = GenerateMentions.obtainTextMention(text.getContent());
		EntityGraph entityGraph = new EntityGraph();
		/*该图中所有的实体*/
		List<Entity> entities = new ArrayList<Entity>();
		Map<String, Integer> entityIndex = new HashMap<String, Integer>();
		for(Mention mention:mentionList){
			entityIndex.put(mention.getMentionName(), entities.size());
			entities.addAll(mention.getCandidateEntity());
		}
		
		entityGraph.setEntities(entities);
		entityGraph.setEntityIndex(entityIndex);
		
		return entityGraph;
	}
	
	/**
	 * 初始化文档的偏好向量
	 * @param entityGraph
	 * @return
	 */
	public void initDocumentPreferenceVector(){
//		double value = 0;
//		this.preferVectorOfDocument = new double[entityLen];
//		for(int i=0;i<entityLen;i++){
//			value = entityGraph.getMentionMap().get(entityGraph.getEntities().get(i)).getTfidfValue() 
//					* entityGraph.getEntities().get(i).getPopularity();
//			this.preferVectorOfDocument[i] = value;
		
		
	}
}
