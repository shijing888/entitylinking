package com.entitylinking.linking.bean;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sweble.wikitext.lazy.utils.ParserShouldNotBeHereException;

import com.entitylinking.config.WikiConfig;
import com.entitylinking.utils.NLPUtils;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiPageNotFoundException;

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
	private Page page;
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
    public void getEntityPageInfo(String title,Map<String, Set<String>> additiveEntityContextDict){
		try {
			page = wikipedia.getPage(title);
			//初始化流行度
	        popularity = page.getNumberOfInlinks();
	        //初始化title
	        entityName = page.getTitle().getWikiStyleTitle().toLowerCase();
	        if(DictBean.getEntityContextDict().containsKey(entityName)){
	        	entityContext = DictBean.getEntityContextDict().get(entityName);
	        }else{
	        	 //初始化上下文
		        String content = page.getPlainText();
		        if(content.length() > RELRWParameterBean.getEntityContentLen()){
		        	content = content.substring(0,RELRWParameterBean.getEntityContentLen());
		        }
		        entityContext = NLPUtils.getEntityContext(content);
		        logger.info(entityName+"不在实体上下文词典中");
		        additiveEntityContextDict.put(entityName, entityContext);
	        }
	       
		}catch (WikiPageNotFoundException e) {
 			// TODO Auto-generated catch block
 			logger.info(title +" is not found!");
 			e.printStackTrace();
 		}catch (WikiApiException e) {
 			// TODO Auto-generated catch block
 			logger.info(title +" is not an entity!");
 			e.printStackTrace();
 		}catch (ParserShouldNotBeHereException e) {
			// TODO: handle exception
		}
    }
    
    /**
     * 获取title对应的实体在wiki中的标准名称
     * @param title
     * @return
     */
    public String getEntityName(String title){
 		try {
 			page = wikipedia.getPage(title);
 			title = page.getTitle().getWikiStyleTitle().toLowerCase();
 	       return title;
 		}catch (WikiPageNotFoundException e) {
 			// TODO Auto-generated catch block
 			logger.info(title +" is not found!");
 			e.printStackTrace();
 		}catch (WikiApiException e) {
 			// TODO Auto-generated catch block
 			logger.info(title +" is not an entity!");
 			e.printStackTrace();
 		}
 		return null;
    }
    /**
     * 获取实体流行度
     * @param title
     * @return
     */
    public double getEntityPopularity(String title){
		try {
			page = wikipedia.getPage(title);
			//初始化流行度
	        popularity = page.getNumberOfInlinks();
//			logger.info(title+" popularity:"+popularity);
	       return page.getNumberOfInlinks();
		}catch (WikiPageNotFoundException e) {
 			// TODO Auto-generated catch block
 			logger.info(title +" is not found!");
 			e.printStackTrace();
 		}catch (WikiApiException e) {
 			// TODO Auto-generated catch block
 			logger.info(title +" is not an entity!");
 			e.printStackTrace();
 		}
       return 0;
    }
}
