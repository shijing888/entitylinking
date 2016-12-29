package com.entitylinking.entitylinking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;


public class FilterDict {

	private static int editDistTresh = 3;
	public static void main(String args[]){
		
//		String rpath = "H:\\MysqlData\\ambiguationDict2.txt";
//		String wpath = "H:\\MysqlData\\ambiguationDict3.txt";
//		filterCategory(rpath, wpath);
		String rpath = "./dict/ambiguationDict.txt";
		String wpath = "./dict/ambiguationDict2.txt";
//		filterBracket(rpath, wpath);
//		filterSingleWord(rpath, wpath);
		filterCandidate(rpath,wpath);
	}
	
	public static void filterCategory(String rpath, String wpath){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(rpath))));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(wpath)),"utf-8"));
			String line;
			StringBuilder sBuilder = new StringBuilder();
			int count = 0;
			while((line=br.readLine())!=null){
				String[] strArray = line.split("\t\\|\\|\t");
				if(strArray.length==2){
					sBuilder.delete(0, sBuilder.length());
					sBuilder.append(strArray[0]).append("\t||\t");
					String[] dictArray = strArray[1].split("\t");
					for(String str:dictArray){
						if(!str.contains(":") && str.length()<50){
							sBuilder.append(str).append("\t|\t");
							count++;
						}
					}
					if(count >1){
						sBuilder.append("\n");
						bw.write(sBuilder.toString());
					}
				
				}
			}
			bw.close();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void filterBracket(String rpath, String wpath){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(rpath))));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(wpath)),"utf-8"));
			String line;
			StringBuilder sBuilder = new StringBuilder();
			while((line=br.readLine())!=null){
				sBuilder.delete(0, sBuilder.length());
				line = line.replaceAll("[\\[\\]]", "");
				sBuilder.append(line).append("\n");
				bw.write(sBuilder.toString());
			}
			bw.close();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void filterSingleWord(String rpath, String wpath){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(rpath))));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(wpath)),"utf-8"));
			String line;
			while((line=br.readLine())!=null){
				String[] lineArray = line.split("\t\\|\\|\t");
				if(lineArray.length > 0){
					if(lineArray[0].length() < 2)
						continue;
				}
				bw.write(line+"\n");
			}
			bw.close();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void filterCandidate(String rPath,String wPath){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(rPath))));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(wPath)),"utf-8"));
			String line,key,value;
			while((line=br.readLine())!=null){
				line = line.replace("&nbsp;", "_");
				String[] lineArray = line.split("\t\\|\\|\t");
				if(lineArray.length == 2){
					key = lineArray[0];
					value = lineArray[1];
					line = judgeString(line,key, value);
					if(line != null){
						bw.write(line+"\n");
					}
					
				}
			}
				
			bw.close();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String judgeString(String line,String s1,String s2){
		StringBuilder stringBuilder = new StringBuilder();
		if(s1.contains("disambiguation")){
			return line;
		}
		
		boolean isContain = false;
		stringBuilder.append(s1).append("\t||\t");
		Set<String> candidateSet = new HashSet<>();
		String[] keyArray = s1.split("[_]+");
		String[] valueArray = s2.split("\t\\|\t");
		for(String value:valueArray){
			isContain = false;
			//首先判断是否包含缩写
			if(keyArray.length == 1){
				String[] subValueArray = value.split("[_]+");
				if(subValueArray.length > 1){
					String result = "";
//					System.out.println(value);
					for(String str:subValueArray){
						result+=str.charAt(0);
					}
					if(result.contains(s1)){
						isContain = true;
						candidateSet.add(value);
					}
				}
				
			}else{
				//判断是否部分包含
				isContain = false;
				for(String item:keyArray){
					if(value.contains(item)){
						isContain = true;
						break;
					}
				}
				//value包含key中一部分
				if(isContain){
					isContain = true;
					candidateSet.add(value);
				}
			}
			//使用编辑距离来判断
			if(!isContain){
				int minEditDist = value.length();
				int dist;
				String[] subValueArray = value.split("[_]+");
				for(String item1:keyArray){
					for(String item2:subValueArray){
						dist = EditDistance.getEditDistance(item1, item2);
						if(minEditDist > dist){
							minEditDist = dist;
						}
					}
				}
				
				if(minEditDist <= editDistTresh){
					candidateSet.add(value);
				}
			}
		}
		if(candidateSet.size() > 0){
			stringBuilder.append(StringUtils.join(candidateSet,"\t|\t"));
			return stringBuilder.toString();
		}
		return null;
	}
	
	
	
	@Test
	public void test(){
		String ss = "h_h__u";
		String re = "[_]+" ;
		ss = ss.replaceAll(re, "");
		System.out.println(ss);
				
	}
}
