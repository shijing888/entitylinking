package com.entitylinking_dbpedia.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.queryparser.classic.ParseException;

import org.apache.commons.lang.StringUtils;
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

import com.entitylinking_dbpedia.linking.bean.RELRWParameterBean;
import com.entitylinking_dbpedia.utils.Parameters;

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

//	private static String[] entityCoocurCountsFields = new String[]{RELRWParameterBean.getEntityRelationField1(),
//													RELRWParameterBean.getEntityRelationField2()};
	private static String[] entityCoocurCountsFields = new String[]{"entity1","entity2"};
	private static BooleanClause.Occur[] flags = new BooleanClause.Occur[]
													{BooleanClause.Occur.MUST,BooleanClause.Occur.MUST};
	public static void main(String args[]){
		//实体共现关系索引创建
//		//索引文件夹
		String indexDir1 = "./index/entityByDbpediaRelationIndex";
		//需要创建索引的文件
		String filePath1 = "./data/dbpedia/infobox_properties_enCoccurence.ttl";
		//索引的字段
		String[] fields1 = new String[]{"entity1","entity2","entityCoocurCount"};
		creatIndex(filePath1, indexDir1, fields1);
		
		//实体文本摘要索引创建
		//索引文件夹
//		String indexDir2 = "./index/abstractTextIndex";
//		//需要创建索引的文件
//		String filePath2 = "./data/dbpedia/abstracts_enText.ttl";
//		//索引的字段
//		String[] fields2 = new String[]{"entity","abstractText"};
//		creatIndex(filePath2, indexDir2, fields2);
		
//		//同义词典索引创建
//		//索引文件夹
//		String indexDir3 = "./index/synonymsIndex";
//		//需要创建索引的文件
//		String filePath3 = "./dict/synonymsDict.txt";
//		//索引的字段
//		String[] fields3 = new String[]{"synonymsKey","synonymsItems"};
//		creatIndex(filePath3, indexDir3, fields3);

		//歧义词典索引创建
		//索引文件夹
//		String indexDir4 = "./index/ambiguationIndex";
//		//需要创建索引的文件
//		String filePath4 = "./dict/ambiguationDict.txt";
//		//索引的字段
//		String[] fields4 = new String[]{"ambiguationKey","ambiguationItems"};
//		creatIndex(filePath4, indexDir4, fields4);
		
//		//label实体标记索引创建
//		//索引文件夹
//		String indexDir5 = "./index/labelIndex";
//		//需要创建索引的文件
//		String filePath5 = "./data/dbpedia/labels_enText.ttl";
//		//索引的字段
//		String[] fields5 = new String[]{"labelName"};
//		creatIndex(filePath5, indexDir5, fields5);
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
			queryString = QueryParser.escape(queryString);;
			Query query = singleQueryParser.parse(queryString);
			TopDocs topDocs = indexSearcher.search(query,1);
			//7. 处理查询结果
			if(topDocs != null && topDocs.scoreDocs.length > 0){
				return indexSearcher.doc(topDocs.scoreDocs[0].doc);
			}
			
		}catch (ParseException e2) {
			// TODO: handle exception
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 查询两个实体之间成边的次数
	 * 查询共现的时候，分词器会按照逗号把词给分开，转义会对括号转义
	 * @param querys
	 * @param indexDir
	 * @return
	 */
	public static int entityCoocurCounts(String[] querys, String indexDir){
		int count = 0;
		if(querys.length == 2){
			querys[0] = QueryParser.escape(querys[0]);
			querys[1] = QueryParser.escape(querys[1]);
		}
		try {
			initAnalyzer();
			initIndexSearcher(indexDir);
			//5. 创建查询解析器，解析query
			Query query = MultiFieldQueryParser.parse(querys, entityCoocurCountsFields, flags,analyzer);
			ScoreDoc[] scoreDocs = indexSearcher.search(query, MAXCoocurence).scoreDocs;
			if(scoreDocs.length > 0){
				Document document = indexSearcher.doc(scoreDocs[0].doc);
//				count = Integer.parseInt(document.get(RELRWParameterBean.getEntityRelationField3()));
				count = Integer.parseInt(document.get("entityCoocurCount"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return count;
	}
	
	/**
	 * 将共现实体查询出来
	 * @param querys
	 * @param queryFields
	 * @param flags
	 * @param indexDir
	 * @return
	 */
	public static Set<String> coocurenceEntities(String[] querys,String[] queryFields, String queryInex,
			BooleanClause.Occur[] flags,String indexDir){
		Set<String> entitySet = new HashSet<>();
		try {
			initAnalyzer();
			initIndexSearcher(indexDir);
			for(int i=0;i<querys.length;i++){
				querys[i] = QueryParser.escape(querys[i]);
			}
			Query query = MultiFieldQueryParser.parse(querys, queryFields, flags,analyzer);
			ScoreDoc[] scoreDocs = indexSearcher.search(query, MAXCoocurence).scoreDocs;
			for(ScoreDoc scoreDoc:scoreDocs){
				Document document = indexSearcher.doc(scoreDoc.doc);
				String arr[] = document.get(queryInex).split("\t\\|\t");
				entitySet.addAll(Arrays.asList(arr));
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
	 * 通过lucene查询词的候选
	 * @param queryString
	 * @param queryField
	 * @param indexDir
	 * @return
	 */
	public static Set<String> queryCandidateLabel(String queryString,String queryField,String indexDir){
		Set<String> entitySet = new HashSet<>();
		try {
			queryString = QueryParser.escape(queryString);
			initAnalyzer();
			initIndexSearcher(indexDir);
			singleQueryParser = singleQueryParserOfEntityCoocurence(queryField);
			//解析查询字符串获取查询对象
			Query query = singleQueryParser.parse(queryString);
			TopDocs topDocs = indexSearcher.search(query,RELRWParameterBean.getCandidateEntityNumThresh());
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for(ScoreDoc scoreDoc:scoreDocs){
				entitySet.add(indexSearcher.doc(scoreDoc.doc).get(queryField));
//				System.out.println(indexSearcher.doc(scoreDoc.doc).get(queryField));
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
	 * 初始化indexSearcher
	 */
	public static void initIndexSearcher(String indexDir){
		
		
		try {
			//1. 获取索引文件目录
			Directory directory = FSDirectory.open(Paths.get(indexDir));
			//2. 创建IndexReader对象，读取索引文件
			IndexReader indexReader = DirectoryReader.open(directory);
			//3. 创建索引查询器，查询索引文件
			indexSearcher = new IndexSearcher(indexReader);
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

		singleQueryParser = new QueryParser(queryField, analyzer);
		return singleQueryParser;
	} 
	
	@Test
	public void test(){
		String[] querys = {"america,_oklahoma","u.s._northeast"};
		String[] querys1 = {"neoconservatism_(america)","america/yellowknife"};
		String indexDir = "./index/entityByDbpediaRelationIndex";
//		String[] queryFields = new String[]{"entity1","entity1"};
//		String[] queryFields2 = new String[]{"entity2","entity2"};
		
//		String qString ="northeast";
//		queryCandidateLabel(qString, "labelName", "./index/labelIndex");
//		Document document = queryDocument(qString, "ambiguationKey", "./index/ambiguationIndex");
//		Document document = queryDocument(qString, "entity", "./index/abstractTextIndex");
//		System.out.println(document.get("entity"));
//		System.out.println(document.get("abstractText"));
//		System.out.println("---------");
//		Document document2 = queryDocument(qString, "entity", "./index/short_abstractTextIndex");
//		System.out.println(document2.get("entity"));
//		System.out.println(document2.get("abstractText"));
//		System.out.println("doc1:"+document.toString());
//		qString ="china";
//		document = queryDocument(qString, "ambiguationKey", "./index/ambiguationIndex");
//		System.out.println(document.get("ambiguationKey"));
//		System.out.println("doc2:"+document.toString());
//		Set<String> set = coocurenceEntities(querys1, queryFields,"entity2",flags, indexDir);
//		System.out.println(StringUtils.join(set, " "));
//		Set<String> set2 = coocurenceEntities(querys1, queryFields2,"entity1",flags, indexDir);
		int count = entityCoocurCounts(querys1, indexDir);
		System.out.println(count);
//		count += entityCoocurCounts(querys2, indexDir);
//		int count = countSingleOccurence(qString, indexDir);
//		System.out.println(count);
//		Document document = queryDocument(ss.replaceAll("/", "//"), "entityRelationValue", "./index/entityRelationIndex");
//		System.out.println(document.get("entityRelationValue"));

	}
}
