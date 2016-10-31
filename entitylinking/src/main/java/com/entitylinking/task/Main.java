package com.entitylinking.task;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.entitylinking.candidate.ner.CandidateMain;
import com.entitylinking.linking.LinkingKB;
import com.entitylinking.linking.bean.EntityGraph;
import com.entitylinking.linking.bean.RELRWParameterBean;
import com.entitylinking.linking.bean.Text;
import com.entitylinking.utils.FileUtils;

/**
 * 实体链接主程序
 * @author HP
 *
 */
public class Main {

	static Logger logger = Logger.getLogger(Main.class);
	static{
		PropertyConfigurator.configure("./log4j.properties");
	}
	private CandidateMain candidateMain;
	public static void main(String args[]) {
		/**
		 *1.加载文件
		 *2.获得候选实体
		 *3.构造密度子图并初始化 
		 *4.使用随机游走计算语义签名 
		 *5.计算全局score并获得目标实体 
		 */
		Main main = new Main();
		main.init();
		main.linkingMainProcess();
		
	}
	
	/**
	 * entity-linking主流程
	 * @return
	 */
	public Map<String, String> linkingMainProcess(){
		File fileDir = new File(RELRWParameterBean.getSourceFileDirPath());
		if(fileDir.isDirectory()){
			File[] fileList = fileDir.listFiles();
			for(File file:fileList){
				String filePath = file.getAbsolutePath();
				Text text = new Text();
				text.setTextName(file.getName());
				text.setContent(FileUtils.readFileContent(filePath));
				//生成候选过程
				Map<String, List<String>> candidateMap = candidateMain.candidatesOfText(filePath); 
				EntityGraph entityGraph = new EntityGraph(candidateMap);
				entityGraph = entityGraph.generateDensityGraph(text);
				text.setEntityGraph(entityGraph);
				//链接知识库过程
				LinkingKB linkingKB = new LinkingKB();
				Map<String, String> mentionEntityPairs = linkingKB.obtainmentionEntityPairs(text);
				StringBuilder sBuilder = new StringBuilder();
				for(Entry<String, String> entry :mentionEntityPairs.entrySet()){
					sBuilder.delete(0, sBuilder.length());
					sBuilder.append(entry.getKey()).append("\t").append(entry.getValue());
					logger.info(sBuilder.toString());
				}
			}
		}
		return null;
	}
	
	public void init(){
		candidateMain = new CandidateMain();
	}
}
