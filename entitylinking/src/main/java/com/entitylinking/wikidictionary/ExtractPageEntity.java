package com.entitylinking.wikidictionary;

import java.util.HashSet;
import java.util.regex.Matcher;

/**
 * 提取消歧页中的实体
 * @author shijing
 *
 */
public class ExtractPageEntity {
	
    public static void pageEntity(Matcher matcher, HashSet<String> set){
    	while(matcher.find()){
    		String result = matcher.group();
    		//去除类别实体
    		if(result.contains(":"))
    			continue;
            int i = result.indexOf("|");
    		int j = result.lastIndexOf("|");   		
    		if(i==j&&i!=-1){
    			//有竖线且竖线唯一
    			String templink1 = result.substring(2, i);
    			String templink2 = result.substring(i+1, result.length()-2);
    
    			templink2 = templink2.replaceAll("\"|\'{2,3}", "");
    			
    			if(templink1.equals(templink2)){
    				//去格式化后前后相同
    				set.add(templink1);
    			}else{
    				//去格式化后前后不同
    				set.add(templink1);
    				set.add(templink2);
    			}
    			
    		}
    		if(i==-1){   
    			//不包含竖线，
    			String result1 = result.substring(2, result.length()-2);
    			set.add(result1);
			    }   		
    	}
    }
    
}
