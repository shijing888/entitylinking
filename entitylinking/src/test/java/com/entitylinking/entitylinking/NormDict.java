package com.entitylinking.entitylinking;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.entitylinking.config.WikiConfig;
import com.entitylinking.utils.NormalizeMention;
import com.entitylinking_dbpedia.utils.FileUtils;
import com.entitylinking_dbpedia.utils.Parameters;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import difflib.StringUtills;

public class NormDict {

	private static Map<String, HashSet<String>> synonymsDict = new HashMap<>();
	private static Map<String, HashSet<String>> ambiguationDict = new HashMap<>();
	
	public static void main(String args[]){
//		String rpath = "./dict/synonymsDict.txt";
//		String wpath = "./dict/synonymsDict2.txt";
//		String rpath2 = "./dict/ambiguationDict.txt";
//		String wpath2 = "./dict/ambiguationDict2.txt";
//		processDict(rpath, wpath, rpath2,wpath2);
		
//		String rpath = "./dict/entityRelation.txt";
//		String wpath = "./dict/entityRelation2.txt";
//		filterDict(rpath, wpath);
		
//		String rpath1 = "./dict/synonymsDict.txt";
//		String wpath = "./dict/unAmbiguaDict.txt";
//		String rpath2 = "./dict/ambiguationDict.txt";
//		unambiguationDict(rpath1, rpath2, wpath);
		
//		String rpath1 = "./dict/unAmbiguaDict.txt";
//		String wpath = "./dict/unAmbiguaMentionOfText.txt";
//		String rpath2 = "./data/ace2004/RawTexts";
//		getUnambiguaOfText(rpath1, rpath2, wpath);
		
		String rpath = "./dict/disAmbiguationMention.txt";
		String mapPath = "./data/dbpedia/entity_popularity.ttl";
		String wpath = "./dict/disAmbiguationMention3.txt";
		filterUnambiguationDict(rpath,mapPath, wpath);
	}
	
public static void processDict(String rpath,String wpath, String rpath2,String wpath2){
		
		synonymsDict = loadDisambiguationDict(rpath);
		ambiguationDict = loadDisambiguationDict(rpath2);
		
		saveSynonymsDict(wpath);
		saveDisambiguationDict(wpath2);
			
	}
	
public static Map<String, HashSet<String>> loadDisambiguationDict(String path){
	Map<String, HashSet<String>> disambiguationDict = new HashMap<String, HashSet<String>>();
	try {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
		String line;
		while((line = br.readLine())!=null){
			String[] lineArray = line.toLowerCase().split("\t\\|\\|\t");
			if(lineArray.length == 2){
				if(disambiguationDict.containsKey(lineArray[0])){
					disambiguationDict.get(lineArray[0]).addAll(new HashSet<>(Arrays.asList(lineArray[1].split("\t\\|\t"))));
					
				}else {
					HashSet<String> itemSet = new HashSet<>(Arrays.asList(lineArray[1].split("\t\\|\t")));
					disambiguationDict.put(lineArray[0], itemSet);
				}
				
			}
		}
		
		br.close();
		return disambiguationDict;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return disambiguationDict;
}
	
	public static void filterDict(String rpath,String wpath){
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(rpath)), "utf-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(wpath)),"utf-8"));
			String line;
			int i=0;
			while((line=bReader.readLine())!=null){
				if(line.length()==0)
					continue;
				   
					String[] lineArray = line.split("\t\\|\\|\t");
					if(lineArray.length==3){
						if(lineArray[0].length()>0 && lineArray[2].length()>0)
							line = NormalizeMention.getNormalizeMention(lineArray[0],true) 
							+ "\t||\t" + lineArray[1] + "\t||\t" 
							+ StringUtills.join(NormalizeMention.getNormalizeMentionList(
									lineArray[2].split("\t\\|\t"),true), "\t|\t");
						if((i++)%10000==0)
							System.out.println("i="+i);
						writer.write(line);
						writer.write("\n");
					}
					
			}
			bReader.close();
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
	
	public static boolean isDelete(String strin){
		if(strin.contains("Category:")){
			return true;
		}else{
			String lineArray[]=strin.split("\t\\|\\|\t");
			if(lineArray.length==2){
				if(lineArray[0].trim().isEmpty()||lineArray[1].trim().isEmpty()){
					return true;
				}
			}
			
		}
		return false;
		
	}
	
	public static void saveDisambiguationDict(String path){
		int i = 0;
		if(ambiguationDict.size() > 0){
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
				for(Entry<String, HashSet<String>>entry:ambiguationDict.entrySet()){
					sBuilder.delete(0, sBuilder.length());
					sBuilder.append(entry.getKey());
					sBuilder.append("\t||\t");
					sBuilder.append(StringUtils.join(entry.getValue(),"\t|\t"));
					sBuilder.append("\n");
					bwBufferedWriter.write(sBuilder.toString());
					if(i++%1000==0)
						System.out.println(i);
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
	
	public static void saveSynonymsDict(String path){
		int i = 0;
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
				for(Entry<String, HashSet<String>> entry:synonymsDict.entrySet()){
					//将同义词典中对应set长度大于1的放入到歧义词典中
					if(entry.getValue().size() > 1){
						if(!ambiguationDict.containsKey(entry.getKey())){
							ambiguationDict.put(entry.getKey(), entry.getValue());
						}else{
							ambiguationDict.get(entry.getKey()).addAll(entry.getValue());
						}
						continue;
					}
						
					sBuilder.delete(0, sBuilder.length());
					sBuilder.append(entry.getKey());
					sBuilder.append("\t||\t");
					sBuilder.append(entry.getValue());
					sBuilder.append("\n");
					bwBufferedWriter.write(sBuilder.toString());
					if(i++%1000==0)
						System.out.println(i);
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
	
	public static void unambiguationDict(String rpath1,String rpath2,String wpath){
		Parameters parameters = new Parameters();
		Map<String, String> synDict = parameters.loadSynonymsDict(rpath1);
		Map<String, HashSet<String>> ambiguaDict = parameters.loadMapDict(rpath2);
		Map<String, String> map = new HashMap<>();
		System.out.println(synDict.size());
		System.out.println(ambiguaDict.size());
		
		for(Entry<String, String>entry:synDict.entrySet()){
			if(!ambiguaDict.containsKey(entry.getKey())){
				map.put(entry.getKey(), entry.getValue());
			}
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(wpath)), "utf-8"));
			StringBuilder builder = new StringBuilder();
			for(Entry<String, String>entry:map.entrySet()){
				builder.delete(0, builder.length());
				builder.append(entry.getKey()).append("\t||\t").append(entry.getValue());
				builder.append("\n");
				writer.write(builder.toString());
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

	public static void getUnambiguaOfText(String rpath1,String rpath2,String wpath){
		Parameters parameters = new Parameters();
		Map<String, String> map = parameters.loadSynonymsDict(rpath1);
		Map<String,HashSet<String>> result = new HashMap<>();
		File fileDir = new File(rpath2);
		if(fileDir.isDirectory()){
			File[] fileList = fileDir.listFiles();
			String textContent;
			for(File file:fileList){
				String filePath = file.getAbsolutePath();
				textContent = FileUtils.readFileContent(filePath);
				textContent = textContent.replaceAll("\\s+", "_");
				textContent = textContent.toLowerCase();
				HashSet<String>set;
				if(result.containsKey(file.getName())){
					set = result.get(file.getName());
				}else{
					set = new HashSet<>();
				}
				for(Entry<String, String>entry:map.entrySet()){
					if(entry.getValue().length()<3 || entry.getValue().matches(".*\\d+.*"))
						continue;
					if(textContent.contains(entry.getKey())){
						set.add(entry.getValue());
					}
				}
				if(set.size()>0)
					result.put(file.getName(), set);
			}
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(wpath)), "utf-8"));
			StringBuilder builder = new StringBuilder();
			for(Entry<String, HashSet<String>>entry:result.entrySet()){
				builder.delete(0, builder.length());
				builder.append(entry.getKey()).append("\t||\t")
						.append(StringUtils.join(entry.getValue(), "\t|\t"));
				builder.append("\n");
				writer.write(builder.toString());
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
	
	public static void filterUnambiguationDict(String rpath,String mapPath,String wpath){
		Parameters parameters = new Parameters();
		Map<String, HashSet<String>>map = parameters.loadMapDict(rpath);
		Map<String,Integer> entityMap = parameters.loadString2IntegerDict(mapPath);
		Wikipedia wiki = WikiConfig.getWiki();
		Page page;
		int countAll = 0;
		for(Entry<String, HashSet<String>>entry:map.entrySet()){
			HashSet<String>set = entry.getValue();
			HashSet<String>newSet = new HashSet<>();
			Iterator<String>iterator = set.iterator();
			while(iterator.hasNext()){
				String str = iterator.next();
				try {
					page = wiki.getPage(str);
					int num = page.getNumberOfInlinks() + page.getNumberOfOutlinks();
					System.out.println(str+"的流行度是:"+num);
					System.out.println(str+"是否是消歧页:"+page.isDisambiguation());
					if(num < 100 || page.isDisambiguation()){
						continue;
					}
					String name = page.getTitle().getWikiStyleTitle().toLowerCase();
					if(entityMap.containsKey(name))
						newSet.add(name);
				}catch (Exception e) {
					// TODO: handle exception
					iterator.remove();
				}
			}
			countAll += newSet.size();
			map.put(entry.getKey(), newSet);
		}
		System.out.println("平均长度为："+countAll/map.size());
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(wpath)), "utf-8"));
			StringBuilder builder = new StringBuilder();
			for(Entry<String, HashSet<String>>entry:map.entrySet()){
				builder.delete(0, builder.length());
				builder.append(entry.getKey()).append("\t||\t")
						.append(StringUtils.join(entry.getValue(), "\t|\t"));
				builder.append("\n");
				writer.write(builder.toString());
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
