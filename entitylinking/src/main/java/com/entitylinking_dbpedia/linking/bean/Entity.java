package com.entitylinking_dbpedia.linking.bean;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import com.entitylinking_dbpedia.config.WikiConfig;
import com.entitylinking_dbpedia.linking.bean.DictBean;
import com.entitylinking_dbpedia.linking.bean.RELRWParameterBean;
import com.entitylinking_dbpedia.lucene.IndexFile;
import com.entitylinking_dbpedia.utils.NLPUtils;

import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * 实体的数据结构
 * @author HP
 *
 */
public class Entity {
	static Logger logger = Logger.getLogger(Entity.class);
	static Wikipedia wikipedia = WikiConfig.getWiki();
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
	/**实体category*/
	private Set<String> category;
	
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
	public Set<String> getCategory() {
		return category;
	}
	public void setCategory(Set<String> category) {
		this.category = category;
	}
	
	/**
	 * 从wiki中获取category
	 * @param title
	 * @return
	 */
	public Set<String> getCategory(String title){
		Set<String> categorySet = new HashSet<>();
		try {
			Page page = wikipedia.getPage(title);
			Set<Category> category = page.getCategories();
			for(Category item:category){
				String[] array = item.getTitle().toString().split(" ");
				for(String s:array){
					categorySet.add(s);
				}
			}
		} catch (WikiApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categorySet;
	}
	
	/**
     * 获取实体信息，名称、上下文、流行度、类型
     * @param title
     */
    public Entity getEntityPageInfo(String title,Map<String, Set<String>> additiveEntityContextDict,
    							Map<String, Set<String>> additiveEntityCategoryDict){
		
        //初始化title
        entityName = title;
        //初始化流行度
		if(DictBean.getEntityByDbpeidaPopularityDict().containsKey(title)){
			popularity = DictBean.getEntityByDbpeidaPopularityDict().get(title);
		}else{
			popularity = 0;
		}
		//初始化类型
//		if(DictBean.getEntityCategoryDict().containsKey(entityName)){
//			category = DictBean.getEntityCategoryDict().get(entityName);
//		}else{
//			category = getCategory(title);
//			additiveEntityCategoryDict.put(entityName, category);
//		}
		
		
		//初始化上下文
        if(DictBean.getEntityContextDict().containsKey(entityName)){
        	entityContext = DictBean.getEntityContextDict().get(entityName);
        }else{
        	Document document = IndexFile.queryDocument(entityName, 
        						RELRWParameterBean.getShortAbstractField1(), PathBean.getShortAbstractTextPath());
	        if(document != null){
	        	String name = document.get(RELRWParameterBean.getShortAbstractField1());
	        	if(!title.equals(name)){
	        		logger.info(title+"无摘要内容,查询出的实体为:\t"+name);
	        		return this;
	        	}
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