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
		
		GenieDebug.error("debug", "������Ϣ:"+str);
		
		try {
			str=new String(str.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		GenieDebug.error("debug", "ת��������Ϣ:"+str);
	    // ���ɶ�ά����,����ʱָ����С,��Ҫ������ͼƬ�Ժ��ٽ�������,������ģ������ʶ��ʧ��
	    BitMatrix matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, 200, 200);
	    int width = matrix.getWidth();
	    int height = matrix.getHeight();
	    // ��ά����תΪһά��������,Ҳ����һֱ��������
	    int[] pixels = new int[width * height];
	    for (int y = 0; y < height; y++) {
	        for (int x = 0; x < width; x++) {
	            if(matrix.get(x, y)){
	                pixels[y * width + x] = 0xff000000;
	            }
	        }
	    }
	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    // ͨ��������������bitmap,����ο�api
	    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	    return bitmap;
	}
}
