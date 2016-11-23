package com.entitylinking.linking.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.lucene.document.Document;

import com.entitylinking.lucene.IndexFile;

/**
 * 一篇文档生成一个实体子图
 * @author HP
 *
 */
public class EntityGraph {

	/*所有实体的size*/
	private int entityLen;
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

	public Map<Mention, Entity> getDisambiguationMap() {
		return disambiguationMap;
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
	 */
	public void setPreferVectorOfDocument() {
		this.preferVectorOfDocument = new double[this.entityLen];
		double important;
		double polularity;
		int index;
		for(Entry<Mention, Entity>entry:this.disambiguationMap.entrySet()){
			important = entry.getKey().getTfidfValue();
			polularity = entry.getValue().getPopularity();
			index = this.entityIndex.get(entry.getValue());
			this.preferVectorOfDocument[index] = important * polularity;
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
	public void initDisambiguationMap(){
		this.disambiguationMap = new HashMap<>();
		for(Mention mention:this.mentions){
			this.disambiguationMap.put(mention, mention.getCandidateEntity().get(0));
		}
	}
	
	
	/**
	 * 计算转移矩阵
	 */
	public void calTransferMatrix(){
		double weight= 0;
		for(int i=0;i<entityLen;i++){
			for(int j=0;j<entityLen;j++){
				weight = calEdgeWeight(entities[i].getEntityName(), entities[j].getEntityName(), 
						RELRWParameterBean.getEntityRelationField2(),
						RELRWParameterBean.getEntityRelationField3(), PathBean.getEntityRelationPath());
				transferMatrix[i][j] = weight;
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
	public double calEdgeWeight(String entity1,String entity2,String queryField1,
			String queryField2,String indexDir){
		String[] querys = new String[]{entity1,entity2};
		String[] queryFields = new String[]{queryField2};
		int count = IndexFile.countCooccurence(querys, queryFields, indexDir);
		try {
			Document document = IndexFile.queryDocument(entity1, queryField2, indexDir);
			int entityOutCount = Integer.parseInt(document.get(queryField1));
			if(entityOutCount > 0 && count > 0 && count <= entityOutCount){
				return count / (double)entityOutCount;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	
	/**
	 * 带重启的随机游走计算语义签名
	 * @param graph
	 * @param beginIndex,文档的语义签名按
	 * @param preferVector
	 * @return newSignatureVector
	 */
	public double[] calSignature(double[] preferVector){
		
		double[] oldSignatureVector = preferVector;
		double[] newSignatureVector = preferVector;
		double[] tempVector;
		double alpha = RELRWParameterBean.getAlpha();
		RealMatrix transferMatrix = new Array2DRowRealMatrix(this.getTransferMatrix());
		ArrayRealVector realVector;
		ArrayRealVector preferRealVector = new ArrayRealVector(preferVector);
		do {
			tempVector = transferMatrix.preMultiply(oldSignatureVector);
			realVector = new ArrayRealVector(tempVector);
			realVector = (ArrayRealVector) realVector.mapMultiply(alpha);
			realVector = realVector.add(preferRealVector.mapMultiply(1-alpha));
			oldSignatureVector = newSignatureVector;
			newSignatureVector = realVector.getDataRef();
		} while (!isConvergence(oldSignatureVector, newSignatureVector));
		
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
