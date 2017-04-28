package com.entitylinking_dbpedia.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.entitylinking_dbpedia.linking.LinkingKB;
import com.entitylinking_dbpedia.linking.bean.DictBean;
import com.entitylinking_dbpedia.linking.bean.Entity;
import com.entitylinking_dbpedia.linking.bean.Mention;
import com.entitylinking_dbpedia.linking.bean.PathBean;
import com.entitylinking_dbpedia.linking.bean.RELRWParameterBean;
import com.entitylinking_dbpedia.linking.bean.Text;
import com.entitylinking_dbpedia.utils.FileUtils;
//import com.entitylinking_dbpedia.utils.NLPUtils;
import com.entitylinking_dbpedia.utils.Parameters;

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

//		String fileDir = "./data/ace2004/RawTexts";
//		String wpath = "./dict/disAmbiguationMention.txt";
//		try {
//			NLPUtils.disambiguationMention(fileDir, wpath);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/**
	 * entity-linking主流程
	 * @return
	 */
	public Map<String, String> linkingMainProcess(){
		File fileDir = new File(RELRWParameterBean.getSourceFileDirPath());
		if(fileDir.isDirectory()){
			File[] fileList = fileDir.listFiles();
			if(fileList == null || fileList.length == 0)
				return null;
			RELRWParameterBean.setTotalDocument(fileList.length);
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
//				texts.add(text);
				//链接知识库过程
				LinkingKB linkingKB = new LinkingKB();
				linkingKB.obtainmentionEntityPairs(text);
				logger.info("mention\t\ttrue entity\t\tfound entity\tscore");
				String resultFilePath = PathBean.getResultDirPath() + File.separator + file.getName();
				StringBuilder sBuilder = new StringBuilder();
				for(Entry<Mention, Entity> entry :text.getEntityGraph()
						.getDisambiguationMap().entrySet()){
					sBuilder.append(entry.getKey().getMentionName()).append("\t")
							.append(entry.getKey().getDbpediaObjectEntity()).append("\t");
					if(entry.getValue().getEntityName() == null 
							|| entry.getValue().getScore() < RELRWParameterBean.getNilThres()){
						sBuilder.append(RELRWParameterBean.getNil());
					}else{
						sBuilder.append(entry.getValue().getEntityName());
					}
					
					sBuilder.append("\t").append(entry.getValue().getScore());
					sBuilder.append("\n");		

				}
				logger.info(sBuilder.toString());
				
				try {
					BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(new File(resultFilePath)), "utf-8"));
					bWriter.write(sBuilder.toString());
					bWriter.close();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//将df持久化到本地
//			Parameters parameters = new Parameters();
//			parameters.pickleDf(PathBean.getDfDictPath());
		}
		
//		saveContextOfGraph(texts);
		return null;
	}
	
	/**
	 * 初始化工作，加载参数和词典等
	 */
	public void init(){
		Parameters parameters = new Parameters();
		parameters.loadPath("./xml/path.xml");
		parameters.loadRELParameters(PathBean.getRelParameterByDbpediaPath());
		parameters.loadDictFromXML();
//		NLPUtils.countDF("./data/ace2004/RawTexts", "./dict/df2.txt");
	}
	
	/**
	 * 保存mention与实体的上下文信息
	 */
	public void saveContextOfGraph(List<Text> texts){
		String textName;
		String saveMentionDirPath = "./data\\ace2004\\context\\mention\\";
		String saveEntityPath = "./data\\ace2004\\context\\entity\\entityContext";
		Map<String, Set<String>> entityContext = new HashMap<String, Set<String>>();
		for(Text text:texts){
			textName = text.getTextName();
			String mentionPath = saveMentionDirPath + textName;
			List<Mention> mentions = text.getEntityGraph().getMentions();
			try {
				BufferedWriter bWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(new File(mentionPath)), "utf-8"));
				for(Mention mention:mentions){
					bWriter.write(mention.getMentionName()+"\t||\t"+StringUtils.join(mention.getMentionContext(), "\t|\t")+"\n");
					List<Entity>entities  = mention.getCandidateEntity();
					for(Entity entity:entities){
						if(!entityContext.containsKey(entity.getEntityName())){
							entityContext.put(entity.getEntityName(), entity.getEntityContext());
						}
					}
				}
				bWriter.close();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		try {
			BufferedWriter bWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(saveEntityPath)), "utf-8"));
			for(Entry<String, Set<String>>entry:entityContext.entrySet()){
				bWriter.write(entry.getKey()+"\t||\t"+StringUtils.join(entry.getValue(), "\t|\t")+"\n");
			}
			bWriter.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}