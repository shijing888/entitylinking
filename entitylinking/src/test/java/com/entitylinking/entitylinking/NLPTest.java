package com.entitylinking.entitylinking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import com.entitylinking.utils.FileUtils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

public class NLPTest {

	public static void main(String args[]){

		Sentence sentence = new Sentence("Lucy is in the sky with diamonds.");
//		List<String> nerTags = sentence.nerTags();
//		for(String ner:nerTags){
//			System.out.println(ner);
//		}
		List<String> mentions = sentence.mentions();
		for(String mention:mentions){
			System.out.println(mention);
		}
//		Document doc = new Document("Lucy is in the sky with diamonds.");
//        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
//            // We're only asking for words -- no need to load any models yet
//            System.out.println("The second word of the sentence '" + sent + "' is " + sent.word(1));
//            // When we ask for the lemma, it will load and run the part of speech tagger
//            System.out.println("The third lemma of the sentence '" + sent + "' is " + sent.lemma(2));
//            // When we ask for the parse, it will load and run the parser
//            System.out.println("The parse of the sentence '" + sent + "' is " + sent.parse());
//            // ...
//        }
	}
	
	@Test
	public void test(){
		String path = "./data/ace2004/RawTexts/chtb_165.eng";
		String content = FileUtils.readFileContent(path);
//		Properties props = new Properties();
//		props.setProperty("annotators", "pos, lemma, ner");
		StanfordCoreNLP pipline = new StanfordCoreNLP(PropertiesUtils.asProperties("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref",
				"pos.model","edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger"));System.out.println("ee");
		Annotation document = new Annotation(content);System.out.println("ff");
		pipline.annotate(document);
		System.out.println("dd");
		List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
		
		String[] strs = new String[]{"PERSON","ORGANIZATION","LOCATION"};
		Set<String> set = new HashSet<>(Arrays.asList(strs));
		
		List<String> list = new ArrayList<String>();
		for(CoreMap sentence: sentences) {
		  // traversing the words in the current sentence
		  // a CoreLabel is a CoreMap with additional token-specific methods
			System.out.println(sentence.toString());
			String entity = null;
			String type = null;
		  for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
		    // this is the text of the token
		    String word = token.get(CoreAnnotations.TextAnnotation.class);
		    // this is the POS tag of the token
		    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
		    // this is the NER label of the token
		    String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
		    // this is the lemma of the token
		    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
		    System.out.println(word+"\t"+pos+"\t"+ne+"\t"+lemma);
		    if(set.contains(ne)){
		    	if(type == null){
		    		type = ne;
		    		entity = lemma;
		    	}else{
		    		if(type != ne){
		    			list.add(type+" || "+entity);
		    			type = ne;
		    			entity = lemma;
		    		}else{
		    			entity += "_" + lemma;
		    		}
		    	}
		    }else{
		    	if(type != null && entity != null){
		    		list.add(type+" || "+entity);
		    		type = null;
			    	entity = null;
		    	}
		    	
		    }
		  }
		}
		for(String item:list)
			System.out.println(item);
	}
}
