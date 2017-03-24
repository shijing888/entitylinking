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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
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
import com.entitylinking_dbpedia.utils.Parameters;

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
		
		//处理文本上下文
//		String rpath = "./data/dbpedia/short_abstracts_en";
//		String wpath = "./data/dbpedia/short_abstracts_context.ttl";
//		NLPUtils.processAbstract(rpath, wpath);
		
		//分割文件
//		String rpath = "./data/dbpedia/long_abstracts_en.ttl";
//		String wpath = "./data/dbpedia/long_abstracts_en";
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
		
		//处理各实体的流行度
//		String rpath = "./data/dbpedia/entity_popular.txt";
//		String wpath = "./data/dbpedia/entity_popularity2.ttl";
//		removeUriOfPopularity(rpath, wpath);
		
		//将三元组摘要处理成平凡文字
//		String rpath = "./data/dbpedia/short_abstracts_en";
//		String wpath = "./data/dbpedia/short_abstracts_enText.ttl";
//		generateAbstractText(rpath, wpath);
		
//		String rpath = "./data/dbpedia/long_abstracts_en";
//		String wpath = "./data/dbpedia/long_abstracts_enText.ttl";
//		generateAbstractText(rpath, wpath);
		
		//将长短摘要进行合并
//		String rpath1 = "./data/dbpedia/short_abstracts_enText.ttl";
//		String rpath2 = "./data/dbpedia/long_abstracts_enText.ttl";
//		String wpath = "./data/dbpedia/abstracts_enText.ttl";
//		mergeShortLongAbstract(rpath1, rpath2, wpath);
		
		//生成label的平凡实体名称
//		String rpath = "./data/dbpedia/labels_en.ttl";
//		String wpath = "./data/dbpedia/labels_enText.ttl";
//		generateLabelText(rpath, wpath);
		
		//替换连接符
//		String rpath = "./data/dbpedia/short_abstracts_enText.ttl";
//		String wpath = "./data/dbpedia/short_abstracts_enText1.ttl";
//		replaceConnector(rpath, wpath);
		
		//将实体映射成数字标号
//		String rpath = "./data/dbpedia/labels_enText.ttl";
//		String wpath = "./data/dbpedia/labelsNum_enText.ttl";
//		entityNumberMapping(rpath, wpath);
		
		//将类型映射成数字标号
//		String rpath = "./data/dbpedia/infobox_properties_en.ttl";
//		String wpath = "./data/dbpedia/propertiesNum_en.ttl";
//		typeNumberMapping(rpath, wpath);
		
		//反转map
//		String rpath = "./data/dbpedia/labelsNum_enText.ttl";
//		String wpath = "./data/dbpedia/numLabels_enText.ttl";
//		inverseMap(rpath, wpath);
//		
//		String rpath2 = "./data/dbpedia/propertiesNum_en.ttl";
//		String wpath2 = "./data/dbpedia/numProperties_en.ttl";
//		inverseMap(rpath2, wpath2);
		
		//获取实体-边的信息
		String rpath = "./data/dbpedia/infobox_properties_enEntity.ttl";
		String wpath = "./data/dbpedia/entity_edge.ttl";
		getEntityEdgeInfo(rpath, wpath);
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
					bWriter.write(line.toLowerCase()+"\n");
				}else{
					bWriter2.write(line.toLowerCase()+"\n");
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
	 * 老师给的一个popularity文件，将uri去除
	 * @param rpath
	 * @param wpath
	 */
	public static void removeUriOfPopularity(String rpath,String wpath){
		String regex = "^<http://dbpedia.org/resource/(.*?)>(.*)";
		Pattern pattern = Pattern.compile(regex);
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(rpath)),"utf-8"));
			BufferedWriter bWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath)), "utf-8"));
			String line;
			StringBuilder stringBuilder = new StringBuilder();
			while((line = bReader.readLine())!=null){
				Matcher matcher = pattern.matcher(line);
				if(matcher.find()){
//					System.out.println(matcher.group(1));
//					System.out.println(matcher.group(2));
					stringBuilder.delete(0, stringBuilder.length());
					stringBuilder.append(matcher.group(1)).append(matcher.group(2)).append("\n");
				}
				bWriter.write(stringBuilder.toString());
			}
			
			bReader.close();
			bWriter.close();
		} catch (Exception e) {
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
	 * 划分大文件为若干小文件，n为分割文件数量
	 * @param rpath
	 * @param wpath
	 * @param n
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
							new OutputStreamWriter(new FileOutputStream(new File(wpath+"/"+j)), "utf-8"));
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
	 * 合并长短文摘要
	 * @param rpath
	 * @param wpath
	 */
	public static void mergeShortLongAbstract(String rpath1,String rpath2,String wpath){
		Map<String, String> abstractMap = new HashMap<>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(rpath1)), "utf-8"));
			String line;
			String key,value;
			StringBuilder stringBuilder = new StringBuilder();
			//读入短摘要
			while((line = bufferedReader.readLine()) != null){
				String[] lineArray = line.split("\t\\|\\|\t");
				if(lineArray.length == 2){
					key = lineArray[0];
					value = lineArray[1];
					if(!abstractMap.containsKey(key)){
						abstractMap.put(key, value);
					}
				}
			}
			
			//读入长摘要
			bufferedReader = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(rpath2)), "utf-8"));
			int i = 0;
			 while((line = bufferedReader.readLine()) != null){
					String[] lineArray = line.split("\t\\|\\|\t");
					if(lineArray.length == 2){
						key = lineArray[0];
						value = lineArray[1];
						if(!abstractMap.containsKey(key)){
							abstractMap.put(key, value);
						}else{
							System.out.println(i++);
							stringBuilder.delete(0, stringBuilder.length());
							stringBuilder.append(abstractMap.get(key)).append(" ").append(value);
							abstractMap.put(key, stringBuilder.toString());
						}
					}
				}
			 
			 //将合并结果写入文件
			 BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					 new FileOutputStream(new File(wpath)), "utf-8"));
			 for(Entry<String, String> entry:abstractMap.entrySet()){
				 stringBuilder.delete(0, stringBuilder.length());
				 stringBuilder.append(entry.getKey()).append("\t||\t").append(entry.getValue()).append("\n");
				 bufferedWriter.write(stringBuilder.toString());
			 }
			 
			 bufferedReader.close();
			 bufferedWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 将三元组短摘要处理成键值对的形式
	 * @param rpath
	 * @param wpath
	 */
	public static void generateAbstractText(String rpath,String wpath){
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
	
	/**
	 * 将实体映射为数字标号
	 * @param rpath
	 * @param wpath
	 */
	public static void entityNumberMapping(String rpath,String wpath){
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(rpath)), "utf-8"));
			 BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					 new FileOutputStream(new File(wpath)), "utf-8"));
			 int num = 0;
			 String line;
			 StringBuilder stringBuilder = new StringBuilder();
			 while((line = bufferedReader.readLine()) != null){
				 stringBuilder.delete(0, stringBuilder.length());
				 stringBuilder.append(num).append("\t||\t").append(line).append("\n");
				 bufferedWriter.write(stringBuilder.toString());
				 num++;
			 }
			 bufferedReader.close();
			 bufferedWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	/**
	 * 将类型映射为数字标号
	 * @param rpath
	 * @param wpath
	 */
	public static void typeNumberMapping(String rpath,String wpath){
		BufferedWriter writer;
		try {
			StringBuilder stringBuilder = new StringBuilder();
			Model model = ModelFactory.createDefaultModel();
			model.read(rpath);

			Set<String> propertySet = new HashSet<>();
			StmtIterator stmtIterator = model.listStatements();
			String property;
			int i = 0;
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath)), "utf-8"));
			while(stmtIterator.hasNext()){
				Statement statement = stmtIterator.next();
				property = statement.getPredicate().getLocalName();
				if(!propertySet.contains(property)){
					propertySet.add(property);
					stringBuilder.delete(0, stringBuilder.length());
					stringBuilder.append(i).append("\t||\t").append(property).append("\n");
					writer.write(stringBuilder.toString());
					i++;
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
	 * 反转map
	 * @param rpath
	 * @param wpath
	 */
	public static void inverseMap(String rpath,String wpath){
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(rpath)), "utf-8"));
			 BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					 new FileOutputStream(new File(wpath)), "utf-8"));
			 String line;
			 while((line = bufferedReader.readLine()) != null){
				 String[] lineArray = line.split("\t\\|\\|\t");
				 if(lineArray.length == 2){
					 bufferedWriter.write(lineArray[1] + "\t||\t" + lineArray[0] + "\n");
				 }
			 }
			 
			 bufferedReader.close();
			 bufferedWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 处理语义图，获得实体-类型-出边数目-出边集合的四元组
	 * @param rpath
	 * @param wpath
	 */
	public static void getEntityEdgeInfo(String rpath,String wpath){
		BufferedWriter writer;
		try {
			Model model = ModelFactory.createDefaultModel();
			model.read(rpath);
			
			Parameters parameters = new Parameters();
			Map<String, Integer>  numProperties = parameters.loadString2IntegerDict(
					"./data/dbpedia/numProperties_en.ttl");
			Map<String, Integer> numLabels = parameters.loadString2IntegerDict(
					"./data/dbpedia/numLabels_enText.ttl");
			Map<Integer, Map<Integer,EdgeType>> entityEdgeMap = new HashMap<>();
			
			StringBuilder stringBuilder = new StringBuilder();
			StmtIterator stmtIterator = model.listStatements();
			String subject, property, object;
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath)), "utf-8"));
			while(stmtIterator.hasNext()){
				Statement statement = stmtIterator.next();
				subject = statement.getSubject().getLocalName();
				property = statement.getPredicate().getLocalName();
				object = statement.getObject().asResource().getLocalName();
				if(!numLabels.containsKey(subject) || !numProperties.containsKey(property) 
						|| !numLabels.containsKey(object))
					continue;
				int labelNum = numLabels.get(subject);
				int typeNum = numProperties.get(property);
				int objNum = numLabels.get(object);
				System.out.println("sub:"+subject+"\tnum:"+labelNum);
				System.out.println("obj:"+object+"\tnum:"+typeNum);
				System.out.println("pro:"+property+"\tnum:"+objNum);
				//若map中不包含该实体,则创建该实体的边关系bean
				if(!entityEdgeMap.containsKey(labelNum)){
					if(entityEdgeMap.size() > 0){
						for(Entry<Integer, Map<Integer, EdgeType>>entry:entityEdgeMap.entrySet()){
							Map<Integer, EdgeType> edgeTypes = entry.getValue();
							for(Entry<Integer, EdgeType>entry2:edgeTypes.entrySet()){
								stringBuilder.delete(0, stringBuilder.length());
								stringBuilder.append(entry.getKey()).append("\t||\t").append(entry2.getKey())
								.append("\t||\t").append(entry2.getValue().getOutEdgeNumber()).append("\t||\t")
								.append(StringUtils.join(entry2.getValue().getOutEdgeEntity(), "\t|\t")).append("\n");
								writer.write(stringBuilder.toString());
							}
							
						}
						entityEdgeMap.clear();
					}
					
					EdgeType edgeType = new EdgeType();
					edgeType.setTypeNumber(typeNum);
					edgeType.setOutEdgeNumber(1);
					Set<Integer> set = new HashSet<>();
					set.add(objNum);
					edgeType.setOutEdgeEntity(set);
					Map<Integer,EdgeType> edgeMap = new HashMap<>();
					edgeMap.put(typeNum, edgeType);
					entityEdgeMap.put(labelNum, edgeMap);
				}else{
					Map<Integer,EdgeType> edgeMap = entityEdgeMap.get(labelNum);
					//若该条边未存在在map中，则添加进去
					if(!edgeMap.containsKey(typeNum)){
						EdgeType edgeType = new EdgeType();
						edgeType.setTypeNumber(typeNum);
						edgeType.setOutEdgeNumber(1);
						Set<Integer> set = new HashSet<>();
						set.add(objNum);
						edgeType.setOutEdgeEntity(set);
						edgeMap.put(typeNum, edgeType);
					}else{
						//若该条边已经存在，则判断宾语是否在集合中，若不在则添加到集合中
						EdgeType edgeType = edgeMap.get(typeNum);
						Set<Integer> outSet = edgeType.getOutEdgeEntity();
						if(!outSet.contains(objNum)){
							outSet.add(objNum);
							edgeType.setOutEdgeNumber(outSet.size());
						}
					}
					
				}
				
			  System.out.println("map size:"+entityEdgeMap.size()); 
			}
			
			//将map中未保存的实体-边信息保存下来
			if(entityEdgeMap.size() > 0){
				for(Entry<Integer, Map<Integer, EdgeType>>entry:entityEdgeMap.entrySet()){
					Map<Integer, EdgeType> edgeTypes = entry.getValue();
					for(Entry<Integer, EdgeType>entry2:edgeTypes.entrySet()){
						stringBuilder.delete(0, stringBuilder.length());
						stringBuilder.append(entry.getKey()).append("\t||\t").append(entry2.getKey())
						.append("\t||\t").append(entry2.getValue().getOutEdgeNumber()).append("\t||\t")
						.append(StringUtils.join(entry2.getValue().getOutEdgeEntity(), "\t|\t")).append("\n");
						writer.write(stringBuilder.toString());
					}
					
				}
				entityEdgeMap.clear();
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
}
