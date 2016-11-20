package com.entitylinking.entitylinking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.junit.Test;


public class FilterDict {

	public static void main(String args[]){
		
//		String rpath = "H:\\MysqlData\\ambiguationDict2.txt";
//		String wpath = "H:\\MysqlData\\ambiguationDict3.txt";
//		filterCategory(rpath, wpath);
		String rpath = "./dict/synonymsDict.txt";
		String wpath = "./dict/synonymsDict2.txt";
		filterBracket(rpath, wpath);
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
			int count = 0;
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
	
	@Test
	public void test(){
		String ss = "[hhu]";
		String re = "[\\[\\]]" ;
		ss = ss.replaceAll(re, "");
		System.out.println(ss);
				
	}
}
