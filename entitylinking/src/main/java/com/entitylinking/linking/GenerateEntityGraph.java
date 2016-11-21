package com.entitylinking.linking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.entitylinking.linking.bean.Entity;
import com.entitylinking.linking.bean.Mention;
import com.entitylinking.linking.bean.Text;
import com.entitylinking.utils.NLPUtils;

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
	public void generateDensityGraph(Text text){
		//生成mention和上下文过程
		NLPUtils.getTextMentionTask(text);
		/*该图中所有的实体*/
		List<Entity> entities = new ArrayList<Entity>();
		Map<String, Integer> entityIndex = new HashMap<String, Integer>();
		for(Mention mention:text.getEntityGraph().getMentions()){
			entityIndex.put(mention.getMentionName(), entities.size());
			entities.addAll(mention.getCandidateEntity());
		}
		//初始化实体图
		text.getEntityGraph().setEntities(entities);
		text.getEntityGraph().setEntityIndex(entityIndex);
		text.getEntityGraph().setEntityLen();
		
		
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
