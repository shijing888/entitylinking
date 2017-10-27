package com.entitylinking_dbpedia.utils;

/**
 * 用于计算字符串间的编辑距离
 * @author HP
 *
 */
public class StringDistance {

	 public static void main(String[] args) {  
	        String s = "Democratic", t="Democrats";  
	        int d = getEditDistance(s, t);  
	        System.out.println(d);  
	        
	        d = getLCSDistance(s, t);
	        System.out.println(d); 
	    }  
	    //返回三者最小值  
	    private static int Minimum(int a, int b, int c) {  
	        int im =  a<b ? a : b;  
	        return im<c ? im : c;  
	    }  
	     
	    /**
	     * 获得编辑距离
	     * @param s
	     * @param t
	     * @return
	     */
	    public static int getEditDistance(String s, String t) {  
	        int d[][]; // matrix  
	        int n; // length of s  
	        int m; // length of t  
	        int i; // iterates through s  
	        int j; // iterates through t  
	        char s_i; // ith character of s  
	        char t_j; // jth character of t  
	        int cost; // cost  
	          
	        // Step 1  
	        n = s.length();  
	        m = t.length();  
	        if (n == 0) {  
	            return m;  
	        }  
	        if (m == 0) {  
	            return n;  
	        }  
	        d = new int[n + 1][m + 1];  
	          
	        // Step 2  
	        for (i = 0; i <= n; i++) {  
	            d[i][0] = i;  
	        }  
	        for (j = 0; j <= m; j++) {  
	            d[0][j] = j;  
	        }  
	          
	        // Step 3  
	        for (i = 1; i <= n; i++) {  
	            s_i = s.charAt(i - 1);  
	            // Step 4  
	            for (j = 1; j <= m; j++) {  
	                t_j = t.charAt(j - 1);  
	                // Step 5  
	                cost = (s_i == t_j) ? 0 : 1;  
	                // Step 6  
	                d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,  
	                        d[i - 1][j - 1] + cost);  
	            }  
	        }  
	        // Step 7  
//	        print(d, m, n);  
	        return d[n][m];  
	    }  
	    
	    /**
	     * 打印出距离矩阵
	     * @param d
	     * @param m
	     * @param n
	     */
	    public static void print(int d[][],int m, int n){  
	        for (int i = 0; i <= n; i++) {  
	            for (int j = 0; j <= m; j++) {  
	                System.out.print(d[i][j]+" ");  
	            }  
	            System.out.println();  
	        }  
	    }  
	    
	   
	    /**
	     * 最长公共子序列
	     * @param str1
	     * @param str2
	     * @return
	     */
	    public static int getLCSDistance(char[] str1, char[] str2)
	    {
	        int substringLength1 = str1.length;
	        int substringLength2 = str2.length;
	 
	        // 构造二维数组记录子问题A[i]和B[j]的LCS的长度
	        int[][] opt = new int[substringLength1 + 1][substringLength2 + 1];
	 
	        // 从后向前，动态规划计算所有子问题。也可从前到后。
	        for (int i = substringLength1 - 1; i >= 0; i--)
	        {
	            for (int j = substringLength2 - 1; j >= 0; j--)
	            {
	                if (str1[i] == str2[j])
	                    opt[i][j] = opt[i + 1][j + 1] + 1;// 状态转移方程
	                else
	                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);// 状态转移方程
	            }
	        }
	        System.out.println("substring1:" + new String(str1));
	        System.out.println("substring2:" + new String(str2));
	        System.out.print("LCS:");
	 
	        int i = 0, j = 0;
	        while (i < substringLength1 && j < substringLength2)
	        {
	            if (str1[i] == str2[j])
	            {
	                System.out.print(str1[i]);
	                i++;
	                j++;
	            }
	            else if (opt[i + 1][j] >= opt[i][j + 1])
	                i++;
	            else
	                j++;
	        }
	        System.out.println();
	        return opt[0][0];
	    }
	 
	    public static int getLCSDistance(String str1, String str2)
	    {
	        return getLCSDistance(str1.toCharArray(), str2.toCharArray());
	    } 
}
