package com.dragonflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.widget.ImageView;
/**
 * ImageAdapter��ImageView��ʵ����
 * @author Administrator
 *
 */
public class ImageViewImp extends ImageView
{
	private int alpha = 250;
	private boolean pressed = false;
	public ImageViewImp(Context context)
	{
		super(context);
	}
	
	public void show()
	{
		new Thread(){
			public void run() {
				int time = 2000;
				try
				{
					pressed = true;
					while(time>0)
					{
						Thread.sleep(200);
						time -= 200;
						alpha-= 25;
						
						postInvalidate();
					}
					pressed = false;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
		if(event.getAction() == MotionEvent.ACTION_DOWN)
			show();

		return false;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(10);
		BitmapDrawable bd = (BitmapDrawable) getDrawable();

		if(bd!=null)
		{
			canvas.drawBitmap(imageScale(bd.getBitmap(), 107, 113), 21,18, p);	
		}
		//canvas.drawBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.kua), 0, 0, p);
		if(isPressed())
		{
			canvas.drawRect(5,5,140,140,p);
		}
		if(pressed)
		{
			p.setAlpha(alpha);
			canvas.drawRect(5,5,140,140,p);
		}
	}
	
	public static Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h){  
		  int  src_w = bitmap.getWidth();
		  int  src_h = bitmap.getHeight();
		  float scale_w = ((float)dst_w)/src_w;
		  float  scale_h = ((float)dst_h)/src_h;
		  Matrix  matrix = new Matrix();
		  matrix.postScale(scale_w, scale_h);
		  Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix, true);	  
		  return dstbmp;
	}

}
