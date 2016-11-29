package com.entitylinking.linking.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.entitylinking.task.Main;
import com.entitylinking.utils.NLPUtils;

/**
 * 文本的数据结构
 * @author HP
 *
 */
public class Text {
	static Logger logger = Logger.getLogger(Main.class);
	/**文档名称*/
	private String textName;
	/**文档内容*/
	private String content;
	/**文档对应的实体图*/
	private EntityGraph entityGraph;
	public Text(){
		entityGraph = new EntityGraph();
	}
	
	public Text(String textName, String content){
		this.textName = textName;
		this.content = content;
		this.entityGraph = new EntityGraph();
	}
	
	public String getTextName() {
		return textName;
	}
	public void setTextName(String textName) {
		this.textName = textName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public EntityGraph getEntityGraph() {
		return entityGraph;
	}
	public void setEntityGraph(EntityGraph entityGraph) {
		this.entityGraph = entityGraph;
	}
	
	/**
	 * 构造关于该篇文档的密度子图
	 * @return
	 */
	public void generateDensityGraph(){
		long time1 = System.currentTimeMillis();
		//生成mention和上下文过程
		NLPUtils.getTextMentionTask(this);
		long time2 = System.currentTimeMillis();
		logger.info("获得mention与上下文共花费:"+(time2 - time1)/60000.0);
		/*该图中所有的实体*/
		int entityLen = this.getEntityGraph().getEntityLen(); 
		Entity[] entities = new Entity[entityLen];
		logger.info("entities size:"+entities.length);
		Map<String, Integer> entityIndex = new HashMap<String, Integer>();
		int index = 0;
		List<Entity> candidateEntity;
		for(Mention mention:this.entityGraph.getMentions()){
			candidateEntity = mention.getCandidateEntity();
			for(int i=0;i<candidateEntity.size() && index<entityLen;i++,index++){
				entityIndex.put(candidateEntity.get(i).getEntityName(), i);
				entities[index] = candidateEntity.get(i);
			}
		}
		logger.info("init entityGraph eitities");
		//初始化实体图
		this.entityGraph.setEntities(entities);
		this.entityGraph.setEntityIndex(entityIndex);
		logger.info("init entityGraph disambiguationMap");
		this.entityGraph.initDisambiguationMap();
		this.entityGraph.setPreferVectorOfDocument();
		long time3 = System.currentTimeMillis();
		logger.info("初始化实体、实体索引、初始mention-entityMap、文档偏好向量共花费:"+(time3 - time2)/60000.0);
		logger.info("init entityGraph transferMatrix");
		//计算转移矩阵
		this.entityGraph.calTransferMatrix();
		long time4 = System.currentTimeMillis();
		logger.info("计算转移概率矩阵花费:"+(time4 - time3)/60000.0);
	}
	
}
