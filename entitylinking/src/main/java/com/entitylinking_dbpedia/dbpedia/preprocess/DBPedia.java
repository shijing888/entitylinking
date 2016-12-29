package com.entitylinking_dbpedia.dbpedia.preprocess;

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

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.log4j.Logger;
import com.entitylinking_dbpedia.task.Main;
import com.entitylinking_dbpedia.utils.FileUtils;
import com.entitylinking_dbpedia.utils.NLPUtils;

/**
 * dbpedia文件的相关处理
 *
 */
public class DBPedia {
	
	static Logger logger = Logger.getLogger(Main.class);
	
	public static void main(String args[]){
		//过滤非实体三元组
//		String rpath = "./data/dbpedia/infobox_properties_en1.ttl.txt";
//		String wpath = "./data/dbpedia/infobox_properties_en2.ttl";
//		filterObjectOfInfobox(rpath, wpath);
		
		//处理共现关系
//		String rpath = "./data/dbpedia/infobox_properties_en.ttl";
//		String wpath = "./data/dbpedia/infobox_properties_enCoccurence.ttl";
//		processInfoboxCoocurence(rpath, wpath);
		
		//处理短文本上下文
		String rpath = "./data/dbpedia/short_abstracts_en";
		String wpath = "./data/dbpedia/short_abstracts_context.ttl";
		NLPUtils.processShortAbstract(rpath, wpath);
		
		//分割文件
//		String rpath = "./data/dbpedia/short_abstracts_en.ttl";
//		String wpath = "./data/dbpedia/short_abstracts_en";
//		getSegFiles(rpath, wpath, 5);
//		
	}
	
	/**
	 * 过滤掉infobox文件中客体不是实体的三元组
	 * @param rpath
	 * @param wpath
	 */
	public static void filterObjectOfInfobox(String rpath,String wpath){
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(rpath)),"utf-8"));
			BufferedWriter bWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath)), "utf-8"));
			String line;
			while((line = bReader.readLine())!=null){
				if(!line.contains("\"")){
					bWriter.write(line+"\n");
				}
				
			}
			bReader.close();
			bWriter.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 划分大文件为若干小文件
	 * @param rpath
	 * @param wpath
	 */
	public static void getSegFiles(String rpath,String wpath,int n){
		try {
			File file = new File(rpath);
			if(!file.isFile()){
				return;
			}
			int lineNums = FileUtils.getFileLines(rpath);
			int segFileLines = lineNums / n;
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file),"utf-8"));
			BufferedWriter bWriter;
			String line;
			int i=0,j=1;
			bWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath+"/"+j)), "utf-8"));
			while((line = bReader.readLine())!=null){
				if(i++ > segFileLines){
					i = 0;
					j++;
					bWriter.close();
					bWriter = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(new File(rpath+"/"+j)), "utf-8"));
				}
				bWriter.write(line+"\n");
			}
			bReader.close();
			bWriter.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 用于生成实体共现文件
	 * @param rpath
	 * @param wpath
	 */
	public static void processInfoboxCoocurence(String rpath,String wpath){
	
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath)), "utf-8"));
			StringBuilder stringBuilder = new StringBuilder();
			Model model = ModelFactory.createDefaultModel();
			model.read(rpath);

			StmtIterator stmtIterator = model.listStatements();
			String subject,object;
			String queryString;
//			System.out.println("subject:"+stmtIterator.toList().size());
			String count;
			while(stmtIterator.hasNext()){
				Statement statement = stmtIterator.next();
				subject = statement.getSubject().toString();
				subject = subject.substring(subject.lastIndexOf("/")+1);
				object = statement.getObject().asResource().toString();
				object = object.substring(object.lastIndexOf("/")+1);
				queryString = 	"PREFIX resource:<http://dbpedia.org/resource/> " +
						"SELECT (count(?type) as ?typeCount) " +
						"WHERE {" + 
						"	resource:" + subject + " ?type " + " resource:" + object + " ." +
						"      }";
				try {
					Query query = QueryFactory.create(queryString);

				    // 创建查询执行对象
				    QueryExecution queryExecution = QueryExecutionFactory.create(query,
				            model);

				    // 执行查询，生成结果
				    ResultSet rs = queryExecution.execSelect();
				    stringBuilder.delete(0, stringBuilder.length());
				    while (rs.hasNext()) {
				        QuerySolution qs = rs.nextSolution();
				        count = qs.get("typeCount").toString();
				        count =	count.substring(0,count.indexOf("^"));
				        if(count.equals("0")){
				        	continue;
				        }
				        stringBuilder.append(subject.toLowerCase()).append("\t||\t").append(object.toLowerCase())
				       				 .append("\t||\t").append(count).append("\n");
				    }
				    
				    writer.write(stringBuilder.toString());
				    writer.flush();
				} catch (Exception e) {
					// TODO: handle exception
				}
			   
			}
			
			writer.close();
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
	
	/**
	 * 查询模型，返回结果集
	 * @param path
	 * @param queryString
	 * @return
	 */
	public static ResultSet queryResult(String path, String queryString) {
	    // 创建模型
	    Model model = ModelFactory.createDefaultModel();
	    model.read(path);

	    // 创建查询
	    Query query = QueryFactory.create(queryString);

	    // 创建查询执行对象
	    QueryExecution queryExecution = QueryExecutionFactory.create(query,
	            model);

	    // 执行查询，生成结果
	    ResultSet rs = queryExecution.execSelect();
	    return rs;
	}
	
	public static ResultSet queryResult(Model model, String queryString) {
	    // 创建查询
	    Query query = QueryFactory.create(queryString);

	    // 创建查询执行对象
	    QueryExecution queryExecution = QueryExecutionFactory.create(query,
	            model);

	    // 执行查询，生成结果
	    ResultSet rs = queryExecution.execSelect();
	    return rs;
	}
}
