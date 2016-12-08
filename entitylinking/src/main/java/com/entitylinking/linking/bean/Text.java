package com.entitylinking.linking.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.search.BooleanClause;

import com.entitylinking.lucene.IndexFile;
import com.entitylinking.task.Main;
import com.entitylinking.utils.NLPUtils;
import com.entitylinking.utils.NormalizeMention;

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
		//生成mention的候选实体与上下文过程
		NLPUtils.getTextMentionTask(this);
		long time2 = System.currentTimeMillis();
		logger.info("获得mention的候选实体与上下文共花费:"+(time2 - time1)/60000.0);
		/*该图中所有的实体*/
		List<Entity> entityList = new ArrayList<>();
		Set<String> entityNameSet = new HashSet<>();
		for(Mention mention:this.entityGraph.getMentions()){
			for(Entity entity:mention.getCandidateEntity()){
				if(!entityNameSet.contains(entity.getEntityName())){
					entityList.add(entity);
					entityNameSet.add(entity.getEntityName());
				}
			}
		}
		logger.info("before extending entityList size:"+entityList.size());
		System.gc();
//		int entityLen = extendEntity(entityList,entityNameSet);
		int entityLen = entityList.size();
		this.entityGraph.setEntityLen(entityLen);
		Entity[] entities = new Entity[entityLen];
		Map<String, Integer> entityIndex = new HashMap<String, Integer>();
		int index = 0;
		List<Entity> candidateEntity;
		for(Mention mention:this.entityGraph.getMentions()){
			candidateEntity = mention.getCandidateEntity();
			mention.setTotalPopularity();
			for(int i=0;i<candidateEntity.size() && index<entityLen;i++,index++){
				entityIndex.put(candidateEntity.get(i).getEntityName(), index);
				entities[index] = candidateEntity.get(i);
			}
		}
		logger.info("after extending entities size:"+entityLen);
		logger.info("init entityGraph eitities");
		//初始化实体图
		this.entityGraph.setEntities(entities);
		this.entityGraph.setEntityIndex(entityIndex);
		logger.info("init entityGraph disambiguationMap");
		boolean isDisAmbiguation = this.entityGraph.initDisambiguationMap();
		this.entityGraph.setPreferVectorOfDocument(isDisAmbiguation);
		long time3 = System.currentTimeMillis();
		logger.info("初始化实体、实体索引、初始mention-entityMap、文档偏好向量共花费:"+(time3 - time2)/60000.0);
		logger.info("init entityGraph transferMatrix");
		//计算转移矩阵
		this.entityGraph.calTransferMatrix();
		long time4 = System.currentTimeMillis();
		logger.info("计算转移概率矩阵花费:"+(time4 - time3)/60000.0);
	}

	/**
	 * 对实体图进行一阶扩展
	 * @param entities
	 * @return
	 */
	public int extendEntity(List<Entity>entities,Set<String>entityNameSet){
		int len = entities.size();
		if(len <= 1){
			return len;
		}
		List<Entity> entityList = new ArrayList<Entity>(entities);
		BooleanClause.Occur[] flags=new BooleanClause.Occur[]{BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};
		String[] queryFields = {RELRWParameterBean.getEntityRelationField3(),
				RELRWParameterBean.getEntityRelationField3()};
		String indexDir = PathBean.getEntityRelationPath();
		Entity entity = new Entity();
		int popularityThresh = RELRWParameterBean.getPopularityThresh();
		double entityPopularity;
		long time1,time2,time3,time4;
//测试使用
len = 11;		
		for(int i=0;i<len-1;i++){
			time1 = System.currentTimeMillis();
			for(int j=i+1;j<len;j++){
				String[] querys = new String[]{entityList.get(i).getEntityName(),
						entityList.get(j).getEntityName()};
				time3 = System.currentTimeMillis();
				Set<String> set = IndexFile.coocurenceEntities(querys, queryFields, flags, indexDir);
				time4 = System.currentTimeMillis();
				if(j == 1){
					logger.info("查询共现花费时间:"+(time4 - time3));
				}
				logger.info(i+"\t"+j+"\t的共现实体有:"+set.size());
				time3 = System.currentTimeMillis();
				for(String item:set){
					item = NormalizeMention.getNormalizeMention(item, true);
					if(!entityNameSet.contains(item)){
						entityPopularity = entity.getEntityPopularity(item); 
						if(entityPopularity > popularityThresh){
							Entity entity2 = new Entity();
							entity2.setEntityName(item);
							entities.add(entity2);
						}
						entityNameSet.add(item);
					}
				}
				time4 = System.currentTimeMillis();
				logger.info(i+"\t"+j+"\t共现实体查询流行度花费时间:"+(time4 - time3)/60000.0);
			}
			time2 = System.currentTimeMillis();
			logger.info("i = "+i + "花费时间:" + (time2 - time1)/60000.0);
			logger.info("i = "+i + "实体总数:" + entityList.size());
		}
		
		return entities.size();
	}
	
}
