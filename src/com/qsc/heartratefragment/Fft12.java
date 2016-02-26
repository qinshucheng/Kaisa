package com.qsc.heartratefragment;

public class Fft12 {
	
	/** 
     * 一维快速傅里叶变换 
     * @param values 一维复数集数组 
     * @return 傅里叶变换后的数组集 
     */  
	public Complex[] fft(Complex[] values) {
        int n = values.length;  
        int r = (int)(Math.log10(n)/Math.log10(2)); //求迭代次数r  
        Complex[][] temp = new Complex[r+1][n]; //计算过程的临时矩阵  
        Complex w = new Complex(0, 0);  //权系数  
        temp[0] = values;  
        int x1, x2; //一对对偶结点的下标值  
        int p, t;   //p表示加权系数Wpn的p值, t是重新排序后对应的序数值  
        for(int l=1; l<=r; l++) {  
            if(l != r) {  
                for(int k=0; k<n; k++) {  
                    if(k < n/Math.pow(2, l)) {  
                        x1 = k;  
                        x2 = x1 + (int)(n/Math.pow(2, l));  
                    } else {  
                        x2 = k;  
                        x1 = x2 - (int)(n/Math.pow(2, l));  
                    }  
                    p = getWeight(k, l, r);
                    w.setRe(Math.cos(-2*Math.PI*p/n));  
                    w.setIm(Math.sin(-2*Math.PI*p/n));  
                    temp[l][k] = Complex.plus(temp[l-1][x1] , w.times(temp[l-1][x2]) );  
                      
                }  
            } else {  
                for(int k=0; k<n/2; k++) {                     
                    x1 = 2*k;  
                    x2 = 2*k+1;  
                    //System.out.println("x1:" + x1 + "  x2:" + x2);  
                    t = reverseRatio(2*k, r);  
                    p = t;  
                    w.setRe(Math.cos(-2*Math.PI*p/n));  
                    w.setIm(Math.sin(-2*Math.PI*p/n));  
                    temp[l][t] = Complex.plus(temp[l-1][x1] , w.times(temp[l-1][x2]) );  
                    t = reverseRatio(2*k+1, r);  
                    p = t;  
                    w.setRe(Math.cos(-2*Math.PI*p/n));  
                    w.setIm(Math.sin(-2*Math.PI*p/n));  
                    temp[l][t] = Complex.plus(temp[l-1][x1] , w.times(temp[l-1][x2]) );  
                }  
            }             
        }         
        return temp[r];  
    }  
	/** 
     *  二维快速傅里叶变换 
     * @param matrix 二维复数集数组      
     * @param w 图像的宽 
     * @param h 图像的高 
    * @return 傅里叶变换后的数组集 
     */ 
	public Complex[][] fft(Complex matrix[][], int w, int h) {  
        double r1 = Math.log10(w)/Math.log10(2.0) - (int)(Math.log10(w)/Math.log10(2.0));  
        double r2 = Math.log10(h)/Math.log10(2.0) - (int)(Math.log10(w)/Math.log10(2.0));         
        if(r1 != 0.0 || r2 != 0.0) {  
            System.err.println("输入的参数w或h不是2的n次幂！");  
            return null;  
        }  
//        int r = 0;  
//        r = (int)(Math.log10(w)/Math.log10(2));  
        //进行行傅里叶变换  
        for(int i=0; i<h; i++) {  
            matrix[i] = fft(matrix[i]);   
        }  
        //进行列傅里叶变换  
//        int n = h;  
//        r = (int)(Math.log10(n)/Math.log10(2)); //求迭代次数r  
        Complex tempCom[] = new Complex[h];  
        for(int j=0; j<w; j++) {  
            for(int i=0; i<h; i++) {  
                tempCom[i] = matrix[i][j];  
            }  
            tempCom = fft(tempCom);   
            for(int i=0; i<h; i++) {  
                matrix[i][j] = tempCom[i];  
            }  
        }         
        return matrix;  
    }
	/** 
	 * 求加权系数 
	 * 1.将数k写成r位的二进制数;2.将该二进制数向右移r-l位;3.将r位的二进制数比特倒转;4.求出倒置后的二进制数代表的十进制数; 
	 * @param k 要倒转的十进制数 
	 * @param l 下标值 
	 * @param r 二进制的位数 
	 * @return 加权系数 
	 */  
	private int getWeight(int k, int l, int r) {  
	    int d = r-l;    //位移量  
	    k = k>>d;       
	    return reverseRatio(k, r);  
	} 
	/** 
	 * 将数进行二进制倒转， 如0101倒转至1010 
	 * 1.将数k写成r位的二进制数;2.将r位的二进制数比特倒转;3.求出倒置后的二进制数代表的十进制数; 
	 * @param k 要倒转的十进制数 
	 * @param r 二进制的位数 
	 * @return 倒转后的十进制数 
	 */  
	private int reverseRatio(int k, int r) {  
	    int n = 0;  
	    StringBuilder sb = new StringBuilder(Integer.toBinaryString(k));  
	    StringBuilder sb2 = new StringBuilder("");  
	    if(sb.length()<r) {  
	        n = r-sb.length();  
	        for(int i=0; i<n; i++) {  
	            sb.insert(0, "0");  
	        }  
	    }  
	      
	    for(int i=0; i<sb.length(); i++) {  
	        sb2.append(sb.charAt(sb.length()-i-1));  
	    }         
	    return Integer.parseInt(sb2.toString(), 2);  
	}

}
