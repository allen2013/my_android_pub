package com.chart.widget;

import java.util.ArrayList;
import java.util.List;

import com.wifianalyzer.bo.WifiRoomSignalInfo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class RoomSignalMapView extends View {

	private List<WifiRoomSignalInfo> roomlist=null;
	private int measureHeight;
	private int measureWidth;
	private int screenHeight;
	private int screenWidth;
	private Canvas m_canvas;
	
	private int space=20;
	private Point focusPoint=null;
	private int focusPointRadius=10;
	
	public RoomSignalMapView(Context context) {
		super(context);
		init(context);
	}
	
	public RoomSignalMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RoomSignalMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	/**
	 * 初始化
	 * @param context
	 */
	private void init(Context context){
		
		DisplayMetrics  dm = context.getApplicationContext().getResources().getDisplayMetrics();
		screenHeight=dm.heightPixels;
		screenWidth=dm.widthPixels;
				
		
	}
	
	/**
	 * 设置房间信息列表
	 * @param list
	 */
	public void setRoomListData(List<WifiRoomSignalInfo> list){
		
		roomlist=new ArrayList<WifiRoomSignalInfo>(list);
		if(roomlist==null){
			roomlist=new ArrayList<WifiRoomSignalInfo>();
		}
		reflashMainUI();
		
	}
	
	/**
	 * 刷新界面
	 */
	private void reflashMainUI(){
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		m_canvas=canvas;
		//画主边框
		drawMainFrame(canvas,space,space,this.measureWidth-space,this.measureHeight-space);
		drawFocusLine(canvas, focusPoint);
		
	}
	
	/**
	 * 画主边框
	 * @param canvas
	 */
	private void drawMainFrame(Canvas canvas,int left, int top, int right, int bottom){
		
		canvas.save();
		Rect r=new Rect();
		r.set(left, top, right, bottom);
		Paint p=new Paint();
		p.setColor(Color.GRAY);
		p.setStrokeWidth(10);
		p.setStyle(Style.STROKE);
		canvas.drawRect(r, p);
		canvas.restore();
		
	}
	
	private void drawFocusLine(Canvas canvas,Point point){
		
		if(point!=null){
			canvas.save();
			Paint p=new Paint();
			p.setColor(Color.GREEN);
			p.setStrokeWidth(3);
			p.setStyle(Style.STROKE);
			canvas.drawLine(point.x, space, point.x, point.y-focusPointRadius, p);
			canvas.drawCircle(point.x, point.y, focusPointRadius, p);
			canvas.drawLine(space, point.y, point.x-focusPointRadius, point.y, p);
			canvas.restore();
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		focusPoint=new Point((int)event.getX(),(int)event.getY());
		reflashMainUI();
		return super.onTouchEvent(event);
	}
	
	protected void onMeasure(int i, int j)
	{
		super.onMeasure(i, j);
		measureWidth = measureWidth(i);
		measureHeight = measureHeight(j);
		setMeasuredDimension(measureWidth, measureHeight);
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
	
	

}
