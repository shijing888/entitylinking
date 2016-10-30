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
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
import java.util.Set;  
  
import javax.xml.parsers.SAXParser;  
import javax.xml.parsers.SAXParserFactory;  
  
import org.apache.commons.lang.StringUtils;  
import org.xml.sax.Attributes;  
import org.xml.sax.SAXException;  
import org.xml.sax.helpers.DefaultHandler;

import com.entitylinking.bean.FilterXMLBean;  
  
/**
 * 通过sax解析xml
 * 
 * @author shijing
 *
 */
public class XMLParser extends DefaultHandler {  
  
	/**
	 * maxListLength,设置最大list长度，达到后输出到文件
	 * idRepeatCount,记录元素“id”出现的次数，只保存值为1时的id
	 * beanList，保存解析出来的内容
	 * xmlBean，保存解析出来的具体元素
	 * currentTag，保存当前的元素标签
	 * sb，用于保存读取的文本
	 * writePath，保存文件的路径
	 */
    private int maxListLength = 0;
    private int idRepeatCount = 0;
    private List<FilterXMLBean> beanList;
    private FilterXMLBean xmlBean;
    private String currentTag = null;  
    private StringBuilder sb = new StringBuilder();
    private String writePath = null;
    
    public XMLParser(String writePath,int maxListLength){
    	this.writePath = writePath;
    	this.maxListLength = maxListLength;
    }
    
    @Override
    public void startDocument() throws SAXException {
    	// TODO Auto-generated method stub
    	beanList = new ArrayList<FilterXMLBean>();
    }
  
    @Override
    public void endDocument() throws SAXException {
    	// TODO Auto-generated method stub
    	 writeToFile();  
    }
    
    @Override  
    public void characters(char[] ch, int start, int length)throws SAXException {  
        if(this.currentTag != null){  
        	String value = new String(ch, start, length);  
            if(this.currentTag.equals("title"))
            	this.xmlBean.setTitle(value);
            else if(this.currentTag.equals("ns"))
            	this.xmlBean.setNs(value);
            else if(this.currentTag.equals("id") && this.idRepeatCount == 1)
            	this.xmlBean.setId(value);
            else if(this.currentTag.equals("text"))
            	this.sb.append(value);
        }  
    }  
  
    @Override  
    public void startElement(String uri, String localName, String qName,  
            Attributes attributes) throws SAXException {
        if ("page".equals(qName)) {
            this.xmlBean = new FilterXMLBean();
        }  
        else if ("redirect".equals(qName)) {
            this.xmlBean.setRedirect(attributes.getValue("title")); 
        }else if("id".equals(qName)){
        	this.idRepeatCount++;
        }
        this.sb.delete(0, sb.length());
        this.currentTag = qName;  
    }  
    
    @Override  
    public void endElement(String uri, String localName, String qName)  
            throws SAXException {  
        if("page".equals(qName)){  
           this.beanList.add(this.xmlBean);
           this.idRepeatCount = 0;
        }else if("text".equals(qName)){
        	this.xmlBean.setText(this.sb.toString());
        }  
          
        if(beanList.size() == maxListLength){  
            writeToFile();  
            beanList.clear();  
        }  
        this.currentTag = null;    
    }  
  
  
    public void writeToFile(){ 
    	try {
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(this.writePath), true),"UTF-8"));
			 for(FilterXMLBean xmlBean:this.beanList){
				 bufferedWriter.write("pagebegin");
				 bufferedWriter.write("\n");
				bufferedWriter.write(xmlBean.getTitle());
				bufferedWriter.write("\t");
				bufferedWriter.write(xmlBean.getNs());
				bufferedWriter.write("\t");
				bufferedWriter.write(xmlBean.getId());
				bufferedWriter.write("\t");
				if(xmlBean.getRedirect() != null){
					bufferedWriter.write(xmlBean.getRedirect());
					
				}else{
					bufferedWriter.write("null");
				}
				bufferedWriter.write("\n");
				bufferedWriter.write(xmlBean.getText());
				bufferedWriter.write("\n");
				bufferedWriter.write("pageend");
				bufferedWriter.write("\n");
				
				bufferedWriter.flush();
		     }
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