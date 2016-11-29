package com.entitylinking.task;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.entitylinking.linking.LinkingKB;
import com.entitylinking.linking.bean.DictBean;
import com.entitylinking.linking.bean.Entity;
import com.entitylinking.linking.bean.Mention;
import com.entitylinking.linking.bean.PathBean;
import com.entitylinking.linking.bean.RELRWParameterBean;
import com.entitylinking.linking.bean.Text;
import com.entitylinking.utils.FileUtils;
import com.entitylinking.utils.NormalizeMention;
import com.entitylinking.utils.Parameters;

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
			String textContent;
			for(File file:fileList){
				String filePath = file.getAbsolutePath();
				textContent = FileUtils.readFileContent(filePath);
				Text text = new Text(file.getName(), textContent);
				logger.info("text name:"+file.getName());
				if(!DictBean.getMentionDict().containsKey(file.getName())){
					continue;
				}
				text.getEntityGraph().setMentions(DictBean.getMentionDict().get(file.getName()));
				//生成该文档的密度子图
				text.generateDensityGraph();
				logger.info("entity graph finish!");
				//链接知识库过程
				LinkingKB linkingKB = new LinkingKB();
				linkingKB.obtainmentionEntityPairs(text);
				logger.info("mention-entity found!");
				StringBuilder sBuilder = new StringBuilder();
				for(Entry<Mention, Entity> entry :text.getEntityGraph()
						.getDisambiguationMap().entrySet()){
					sBuilder.delete(0, sBuilder.length());
					sBuilder.append(entry.getKey().getMentionName()).append("\t")
							.append(entry.getKey().getObjectEntity()).append("\t");
					if(entry.getValue() == null){
						sBuilder.append("null");
					}else{
						sBuilder.append(entry.getValue().getEntityName());
					}
							
					logger.info(sBuilder.toString());
				}
			}
			
			//将df持久化到本地
//			Parameters parameters = new Parameters();
//			parameters.pickleDf(PathBean.getDfDictPath());
		}
		return null;
	}
	
	/**
	 * 初始化工作
	 */
	public void init(){
		Parameters parameters = new Parameters();
		parameters.loadPath("./xml/path.xml");
		parameters.loadRELParameters(PathBean.getRelParameterPath());
		parameters.loadDictFromXML();
		logger.info("houxuanshiti阈值:"+RELRWParameterBean.getCandidateEntityNumThresh());
//		NLPUtils.countDF("./data/ace2004/RawTexts", "./dict/df.txt");
	}
	
	public void testEntity(String mention){
		mention = NormalizeMention.getNormalizeMention(mention,true);
		logger.info("mention:"+mention);
		Mention mention2 = new Mention(mention);
		List<Entity> candidateList = mention2.obtainCandidate(mention);
		logger.info(mention+" candidates size:"+ candidateList.size());
		logger.info(mention+" candidates are:"+ StringUtils.join(candidateList, "\t"));
		for(Entity entity:candidateList){
			logger.info("candidate entity:"+entity.getEntityName());
		}
	}
}
