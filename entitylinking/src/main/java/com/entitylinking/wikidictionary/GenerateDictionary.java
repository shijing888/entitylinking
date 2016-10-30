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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.entitylinking.config.HibernateSession;
import com.entitylinking.config.WikiConfig;
import com.entitylinking.wiki.bean.PageBean;
import com.entitylinking.wiki.bean.PageRedictBean;

import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * 通过wikipedia生成同义歧义词典
 * @author shijing
 *
 */
public class GenerateDictionary {

	/**
	 * disambiguationDict
	 * 			歧义词典
	 * synonymsDict
	 * 			同义词典
	 */
	private HashMap<String, HashSet<String>> disambiguationDict = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> synonymsDict = new HashMap<String, HashSet<String>>();
	private Session session = null;
	
	/**
	 * 用于找出文本中出现的实体[[entity]]
	 */
	private static final String ENTITY = "\\[\\[([\\s\\S]*?)\\]\\]";
	public static Pattern pattern = Pattern.compile(ENTITY);
	private Matcher matcher = null;
	
	/**
	 * 以下数目基于2016.09.01的离线wikipedia文件
	 * MAXRECORDS
	 * 			每次查询出的最大记录数目；
	 * MAXRECORDCOUNTS
	 * 			page中最大的id值
	 */
	private static final int MAXRECORDS = 10000;
	private static final int MAXRECORDCOUNTS = 51484878;
	
	static Logger logger = Logger.getLogger(GenerateDictionary.class);
	static{
		PropertyConfigurator.configure("./log4j.properties");
	}
	
	public GenerateDictionary(){
		session = HibernateSession.getSession();
	}
	
	public static void main(String args[]){
		GenerateDictionary generateDictionary  = new GenerateDictionary();
		String saveDisambiguationDictPath = "H:\\MysqlData\\ambiguationDict.txt";
		String saveSynonymsDictPath = "H:\\MysqlData\\synonymsDict.txt";
		generateDictionary.recordPageTable(saveDisambiguationDictPath, saveSynonymsDictPath);
	}
	
	/**
	 * 从page表中生成词典
	 */
	@SuppressWarnings("unchecked")
	public void recordPageTable(String saveDisambiguationDictPath, String saveSynonymsDictPath){
        List<PageBean> pageList = new ArrayList<PageBean>();
        Wikipedia wikipedia = WikiConfig.getWiki();
        //根据循环进行数据库的IO，避免内存不足
        for(int i=0;i<MAXRECORDCOUNTS;i+=MAXRECORDS){
        	//开始事务  
            Transaction transaction = session.beginTransaction();  
            //查询sql
            String sqlString = "select new com.entitylinking.bean.PageBean(name,text)"
            					+" from PageBean page where page.id >=" + i +"and page.id < "+ (i+MAXRECORDS);
            Query query = session.createQuery(sqlString);
           
            pageList = query.list();
            transaction.commit();
           
            String title;
            for(PageBean pageBean : pageList){
            	title = pageBean.getName();
            	
            		try {
            			//同义词典的处理,通过jwpl和规则判断是否为消歧页
						if(!isContainDisambiguation(title) && !wikipedia.getPage(title).isDisambiguation()){
							if(!synonymsDict.containsKey(title)){
		            			//将标题放入词典
								HashSet<String> titleValue = new HashSet<>();
								titleValue.add(title);
		            			synonymsDict.put(title, titleValue);
		            		}
							//对正文进行解析
							Map<String,HashSet<String>> anchorPairs = new HashMap<String,HashSet<String>>();
		            		ExtractInPageEntity.inPageEntity(pageBean.getText(), anchorPairs);
		            		for(Entry<String, HashSet<String>>entry:anchorPairs.entrySet()){
		            			if(!synonymsDict.containsKey(entry.getKey())){
		                			synonymsDict.put(entry.getKey(), entry.getValue());
		                		}
		            		}
						}else{
							if(!disambiguationDict.containsKey(title))
								continue;
							//若为消歧页，则解析消歧页面构造消歧词典
							matcher = pattern.matcher(pageBean.getText());
							HashSet<String> ambiguationDict = new HashSet<String>(); 
							ExtractPageEntity.pageEntity(matcher, ambiguationDict);
							if(ambiguationDict.size()>0){
								disambiguationDict.put(title, ambiguationDict);
							}
						}
					} catch (WikiApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
            		
            	}
            
            	pageList.clear();
            	System.gc();
            	logger.info("synonymsDict.size():"+synonymsDict.size());
            }
            
        
	        //用于持久化同义词典
	    	recordPageMapLineTable();
	        saveSynonymsDict(saveSynonymsDictPath);
	        
	        //用于持久化歧义词典
	    	saveDisambiguationDict(saveDisambiguationDictPath);
	    	disambiguationDict.clear();
        
        
        
	    	HibernateSession.closeSession();
        
	}
	
	/**
	 * 从pagemapline表中解析词条
	 */
	@SuppressWarnings("unchecked")
	public void recordPageMapLineTable(){
		 List<PageRedictBean> pageList = new ArrayList<PageRedictBean>();
        for(int i=0;i<MAXRECORDCOUNTS;i+=MAXRECORDS){
        	//开始事务  
            Transaction transaction = session.beginTransaction();  
            //查询sql
            StringBuilder sqlString = new StringBuilder();
            sqlString.append("select new com.entitylinking.bean.PageRedictBean(pagemapline.name, page.name) ")
             			.append(" from PageBean page, PageMapLineBean pagemapline where pagemapline.id >= ")
             			.append(i).append(" and pagemapline.id < "+ (i+MAXRECORDS) + " and pagemapline.pageID = page.id");
            Query query = session.createQuery(sqlString.toString());
            pageList = query.list();
            transaction.commit();
           
            String source;
            String dest;
            for(PageRedictBean pageRedictBean : pageList){
            	
            	source = pageRedictBean.getSource();
            	dest = pageRedictBean.getDest();
            	
            	//同义词典的处理
        		if(!synonymsDict.containsKey(source)){
        			//将标题放入词典
        			HashSet<String> value = new HashSet<>();
					value.add(dest);
        			synonymsDict.put(source, value);
        		}
            		
            }
            logger.info("mapline synonymsDict.size():"+synonymsDict.size());
            pageList.clear();
            System.gc();
        }  
	}
	
	/**
	 * 判断title中是否包含"(disambiguation)"
	 * @param title,wiki实体页的标题
	 * @return
	 */
	public boolean isContainDisambiguation(String title){
		if(title.contains("(disambiguation)"))
			return true;
		else
			return false;
	}
	
	/**
	 * 去除标题中包含的"(disambiguation)"
	 * @param title
	 * @return
	 */
	public String removeDisambiguation(String title){
		return title.replace("(disambiguation)", "");
	}
	
	public void saveDisambiguationDict(String path){
		if(disambiguationDict.size() > 0){
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
				StringBuilder sBuilder = new StringBuilder();
				for(Entry<String, HashSet<String>>entry:disambiguationDict.entrySet()){
					sBuilder.delete(0, sBuilder.length());
					sBuilder.append(entry.getKey());
					sBuilder.append("\t||\t");
					sBuilder.append(StringUtils.join(entry.getValue(),"\t|\t"));
					sBuilder.append("\n");
					bwBufferedWriter.write(sBuilder.toString());
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
	
	public void saveSynonymsDict(String path){
		if(synonymsDict.size() > 0){
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
				StringBuilder sBuilder = new StringBuilder();
				for(Entry<String, HashSet<String>> entry:synonymsDict.entrySet()){
					//将同义词典中对应set长度大于1的放入到歧义词典中
					if(entry.getValue().size() > 1){
						if(!disambiguationDict.containsKey(entry.getKey())){
							disambiguationDict.put(entry.getKey(), entry.getValue());
						}else{
							disambiguationDict.get(entry.getKey()).addAll(entry.getValue());
						}
						continue;
					}
						
					sBuilder.delete(0, sBuilder.length());
					sBuilder.append(entry.getKey());
					sBuilder.append("\t||\t");
					sBuilder.append(entry.getValue());
					sBuilder.append("\n");
					bwBufferedWriter.write(sBuilder.toString());
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
