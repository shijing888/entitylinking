package com.entitylinking.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.entitylinking.linking.bean.DictBean;
import com.entitylinking.linking.bean.Entity;
import com.entitylinking.linking.bean.Mention;
import com.entitylinking.linking.bean.RELRWParameterBean;
import com.entitylinking.linking.bean.Text;

import difflib.StringUtills;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
/**
 * 斯坦福nlp工具包来获取mention
 * @author HP
 *
 */
public class NLPUtils {

	static Logger logger = Logger.getLogger(NLPUtils.class);
	static StanfordCoreNLP pipeline;
	static{
		/** creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing,
		 *  and coreference resolution
        */
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        pipeline = new StanfordCoreNLP(props);
//        candidateMain =CandidateMain.getCandidateMain();
	}
	
	//存放用于发现实体的类别
	private static Set<String> nerCategory = new HashSet<>(Arrays.asList(
			new String[]{"PERSON","LOCATION","ORGANIZATION","DATE"}));
	
	public static void main(String args[]){
		 // read some text in the text variable
//        String path = "./data/ace2004/RawTexts/chtb_171.eng";
//		String content = FileUtils.readFileContent(path);
//        processTextTask(new Text(content));
		String dirPath = "./data/ace2004/RawTexts";
		String savePath = "./dict/df.txt";
		String stopPath = "./dict/stopword_en.txt";
		String PosPath = "./dict/pos.txt";
		Parameters parameters = new Parameters();
		DictBean.setStopWordDict(parameters.loadSetDict(stopPath));
		DictBean.setDfDict(parameters.loadDfDict(savePath));
		DictBean.setPosDict(parameters.loadSetDict(PosPath));
		countDF(dirPath, savePath);
	}
	
	/**
	 * 处理传入的文档，获得mention集及候选实体集
	 * @param text
	 */
	public static void getTextMentionTask(Text text){
		
		 // create an empty Annotation just with the given text
        Annotation document = new Annotation(text.getContent());
        
        // run all Annotators on this text
        pipeline.annotate(document);
        Map<String, List<Integer>> wordsMap = new HashMap<String, List<Integer>>();
        Set<String> nerSet = new HashSet<>();
        List<Mention> mentions = new ArrayList<Mention>();
        
        // these are all the sentences in this document
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        int lenCount = 0;
        int index = 0;
        for(CoreMap sentence: sentences) {
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // this is the text of the token
                //String word = token.get(TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(NamedEntityTagAnnotation.class);
                String lemma = token.get(LemmaAnnotation.class).toLowerCase();
                //去停用词
                if(DictBean.getStopWordDict().contains(lemma) || lemma.length() < 2)
                	continue;
                //用于记录mention与text的上下文
                if(DictBean.getPosDict().contains(pos)){
                	index = lenCount + token.index();
                	//将上下文信息存入text中
                	text.getTextContext().put(index, lemma);
                	if(wordsMap.containsKey(lemma)){
                		wordsMap.get(lemma).add(index);
                	}else{
                		List<Integer> mentionIndexList = new ArrayList<>();
                		mentionIndexList.add(index);
                		wordsMap.put(lemma, mentionIndexList);
                	}
//                	logger.info("token name:"+lemma+"\tindex:"+StringUtills.join(wordsMap.get(lemma), "\t"));
                	if(nerCategory.contains(ne)){
                		nerSet.add(lemma);
                	}
                }
            }
            lenCount += sentence.size();
        }
       //初始化text中上下文索引
        text.setTextContextIndex();
        int entityLen = 0;
        //保存mention
        for(Entry<String, List<Integer>>entry:wordsMap.entrySet()){
        	if(nerSet.contains(entry.getKey())){
        		//初始化mention name
        		Mention mention = new Mention(entry.getKey());
        		//初始化mention tfidf
        		logger.info("entry key:"+entry.getKey());
        		logger.info("df size:"+DictBean.getDfDict().get(entry.getKey()));
        		mention.setTfidfValue(CommonUtils.calTfidf(entry.getValue().size(), 
        				DictBean.getDfDict().get(entry.getKey()), lenCount));
            	//初始化mention在文中的位置信息
        		mention.setMentionIndex(entry.getValue());
//        		logger.info("mentionIndex size:"+entry.getValue().size());
            	//初始化mention的候选实体列表
        		List<Entity> candidateEntity = mention.candidatesOfMention(mention.getMentionName());
        		mention.setCandidateEntity(candidateEntity);
        		entityLen += candidateEntity.size();
            	//初始化mention的上下文集合
        		mention.initMentionContext(text.getTextContext(), text.getTextContextIndex());
        		mentions.add(mention);
        	}
        	
        }
        //对mention按照歧义性排序，歧义性按照候选实体个数来定义
        Collections.sort(mentions, new Comparator<Mention>() {
			@Override
			public int compare(Mention arg0, Mention arg1) {
				// TODO Auto-generated method stub
				return arg0.getCandidateEntity().size() - arg1.getCandidateEntity().size();
			}
		});
        text.getEntityGraph().setEntityLen(entityLen);
        text.getEntityGraph().setMentions(mentions);
	}
	
	public static Set<String> getEntityContext(String content){
		
		 // create an empty Annotation just with the given text
       Annotation document = new Annotation(content);
       // run all Annotators on this text
       pipeline.annotate(document);
       Set<String> nerContextSet = new HashSet<>();
       int entityContextLen = RELRWParameterBean.getContextWindow();
       // these are all the sentences in this document
       List<CoreMap> sentences = document.get(SentencesAnnotation.class);
       int index = 0;
       for(CoreMap sentence: sentences) {
           // a CoreLabel is a CoreMap with additional token-specific methods
           for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
               // this is the text of the token
               //String word = token.get(TextAnnotation.class);
               // this is the POS tag of the token
               String pos = token.get(PartOfSpeechAnnotation.class);
               // this is the NER label of the token
               String ne = token.get(NamedEntityTagAnnotation.class);
               String lemma = token.get(LemmaAnnotation.class).toLowerCase();
               //去停用词
               if(DictBean.getStopWordDict().contains(lemma) || lemma.length() < 2)
               	continue;
               //用于记录mention与text的上下文
               if(DictBean.getPosDict().contains(pos) && nerCategory.contains(ne)){
            	  if(index < entityContextLen){
            		 nerContextSet.add(lemma); 
            	  }else {
					return nerContextSet;
				}
               }
           }
       }
      
      return nerContextSet;
	}
	
	/**
	 * 统计数据集的文档频率
	 * @param fileDir
	 * @param savePath
	 */
	public static void countDF(String fileDir,String savePath){
		File dir = new File(fileDir);
		if(dir.isDirectory()){
			File[] files = dir.listFiles();
			for(File file:files){
				 // create an empty Annotation just with the given text
		        Annotation document = new Annotation(FileUtils.readFileContent(file.getAbsolutePath()));
		        
		        // run all Annotators on this text
		        pipeline.annotate(document);
		       
		        // these are all the sentences in this document
		        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		        for(CoreMap sentence: sentences) {
		            // a CoreLabel is a CoreMap with additional token-specific methods
		            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
		                String pos = token.get(PartOfSpeechAnnotation.class);
		                String lemma = token.get(LemmaAnnotation.class).toLowerCase();
		                //去停用词
		                if(DictBean.getStopWordDict().contains(lemma) || lemma.length() < 2)
		                	continue;
		                
		                if(DictBean.getPosDict().contains(pos)){
		                	if(DictBean.getDfDict().containsKey(lemma)){
		                		DictBean.getDfDict().put(lemma, DictBean.getDfDict().get(lemma)+1);
		                	}else{
		                		DictBean.getDfDict().put(lemma, 1);
		                	}
		                	
		                }
		            }
		        } 
			}
		}
		
		FileUtils.writeFileContent(savePath, DictBean.getDfDict());
	}
	
	public static void testNLP(String content){
		 // create an empty Annotation just with the given text
        Annotation document = new Annotation(content);
        
        // run all Annotators on this text
        pipeline.annotate(document);
        
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        logger.info("word\tpos\tlemma\tner");
        int lenCount = 0;
        for(CoreMap sentence: sentences) {
        	logger.info("sentence:"+sentence.toString());
             // traversing the words in the current sentence
             // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(NamedEntityTagAnnotation.class);
                String lemma = token.get(LemmaAnnotation.class);
                int index = lenCount + token.index();
                int index2 = token.sentIndex();
                logger.info(word+"\t"+pos+"\t"+lemma+"\t"+ne+"\t"+index+"\t"+index2);
            }
            lenCount+=sentence.size();
            // this is the parse tree of the current sentence
//            Tree tree = sentence.get(TreeAnnotation.class);
            // this is the Stanford dependency graph of the current sentence
//            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
        }
        // This is the coreference link graph
        // Each chain stores a set of mentions that link to each other,
        // along with a method for getting the most representative mention
        // Both sentence and token offsets start at 1!
//        Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
	}
}
