package com.qsc.heartratefragment;

public class ImageProcessing_c {

	public static int decodeYUV420SPtoY(byte[] yuv420sp, int width, int height) {

		if (yuv420sp == null) return 0;
		
		int yuv_y = 0;
	    for (int j = 0, yp = 0; j < height; j++) {
	        for (int i = 0; i < width; i++, yp++) {
	            int y = (0xff & ((int) yuv420sp[yp])) - 16;
	            if (y < 0) y = 0;
	            yuv_y += y;
	        }
	    }return yuv_y;
    }

}
