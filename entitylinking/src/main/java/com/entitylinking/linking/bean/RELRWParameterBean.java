package com.entitylinking.linking.bean;

/**
 * REL-RW算法中的配置参数
 * @author HP
 *
 */
public class RELRWParameterBean {

	private static String sourceFileDirPath;
	private static int contextWindow;
	
	public static String getSourceFileDirPath() {
		return sourceFileDirPath;
	}
	public static void setSourceFileDirPath(String sourceFileDirPath) {
		RELRWParameterBean.sourceFileDirPath = sourceFileDirPath;
	}
	public static int getContextWindow() {
		return contextWindow;
	}
	public static void setContextWindow(int contextWindow) {
		RELRWParameterBean.contextWindow = contextWindow;
	}
	
}
