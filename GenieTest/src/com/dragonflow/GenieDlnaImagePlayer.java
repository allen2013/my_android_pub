package com.dragonflow;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import com.dragonflow.TouchView.OnTouchCallBack;
import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;
import com.ewm.ImagesBrowseActivity;









import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

public class GenieDlnaImagePlayer extends Activity implements AnimationListener {

    /** Constant used as menu item id for setting zoom control type */
    private static final int MENU_ID_ZOOM = 0;

    /** Constant used as menu item id for setting pan control type */
    private static final int MENU_ID_PAN = 1;

    /** Constant used as menu item id for resetting zoom state */
    private static final int MENU_ID_RESET = 2;



    /** Decoded bitmap image */
    private Bitmap mBitmap;


    
    private ZoomControls m_ZoomControls = null;
    
    
    private Boolean m_create = false;
    
	private Timer settimer = null;
	private TimerTask task = null;
	private static int tick ;
	
	
	public 	Button  m_back = null;
	public 	Button  m_about = null;
	
	
	
	
	private ViewScroll detail = null;
	private LinearLayout ll = null;
	private LinearLayout.LayoutParams parm = null;
   

    //public native int SetViewOnImagePlayer();
    //public native int initImagePlayerFieldAndMethod();
    
    //public native String GetVideoUrl();
    
    final private static int MESSAGE_LOADINGIMAGEEND = 51118;
    final private static int MESSAGE_ONREPLAY = 519;
    final private static int MESSAGE_ZOOMCONTROLS_HIDE = 520;
    
    
    final private static int  fadeOutTime = 1800;
   
    private  FadeTimer  fadetimer = null;
    private Animation animation;
    public AnimationListener animationListener = this;
    private static boolean m_picshow = false;
    
    private static boolean m_ispause = false;
    
    private RelativeLayout m_titler = null;
    

	private Handler handler = new Handler()
	{   
		public void handleMessage(Message msg) 
		{   
			String error = null;
			GenieDebug.error("handleMessage", "GenieDlnaImagePlayer handleMessage msg.what = "+msg.what);
			switch(msg.what)
			{
			case MESSAGE_LOADINGIMAGEEND:
				showimage();
				break;
			case GenieDlnaActionDefines.ACTION_RENDER_TOIMAGEREPLAY:
				ImageViewOnRePlay();
				break;
        	case GenieDlnaActionDefines.ACTION_RENDER_TOVIDEOPLAY:
        		ToVideoPlay();
        		break;
			case MESSAGE_ZOOMCONTROLS_HIDE:
				ZoomControlsHide();
				break;
			}
		}
	}; 
	
	
	private void StartDlnaAction(int action)
	{		
		
		GenieDebug.error("debug","9999df999999 StartDlnaAction action = "+action);
		
		Intent Dlna = new Intent("com.netgear.genie.GenieDlnaService");
		Dlna.putExtra(GenieGlobalDefines.DLNA_ACTION,action);
		startService(Dlna);
		GenieDebug.error("debug","9999df999999 StartDlnaAction end");
		
		

	}
	
		
		private class DLNAReceiver extends BroadcastReceiver{//缁ф壙鑷狟roadcastReceiver鐨勫瓙绫�


	        @Override
		        public void onReceive(Context context, Intent intent) {//閲嶅啓onReceive鏂规硶
	    
	       	 int action = -1;
	    		
	       	 	
	    		action = intent.getIntExtra(GenieGlobalDefines.DLNA_ACTION_RET,-1);
	    		
	    		GenieDebug.error("DLNAReceiver", "onReceive DLNA_ACTION_RET ="+action);
	    		sendMessage2UI(action);


	        }
	      }
		
		 private DLNAReceiver m_DLNAReceiver = null;

	
	
	public int RendererOnRePlay()
	{
		sendMessage2UI(MESSAGE_ONREPLAY);
		return 0;
	}
	
	private void ImageViewOnRePlay()
	{
		if(ll != null && detail != null && parm != null )
		{
	        if(null != mBitmap)
	        {
	        	mBitmap.recycle();
	        	mBitmap = null;
	        }
//			mBitmap =  GetUrlBitmap();
			ll.removeView(detail);
			detail = null;
			
			GetImageOnThread();
			
//			if(null != mBitmap)
//			{
//				detail = new ViewScroll(GenieDlnaImagePlayer.this, mBitmap,null);	
//				ll.addView(detail,parm);
//			}else
//			{
//				GenieDebug.error("debug","ImageViewOnRePlay null == mBitmap");
//			}
		}
		

	}
	
	
	public void CanclTimer()
	{
		if(null != settimer)
		{
			settimer.cancel();
			settimer = null;
		}
		if(null != task)
		{
			task = null;
		}
	}
	
	public void StartTimer()
	{
		
		CanclTimer();
		
		settimer  = new Timer(); 
		task = new TimerTask()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				GenieDebug.error("debug","$$--run ---0--- ");
				if(m_ZoomControls.isShown())
				{
					tick++;
					
					GenieDebug.error("debug","$$--run ---1--- tick = "+tick);
				
					if(tick == 3 )
					{
						if(m_ZoomControls.hasFocus())
						{
							tick = 0;
						}else
						{
							sendMessage2UI(MESSAGE_ZOOMCONTROLS_HIDE);
							//settimer.cancel();
						}
					}
				}
				
			}
			
		};
		settimer.schedule(task,1000,1000);
	}
	
	
	
	
	/**
	 * Resets the fade out timer to 0. Creating a new one if needed
	 */
	private void resetTimer() {
		// Only set the timer if we have a timeout of at least 1 millisecond
		if (fadeOutTime > 0) {
			// Check if we need to create a new timer
			if (fadetimer == null || fadetimer._run == false) {
				// Create and start a new timer
				fadetimer = new FadeTimer();
				fadetimer.execute();
			} else {
				// Reset the current tiemr to 0
				fadetimer.resetTimer();
			}
		}
	}

	/**
	 * Counts from 0 to the fade out time and animates the view away when
	 * reached
	 */
	private class FadeTimer extends AsyncTask<Void, Void, Void> {
		// The current count
		private int timer = 0;
		// If we are inside the timing loop
		private boolean _run = true;

		public void resetTimer() {
			timer = 0;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			while (_run) {
				try {
					// Wait for a millisecond
					Thread.sleep(1);
					// Increment the timer
					timer++;

					// Check if we've reached the fade out time
					if (timer == fadeOutTime) {
						// Stop running
						_run = false;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			animation = AnimationUtils.loadAnimation(getContext(),
					android.R.anim.fade_out);
			animation.setAnimationListener(animationListener);
			//startAnimation(animation);
			if(null != m_ZoomControls)
				m_ZoomControls.startAnimation(animation);
			if(null != m_titler)
	    	{
	    		m_titler.startAnimation(animation);
	    	}
		}
	}
	
	public Context getContext()
	{
		return this;
	}
	
	
	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if(null != m_ZoomControls)
			m_ZoomControls.setVisibility(View.GONE);
		if(null != m_titler)
    	{
    		m_titler.setVisibility(View.GONE);
    	}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	
	private  void ZoomControlsShow()
	{
		
		GenieDebug.error("debug","----GenieVideoPlay- ZoomControlsShow---0--- ");
		if(null != m_ZoomControls && null != m_titler)
		{
			GenieDebug.error("debug","----GenieVideoPlay- ZoomControlsShow---1--- ");
			
			m_ZoomControls.setVisibility(View.VISIBLE);			
			//m_ZoomControls.show();
			m_titler.setVisibility(View.VISIBLE);
					
			resetTimer();
		}
		
		
		
		
		
	}
	
	private  void ZoomControlsHide()
	{
		if(null != m_ZoomControls)
		{
			m_ZoomControls.setVisibility(View.GONE);
		}
	}
	


	public int  SendMssageToVideoPlay()
	{
		handler.sendEmptyMessage(GenieDlnaStatus.VIEW_MEDIAVIDEOPLAY);
		return 0;
	}

	private void ToVideoPlay()
	{
		GenieDebug.error("debug","imageplay ToVideoPlay");
		Intent intent = new Intent();
    	
		//intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		intent.setClass(GenieDlnaImagePlayer.this, GenieDlnaVideoPlay.class);
		startActivity(intent);
		this.finish();
	}
	
	private void sendMessage2UI(int msg)
	{
		
		handler.sendEmptyMessage(msg);
	}
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SLIDE_STOP);
		super.onBackPressed();
		
	}

	public void InitTitleView()
	{

		m_titler = (RelativeLayout)findViewById(R.id.titler);
    	//GenieDlnaTab.m_about.setText(R.string.refresh);
    	
		m_back = (Button)findViewById(R.id.back);
		m_about = (Button)findViewById(R.id.about);
		
    	m_back.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartDlnaAction(GenieDlnaActionDefines.ACTION_CONTROL_MEDIA_SLIDE_STOP);
				GenieDlnaImagePlayer.this.onBackPressed();
			}
		});
		
    	m_about.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		m_back.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                           v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		
		m_about.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		
		m_about.setVisibility(View.GONE);
	}
	
	private void GetImageOnThread()
	{
		ProgressBar temp = (ProgressBar)findViewById(R.id.imageloading);
		temp.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				mBitmap =  GetUrlBitmap();
				sendMessage2UI(MESSAGE_LOADINGIMAGEEND);
			}
		}).start();
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
	 GenieDebug.error("onConfigurationChanged", "onConfigurationChanged !!!!!!!!!");
	 GenieDebug.error("onConfigurationChanged", "newConfig.orientation = "+newConfig.orientation);
	 
	 if(mBitmap != null && null != detail && ll != null && parm != null)
	 {
		 GenieDebug.error("onConfigurationChanged", "onConfigurationChanged 2222222");
		 ll.removeView(detail);
		 detail = null;
		 detail = new ViewScroll(GenieDlnaImagePlayer.this, mBitmap,null);
		 detail.SetOnTouchCallBack(new OnTouchCallBack() {
				
				public void OnTouchAction(MotionEvent event) {
					// TODO Auto-generated method stub
					if(m_picshow)
			    	{
						if(null != m_ZoomControls)
				    		m_ZoomControls.setVisibility(View.VISIBLE);
						if(null != m_titler)
				    	{
				    		m_titler.setVisibility(View.VISIBLE);
				    	}
						resetTimer();
			    	}
				}
			});
		 ll.addView(detail,parm);
	 }

	 super.onConfigurationChanged(newConfig);
	}
	
	private void showimage()
	{
		if(null != mBitmap && ll != null && parm != null)
		{
			detail = new ViewScroll(GenieDlnaImagePlayer.this, mBitmap,null);	
			detail.SetOnTouchCallBack(new OnTouchCallBack() {
				
				public void OnTouchAction(MotionEvent event) {
					// TODO Auto-generated method stub
					if(m_picshow)
			    	{
						if(null != m_ZoomControls)
				    		m_ZoomControls.setVisibility(View.VISIBLE);
						if(null != m_titler)
				    	{
				    		m_titler.setVisibility(View.VISIBLE);
				    	}
						resetTimer();
			    	}
				}
			});
			ll.addView(detail,parm);
		}else
		{
			GenieDebug.error("debug","onCreate null == mBitmap");
		}
		ProgressBar temp = (ProgressBar)findViewById(R.id.imageloading);
		temp.setVisibility(View.GONE);
		
        StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_ONPLAYING);
        StartDlnaAction(GenieDlnaActionDefines.ACTION_RENDER_ONSTOPPED);
        
        m_picshow = true;
        resetTimer();
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Display d = getWindowManager().getDefaultDisplay();
		final int woindow_w = d.getWidth();
		final int woindow_h = d.getHeight();
		
		m_ispause = true;
		
		GenieDebug.error("onCreate", "geniedlnaimageplay onCreate --woindow_w == "+woindow_w);
    	
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE)
		{
			setTheme(R.style.bigactivityTitlebarNoSearch);
		}else
		{
			setTheme(R.style.activityTitlebarNoSearch); //qicheng.ai add
		}
        
        //setTheme(R.style.activityTitlebarNoSearch);//qicheng.ai add        
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.dlnaimageplayer2);
//        if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE)
//		{
//        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_big);
//		}else
//		{
//			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
//		}
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        
        InitTitleView();
        
       // SetViewOnImagePlayer();
       // initImagePlayerFieldAndMethod();
        m_create = true;
        m_picshow = false;

        //mZoomState = new ZoomState();
        
        

        //mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        
        //mBitmap =  GetUrlBitmap();
        
        //mZoomListener = new SimpleZoomListener();
        //mZoomListener.setZoomState(mZoomState);

        //mZoomView = (ImageZoomView)findViewById(R.id.zoomview);
        
        //mZoomView.setZoomState(mZoomState);
        //mZoomView.setImage(mBitmap);
        //mZoomView.setOnTouchListener(mZoomListener);
        
        
        ll = (LinearLayout) findViewById(R.id.twill);
		parm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        //parm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        
		detail = null;
		
        m_ZoomControls = (ZoomControls)findViewById(R.id.zoomcontrols);
        m_ZoomControls.setOnZoomInClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != detail)
				{
					detail.onZoomIn();
				}
			}
		});
        m_ZoomControls.setOnZoomOutClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != detail)
				{
					detail.onZoomOut();
				}
			}
		});
        ZoomControlsShow();
        
        GetImageOnThread();
        
        GenieDlnaActionDefines.m_PlayViewLoading = false;
        
       // StartTimer();

        //resetZoomState();

    }
    
    
    public Bitmap GetUrlBitmap()
    {
    	m_picshow = false;
    	
    	runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		    	if(null != m_ZoomControls)
		    		m_ZoomControls.setVisibility(View.VISIBLE);
		    	if(null != m_titler)
		    	{
		    		m_titler.setVisibility(View.VISIBLE);
		    	}
			}
		});

    	
    	String imageurl = null;
    	 try {     
    		 	GenieDebug.error("debug","GetUrlBitmap 0");
    		 	
    		 	if(null == GenieDlnaActionDefines.m_playurl)
    		 	{
    		 		return null;
    		 	}
    		 	imageurl = GenieDlnaActionDefines.m_playurl;
    		 	
    		 	GenieDebug.error("debug","GetUrlBitmap imageurl ="+imageurl);
    		 	
    		    URL url = new URL(imageurl);     
    		    URLConnection conn;     
    	        conn = url.openConnection();     
    	        conn.connect();     
    	        InputStream in = conn.getInputStream();     
    	        
    	      byte[] bt=getBytes(in); //
    	      
    	        if(null != mBitmap)
    	        {
    	        	mBitmap.recycle();
    	        	mBitmap = null;
    	        }
    	        System.gc();
    	        
    	        BitmapFactory.Options opt = new BitmapFactory.Options(); 
    	        //opt.inTempStorage = new byte[16*1024]; 
    	        
    	        opt.inJustDecodeBounds = true;
        		BitmapFactory.decodeByteArray(bt,0,bt.length, opt);
        		opt.inJustDecodeBounds = false;
        		
    			Display d = getWindowManager().getDefaultDisplay();
    			final int w = d.getWidth();
    			final int h = d.getHeight();
    			

    			GenieDebug.error("GetUrlBitmap", " GetUrlBitmap w="+w);
    			GenieDebug.error("GetUrlBitmap", " GetUrlBitmap h="+h);
    			
    			final int SampleSize =  w>h?h:w;
    			
    			GenieDebug.error("GetUrlBitmap", " GetUrlBitmap SampleSize="+SampleSize);

    			if(opt.outWidth > SampleSize || opt.outHeight > SampleSize)
    			{	
	        		if (opt.outWidth > opt.outHeight) {
	        			opt.inSampleSize = opt.outWidth / SampleSize;
	        		} else {
	        			opt.inSampleSize = opt.outHeight / SampleSize;
	        		}
    			}
    	      mBitmap =BitmapFactory.decodeByteArray(bt,0,bt.length,opt);
    	        
    	      //  mBitmap = BitmapFactory.decodeStream(in); //濡傛灉閲囩敤杩欑瑙ｇ爜鏂瑰紡鍦ㄤ綆鐗堟湰鐨凙PI涓婁細鍑虹幇瑙ｇ爜闂

    	             
    	        in.close(); 
    	        opt = null;
    	        
    	        bt = null;
    	        
    	        System.gc();
    	       } catch (MalformedURLException e) 
    	       {
    	    	     e.printStackTrace();     
    	    	     return null;
    	       } catch (Exception e)
    	       {
    	    	     e.printStackTrace();
    	    	     return null;
    	       }catch(Error e)
    	       {
    	    	   e.printStackTrace();
    	    	   return null;
    	       }finally{

    	       }
    	       
    	       
    	       
    	     return   mBitmap;
    }
    
    
    private byte[] getBytes(InputStream is) throws IOException {

    	ByteArrayOutputStream baos =new ByteArrayOutputStream();
    	byte[] b =new byte[1024];
    	int len =0;

    	while ((len = is.read(b, 0, 1024)) !=-1) 
    	{
    	baos.write(b, 0, len);
    	baos.flush();
    	}
    	byte[] bytes = baos.toByteArray();
    	return bytes;
    	}

    
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
    	
    	GenieDebug.error("debug","----GenieVideoPlay- onTouchEvent------ ");
    	super.onTouchEvent(event);
    	
    	ZoomControlsShow();
		return false;
	}
	public void onResume() {
    	
    	//SetViewOnImagePlayer();
    	
		if(ImagesBrowseActivity.imagescolse){
			ImagesBrowseActivity.imagescolse=false;
			onBackPressed();
		}
		
    	GenieDebug.error("debug","----GenieVideoPlay- onResume------ ");
    	if(m_create)
    	{
    		m_create = false;
    	}else
    	{
    		//initImagePlayerFieldAndMethod();
    		
    	}
    	
    	GenieDlnaActionDefines.m_isImagePlayView = true;

    	super.onResume();
    	
    	m_ispause = false;
    }
	
	
	 private void UnRegisterBroadcastReceiver()
	 {
		 if(null != m_DLNAReceiver)
			{
				unregisterReceiver(m_DLNAReceiver);
				m_DLNAReceiver = null;
			}
	 }

	 private void RegisterBroadcastReceiver()
	 {
		 UnRegisterBroadcastReceiver();
		 
			m_DLNAReceiver = new DLNAReceiver();
			IntentFilter filter = new IntentFilter();//鍒涘缓IntentFilter瀵硅薄
			filter.addAction(GenieGlobalDefines.DLNA_ACTION_BROADCAST);
			registerReceiver(m_DLNAReceiver, filter);//娉ㄥ唽Broadcast Receiver
	 }
	
	 @Override
		protected void onStart() {
			// TODO Auto-generated method stub
	    	
			super.onStart();
						
			RegisterBroadcastReceiver();
		
		}


    @Override
    public void onDestroy() {
        super.onDestroy();
        
        
        GenieDlnaActionDefines.m_isImagePlayView = false;
        if(null != mBitmap)
        {
        	mBitmap.recycle();
        	mBitmap = null;
        }
        CanclTimer();
        UnRegisterBroadcastReceiver();
        m_ispause = true;
        System.gc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_ZOOM, 0, R.string.menu_zoom);
        menu.add(Menu.NONE, MENU_ID_PAN, 1, R.string.menu_pan);
        menu.add(Menu.NONE, MENU_ID_RESET, 2, R.string.menu_reset);
        return super.onCreateOptionsMenu(menu);
    }

	




}