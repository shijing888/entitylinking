package com.entitylinking.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import com.entitylinking.utils.NormalizeMention;
import com.entitylinking.wiki.bean.PageBean;

import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * 通过wikipedia生成同义歧义词典
 * @author shijing
 *
 */
public class GenerateDictionary2 {

	/**
	 * disambiguationDict
	 * 			歧义词典
	 * synonymsDict
	 * 			同义词典
	 * pageIdSet
	 * 			page表中所有id集合
	 */
	private HashMap<String, HashSet<String>> disambiguationDict = new HashMap<String, HashSet<String>>();
	private HashMap<String, String> synonymsDict = new HashMap<String, String>();
	private Session session = null;
	
	/**
	 * 用于找出文本中出现的实体
	 */
	private static final String ENTITY = "\\[\\[([\\s\\S]*?)\\]\\]";
	public static Pattern pattern = Pattern.compile(ENTITY);
	private Matcher matcher = null;
	
	/**
	 * MAXRECORDS
	 * 			每次查询出的最大记录数目；
	 * MAXDICTCOUNT
	 * 			歧义词集合中最大元素数目,超过该值则持久化到本地；
	 * MAXRECORDCOUNTS
	 * 			page中最大的id值
	 * PAGECOUNTS
	 * 			page表中所有实体数目；
	 * PAGEMAPLINECOUNTS
	 * 			pagemapline表中所有记录数目；
	 */
	private static final int MAXRECORDS = 10000;
	private static final int MAXDICTCOUNT = 1000;
	private static final int PAGECOUNTS = 5240554;
	private static final int MAXRECORDCOUNTS = 51484878;
	private static final int PAGEMAPLINECOUNTS = 12729405;
	
	static Logger logger = Logger.getLogger(GenerateDictionary2.class);
	static{
		PropertyConfigurator.configure("./log4j.properties");
	}
	
	public GenerateDictionary2(){
		session = HibernateSession.getSession();
	}
	
	public static void main(String args[]){
		GenerateDictionary2 generateDictionary  = new GenerateDictionary2();
		String savesynonymsDictPath = "./dict/synonymsDict2.txt";
		generateDictionary.readDict("./dict/synonymsDict.txt");
		logger.info("dict size:"+generateDictionary.synonymsDict.size());
		generateDictionary.recordPageTable(savesynonymsDictPath);
	}
	
	/**
	 * 从page表中生成词典
	 * @throws WikiApiException 
	 */
	@SuppressWarnings("unchecked")
	public void recordPageTable(String savesynonymsDictPath){
        List<PageBean> pageList = new ArrayList<PageBean>();
        //根据循环进行数据库的IO，避免内存不足
        for(int i=0;i<MAXRECORDCOUNTS;i+=MAXRECORDS){
        	//开始事务
            Transaction transaction = session.beginTransaction();  
            //查询sql
            String sqlString = "select new com.entitylinking.wiki.bean.PageBean(name,text)"
            					+" from PageBean page where page.id >=" + i +" and page.id < "+ (i+MAXRECORDS);
            Query query = session.createQuery(sqlString);
           
            pageList = query.list();
            transaction.commit();
           
            String title;
            for(PageBean pageBean : pageList){
            	title = NormalizeMention.getNormalizeMention(pageBean.getName(), true);
            	if(title.length() < 2 && synonymsDict.containsKey(title))
					continue;
				synonymsDict.put(title, title);
            }
            System.out.println(i+"\t"+"synonymsDict.size():"+synonymsDict.size());
            pageList.clear();
            System.gc();

        }  
        //用于持久化歧义词典
        saveSynonymsDict(savesynonymsDictPath);
        
        HibernateSession.closeSession();
	}
	
	
	public void saveDisambiguationDict(String path){
		if(disambiguationDict.size() > 0){
//			System.out.println("disambiguationDict.size():"+disambiguationDict.size());
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
				BufferedWriter bwBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"utf-8"));
				StringBuilder sBuilder = new StringBuilder();
				for(Entry<String, HashSet<String>>entry:disambiguationDict.entrySet()){
					if(entry.getValue().size()>1){
						sBuilder.delete(0, sBuilder.length());
						sBuilder.append(entry.getKey());
						sBuilder.append("\t||\t");
						sBuilder.append(StringUtils.join(entry.getValue(),"\t"));
						sBuilder.append("\n");
						bwBufferedWriter.write(sBuilder.toString());
					}
					
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
	
	public void readDict(String path){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "utf-8"));
			String line;
			while((line=br.readLine())!=null){
				String lineArray[]=line.split("\t\\|\\|\t");
				if(lineArray.length==2){
					synonymsDict.put(lineArray[0], lineArray[1]);
				}
				
			}
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
				BufferedWriter bwBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"utf-8"));
				StringBuilder sBuilder = new StringBuilder();
				for(Entry<String, String> entry:synonymsDict.entrySet()){
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
