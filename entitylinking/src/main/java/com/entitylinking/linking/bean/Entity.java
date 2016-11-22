package com.entitylinking.linking.bean;

import java.util.Set;

import org.apache.log4j.Logger;

import com.entitylinking.config.WikiConfig;
import com.entitylinking.utils.NLPUtils;

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
	/**实体名称*/
	private String entityName;
	/**实体流行度*/
	private double popularity;
	/**实体的语义签名*/
	private double[] semanticSignature;
	/**实体的上下文*/
	private Set<String> entityContext;
	
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
	
	/**
     * 获取实体信息，名称、上下文、流行度
     * @param title
     * @throws WikiApiException
     */
    public void getEntityPageInfo(String title) throws WikiApiException{
    	Wikipedia wikipedia = WikiConfig.getWiki();
        Page page = wikipedia.getPage(title);
        logger.info(page.getTitle());
        //初始化title
        entityName = page.getTitle().getWikiStyleTitle().toLowerCase();
        //初始化上下文
        entityContext = NLPUtils.getEntityContext(page.getPlainText());
        //初始化流行度
        popularity = page.getNumberOfInlinks();
    }
}
