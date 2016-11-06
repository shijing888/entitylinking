package com.entitylinking.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.entitylinking.candidate.CandidateMain;
import com.entitylinking.linking.bean.DictBean;
import com.entitylinking.linking.bean.Mention;
import com.entitylinking.linking.bean.Text;
import com.entitylinking.linking.bean.TextContext;

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
        
	}
	
	//存放用于发现实体的类别
	private static Set<String> nerCategory = new HashSet<>(Arrays.asList(
			new String[]{"PERSON","LOCATION","ORGANIZATION","DATE"}));
	private static CandidateMain candidateMain =CandidateMain.getCandidateMain();
	
	public static void main(String args[]){
		 // read some text in the text variable
        String path = "./data/ace2004/RawTexts/chtb_171.eng";
		String content = FileUtils.readFileContent(path);
//		String content = " The eighth Andes parliament meeting was convened in Lima on the 13th. How are you?";
        processTextTask(new Text(content));
//		testNLP(content);
	}
	
	/**
	 * 处理传入的文档，获得mention集及候选实体集
	 * @param text
	 */
	public static void processTextTask(Text text){
		
		 // create an empty Annotation just with the given text
        Annotation document = new Annotation(text.getContent());
        
        // run all Annotators on this text
        pipeline.annotate(document);
        Map<String, List<Integer>> wordsMap = new HashMap<String, List<Integer>>();
        Set<String> nerSet = new HashSet<>();
        List<Mention> mentions = new ArrayList<Mention>();
        TextContext textContext = new TextContext();
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
                
                //用于记录mention与text的上下文
                if(DictBean.getPosDict().contains(pos)){
                	if(DictBean.getDfDict().containsKey(lemma)){
                		DictBean.getDfDict().put(lemma, DictBean.getDfDict().get(lemma)+1);
                	}else{
                		DictBean.getDfDict().put(lemma, 1);
                	}
                	index = lenCount + token.index();
                	textContext.getContext().put(index, lemma);
                	if(wordsMap.containsKey(lemma)){
                		wordsMap.get(lemma).add(index);
                	}else{
                		List<Integer> mentionIndexList = new ArrayList<>(index);
                		wordsMap.put(lemma, mentionIndexList);
                	}
                	
                	if(nerCategory.contains(ne)){
                		nerSet.add(lemma);
                	}
                }
            }
            lenCount += sentence.size();
        }
       
        //分别保存mention与上下文
        for(Entry<String, List<Integer>>entry:wordsMap.entrySet()){
        	if(nerSet.contains(entry.getKey())){
        		Mention mention = new Mention(entry.getKey());
        		mention.setTfidfValue(entry.getValue().size()/(double)DictBean.getDfDict().get(entry.getKey()));
            	mention.setMentionIndex(entry.getValue());
            	mention.setCandidateEntity(candidateMain.candidatesOfMention(mention.getMentionName()));
            	mentions.add(mention);
        	}
        	
        	textContext.getWordTfidf().put(entry.getKey(), 
        			entry.getValue().size()/(double)DictBean.getDfDict().get(entry.getKey()));
        }
        text.setTextContext(textContext);
        text.getEntityGraph().setMentions(mentions);
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
