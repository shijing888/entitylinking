package com.entitylinking.wiki.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 使用dom4j解析xml文件
 * @author shijing
 *
 */
public class DomXML {

private void filterWiki(String rpath,String wpath){
		
		InputStream input;
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(wpath)),"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			//1.创建reader
			SAXReader reader = new SAXReader();
			//2.读取xml文件到document
			input = new FileInputStream(new File(rpath));
			Document document = reader.read(input);
			//获取xml的根节点
			Element root = document.getRootElement();
			List<Element> elements = root.elements();
			System.out.println(elements.size());
			//需要读取的属性
			String title;
			String ns;
			String id;
			String redirect;
			String text;
			
			for(Element element:elements){
				if(element.getName().equals("page")){
					bufferedWriter.write("pagebegin");
					bufferedWriter.write("\n");
					title = element.elementText("title");
					ns = element.elementText("ns");
					id = element.elementText("id");
					redirect = element.attributeValue("title");
					text = element.element("revision").elementText("text");
					
					bufferedWriter.write(title);
					bufferedWriter.write("\t");
					bufferedWriter.write(ns);
					bufferedWriter.write("\t");
					bufferedWriter.write(id);
					bufferedWriter.write("\t");
					if(redirect != null){
						bufferedWriter.write(redirect);
						
					}else{
						bufferedWriter.write("null");
					}
					bufferedWriter.write("\n");
					bufferedWriter.write(text);
					bufferedWriter.write("pageend");
					bufferedWriter.write("\n");
					
					bufferedWriter.flush();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
