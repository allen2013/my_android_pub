package com.chart.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class NewHistogramView extends View {
	private int AnimationIndex = 0;
	private Thread AnimationThread = null;
	private List<FoldLineHelp> HistogramHelps = null;
	private Paint HistogramPaint = null;
	private int Max;
	private int Min;
	private float PaintBetween = 5.0F;
	private float Spacing = 8.0F;
	private float StartSpacing = 10.0F;
	private Paint XAxisLablePaint = null;
	private Paint XAxisPaint = null;
	private List<Integer> XLables = null;
	private String XTitle = null;
	private Paint XTitlePaint = null;
	private float Xinterval;
	private Paint YAxisLablePaint = null;
	private Paint YAxisPaint = null;
	private float YAxisSpacing;
	private List<Integer> YLables = null;
	private String YTitle = null;
	private Paint YTitlePaint = null;
	private boolean animationswtich = true;
	private DisplayMetrics dm = null;
	private float increaseSpeed;
	private final float interval = 20.0F;
	private int measureHeight;
	private int measureWidth;
	private boolean onOFF = true;
	private float ratio;
	private float screenDensity;
	private int screenHeight;
	private int screenWidth;
	private float userHeight;
	private float userWidth;
	private int yMax;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				AnimationOndraw(AnimationIndex, 100);
				break;
			}
		};
	};

	public NewHistogramView(Context context) {
		super(context);
		init(context);
	}

	public NewHistogramView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public NewHistogramView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void AnimationOndraw(int i, int j){
		Iterator<FoldLineHelp> iterator = HistogramHelps.iterator();
		while(iterator.hasNext()){
			FoldLineHelp foldlinehelp=(FoldLineHelp)iterator.next();
			if (foldlinehelp.prelevel > foldlinehelp.level)
			{
				int l = (foldlinehelp.prelevel - foldlinehelp.level) / j;
				if ((foldlinehelp.prelevel - foldlinehelp.level) % j > 0)
					l++;
				if (foldlinehelp.templevel - foldlinehelp.level < l)
					foldlinehelp.templevel = foldlinehelp.level;
				else
					foldlinehelp.templevel = foldlinehelp.templevel - l;
			} else if(foldlinehelp.prelevel < foldlinehelp.level)
			{
				int k = (foldlinehelp.level - foldlinehelp.prelevel) / j;
				if ((foldlinehelp.level - foldlinehelp.prelevel) % j > 0)
					k++;
				if (foldlinehelp.level - foldlinehelp.templevel < k)
					foldlinehelp.templevel = foldlinehelp.level;
				else
					foldlinehelp.templevel = k + foldlinehelp.templevel;
			}
			
		}
		upDataMainUI();
	}
	
	private void StartAnimation()
	{
		if (AnimationThread != null)
		{
			if (AnimationThread.isAlive())
				AnimationThread.interrupt();
			AnimationThread = null;
		}
		AnimationIndex = 0;
		AnimationThread = new Thread(new Runnable() {

			public void run()
			{
				AnimationIndex = 0;
				do
				{
					NewHistogramView newhistogramview;
					try
					{
						Thread.sleep(15L);
					}
					catch (InterruptedException interruptedexception)
					{
						interruptedexception.printStackTrace();
					}
					handler.sendEmptyMessage(0);
					newhistogramview = NewHistogramView.this;
					newhistogramview.AnimationIndex = 1 + newhistogramview.AnimationIndex;
				} while (AnimationIndex < 100);
			}

			
		});
		AnimationThread.start();
	}
	
	private void calculateDrawXAxisLableHight(List<Integer> list)
	{
		Rect rect = new Rect();
		if (XAxisLablePaint != null && list != null && list.size() > 0){ 
			for(int j=0;j<list.size();j++){
				String s = ((Integer)list.get(j)).toString();
				XAxisLablePaint.getTextBounds(s, 0, s.length(), rect);
			}
			userHeight = userHeight + (float)rect.height();
		}

	}
	
	private void calculateDrawXAxisTitlehight(String s)
	{
		if (XTitlePaint != null && s != null)
		{
			Rect rect = new Rect();
			XTitlePaint.getTextBounds(s, 0, s.length(), rect);
			userHeight = (float)rect.height() + PaintBetween;
		} else
		{
			userHeight = 0.0F;
		}
	}
	
	private void calculateDrawYAxisLableWidth(List list)
	{
		int i = 0;
		if (YAxisLablePaint != null && list != null && list.size() > 0){
			for(int j=0;j<list.size();j++){
				float f = YAxisLablePaint.measureText(((Integer)list.get(j)).toString());
				if ((float)i < f)
					i = (int)f;
			}
		}
		userWidth = userWidth + (float)i + PaintBetween;
	}
	
	private void calculateDrawYAxisTitltWidth1(String s)
	{
		if (YTitlePaint != null && s != null)
		{
			Rect rect = new Rect();
			YTitlePaint.getTextBounds(s, 0, s.length(), rect);
			userWidth = rect.height();
		} else
		{
			userWidth = 0.0F;
		}
	}
	
	private void calculateDrawYAxisTitltWidth2(String s)
	{
		if (YTitlePaint != null && s != null)
		{
			Rect rect = new Rect();
			YTitlePaint.getTextBounds(s, 0, s.length(), rect);
			userWidth = userWidth + PaintBetween;
		} else
		{
			userWidth = 0.0F;
		}
	}
	
	
	private void drawHistogram(List<FoldLineHelp> list, List<Integer> list1, float f, float f1, float f2, Canvas canvas)
	{
		if (HistogramPaint != null && list != null && list.size() > 0 && list1 != null && list1.size() > 0){ 
			
			Rect rect = new Rect();
			for(int i=0;i<list1.size();i++){
				ArrayList<FoldLineHelp> arraylist = new ArrayList<FoldLineHelp>();
				for(int j=0;j<list.size();j++){
					FoldLineHelp foldlinehelp = (FoldLineHelp)list.get(j);
					if(foldlinehelp.SSID!=null && foldlinehelp.BSSID!=null){
						if (foldlinehelp.channel == ((Integer)list1.get(i)).intValue())
							arraylist.add(foldlinehelp);
					}
				}
				Collections.sort(arraylist, new Comparator() {
	
					public int compare(FoldLineHelp foldlinehelp2, FoldLineHelp foldlinehelp3)
					{
						return foldlinehelp3.level - foldlinehelp2.level;
					}
	
					public int compare(Object obj, Object obj1)
					{
						return compare((FoldLineHelp)obj, (FoldLineHelp)obj1);
					}
					
				});
				for(int k=0;k<arraylist.size();k++){
					canvas.save();
					FoldLineHelp foldlinehelp1 = (FoldLineHelp)arraylist.get(k);
					HistogramPaint.setColor(foldlinehelp1.color);
					if (foldlinehelp1.drawflag)
					{
						String s = foldlinehelp1.SSID==null?"":foldlinehelp1.SSID;
						
						int l;
						int i1;
						float f3;
						float f4;
						float f5;
						float f6;
						float f7;
						Path path;
						if (animationswtich)
							l = foldlinehelp1.templevel;
						else
							l = foldlinehelp1.level;
						i1 = foldlinehelp1.channel;
						HistogramPaint.getTextBounds(s, 0, s.length(), rect);
						f3 = f1 - (float)(l - Min) * ratio;
						if (f3 >= f1)
							f3 = f1;
						f4 = f + f2 * (float)(i1 - 1);
						f5 = f + f2 * (float)i1;
						f6 = 2.0F * f3 - (f1 + f1) / 2.0F;
						f7 = f + f2 * (float)(i1 + 1);
						path = new Path();
						path.moveTo(f4, f1);
						path.quadTo(f5, f6, f7, f1);
						HistogramPaint.setStyle(android.graphics.Paint.Style.STROKE);
						HistogramPaint.setStrokeWidth(3F);
						canvas.drawPath(path, HistogramPaint);
						HistogramPaint.setStyle(android.graphics.Paint.Style.FILL);
						HistogramPaint.setAlpha(100);
						canvas.drawPath(path, HistogramPaint);
						HistogramPaint.setAlpha(255);
						if (i1 == ((Integer)list1.get(-1 + list1.size())).intValue())
							canvas.drawText(s, (f + f2 * (float)i1) - (float)s.length(), f3 - (float)rect.height(), HistogramPaint);
						else
							canvas.drawText(s, f + f2 * (float)i1, f3 - (float)rect.height(), HistogramPaint);
					}
				}
			}
		}

	}
	
	private void drawXAxis(float f, float f1, float f2, float f3, Canvas canvas)
	{
		if (XAxisPaint != null)
		{
			canvas.save();
			canvas.drawLine(f, f1, f2, f3, XAxisPaint);
			canvas.restore();
		}
	}
	
	private void drawXAxisLable(List list, float f, float f1, float f2, Canvas canvas)
	{
		Rect rect = new Rect();
		if (XAxisLablePaint != null && list != null && list.size() > 0){
			Xinterval = (f1 - f) / (float)list.size();
			for(int j=0;j<list.size();j++){
				canvas.save();
				String s = ((Integer)list.get(j)).toString();
				XAxisLablePaint.measureText(s);
				XAxisLablePaint.getTextBounds(s, 0, s.length(), rect);
				canvas.drawText(s, f + Xinterval * (float)j, f2 - (float)rect.height(), XAxisLablePaint);
				canvas.restore();
			}
			userHeight = userHeight + (float)rect.height() + PaintBetween;
		
		}
	}
	
	private void drawXAxisTitle(String s, float f, float f1, float f2, float f3, Canvas canvas)
	{
		if (XTitlePaint != null && s != null)
		{
			canvas.save();
			Path path = new Path();
			path.moveTo(f, f1);
			path.lineTo(f2, f3);
			canvas.drawTextOnPath(s, path, 0.0F, 0.0F, XTitlePaint);
			canvas.restore();
		}
	}
	
	private void drawYAxis(float f, float f1, float f2, float f3, Canvas canvas)
	{
		if (YAxisPaint != null)
		{
			canvas.save();
			canvas.drawLine(f, f1, f2, f3, YAxisPaint);
			canvas.restore();
		}
	}
	
	private void drawYAxisBoundary(List<Integer> list, float f, float f1, float f2, float f3, Canvas canvas)
	{
		if (YAxisLablePaint != null && list != null && list.size() > 0){
			Paint paint = new Paint();
			for(int j=0;j<list.size();j++){
				canvas.save();
				if (j + 1 < list.size())
				{
					paint.setColor(0xff888888);
					paint.setPathEffect(new DashPathEffect(new float[] {
						5F, 5F, 5F, 5F
					}, PaintBetween));
					paint.setAntiAlias(true);
					canvas.drawLine(f, f2 - f3 * (float)(j + 1), f1, f2 - f3 * (float)(j + 1), paint);
				}
				canvas.restore();
			}
		}

	}
	
	private void drawYAxisLable(List<Integer> list, float f, float f1, float f2, Canvas canvas)
	{
		if (YAxisLablePaint != null && list!= null && list.size() > 0){
			for(int j=0;j<list.size();j++){
				canvas.save();
				float f3 = YAxisLablePaint.measureText(((Integer)list.get(j)).toString());
				canvas.drawText(((Integer)list.get(j)).toString(), f + f3, f1 - f2 * (float)j, YAxisLablePaint);
				canvas.restore();
			}
		}

	}
	
	private void drawYAxisTitlt(String s, float f, float f1, float f2, float f3, Canvas canvas)
	{
		if (YTitlePaint != null && s != null)
		{
			canvas.save();
			Path path = new Path();
			path.moveTo(f, f1);
			path.lineTo(f2, f3);
			canvas.drawTextOnPath(s, path, 0.0F, 0.0F, YTitlePaint);
			canvas.restore();
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

	private void initHistogramPaint() {
		HistogramPaint = new Paint();
		HistogramPaint.setAntiAlias(true);
		HistogramPaint.setTextAlign(Paint.Align.CENTER);
	}

	private void initPaint() {
		initYTitlePaint();
		initXTitlePaint();
		initYAxisLablePaint();
		initXAxisLablePaint();
		initYAxisPaint();
		initXAxisPaint();
		initHistogramPaint();
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

	private void initXTitlePaint() {
		XTitlePaint = new Paint();
		XTitlePaint.setAntiAlias(true);
		XTitlePaint.setColor(Color.BLACK);
		XTitlePaint.setTextSize(20.0F);
		XTitlePaint.setTextAlign(Paint.Align.CENTER);
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
	
	private int measureHeight(int i)
	{
		int j = android.view.View.MeasureSpec.getMode(i);
		int k = android.view.View.MeasureSpec.getSize(i);
		if (j == 0)
			k = screenHeight;
		return k;
	}

	private int measureWidth(int i)
	{
		int j = android.view.View.MeasureSpec.getMode(i);
		int k = android.view.View.MeasureSpec.getSize(i);
		if (j == 0)
			k = screenWidth;
		return k;
	}

	private void setYAxisSpacing(List list, int i)
	{
		if (list != null && list.size() > 0)
		{
			YAxisSpacing = i / (-1 + list.size());
			ratio = YAxisSpacing / increaseSpeed;
		} else
		{
			YAxisSpacing = 0.0F;
		}
	}

	public void SetAnimation(boolean flag)
	{
		animationswtich = flag;
	}

	public int getmeasureHeight()
	{
		return measureHeight;
	}

	public int getmeasureWidth()
	{
		return measureWidth;
	}

	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		calculateDrawYAxisTitltWidth1(YTitle);
		calculateDrawYAxisTitltWidth2(YTitle);
		calculateDrawYAxisLableWidth(YLables);
		drawXAxisTitle(XTitle, userWidth, measureHeight, measureWidth, measureHeight, canvas);
		calculateDrawXAxisTitlehight(XTitle);
		drawXAxisLable(XLables, userWidth, measureWidth, (float)measureHeight - userHeight, canvas);
		calculateDrawXAxisLableHight(XLables);
		setYAxisSpacing(YLables, (int)((float)measureHeight - Spacing - userHeight));
		userWidth = 0.0F;
		calculateDrawYAxisTitltWidth1(YTitle);
		drawYAxisTitlt(YTitle, userWidth, (float)measureHeight - userHeight, userWidth, Spacing, canvas);
		calculateDrawYAxisTitltWidth2(YTitle);
		drawYAxisLable(YLables, userWidth, (float)measureHeight - userHeight, YAxisSpacing, canvas);
		calculateDrawYAxisLableWidth(YLables);
		drawYAxisBoundary(YLables, userWidth, measureWidth, (float)measureHeight - userHeight, YAxisSpacing, canvas);
		drawYAxis(userWidth, (float)measureHeight - userHeight, userWidth, Spacing, canvas);
		drawXAxis(userWidth, (float)measureHeight - userHeight, measureWidth, (float)measureHeight - userHeight, canvas);
		drawHistogram(HistogramHelps, XLables, userWidth, (float)measureHeight - userHeight, Xinterval, canvas);
	}

	protected void onMeasure(int i, int j)
	{
		super.onMeasure(i, j);
		measureWidth = measureWidth(i);
		measureHeight = measureHeight(j);
		setMeasuredDimension(measureWidth, measureHeight);
		measureHeight = (int)((float)measureHeight - Spacing);
	}

	public void setDisPlayOnOFF(boolean flag)
	{
		onOFF = flag;
		upDataMainUI();
	}

	public void setHistogramHelpData(List list)
	{
		if (list != null)
			HistogramHelps = new ArrayList(list);
		if (animationswtich)
			StartAnimation();
		else
			upDataMainUI();
	}

	public void setXLables(int i, int j, int k)
	{
		if (XLables != null)
		{
			XLables.clear();
			XLables = null;
		}
		XLables = new ArrayList();
		int l = (i - j) / k;
		int i1 = 0;
		do
		{
			XLables.add(Integer.valueOf(l * i1));
			i1++;
		} while (i1<=k);
		upDataMainUI();
	}

	public void setXTitle(String s)
	{
		XTitle = s;
		upDataMainUI();
	}

	public void setYLables(int i, int j, int k)
	{
		if (YLables != null)
		{
			YLables.clear();
			YLables = null;
		}
		yMax = i - j;
		Min = j;
		Max = i;
		YLables = new ArrayList<Integer>();
		increaseSpeed = yMax / k;
		int l = 0;
		do
		{
			YLables.add(Integer.valueOf(j + (int)(increaseSpeed * (float)l)));
			l++;
		} while (l<=k);
		upDataMainUI();
	}

	public void setYTitle(String s)
	{
		YTitle = s;
		upDataMainUI();
	}

	public void upDataMainUI()
	{
		invalidate();
	}
	
}
