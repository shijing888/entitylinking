package com.entitylinking.wikidictionary;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
/**
 * 提取实体页内的超链接
 * @author shijing
 *
 */
public class ExtractInPageEntity {
	   
     public static void inPageEntity(String strin, Map<String,HashSet<String>> map){
     	int len = strin.length();
  		LinkedList<Integer> stack = new LinkedList<Integer>();
  		
  		int k=0,n;
  		String temp;
  		
  		for(int i=0;i<len-1;i++){
  			if(isleft(strin.charAt(i))&& isleft(strin.charAt(i+1))){
  				if(stack.isEmpty()){
  					k=i;//栈底元素记位置
  					i++;
  					}
  				stack.push(i);
  			}else if(isright(strin.charAt(i))&&isright(strin.charAt(i+1))){
  				if(stack.isEmpty()){
  					System.out.println("栈空匹配但是匹配");
  					return;
  				}else if(stack.peek().equals(strin.charAt(i))){
  					System.out.println("匹配错误");
  					return;
  				}else{
  					stack.pop();
  					if(stack.isEmpty()){
  						n=i+2;
  						temp = strin.substring(k, n);
  						int j = temp.indexOf("|");
  						int m = temp.lastIndexOf("|");
  						
  						if(m==j&&j!=-1){
  			    			//有竖线且竖线唯一
  			    			String templink1 = temp.substring(2, j);
  			    			String templink2 = temp.substring(j+1, temp.length()-2);
  			    			//去除格式及锚点
  			    			templink2 = templink2.replaceAll("\"|\'{2,3}", "");
  			    			if(templink1.contains("#"))
  			    				templink1 = templink1.substring(0, templink1.indexOf("#"));
  			    			if(!templink1.equals(templink2)){
  			    				if(map.containsKey(templink2)){
  			    					map.get(templink2).add(templink1);
  			    				}else{
  			    					HashSet<String> set = new HashSet<>();
  			    					set.add(templink1);
  			    					map.put(templink2, set);
  			    				}
  			    			}
  						}
  						if(j==-1){   
  			    			continue;
  						    }
  						if(j!=m && j!=-1){
  							//有竖线且竖线不唯一
  							continue;
  						}
  						
  					     }
  				     }
  				     i++;
  			      }
  		}
     }
     private static  boolean isleft(char ch){
 		if(ch == '[') return true;
 		else return false;
 	}
 	
 	private static boolean isright(char ch){
 		if(ch == ']') return true;
 		else return false; 		
 	}
}
