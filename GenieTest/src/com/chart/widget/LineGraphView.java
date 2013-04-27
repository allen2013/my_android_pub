package com.chart.widget;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class LineGraphView extends View {
	private static final float interval = 20.0F;
	private String BSSID;
	private int Max;
	private int Min;
	private float PaintBetween = 3.0F;
	private float Spacing = 8.0F;
	private float StartSpacing = 10.0F;
	private Paint XAxisLablePaint = null;
	private Paint XAxisPaint = null;
	private float Xinterval;
	private Paint YAxisLablePaint = null;
	private Paint YAxisPaint = null;
	private float YAxisSpacing;
	private List<Integer> YLables = null;
	private String YTitle = null;
	private Paint YTitlePaint = null;
	private DisplayMetrics dm = null;
	private List<FoldLineHelp> foldLineHelps = null;
	private Paint foldLinePaint = null;
	private float increaseSpeed;
	private int measureHeight;
	private int measureWidth;
	private boolean onOFF = true;
	private float ratio;
	private float screenDensity;
	private int screenHeight;
	private int screenWidth;
	private float userWidth;
	private int yMax;

	public LineGraphView(Context context) {
		super(context);
		init(context);
	}

	public LineGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LineGraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	
	private void drawFoldLine(List<FoldLineHelp> paramList, Canvas paramCanvas){
	    if ((paramList != null) && (this.foldLinePaint != null)){
		    FoldLineHelp localFoldLineHelp;
		    for (int i = 0; i<paramList.size(); ++i)
		    {
		      paramCanvas.save();
		      localFoldLineHelp = (FoldLineHelp)paramList.get(i);
		      if(localFoldLineHelp.SSID!=null && localFoldLineHelp.BSSID!=null){
			      this.foldLinePaint.setColor(localFoldLineHelp.color);
			      if (!localFoldLineHelp.drawflag){
			        continue;
			      }
			      drawSpecialFoldLine(localFoldLineHelp, this.BSSID);
			      if (this.onOFF){
			    	  paramCanvas.drawPath(makePathDash(localFoldLineHelp.pts, this.userWidth), this.foldLinePaint);
			      }else{
			    	  paramCanvas.drawPath(makePathDashFixed(localFoldLineHelp.pts, this.userWidth), this.foldLinePaint);
			    	  
			      }
		      }
		      paramCanvas.restore();
		    }
	    }
	}
	
	private void drawSpecialFoldLine(FoldLineHelp paramFoldLineHelp, String paramString){
	    if ((paramFoldLineHelp != null) && (paramFoldLineHelp.BSSID != null) && (paramFoldLineHelp.BSSID.equals(paramString)) && (this.foldLinePaint != null)){
	    	this.foldLinePaint.setStrokeWidth(6.0F);
	    }else{
	    	this.foldLinePaint.setStrokeWidth(3.0F);
	    }
	}
	
	private void drawXAxis(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Canvas paramCanvas){
	    if (this.XAxisPaint != null){
		    paramCanvas.save();
		    paramCanvas.drawLine(paramFloat1, paramFloat2, paramFloat3, paramFloat4, this.XAxisPaint);
		    paramCanvas.restore();
	    }
	}

	private void drawYAxis(float paramFloat1, float paramFloat2,float paramFloat3, float paramFloat4, Canvas paramCanvas) {
		if (YAxisPaint != null) {
			paramCanvas.save();
			paramCanvas.drawLine(paramFloat1, paramFloat2, paramFloat3,
					paramFloat4, YAxisPaint);
			paramCanvas.restore();
			
		}
	}
	
	private void drawYAxisBoundary(List<Integer> paramList, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Canvas paramCanvas){
	    int i=0;
	    Paint localPaint;
	    if ((this.YAxisLablePaint != null) && (paramList != null) && (paramList.size() > 0))
	    {
	      i = paramList.size();
	      localPaint = new Paint();
	    }else{
	    	return;
	    }
	    for (int j = 0;j<i ; ++j)
	    {
	      paramCanvas.save();
	      if (j + 1 < i)
	      {
	        localPaint.setColor(-7829368);
	        localPaint.setPathEffect(new DashPathEffect(new float[] { 5.0F, 5.0F, 5.0F, 5.0F }, this.PaintBetween));
	        localPaint.setAntiAlias(true);
	        paramCanvas.drawLine(paramFloat1, paramFloat3 - paramFloat4 * (j + 1), paramFloat2, paramFloat3 - paramFloat4 * (j + 1), localPaint);
	      }
	      paramCanvas.restore();
	    }
	}
	
	private void drawYAxisLable(List<Integer> paramList, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Canvas paramCanvas){
	    int i = 0;
	    int j=0;
	    if ((this.YAxisLablePaint != null) && (paramList != null) && (paramList.size() > 0))
	    {
	      j = paramList.size();
	    }
	    for (int k = 0; ; ++k)
	    {
	      if (k >= j)
	      {
	        this.userWidth = (this.userWidth + i + this.PaintBetween);
	        return;
	      }
	      paramCanvas.save();
	      float f = this.YAxisLablePaint.measureText(((Integer)paramList.get(k)).toString());
	      paramCanvas.drawText(((Integer)paramList.get(k)).toString(), paramFloat1 + f, paramFloat3 - paramFloat4 * k, this.YAxisLablePaint);
	      if ((float)i < f){
	        i = (int)f;
	      }
	      paramCanvas.restore();
	    }
	}
	
	private void drawYAxisTitlt(String paramString, float paramFloat1, float paramFloat2, Canvas paramCanvas){
		if (YTitlePaint != null && paramString != null)
		{
			Rect rect = new Rect();
			YTitlePaint.getTextBounds(paramString, 0, paramString.length(), rect);
			paramCanvas.save();
			Path path = new Path();
			path.moveTo(paramFloat1, paramFloat2);
			path.lineTo(paramFloat1, Spacing);
			paramCanvas.drawTextOnPath(paramString, path, 0.0F, 0.0F, YTitlePaint);
			paramCanvas.restore();
			userWidth = StartSpacing + (float)rect.height() + PaintBetween;
		} else
		{
			userWidth = 0.0F;
		}
	}

	private void init(Context paramContext) {
		Spacing = 8.0F;
		PaintBetween = 3.0F;
		StartSpacing = 10.0F;
		initDisplayMetrics(paramContext);
		initPaint();
	}

	private void initDisplayMetrics(Context paramContext) {
		dm = paramContext.getApplicationContext().getResources()
				.getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		screenDensity = dm.density;
		Spacing *= screenDensity;
		PaintBetween *= screenDensity;
		StartSpacing *= screenDensity;
	}

	private void initFoldLinePaint() {
		foldLinePaint = new Paint(1);
		foldLinePaint.setAntiAlias(true);
		foldLinePaint.setStyle(Paint.Style.STROKE);
		foldLinePaint.setStrokeWidth(3.0F);
	}

	private void initPaint() {
		initYTitlePaint();
		initYAxisLablePaint();
		initXAxisLablePaint();
		initYAxisPaint();
		initXAxisPaint();
		initFoldLinePaint();
	}

	private void initXAxisLablePaint() {
		XAxisLablePaint = new Paint();
		XAxisLablePaint.setAntiAlias(true);
		XAxisLablePaint.setColor(Color.BLACK);
		XAxisLablePaint.setTextAlign(Paint.Align.CENTER);
	}

	private void initXAxisPaint() {
		XAxisPaint = new Paint();
		XAxisPaint.setAntiAlias(true);
		XAxisPaint.setColor(Color.BLACK);
		XAxisPaint.setTextAlign(Paint.Align.CENTER);
	}

	private void initYAxisLablePaint() {
		YAxisLablePaint = new Paint();
		YAxisLablePaint.setAntiAlias(true);
		YAxisLablePaint.setColor(Color.BLACK);
		YAxisLablePaint.setTextSize(16.0F);
		YAxisLablePaint.setTextAlign(Paint.Align.RIGHT);
	}

	
	private void initYAxisPaint() {
		YAxisPaint = new Paint();
		YAxisPaint.setAntiAlias(true);
		YAxisPaint.setColor(Color.BLACK);
		YAxisPaint.setTextAlign(Paint.Align.CENTER);
	}

	private void initYTitlePaint() {
		YTitlePaint = new Paint();
		YTitlePaint.setAntiAlias(true);
		YTitlePaint.setColor(Color.BLACK);
		YTitlePaint.setTextSize(20.0F);
		YTitlePaint.setTextAlign(Paint.Align.CENTER);
	}
	
	private Path makePathDash(List<Integer> paramList, float paramFloat){
	    Path localPath = null;
	    int i;
	    int j;
	    int i1;
	    int l;
	    if (paramList != null)
	    {
	      i1 = 0;
	      Object obj;
	      localPath = new Path();
	      i = paramList.size();
	      this.Xinterval = ((this.measureWidth - paramFloat) / 20.0F);
	      j = 1 + (int)((this.measureWidth - paramFloat) / this.Xinterval);
	      if (i <= j)
			{
				obj = paramList;
			} else
			{
				obj = new ArrayList();
				int k = i - j;
				while (k < i) 
				{
					((List) (obj)).add((Integer)paramList.get(k));
					k++;
				}
			}
			l = ((List) (obj)).size();
			while(i1 < l){
				int j1 = ((Integer)((List) (obj)).get(i1)).intValue();
				float f1 = Math.abs((float)measureHeight - (float)(j1 - Min) * ratio);
				if (f1 >= (float)measureHeight)
					f1 = measureHeight;
				if (i1 == 0)
					localPath.moveTo(paramFloat + Xinterval * (float)i1, f1);
				else
					localPath.lineTo(paramFloat + Xinterval * (float)i1, f1);
				i1++;
			}
	    }
	    
	    return localPath;
	    
	    
	}
	
	private Path makePathDashFixed(List<Integer> paramList, float paramFloat){
	    Path localPath = null;
	    int i;
	    int j;
	    int i1;
	    int l;
	    if (paramList != null)
	    {
	      Object obj;
	      i1 = 0;
	      localPath = new Path();
	      i = paramList.size();
	      this.Xinterval = 20.0F;
	      j = 1 + (int)((this.measureWidth - paramFloat) / this.Xinterval);
	      if (i <= j){
				obj = paramList;
		  } else
		  {
				obj = new ArrayList();
				int k = i - j;
				while (k < i) 
				{
					((List) (obj)).add((Integer)paramList.get(k));
					k++;
				}
		  }
		  l = ((List) (obj)).size();
		  while(i1 < l){
			  	int j1 = ((Integer)((List) (obj)).get(i1)).intValue();
				float f1 = Math.abs((float)measureHeight - (float)(j1 - Min) * ratio);
				if (f1 >= (float)measureHeight)
					f1 = measureHeight;
				if (i1 == 0)
					localPath.moveTo(paramFloat + Xinterval * (float)i1, f1);
				else
					localPath.lineTo(paramFloat + Xinterval * (float)i1, f1);
				i1++;
		  }
			
	    
	    }
	    return localPath;
	}

	private int measureHeight(int paramInt) {
		int i = View.MeasureSpec.getMode(paramInt);
		int j = View.MeasureSpec.getSize(paramInt);
		if (i == 0) {
			j = screenHeight;
		}
		return j;
	}

	private int measureWidth(int paramInt) {
		int i = View.MeasureSpec.getMode(paramInt);
		int j = View.MeasureSpec.getSize(paramInt);
		if (i == 0) {
			j = screenWidth;
		}
		return j;
	}
	
	private void setYAxisSpacing(List<Integer> paramList, int paramInt){
		
		if (paramList != null && paramList.size() > 0)
		{
			YAxisSpacing = paramInt / (-1 + paramList.size());
			ratio = YAxisSpacing / increaseSpeed;
		} else
		{
			YAxisSpacing = 0.0F;
		}
	}
	
	public void aaa(){
		
	}

	public int getmeasureHeight() {
		return measureHeight;
	}

	public int getmeasureWidth() {
		return measureWidth;
	}

	protected void onDraw(Canvas paramCanvas) {
		super.onDraw(paramCanvas);
		userWidth = 0.0F;
		setYAxisSpacing(YLables, (int) (measureHeight - Spacing));
		drawYAxisTitlt(YTitle, StartSpacing, measureHeight,paramCanvas);
		drawYAxisLable(YLables, userWidth, measureWidth,measureHeight, YAxisSpacing, paramCanvas);
		drawYAxisBoundary(YLables, userWidth, measureWidth,measureHeight, YAxisSpacing, paramCanvas);
		drawYAxis(userWidth, measureHeight, userWidth,Spacing, paramCanvas);
		drawXAxis(userWidth, measureHeight, measureWidth,measureHeight, paramCanvas);
		drawFoldLine(foldLineHelps, paramCanvas);
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		super.onMeasure(paramInt1, paramInt2);
		measureWidth = measureWidth(paramInt1);
		measureHeight = measureHeight(paramInt2);
		setMeasuredDimension(measureWidth, measureHeight);
		measureHeight = (int) (measureHeight - Spacing);
	}

	public void setCheckedFoldLine(String paramString) {
		BSSID = paramString;
		upDataMainUI();
	}

	public void setDisPlayOnOFF(boolean paramBoolean) {
		onOFF = paramBoolean;
		upDataMainUI();
	}

	public void setFoldLineData(List<FoldLineHelp> paramList) {
		if (paramList != null) {
			foldLineHelps = paramList;
			upDataMainUI();
		}
	}

	public void setYTitle(String paramString) {
		YTitle = paramString;
		upDataMainUI();
	}

	public void upDataMainUI() {
		invalidate();
	}
	
	public void setYLables(int paramInt1, int paramInt2, int paramInt3){
	    if (this.YLables != null)
	    {
	      this.YLables.clear();
	      this.YLables = null;
	    }
	    this.yMax = (paramInt1 - paramInt2);
	    this.Min = paramInt2;
	    this.Max = paramInt1;
	    this.YLables = new ArrayList<Integer>();
	    this.increaseSpeed = (this.yMax / paramInt3);
	    for (int i = 0; ; ++i)
	    {
	      if (i > paramInt3)
	      {
	        upDataMainUI();
	        return;
	      }
	      this.YLables.add(Integer.valueOf(paramInt2 + (int)(this.increaseSpeed * i)));
	    }
	}

}
