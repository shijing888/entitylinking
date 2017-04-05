package com.entitylinking_dbpedia.linking.bean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.ArrayUtils;
//import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.log4j.Logger;
//import org.apache.lucene.document.Document;

import com.entitylinking_dbpedia.lucene.IndexFile;
import com.entitylinking_dbpedia.utils.Parameters;

/**
 * 一篇文档生成一个实体子图
 * @author HP
 *
 */
public class EntityGraph {
	static Logger logger = Logger.getLogger(EntityGraph.class);
	static int MAXITERATERCOUNTS = 9999;
	/*所有实体的size*/
	private int entityLen;
	/*候选实体的size*/
	private int candidateEntityLen;
	/*该图中所有的实体*/
	private Entity[] entities;
	/*该图中所有的mention*/
	private List<Mention> mentions;
	/*用于记录每个实体在entities中的位置*/
	private Map<String, Integer> entityIndex;
	/*mention-候选实体map*/
	private Map<Mention, List<Entity>> entityMap ;
	/*文档的语义签名向量*/
	private double[] semantitcSignatureOfDocument;
	/*实体的语义签名向量*/
	private double[] semantitcSignatureOfEntity;
	/*文档的偏好向量*/
	private double[] preferVectorOfDocument;
	/*实体的偏好向量*/
	private double[] preferVectorOfEntity;
	/*实体间的转移矩阵*/
	private double[][] transferMatrix;
	/*实体图中边的权重*/
	private double[][] edgesWeights;
	/*mention-entity pair*/
	private Map<Mention, Entity> disambiguationMap;
	
	public EntityGraph(){
		this.disambiguationMap = new HashMap<Mention, Entity>();
		this.semantitcSignatureOfDocument = new double[this.entityLen];
		this.semantitcSignatureOfEntity = new double[this.entityLen];
	}
	public Map<String, Integer> getEntityIndex() {
		return entityIndex;
	}

	public List<Mention> getMentions() {
		return mentions;
	}

	public void setMentions(List<Mention> mentions) {
		this.mentions = mentions;
	}

	public int getEntityLen() {
		return entityLen;
	}

	public void setEntityLen(int len) {
		this.entityLen = len;
	}

	public int getCandidateEntityLen() {
		return candidateEntityLen;
	}
	public void setCandidateEntityLen(int candidateEntityLen) {
		this.candidateEntityLen = candidateEntityLen;
	}
	public Map<Mention, Entity> getDisambiguationMap() {
		return disambiguationMap;
	}
	public void setDisambiguationMap(Map<Mention, Entity> disambiguationMap) {
		this.disambiguationMap = disambiguationMap;
	}
	public void setEntityIndex(Map<String, Integer> entityIndex) {
		this.entityIndex = entityIndex;
	}

	public Map<Mention, List<Entity>> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<Mention, List<Entity>> entityMap) {
		this.entityMap = entityMap;
	}

	public double[] getSemantitcSignatureOfDocument() {
		return semantitcSignatureOfDocument;
	}

	public void setSemantitcSignatureOfDocument(double[] semantitcSignatureOfDocument) {
		this.semantitcSignatureOfDocument = semantitcSignatureOfDocument;
	}

	public double[] getSemantitcSignatureOfEntity() {
		return semantitcSignatureOfEntity;
	}
	public void setSemantitcSignatureOfEntity(double[] semantitcSignatureOfEntity) {
		this.semantitcSignatureOfEntity = semantitcSignatureOfEntity;
	}
	public double[] getPreferVectorOfDocument() {
		return preferVectorOfDocument;
	}

	/**
	 * 设置文档的偏好向量
	 * @param isAmbiguation,两种不同的初始化形式,若无歧义，则先验设为1
	 */
	public void setPreferVectorOfDocument(boolean isDisAmbiguation) {
		if(this.preferVectorOfDocument == null){
			this.preferVectorOfDocument = new double[this.entityLen];
		}
		double important = 0;
		double polularity;
		double totalPopularity;
		int index;
		if(isDisAmbiguation){
			for(Entry<Mention, Entity>entry:this.disambiguationMap.entrySet()){
				if(entry.getValue() != null){
					important = entry.getKey().getTfidfValue();
//					polularity = 1;
					index = this.entityIndex.get(entry.getValue().getEntityName());
					this.preferVectorOfDocument[index] = important;
				}
			}
		}else{
			for(Entry<Mention, Entity>entry:this.disambiguationMap.entrySet()){
				if(entry.getValue() != null){
					totalPopularity = entry.getKey().getTotalPopularity();
					logger.info(entry.getKey().getMentionName()+"的总流行度:"+totalPopularity);
					important = entry.getKey().getTfidfValue();
					index = this.entityIndex.get(entry.getValue().getEntityName());
					if(totalPopularity == 0){
						this.preferVectorOfDocument[index] = important;
					}else{
						polularity = entry.getValue().getPopularity() / totalPopularity;
						this.preferVectorOfDocument[index] = important * polularity;
					}
					
				}
			}
		}
		logger.info("important:"+important);
		logger.info("文档的偏好向量为:" + StringUtils.join(ArrayUtils.toObject(preferVectorOfDocument), "\t"));
	}

	/**
	 * 对文档语义向量进行更新
	 * @param mention
	 * @param entity
	 */
	public void updatePreferVectorOfDocument(Mention mention, Entity entity){
		if(entity!=null && !entity.getEntityName().equals(RELRWParameterBean.getNil())){
			int index = this.entityIndex.get(entity.getEntityName());
			double important = mention.getTfidfValue();
			this.preferVectorOfDocument[index] = important;
		}
	
	}
	
	public double[] getPreferVectorOfEntity() {
		return preferVectorOfEntity;
	}

	/**
	 * 设置实体的偏好向量
	 * @param index
	 */
	public void setPreferVectorOfEntity(int index) {
		this.preferVectorOfEntity = new double[this.entityLen];
		this.preferVectorOfEntity[index] = 1;
	}

	public double[][] getEdgesWeights() {
		return edgesWeights;
	}

	public void setEdgesWeights(double[][] edgesWeights) {
		this.edgesWeights = edgesWeights;
	}

	public Entity[] getEntities() {
		return entities;
	}
	public void setEntities(Entity[] entities) {
		this.entities = entities;
	}
	
	public double[][] getTransferMatrix() {
		return transferMatrix;
	}
	
	/**
	 * 初始化无歧义mention-entity对，用于计算文档的语义签名
	 */
	public boolean initDisambiguationMap(){
		boolean isDisAmbiguation = true;
		this.disambiguationMap = new HashMap<>();
		for(Mention mention:this.mentions){
			if(mention.getCandidateEntity().size() == 1){
				this.disambiguationMap.put(mention, mention.getCandidateEntity().get(0));
			}
		}
		//若没有无歧义对，则将歧义性最低的给加入到map中
		if(this.disambiguationMap.size() == 0){
			isDisAmbiguation = false;
			for(Mention mention:this.mentions){
				if(!mention.getCandidateEntity().isEmpty()){
					this.disambiguationMap.put(mention, mention.getCandidateEntity().get(0));
					return isDisAmbiguation;
				}
			}
			
		}
		
		return isDisAmbiguation;
	}
	
	/**
	 * 计算转移矩阵
	 */
	public void calTransferMatrix(){
		double[] weights;
		this.transferMatrix = new double[entityLen][entityLen];
		for(int i=0;i<candidateEntityLen;i++){
			logger.info("i = "+i);
			for(int j=i;j<entityLen;j++){
				weights = calEdgeWeight(entities[i], 
						entities[j], PathBean.getEntityByDbpediaRelationPath());
				transferMatrix[i][j] = weights[0];
				transferMatrix[j][i] = weights[1];
//				logger.info(i+"\t"+entities[i].getEntityName()+"\t"+j+"\t"+entities[j].getEntityName()
//						+"\t转移权重:"+weights[0]+"\t"+weights[1]);
			}
		}
		
		//将转移概率做归一化处理
		double[] sum = new double[entityLen];
		for(int i=0;i<entityLen;i++){
			for(int j=0;j<entityLen;j++){
				sum[j] += transferMatrix[i][j];
			}
		}
		for(int i=0;i<entityLen;i++){
			for(int j=0;j<entityLen;j++){
				if(sum[j] > 0){
					transferMatrix[i][j] /= sum[j];
				}else{
					transferMatrix[i][j] = 0;
				}
				
			}
		}
	}
	
	/**
	 * 计算实体图中边的权重与转移概率
	 * @param entity1
	 * @param entity2
	 * @param queryField1,保存count的域
	 * @param queryField2，保存实体页中共现的其他实体
	 * @param indexDir
	 * @return
	 */
	public double[] calEdgeWeight(Entity ent1,Entity ent2, String indexDir){
		String entity1 = ent1.getEntityName();
		String entity2 = ent2.getEntityName();
		double[] weights = new double[2];
		try {
			String[] querys = new String[]{entity1,entity2};
			int count = IndexFile.entityCoocurCounts(querys,indexDir);
			querys[0] = entity2;
			querys[1] = entity1;
			count += IndexFile.entityCoocurCounts(querys,indexDir);
//			double similary = CommonUtils.jaccardOfSet(ent1.getCategory(), ent2.getCategory());
			int singleCountOfEntity1 = IndexFile.countSingleOccurence(entity1, indexDir);
			int singleCountOfEntity2 = IndexFile.countSingleOccurence(entity2, indexDir);
			if(count < RELRWParameterBean.getCooccurenceThresh() || singleCountOfEntity1 == 0
					|| singleCountOfEntity2 == 0){
//				weights[0] = similary;
//				weights[1] = similary;
				weights[0] = 0;
				weights[1] = 0;
				return weights;
			}
//			Document document = IndexFile.queryDocument(entity1, queryFields[0], indexDir);
//			String[] relateEntity = document.get(queryFields[2]).split("\t\\|\t");
//			int singleCount = 200 * relateEntity.length;
//			int outEntityCounts = Integer.parseInt(document.get(queryFields[1]));
//			int outEntityCounts = 0;
//			for(String item:relateEntity){
//				querys = new String[]{entity1,item};
//				outEntityCounts += IndexFile.countCooccurence(querys, indexDir);
//			}
//			logger.info("实体"+entity1+"与实体"+entity2+" 共现次数:"+count);
//			logger.info("与实体 "+entity1+" 有关的其他实体所有共现次数:"+outEntityCounts);
//			weights[0] = similary + count / (double)singleCountOfEntity1;
//			weights[1] = similary + count / (double)singleCountOfEntity2;
			weights[0] =  count / (double)singleCountOfEntity1;
			weights[1] =  count / (double)singleCountOfEntity2;
			return weights;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return weights;
	}
	
	
	/**
	 * 计算卡茨路径的权重矩阵
	 */
	public void calTransferMatrixOfKatzPath(){
		double[] weights;
		this.transferMatrix = new double[entityLen][entityLen];
		Parameters parameters = new Parameters();
		Map<String, Integer> labelMap = parameters.loadString2IntegerDict(
				"./data/dbpedia/numLabels_enText.ttl");
		Map<Integer, HashSet<Integer>> entityOutMap = parameters.loadEntityOutMap(
				"./data/dbpedia/entity_edge.ttl");
		logger.info("labelmap size:"+labelMap.size());
		logger.info("entityOutMap size:"+entityOutMap.size());
		for(int i=0;i<candidateEntityLen;i++){
			logger.info("i = "+i);
			for(int j=i;j<entityLen;j++){
				weights = calEdgeWeightOfKatzPath(entities[i], entities[j],labelMap,entityOutMap);
				transferMatrix[i][j] = weights[0];
				transferMatrix[j][i] = weights[1];
			}
		}
		
		//将转移概率做归一化处理
		double[] sum = new double[entityLen];
		for(int i=0;i<entityLen;i++){
			for(int j=0;j<entityLen;j++){
				sum[j] += transferMatrix[i][j];
			}
		}
		for(int i=0;i<entityLen;i++){
			for(int j=0;j<entityLen;j++){
				if(sum[j] > 0){
					transferMatrix[i][j] /= sum[j];
				}else{
					transferMatrix[i][j] = 0;
				}
				
			}
		}
	}
	
	/**
	 * 计算两个实体间的卡茨关联性
	 * @param ent1
	 * @param ent2
	 * @param labelMap
	 * @param entityOutMap
	 * @return
	 */
	public double[] calEdgeWeightOfKatzPath(Entity ent1,Entity ent2,
			Map<String, Integer> labelMap,Map<Integer, HashSet<Integer>> entityOutMap){
		double score = 0;
		double pathAlpha = RELRWParameterBean.getPathAlpha();
		List<Integer> pathList = new ArrayList<Integer>();
		double[] weights = new double[2];
		
		String name1 = ent1.getEntityName();
		String name2 = ent2.getEntityName();
		
		if(!labelMap.containsKey(name1) || !labelMap.containsKey(name2)){
			return weights;
		}
		
		int label1 = labelMap.get(name1);
		int label2 = labelMap.get(name2);
	
		getSkipKatzPath(label1, label2, entityOutMap, pathList, RELRWParameterBean.getSkipNums());
	
		if(pathList.size() > 0){
			for(Integer item : pathList){
				score += Math.pow(pathAlpha, item);
			}
			score /= pathList.size();
		}
		
		weights[0] = weights[1] = score;
		if(score > 0)
			logger.info(name1+"\t"+name2+"的katz score:"+score);
		return weights;
	}
	
	/**
	 * 获得两个实体经过若干条的路径
	 * @param label
	 * @param entityOutMap
	 * @param pathList，存放的是路径长度
	 * @param skipNum
	 */
	public void getSkipKatzPath(Integer label1,Integer label2,Map<Integer, HashSet<Integer>> entityOutMap, 
			List<Integer> pathList,int skipNum){
		HashSet<Integer> set1 = entityOutMap.get(label1);
		HashSet<Integer> set2 = entityOutMap.get(label2);
		
		if(set1 == null && set2 == null){
			return;
		}
		//直接相连
		if(set1 != null && set1.contains(label2)){
			pathList.add(1);
		}
		if(pathList.size() > RELRWParameterBean.getTopK())
			return;
		if(set2 != null && set2.contains(label1)){
			pathList.add(1);
		}
		//直接相连则返回
		if(skipNum == 0)
			return;
		
		HashSet<Integer> tempSet1 = new HashSet<>();
		HashSet<Integer> tempSet2 = new HashSet<>();
		int i = 1;
		while(i++ <= skipNum){
			if(set1 != null && !set1.isEmpty()){
				if(set1.contains(label2)){
					set1.remove(label2);
				}
				tempSet1.clear();
				for(Integer item:set1){
					if(entityOutMap.get(item) != null){
						if(entityOutMap.get(item).contains(label2)){
							if(pathList.size() < RELRWParameterBean.getTopK())
								pathList.add(i);
							else
								return;
						}
						tempSet1.addAll(entityOutMap.get(item));
					}
					
				}
				set1.clear();
				set1.addAll(tempSet1);
			}
			
			if(set2 != null && !set2.isEmpty()){
				if(set2.contains(label1)){
					set2.remove(label1);
				}
				tempSet2.clear();
				for(Integer item:set2){
					if(entityOutMap.get(item) != null){
						if(entityOutMap.get(item).contains(label1)){
							if(pathList.size() < RELRWParameterBean.getTopK())
								pathList.add(i);
							else
								return;
						}
						tempSet2.addAll(entityOutMap.get(item));
					}
					
				}
				set2.clear();
				set2.addAll(tempSet1);
			}
			
		}
		
	}
	
	
	/**
	 * 计算基于排他性路径的权重矩阵
	 */
	public void calTransferMatrixOfExclusivityPath(){
		double[] weights;
		this.transferMatrix = new double[entityLen][entityLen];
		Parameters parameters = new Parameters();
		Map<String, Integer> labelMap = parameters.loadString2IntegerDict(
				"./data/dbpedia/numLabels_enText.ttl");
		Map<Integer, HashMap<Integer, HashSet<Integer>>> entityOutTypeMap = 
				parameters.loadEntityOutTypeMap("./data/dbpedia/entity_edge.ttl");
		Map<Integer, HashMap<Integer, HashSet<Integer>>> entityInTypeMap = 
				parameters.loadEntityOutTypeMap("./data/dbpedia/entityIn_edge.ttl");
//		Map<Integer, HashMap<Integer, HashSet<Integer>>> entityInTypeMap =
//				getEntityInMap(entityOutTypeMap);
//		pickleMap("./data/dbpedia/entityIn_edge.ttl", entityInTypeMap);
		logger.info("labelmap size:"+labelMap.size());
		logger.info("entityOutTypeMap size:"+entityOutTypeMap.size());
		logger.info("entityInTypeMap size:"+entityInTypeMap.size());
		for(int i=0;i<candidateEntityLen;i++){
			logger.info("i = "+i);
			for(int j=i;j<entityLen;j++){
				weights = calEdgeWeightOfExclusivityPath(entities[i], entities[j],
						labelMap,entityOutTypeMap,entityInTypeMap);
				transferMatrix[i][j] = weights[0];
				transferMatrix[j][i] = weights[1];
			}
		}
		
		//将转移概率做归一化处理
		double[] sum = new double[entityLen];
		for(int i=0;i<entityLen;i++){
			for(int j=0;j<entityLen;j++){
				sum[j] += transferMatrix[i][j];
			}
		}
		for(int i=0;i<entityLen;i++){
			for(int j=0;j<entityLen;j++){
				if(sum[j] > 0){
					transferMatrix[i][j] /= sum[j];
				}else{
					transferMatrix[i][j] = 0;
				}
				
			}
		}
	}
	
	/**
	 * 计算两个实体间的卡茨关联性
	 * @param ent1
	 * @param ent2
	 * @param labelMap
	 * @param entityOutMap
	 * @return
	 */
	public double[] calEdgeWeightOfExclusivityPath(Entity ent1,Entity ent2,Map<String, Integer> labelMap,
			Map<Integer, HashMap<Integer, HashSet<Integer>>> entityOutMap,
			Map<Integer, HashMap<Integer, HashSet<Integer>>> entityInMap){
		double score = 0;
		double pathAlpha = RELRWParameterBean.getPathAlpha();
		List<ArrayList<Double>> pathList = new ArrayList<ArrayList<Double>>();
		double[] weights = new double[2];
		
		String name1 = ent1.getEntityName();
		String name2 = ent2.getEntityName();
		
		if(!labelMap.containsKey(name1) || !labelMap.containsKey(name2)){
			return weights;
		}
		
		int label1 = labelMap.get(name1);
		int label2 = labelMap.get(name2);
	
		getSkipExclusivityPath(label1, label2, entityOutMap,entityInMap, 
				pathList, RELRWParameterBean.getSkipNums());
	
		if(pathList.size() > 0){
			for(ArrayList<Double> item : pathList){
				if(item.size() > 0){
					double weight = 0;
					for(double excluScore:item){
						if(excluScore > 0){
							weight += 1/ excluScore;
						}
					}
					if(weight > 0){
						weight = 1 / weight;
					}
					score += Math.pow(pathAlpha, item.size()) * weight;
				}
				
			}
			score /= pathList.size();
		}
		
		weights[0] = weights[1] = score;
		logger.info(name1+"\t"+name2+"的katz score:"+score);
		return weights;
	}
	
	
	/**
	 * 获得两个实体经过若干条的路径,采用排他性的方法
	 * @param label
	 * @param entityOutMap
	 * @param pathList，存放的是路径长度
	 * @param skipNum
	 */
	public void getSkipExclusivityPath(Integer label1,Integer label2,
			Map<Integer, HashMap<Integer, HashSet<Integer>>>entityOutMap, 
			Map<Integer, HashMap<Integer, HashSet<Integer>>> entityInMap,
			List<ArrayList<Double>> pathList,int skipNum){
		
		HashMap<Integer, HashSet<Integer>> map1 = entityOutMap.get(label1);
		HashMap<Integer, HashSet<Integer>> map2 = entityOutMap.get(label2);
		
		if(map1 == null && map2 == null){
			return;
		}
		//直接相连
		if(map1 != null){
			for(Entry<Integer, HashSet<Integer>>entry: map1.entrySet()){
				HashSet<Integer> set1 = entry.getValue();
				int propertyType = entry.getKey();
				if(set1 != null && set1.contains(label2)){
					ArrayList<Double> typeExclusivity = new ArrayList<>();
					double excluXY = getExcluScore(set1, entityInMap.get(label2).get(propertyType));
					if(excluXY > 0){
						typeExclusivity.add(excluXY);
						if(pathList.size() < RELRWParameterBean.getTopK())
							pathList.add(typeExclusivity);
						else
							return;
					}
					
				}
			}
		
		}
		
		if(map2 != null){
			for(Entry<Integer, HashSet<Integer>>entry: map2.entrySet()){
				HashSet<Integer> set2 = entry.getValue();
				int propertyType = entry.getKey();
				if(set2 != null && set2.contains(label1)){
					ArrayList<Double> typeExclusivity = new ArrayList<>();
					double excluXY = getExcluScore(set2, entityInMap.get(label1).get(propertyType));
					if(excluXY > 0){
						typeExclusivity.add(excluXY);
						if(pathList.size() < RELRWParameterBean.getTopK())
							pathList.add(typeExclusivity);
						else
							return;
					}
				}
			}
		}
		
		//直接相连则返回
		if(skipNum == 0)
			return;
		
		//考虑多跳情况
		int i = 1;
		while(i++ <= skipNum){
			if(map1 != null && !map1.isEmpty()){
				for(Entry<Integer, HashSet<Integer>>entry:map1.entrySet()){
					int propertyType = entry.getKey();
					HashSet<Integer> set1 = entry.getValue();
					if(set1 != null && !set1.isEmpty()){
						if(set1.contains(label2)){
							set1.remove(label2);
						}
					}
					
					double excluScore = 0;
					
					for(Integer item:set1){
						HashMap<Integer, HashSet<Integer>> typeMap = entityOutMap.get(item);
						if(typeMap != null){
							for(Entry<Integer, HashSet<Integer>>entry2:typeMap.entrySet()){
								int typeLabel = entry2.getKey();
								HashSet<Integer> typeSet = entry2.getValue();
								if(typeSet.contains(label2)){
									//这里将路径保存在typeExclusivity
									ArrayList<Double>  typeExclusivity = new ArrayList<>();
									excluScore = getExcluScore(set1, 
											entityInMap.get(item).get(propertyType));
									typeExclusivity.add(excluScore);
									excluScore = getExcluScore(typeSet, 
											entityInMap.get(label2).get(typeLabel));
									typeExclusivity.add(excluScore);
									if(pathList.size() < RELRWParameterBean.getTopK())
										pathList.add(typeExclusivity);
									else
										return;
								}
							}
							
						}
						
					}
				}
				
				
			}
			
			if(map2 != null && !map2.isEmpty()){
				for(Entry<Integer, HashSet<Integer>>entry:map2.entrySet()){
					int propertyType = entry.getKey();
					HashSet<Integer> set2 = entry.getValue();
					if(set2 != null && !set2.isEmpty()){
						if(set2.contains(label1)){
							set2.remove(label1);
						}
					}
					
					double excluScore = 0;
					
					
					for(Integer item:set2){
						HashMap<Integer, HashSet<Integer>> typeMap = entityOutMap.get(item);
						if(typeMap != null){
							for(Entry<Integer, HashSet<Integer>>entry2:typeMap.entrySet()){
								int typeLabel = entry2.getKey();
								HashSet<Integer> typeSet = entry2.getValue();
								if(typeSet.contains(label1)){
									//这里将路径保存在typeExclusivity
									ArrayList<Double>  typeExclusivity = new ArrayList<>();
									excluScore = getExcluScore(set2, 
											entityInMap.get(item).get(propertyType));
									typeExclusivity.add(excluScore);
									excluScore = getExcluScore(typeSet, 
											entityInMap.get(label1).get(typeLabel));
									typeExclusivity.add(excluScore);
									if(pathList.size() < RELRWParameterBean.getTopK())
										pathList.add(typeExclusivity);
									else
										return;
								}
							}
							
						}
						
					}
				}
				
				
			}
			
		}
		
	}
	
	/**
	 * 计算两个相连实体间的排他性得分
	 * @param set1
	 * @param set2
	 * @return
	 */
	public double getExcluScore(HashSet<Integer>set1, HashSet<Integer>set2){
		int xToAny,anyToY;
		try {
			xToAny = set1.size();
			anyToY = set2.size();
		} catch (Exception e) {
			// TODO: handle exception
			anyToY = 0;
			xToAny = 0;
			return 0;
		}
		double excluXY = xToAny + anyToY -1;
		if(excluXY > 0)
			excluXY = 1 / excluXY;

		return excluXY;
	}
	
	
	/**
	 * 计算基于类型流行度路径的权重矩阵
	 */
	public void calTransferMatrixOfPopularityPath(){
		double[] weights;
		this.transferMatrix = new double[entityLen][entityLen];
		Parameters parameters = new Parameters();
		Map<String, Integer> labelMap = parameters.loadString2IntegerDict(
				"./data/dbpedia/numLabels_enText.ttl");
		Map<Integer, HashMap<Integer, HashSet<Integer>>> entityOutTypeMap = 
				parameters.loadEntityOutTypeMap("./data/dbpedia/entity_edge.ttl");
		Map<Integer, HashMap<Integer, HashSet<Integer>>> entityInTypeMap = 
				parameters.loadEntityOutTypeMap("./data/dbpedia/entityIn_edge.ttl");
		Map<Integer,Integer> typeFrequencyMap = parameters.loadInteger2IntegerDict(
				"./data/dbpedia/typeFrequency.ttl");
		
		for(int i=0;i<candidateEntityLen;i++){
			logger.info("i = "+i);
			for(int j=i;j<entityLen;j++){
				weights = calEdgeWeightOfPopularityPath(entities[i], entities[j],
						labelMap,entityOutTypeMap,entityInTypeMap,typeFrequencyMap );
				transferMatrix[i][j] = weights[0];
				transferMatrix[j][i] = weights[1];
			}
		}
		
		//将转移概率做归一化处理
		double[] sum = new double[entityLen];
		for(int i=0;i<entityLen;i++){
			for(int j=0;j<entityLen;j++){
				sum[j] += transferMatrix[i][j];
			}
		}
		for(int i=0;i<entityLen;i++){
			for(int j=0;j<entityLen;j++){
				if(sum[j] > 0){
					transferMatrix[i][j] /= sum[j];
				}else{
					transferMatrix[i][j] = 0;
				}
				
			}
		}
	}
	
	/**
	 * 计算两个实体间的卡茨关联性
	 * @param ent1
	 * @param ent2
	 * @param labelMap
	 * @param entityOutMap
	 * @return
	 */
	public double[] calEdgeWeightOfPopularityPath(Entity ent1,Entity ent2,Map<String, Integer> labelMap,
			Map<Integer, HashMap<Integer, HashSet<Integer>>> entityOutMap,
			Map<Integer, HashMap<Integer, HashSet<Integer>>> entityInMap,
			Map<Integer,Integer> typeFrequencyMap){
		double score = 0;
		double pathAlpha = RELRWParameterBean.getPathAlpha();
		List<ArrayList<Double>> pathList = new ArrayList<ArrayList<Double>>();
		double[] weights = new double[2];
		
		String name1 = ent1.getEntityName();
		String name2 = ent2.getEntityName();
		
		if(!labelMap.containsKey(name1) || !labelMap.containsKey(name2)){
			return weights;
		}
		
		int label1 = labelMap.get(name1);
		int label2 = labelMap.get(name2);
	
		getSkipPopularityPath(label1, label2, entityOutMap,entityInMap,typeFrequencyMap,  
				pathList, RELRWParameterBean.getSkipNums());
	
		if(pathList.size() > 0){
			for(ArrayList<Double> item : pathList){
				if(item.size() > 0){
					double weight = 0;
					for(double excluScore:item){
						if(excluScore > 0){
							weight += 1/ excluScore;
						}
					}
					if(weight > 0){
						weight = 1 / weight;
					}
					score += Math.pow(pathAlpha, item.size()) * weight;
				}
				
			}
			score /= pathList.size();
		}
		
		weights[0] = weights[1] = score;
		logger.info(name1+"\t"+name2+"的katz score:"+score);
		return weights;
	}
	
	
	/**
	 * 获得两个实体经过若干条的路径,采用排他性的方法
	 * @param label
	 * @param entityOutMap
	 * @param pathList，存放的是路径长度
	 * @param skipNum
	 */
	public void getSkipPopularityPath(Integer label1,Integer label2,
			Map<Integer, HashMap<Integer, HashSet<Integer>>>entityOutMap, 
			Map<Integer, HashMap<Integer, HashSet<Integer>>> entityInMap,
			Map<Integer,Integer> typeFrequencyMap ,
			List<ArrayList<Double>> pathList,int skipNum){
		
		HashMap<Integer, HashSet<Integer>> map1 = entityOutMap.get(label1);
		HashMap<Integer, HashSet<Integer>> map2 = entityOutMap.get(label2);
		
		if(map1 == null && map2 == null){
			return;
		}
		//直接相连
		if(map1 != null){
			for(Entry<Integer, HashSet<Integer>>entry: map1.entrySet()){
				HashSet<Integer> set1 = entry.getValue();
				int propertyType = entry.getKey();
				if(set1 != null && set1.contains(label2)){
					ArrayList<Double> typeExclusivity = new ArrayList<>();
					double frequency = typeFrequencyMap.get(propertyType);
					double excluXY = getPopularityScore(set1, 
							entityInMap.get(label2).get(propertyType), frequency );
					if(excluXY > 0){
						typeExclusivity.add(excluXY);
						if(pathList.size() < RELRWParameterBean.getTopK())
							pathList.add(typeExclusivity);
						else
							return;
					}
					
				}
			}
		
		}
		
		if(map2 != null){
			for(Entry<Integer, HashSet<Integer>>entry: map2.entrySet()){
				HashSet<Integer> set2 = entry.getValue();
				int propertyType = entry.getKey();
				if(set2 != null && set2.contains(label1)){
					ArrayList<Double> typeExclusivity = new ArrayList<>();
					double frequency = typeFrequencyMap.get(propertyType);
					double excluXY = getPopularityScore(set2, 
							entityInMap.get(label1).get(propertyType),frequency);
					if(excluXY > 0){
						typeExclusivity.add(excluXY);
						if(pathList.size() < RELRWParameterBean.getTopK())
							pathList.add(typeExclusivity);
						else
							return;
					}
				}
			}
		}
		
		//直接相连则返回
		if(skipNum == 0)
			return;
		
		//考虑多跳情况
		int i = 1;
		while(i++ <= skipNum){
			if(map1 != null && !map1.isEmpty()){
				for(Entry<Integer, HashSet<Integer>>entry:map1.entrySet()){
					int propertyType = entry.getKey();
					HashSet<Integer> set1 = entry.getValue();
					if(set1 != null && !set1.isEmpty()){
						if(set1.contains(label2)){
							set1.remove(label2);
						}
					}
					
					double excluScore = 0;
					
					for(Integer item:set1){
						HashMap<Integer, HashSet<Integer>> typeMap = entityOutMap.get(item);
						if(typeMap != null){
							for(Entry<Integer, HashSet<Integer>>entry2:typeMap.entrySet()){
								int typeLabel = entry2.getKey();
								HashSet<Integer> typeSet = entry2.getValue();
								if(typeSet.contains(label2)){
									//这里将路径保存在typeExclusivity
									ArrayList<Double>  typeExclusivity = new ArrayList<>();
									double frequency = typeFrequencyMap.get(propertyType);
									excluScore = getPopularityScore(set1, 
											entityInMap.get(item).get(propertyType),frequency);
									typeExclusivity.add(excluScore);
									frequency = typeFrequencyMap.get(typeLabel);
									excluScore = getPopularityScore(typeSet, 
											entityInMap.get(label2).get(typeLabel),frequency);
									typeExclusivity.add(excluScore);
									if(pathList.size() < RELRWParameterBean.getTopK())
										pathList.add(typeExclusivity);
									else
										return;
								}
							}
							
						}
						
					}
				}
				
				
			}
			
			if(map2 != null && !map2.isEmpty()){
				for(Entry<Integer, HashSet<Integer>>entry:map2.entrySet()){
					int propertyType = entry.getKey();
					HashSet<Integer> set2 = entry.getValue();
					if(set2 != null && !set2.isEmpty()){
						if(set2.contains(label1)){
							set2.remove(label1);
						}
					}
					
					double excluScore = 0;
					
					
					for(Integer item:set2){
						HashMap<Integer, HashSet<Integer>> typeMap = entityOutMap.get(item);
						if(typeMap != null){
							for(Entry<Integer, HashSet<Integer>>entry2:typeMap.entrySet()){
								int typeLabel = entry2.getKey();
								HashSet<Integer> typeSet = entry2.getValue();
								if(typeSet.contains(label1)){
									//这里将路径保存在typeExclusivity
									ArrayList<Double>  typeExclusivity = new ArrayList<>();
									double frequency = typeFrequencyMap.get(propertyType);
									excluScore = getPopularityScore(set2, 
											entityInMap.get(item).get(propertyType),frequency);
									typeExclusivity.add(excluScore);
									frequency = typeFrequencyMap.get(typeLabel);
									excluScore = getPopularityScore(typeSet, 
											entityInMap.get(label1).get(typeLabel),frequency);
									typeExclusivity.add(excluScore);
									if(pathList.size() < RELRWParameterBean.getTopK())
										pathList.add(typeExclusivity);
									else
										return;
								}
							}
							
						}
						
					}
				}
				
				
			}
			
		}
		
	}
	
	
	/**
	 * 计算两个相连实体类型间的流行度得分
	 * @param set1
	 * @param set2
	 * @return
	 */
	public double getPopularityScore(HashSet<Integer>set1, HashSet<Integer>set2,
			double frequency){
		int xToAny,anyToY;
		try {
			xToAny = set1.size();
			anyToY = set2.size();
		} catch (Exception e) {
			// TODO: handle exception
			anyToY = 0;
			xToAny = 0;
			return 0;
		}
		double excluXY = xToAny + anyToY -1;
		if(excluXY > 0 && frequency >0){
			excluXY = excluXY / frequency;
		}else{
			excluXY = 0;
		}

		return excluXY;
	}
	
	/**
	 * 从实体出边词典中获得入边信息词典
	 * @param entityOutMap
	 * @return
	 */
	public Map<Integer, HashMap<Integer, HashSet<Integer>>> getEntityInMap (
			Map<Integer, HashMap<Integer, HashSet<Integer>>> entityOutMap){
		Map<Integer, HashMap<Integer, HashSet<Integer>>> entityInMap = new HashMap<>();
		
		for(Entry<Integer, HashMap<Integer, HashSet<Integer>>> entry:entityOutMap.entrySet()){
			Map<Integer, HashSet<Integer>> typeMap = entry.getValue();
			int label = entry.getKey();
			for(Entry<Integer, HashSet<Integer>>entry2:typeMap.entrySet()){
				HashSet<Integer> set = entry2.getValue();
				int typeLabel = entry2.getKey();
				for(Integer item:set){
					if(!entityInMap.containsKey(item)){
						HashMap<Integer, HashSet<Integer>> inTypeMap = new HashMap<>();
						HashSet<Integer> inSet = new HashSet<>();
						inSet.add(label);
						inTypeMap.put(typeLabel, inSet);
						entityInMap.put(item, inTypeMap);
					}else{
						HashMap<Integer, HashSet<Integer>> inTypeMap = entityInMap.get(item);
						if(!inTypeMap.containsKey(typeLabel)){
							HashSet<Integer> inSet = new HashSet<>();
							inSet.add(label);
							inTypeMap.put(typeLabel, inSet);
						}else{
							HashSet<Integer> inSet = inTypeMap.get(typeLabel);
							if(!inSet.contains(label)){
								inSet.add(label);
							}
						}
					}
				}
			}
		}
		
		return entityInMap;
	}
	
	/**
	 * 将map持久化到本地
	 * @param wpath
	 * @param entityInMap
	 */
	public void pickleMap(String wpath,Map<Integer, HashMap<Integer, HashSet<Integer>>>entityInMap){
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(wpath)), "utf-8"));
			StringBuilder builder = new StringBuilder();
			for(Entry<Integer, HashMap<Integer, HashSet<Integer>>> entry:entityInMap.entrySet()){
				Map<Integer, HashSet<Integer>> typeMap = entry.getValue();
				int label = entry.getKey();
				for(Entry<Integer, HashSet<Integer>>entry2:typeMap.entrySet()){
					HashSet<Integer> set = entry2.getValue();
					int typeLabel = entry2.getKey();
					builder.delete(0, builder.length());
					builder.append(label).append("\t||\t").append(typeLabel).append("\t||\t")
							.append(set.size()).append("\t||\t").append(StringUtils.join(set,"\t|\t"));
					builder.append("\n");
					writer.write(builder.toString());
					
				}
			}
			
			writer.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 带重启的随机游走计算语义签名
	 * @param graph
	 * @param beginIndex,文档的语义签名按
	 * @param preferVector
	 * @return newSignatureVector
	 */
	public double[] calSignature(double[] preferVector){
		int len = preferVector.length;
		double[] oldSignatureVector = new double[len];
		double[] newSignatureVector = new double[len];
		for(int i=0;i<len;i++){
			oldSignatureVector[i] = 1;
			newSignatureVector[i] = 1;
		}
		double[] tempVector;
		double alpha = RELRWParameterBean.getAlpha();
		RealMatrix transferMatrix = new Array2DRowRealMatrix(this.getTransferMatrix());
		ArrayRealVector realVector;
		ArrayRealVector preferRealVector = new ArrayRealVector(preferVector);
		logger.info("初始向量:"+StringUtils.join(ArrayUtils.toObject(preferVector), "\t"));
		int i=0;
		do {
			oldSignatureVector = Arrays.copyOf(newSignatureVector, len);
			tempVector = transferMatrix.preMultiply(oldSignatureVector);
			realVector = new ArrayRealVector(tempVector);
			realVector = (ArrayRealVector) realVector.mapMultiply(alpha);
			realVector = realVector.add(preferRealVector.mapMultiply(1-alpha));
//			System.out.println(StringUtils.join(ArrayUtils.toObject(realVector.getDataRef()), "\t"));
			newSignatureVector = realVector.getDataRef();
		} while (!isConvergence(oldSignatureVector, newSignatureVector) && i++ < MAXITERATERCOUNTS);
		
		logger.info("收敛后的向量:"+StringUtils.join(ArrayUtils.toObject(newSignatureVector), "\t"));
		return newSignatureVector;
	}
	
	/**
	 * 判断是否收敛
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public boolean isConvergence(double[] vector1,double[] vector2){
		double result = 0;
		if(vector1.length == vector2.length){
			for(int i =0;i<vector1.length;i++){
				result += Math.abs(vector1[i] - vector2[i]);
			}
		}
		if(result < RELRWParameterBean.getConvergencePrecise()){
			return true;
		}else {
			return false;
		}
	}
}
