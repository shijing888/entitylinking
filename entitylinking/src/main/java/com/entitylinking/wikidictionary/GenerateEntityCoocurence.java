package com.entitylinking.wikidictionary;

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
import java.util.Map;
import java.util.Map.Entry;

//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

/**
 * 用于生成实体之间的关系
 * @author HP
 *
 */
public class GenerateEntityCoocurence {

	/**实体入度词典*/
	private Map<String, Integer> entityCoocurence = new HashMap<String, Integer>();
//	static Logger logger = Logger.getLogger(GenerateEntityCoocurence.class);
//	static{
//		PropertyConfigurator.configure("log4j.properties");
//	}
	public static void main(String args[]){
		String rPath = "./dict/entityRelation4.txt";
		String 	wPath = "./dict/coEntitiesCount/coEntitiesCount_";
		GenerateEntityCoocurence generateEntityRelation = new GenerateEntityCoocurence();
		generateEntityRelation.countCoocurence(rPath, wPath);
	}
	
	public void countCoocurence(String rPath, String wPath){
		try {
			String line;
			int len;
			int k=0;
			String str1,str2;
			StringBuilder str3 = new StringBuilder();
			String[] alphabet = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q",
					"r","s","t","u","v","w","x","y","z"};
		
			StringBuilder stringBuilder = new StringBuilder();
			for(int m=0;m<alphabet.length;m++){
				for(int n=m;n<alphabet.length;n++){
					entityCoocurence.clear();
					String path = wPath + m +"_"+ n +".txt";
					@SuppressWarnings("resource")
					BufferedReader bReader = new BufferedReader(new InputStreamReader(
							new FileInputStream(new File(rPath)), "utf-8"));
					while((line=bReader.readLine())!=null){
						stringBuilder.delete(0, stringBuilder.length());
						System.out.println(stringBuilder.append("m:").append(m).append("\tn:")
								.append(n).append("\tk:").append(k++));
//						System.out.println(line);
						String[] lineArray = line.split("\t\\|\\|\t");
						if(lineArray.length == 3){
//							System.out.println(lineArray[2]);
							String[] coEntities = lineArray[2].split("\t\\|\t");
							len = coEntities.length;
							for(int i=0;i<len-1;i++){
								for(int j=i+1;j<len;j++){
//									System.out.println(coEntities[i]);
									if(coEntities[i].split("\t").length==2 && coEntities[j].split("\t").length==2){
										str3.delete(0, str3.length());
										str1 = coEntities[i].split("\t")[0];
										str2 = coEntities[j].split("\t")[0];
										if(str1.startsWith(alphabet[m]) && str2.charAt(0) >= alphabet[m].charAt(0)){
											str3.append(str1).append("\t").append(str2);
										}else if(str2.startsWith(alphabet[m]) && str1.charAt(0) 
												>= alphabet[m].charAt(0)){
											str3.append(str2).append("\t").append(str1);
										}
										if(str3.length()>0){
											if(!entityCoocurence.containsKey(str3.toString())){
												entityCoocurence.put(str3.toString(), 1);
											}else {
												entityCoocurence.put(str3.toString(), 
														entityCoocurence.get(str3.toString())+1);
											}
										}
										
									}
									
								}
							}
						}
					}
					saveCoEntitiesDict(path);
				}
		
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveCoEntitiesDict(String path){
//		logger.info("持久化到本地文件begin！");
		if(entityCoocurence.size() > 0){
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
				StringBuilder stringBuilder = new StringBuilder();
				for(Entry<String, Integer>entry:entityCoocurence.entrySet()){
					stringBuilder.delete(0, stringBuilder.length());
					stringBuilder.append(entry.getKey()).append("\t||\t").append(entry.getValue()).append("\n");
					bwBufferedWriter.write(stringBuilder.toString());
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
