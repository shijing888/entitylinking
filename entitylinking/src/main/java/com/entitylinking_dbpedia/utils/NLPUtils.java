package com.entitylinking_dbpedia.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import com.entitylinking_dbpedia.linking.bean.PathBean;
import com.entitylinking_dbpedia.utils.CommonUtils;
import com.entitylinking_dbpedia.utils.Parameters;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;

import com.entitylinking.config.WikiConfig;
import com.entitylinking_dbpedia.linking.bean.DictBean;
import com.entitylinking_dbpedia.linking.bean.Entity;
import com.entitylinking_dbpedia.linking.bean.Mention;
import com.entitylinking_dbpedia.linking.bean.RELRWParameterBean;
import com.entitylinking_dbpedia.linking.bean.Text;
import com.entitylinking_dbpedia.lucene.IndexFile;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.ling.CoreAnnotations;
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
//	private static Set<String> nerCategory = new HashSet<>(Arrays.asList(
//			new String[]{"PERSON","LOCATION","ORGANIZATION","DATE"}));
	
	public static void main(String args[]){
		 // read some text in the text variable
//        String path = "./data/ace2004/RawTexts/chtb_171.eng";
//		String content = FileUtils.readFileContent(path);
//        processTextTask(new Text(content));

		String dirPath = "./data/ace2004/RawTexts";
		String wpath = "./dict/disAmbiguationMention.txt";
		try {
			disambiguationMention(dirPath,wpath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 处理传入的文档，获得mention集、mention的上下文信息及候选实体集
	 * @param text
	 */
	public static void getTextMentionTask(Text text){
		//mentions从xml中已经加载完成
        List<Mention> mentions = text.getEntityGraph().getMentions();
        Parameters parameters = new Parameters();
//        DictBean dictBean = parameters.loadSurfaceFormDict();
        String mentionContextPath = PathBean.getMentionContextDirPath() + text.getTextName();
        String entityByDbpediaContextPath = PathBean.getEntityByDbpediaContextPath();
        String entityCategoryPath = PathBean.getEntityCategoryPath();
        Map<String, HashSet<String>> mentionContextMap = parameters.loadMapDict(mentionContextPath);
        Map<String, HashSet<String>> entityCategoryMap = parameters.loadMapDict(entityCategoryPath);
        DictBean.setEntityContextDict(parameters.loadMapDict(entityByDbpediaContextPath));
        DictBean.setEntityCategoryDict(entityCategoryMap);

        String content;
        int mentionOffset;
        int beginOffset;
        int endOffset;
        int textLen = text.getContent().split("\\s+").length;
        double tfidfValue;
        Map<String, Set<String>> additiveMentionContextDict = new HashMap<String, Set<String>>();
        Map<String, Set<String>> additiveEntityContextDict = new HashMap<String, Set<String>>();
        Map<String, Set<String>> additiveEntityCategoryDict = new HashMap<String, Set<String>>();
        
        for(Mention mention:mentions){
        	logger.info("mention:"+mention.getMentionName());
        	//初始化mention tfidf
        	tfidfValue = CommonUtils.calTfidf(mention.getOccurCounts(), 
    				DictBean.getDfDict().get(mention.getMentionName()), textLen);
        	logger.info("mention出现次数:"+mention.getOccurCounts());
        	logger.info("mention df:"+DictBean.getDfDict().get(mention.getMentionName()));
        	logger.info(mention.getMentionName()+"的tfidf值为:"+tfidfValue);
    		mention.setTfidfValue(tfidfValue);
    		//获取候选实体
    		List<Entity> candidateEntity = mention.obtainCandidate(additiveEntityContextDict,
    				additiveEntityCategoryDict);
    		mention.setCandidateEntity(candidateEntity);
    		
    		if(mentionContextMap.containsKey(mention.getMentionName())){
    			mention.setMentionContext(mentionContextMap.get(mention.getMentionName()));
    		}else{
        		//通过mention的offset及窗口来获取上下文
            	mentionOffset = mention.getMentionOffset();
            	if(mentionOffset - RELRWParameterBean.getContextWindow() < 0){
            		beginOffset = 0;
            	}else{
            		beginOffset  = mentionOffset - RELRWParameterBean.getContextWindow();
            	}
            	
            	if(mentionOffset + RELRWParameterBean.getContextWindow() >= text.getContent().length()){
            		endOffset = text.getContent().length();
            	}else{
            		endOffset = mentionOffset + RELRWParameterBean.getContextWindow();
            	}
            	//substring左闭右开
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
                        // this is the POS tag of the token
                        String pos = token.get(PartOfSpeechAnnotation.class);
                        // this is the NER label of the token
//                        String ne = token.get(NamedEntityTagAnnotation.class);
                        // this is the NER label of the token
                        String lemma = token.get(LemmaAnnotation.class).toLowerCase();
                        //去停用词
                        if(DictBean.getStopWordDict().contains(lemma) || lemma.length() < 2)
                        	continue;
                        //用于记录mention的上下文
                        if(DictBean.getPosDict().contains(pos)){
                        	mention.getMentionContext().add(lemma);
                        }
                    }
                }
                logger.info(mention.getMentionName()+"不在mention上下文词典中");
                additiveMentionContextDict.put(mention.getMentionName(), mention.getMentionContext());
    		}

        }
        //将词典持久化到本地
        parameters.pickleContextMap(mentionContextPath, additiveMentionContextDict);
        parameters.pickleContextMap(entityByDbpediaContextPath, additiveEntityContextDict);
        parameters.pickleContextMap(entityCategoryPath, additiveEntityCategoryDict);
        //清空词典
        additiveMentionContextDict.clear();
        additiveEntityContextDict.clear();
        additiveEntityCategoryDict.clear();
        
        //对mention按照歧义性排序，歧义性按照候选实体个数来定义
        Collections.sort(mentions, new Comparator<Mention>() {
			@Override
			public int compare(Mention arg0, Mention arg1) {
				// TODO Auto-generated method stub
				return arg0.getCandidateEntity().size() - arg1.getCandidateEntity().size();
			}
		});
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
//               String word = token.get(TextAnnotation.class);
               // this is the POS tag of the token
               String pos = token.get(PartOfSpeechAnnotation.class);
               // this is the NER label of the token
//               String ne = token.get(NamedEntityTagAnnotation.class);
               String lemma = token.get(LemmaAnnotation.class).toLowerCase();
               //去停用词
               if(DictBean.getStopWordDict().contains(lemma) || lemma.length() < 2)
               	continue;
               //用于记录mention与text的上下文
               if(DictBean.getPosDict().contains(pos)){
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
			Map<String, Set<String>> dfMap = new HashMap<>();
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
		                
//		                if(DictBean.getPosDict().contains(pos)){
//		                	if(DictBean.getDfDict().containsKey(lemma)){
//		                		DictBean.getDfDict().put(lemma, DictBean.getDfDict().get(lemma)+1);
//		                	}else{
//		                		DictBean.getDfDict().put(lemma, 1);
//		                	}
//		                	
//		                	
//		                }
		                
		                if(DictBean.getPosDict().contains(pos)){
		                	if(dfMap.containsKey(lemma)){
		                		dfMap.get(lemma).add(file.getName());
		                	}else{
		                		Set<String>set = new HashSet<>();
		                		set.add(file.getName());
		                		dfMap.put(lemma, set);
		                	}
		                	
		                	
		                }
		            }
		        } 
			}
			
			for(Entry<String, Set<String>>entry:dfMap.entrySet()){
				DictBean.getDfDict().put(entry.getKey(), entry.getValue().size());
			}
		}
		
		
		FileUtils.writeFileContent(savePath, DictBean.getDfDict());
	}
	
	/**
	 * 用斯坦福工具包获取无歧义mention对应的唯一实体
	 * @param fileDir,wpath
	 * @throws IOException 
	 */
	public static void disambiguationMention(String fileDir,String wpath)
			throws IOException{
		Map<String, HashSet<String>> mentionMap = mentionOfText(fileDir);
		System.out.println("mentionMap size:"+mentionMap.size());

		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(new File(wpath)), "utf-8"));
		StringBuilder builder = new StringBuilder();
		
		Wikipedia wiki = WikiConfig.getWiki();
		Page page;
		
		for(Entry<String, HashSet<String>>entry:mentionMap.entrySet()){
			String textName = entry.getKey();
			HashSet<String>set = entry.getValue();
			Iterator<String>iterator = set.iterator();
			while(iterator.hasNext()){
				String str = iterator.next();
				try {
					page = wiki.getPage(str);
					int num = page.getNumberOfInlinks() + page.getNumberOfOutlinks();
					System.out.println(str+"的流行度是:"+num);
					if(num < 100){
						iterator.remove();
					}
				}catch (Exception e) {
					// TODO: handle exception
					iterator.remove();
				}
			}
    		builder.delete(0, builder.length());
    		builder.append(textName).append("\t||\t").append(StringUtils.join(set,"\t|\t")).append("\n");
    		writer.write(builder.toString());
			
		}
		writer.close();
	}
	
	/**
	 * 用斯坦福工具包获取文本的mention
	 * @param fileDir
	 * @return
	 */
	public static Map<String, HashSet<String>> mentionOfText 
						(String fileDir){
		File dir = new File(fileDir);
		if(dir.isDirectory()){
			File[] files = dir.listFiles();
			Map<String, HashSet<String>> mentionMap = new HashMap<>();
			String[] strs = new String[]{"PERSON","ORGANIZATION","LOCATION"};
			Set<String> entityTypeSet = new HashSet<>(Arrays.asList(strs));
			for(File file:files){
				 // create an empty Annotation just with the given text
				String content = FileUtils.readFileContent(file.getAbsolutePath());
		        Annotation document = new Annotation(content);
		        
		        // run all Annotators on this text
		        pipeline.annotate(document);
		        // these are all the sentences in this document
		        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		        
		        HashSet<String> entitySet = new HashSet<String>();
		        for(CoreMap sentence: sentences) {
		            // a CoreLabel is a CoreMap with additional token-specific methods
		        	String entity = null;
					String type = null;
		            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
		    		    // this is the text of the token
		    		    String word = token.get(CoreAnnotations.TextAnnotation.class);
		    		    // this is the POS tag of the token
		    		    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
		    		    // this is the NER label of the token
		    		    String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
		    		    // this is the lemma of the token
		    		    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
		    		    System.out.println(word+"\t"+pos+"\t"+ne+"\t"+lemma);
		    		    if(entityTypeSet.contains(ne)){
		    		    	if(type == null){
		    		    		type = ne;
		    		    		entity = lemma;
		    		    	}else{
		    		    		if(type != ne){
		    		    			entitySet.add(entity.toLowerCase());
		    		    			type = ne;
		    		    			entity = lemma;
		    		    		}else{
		    		    			entity += "_" + lemma;
		    		    		}
		    		    	}
		    		    }else{
		    		    	if(type != null && entity != null){
		    		    		entitySet.add(entity.toLowerCase());
		    		    		type = null;
		    			    	entity = null;
		    		    	}
		    		    	
		    		    }
		    		  
		            }
		        } 
		        
		        mentionMap.put(file.getName(), entitySet);
			}
			
			return mentionMap;
		}
		
		return null;
	}
	
	
	/**
	 * 判断mention是否有歧义，若有则返回null，否则返回其所对应的唯一实体
	 * @param mention
	 * @return
	 */
	public static String judgeMentionAmbiguation(String mention){

		String mentionStr = NormalizeMention.getNormalizeMention(mention,true);
		Document document;
		//先从同义词典中寻找标准mention形式
		document = IndexFile.queryDocument(mentionStr, RELRWParameterBean.getSynonymsDictField1(),
				PathBean.getSynonymsDictPath());
		if(document != null){
			mentionStr = document.get(RELRWParameterBean.getSynonymsDictField2());
		}
		//再从歧义词典中寻找
		document = IndexFile.queryDocument(mentionStr, RELRWParameterBean.getAmbiguationDictField1(),
				PathBean.getAmbiguationDictPath());
		if(document != null){
			String[] candidates = document.get(RELRWParameterBean.getAmbiguationDictField2()).split("\t\\|\t");
			if(candidates.length == 1)
				return candidates[0];
		}
		
		return null;
	}
	
	
	/**
	 * 处理摘要文字
	 * @param rpath
	 * @param wpath
	 */
	public static void processAbstract(String rpath,String wpath){
		BufferedWriter writer;
		try {
			File rfile = new File(rpath);
			File wfile = new File(wpath);
			if(!wfile.exists()){
				wfile.createNewFile();
			}
			if(rfile.isDirectory()){
				File[] files = rfile.listFiles();
				for(File file2:files){
					rpath = file2.getPath();
					StringBuilder stringBuilder = new StringBuilder();
					Model model = ModelFactory.createDefaultModel();
					model.read(rpath);
					System.out.println("rpath:"+rpath);
					int i = 0;
					writer = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(wfile,true), "utf-8"));
					StmtIterator stmtIterator = model.listStatements();
					String subject,object;
					while(stmtIterator.hasNext()){
						System.out.println(i++);
						Statement statement = stmtIterator.next();
						subject = statement.getSubject().toString();
						subject = subject.substring(subject.lastIndexOf("/")+1).toLowerCase();
						object = statement.getObject().asLiteral().toString().toLowerCase();
//						System.out.println("object:"+object);
						Set<String> contextSet = new HashSet<>();
						 // create an empty Annotation just with the given text
			            Annotation document = new Annotation(object);
			            // run all Annotators on this text
			            pipeline.annotate(document);
			            // these are all the sentences in this document
			            List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			            for(CoreMap sentence: sentences) {
			                // a CoreLabel is a CoreMap with additional token-specific methods
			                for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
			                    // this is the POS tag of the token
			                    String pos = token.get(PartOfSpeechAnnotation.class);
			                    // this is the NER label of the token
//			                    String ne = token.get(NamedEntityTagAnnotation.class);
			                    // this is the NER label of the token
			                    String lemma = token.get(LemmaAnnotation.class).toLowerCase();
			                    //去停用词
			                    if(DictBean.getStopWordDict().contains(lemma) || lemma.length() < 2)
			                    	continue;
			                    //用于记录mention的上下文
			                    if(DictBean.getPosDict().contains(pos)){
			                    	contextSet.add(lemma);
			                    }
			                }
			            }
						
			            if(contextSet.size() > 0){
			            	stringBuilder.delete(0, stringBuilder.length());
			  	            stringBuilder.append(subject).append("\t||\t")
			  	            			 .append(StringUtils.join(contextSet, "\t|\t")).append("\n");
			  	            
			  	            writer.write(stringBuilder.toString());
			  			    writer.flush();
			            }
			            
					}
					
					writer.close();
				}
			}
			
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
