package com.entitylinking.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.entitylinking.linking.bean.DictBean;

/**
 * 加载配置文件的参数
 * @author shijing
 *
 */
public class Parameters {

	private static Logger logger=Logger.getLogger(Parameters.class);
	
	/**
	 * 解析dict.xml文件
	 * @param path
	 * @return
	 */
	public void loadDictFromXML(String path){

		InputStream input;
		try {
			//1.创建reader
			SAXReader reader = new SAXReader();
			//2.读取xml文件到document
			input = new FileInputStream(new File(path));
			Document document = reader.read(input);
			//获取xml的根节点
			Element root = document.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> elements = root.elements();
			
			for(Element element:elements){
				if(element.getName().equals("dictPath")){
					DictBean.setSynonymsDictPath(element.elementText("synonyms"));
					DictBean.setAmbiguationDictPath(element.elementText("ambiguation"));
				}
			}
			
			input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(DictBean.getSynonymsDictPath()!=null && DictBean.getAmbiguationDictPath()!=null){
			logger.info("加载词典路径 已完成！");
			DictBean.setSynonymsDict(loadSynonymsDict(DictBean.getSynonymsDictPath()));
			DictBean.setAmbiguationDict(loadDisambiguationDict(DictBean.getAmbiguationDictPath()));
			logger.info("MJ:"+StringUtils.join(DictBean.getAmbiguationDict().get("MJ"),"\t"));
		}
	}
	
	/**
	 * 根据词典路径将词典加载进来
	 * @param dictBean
	 * @return
	 */
	public Map<String, String> loadSynonymsDict(String path){
		Map<String, String> synonymsDict = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"UTF-8"));
			String line;
			while((line = br.readLine())!=null){
				String[] lineArray = line.split("\t\\|\\|\t");
				if(lineArray.length == 2){
					synonymsDict.put(lineArray[0], lineArray[1]);
				}
			}
			
			br.close();
			return synonymsDict;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return synonymsDict;
	}
	
	public Map<String, HashSet<String>> loadDisambiguationDict(String path){
		Map<String, HashSet<String>> disambiguationDict = new HashMap<String, HashSet<String>>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"UTF-8"));
			String line;
			while((line = br.readLine())!=null){
				String[] lineArray = line.split("\t\\|\\|\t");
				if(lineArray.length == 2){
					HashSet<String> itemSet = new HashSet<>(Arrays.asList(lineArray[1].split("\t\\|\t")));
					disambiguationDict.put(lineArray[0], itemSet);
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
}
