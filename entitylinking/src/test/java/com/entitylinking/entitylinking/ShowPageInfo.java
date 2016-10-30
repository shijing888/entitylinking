package com.entitylinking.entitylinking;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Title;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

import static de.tudarmstadt.ukp.wikipedia.api.WikiConstants.LF;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

/**
 * 使用wiki jar包连接数据库查询页面信息
 * @author shijing
 *
 */
public class ShowPageInfo {

	static Logger logger = Logger.getLogger(ShowPageInfo.class);
	static{
		PropertyConfigurator.configure("log4j.properties");
	}
    public static void main(String[] args) throws Exception {
      showPageInfo("michael jordan");
    }
    
    public static void showPageInfo(String title) throws WikiApiException{
    	  // 数据库连接参数配置
        DatabaseConfiguration dbConfig = new DatabaseConfiguration();
        dbConfig.setHost("localhost");   //主机名
        dbConfig.setDatabase("wikidata");  //数据库名
        dbConfig.setUser("root");        //访问数据库的用户名
        dbConfig.setPassword("mysql");    //访问数据库的密码
        dbConfig.setLanguage(Language.english);
        // 创建Wikipedia处理对象
        Wikipedia wiki = new Wikipedia(dbConfig);
//        String title = "micheal jordan";
        Page page = wiki.getPage(title);  
        logger.info(page.getText());
        // wikipedia页面的title
        System.out.println("Queried string       : " + title);
        System.out.println("Title                : " + page.getTitle());
        System.out.println("id                   : " + page.getPageId());
        // 是否是消歧页面
        System.out.println("IsDisambiguationPage : " + page.isDisambiguation());       
        // 是否是重定向页面
        System.out.println("redirect page query  : " + page.isRedirect());       
        // 有多少个页面指向该页面
        System.out.println("# of ingoing links   : " + page.getNumberOfInlinks());            
        // 该页面指向了多少个页面
        System.out.println("# of outgoing links  : " + page.getNumberOfOutlinks());
        // 该页面属于多少个类别
        System.out.println("# of categories      : " + page.getNumberOfCategories());
//        System.out.println("plainText"+"\t"+page.getPlainText());
//       logger.info("Text"+"\t"+page.getText());
        
        StringBuilder sb = new StringBuilder();
        // 页面的所有重定向页面
        sb.append("Redirects" + LF);
        for (String redirect : page.getRedirects()) {
            sb.append("  " + new Title(redirect).getPlainTitle() + LF);
        }
        sb.append(LF);     
//           
//        // 指向该页面的所有页面
//        sb.append("In-Links" + LF);
//        for (Page inLinkPage : page.getInlinks()) {
//            sb.append("  " + inLinkPage.getTitle() + LF);
//        }
//        sb.append(LF);
//
//        // 该页面指向的所有页面
//        sb.append("Out-Links" + LF);
//        for (Page outLinkPage : page.getOutlinks()) {
//            sb.append("  " + outLinkPage.getTitle() + LF);
//        } 
//     
        System.out.println(sb);
    }
    
    @Test
    public void test(){
    	Connection conn = null;
    	 String url = "jdbc:mysql://localhost:3306/wikidata?"
                 + "user=root&password=mysql&useUnicode=true&characterEncoding=UTF8";
    	 try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url);
			 Statement stmt = conn.createStatement();
	         String sql = "create table student(NO char(20),name varchar(20),primary key(NO))";
	            int result = stmt.executeUpdate(sql);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
	            if (result != -1) {
	                System.out.println("创建数据表成功");
	            }
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 动态加载mysql驱动
 catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}