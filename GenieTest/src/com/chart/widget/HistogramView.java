package com.chart.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

public class HistogramView extends ChartBaseView {

	private Paint downColumnarPaint = null;
	private Paint uPColumnarPaint = null;

	private String downloadColorNote;
	private String uploadColorNote;
	
	private Thread thread = null;
	private boolean display = false;
	private boolean refresh = false;

	public HistogramView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public HistogramView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public HistogramView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {

		downColumnarPaint = new Paint();
		downColumnarPaint.setAntiAlias(true);
		downColumnarPaint.setARGB(255, 154, 153, 255);

		uPColumnarPaint = new Paint();
		uPColumnarPaint.setAntiAlias(true);
		uPColumnarPaint.setColor(Color.GRAY);
		startThread();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (chart_Traction_X != 0 && chart_Origin_Y != 0) {
			drawColumnarColorNote(chart_Traction_X, chart_Origin_Y, canvas);
			if (chart_Origin_X != 0 && XAxisLables != null
					&& !XAxisLables.isEmpty()) {
				drawPillar(XAxisLables, chart_Origin_X, chart_Origin_Y, canvas);
			}
		}

	}

	private void drawRect(int i,float chart_Origin_X,float interval,float chart_Origin_Y,List<XAxisLable> date_X,Canvas canvas){
		if(paint!= null && dataPaint!= null){
			paint.setAntiAlias(true);
			setPaint(Color.BLACK);
			float left = chart_Origin_X+(Spacing_X*(i+1))-interval;
			float stringLeft = chart_Origin_X+(Spacing_X*(i+1))-interval/2;
			
//			GenieDebug.error("drawRect000000000", "increase��"+increase+" Spacing��"+Spacing+" ratio��"+ratio);
			float Down_top = (float) ((((XAxisLable)date_X.get(i)).getDownLoad())/ratio);
			float UP_top =(float) ((((XAxisLable)date_X.get(i)).getUpLoad())/ratio);
			float right = chart_Origin_X+(Spacing_X*(i+1));
			
			float left1 = left+interval/3;
			
//			float right1 = chart_Origin_X+(Spacing_X*(i+1))+interval/3;
//			
//			float stringLeft1 = left1+interval/2;
			
			canvas.save();
			float totalTop = Down_top+UP_top;
			if(downtop != null){ 
				
			
				downtop[i] +=increaseThread;
//				float top1 = chart_Origin_Y-downtop[i]-columnarHeight;
//				float top2 = chart_Origin_Y-downtop[i]-columnarHeight;
//				float top3 = chart_Origin_Y-columnarHeight;
//				
//				float top11 = chart_Origin_Y-totalTop-columnarHeight;
//				float top22 = chart_Origin_Y-Down_top-columnarHeight;
				
				float top = chart_Origin_Y-downtop[i];
				boolean flag_spac = false;
					
					//download
					if(downtop[i] <= Down_top){
						canvas.drawRect(left, top, right, chart_Origin_Y, downColumnarPaint);
					}else{
						canvas.drawRect(left, chart_Origin_Y-Down_top, right, chart_Origin_Y, downColumnarPaint);
//						dataPaint.setColor(Color.WHITE);
						dataPaint.setColor(Color.BLACK);
						
						java.text.DecimalFormat df=new java.text.DecimalFormat("0.##");

				
						String str_downLoad = df.format(((XAxisLable)date_X.get(i)).getDownLoad());
						
						//String str_downLoad = ((XAxisLable)date_X.get(i)).getDownLoad()+"";
						float str_downLoad_length = dataPaint.measureText(str_downLoad);
						if(str_downLoad_length>(interval+(interval/3))){
							flag_spac = true;
							int length = str_downLoad.length();
							int l = length/2;
							String str;
							str = str_downLoad.subSequence(0, l).toString();
							Rect bounds = new Rect();
							dataPaint.getTextBounds(str, 0, str.length(), bounds);
							canvas.drawText(str, stringLeft, chart_Origin_Y-(Down_top/2)-bounds.height(), dataPaint); //����
							
							str =str_downLoad.subSequence(l, length).toString();
							dataPaint.getTextBounds(str, 0, str.length(), bounds);
							canvas.drawText(str, stringLeft, chart_Origin_Y-(Down_top/2), dataPaint); //����
						}else{
							canvas.drawText(str_downLoad, stringLeft, chart_Origin_Y-(Down_top/2), dataPaint); //����
							flag_spac = false;
						}
						
					}
					
					//upload
					
					if(downtop[i] >= Down_top &&downtop[i] < totalTop){
						canvas.drawRect(left, top, right, chart_Origin_Y-Down_top, uPColumnarPaint);
					}
					if(downtop[i] >= totalTop){
						canvas.drawRect(left, chart_Origin_Y-Down_top-UP_top, right, chart_Origin_Y-Down_top, uPColumnarPaint);
						
						dataPaint.setColor(Color.BLACK);
						
						java.text.DecimalFormat df=new java.text.DecimalFormat("0.##");

						
						String str_upLoad = df.format(((XAxisLable)date_X.get(i)).getUpLoad());
						
						
						//String str_upLoad = ((XAxisLable)date_X.get(i)).getUpLoad()+"";
						float str_upLoad_length = dataPaint.measureText(str_upLoad);
						if(str_upLoad_length>(interval+(interval/3))){
							int length = str_upLoad.length();
							int l = length/2;
							String str;
							str = str_upLoad.subSequence(0, l).toString();
							Rect bounds = new Rect();
							if(flag_spac&&(Down_top+UP_top)<Spacing_Y){
								dataPaint.getTextBounds(str, 0, str.length(), bounds);
								canvas.drawText(str, stringLeft, chart_Origin_Y-Down_top-UP_top-PaintBetween-bounds.height()*2, dataPaint); //����
								
								str =str_upLoad.subSequence(l, length).toString();
								dataPaint.getTextBounds(str, 0, str.length(), bounds);
								canvas.drawText(str, stringLeft, chart_Origin_Y-Down_top-UP_top-PaintBetween-bounds.height(), dataPaint); //����
							}else{
								dataPaint.getTextBounds(str, 0, str.length(), bounds);
								canvas.drawText(str, stringLeft, chart_Origin_Y-Down_top-UP_top-PaintBetween-bounds.height(), dataPaint); //����
								
								str =str_upLoad.subSequence(l, length).toString();
								dataPaint.getTextBounds(str, 0, str.length(), bounds);
								canvas.drawText(str, stringLeft, chart_Origin_Y-Down_top-UP_top-PaintBetween, dataPaint); //����
							}
							
						}else{
							if(flag_spac&&(Down_top+UP_top)<Spacing_Y){
								Rect bounds = new Rect();
								dataPaint.getTextBounds(str_upLoad, 0, str_upLoad.length(), bounds);
								canvas.drawText(str_upLoad, stringLeft, chart_Origin_Y-Down_top-UP_top-PaintBetween-bounds.height()*2, dataPaint); //����
							}else if((Down_top+UP_top)<Spacing_Y){
								Rect bounds = new Rect();
								dataPaint.getTextBounds(str_upLoad, 0, str_upLoad.length(), bounds);
								canvas.drawText(str_upLoad, stringLeft, chart_Origin_Y-Down_top-UP_top-PaintBetween-bounds.height(), dataPaint); //����
							}else{
								canvas.drawText(str_upLoad, stringLeft, chart_Origin_Y-Down_top-UP_top-PaintBetween, dataPaint); //����
							}
							
							
							
						}
						
						java.text.DecimalFormat df2=new java.text.DecimalFormat("0.##");

						
						String str_downLoad = df2.format(((XAxisLable)date_X.get(i)).getDownLoad());
			
						
						//draw download str
//						String str_downLoad = ((XAxisLable)date_X.get(i)).getDownLoad()+"";
						float str_downLoad_length = dataPaint.measureText(str_downLoad);
						if(str_downLoad_length>(interval+(interval/3))){
							flag_spac = true;
							int length = str_downLoad.length();
							int l = length/2;
							String str;
							str = str_downLoad.subSequence(0, l).toString();
							Rect bounds = new Rect();
							dataPaint.getTextBounds(str, 0, str.length(), bounds);
							canvas.drawText(str, stringLeft, chart_Origin_Y-(Down_top/2)-bounds.height(), dataPaint); //����
							
							str =str_downLoad.subSequence(l, length).toString();
							dataPaint.getTextBounds(str, 0, str.length(), bounds);
							canvas.drawText(str, stringLeft, chart_Origin_Y-(Down_top/2), dataPaint); //����
						}else{
							canvas.drawText(str_downLoad, stringLeft, chart_Origin_Y-(Down_top/2), dataPaint); //����
							flag_spac = false;
						}
//						canvas.drawText(str_upLoad, stringLeft, chart_Origin_Y-Down_top-UP_top-PaintBetween, dataPaint); //�ϴ�
						canvas.restore();
					}
					if(downtop[i]>chart_Origin_Y){
						display = false;
						
					}
			}
		}
	}

	private void drawPillar(List<XAxisLable> date_X, float chart_Origin_X,float chart_Origin_Y, Canvas canvas) {
		int count_X = date_X.size();
		for (int i = 0; i < count_X; i++) {
			if(refresh){
				if((i+1)==count_X){
					refresh = false;
				}
			}else{
				drawRect(i, chart_Origin_X, Spacing_X / 2, chart_Origin_Y, date_X,canvas);
			}
			
		}

	}

	private void drawColumnarColorNote(float chart_Traction_X,
			float chart_Origin_Y, Canvas canvas) {
		// downloadColorNote uploadColorNote "Upload" "Download"
		if (paint != null && downColumnarPaint != null
				&& uPColumnarPaint != null) {
			canvas.save();
			float downtextLengh = paint.measureText(downloadColorNote);
			float uptextLengh = paint.measureText(uploadColorNote);

			float notePlaceX = chart_Traction_X - PaintBetween * 4
					- downtextLengh - uptextLengh - RightSpacing;
			float notePlaceY = chart_Origin_Y + bottomSpacing +bottomSpacing/2;
			canvas.drawRect(notePlaceX, notePlaceY, notePlaceX + PaintBetween,notePlaceY + PaintBetween, downColumnarPaint);
			canvas.drawText(downloadColorNote, notePlaceX + PaintBetween * 2,notePlaceY + PaintBetween, paint);

			float textspeace = notePlaceX + PaintBetween * 2 + downtextLengh + PaintBetween * 3;

			canvas.drawRect(textspeace, notePlaceY, textspeace + PaintBetween,notePlaceY + PaintBetween, uPColumnarPaint);
			canvas.drawText(uploadColorNote, textspeace + PaintBetween * 2,notePlaceY + PaintBetween, paint);

			canvas.restore();
		}
	}

	public void setColorNoteText(String downloadColorNote,
			String uploadColorNote) {
		if (downloadColorNote != null && uploadColorNote != null) {
			this.downloadColorNote = downloadColorNote;
			this.uploadColorNote = uploadColorNote;
			this.upDataMainUI();
		}

	}
	
	public void setDownLoadColumnarPaintColor(int color){
		if(downColumnarPaint != null){
			downColumnarPaint.setAntiAlias(true);
			downColumnarPaint.setColor(color);
		}
		upDataMainUI();
	}
	
	public void setDownLoadColumnarPaintARGB(int a, int r, int g, int b){
		if(downColumnarPaint != null){
			downColumnarPaint.setAntiAlias(true);
			downColumnarPaint.setARGB(a, r, g, b);
		}
		upDataMainUI();
	}
	
	public void setUPLoadColumnarPaintColor(int color){
		if(uPColumnarPaint != null){
			uPColumnarPaint.setAntiAlias(true);
			uPColumnarPaint.setColor(color);
		}
		upDataMainUI();
	}
	
	public void setUPLoadColumnarPaintARGB(int a, int r, int g, int b){
		if(uPColumnarPaint != null){
			uPColumnarPaint.setAntiAlias(true);
			uPColumnarPaint.setARGB(a, r, g, b);
		}
		upDataMainUI();
	}

	
	private void startThread(){
		stopThread();
		if(thread == null){
			display = true;
			if(downtop != null){
				int size  = downtop.length;
				for (int i = 0; i < size; i++) {
					downtop[i]=0;
				}
			}
			thread = new Thread(){
				@Override
				public void run(){
					while(!Thread.currentThread().isInterrupted() && display )
					{
						postInvalidate();
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							this.interrupt();
						}
						
					}
				}
			};
			
			thread.start();
		}
	}
	
	private void stopThread(){
		if(thread != null&&!Thread.currentThread().isInterrupted() && display){
			display = false;
//			thread.stop();
			thread.interrupt();
			
		}
		
		thread = null;
	}
	
	public void refreshHistogram(){
		startThread();
		refresh = true;
	}
	
}
