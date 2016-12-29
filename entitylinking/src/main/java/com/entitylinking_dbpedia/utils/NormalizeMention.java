package com.entitylinking_dbpedia.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将文本中出现的词规范化成标准的mentions
 *			例如: michael jordan , Michael_Jordan
 * @author shijing
 *
 */
public class NormalizeMention {
	  
	public static void main(String args[]){
		
		System.out.println(NormalizeMention.getNormalizeMention("michael jeffy jordan ()",false));
	}
	
	public static List<String> getNormalizeMentionList(String[] titleList, boolean caseSensitive){
		List<String> normalMentionSet = new ArrayList<String>();
		for(String str:titleList){
			str = getNormalizeMention(str, caseSensitive);
			normalMentionSet.add(str);
		}
		return normalMentionSet;
	}
	
	/**
	 * 标准化
	 * @param titleText
	 * @param caseSensetive，若为true则全部小写
	 * @return
	 */
	public static String getNormalizeMention(String titleText, boolean caseSensetive){
		
			String rawTitleText = "";
			String wikiStyleTitle = "";
	        for(String str:titleText.split("[ _]")){
	        	if(str.equals(""))
	        		continue;
	        	if(str.contains("(") && str.contains(")")){
	        		rawTitleText = rawTitleText + str;
	        	}else{
	        		rawTitleText = rawTitleText + UpperCharacter(str) + "_";
	        	}
	        }
	        if(rawTitleText.endsWith("_"))
	        	rawTitleText = rawTitleText.substring(0, rawTitleText.length()-1);
	        String titlePart = null;
	        if (rawTitleText.contains("#")) {
	            titlePart = rawTitleText.substring(0, rawTitleText.lastIndexOf("#"));
	        }
	        else {
	            titlePart = rawTitleText;
	        }

	        String regexFindParts = "(.*?)[ _]\\((.+?)\\)$";

	        Pattern patternNamespace = Pattern.compile(regexFindParts);
	        Matcher matcherNamespace = patternNamespace.matcher(
	        		decodeTitleWikistyle(titlePart)
	        );

	        // group 0 is the whole match
	        if (matcherNamespace.find()) {
	            String relevantTitleParts = matcherNamespace.group(1) + " (" + matcherNamespace.group(2) + ")";
	            wikiStyleTitle = encodeTitleWikistyle(relevantTitleParts);
	        }
	        else {
	        	wikiStyleTitle = encodeTitleWikistyle(titlePart);
	        }

	      if(caseSensetive){
	    	  wikiStyleTitle = wikiStyleTitle.toLowerCase();
	      }
	      return wikiStyleTitle;
	}
	
	 private static String decodeTitleWikistyle(String pTitle) {
	        String encodedTitle = pTitle.replace('_', ' ');
	        return encodedTitle;
	 }
	 
	 private static String encodeTitleWikistyle(String pTitle) {
	        String encodedTitle = pTitle.replace(' ', '_');
	        return encodedTitle;
	}
	 
	 private static String UpperCharacter(String str){
		 char[] ch = str.toCharArray();
		 if(ch[0] >= 'a' && ch[0] <= 'z'){
		 ch[0] = (char)(ch[0] - 32);
		 }
		 return new String(ch); 
	 }
	 
}
