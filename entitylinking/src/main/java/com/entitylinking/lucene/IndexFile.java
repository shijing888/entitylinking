package com.entitylinking.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.entitylinking.linking.bean.RELRWParameterBean;

/**
 * 文件索引操作
 * @author HP
 *
 */
public class IndexFile {

	static Logger logger = Logger.getLogger(IndexFile.class);
	private static final int MAXCoocurence = 999;
	private static final int MAXSingleOccurence = 99999;
	private static Analyzer analyzer;
	private static QueryParser singleQueryParser;
	private static IndexSearcher indexSearcher;
	private static String[] cooccurenceQueryFields = new String[]{RELRWParameterBean.getEntityRelationField3(),
													RELRWParameterBean.getEntityRelationField3()};
	private static BooleanClause.Occur[] flags = new BooleanClause.Occur[]
													{BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};
	public static void main(String args[]){
		
		//索引文件夹
		String indexDir1 = "./index/synonymsIndex";
		String indexDir2 = "./index/ambiguationIndex";
		String indexDir3 = "./index/entityRelationIndex";
		//需要创建索引的文件
		String filePath1 = "./dict/synonymsDict.txt";
		String filePath2 = "./dict/ambiguationDict.txt";
		String filePath3 = "./dict/entityRelation.txt";
		//索引的字段
		String[] fields1 = new String[]{"synonymsDictKey","synonymsDictValue"}; 
		String[] fields2 = new String[]{"ambiguationDictKey","ambiguationDictValue"}; 
		String[] fields3 = new String[]{"entityRelationKey","entityCount","entityRelationValue"}; 
		
		creatIndex(filePath1, indexDir1, fields1);
		creatIndex(filePath2, indexDir2, fields2);
		creatIndex(filePath3, indexDir3, fields3);
	}
	
	/**
	 * 为指定文件创建索引
	 * @param filePath
	 * @param indexDir
	 * @param fields
	 */
	public static void creatIndex(String filePath, String indexDir, String[] fields){
		IndexWriter indexWriter = null;
		try {
			//1. 创建目录
			Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(indexDir));
			//2. 创建IndexWriter
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, indexWriterConfig);
			//3. 创建文件对象
			File file  = new File(filePath);
			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			String line ;
			while((line = bReader.readLine())!= null){
				//4. 创建Document对象
				Document document = new Document();
				String[] lineArray = line.split("\t\\|\\|\t");
				if(lineArray.length == fields.length){
					for(int i=0;i<lineArray.length;i++){
						document.add(new Field(fields[i], lineArray[i], 
								TextField.TYPE_STORED));
					}
					//5. 将文档添加到indexWriter对象中
					indexWriter.addDocument(document);
				}
			}
			bReader.close();
			indexWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 通过索引查询关键词所对应的document
	 * @param queryString
	 * @param queryField
	 */
	public static Document queryDocument(String queryString, String queryField, String indexDir){
		try {
			initAnalyzer();
			initIndexSearcher(indexDir);
			singleQueryParserOfEntityCoocurence(queryField);
			//6. 解析查询字符串获取查询对象
			Query query = singleQueryParser.parse(queryString);
			TopDocs topDocs = indexSearcher.search(query,1);
			//7. 处理查询结果
			if(topDocs != null && topDocs.scoreDocs.length > 0){
				return indexSearcher.doc(topDocs.scoreDocs[0].doc);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Set<String> coocurenceEntities(String[] querys,String[] queryFields, 
			BooleanClause.Occur[] flags,String indexDir){
		Set<String> entitySet = new HashSet<>();
		try {
			initAnalyzer();
			initIndexSearcher(indexDir);
			//5. 创建查询解析器，解析query
			Query query = MultiFieldQueryParser.parse(querys, queryFields, flags,analyzer);
			ScoreDoc[] scoreDocs = indexSearcher.search(query, MAXCoocurence).scoreDocs;
			for(ScoreDoc scoreDoc:scoreDocs){
				entitySet.addAll(Arrays.asList(indexSearcher.doc(scoreDoc.doc)
						.get(queryFields[0]).split("\t\\|\t")));
			}
			for(String str:querys){
				if(entitySet.contains(str)){
					entitySet.remove(str);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return entitySet;
	} 
	
	/**
	 * 查询某个实体的出现次数
	 * @param queryString
	 * @param indexDir
	 * @return
	 */
	public static int countSingleOccurence(String queryString, String indexDir){
		try {
			queryString = QueryParser.escape(queryString);
			initAnalyzer();
			initIndexSearcher(indexDir);
			singleQueryParser = singleQueryParserOfEntityCoocurence(RELRWParameterBean.getEntityRelationField3());
			//解析查询字符串获取查询对象
			Query query = singleQueryParser.parse(queryString);
			TopDocs topDocs = indexSearcher.search(query,MAXSingleOccurence);
			//7. 处理查询结果
			if(topDocs != null){
				return topDocs.scoreDocs.length;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	/**
	 * 通过查询共现次数来获取实体图中边的权重
	 * @param querys
	 * @param queryFields
	 * @param indexDir
	 * @return
	 */
	public static int countCooccurence(String[] querys, String indexDir){
		int count = 0;
		if(querys.length == 2){
			querys[0] = QueryParser.escape(querys[0]);
			querys[1] = QueryParser.escape(querys[1]);
		}
		try {
			initAnalyzer();
			initIndexSearcher(indexDir);
			//5. 创建查询解析器，解析query
			Query query = MultiFieldQueryParser.parse(querys, cooccurenceQueryFields, flags,analyzer);
//			ScoreDoc[] scoreDocs = indexSearcher.search(query, MAXTOP).scoreDocs;
//			for(ScoreDoc scoreDoc:scoreDocs){
//				System.out.println(indexSearcher.doc(scoreDoc.doc).get(queryFields[0]));
//			}
			count = indexSearcher.search(query, MAXCoocurence).scoreDocs.length;
//			logger.info(querys[0]+"\t"+querys[1]+"\t共现次数:"+count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return count;
	}
	
	/**
	 * 初始化indexSearcher
	 */
	public static void initIndexSearcher(String indexDir){
		try {
			if(indexSearcher == null){
				//1. 获取索引文件目录
				Directory directory = FSDirectory.open(Paths.get(indexDir));
				//2. 创建IndexReader对象，读取索引文件
				IndexReader indexReader = DirectoryReader.open(directory);
				//3. 创建索引查询器，查询索引文件
				if(indexSearcher == null)
					indexSearcher = new IndexSearcher(indexReader);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	/**
	 * 初始化标准解析器
	 */
	public static void initAnalyzer(){
		if(analyzer == null){
			analyzer = new StandardAnalyzer();
		}
		
	}
	
	/**
	 * 返回指定搜索域下的queryParser
	 * @param queryField
	 * @return
	 */
	public static QueryParser singleQueryParserOfEntityCoocurence(String queryField){

		if(singleQueryParser == null){
			singleQueryParser = new QueryParser(queryField, analyzer);
		}
		return singleQueryParser;
	} 
	
	@Test
	public void test(){
//		String[] querys = {"greens/green_party_usa","green"};
		String indexDir = "./index/entityRelationIndex";
		String qString ="china";
//		coocurenceEntities(querys, queryFields,flags, indexDir);
//		int count = countCooccurence(querys, indexDir);
		int count = countSingleOccurence(qString, indexDir);
		System.out.println(count);
//		Document document = queryDocument(ss.replaceAll("/", "//"), "entityRelationValue", "./index/entityRelationIndex");
//		System.out.println(document.get("entityRelationValue"));
		//		 String sql="1' or '1'='1";  
//        System.out.println("防SQL注入:"+StringEscapeUtils.escapeSql(sql)); //防SQL注入  
//          
//        System.out.println("转义HTML,注意汉字:"+StringEscapeUtils.escapeHtml("<font>chen磊  xing</font>"));    //转义HTML,注意汉字  
//        System.out.println("反转义HTML:"+StringEscapeUtils.unescapeHtml("<font>chen磊  xing</font>"));  //反转义HTML  
//          
//        System.out.println("转成Unicode编码："+StringEscapeUtils.escapeJava("陈磊兴"));     //转义成Unicode编码  
//          
//        System.out.println("转义XML："+StringEscapeUtils.escapeXml("<name>陈磊兴</name>"));   //转义xml  
//        System.out.println("反转义XML："+StringEscapeUtils.unescapeXml("<name>陈磊兴</name>"));    //转义xml  
	}
}
