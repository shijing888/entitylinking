package com.entitylinking.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.entitylinking.linking.bean.DictBean;
import com.entitylinking.linking.bean.Entity;
import com.entitylinking.linking.bean.Mention;
import com.entitylinking.linking.bean.RELRWParameterBean;
import com.entitylinking.linking.bean.Text;

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
	 * 处理传入的文档，获得mention集、mention的上下文信息及候选实体集
	 * @param text
	 */
	public static void getTextMentionTask(Text text){
		//mentions从xml中已经加载完成
        List<Mention> mentions = text.getEntityGraph().getMentions();
        String content;
        int mentionOffset;
        int beginOffset;
        int endOffset;
        int textLen = text.getContent().split("\\s+").length;
        int entityLen = 0;
        for(Mention mention:mentions){
        	logger.info("mention:"+mention.getMentionName());
        	//初始化mention tfidf
    		mention.setTfidfValue(CommonUtils.calTfidf(mention.getOccurCounts(), 
    				DictBean.getDfDict().get(mention.getMentionName()), textLen));
    		//获取候选实体
    		List<Entity> candidateEntity = mention.candidatesOfMention(mention.getMentionName());
    		mention.setCandidateEntity(candidateEntity);
    		entityLen += candidateEntity.size();
    		//通过mention的offset及窗口来获取上下文
        	mentionOffset = mention.getMentionOffset();
        	if(mentionOffset - RELRWParameterBean.getContextWindow() < 0){
        		beginOffset = 0;
        	}else{
        		beginOffset  = mentionOffset - RELRWParameterBean.getContextWindow();
        	}
        	
        	if(mentionOffset + RELRWParameterBean.getContextWindow() >= text.getContent().length()){
        		endOffset = text.getContent().length() -1;
        	}else{
        		endOffset = mentionOffset + RELRWParameterBean.getContextWindow();
        	}
        	
        	content = text.getContent().substring(beginOffset,endOffset);
        	
        	 // create an empty Annotation just with the given text
            Annotation document = new Annotation(content);
            // run all Annotators on this text
            pipeline.annotate(document);
            // these are all the sentences in this document
            List<CoreMap> sentences = document.get(SentencesAnnotation.class);
            for(CoreMap sentence: sentences) {
                // a CoreLabel is a CoreMap with additional token-specific methods
                for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                    // this is the text of the token
                    //String word = token.get(TextAnnotation.class);
                    // this is the POS tag of the token
                    String pos = token.get(PartOfSpeechAnnotation.class);
                    // this is the NER label of the token
                    String ne = token.get(NamedEntityTagAnnotation.class);
                    // this is the NER label of the token
                    String lemma = token.get(LemmaAnnotation.class).toLowerCase();
                    //去停用词
                    if(DictBean.getStopWordDict().contains(lemma) || lemma.length() < 2)
                    	continue;
                    //用于记录mention的上下文
                    if(DictBean.getPosDict().contains(pos) && nerCategory.contains(ne)){
                    	mention.getMentionContext().add(lemma);
                    }
                }
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
	}
	
	/**
	 * 获取实体上下文
	 * @param content
	 * @return
	 */
	public static Set<String> getEntityContext(String content){
		if(content.length() > 2 * RELRWParameterBean.getContextWindow()){
			content = content.substring(0, 2* RELRWParameterBean.getContextWindow());
		}
		 // create an empty Annotation just with the given text
       Annotation document = new Annotation(content);
       // run all Annotators on this text
       pipeline.annotate(document);
       Set<String> nerContextSet = new HashSet<>();
       // these are all the sentences in this document
       List<CoreMap> sentences = document.get(SentencesAnnotation.class);
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
            		nerContextSet.add(lemma); 
            	  
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
