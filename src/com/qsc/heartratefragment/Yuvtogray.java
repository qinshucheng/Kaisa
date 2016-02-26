package com.qsc.heartratefragment;

public class Yuvtogray{
	
	public static int[] yvalue(byte[] data, int w, int h) {
		 int inputOffset = 0;
		 int[] pixels = new int[w*h];
	        for (int y = 0; y < h; y++) {
	            int outputOffset = y * w;
	            for (int x = 0; x < w; x++) {
	                int grey = data[inputOffset + x] & 0xff;
	                pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
	            }
	            inputOffset += w;
	        }
		return pixels;
		
	}
}