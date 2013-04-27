package com.dragonflow;

import java.util.ArrayList;
import java.util.HashMap;

import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;
import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GenieAlarmActivity extends Activity implements Button.OnClickListener{

	public Button  ok = null;
	public Button  cancel = null;
	private ProgressDialog progressDialog = null;
	//private GenieSoap soapCommand = null;
	private GenieUserInfo userInfo = null;
	
	private Handler handler = new Handler()
	{   
        @Override   
        public void handleMessage(Message msg) 
        {   
        	GenieDebug.error("GenieAlarmActivity", "main handleMessage!! msg.what = "+msg.what);
        	switch(msg.what)
        	{
        	case GenieGlobalDefines.ESoapRequestfailure:
        		closeWaitingDialog();
        		ShowToast(false);
        		break ;
        	case GenieGlobalDefines.ESoapRequestsuccess:
        		closeWaitingDialog();
        		ShowToast(true);
        		GenieAlarmActivity_finish();
        		break ;	
        	}
        }
	};  
	
	private void GenieAlarmActivity_finish()
	{
		this.finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.alarmview);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_alert);

		

		ok = (Button)findViewById(R.id.button_ok);
		cancel = (Button)findViewById(R.id.button_cancel);
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
		
		
    	userInfo = GenieShareAPI.ReadDataFromFile(this, GenieGlobalDefines.USER_INFO_FILENAME);
    	
    	//userInfo.isSave = '0';  //test
    	
    	GenieDebug.error("GenieAlarmActivity", "onCreate --1--");
    	//soapCommand = new GenieSoap(this);
    	GenieDebug.error("GenieAlarmActivity", "onCreate --2--");
    	//soapCommand.initialDictionaryElements();
	}
	
	public void CancelAlarm()
	{
		if((GenieMainView.alarmManager != null) && (GenieMainView.pendingActivityIntent != null))
		{
			GenieMainView.alarmManager.cancel(GenieMainView.pendingActivityIntent);
			GenieMainView.alarmManager = null;
			GenieMainView.pendingActivityIntent = null;
		}	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.button_ok:			
			CancelAlarm();
			DeleteGuesstTime();
			DisableGuestAccess();			
			break;
		case R.id.button_cancel:
			CancelAlarm();
			//DeleteGuesstTime();
			GenieAlarmActivity_finish();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		//GenieMainView.alarmManager.cancel(GenieMainView.pendingActivityIntent);
	}
	
    public void sendMessage2UI(int msg)
    {
    	handler.sendEmptyMessage(msg);
    }
	
	public Boolean setGuestAccessDisabled()
	{
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
  		
	  	GenieRequestInfo request0 = new GenieRequestInfo();
	  	request0.aRequestLable="GenieAlarmActivity";
	  	request0.aSoapType = GenieGlobalDefines.ESoapRequestConfigGuestEnable;
	  	request0.aServer = "WLANConfiguration";
	  	request0.aMethod = "SetGuestAccessEnabled";
	  	request0.aNeedwrap = true;
	  	request0.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	  	request0.aNeedParser=false;
	  	request0.aTimeout = 25000;
	  	request0.aElement = new ArrayList<String>();
	  	request0.aElement.add(GenieGlobalDefines.DICTIONARY_KEY_GUEST_ABLE);
	  	request0.aElement.add("0");
	  	requestinfo.add(request0);
	  	
	  	
	  	m_RequestSetting = new GenieRequest(this,requestinfo);
	  	m_RequestSetting.SetProgressInfo(true, true);
	  	m_RequestSetting.Start();
	  	
		//Boolean Status = false;

		//soapCommand.configurationstarted();
		//Status = soapCommand.setGuestAccessEnabled();
    	//soapCommand.configurationfinished();
    	
    	//return Status;
    	return true;
	}
	
	public void DeleteGuesstTime()
	{
		HashMap<String,String> accesstime =  GenieSerializ.ReadMap(GenieAlarmActivity.this, "guestaccess");

		if(accesstime == null)
		{			
			accesstime = new HashMap<String,String>();
		}
		
		accesstime.put("GUESTABLE","0");
		accesstime.put("GUESTTIME",GenieGlobalDefines.NULL);
	 
	 	accesstime.put("GUESTSTARTTIME",GenieGlobalDefines.NULL);
	 
	 	accesstime.put("GUESTENDTIME",GenieGlobalDefines.NULL);
	 
	 	accesstime.put("GUESTTIMEMAC",GenieGlobalDefines.NULL);
		
	 	GenieSerializ.WriteMap(GenieAlarmActivity.this, accesstime, "guestaccess");
		
	}
	
	public void DisableGuestAccess()
	{
		GenieDebug.error("debug", "DisableGuestAccess --1--");
		setGuestAccessDisabled();
//	   showWaitingDialog();
//	   GenieDebug.error("debug", "DisableGuestAccess --1--");
//	    	
//	   new Thread()
//	   {
//		   public void run() 
//	    	{  
//			   
//	    		GenieDebug.error("debug", "DisableGuestAccess --run--");
//	    		
//
//	    		
//	    		if(setGuestAccessDisabled())
//	    		{
//	    			
//	    			HashMap<String,String> accesstime =  GenieSerializ.ReadMap(GenieAlarmActivity.this, "guestaccess");
//
//	        		if(accesstime == null)
//	        		{			
//	        			accesstime = new HashMap<String,String>();
//	        		}
//	        		
//	   			 	accesstime.put("GUESTABLE","0");
//	   			 	accesstime.put("GUESTTIME",GenieGlobalDefines.NULL);
//				 
//				 	accesstime.put("GUESTSTARTTIME",GenieGlobalDefines.NULL);
//				 
//				 	accesstime.put("GUESTENDTIME",GenieGlobalDefines.NULL);
//				 
//				 	accesstime.put("GUESTTIMEMAC",GenieGlobalDefines.NULL);
//	        		
//				 	GenieSerializ.WriteMap(GenieAlarmActivity.this, accesstime, "guestaccess");
//	        		
//	    			String temp = String.valueOf(0);
//	   			 	GenieDebug.error("DisableGuestAccess","disabletime = "+temp);
//	   			 	GenieShareAPI.strncpy(userInfo.disabletime, temp.toCharArray(),0,temp.length());
//   		 
//	   			 	GenieShareAPI.WriteData2File(userInfo,GenieAlarmActivity.this, GenieGlobalDefines.USER_INFO_FILENAME);
//	    			
//	   			 	sendMessage2UI(GenieGlobalDefines.ESoapRequestsuccess);    				
//	    		}else
//	    		{
//	    			sendMessage2UI(GenieGlobalDefines.ESoapRequestfailure);
//	    		}
//	    		
//	    	}
//	    }.start();  
	
	}
	
	
    public void showWaitingDialog()
    {
    	closeWaitingDialog();
//    	progressDialog = ProgressDialog.show(GenieMainView.this, "Loading...", "Please wait...", true, false);   
    	progressDialog =  ProgressDialog.show(this, "Loading...", "Please wait...", true, true);
    }
    public void closeWaitingDialog()
    {
    	if(progressDialog != null)
		{
			progressDialog.dismiss();
			progressDialog = null;
		}
    }
    
    public void ShowToast(Boolean status)
    {
//    	 toast = Toast.makeText(getApplicationContext(),
    	String str = null;
    	Toast toast = null;
    	
    	if(status)
    	{
    		str = getResources().getString(R.string.success);
    	}else
    	{
    		str = getResources().getString(R.string.failure);
    	}

    	
//    	 toast = Toast.makeText(this,
//    			 str, Toast.LENGTH_LONG);
//		   	   toast.setGravity(Gravity.CENTER, 0, 0);
//		   	   toast.show();
    }
    
	protected void onStart() {
		// TODO Auto-generated method stub
    	
		super.onStart();
					
		RegisterBroadcastReceiver();
	
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		StopRequest();
		UnRegisterBroadcastReceiver();
		
//		 if(null != m_WIFIBroadcast)
//	        {
//	        	m_WIFIBroadcast.UnRegisterBroadcastReceiver();
//	        	m_WIFIBroadcast = null;
//	        }
	}
	
	  private GenieRequest m_RequestSetting = null;
	
	  private void StopRequest()
	  {
		
		  if(null != m_RequestSetting)
		  {
			  m_RequestSetting.Stop();
			  m_RequestSetting = null;
		  }
	  }
	
	   private RequestReceiver m_RequestReceiver = null;
	    
		 private void UnRegisterBroadcastReceiver()
		 {
			 if(null != m_RequestReceiver)
				{
					unregisterReceiver(m_RequestReceiver);
					m_RequestReceiver = null;
				}
		 }

		 private void RegisterBroadcastReceiver()
		 {
			 UnRegisterBroadcastReceiver();
			 
				m_RequestReceiver = new RequestReceiver();
				IntentFilter filter = new IntentFilter();//鍒涘缓IntentFilter瀵硅薄
				filter.addAction(GenieGlobalDefines.REQUEST_ACTION_RET_BROADCAST);
				registerReceiver(m_RequestReceiver, filter);//娉ㄥ唽Broadcast Receiver
		 }
		 
		 
		  private class RequestReceiver extends BroadcastReceiver{//缁ф壙鑷狟roadcastReceiver鐨勫瓙绫�


		         @Override
			        public void onReceive(Context context, Intent intent) {//閲嶅啓onReceive鏂规硶
		     
		     		String lable = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_LABLE);
		     		RequestActionType ActionType = (RequestActionType) intent.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_TYPE);
		     		RequestResultType aResultType = (RequestResultType) intent.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESULTTYPE);
		     		String aServer = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SERVER);
		     		String aMethod = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_METHOD);
		     		String aResponseCode = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSECODE);
		     		int aHttpResponseCode = intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_HTTPRESPONSECODE,-2);
		     		int aHttpType =  intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_HTTP_TYPE,-2);
		     		int aSoapType =  intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SOAP_TYPE,-2);
		     		String aResponse = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSE);
		     	
		     		GenieDebug.error("debug", "RequestReceiver onReceive lable ="+lable);
	     		
		     		
//		     		if(aSoapType == GenieGlobalDefines.ESoapRequestGuestEnable
//		     				|| aSoapType == GenieGlobalDefines.ESoapReqiestTrafficEnable)
//		     		{
//		     			aResponseCode = "401";
//		     			aResultType = RequestResultType.failed;
//		     		}
		     		
		     		if(lable != null && lable.equals("GenieAlarmActivity"))
		     		{
			     		GenieDebug.error("debug", "RequestReceiver onReceive aServer ="+aServer);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aMethod ="+aMethod);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aResponseCode ="+aResponseCode);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aHttpResponseCode ="+aHttpResponseCode);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aResponse ="+aResponse);
			     		GenieDebug.error("debug", "RequestReceiver onReceive ActionType ="+ActionType);
			     		GenieDebug.error("debug", "RequestReceiver onReceive aResultType ="+aResultType);

			     		if(null != ActionType && ActionType == RequestActionType.Soap)
			     		{
			     			if(null != aResultType)
			     			{
			     				if(aResultType == RequestResultType.Succes)
			     				{
				     				switch(aSoapType)
				     				{
				     				case GenieGlobalDefines.ESoapRequestConfigGuestEnable:		
				     					GenieAlarmActivity_finish();
				     					break;
				     				default:
				     					break;
				     				}
				     				//fillData2List();
			     				}else
			     				{
			     					switch(aSoapType)
				     				{
				     				case GenieGlobalDefines.ESoapRequestConfigGuestEnable:
				     					break;				     				
				     				default:
				     					break;
				     				}
			     				}
			     			}
			     		}
		     		}

		         }
		  }
}
