package com.entitylinking.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
}
