package com.entitylinking.entitylinking;

import java.util.List;

import edu.stanford.nlp.simple.Sentence;

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
}
