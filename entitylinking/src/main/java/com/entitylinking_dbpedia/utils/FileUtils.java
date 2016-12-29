package com.entitylinking_dbpedia.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 文件读写
 * @author HP
 *
 */
public class FileUtils {

	/**
	 * 读取指定文件的内容
	 * @param path
	 * @return
	 */
	public static String readFileContent(String path){
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "utf-8"));
			StringBuilder text = new StringBuilder();
			String line;
			while((line=bReader.readLine())!=null){
				text.append(line);
			}
			bReader.close();
			return text.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将map中的内容写入文件
	 * @param path
	 * @param map
	 */
	public static void writeFileContent(String path, Map<String, Integer> map){
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(path)), "utf-8"));
			StringBuilder sBuilder = new StringBuilder();
			for(Entry<String, Integer>entry:map.entrySet()){
				sBuilder.append(entry.getKey()).append("\t||\t").append(entry.getValue()).append("\n");
			}
			writer.write(sBuilder.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 统计文件行数
	 * @param rpath
	 * @return
	 */
	public static int getFileLines(String rpath){
		try {
			File file = new File(rpath);
			if(file.isFile()){
				BufferedReader bReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file),"utf-8"));
				@SuppressWarnings("unused")
				String line;
				int i=0;
				while((line = bReader.readLine())!=null){
					i++;
				}
				bReader.close();
				return i;
			}
		
		} catch (IOException e) {
			// TODO: handle exception
		}
		return 0;
	}
}
