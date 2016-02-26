package com.qsc.heartratefragment;


public class Takey {
	
	public static int[][] yvalue(byte[] yuv420sp, int width, int height){
        int[][] yy = new int[height][width];
        for (int j = 0, yp = 0; j < height; j++) {
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                yy[j][i] = y;
            }
        }
		return yy;
		
	}
}