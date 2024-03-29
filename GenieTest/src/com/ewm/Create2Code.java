package com.ewm;

import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;

import com.dragonflow.GenieDebug;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class Create2Code {
	
	
	public static Bitmap Create2DCode(String str) throws WriterException {
		
		GenieDebug.error("debug", "无线信息:"+str);
		
		try {
			str=new String(str.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		GenieDebug.error("debug", "转码无线信息:"+str);
	    // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
	    BitMatrix matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, 200, 200);
	    int width = matrix.getWidth();
	    int height = matrix.getHeight();
	    // 二维矩阵转为一维像素数组,也就是一直横着排了
	    int[] pixels = new int[width * height];
	    for (int y = 0; y < height; y++) {
	        for (int x = 0; x < width; x++) {
	            if(matrix.get(x, y)){
	                pixels[y * width + x] = 0xff000000;
	            }
	        }
	    }
	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    // 通过像素数组生成bitmap,具体参考api
	    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	    return bitmap;
	}
}
