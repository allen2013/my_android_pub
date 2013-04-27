package com.dragonflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ros.zeroconf.jmdns.DiscoveredService;

import com.dragonflow.genie.ui.R; 
import com.filebrowse.ScanDeviceService;



import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;



public class mapview extends View {
	
	Paint mPaint; //
	
	Bitmap bmp = null;
	Bitmap bmp_blockflag = null;
	Bitmap wireless_bmp = null;
	Bitmap turbo=null;
	int intencity = -1;
	Context mContext;
	double PI = 3.1415926;
	int C;
	int imgW,imgH,wireless_imgW,wireless_imgH;
	double Angle = 0;
    double RealX = 0;
    double RealY = 0;
    int RL = 300;
    int RS = 150;
    int x0 = 200;
    int y0 = 320;
    public static final int imagebase = GenieGlobalDefines.Device_Internet;
    public static mapview mview=null;
    private int m_wlan_ebable = 0;
    
    final public static int MaxDeviceSize = 9;
    
    private int m_devicesblockstatus = -1;
	public static final int image[] = {
		
		
		
		
		
		R.drawable.internetnormal,
		R.drawable.hdm,
//		R.drawable.gatewaydev,
//		R.drawable.networkdev,		
//		R.drawable.windowspc,
//		R.drawable.bridge,
//		R.drawable.cablestb,
//		R.drawable.cameradev,
//		R.drawable.gamedev,
//		R.drawable.imacdev,
//		R.drawable.ipad,
//		R.drawable.iphone,
//		R.drawable.ipodtouch,
//		R.drawable.linuxpc,
//		R.drawable.macbookdev,
//		R.drawable.macminidev,
//		R.drawable.macprodev,
//		R.drawable.mediadev,
//		R.drawable.mobiledev,
//		R.drawable.netstoragedev,
//		R.drawable.switchdev,   //switch
//		R.drawable.printerdev,
//		R.drawable.repeater,
//		R.drawable.satellitestb,
//		R.drawable.scannerdev,
//		R.drawable.slingbox,
//		R.drawable.stb,
//		R.drawable.tablepc,
//		R.drawable.tv,
//		R.drawable.androiddevice,
//		R.drawable.androidphone,
//		R.drawable.androitablet,
//		R.drawable.unixpc,
//		
		R.drawable.imacdev,
		R.drawable.ipad,
		R.drawable.iphone,
		R.drawable.ipodtouch,
		R.drawable.androiddevice,
		R.drawable.androidphone,
		R.drawable.androitablet,
		R.drawable.blurayplayer,    //
		R.drawable.bridge,
		R.drawable.cablestb,
		R.drawable.cameradev,
		R.drawable.dvr,
		R.drawable.gamedev,
		R.drawable.linuxpc,
		R.drawable.macminidev,
		R.drawable.macprodev,
		R.drawable.macbookdev,
		R.drawable.mediadev,
		R.drawable.networkdev,
		R.drawable.stb,
		R.drawable.printerdev,
		R.drawable.repeater,
		R.drawable.gatewaydev,
		R.drawable.satellitestb,
		R.drawable.scannerdev,
		R.drawable.slingbox,
		R.drawable.netstoragedev,
		R.drawable.mobiledev,
		R.drawable.switchdev,
		R.drawable.tv,
		R.drawable.tablepc,
		R.drawable.unixpc,		
		R.drawable.windowspc,		
		R.drawable.cg3300,
		R.drawable.cgd24g,
		R.drawable.dg834gt,
		R.drawable.dg834gv,
		R.drawable.dg834g,
		R.drawable.dg834n,
		R.drawable.dg834pn,
		R.drawable.dg834,
		R.drawable.dgn1000_rn,
		R.drawable.dgn2200m,
		R.drawable.dgn2200,
		R.drawable.dgn2000,
		R.drawable.dgn3500,
		R.drawable.dgnb2100,
		R.drawable.dgnd3300,
		R.drawable.dgnd3700,
		R.drawable.dm111psp,
		R.drawable.dm111p,
		R.drawable.jwnr2000t,
		R.drawable.mbm621,
		R.drawable.mbr624gu,
		R.drawable.mbr1210_1bmcns,
		R.drawable.mbrn3000,
		R.drawable.rp614,
		R.drawable.wgr612,
		R.drawable.wgr614l,
		R.drawable.wgr614,
		R.drawable.wgt624,
		R.drawable.wnb2100,
		R.drawable.wndr37av,
		R.drawable.wndr3300,
		R.drawable.wndr3400,
		R.drawable.wndr3700,
		R.drawable.wndr3800,
		R.drawable.wndr4000,
		R.drawable.wndrmac,
		R.drawable.wnr612,
		R.drawable.wnr834b,
		R.drawable.wnr834m,
		R.drawable.wnr854t,
		R.drawable.wnr1000,
		R.drawable.wnr2000,
		R.drawable.wnr2200,
		R.drawable.wnr3500l,
		R.drawable.wnr3500,
		R.drawable.wnxr2000,
		R.drawable.wpn824ext,
		R.drawable.wpn824n,
		R.drawable.wpn824,
		R.drawable.wndr4500,
		R.drawable.wndr4700,
		R.drawable.dgnd4000,
		R.drawable.wnr500,
		R.drawable.jnr3000,
		R.drawable.jnr3210,
		R.drawable.jwnr2000,
		R.drawable.r6300,
		R.drawable.r6200,
	};
	

//	public static final int imageclick[] = {
//		R.drawable.internetnormal,
//		R.drawable.hdm_down,
//		R.drawable.gatewaydev_down,
//		R.drawable.networkdev_down,		
//		R.drawable.windowspc_down,
//		R.drawable.bridge_down,
//		R.drawable.cablestb_down,
//		R.drawable.cameradev_down,
//		R.drawable.gamedev_down,
//		R.drawable.imacdev_down,
//		R.drawable.ipad_down,
//		R.drawable.iphone_down,
//		R.drawable.ipodtouch_down,
//		R.drawable.linuxpc_down,
//		R.drawable.macbookdev_down,
//		R.drawable.macminidev_down,
//		R.drawable.macprodev_down,
//		R.drawable.mediadev_down,
//		R.drawable.mobiledev_down,
//		R.drawable.netstoragedev_down,
//		R.drawable.switchdev_down,   //switch
//		R.drawable.printerdev_down,
//		R.drawable.repeater_down,
//		R.drawable.satellitestb_down,
//		R.drawable.scannerdev_down,
//		R.drawable.slingbox_down,
//		R.drawable.stb_down,
//		R.drawable.tablepc_down,
//		R.drawable.tv_down,
//		R.drawable.androiddevice_down,
//		R.drawable.androidphone_down,
//		R.drawable.androitablet_down,
//		R.drawable.unixpc_down,
//		R.drawable.cg3300_down,
//		R.drawable.cgd24g_down,
//		R.drawable.dg834gt_down,
//		R.drawable.dg834gv_down,
//		R.drawable.dg834g_down,
//		R.drawable.dg834n_down,
//		R.drawable.dg834pn_down,
//		R.drawable.dg834_down,
//		R.drawable.dgn1000_rn_down,
//		R.drawable.dgn2200m_down,
//		R.drawable.dgn2200_down,
//		R.drawable.dgn2000_down,
//		R.drawable.dgn3500_down,
//		R.drawable.dgnb2100_down,
//		R.drawable.dgnd3300_down,
//		R.drawable.dgnd3700_down,
//		R.drawable.dm111psp_down,
//		R.drawable.dm111p_down,
//		R.drawable.jwnr2000_down,
//		R.drawable.mbm621_down,
//		R.drawable.mbr624gu_down,
//		R.drawable.mbr1210_1bmcns_down,
//		R.drawable.mbrn3000_down,
//		R.drawable.rp614_down,
//		R.drawable.wgr612_down,
//		R.drawable.wgr614l_down,
//		R.drawable.wgr614_down,
//		R.drawable.wgt624_down,
//		R.drawable.wnb2100_down,
//		R.drawable.wndr37av_down,
//		R.drawable.wndr3300_down,
//		R.drawable.wndr3400_down,
//		R.drawable.wndr3700_down,
//		R.drawable.wndr3800_down,
//		R.drawable.wndr4000_down,
//		R.drawable.wndrmac_down,
//		R.drawable.wnr612_down,
//		R.drawable.wnr834b_down,
//		R.drawable.wnr834m_down,
//		R.drawable.wnr854t_down,
//		R.drawable.wnr1000_down,
//		R.drawable.wnr2000_down,
//		R.drawable.wnr2200_down,
//		R.drawable.wnr3500l_down,
//		R.drawable.wnr3500_down,
//		R.drawable.wnxr2000_down,
//		R.drawable.wpn824ext_down,
//		R.drawable.wpn824n_down,
//		R.drawable.wpn824_down,
//		R.drawable.wndr4500_down,
//
//	};
    
    private float mPhase;
    
    private OnMapClickListener  m_listener = null;
    
    public ArrayList<DeviceInfo>  info = null;
	
	public mapview(Context context,ArrayList<DeviceInfo>  devicelist,int wlan_ebable,int devicesblockenable) {
		super(context);
		mContext = context;
		
		m_devicesblockstatus = devicesblockenable;
		if(m_devicesblockstatus == GenieGlobalDefines.BLOCKDEVICES_ENABLE)
		{
			bmp_blockflag = BitmapFactory.decodeResource(context.getResources(),R.drawable.blockflag);
		}
		
		
	    bmp = BitmapFactory.decodeResource(context.getResources(), image[0]);
		Display d = ((Activity)context).getWindowManager().getDefaultDisplay();
		imgW = bmp.getWidth();
		imgH = bmp.getHeight();
//		RL = ((d.getHeight()-120)/ 2 - imgW) ;
//		RS = (d.getWidth()/ 2 - imgW);
//		x0 = RS + imgW / 2;
//		//x0 = RS;
//		y0 = RL;
		
		turbo=BitmapFactory.decodeResource(this.mContext.getResources(),R.drawable.turbo);
		

		
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		
		int titlerhight = (int)(70*metrics.scaledDensity);
      

		RL = ((d.getHeight() - titlerhight)/2  - imgH);
		RS = (d.getWidth()/2  - imgW);
		x0 = d.getWidth()/2;
		//x0 = RS;
		y0 = RL + imgH/2;
		
		GenieDebug.error("mapview","mapview  RL = "+RL);
		GenieDebug.error("mapview","mapview  RS = "+RS);
		GenieDebug.error("mapview","mapview  x0 = "+x0);
		GenieDebug.error("mapview","mapview  y0 = "+y0);
		
		
		wireless_bmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.wirelessflag1);
		wireless_imgW = wireless_bmp.getWidth();
		wireless_imgH = wireless_bmp.getHeight();
		
		GenieDebug.error("mapview","mapview  0");
		
		GenieDebug.error("mapview","mapview  d.getHeight() = "+d.getHeight());
		GenieDebug.error("mapview","mapview  d.getWidth() = "+d.getWidth());
		
		m_wlan_ebable = wlan_ebable;
		
		info = new ArrayList<DeviceInfo>();
		
		info.addAll(devicelist); 
		
		/*DeviceInfo temp = new DeviceInfo(); 
		temp.type = 0;
		info.add(temp);
		temp = null;
		
		temp = new DeviceInfo();
		temp.type = 1;
		info.add(temp);
		temp = null;
		
		temp = new DeviceInfo();
		temp.type = 1;
		info.add(temp);
		temp = null;
		
		temp = new DeviceInfo();
		temp.type = 1;
		info.add(temp);
		temp = null;
		
		temp = new DeviceInfo();
		temp.type = 1;
		info.add(temp);
		temp = null;
		
		temp = new DeviceInfo();
		temp.type = 1;
		info.add(temp);
		temp = null;
		
		temp = new DeviceInfo();
		temp.type = 1;
		info.add(temp);
		temp = null;
		
		temp = new DeviceInfo();
		temp.type = 1;
		info.add(temp);
		temp = null;
*/		
		GenieDebug.error("mapview","mapview  info.size() = "+info.size());
		
		Angle = 360 / (info.size() - 1);
    	for(int j = 0; j < info.size() - 1; j++)
    	{
	    	RealX = RS * Math.cos(((j * Angle) + Angle) * (PI/180)) + x0;
		    RealY = RL * Math.sin(((j * Angle) + Angle) * (PI/180)) + y0;
		    
		    GenieDebug.error("mapview","mapview  j = "+j);
		    
			GenieDebug.error("mapview","mapview  RealX = "+RealX);
			GenieDebug.error("mapview","mapview  RealY = "+RealY);
		    
		    info.get(j).x = (int)RealX;
		    info.get(j).y = (int)RealY; 
		    info.get(j).clicked = false;
		    
		    
		    GenieDebug.error("mapview","mapview  info.get(j).type = "+info.get(j).type);      
		    info.get(j).bmp = BitmapFactory.decodeResource(context.getResources(), image[info.get(j).type - imagebase]);
		    //info.get(j).clickbmp = BitmapFactory.decodeResource(context.getResources(), imageclick[info.get(j).type - imagebase]);
		    
		    info.get(j).w = info.get(j).bmp.getWidth();
		    info.get(j).h = info.get(j).bmp.getHeight();
		    info.get(j).bmpx = (info.get(j).x-info.get(j).w/2);
		    info.get(j).bmpy = (info.get(j).y-info.get(j).h/2);
		    
		    if(null != info.get(j).ConnectionType && info.get(j).ConnectionType.equals("wireless"))
			{
		    	String s = info.get(j).intensity;
				if(s != null && s != "")
				{
					int i = Integer.parseInt(s);
					if(i < 20)
						info.get(j).wlbmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.wirelessflag1);
					else if(i >= 20 && i < 50)
						info.get(j).wlbmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.wirelessflag2);
					else if(i >= 50 && i < 70)
						info.get(j).wlbmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.wirelessflag3);
//					else if(i > 40 && i <= 60)
//						info.get(j).wlbmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.wirelessflag4);
//					else if(i > 40 && i <= 70)
//						info.get(j).wlbmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.wirelessflag3);
					else if(i > 70)
						info.get(j).wlbmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.wirelessflag4);
					else
						info.get(j).wlbmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.wirelessflag4);
				}
				else
				{
					info.get(j).wlbmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.wirelessflag4);
				}
			}
    	}
    	
    	int rout = info.size() - 1;
    	
    	info.get(rout).x = x0;
    	
	    info.get(rout).y = y0;
	    info.get(rout).bmp = BitmapFactory.decodeResource(context.getResources(), image[info.get(rout).type - imagebase]);
	    //info.get(rout).clickbmp = BitmapFactory.decodeResource(context.getResources(), imageclick[info.get(rout).type - imagebase]);
	    info.get(rout).w = info.get(rout).bmp.getWidth();
	    info.get(rout).h = info.get(rout).bmp.getHeight();
	    info.get(rout).bmpx = (info.get(rout).x-info.get(rout).w/2);
	    info.get(rout).bmpy = (info.get(rout).y-info.get(rout).h/2);
	    info.get(rout).clicked = false;
	    
	    this.setClickable(true);
	    invalidate();
	    this.mview=this;
	}
	
	public void addClickListener(OnMapClickListener listener) {
		m_listener = listener;
	}
	
	public mapview(Context context, AttributeSet attrs){
		super(context, attrs);

	}
	
	
	
	private  int m_ClickDownId = -1;


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//GenieDebug.error("onTouchEvent","onTouchEvent 222 int");		
		super.onTouchEvent(event);
		
		//GenieDebug.error("onTouchEvent","onTouchEvent 222 int Action = "+event.getAction());
		int x,y,w,h,type;
		
		
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			GenieDebug.error("onTouchEvent","onTouchEvent 222 ACTION_DOWN");
			m_ClickDownId = -1;
			
			for(int j = 0; j < info.size(); j++)
			{	
				x = info.get(j).x;
	    		y = info.get(j).y;
	    		w = info.get(j).w;
	    		h = info.get(j).h;
	    		type = info.get(j).type;
	    		
	    		int bmpx = info.get(j).bmpx;
	    		int bmpy = info.get(j).bmpy;
				
				if(event.getX() > bmpx && event.getX() < (bmpx + w) &&
					event.getY() > bmpy && event.getY() < (bmpy + h))
				{
					if(!GenieRequest.m_SmartNetWork)
	    	    	{
						if(info.get(j).type != GenieGlobalDefines.Device_Internet)
						{
							info.get(j).clicked = true;
							m_ClickDownId = j;
							invalidate();
							break;
						}
	    	    	}else
	    	    	{
	    	    		GenieDebug.error("onTouchEvent","onTouchEvent  info.get(j).type = "+info.get(j).type);
	    	    		
	    	    		if(info.get(j).type != GenieGlobalDefines.Device_Internet
	    	    				&& (j != (info.size()-1)))
						{
							info.get(j).clicked = true;
							m_ClickDownId = j;
							invalidate();
							break;
						}
	    	    	}
				}
			}
			
			GenieDebug.error("onTouchEvent","onTouchEvent 222 ACTION_DOWN m_ClickDownId="+m_ClickDownId);
			
		}
		boolean upclick = false;
		
		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			GenieDebug.error("onTouchEvent","onTouchEvent 222 ACTION_UP m_ClickDownId="+m_ClickDownId);
			
			for(int j = 0; j < info.size(); j++)
			{	
				x = info.get(j).x;
	    		y = info.get(j).y;
	    		w = info.get(j).w;
	    		h = info.get(j).h;
	    		type = info.get(j).type;
	    		
	    		int bmpx = info.get(j).bmpx;
	    		int bmpy = info.get(j).bmpy;
				
				if(event.getX() > bmpx && event.getX() < (bmpx + w) &&
					event.getY() > bmpy && event.getY() < (bmpy + h))
				{
					GenieDebug.error("onTouchEvent","onTouchEvent 222 ACTION_UP 55 j="+j);
					GenieDebug.error("onTouchEvent","onTouchEvent 222 ACTION_UP 55 m_ClickDownId="+m_ClickDownId);
					if(null != m_listener && j == m_ClickDownId)
					{
						

						//mPaint.setColor(getResources().getColor(R.color.blackcolor));
						//mPaint.setStyle(Style.FILL);
						//canvas.drawRect(x, y,(x+w),(y+h+15), mPaint); //

						info.get(j).clicked = false;
						GenieDebug.error("onTouchEvent","onTouchEvent  j = "+j);
						GenieDebug.error("onTouchEvent","onTouchEvent type = "+type);
						invalidate();
						
						if(j == (info.size()-1))
						{
							m_listener.onClick(this, j, type,true);
						}else
						{
							m_listener.onClick(this, j, type,false);
						}
						
						
						upclick = true;
						
					}
					break;
				}
			}
			
			if(!upclick)
			{
				if(-1 != m_ClickDownId)
				{
					info.get(m_ClickDownId).clicked = false;				
					invalidate();
					m_ClickDownId = -1;
				}
			}
			
		}
		
		GenieDebug.error("onTouchEvent","onTouchEvent 222 outret = ");
		
		return false;
	}
	
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		int x,y,w,h;
		
		mPaint = new Paint();
		//GenieDebug.error("mapview","mapview  onDraw  info.size() = "+info.size());
        
		 
		Paint mPaint_line = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint_line.setStyle(Paint.Style.STROKE);
		mPaint_line.setStrokeWidth(3);
    	for(int j = 0; j < info.size()-1; j++)
    	{
    		x = info.get(j).x;
    		y = info.get(j).y;
    		w = info.get(j).w;
    		h = info.get(j).h;
    		int bmpx = info.get(j).bmpx;
    		int bmpy = info.get(j).bmpy;
    		
    		
    		
		   // mPaint.setColor(Color.BLUE); 
		    //mPaint.setStrokeWidth(2);
		    
//    		Path mPath = new Path(); 
//    		mPath.moveTo(x0, y0); 
//    		mPath.lineTo((x + imgW / 2), ( y + imgH / 2)); 
//    		
//    		RectF bounds = new RectF();
//            mPath.computeBounds(bounds, false);
//            canvas.translate(10 - bounds.left, 10 - bounds.top);
//    		
//    		PathEffect mEffects = new PathEffect();
//    		
//    		mEffects = new DashPathEffect(new float[] {10, 5, 5, 5}, mPhase);
//    		
//            mPhase += 1;
//            //invalidate();
//            
//            mPaint_line.setPathEffect(mEffects);
//            mPaint_line.setColor(Color.BLUE);
//            canvas.drawPath(mPath, mPaint_line);
    		
    		if(j == (info.size()-2))
    		{
    			
    			Paint paint2 = new Paint();
    			if(1 == m_wlan_ebable)
    			{
    				paint2.setColor(getResources().getColor(R.color.MediumSeaGreen));
    			}else
    			{
    				paint2.setColor(Color.RED);
    			}
    			paint2.setStrokeWidth(3);
    			paint2.setAntiAlias(true);
//    			canvas.drawLine(x0, y0, x + imgW / 2, y + imgH / 2, paint2);
    			canvas.drawLine(x0, y0, x , y, paint2);
    		}else
    		{
    			if(null != info.get(j).ConnectionType )
    			{
	    			if(info.get(j).ConnectionType.equals("wireless"))
	    			{
	    				Path p = new Path(); 
	    			
	    				p.moveTo(x0, y0); 
	//    				p.lineTo((x + imgW / 2), ( y + imgH / 2));  
	    				p.lineTo(x , y );  
	    		
	    				//p.moveTo((x + imgW / 2), ( y + imgH / 2)); 
	    				//p.lineTo(x0, y0); 
	    		
	    				//PathEffect mEffects =  new PathDashPathEffect(makePathDash(), 15, 1,PathDashPathEffect.Style.ROTATE);
	    				PathEffect mEffects =  new DashPathEffect(new float[] {5, 5, 5, 5}, 1); 
	    				mPaint_line.setPathEffect(mEffects); 
	    				mPaint_line.setAntiAlias(true);
	    				if(GenieGlobalDefines.Device_Genie == info.get(j).type)
	    				{
	    					mPaint_line.setColor(getResources().getColor(R.color.MediumSeaGreen));
	    				}else
	    				{
	    					mPaint_line.setColor(Color.BLUE);
	    				}
	    				canvas.drawPath(p, mPaint_line);
	    				
	    				if(info.get(j).wlbmp != null)
	    					canvas.drawBitmap(info.get(j).wlbmp,((x0+x)/2-(wireless_imgW/2)),((y0+y)/2 - (wireless_imgH/2)), null);
	    				
	    				
	    			}else
	    			{
	    				Paint paint2 = new Paint();        			
	        			//paint2.setColor(getResources().getColor(R.color.MediumSeaGreen));
	    				paint2.setColor(Color.BLUE); 
	        			paint2.setStrokeWidth(3);
	        			paint2.setAntiAlias(true);
	        			//canvas.drawLine(x0, y0, x + imgW / 2, y + imgH / 2, paint2);
	        			canvas.drawLine(x0, y0, x, y, paint2);
	    			}
    			}else
    			{
    				Path p = new Path(); 
	    			
    				p.moveTo(x0, y0); 
//    				p.lineTo((x + imgW / 2), ( y + imgH / 2));  
    				p.lineTo(x , y );  
    		
    				//p.moveTo((x + imgW / 2), ( y + imgH / 2)); 
    				//p.lineTo(x0, y0); 
    		
    				//PathEffect mEffects =  new PathDashPathEffect(makePathDash(), 15, 1,PathDashPathEffect.Style.ROTATE);
    				PathEffect mEffects =  new DashPathEffect(new float[] {5, 5, 5, 5}, 1); 
    				mPaint_line.setPathEffect(mEffects); 
    				mPaint_line.setAntiAlias(true);
    				if(GenieGlobalDefines.Device_Genie == info.get(j).type)
    				{
    					mPaint_line.setColor(getResources().getColor(R.color.MediumSeaGreen));
    				}else
    				{
    					mPaint_line.setColor(Color.BLUE);
    				}
    				canvas.drawPath(p, mPaint_line);
    			}
    		}	

            
            
            //GenieDebug.error("onDraw","onDraw 333  j = "+j);
            
          //  canvas.translate(0, 28);

		    //mPaint.setStyle(Style.);
		    //canvas.drawLine(x0, y0, x + imgW / 2, y + imgH / 2, mPaint);
            
            
		    
		    //mPaint.setColor(getResources().getColor(R.color.blackcolor));
			//mPaint.setStyle(Style.FILL);
		    //canvas.drawRect(x, y,(x+w),(y+h+15), mPaint); //
            
//            
//            if(x < (x0-imgW/2))
//            {
//            	bmpx = x - imgW/2;
//            }else 
//            {
//            	bmpx = x;
//            }
//            
//            if(y > y0)
//            {
//            	bmpy = y - imgW/2;
//            }else 
//            {
//            	bmpy = y;
//            }
//            
            
		    
    		if(info.get(j).clicked)
    		{
    			//if(null != info.get(j).clickbmp)
    			//{
    			//	canvas.drawBitmap(info.get(j).clickbmp,bmpx,bmpy, null);
    			//}else
    			//{
    			//	canvas.drawBitmap(info.get(j).bmp,bmpx,bmpy, null);
    			//}
    			
    			Rect  dstrect = new Rect(bmpx-8, bmpy-8, bmpx+w+8, bmpy+h+8);
    			
    			canvas.drawBitmap(info.get(j).bmp, null, dstrect, null);
    			
    			if(m_devicesblockstatus == GenieGlobalDefines.BLOCKDEVICES_ENABLE)
    			{
    				String  blockstatus = info.get(j).blockstatus;
    				if(null != blockstatus && blockstatus.equals("Block"))
    				{
    					int x0  = bmpx-8;
    					int y0  = bmpy+h/2-16;
    					int r0  = bmpx+w/2+16;
    					int b0  = bmpy+h+8;
    					Rect  dstrect2 = new Rect(x0, y0, r0, b0);
    					canvas.drawBitmap(bmp_blockflag, null, dstrect2, null);
    				}
    			}
    			
    		}else
    		{
    			
    			
    			canvas.drawBitmap(info.get(j).bmp,bmpx,bmpy, null);
    			if(m_devicesblockstatus == GenieGlobalDefines.BLOCKDEVICES_ENABLE)
    			{
    				String  blockstatus = info.get(j).blockstatus;
    				if(null != blockstatus && blockstatus.equals("Block"))
    				{
    					
    					
    					int x0  = bmpx;
    					int y0  = bmpy+h/2-8;
    					int r0  = bmpx+w/2+8;
    					int b0  = bmpy+h;
    					Rect  dstrect2 = new Rect(x0, y0, r0, b0);
    					canvas.drawBitmap(bmp_blockflag, null, dstrect2, null);
    				}
    			}
    			
    		}
			//mPaint.setColor(Color.RED);
			//mPaint.setStyle(Style.FILL);
		    //mPaint.setColor(getResources().getColor(R.color.blackcolor));
		    //canvas.drawRect(bmpx, (bmpy+h),(bmpx+w),(bmpy+h+20), mPaint); //
		    
		    mPaint.setColor(Color.BLACK);
		    if(GenieGlobalDefines.Device_Internet != info.get(j).type)
		    {
		    	String name = info.get(j).name;
		    
		    	if(name.length() > 10)
		    	{
		    		name = name.substring(0, 9);
		    		name = name+"...";
		    	}
		    	if(getWidth()>GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE){
		    		float rate=4 *(getHeight()>getWidth()?(getWidth()/320):(getWidth()/480));
		    		mPaint.setTextSize(rate);
		    	}
		    	canvas.drawText(name, bmpx, (bmpy+h+10), mPaint);
		    }

		    System.out.println("~~~~~~~~~~~!!!"+info.get(j).Ip);
			if(info.get(j).isturbo&&ScanDeviceService.discovered_services.contains(info.get(j).Ip)){
				
				int x0  = bmpx+w+16-turbo.getWidth();
				int y0  = bmpy;
				int r0  = bmpx+w+16;
				int b0  = bmpy+turbo.getHeight();
				//вСиосроб
				Rect  dstrect2 = new Rect(x0, y0, r0, b0);
				canvas.drawBitmap(turbo, null, dstrect2, null);
			}
    	}
    	
    	int rout = info.size()-1;
    	
    	x = info.get(rout).x;
		y = info.get(rout).y;
		w = info.get(rout).w;
		h = info.get(rout).h;
		
		int bmpx = info.get(rout).bmpx;
		int bmpy = info.get(rout).bmpy;
		
		//mPaint.setColor(getResources().getColor(R.color.blackcolor));
		//mPaint.setStyle(Style.FILL);
		//canvas.drawRect(x, y,(x+w),(y+h+15), mPaint); //О©╫О©╫О©╫ф╬О©╫О©╫О©╫
	    
		
		if(info.get(rout).clicked)
		{
//			if(null != info.get(rout).clickbmp)
//			{
//				canvas.drawBitmap(info.get(rout).clickbmp,bmpx,bmpy, null);
//			}else
//			{
//				canvas.drawBitmap(info.get(rout).bmp,bmpx,bmpy, null);
//			}
			
			Rect  dstrect = new Rect(bmpx-8, bmpy-8, bmpx+w+8, bmpy+h+8);
			
			canvas.drawBitmap(info.get(rout).bmp, null, dstrect, null);
		}else
		{
			canvas.drawBitmap(info.get(rout).bmp,bmpx,bmpy, null);
		}
		
	    //canvas.drawBitmap(info.get(rout).bmp,bmpx,bmpy, null);
	    
		//mPaint.setColor(Color.RED);
		//mPaint.setStyle(Style.FILL);
	    
	    //canvas.drawRect(x, (y+h),(x+w),(y+h+20), mPaint); //
	    
	    //mPaint.setColor(Color.BLACK);
	    //canvas.drawText(info.get(rout).name, x, (y+h+10), mPaint);
    	
	    
		//
		//mPaint = new Paint();
		//mPaint.setColor(Color.RED);
//		mPaint.setStyle(Style.FILL); //
//		canvas.drawRect(10, 10, 100, 100, mPaint); //
//		
//		mPaint.setColor(Color.BLUE);
//		canvas.drawText("", 10, 120, mPaint);
	}
	
	
	private static Path makePathDash() {
        Path p = new Path();
        p.moveTo(4, 0);
        p.lineTo(0, -4);
        p.lineTo(8, -4);
        p.lineTo(12, 0);
        p.lineTo(8, 4);
        p.lineTo(0, 4);
        return p;
    }
}


