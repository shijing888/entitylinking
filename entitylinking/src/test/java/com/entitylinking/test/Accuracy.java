package com.entitylinking.test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.entitylinking_dbpedia.utils.CommonUtils;
import com.entitylinking_dbpedia.utils.Parameters;

public class Accuracy {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path ="./data/result/ace2004_dbpedia";
		String outpath = "./data/result/result.txt";
		String alignPath = "./dict/entityAlign.txt";
		genereateAccuracyResult(path, outpath,alignPath);
	}		
	
	public static void genereateAccuracyResult(String rpath, String wpath,String alignPath){
		Parameters parameters = new Parameters();
		Map<String, String> map = parameters.loadString2StringDict(alignPath);
		File file = new File(rpath);
		if(file.isDirectory()){
			File[] templist= file.listFiles();
			
			try {
				FileWriter filewriter = new FileWriter(new File(wpath));
				String temp = null;
				BufferedReader reader = null;
				//计算准确率
				int sum = 0 ;
				int error = 0;
				double correct = 0;
				//计算无nil情况下的精度、召回率与F1值
				int noneNilSum = 0;
				int noneNilCorrect = 0;
				int standardNilCount = 0;
				double precision = 0;
				double recall = 0;
				double f1 = 0;
				for(int i=0; i<templist.length;i++){				
					reader = new BufferedReader(new FileReader(templist[i]));
					while((temp = reader.readLine())!=null){
						String[] tempstrArray = temp.split("\t");
						if(tempstrArray.length == 4){
							sum++ ;
							if(map.containsKey(tempstrArray[2])){
								tempstrArray[2] = map.get(tempstrArray[2]);
							}
							tempstrArray[1] = CommonUtils.replaceConnector(tempstrArray[1]);
							tempstrArray[2] = CommonUtils.replaceConnector(tempstrArray[2]);
							if(!tempstrArray[1].equals(tempstrArray[2])){
								error++;					
								filewriter.write(temp +"\t" +templist[i].getName()+"\n");
							}
							//统计标准答案中nil的个数
							if(tempstrArray[1].equals("nil")){
								standardNilCount++;
							}
							if(!tempstrArray[2].equals("nil")){
								noneNilSum++;
								if(tempstrArray[1].equals(tempstrArray[2])){
									noneNilCorrect++;					
								}
							}
						}
						
					}
				
				}
			
				correct = (double)(sum -error) / sum;
				precision = (double)(noneNilCorrect) / noneNilSum;
				recall = (double)(noneNilCorrect) / (sum - standardNilCount);
				f1 = (2 * precision * recall) /(precision + recall);
				filewriter.write("总数:"+sum+"\t"+"错误数:"+error+"\t"+"准确率:"+correct);
				filewriter.write("\n"+"精度:"+precision);
				filewriter.write("\n"+"召回率:"+recall);
				filewriter.write("\n"+"f1值:"+f1);
				filewriter.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
		}
		

		
	}
}


