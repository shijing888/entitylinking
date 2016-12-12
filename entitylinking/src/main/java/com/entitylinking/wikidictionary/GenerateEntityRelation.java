package com.entitylinking.wikidictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.entitylinking.config.HibernateSession;
import com.entitylinking.utils.NormalizeMention;
import com.entitylinking.wiki.bean.PageBean;

/**
 * 用于生成实体之间的关系
 * @author HP
 *
 */
public class GenerateEntityRelation {

	/**实体关联词典*/
	private HashSet<String> entityRelationDict = new HashSet<String>();
	/**实体入度词典*/
	private Map<String, Long> entityInlinks = new HashMap<String, Long>();
	
	/**每次查询出的最大记录数目*/
	private static final int MAXRECORDS = 10000;
	private static final int MAXCOUNTS = 100000;
	/**page中最大的id值*/
	private static final int MAXRECORDCOUNTS = 51484878;
	private Session session = null;
	private static Logger logger = Logger.getLogger(GenerateEntityRelation.class);
	static{
		PropertyConfigurator.configure("log4j.properties");
	}
	public GenerateEntityRelation(){
		session = HibernateSession.getSession();
	}
	public static void main(String args[]){
		String wpath = "./dict/entityRelation2.txt";
		GenerateEntityRelation generateEntityRelation = new GenerateEntityRelation();
		generateEntityRelation.generateEntityInlinksDict();
		generateEntityRelation.generateEntityDict(wpath);
	}
	
	public void generateEntityInlinksDict(){
		//根据循环进行数据库的IO，避免内存不足
		for(int i=0;i<MAXRECORDCOUNTS;i+=MAXRECORDS){
			//开始事务  
		    Transaction transaction = session.beginTransaction();  
		    //查询sql
		    String sqlString = "select page.name, count(pageInlinks.id)"
    			+" from PageBean page, PageInLinksBean pageInlinks where page.id = pageInlinks.id"
    			+" and page.id >= " + i +" and page.id < "+ (i+MAXRECORDS)
    			+" group by page.id";
		    Query query = session.createQuery(sqlString);
		    @SuppressWarnings("rawtypes")
			Iterator it=query.list().iterator(); 
		    while(it.hasNext()){
		    	 Object[] obj=(Object[])it.next();   
		    	 entityInlinks.put(obj[0].toString().toLowerCase(), (Long) obj[1]);
		    }
		    System.out.println("入度进度i="+i);
		    transaction.commit();
		}
	}
	@SuppressWarnings("unchecked")
	public void generateEntityDict(String wpath){
		long time1 = System.currentTimeMillis();
		List<PageBean> pageList = new ArrayList<PageBean>();
		int outLinksNum = 0;
		StringBuilder sb = new StringBuilder();
		//根据循环进行数据库的IO，避免内存不足
		for(int i=0;i<MAXRECORDCOUNTS;i+=MAXRECORDS){
			//开始事务  
		    Transaction transaction = session.beginTransaction();  
		    //查询sql
		    String sqlString = "select new com.entitylinking.wiki.bean.PageBean(name,text)"
		    					+" from PageBean page where page.id >=" + i +"and page.id < "+ (i+MAXRECORDS);
		    Query query = session.createQuery(sqlString);
		   
		    pageList = query.list();
		    transaction.commit();
		    String title;
		    String text;
		    Long inDrgee;
		    for(PageBean pageBean : pageList){
		    	sb.delete(0, sb.length());
		    	title = pageBean.getName();
		    	text = pageBean.getText();
		    	HashSet<String> entitySet = new HashSet<String>();
		    	ExtractInPageEntity.allEntitiesInPage(text, entitySet);
		    	outLinksNum = entitySet.size();
		    	if(outLinksNum==0)
		    		continue;
		    	title = NormalizeMention.getNormalizeMention(title, true);
		    	sb.append(title).append("\t||\t")
		    					.append(outLinksNum).append("\t||\t");
		    	for(String str:entitySet){
	    			str = NormalizeMention.getNormalizeMention(str, true);
	    			if(entityInlinks.containsKey(str)){
	    				inDrgee =entityInlinks.get(str);
	    				sb.append(str).append("\t").append(inDrgee).append("\t|\t");
	    			}
		    	}
		    	sb.append("\n");
		    	entityRelationDict.add(sb.toString());
		    	
		    }
	    	pageList.clear();
	    	System.gc();
	    	logger.info("共现关系进度i="+i);
	    	logger.info("entityRelationDict.size():"+entityRelationDict.size());
	    	if(entityRelationDict.size() > MAXCOUNTS){
	    		saveEntityRelationDict(wpath);
	    		entityRelationDict.clear();
	    	}
		}
		    
		long time2 = System.currentTimeMillis();
		logger.info("解析实体关联耗时:"+(time2-time1)/60000.0);
	    //用于持久化实体关联词典
		saveEntityRelationDict(wpath);
		HibernateSession.closeSession();
	}
	
	public void saveEntityRelationDict(String path){
		logger.info("持久化到本地文件begin！");
		if(entityRelationDict.size() > 0){
			File file = new File(path);
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				BufferedWriter bwBufferedWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file,true),"utf-8"));
				for(String str:entityRelationDict){
					bwBufferedWriter.write(str);
				}
				bwBufferedWriter.close();
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
	}
}
