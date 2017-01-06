package com.entitylinking_dbpedia.linking.bean;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import com.entitylinking_dbpedia.linking.bean.DictBean;
import com.entitylinking_dbpedia.linking.bean.RELRWParameterBean;
import com.entitylinking_dbpedia.lucene.IndexFile;
import com.entitylinking_dbpedia.utils.NLPUtils;

/**
 * 实体的数据结构
 * @author HP
 *
 */
public class Entity {
	static Logger logger = Logger.getLogger(Entity.class);
	/**实体名称*/
	private String entityName;
	/**实体流行度*/
	private double popularity;
	/**实体的语义签名*/
	private double[] semanticSignature;
	/**实体的上下文*/
	private Set<String> entityContext;
	/**实体的得分*/
	private double score;
	
	public Entity(String name){
		this.entityName = name;
	}
	public Entity(){}
	
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public double getPopularity() {
		return popularity;
	}
	public void setPopularity(double popularity) {
		this.popularity = popularity;
	}
	public double[] getSemanticSignature() {
		return semanticSignature;
	}
	public void setSemanticSignature(double[] semanticSignature) {
		this.semanticSignature = semanticSignature;
	}
	public Set<String> getEntityContext() {
		return entityContext;
	}
	public void setEntityContext(Set<String> entityContext) {
		this.entityContext = entityContext;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	/**
     * 获取实体信息，名称、上下文、流行度
     * @param title
     * @throws WikiApiException
     */
    public Entity getEntityPageInfo(String title,Map<String, Set<String>> additiveEntityContextDict){
		
        //初始化title
        entityName = title;
        //初始化流行度
		if(DictBean.getEntityByDbpeidaPopularityDict().containsKey(title)){
			popularity = DictBean.getEntityByDbpeidaPopularityDict().get(title);
		}else{
			popularity = 0;
		}
		//初始化上下文
        if(DictBean.getEntityContextDict().containsKey(entityName)){
        	entityContext = DictBean.getEntityContextDict().get(entityName);
        }else{
        	Document document = IndexFile.queryDocument(entityName, 
        						RELRWParameterBean.getShortAbstractField1(), PathBean.getShortAbstractTextPath());
	        if(document != null){
	        	String content = document.get(RELRWParameterBean.getShortAbstractField2());
		        if(content.length() > RELRWParameterBean.getEntityContentLen()){
		        	content = content.substring(0,RELRWParameterBean.getEntityContentLen());
		        }
		        entityContext = NLPUtils.getEntityContext(content);
		        
	        }else {
				entityContext = new HashSet<>();
			}
	        
	        logger.info(entityName+"不在实体上下文词典中");
	        additiveEntityContextDict.put(entityName, entityContext);
	       
        }
	       
        return this;
    }
    
}