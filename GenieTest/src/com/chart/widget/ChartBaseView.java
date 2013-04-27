package com.chart.widget;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.dragonflow.GenieDebug;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class ChartBaseView extends View {
	
	protected static  float TopSpacing = 20;
	
	protected static  float bottomSpacing = 20;
	
	protected static  float RightSpacing = 20;
	
	protected static  float LiftSpacing = 10;
	
	protected static  float PaintBetween = 8;
	
	protected static  float columnarHeight = 10;
	
	protected static  float Spacing_X = 40;
	
	protected static  float Spacing_Y = 20;
	
	
	protected   List<XAxisLable> XAxisLables = new ArrayList<XAxisLable>();
	
	protected  List<Integer> YAxisLable = new ArrayList<Integer>();
	
	protected static float chart_Origin_X=0,chart_Origin_Y=0,chart_Traction_X=0;
	
	protected  float[] downtop  = null;
	
	protected  float ratio;
	
	protected  float increaseThread;
	
	private static final  float fixedWidth1 = 400;
	
	private static final  float fixedWidth2 = 800;
	
	private int screenWidth,screenHeight;
	
	
	
	private static boolean getDensity = true;
	private float histogramHeight;
	private int subsection = 0;
	private int textSize;
	
	private String YAxisText=null;
	private double increase ;
	
	
	
	
	protected   Paint paint = null;
	protected  Paint dataPaint = null;
	private Paint YAxisTextPaint = null;
	private Paint textNamePaint = null;
	
	
	
	private String CoordinateAxisName=null;

	public ChartBaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ChartBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ChartBaseView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	private  float getDisplayMetrics(Context cx) {    
		DisplayMetrics dm = new DisplayMetrics();    

		dm = cx.getApplicationContext().getResources().getDisplayMetrics();   

		 screenWidth = dm.widthPixels;   
		 
		 screenHeight = dm.heightPixels;

		 float density = dm.density;

		 float xdpi = dm.xdpi;   

		 float ydpi = dm.ydpi;         

		// ViewDebug.debug("getDisplayMetrics", "screenWidth: "+screenWidth+" screenHeight: "+screenHeight+" density: "+density+" xdpi: "+xdpi+" ydpi: "+ydpi);

		 return density; 

		}
	
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measureWidth =  measureWidth(widthMeasureSpec);
		screenWidth = measureWidth;
		screenHeight = measureHeight(heightMeasureSpec);
		setMeasuredDimension(screenWidth,screenHeight);
	}
	
	private int measureWidth(int widthMeasureSpec){
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		if(specMode == MeasureSpec.UNSPECIFIED){
			return screenWidth;
		}else{
			return specSize;
		}
	}
	
	private int measureHeight(int widthMeasureSpec){
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		if(specMode == MeasureSpec.UNSPECIFIED){
			return screenHeight;
		}else{
			return specSize;
		}
	}
	
	private  void setDensitySpacing(float density){
		if(getDensity){
			TopSpacing = (int) (TopSpacing*density);
			bottomSpacing = (int) (bottomSpacing*density);
			RightSpacing = (int) (RightSpacing*density);
			LiftSpacing = (int) (LiftSpacing*density);
			PaintBetween = (int) (PaintBetween*density);
			columnarHeight = (int) (columnarHeight*density);
			getDensity = false;
		}
		
		
	}
	
	private void init(Context context){
		
		
		float density = getDisplayMetrics(context);
		setDensitySpacing(density);
		
		paint = new Paint();
		paint.setTextScaleX(1);
		paint.setTextAlign(Align.LEFT);
		
		
		YAxisTextPaint = new Paint();
		YAxisTextPaint.setAntiAlias(true);
		YAxisTextPaint.setColor(Color.BLACK);
		
		textNamePaint = new Paint();
		textNamePaint.setAntiAlias(true);
		textNamePaint.setColor(Color.BLACK);
		textNamePaint.setTextAlign(Align.CENTER);
		
		dataPaint = new Paint();
		dataPaint.setAntiAlias(true);
		dataPaint.setTextAlign(Align.CENTER);
	}
	
	private void initPaintAttribute(){
		
		if(screenWidth<fixedWidth1){
			textSize = 8;
		}else if(screenWidth<fixedWidth2){
			textSize = 12;
		}else{
			textSize = 14;
		}
		//ViewDebug.debug("initPaint", "textSize:"+textSize);
		paint.setTextSize(textSize);
		
		YAxisTextPaint.setTextSize(textSize);
		
		dataPaint.setTextSize(textSize);
	}
	
	private  void  setYSpacing(){
		if(subsection != 0){
			histogramHeight = screenHeight-(TopSpacing*3)-bottomSpacing*2-columnarHeight;
			Spacing_Y = histogramHeight/subsection;
			if(Spacing_Y<=0){
				Spacing_Y = 1;
				increaseThread = Spacing_Y/3;
			}else{
				increaseThread = Spacing_Y/3;
			}
			
			
			ratio = (float)increase/Spacing_Y;
			
			//ratio = new Float(new DecimalFormat("0.00000000").format(increase/Spacing_Y)).floatValue();
			//ViewDebug.debug("setYSpacing",""+Spacing_X);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		initPaintAttribute();
		setYSpacing();
		drawCoordinateAxis(YAxisLable, XAxisLables, screenWidth, canvas);
		
	}
	
	

	private void drawCoordinateAxis(List<Integer> date_Y ,List<XAxisLable> date_X,int MeasureSpec_W,Canvas canvas){
		int count_Y = date_Y.size();
		int count_X = date_X.size();
		chart_Origin_X = (LiftSpacing*4)+PaintBetween*2;//+PaintBetween+PaintBetween
		
		if(count_X <= 0)
		{
			Spacing_X = 0;
		}else{
			Spacing_X = ((MeasureSpec_W-chart_Origin_X-RightSpacing*2)/count_X);
		}
		
		//same: histogramHeight
		chart_Origin_Y = screenHeight - bottomSpacing*2-columnarHeight; 
		//TopSpacing*2+((count_Y-1)*Spacing_Y);
		float XAxisLength = (count_X*Spacing_X)+RightSpacing;
		chart_Traction_X = MeasureSpec_W-RightSpacing;
		
		GenieDebug.error("drawCoordinateAxis   =>", chart_Origin_X+":"+Spacing_X+":"+chart_Origin_Y+":"+chart_Traction_X+":"+count_X);
		
		drawYAxisText(PaintBetween*2, chart_Origin_Y-chart_Origin_Y/3, canvas);
		
		drawCoordinateAxisName(CoordinateAxisName, screenWidth/2, TopSpacing, canvas);
		
		drawYAxis(chart_Origin_X, TopSpacing*2, chart_Origin_X, chart_Origin_Y, canvas);
		
		drawXAxis(chart_Origin_X, chart_Origin_Y, chart_Traction_X, chart_Origin_Y,canvas);
		
		drawYAxisLable(count_Y, chart_Origin_X,chart_Traction_X, chart_Origin_Y, date_Y, canvas);
		
		drawXAxisLable(count_X, chart_Origin_X, chart_Origin_Y, date_X, canvas);
	}
	
	
	
	private void drawYAxisText(float startX,float startY,Canvas canvas){
		if(YAxisTextPaint != null&&YAxisText!=null){
			Path path = new Path();
			path.moveTo(startX, startY);
			path.lineTo(startX, 0);
			canvas.drawTextOnPath(YAxisText, path, 0, 0, YAxisTextPaint);
		}
	}
	
	private void drawCoordinateAxisName(String CoordinateAxisName,float XAxis, float YAxis,Canvas canvas ){
		if(textNamePaint!= null&&CoordinateAxisName != null){
			
			canvas.save();
			canvas.drawText(CoordinateAxisName, XAxis, YAxis, textNamePaint);
			canvas.restore();
		}
	}
	
	
	
	private void drawXAxis(float startX, float startY, float stopX, float stopY,Canvas canvas){
		if(paint!= null){
			setPaint(Color.BLACK);
			canvas.save();
			canvas.drawLine(startX,startY,stopX,stopY, paint);
			canvas.restore();
		}
	}
	
	
	private void drawYAxis(float startX, float startY, float stopX, float stopY,Canvas canvas){
		if(paint!= null){
			setPaint(Color.BLACK);
			canvas.save();
			canvas.drawLine(startX,startY,stopX,stopY, paint);
			
			canvas.restore();
		}
	}
	
	private void drawYAxisLable(int count_Y,float chart_Origin_X,float chart_Traction_X,float chart_Origin_Y,List<Integer> date_Y,Canvas canvas){
		if(paint!= null){
			Paint paint1 = new Paint();
			for (int i = 0; i < count_Y; i++) {
				canvas.save();
				if((i+1)<count_Y){
					canvas.drawLine(chart_Origin_X, chart_Origin_Y-(Spacing_Y*(i+1)), chart_Origin_X-PaintBetween, chart_Origin_Y-(Spacing_Y*(i+1)), paint);
					
					paint1.setColor(Color.GRAY);
    				
    				PathEffect mEffects =  new DashPathEffect(new float[] {5, 5, 5, 5}, PaintBetween); 
    				paint1.setPathEffect(mEffects); 
    				paint1.setAntiAlias(true);
    				canvas.drawLine(chart_Origin_X, chart_Origin_Y-(Spacing_Y*(i+1)), chart_Traction_X, chart_Origin_Y-(Spacing_Y*(i+1)), paint1);
				}
				canvas.drawText(date_Y.get(i).toString(), chart_Origin_X-(LiftSpacing*3), chart_Origin_Y-(Spacing_Y*i), paint);
				canvas.restore();
			}
		}
	}
	
	private void drawXAxisLable(int count_X,float chart_Origin_X,float chart_Origin_Y,List<XAxisLable> date_X,Canvas canvas){
		if(paint!= null && dataPaint!= null){
			Rect bounds = new Rect();
			for (int i = 0; i < count_X; i++) {
				float interval = ((Spacing_X*(i+1))-(Spacing_X*i))/2;
				
				canvas.save();
				
//				canvas.drawLine(chart_Origin_X+(Spacing_X*(i+1)), chart_Origin_Y, chart_Origin_X+(Spacing_X*(i+1)), chart_Origin_Y-PaintBetween, paint);
				
				dataPaint.setColor(Color.BLACK);
				String name = ((XAxisLable)date_X.get(i)).getTrafficName();
				
				dataPaint.getTextBounds(name, 0, name.length(), bounds);
				float textlengh = dataPaint.measureText(name);
				if(textlengh>(interval+(interval/3))){
					boolean mat = name.matches("^(\\s|.*\\s+.*)$");   
			        if(mat){   
			            int index = name.indexOf(" ");
			            String str1 = name.substring(0,index);
			            String str2 = name.substring(index+1,name.length());
						
			        	canvas.drawText(str1, chart_Origin_X+(Spacing_X*(i+1))-interval/2, chart_Origin_Y+bounds.height()+3, dataPaint);
			        	canvas.drawText(str2, chart_Origin_X+(Spacing_X*(i+1))-interval/2, chart_Origin_Y+bounds.height()+4+bounds.height(), dataPaint);
			        }else{
						
			        	canvas.drawText(name, chart_Origin_X+(Spacing_X*(i+1))-interval/2, chart_Origin_Y+bounds.height()+3, dataPaint);
			        }
				}else{
					canvas.drawText(name, chart_Origin_X+(Spacing_X*(i+1))-interval/2, chart_Origin_Y+bounds.height()+3, dataPaint);
				}
				
				canvas.restore();
			}
		}
		
	}
	
	//Draw parallelogram
	private Path getPath(float startX,float startY,float[] x, float[] y) {
        if (x == null || y == null) {
            return null;
        }
        Path path = new Path();
        path.moveTo(startX, startY);
        for (int i = 0; i < x.length; i++) {
            path.lineTo(x[i], y[i]);
        }
        path.close();
        return path;
    } 
	
	public void setXAxisLable(List<XAxisLable> list){
		if(XAxisLables != null){
			this.XAxisLables.clear();
			if(null != list && !list.isEmpty()){
				this.XAxisLables = list;
				if(downtop != null){
					downtop = null;
					downtop = new float[list.size()];
				}else{
					downtop = new float[list.size()];
				}
				
			}
		}
		upDataMainUI();
	}
	
	public void setYAxisLable(float total,int subsection,String YAxisText){
		if(YAxisLable != null){
			if(!YAxisLable.isEmpty()){
				this.YAxisLable.clear();
			}
//			if(subsection>total){
//				throw new RuntimeException("subsection must not > total");
//			}
			if(subsection<0||total<0){
				return;
			}
			this.YAxisText = YAxisText;
			this.subsection = subsection;
			
			int maxvalue;
			
			if(total < 10)
			{
				maxvalue = 10;
			}else if(total < 50)
			{
				maxvalue = 50;
			}else if(total < 100)
			{
				maxvalue = 100;
			}else
			{
				int temp1 ;
				int temp2 ;
				
				int temp3 = (int)total;
				int temp4;
				int n = 0;
				boolean flag = false;
				while(true)
				{
					temp4 = temp3%10;
					if(temp4 > 0)
					{
						flag = true;
					}
					temp3 = temp3/10;					
					n++;
					if(temp3 >= 10 && temp3 <100)
					{
						//temp1 = temp3;
						break;
					}
				}
				//temp1 = temp3/5;
				//temp2 = temp3%5;
				
				
				
				//if(temp2>0)
				//{
				//	maxvalue = (temp1+1)*5*(int)(Math.pow(10, n));
				//}else
				//{
					if(flag)
					{
						maxvalue = (temp3+1)*(int)(Math.pow(10, n));
					}else
					{
						maxvalue = temp3*((int)(Math.pow(10, n)));
					}
				//}
				
				GenieDebug.error("debug","setYAxisLable total= "+total);
				GenieDebug.error("debug","setYAxisLable n= "+n);
				GenieDebug.error("debug","setYAxisLable temp4= "+temp4);
				GenieDebug.error("debug","setYAxisLable temp3= "+temp3);
				//GenieDebug.error("debug","setYAxisLable temp1= "+temp1);
				//GenieDebug.error("debug","setYAxisLable temp2= "+temp2);
				GenieDebug.error("debug","setYAxisLable maxvalue= "+maxvalue);
			}
			
			
			increase  = (int) (maxvalue/subsection);
			for (int i = 0; i <= subsection; i++) {
				YAxisLable.add((int) (i*increase));
				//ViewDebug.debug("setYAxisLable", i*increase+"");
			}
		}
		upDataMainUI();
	}
	
	public void setTitleText(String textName){
		if(textName != null){
			this.CoordinateAxisName = textName;
			upDataMainUI();
		}
		
	}
	
	public void setTitleNamePaintColor(int color){
		if(textNamePaint != null){
			textNamePaint.setAntiAlias(true);
			textNamePaint.setColor(color);
		}
		upDataMainUI();
	}
	
	public void setTitleNamePaintARGB(int a, int r, int g, int b){
		if(textNamePaint != null){
			textNamePaint.setAntiAlias(true);
			textNamePaint.setARGB(a, r, g, b);
		}
		upDataMainUI();
	}
	
	public void setTitleNameSize(float textsize){
		if(textNamePaint != null){
			textNamePaint.setTextSize(textsize);
			textNamePaint.setTypeface(Typeface.DEFAULT_BOLD);
		}
		upDataMainUI();
	}
	
	protected void setPaint(int color){
		if(paint!= null){
			paint.setAntiAlias(true);
			paint.setColor(color);
		}
	}
	
	public void upDataMainUI(){
		requestLayout();
		invalidate();
	}
}
