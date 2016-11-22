package com.entitylinking.entitylinking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneTest {

	public static void main(String args[]){
//		String path = "./dict/synonymsDict.txt";
//		creatIndex(path);
		queryDocument("baczki", "synonymsKey");
	}
	public static void creatIndex(String path){
		IndexWriter indexWriter = null;
		try {
			//1. 创建目录
			Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("./index"));
			//2. 创建IndexWriter
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, indexWriterConfig);
			//3. 创建文件对象
			File file  = new File(path);
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line ;
			while((line = bReader.readLine())!= null){
				//4. 创建Document对象
				Document document = new Document();
				String[] lineArray = line.split("\t\\|\\|\t");
				if(lineArray.length == 2){
					document.add(new Field("synonymsKey", lineArray[0], TextField.TYPE_STORED));
					document.add(new Field("synonymsValue", lineArray[1], TextField.TYPE_STORED));
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
	
	public static void queryDocument(String queryString,String queryField){
		
		try {
			//1. 获取索引文件目录
			Directory directory = FSDirectory.open(Paths.get("./index"));
			//2. 创建IndexReader对象，读取索引文件
			IndexReader indexReader = DirectoryReader.open(directory);
			//3. 创建索引查询器，查询索引文件
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			//4. 实例化分析器
			Analyzer analyzer = new StandardAnalyzer();
			//5. 创建查询解析器，解析query
			QueryParser queryParser = new QueryParser(queryField, analyzer);
			//6. 解析查询字符串获取查询对象
			Query query = queryParser.parse(queryString);
			TopDocs topDocs = indexSearcher.search(query, 10);
			//7. 处理查询结果
			for(ScoreDoc scoreDoc:topDocs.scoreDocs){
				Document document = indexSearcher.doc(scoreDoc.doc);
				System.out.println(document.get("synonymsKey")+'\t'+document.get("synonymsValue")+'\t'+scoreDoc.score);
			}
			
			//
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
