package com.entitylinking.wiki.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * 解析xml
 * @author shijing
 *
 */
public class FilterWikiXML {
	
	private XMLParser xmlParser = null;
	
	public static void main(String args[]){
		String rpath = "D:\\迅雷下载\\enwiki-20160901-pages-articles-multistream.xml\\enwiki-20160901-pages-articles-multistream.xml";
//		String rpath = "D:\\迅雷下载\\enwiki-20160901-pages-articles-multistream.xml\\enwiki.xml";
		String wpath = "D:\\迅雷下载\\enwiki-20160901-pages-articles-multistream.xml\\filterWiki.txt";
		FilterWikiXML filterWikiXML = new FilterWikiXML();
		filterWikiXML.xmlParser = new XMLParser(wpath, 10000);
		filterWikiXML.filterWiki(rpath, wpath);
	}
	
	private void filterWiki(String rpath,String wpath){
		
		  SAXParser parser = null;  
	        try {  
	            //构建SAXParser  
	            parser = SAXParserFactory.newInstance().newSAXParser();  
	           InputStream stream = new FileInputStream(new File(rpath));
	            //调用parse()方法  
	            parser.parse(stream, this.xmlParser);  
	        }catch (IOException e) {  
	            e.printStackTrace();  
	        } catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	    }  
	
	@Test
	public void readFile(){
		String rpath = "D:\\迅雷下载\\enwiki-20160901\\output\\Page.txt";
		String wpath = "H:\\MysqlData\\Page2.txt";
		int lineCount = 100;
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(rpath)), "utf-8"));
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(wpath))));
//			String line = "id" + "\t" + "pageId" + "\t" + "name" + "\t" + "text" + "\t";
			int i = 0;
//			bufferedWriter.write(line);
//			bufferedWriter.write("\n");
			String line;
			while(i<lineCount && (line=bufferedReader.readLine())!=null){
				bufferedWriter.write(line+"\n");
				i++;
			}
			
			bufferedReader.close();
			bufferedWriter.close();
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
}
