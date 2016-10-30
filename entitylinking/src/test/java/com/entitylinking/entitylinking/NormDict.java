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
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.entitylinking.utils.NormalizeMention;

import difflib.StringUtills;

public class NormDict {

	private static Map<String, HashSet<String>> synonymsDict = new HashMap<>();
	private static Map<String, HashSet<String>> ambiguationDict = new HashMap<>();
	
	public static void main(String args[]){
		String rpath = "./dict/synonymsDict.txt";
		String wpath = "./dict/synonymsDict2.txt";
		String rpath2 = "./dict/ambiguationDict.txt";
		String wpath2 = "./dict/ambiguationDict2.txt";
		processDict(rpath, wpath, rpath2,wpath2);
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
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(rpath)), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(wpath)),"UTF-8"));
			String line;
			int i=0;
			while((line=bReader.readLine())!=null){
				if(line.length()==0)
					continue;
				if(!isDelete(line)){
					String[] lineArray = line.split("\t\\|\\|\t");
					if(lineArray.length==2){
						if(lineArray[0].length()>0 && lineArray[1].length()>0)
							line = NormalizeMention.getNormalizeMention(lineArray[0],false) + "\t||\t" + StringUtills.join(NormalizeMention.getNormalizeMentionList(lineArray[1].split("\t\\|\t")), "\t|\t");
						if((i++)%10000==0)
							System.out.println("i="+i);
						writer.write(line);
						writer.write("\n");
					}
					
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

}
