package com.dragonflow;


import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class GenieDlnaOption extends Activity {

	//public native int SetViewOnOption();
	//public native int initOptionFieldAndMethod();
	//public native void ResetFileSystem(String path,String servername,String server);
	private Boolean m_create = false;
	
	//private EditText m_pathET = null;
	//private Button m_selectBT = null;
	
	
	final public static int MESSAGE_SHOWWAITDIALOG = 502;
	final public static int MESSAGE_CLOSEWAITDIALOG = 503;
	
	public ProgressDialog progressDialog = null;
	
	public ProgressDialog m_waitingDialog = null;
	
	private CheckBox m_ServerSwitch = null;
	private CheckBox m_RenderSwitch = null;
	private Button m_dlnareset = null;
	
	private Button m_RefreshShareContent = null;
	

	
	

	private static boolean m_ispause = false;
	
	
	private Handler handler = new Handler()
	{   
        public void handleMessage(Message msg) 
        {   
        	String error = null;
        	GenieDebug.error("handleMessage", "GenieDlnaOption handleMessage msg.what = "+msg.what);
        	switch(msg.what)
        	{
        	case GenieDlnaActionDefines.ACTION_RENDER_SETPLAYING:
        		if(null != GenieDlnaActionDefines.m_playurl)
        		{
        			if(GenieDlnaActionDefines.m_playStyle == 1 ||
        					GenieDlnaActionDefines.m_playStyle == 2)
        			{
        				ToVideoPlay();
        			}else if(GenieDlnaActionDefines.m_playStyle == 3)
        			{
        				ToImagePlayer();
        			}
        		}
        		break;
        	case GenieDlnaActionDefines.ACTION_RENDER_TOVIDEOPLAY:
        		ToVideoPlay();
        		break;
        	case GenieDlnaActionDefines.ACTION_RENDER_TOIMAGEPLAY:
        		ToImagePlayer();
        		break;	
        	case GenieDlnaActionDefines.ACTION_DLNA_OPTOIN_SHOW:
        		showOptionWaiting();
        		break;
        	case GenieDlnaActionDefines.ACTION_DLNA_OPTOIN_CANCLE:
        		cancleOptionWaiting();
        		break;	
        	case MESSAGE_SHOWWAITDIALOG:
        		showWaitingDialog2();
        		return ;
        	case MESSAGE_CLOSEWAITDIALOG:
        		closeWaitingDilalog2();
        		return ;	
        	}
        }
	}; 
	
	
	
	private int  SendMssageToImagePlayer()
	{
		handler.sendEmptyMessage(GenieDlnaStatus.VIEW_IMAGEPLAYER);
		return 0;
	}
	private void ToImagePlayer()
	{
		GenieDebug.error("handleMessage", "GenieDlnaOption ToImagePlayer");
		
		if(m_ispause)
			return ;
		
		Intent intent = new Intent();
    	
		//intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		intent.setClass(GenieDlnaOption.this, GenieDlnaImagePlayer.class);
		startActivity(intent);
	}
	
	
	private int  SendMssageToVideoPlay()
	{
		handler.sendEmptyMessage(GenieDlnaStatus.VIEW_MEDIAVIDEOPLAY);
		return 0;
	}
	
	
	
	
	
	
	
	private void ToVideoPlay()
	{
		
		GenieDebug.error("handleMessage", "GenieDlnaOption ToVideoPlay");
		if(m_ispause)
			return ;
		Intent intent = new Intent();
		        	
        //intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
		intent.setClass(GenieDlnaOption.this, GenieDlnaVideoPlay.class);
		startActivity(intent);
	}
	
	private void cancleOptionWaiting()
	{
		if(null != m_waitingDialog)
		{
			if(m_waitingDialog.isShowing())
				m_waitingDialog.dismiss();
			m_waitingDialog = null;
		}
	}
	private void showOptionWaiting()
	{
		cancleOptionWaiting();
		m_waitingDialog =new  ProgressDialog(this);
		
		m_waitingDialog.setCancelable(false);
		m_waitingDialog.setMessage(getResources().getString(R.string.pleasewait)+"...");
		m_waitingDialog.show();
		
	}
	
	
	
	
	
	
	private void showWaitingDialog2()
	{
		closeWaitingDilalog2();
		
		progressDialog = ProgressDialog.show(this, "Loading...", "Please wait...", true, true);   
	}
	 
	
	
	private void showWaitingDialog()
    {

		sendMessage2UI(MESSAGE_SHOWWAITDIALOG);
		return ;
		
	}
	
	
	private void closeWaitingDilalog2()
	{
		if(progressDialog != null)
		{
			progressDialog.dismiss();
			progressDialog = null;
		}   
	}
	
	private void closeWaitingDialog()
    {
		sendMessage2UI(MESSAGE_CLOSEWAITDIALOG);

    }
	
    public void sendMessage2UI(int msg)
    {
    	GenieDebug.error("debug","sendMessage2UI msg = "+msg);
    	handler.sendEmptyMessage(msg);
    }

	
    
    
	private void OnClickApply()
	{
		
		
		Dialog ApplyDialog = new AlertDialog.Builder(this)
        .setIcon(R.drawable.icon)
        .setTitle(R.string.dlna_option)
        .setMessage(getResources().getString(R.string.apply)+"?")
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked Yes so do some stuff */
            	showWaitingDialog2();
               new Thread()
  			   {
  				   public void run() 
  			    	{   
  			    		GenieDebug.error("debug", " StartServer --run--");
  			    		
  			    		//showWaitingDialog();			    		
  			    		//ResetFileSystem(GenieDlnaStatus.m_SharePath,GenieDlnaStatus.m_Sharename,GenieDlnaStatus.m_Servername);    
  				    	closeWaitingDialog();
  			    	}
  			    }.start();  
            	
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked No so do some stuff */
            }
        })
       .create();
		//ApplyDialog.getContext().setTheme(android.R.style.Theme_NoTitleBar);
    	
    	
		
		
		ApplyDialog.show();
	}
	
	
	
	
	   @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		   
			GenieDlnaTab.tabHost.setCurrentTabByTag(GenieDlnaTab.TAB_SERVER);
			GenieDlnaTab.m_radio0.setChecked(true);
		
	}
	public void InitTitleView()
		{
	    	if(GenieDlnaTab.m_about == null || GenieDlnaTab.m_back == null || GenieDlnaTab.m_dlnatitle == null)
	    	{
	    		return ;    		
	    	}
	    	GenieDlnaTab.m_dlnatitle.setText(R.string.optiontitle);
	    	
	    	GenieDlnaTab.m_about.setText(R.string.apply);
	    	GenieDlnaTab.m_about.setVisibility(View.GONE);
	    	
	    	GenieDlnaTab.m_back.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//GenieDlnaOption.this.onBackPressed();
					GenieDlnaTab.tabHost.setCurrentTabByTag(GenieDlnaTab.TAB_SERVER);
					GenieDlnaTab.m_radio0.setChecked(true);
				}
			});
			
	    	GenieDlnaTab.m_about.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//OnClickApply();
				}
			});
			
			GenieDlnaTab.m_back.setOnTouchListener(new OnTouchListener(){      
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
			
			GenieDlnaTab.m_about.setOnTouchListener(new OnTouchListener(){      
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
		}
	
	private void StartDlnaAction(int action)
	{		
		
		GenieDebug.error("debug","9999df999999 StartDlnaAction action = "+action);
		
		Intent Dlna = new Intent("com.netgear.genie.GenieDlnaService");
		Dlna.putExtra(GenieGlobalDefines.DLNA_ACTION,action);
		startService(Dlna);
		GenieDebug.error("debug","9999df999999 StartDlnaAction end");
		
		

	}
	
	public void InitView()
	{
		/*
		m_pathET = (EditText)findViewById(R.id.shartpath);
		m_selectBT = (Button)findViewById(R.id.seleltbutton);
		
		GenieDebug.error("InitView ","GenieDlnaStatus.m_SharePath = "+GenieDlnaStatus.m_SharePath);
		
		m_pathET.setText(GenieDlnaStatus.m_SharePath);
		
		m_selectBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
		    	
				//intent.putExtra(GenieGlobalDefines.LIST_TYPE_INDEX, GenieGlobalDefines.ACTIVE_FUNCTION_WIRELESS);
				intent.setClass(GenieDlnaOption.this, FileManager.class);
				startActivity(intent);
				
			}
		});
		*/
		m_dlnareset = (Button)findViewById(R.id.dlnareset);
		
		m_dlnareset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartDlnaAction(GenieDlnaActionDefines.ACTION_DLNA_RESET);
			}
		});
		
		
		m_RefreshShareContent = (Button)findViewById(R.id.folderfresh);
		
		m_RefreshShareContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartDlnaAction(GenieDlnaActionDefines.ACTION_DLNA_REFRESHSHARECONTENT);
			}
		});
		
		
		
		
		
		
		
		m_ServerSwitch  = (CheckBox)findViewById(R.id.switchserver);
		
		m_ServerSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				GenieDebug.error("debug", "onCheckedChanged isChecked ="+isChecked);
				if(null != GenieDlnaActionDefines.m_DLNAConfig)
				{
					if(isChecked)
					{
						GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch = "1";
					}else
					{
						GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch = "0";
					}
					
					GenieDebug.error("debug","onCheckedChanged isChecked  ServerSwitch ="+GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch);
					StartDlnaAction(GenieDlnaActionDefines.ACTION_DLNA_SWITCHSERVER);
				}
			}
		});
		
		
		
		m_RenderSwitch  = (CheckBox)findViewById(R.id.switchrender);
		
		m_RenderSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				GenieDebug.error("debug", "onCheckedChanged isChecked ="+isChecked);
				if(null != GenieDlnaActionDefines.m_DLNAConfig)
				{
					if(isChecked)
					{
						GenieDlnaActionDefines.m_DLNAConfig.RenderSwitch = "1";
					}else
					{
						GenieDlnaActionDefines.m_DLNAConfig.RenderSwitch = "0";
					}
					GenieDebug.error("debug","onCheckedChanged isChecked  RenderSwitch ="+GenieDlnaActionDefines.m_DLNAConfig.RenderSwitch);
					StartDlnaAction(GenieDlnaActionDefines.ACTION_DLNA_SWITCHRENDER);
				}
			}
		});
		
		if(null != GenieDlnaActionDefines.m_DLNAConfig)
		{
			if(GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch.equals("1"))
			{
				m_ServerSwitch.setChecked(true);
			}else
			{
				m_ServerSwitch.setChecked(false);
			}
		
			if(GenieDlnaActionDefines.m_DLNAConfig.RenderSwitch.equals("1"))		
			{
				m_RenderSwitch.setChecked(true);
			}else
			{
				m_RenderSwitch.setChecked(false);
			}
		}	
		
	}
	
	 @Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			m_ispause = true;
		}
		 
	
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		m_ispause = true;
		UnRegisterBroadcastReceiver();
		StartDlnaAction(GenieDlnaActionDefines.ACTION_DLNA_SAVECONFIG);
		GenieDebug.error("debug","-888888888888888888888----onDestroy end------ ");
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
        //setTheme(R.style.activityTitlebarNoSearch);//qicheng.ai add        
        //requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
    	//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
    	setContentView(R.layout.dlna_option);
    	
    	//GenieDlnaStatus.m_thisview = this;
    	m_ispause = true;
    	InitTitleView();
    	InitView();
        
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
        
    	
		
		m_create = true;
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		GenieDlnaStatus.m_thisview = this;
		
		
//		if(null != m_pathET)
//		{
//			m_pathET.setText(GenieDlnaStatus.m_SharePath);
//		}
		
		GenieDebug.error("debug","----GenieDlnaRender- onResume------ ");
		if(m_create)
		{
			m_create = false;
		}else
		{
			
		}
		m_ispause = false;
		InitTitleView();
		
		if(null != GenieDlnaActionDefines.m_DLNAConfig)
		{
			if(GenieDlnaActionDefines.m_DLNAConfig.ServerSwitch.equals("1"))
			{
				m_ServerSwitch.setChecked(true);
			}else
			{
				m_ServerSwitch.setChecked(false);
			}
		
			if(GenieDlnaActionDefines.m_DLNAConfig.RenderSwitch.equals("1"))		
			{
				m_RenderSwitch.setChecked(true);
			}else
			{
				m_RenderSwitch.setChecked(false);
			}
		}	
		//RenderOnRefresh();
		
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
    	
		super.onStart();
					
		RegisterBroadcastReceiver();
	
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
	 
	 
	    private class DLNAReceiver extends BroadcastReceiver{//缁ф壙鑷狟roadcastReceiver鐨勫瓙绫�


	         @Override
		        public void onReceive(Context context, Intent intent) {//閲嶅啓onReceive鏂规硶
	     
	        	 int action = -1;
	        	 
		     		if(m_ispause)
		    			return;
	     		
	     		action = intent.getIntExtra(GenieGlobalDefines.DLNA_ACTION_RET,-1);
	     		
	     		GenieDebug.error("DLNAReceiver", "GenieDlnaRender onReceive DLNA_ACTION_RET ="+action);
	     		
	     		sendMessage2UI(action);
	     		
	         }
	       }
	    
	    private DLNAReceiver m_DLNAReceiver = null;
	
}
