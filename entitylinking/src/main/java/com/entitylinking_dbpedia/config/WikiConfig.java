package com.entitylinking_dbpedia.config;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;

/**
 * 配置连接wiki数据库
 * @author shijing
 *
 */
public class WikiConfig {

	private static  DatabaseConfiguration dbConfig = new DatabaseConfiguration();
	public static Wikipedia getWiki(){
		dbConfig.setHost("localhost");   //主机名
        dbConfig.setDatabase("wikidata");  //数据库名
        dbConfig.setUser("root");        //访问数据库的用户名
        dbConfig.setPassword("mysql");    //访问数据库的密码
        dbConfig.setLanguage(Language.english);
        // 创建Wikipedia处理对象
        try {
			Wikipedia wiki = new Wikipedia(dbConfig);
			return wiki;
		} catch (WikiInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
