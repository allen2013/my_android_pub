package com.dragonflow;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class GenieSlideService extends Service {

	//public native int initSlideServiceFieldAndMethod();
	//public native int OnRenderSlidePlay();
	
	
	final public static int MESSAGE_PLAYFAIL = 505;
	final public static int MESSAGE_PLAYSUCCESS = 506;
	
	private Timer m_SlideTimer = null;
	private TimerTask m_SlideTask = null;
	
	
	final public static int m_Speed_i[] = {
		10000,
		5000,
		2000,
	};
	
	private int m_SlideSpeed = 5000;
	
	
	
	
	
	
	private Handler handler = new Handler()
	{   
        public void handleMessage(Message msg) 
        {   
        	String error = null;
        	GenieDebug.error("GenieSlideService", "GenieSlideService handleMessage msg.what = "+msg.what);
        	switch(msg.what)
        	{
        	case MESSAGE_PLAYFAIL:
        		
        		if(GenieDlnaStatus.m_OnSlidePlay)
        		{
        			StartSlidePlay();
        		}
        		break;
        	case MESSAGE_PLAYSUCCESS:
        		if(GenieDlnaStatus.m_OnSlidePlay)
        		{
        			StartSlidePlay();
        		}
        		break;
        	}
        	

        	
			sendbroad(msg.what);
        }
	}; 
	
	
	public void sendbroad(int value)
	{
		Intent slide = new Intent(GenieGlobalDefines.SLIDE_ACTION_BROADCAST);
		slide.putExtra(GenieGlobalDefines.SLIDE_ACTION_RET, value);
		sendBroadcast(slide);
	}
	
	
	public void PlayFailed_SlideService()
	{
		sendMessage2UI(MESSAGE_PLAYFAIL);
	}
	
	
	public void PlaySuccess_SlideService()
	{
		sendMessage2UI(MESSAGE_PLAYSUCCESS);
	}
	
	 private void sendMessage2UI(int msg)
	 {
	   	handler.sendEmptyMessage(msg);
	 }
	
	 
	 
	 
	   private void CanclSlidePlay()
	   {
		   GenieDebug.error("debug","999999999999 CanclSlidePlay 0");
		   
			if(null != m_SlideTimer)
			{
				m_SlideTimer.cancel();
				m_SlideTimer = null;
			}
			if(null != m_SlideTask)
			{
				m_SlideTask = null;
			}
			
			//m_OnSlideModle = false;
			GenieDlnaStatus.m_OnSlidePlay = false;
	   }
	   
		private void StartSlidePlay()
		{		

			GenieDebug.error("debug","999999999999 StartSlidePlay 0");
			
			
			if(null != m_SlideTimer)
			{
				m_SlideTimer.cancel();
				m_SlideTimer = null;
			}
			if(null != m_SlideTask)
			{
				m_SlideTask = null;
			}
			
			
			m_SlideTimer  = new Timer(); 
			m_SlideTask = new TimerTask()
			{
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					GenieDebug.error("debug","999999999999 StartSlidePlay run OnRenderSlidePlay()");	
					//OnRenderSlidePlay();
					
				}
				
			};
			
			
			
//			int speed ;
//			if(m_SpeedIndex >= 0 && m_SpeedIndex <= 2)
//			{
//				speed = m_Speed_i[m_SpeedIndex];
//			}else
//			{
//				speed = m_Speed_i[1];
//			}
			
			
			m_SlideTimer.schedule(m_SlideTask,m_SlideSpeed);
			
			GenieDebug.error("debug","999999999999 StartSlidePlay 1");			
			
			//m_OnSlideModle = true; 
			GenieDlnaStatus.m_OnSlidePlay = true;
		}
	 
		
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		//initSlideServiceFieldAndMethod();
//		GenieDlnaStatus.m_OnSlidePlay = false;
//		m_SlideSpeed = m_Speed_i[1];
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
//		CanclSlidePlay();
//		
//		sendbroad(-1);
		
	}
	
	
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		int action = -1;
		
		action = intent.getIntExtra(GenieGlobalDefines.SLIDE_ACTION,-1);
		
		GenieDebug.error("GenieSlideService", "onStartCommand action ="+action);
		
		
//		if(action == 5)
//		{
//			Intent slide = new Intent(GenieGlobalDefines.SLIDE_ACTION_BROADCAST);
//			slide.putExtra(GenieGlobalDefines.SLIDE_ACTION_RET, 5555);
//			sendBroadcast(slide);
//		}
//		
//		if(action == 101)
//		{
//			Intent slide = new Intent(GenieGlobalDefines.SLIDE_ACTION_BROADCAST);
//			slide.putExtra(GenieGlobalDefines.SLIDE_ACTION_RET, 101101);
//			sendBroadcast(slide);
//		}
//		
//		if(action >= 10 && action < 13)
//		{
//			m_SlideSpeed = m_Speed_i[action - 10];
//			OnRenderSlidePlay();
//			StartSlidePlay();
//		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
