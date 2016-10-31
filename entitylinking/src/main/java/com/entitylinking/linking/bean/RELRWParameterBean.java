package com.entitylinking.linking.bean;

/**
 * REL-RW算法中的配置参数
 * @author HP
 *
 */
public class RELRWParameterBean {

	private static String sourceFileDirPath;

	public static String getSourceFileDirPath() {
		return sourceFileDirPath;
	}

	public static void setSourceFileDirPath(String sourceFileDirPath) {
		RELRWParameterBean.sourceFileDirPath = sourceFileDirPath;
	}
	
}
