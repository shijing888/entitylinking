package com.entitylinking.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Result {

	public static void main(String args[]){
		String rpath1 = "data\\result_repository\\ace2004_dbpedia_4.19\\result.txt";
		String rpath2 = "data\\result\\result.txt";
		System.out.println("在加无歧义词典后新出现的错误有:");
		compareDiffer(rpath1, rpath2);
		System.out.println("在加无歧义词典后修正的错误有:");
		compareDiffer(rpath2, rpath1);
	}
	
	public static void compareDiffer(String rpath1,String rpath2){
		try {
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(rpath1)), "utf-8"));
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(rpath2)), "utf-8"));
			
			Map<String, String> map = new HashMap<String, String>();
			String line;
			String temp;
			while((line = reader1.readLine()) != null){
				String[] lineArray = line.split("\t");
				if(lineArray.length == 5){
					temp = lineArray[0]+"_"+lineArray[4];
//					System.out.println(temp);
					map.put(temp, line);
				}
			}
			
			reader1.close();
			while((line = reader2.readLine()) != null){
				String[] lineArray = line.split("\t");
				if(lineArray.length == 5){
					temp = lineArray[0]+"_"+lineArray[4];
					if(!map.containsKey(temp)){
						System.out.println(line);
					}
				}
			}
			reader2.close();
		} catch (Exception e) {
 			// TODO: handle exception
		}
	}
}
