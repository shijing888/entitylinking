package com.entitylinking_dbpedia.linking.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.search.BooleanClause;

import com.entitylinking_dbpedia.lucene.IndexFile;
import com.entitylinking_dbpedia.task.Main;
import com.entitylinking_dbpedia.utils.NLPUtils;

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
		Map<String, Integer> entityIndex = new HashMap<String, Integer>();
		List<Entity> candidateEntity;
		String entityName;
		double averageTfidfOfMentions = 0;
		//获得候选实体list
		for(Mention mention:this.entityGraph.getMentions()){
			candidateEntity = mention.getCandidateEntity();
			mention.setTotalPopularity();
			averageTfidfOfMentions += mention.getTfidfValue();
			for(int i=0;i<candidateEntity.size();i++){
				entityName = candidateEntity.get(i).getEntityName();
				if(!entityNameSet.contains(entityName)){
					entityList.add(candidateEntity.get(i));
					entityNameSet.add(entityName);
				}
			}
			
		}
		int mentionsLen =  this.getEntityGraph().getMentions().size();
		if(mentionsLen>0)
			averageTfidfOfMentions /= mentionsLen;
		else
			averageTfidfOfMentions = 0;
		this.entityGraph.setAverageTfidf(averageTfidfOfMentions);
		logger.info("before extending entityList size:"+entityList.size());
		//对候选实体进行扩展，构成实体图
		HashSet<String> unAmbiguaSet = DictBean.getUnAmbiguaDict().get(textName);
		if(unAmbiguaSet==null)
			unAmbiguaSet = new HashSet<>();
		int[] lens = extendEntity(unAmbiguaSet,entityList,entityNameSet);
		this.entityGraph.setEntityLen(lens[1]);
		this.entityGraph.setCandidateEntityLen(lens[0]);
		this.entityGraph.setUnAmbiguaSet(unAmbiguaSet);
		Entity[] entities = new Entity[lens[1]];
		//将list转化成数组保存实体
		for(int i=0;i<lens[1];i++){
			if(i<lens[0]){
				entityIndex.put(entityList.get(i).getEntityName(), i);
			}
			entities[i] = entityList.get(i);
		}
		
		logger.info("after extending entities size:"+lens[1]);
		logger.info("init entityGraph eitities");
		//初始化实体图
		this.entityGraph.setEntities(entities);
		this.entityGraph.setEntityIndex(entityIndex);
		logger.info("init entityGraph disambiguationMap");
		this.entityGraph.initDisambiguationMap();
		this.entityGraph.setPreferVectorOfDocument(true);
		long time3 = System.currentTimeMillis();
		logger.info("初始化实体、实体索引、初始mention-entityMap、文档偏好向量共花费:"+(time3 - time2)/60000.0);
		logger.info("init entityGraph transferMatrix");
		//利用共现计算转移矩阵
		this.entityGraph.calTransferMatrix();
		//利用卡茨关联性计算转移矩阵
//		this.entityGraph.calTransferMatrixOfKatzPath();
		//利用排他性计算转移矩阵
//		this.entityGraph.calTransferMatrixOfExclusivityPath();
		//利用流行度计算转移矩阵
//		this.entityGraph.calTransferMatrixOfPopularityPath();
		long time4 = System.currentTimeMillis();
		logger.info("计算转移概率矩阵花费:"+(time4 - time3)/60000.0);
	}

	/**
	 * 对实体图进行一阶扩展
	 * @param entities
	 * @return
	 */
	public int[] extendEntity(HashSet<String> unAmbiguaSet,List<Entity>entities,Set<String>entityNameSet){
		int lens[] = new int[2];
		int len = entities.size();
		List<Entity> entityList = new ArrayList<Entity>(entities);
		//140-148用于扩展从文本中发现的无歧义实体
		for(String str:unAmbiguaSet){
			if(str == null)
				continue;
			if(!entityNameSet.contains(str)){
				Entity entity = new Entity();
				entity.setEntityName(str);
				entities.add(entity);
				entityNameSet.add(str);
			}
		}
		
		lens[0] = entities.size();
		BooleanClause.Occur[] flags=new BooleanClause.Occur[]{
				BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};
		String[] queryFields = {RELRWParameterBean.getEntityRelationField1(),
				RELRWParameterBean.getEntityRelationField2()};
		String indexDir = PathBean.getEntityByDbpediaRelationPath();
		int popularityThresh = RELRWParameterBean.getPopularityThresh();
		long time1,time2;
		time1 = System.currentTimeMillis();
		
		for(int i=0;i<len-1;i++){
			for(int j=i+1;j<len;j++){
				String[] querys = new String[]{entityList.get(i).getEntityName(),
						entityList.get(j).getEntityName()};
				Set<String> set = IndexFile.coocurenceEntities(querys, queryFields, flags, indexDir);
				querys[0] = entityList.get(j).getEntityName();
				querys[1] = entityList.get(i).getEntityName();
				set.addAll(IndexFile.coocurenceEntities(querys, queryFields, flags, indexDir));
//				logger.info(i+"\t"+j+"\t的共现实体有:"+set.size());
				for(String item:set){
					String[] itemArray = item.split("\t");
					if(itemArray.length == 2 ){
						try {
							if(!entityNameSet.contains(itemArray[0])){
								if(Integer.parseInt(itemArray[1]) >= popularityThresh){
									Entity entity = new Entity();
									entity.setEntityName(itemArray[0]);
									entities.add(entity);
								}
							}
							entityNameSet.add(itemArray[0]);
						} catch (Exception e) {
							// TODO: handle exception
							logger.info("实体格式错误:"+item);
						}
						
					}
				}
			}
			
			
			logger.info("i = "+i + "扩展后实体总数:" + entities.size());
		}
		time2 = System.currentTimeMillis();
		logger.info("扩展实体花费时间:" + (time2 - time1)/60000.0);
		lens[1] = entities.size();
		return lens;
	}
	
}
