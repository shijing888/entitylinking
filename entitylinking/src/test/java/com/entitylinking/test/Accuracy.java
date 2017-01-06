package com.entitylinking.test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Accuracy {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path ="./data/result/ace2004_dbpedia";
		String outpath = "./data/result/result.txt";
		genereateAccuracyResult(path, outpath);
	}		
	
	public static void genereateAccuracyResult(String rpath, String wpath){
		
		File file = new File(rpath);
		if(file.isDirectory()){
			File[] templist= file.listFiles();
			
			try {
				FileWriter filewriter = new FileWriter(new File(wpath));
				String temp = null;
				BufferedReader reader = null;
				int sum = 0 ;
				int error = 0;
				double correct = 0;
				for(int i=0; i<templist.length;i++){				
					reader = new BufferedReader(new FileReader(templist[i]));
					while((temp = reader.readLine())!=null){
						sum++ ;
						String[] tempstrArray = temp.split("\t");
						if(tempstrArray.length == 4){
							if(!(tempstrArray[1].equals(tempstrArray[2]))){
								error++;					
								filewriter.write(temp +"\t" +templist[i].getName()+"\n");
							}
						}
						
					}
				
				}
			
				correct = (double)(sum -error) / sum;
				filewriter.write("总数:"+sum+"\t"+"错误数:"+error+"\t"+"准确率:"+correct);
				filewriter.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
		}
		

		
	}
}


