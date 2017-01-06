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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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

/**
 * dbpedia文件的相关处理
 *
 */
public class DBPedia {
	
	static Logger logger = Logger.getLogger(Main.class);
	
	public static void main(String args[]){
		//过滤非实体三元组
//		String rpath = "./data/dbpedia/infobox_properties_en.ttl";
//		String wpath1 = "./data/dbpedia/infobox_properties_enEntity.ttl";
//		String wpath2 = "./data/dbpedia/infobox_properties_enNonEntity.ttl";
//		splitObjectOfInfobox(rpath, wpath1, wpath2);
		
		//处理共现关系
//		String rpath = "./data/dbpedia/infobox_properties_enEntity.ttl";
//		String wpath = "./data/dbpedia/infobox_properties_enCoccurence.ttl";
//		processInfoboxCoocurence(rpath, wpath);
		
		//处理短文本上下文
//		String rpath = "./data/dbpedia/short_abstracts_en";
//		String wpath = "./data/dbpedia/short_abstracts_context.ttl";
//		NLPUtils.processShortAbstract(rpath, wpath);
		
		//分割文件
//		String rpath = "./data/dbpedia/short_abstracts_en.ttl";
//		String wpath = "./data/dbpedia/short_abstracts_en";
//		getSegFiles(rpath, wpath, 5);
		
		//去重重复行
//		String rpath = "./data/dbpedia/infobox_properties_enCoccurence.ttl";
//		String wpath = "./data/dbpedia/infobox_properties_enCoccurence2.ttl";
//		removeRepeatLines(rpath, wpath);
		
		//统计各实体的流行度
//		String rpath1 = "./data/dbpedia/infobox_properties_enCoccurence.ttl";
//		String rpath2 = "./data/dbpedia/infobox_properties_enNonEntity.ttl";
//		String wpath = "./data/dbpedia/entity_popularity.ttl";
//		countPopularityOfEntity(rpath1,rpath2, wpath);
		
		//将三元组短摘要处理成平凡文字
//		String rpath = "./data/dbpedia/short_abstracts_en";
//		String wpath = "./data/dbpedia/short_abstracts_enText.ttl";
//		generateShortAbstractText(rpath, wpath);
		
		//生成label的平凡实体名称
//		String rpath = "./data/dbpedia/labels_en.ttl";
//		String wpath = "./data/dbpedia/labels_enText.ttl";
//		generateLabelText(rpath, wpath);
		
		//替换连接符
		String rpath = "./data/dbpedia/short_abstracts_enText.ttl";
		String wpath = "./data/dbpedia/short_abstracts_enText1.ttl";
		replaceConnector(rpath, wpath);
	}
	
	/**
	 * 按infobox中宾语是否是实体将文件分割成两个文件
	 * @param rpath
	 * @param wpath1,wpath2
	 */
	public static void splitObjectOfInfobox(String rpath,String wpath1,String wpath2){
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(rpath)),"utf-8"));
			BufferedWriter bWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath1)), "utf-8"));
			BufferedWriter bWriter2 = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath2)), "utf-8"));
			String line;
			while((line = bReader.readLine())!=null){
				if(!line.contains("\"")){
					bWriter.write(line+"\n");
				}else{
					bWriter2.write(line+"\n");
				}
				
			}
			bReader.close();
			bWriter.close();
			bWriter2.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 消去重复行
	 * @param rpath
	 * @param wpath
	 */
	public static void removeRepeatLines(String rpath,String wpath){
		Set<String> lines = new HashSet<>();
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(rpath)),"utf-8"));
			BufferedWriter bWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath)), "utf-8"));
			String line;
			while((line = bReader.readLine())!=null){
				if(!lines.contains(line)){
					bWriter.write(line+"\n");
					lines.add(line);
				}
				
			}
			bReader.close();
			bWriter.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 从实体属性中统计实体流行度
	 * @param rpath
	 */
	public static Map<String, Integer> countPopularityOfAttribute(String rpath){
		Map<String, Integer> entityPopularity = new HashMap<>();
		Model model = ModelFactory.createDefaultModel();
		model.read(rpath);
		StmtIterator stmtIterator = model.listStatements();
		String subject;
		int i = 0;
		String prefix = "http://dbpedia.org/resource/";
		int prefixLen = prefix.length();
		while(stmtIterator.hasNext()){
			System.out.println(i++);
			Statement statement = stmtIterator.next();
			subject = statement.getSubject().toString();
//				System.out.println("beigin:"+subject);
			subject = subject.substring(subject.indexOf(prefix)+prefixLen).toLowerCase();
//				System.out.println("after:"+subject);
            if(entityPopularity.containsKey(subject)){
            	entityPopularity.put(subject,entityPopularity.get(subject) + 1);
            }else{
            	entityPopularity.put(subject,1);
            }
			
		}
		
		return entityPopularity;
	}
	
	/**
	 * 统计出各实体的流行度
	 * @param rpath
	 * @param wpath
	 */
	public static void countPopularityOfEntity(String rpath1,String rpath2,String wpath){
		Map<String, Integer> popularityMap = countPopularityOfAttribute(rpath2);
		System.out.println("map size:"+popularityMap.size());
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(rpath1)),"utf-8"));
			String line;
			while((line = bReader.readLine())!=null){
				String[] lineArray = line.split("\t\\|\\|\t");
				if(lineArray.length == 3){
					if(!popularityMap.containsKey(lineArray[0])){
						popularityMap.put(lineArray[0], Integer.parseInt(lineArray[2]));
					}else{
						popularityMap.put(lineArray[0], 
								popularityMap.get(lineArray[0]) + Integer.parseInt(lineArray[2]));
					}
					
					if(!popularityMap.containsKey(lineArray[1])){
						popularityMap.put(lineArray[1], Integer.parseInt(lineArray[2]));
					}else{
						popularityMap.put(lineArray[1], 
								popularityMap.get(lineArray[1]) + Integer.parseInt(lineArray[2]));
					}
				}
				
			}
			
			bReader.close();
			
			BufferedWriter bWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath)), "utf-8"));
			for(Entry<String, Integer>entry:popularityMap.entrySet()){
				bWriter.write(entry.getKey()+"\t||\t"+entry.getValue()+"\n");
			}
			bWriter.close();
		} catch (IOException e) {
			// TODO: handle exception
		}catch (NumberFormatException e) {
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
	 * 获取label文件的平凡实体名称
	 * @param rpath
	 * @param wpath
	 */
	public static void generateLabelText(String rpath,String wpath){
		BufferedWriter writer;
		try {
			File wfile = new File(wpath);
			if(!wfile.exists()){
				wfile.createNewFile();
			}
			
			Model model = ModelFactory.createDefaultModel();
			model.read(rpath);
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(wfile,true), "utf-8"));
			StmtIterator stmtIterator = model.listStatements();
			String subject;
			int i = 0;
			String prefix = "http://dbpedia.org/resource/";
			int prefixLen = prefix.length();
			while(stmtIterator.hasNext()){
				System.out.println(i++);
				Statement statement = stmtIterator.next();
				subject = statement.getSubject().toString();
//				System.out.println("beigin:"+subject);
				subject = subject.substring(subject.indexOf(prefix)+prefixLen).toLowerCase();
//				System.out.println("after:"+subject);
  	            writer.write(subject+"\n");
				
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
	 * 将三元组短摘要处理成键值对的形式
	 * @param rpath
	 * @param wpath
	 */
	public static void generateShortAbstractText(String rpath,String wpath){
		BufferedWriter writer;
		try {
			File rfile = new File(rpath);
			File wfile = new File(wpath);
			if(!wfile.exists()){
				wfile.createNewFile();
			}
			if(rfile.isDirectory()){
				File[] files = rfile.listFiles();
				int i = 0;
				String prefix = "http://dbpedia.org/resource/";
				int prefixLen = prefix.length();
				for(File file2:files){
					rpath = file2.getPath();
					StringBuilder stringBuilder = new StringBuilder();
					Model model = ModelFactory.createDefaultModel();
					model.read(rpath);
					System.out.println("rpath:"+rpath);
					writer = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(wfile,true), "utf-8"));
					StmtIterator stmtIterator = model.listStatements();
					String subject,object;
					while(stmtIterator.hasNext()){
						System.out.println(i++);
						Statement statement = stmtIterator.next();
						subject = statement.getSubject().toString();
						subject = subject.substring(subject.indexOf(prefix)+prefixLen).toLowerCase();
						object = statement.getObject().asLiteral().toString().toLowerCase();
						
						stringBuilder.delete(0, stringBuilder.length());
		  	            stringBuilder.append(subject).append("\t||\t")
		  	            			 .append(object).append("\n");
		  	            
		  	            writer.write(stringBuilder.toString());
						
					}
					
					writer.close();
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
			String prefix = "http://dbpedia.org/resource/";
			int prefixLen = prefix.length();
			while(stmtIterator.hasNext()){
				Statement statement = stmtIterator.next();
				subject = statement.getSubject().toString();
				subject = subject.substring(subject.indexOf(prefix)+prefixLen);
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
	 * 将文件中出现的连接符"-"替换为"_"
	 * @param rpath
	 * @param wpath
	 */
	public static void replaceConnector(String rpath,String wpath){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(rpath)), "utf-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(wpath)), "utf-8"));
			
			String line;
			while((line = reader.readLine()) != null){
				line = line.replace("-", "_");
				writer.write(line + "\n");
			}
			
			reader.close();
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
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
