package com.entitylinking.wikidictionary;

/**
 * 生成词典的主函数
 * @author shijing
 *
 */
public class WikiMain {

	public static void main(String args[]){
		GenerateDictionary generateDictionary  = new GenerateDictionary();
		String saveDisambiguationDictPath = "H:\\MysqlData\\ambiguationDict2.txt";
		String saveSynonymsDictPath = "H:\\MysqlData\\synonymsDict.txt";
		generateDictionary.recordPageTable(saveDisambiguationDictPath, saveSynonymsDictPath);
	}
}
