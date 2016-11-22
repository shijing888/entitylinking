package com.entitylinking.linking.bean;

import java.util.List;
import java.util.Map;

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
	/*用于记录每个mention对应的实体在entities中的起始位置，特殊mention(OTHER)标注的是扩展实体的起始位置*/
	private Map<String, Integer> entityIndex;
	/*mention-候选实体map*/
	private Map<Mention, List<String>> entityMap ;
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
	/*实体图对象*/
	private EntityGraph entityGraph;
	
	public EntityGraph getEntityGraph() {
		return entityGraph;
	}

	public void setEntityGraph(EntityGraph entityGraph) {
		this.entityGraph = entityGraph;
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

	public void setEntityIndex(Map<String, Integer> entityIndex) {
		this.entityIndex = entityIndex;
	}

	public Map<Mention, List<String>> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<Mention, List<String>> entityMap) {
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

	public void setPreferVectorOfDocument(double[] preferVectorOfDocument) {
		this.preferVectorOfDocument = preferVectorOfDocument;
	}

	public double[] getPreferVectorOfEntity() {
		return preferVectorOfEntity;
	}

	public void setPreferVectorOfEntity(double[] preferVectorOfEntity) {
		this.preferVectorOfEntity = preferVectorOfEntity;
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
}
