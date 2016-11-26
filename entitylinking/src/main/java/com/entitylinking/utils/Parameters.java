package com.entitylinking.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.entitylinking.linking.bean.DictBean;
import com.entitylinking.linking.bean.PathBean;
import com.entitylinking.linking.bean.RELRWParameterBean;

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
	public void loadDictFromXML(){
		if(PathBean.getSynonymsDictPath()!=null 
				&& PathBean.getAmbiguationDictPath()!=null){
			logger.info("加载词典开始！");
			long time1 = System.currentTimeMillis();
			logger.info("synonymsDict path:"+PathBean.getSynonymsDictPath());
			DictBean.setSynonymsDict(loadSynonymsDict(PathBean.getSynonymsDictPath()));
			DictBean.setAmbiguationDict(loadDisambiguationDict(
					PathBean.getAmbiguationDictPath()));
			long time2 = System.currentTimeMillis();
			logger.info("加载词典已完成！加载时间:"+(time2-time1)/60000);
		}
		
		DictBean.setPosDict(loadSetDict(PathBean.getPosDictPath()));
		DictBean.setStopWordDict(loadSetDict(PathBean.getStopWordDictPath()));
		DictBean.setDfDict(loadDfDict(PathBean.getDfDictPath()));
	}
	
	/**
	 * 获取xml文件的element列表
	 * @param path
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getElementsFromXML(String path){
		InputStream input;
		List<Element> elements = null;
		try {
			//1.创建reader
			SAXReader reader = new SAXReader();
			//2.读取xml文件到document
			input = new FileInputStream(new File(path));
			Document document = reader.read(input);
			//获取xml的根节点
			Element root = document.getRootElement();
			elements = root.elements();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return elements;
	}
	
	/**
	 * 首先将配置文件的路径加载进来
	 * @param path
	 */
	public void loadPath(String path){
		List<Element> elements = getElementsFromXML(path);
		for(Element element:elements){
			if(element.getName().equals("dictPath")){//字典路径
				PathBean.setSynonymsDictPath(element.elementText("synonyms"));
				PathBean.setAmbiguationDictPath(element.elementText("ambiguation"));
				PathBean.setPosDictPath(element.elementText("pos"));
				PathBean.setStopWordDictPath(element.elementText("stopwords"));
				PathBean.setDfDictPath(element.elementText("df"));
			}else if(element.getName().equals("relPath")){//robust 实体链接方法参数
				PathBean.setRelParameterPath(element.elementText("relParameterPath"));
			}else if(element.getName().equals("indexDirPath")){//索引文件路径
				PathBean.setEntityRelationPath(element.elementText("entityRelationPath"));
			}
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
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)),"UTF-8"));
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
	
	/**
	 * 加载常规词典，如词性、停用词
	 * @param path
	 * @return
	 */
	public Set<String> loadSetDict(String path){
		Set<String> dictSet = new HashSet<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)),"UTF-8"));
			String line;
			while((line = br.readLine())!=null){
				dictSet.add(line);
			}
			
			br.close();
			return dictSet;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dictSet;
	}
	
	/**
	 * 加载文档频率df
	 * @param path
	 * @return
	 */
	public Map<String, Integer> loadDfDict(String path){
		Map<String, Integer> dfMap = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)),"UTF-8"));
			String line;
			while((line = br.readLine())!=null){
				String[] lineArray = line.split("\t\\|\\|\t");
				if(lineArray.length==2){
					dfMap.put(lineArray[0], Integer.parseInt(lineArray[1]));
				}
			}
			
			br.close();
			return dfMap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dfMap;
	}
	
	//将df持久化到本地
	public void pickleDf(String path){
		try {
			BufferedWriter bWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(path)),"utf-8"));
			StringBuilder sBuilder = new StringBuilder();
			for(Entry<String, Integer>entry:DictBean.getDfDict().entrySet()){
				sBuilder.delete(0, sBuilder.length());
				bWriter.write(sBuilder.append(entry.getKey()).append("\t")
						.append(entry.getValue()).append("\n").toString());
			}
			bWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String, HashSet<String>> loadDisambiguationDict(String path){
		Map<String, HashSet<String>> disambiguationDict 
								= new HashMap<String, HashSet<String>>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)),"UTF-8"));
			String line;
			while((line = br.readLine())!=null){
				String[] lineArray = line.split("\t\\|\\|\t");
				if(lineArray.length == 2){
					HashSet<String> itemSet 
							= new HashSet<>(Arrays.asList(lineArray[1].split("\t\\|\t")));
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

	/**
	 * 加载robust entity linking 参数
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	public void loadRELParameters(String path){
		List<Element> elements = getElementsFromXML(path);
		try {
			for(Element element:elements){
				if(element.getName().equals("paramenter")){
					RELRWParameterBean.setSourceFileDirPath(
							element.elementText("fileDirPath"));
					RELRWParameterBean.setContextWindow(Integer.parseInt(
							element.elementText("contextWindow")));
					RELRWParameterBean.setAlpha(Double.parseDouble(
							element.elementText("alpha")));
					RELRWParameterBean.setConvergencePrecise(Double.parseDouble(
							element.elementText("convergencePrecise")));
					RELRWParameterBean.setGamma(Double.parseDouble(
							element.elementText("gamma")));
					RELRWParameterBean.setEntityContentLen(Integer.parseInt(
							element.elementText("entityContentLen")));
				}else if(element.getName().equals("indexFields")){
					List<Element> subElements = element.elements();
					for(Element subElement:subElements){
						if(subElement.getName().equals("entityRelationFields")){
							RELRWParameterBean.setEntityRelationField1(
									subElement.elementText("field1"));
							RELRWParameterBean.setEntityRelationField2(
									subElement.elementText("field2"));
							RELRWParameterBean.setEntityRelationField3(
									subElement.elementText("field3"));
						}
					}
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("parameters' format error!");
		}
		
	}
}
