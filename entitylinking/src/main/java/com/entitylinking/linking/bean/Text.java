package com.entitylinking.linking.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.entitylinking.utils.CommonUtils;
import com.entitylinking.utils.NLPUtils;

/**
 * 文本的数据结构
 * @author HP
 *
 */
public class Text {
	/**文档名称*/
	private String textName;
	/**文档内容*/
	private String content;
	/**文档对应的实体图*/
	private EntityGraph entityGraph;
	/**文档中上下文*/
	private Map<Integer, String> textContext;
	private List<Integer> textContextIndex;
	
	public Text(){
		entityGraph = new EntityGraph();
		textContext = new HashMap<>();
	}
	
	public Text(String textName, String content){
		this.textName = textName;
		this.content = content;
		this.entityGraph = new EntityGraph();
		this.textContext = new HashMap<>();
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
	
	public Map<Integer, String> getTextContext() {
		return textContext;
	}

	public void setTextContext(Map<Integer, String> textContext) {
		this.textContext = textContext;
	}

	public List<Integer> getTextContextIndex() {
		return textContextIndex;
	}

	public void setTextContextIndex() {
		this.textContextIndex = new ArrayList<>(this.textContext.keySet());
		CommonUtils.sortList(this.textContextIndex, false);
	}

	/**
	 * 构造关于该篇文档的密度子图
	 * @return
	 */
	public void generateDensityGraph(){
		//生成mention和上下文过程
		NLPUtils.getTextMentionTask(this);
		/*该图中所有的实体*/
		int entityLen = this.getEntityGraph().getEntityLen(); 
		Entity[] entities = new Entity[entityLen];
		Map<String, Integer> entityIndex = new HashMap<String, Integer>();
		int index = 0;
		List<Entity> candidateEntity;
		for(Mention mention:this.entityGraph.getMentions()){
			candidateEntity = mention.candidatesOfMention(mention.getMentionName());
			for(int i=0;i<candidateEntity.size() && index<entityLen;i++,index++){
				entityIndex.put(candidateEntity.get(i).getEntityName(), i);
				entities[index] = candidateEntity.get(i);
			}
		}
		//初始化实体图
		this.entityGraph.setEntities(entities);
		this.entityGraph.setEntityIndex(entityIndex);
		//计算转移矩阵
		this.entityGraph.calTransferMatrix();
	}
	
}
