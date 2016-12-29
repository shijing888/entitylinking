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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.entitylinking.linking.bean.DictBean;
import com.entitylinking.linking.bean.Mention;
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
		DictBean.setPosDict(loadSetDict(PathBean.getPosDictPath()));
		DictBean.setStopWordDict(loadSetDict(PathBean.getStopWordDictPath()));
		DictBean.setDfDict(loadDfDict(PathBean.getDfDictPath()));
		DictBean.setMentionDict(loadMentionDict(PathBean.getMentionDictPath()));
	}
	
	public DictBean loadSurfaceFormDict(){
		DictBean dictBean = new DictBean();
		if(PathBean.getSynonymsDictPath()!=null 
				&& PathBean.getAmbiguationDictPath()!=null){
			logger.info("加载词典开始！");
			long time1 = System.currentTimeMillis();
			dictBean.setSynonymsDict(loadSynonymsDict(PathBean.getSynonymsDictPath()));
			dictBean.setAmbiguationDict(loadMapDict(
					PathBean.getAmbiguationDictPath()));
			long time2 = System.currentTimeMillis();
			logger.info("加载词典已完成！加载时间:"+(time2-time1)/60000);
			logger.info("SynonymsDict size:"+dictBean.getSynonymsDict().size());
			logger.info("AmbiguationDict size:"+dictBean.getAmbiguationDict().size());
		}
		return dictBean;
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
				PathBean.setMentionDictPath(element.elementText("mentionDict"));
			}else if(element.getName().equals("relPath")){//robust 实体链接方法参数
				PathBean.setRelParameterPath(element.elementText("relParameterPath"));
				PathBean.setResultDirPath(element.elementText("resultDir"));
				PathBean.setEntityContextPath(element.elementText("entityContextPath"));
				PathBean.setMentionContextDirPath(element.elementText("mentionContextDirPath"));
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
	 * 加载mention集合
	 * @param path
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<Mention>> loadMentionDict(String path){
		Map<String, List<Mention>> mentionDict = new HashMap<>();
		List<Element> elements = getElementsFromXML(path);
		for(Element element:elements){
			String docuName = element.attributeValue("docName");
			List<Mention> mentions = new ArrayList<>();
			List<Element> subElements = element.elements();
			for(Element subElement:subElements){
				Mention mention = new Mention(NormalizeMention.getNormalizeMention(
						subElement.elementText("mention"), true));
				mention.setObjectEntity(NormalizeMention.getNormalizeMention(
						subElement.elementText("wikiName"),true));
				mention.setMentionOffset(Integer.parseInt(subElement.elementText("offset")));
				mention.setOccurCounts(Integer.parseInt(subElement.elementText("length")));
				mentions.add(mention);
			}
			mentionDict.put(docuName, mentions);
		}
		DictBean.setMentionDict(mentionDict);
		return mentionDict;
	}
	
	/**
	 * 计算mention的df
	 * @param wpath
	 * @param xmlPath
	 */
	@SuppressWarnings("unchecked")
	public void getMentionDf(String wpath,String xmlPath){
		try {
			Map<String, Integer> dfMap = loadDfDict(wpath);
			List<Element> elements = getElementsFromXML(xmlPath);
			for(Element element:elements){
				List<Element> subElements = element.elements();
				for(Element subElement:subElements){
					String normMention = NormalizeMention.getNormalizeMention(
							subElement.elementText("mention"), true);
					if(!dfMap.containsKey(normMention)){
						dfMap.put(normMention, 1);
					}
				}
			}
			BufferedWriter bWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath)),"utf-8"));
			StringBuilder sBuilder = new StringBuilder();
			for(Entry<String, Integer>entry:dfMap.entrySet()){
				sBuilder.delete(0, sBuilder.length());
				bWriter.write(sBuilder.append(entry.getKey()).append("\t||\t")
						.append(entry.getValue()).append("\n").toString());
			}
			bWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				bWriter.write(sBuilder.append(entry.getKey()).append("\t||\t")
						.append(entry.getValue()).append("\n").toString());
			}
			bWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 将增加的上下文信息持久化到本地
	 * @param path
	 * @param contextMap
	 */
	public void pickleContextMap(String path,Map<String, Set<String>> contextMap){
		try {
			File file = new File(path);
			if(!file.exists()){
				file.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
			StringBuilder stringBuilder = new StringBuilder();
			
			for(Entry<String, Set<String>>entry:contextMap.entrySet()){
				stringBuilder.append(entry.getKey()).append("\t||\t")
					.append(StringUtils.join(entry.getValue(), "\t|\t")).append("\n");
			}
			writer.write(stringBuilder.toString());
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	/**
	 * 用于加载消歧词典、上下文词典
	 * @param path
	 * @return
	 */
	public Map<String, HashSet<String>> loadMapDict(String path){
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
					RELRWParameterBean.setCandidateEntityNumThresh(Integer.parseInt(
							element.elementText("candidateEntityNumThresh")));
					RELRWParameterBean.setPopularityThresh(Integer.parseInt(
							element.elementText("popularityThresh")));
					RELRWParameterBean.setCooccurenceThresh(Integer.parseInt(
							element.elementText("cooccurenceThresh")));
					RELRWParameterBean.setSemanticSimWeight(Double.parseDouble(
							element.elementText("semanticSimWeight")));
					RELRWParameterBean.setContextSimWeight(Double.parseDouble(
							element.elementText("contextSimWeight")));
					RELRWParameterBean.setPopularityWeight(Double.parseDouble(
							element.elementText("popularityWeight")));
					RELRWParameterBean.setNilThres(Double.parseDouble(element.elementText("nilThres")));
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
				}else if(element.getName().equals("constant")){
							RELRWParameterBean.setNil(
									element.elementText("nil"));
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("parameters' format error!");
		}
		
	}
	
	public static void main(String args[]){
		Parameters parameters = new Parameters();
		String wpath = "./dict/df.txt";
		String xmlPath = "./data/ace2004/ace2004.xml";
		parameters.getMentionDf(wpath, xmlPath);
	}
}
