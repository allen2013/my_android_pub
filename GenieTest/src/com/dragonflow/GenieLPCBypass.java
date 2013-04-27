package com.dragonflow;

import java.util.ArrayList;
import java.util.List;

import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;

public class GenieLPCBypass extends Activity {
	
	private ListView lv_username_bypass;
	private TextView lpc_bypass_status;
//	private Button bt_lpc_bypass_logout;
	private LinearLayout linearLayout01,linearLayout02;
	private AlertDialog dialog = null;
	
	
	
	private GenieRequest m_SettingRequest = null;
	private RequestReceiver m_RequestReceiver = null;
	
	private String m_loginpassword = null;
	private String[] openDNSuserNames = null;
	
	private String dNSMasqDeviceID = null;
//	private String myDeviceID  = null;
	private String BypassAccountName = null;
	//是否切换路由
	private boolean isChangeRouter=false;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Display d = getWindowManager().getDefaultDisplay();
		final int woindow_w = d.getWidth();
		final int woindow_h = d.getHeight();
		
		GenieDebug.error("onCreate", "onCreate --woindow_w == "+woindow_w);
    	
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE)
		{
			setTheme(R.style.bigactivityTitlebarNoSearch);
		}else
		{
			setTheme(R.style.activityTitlebarNoSearch); //qicheng.ai add
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE|Window.FEATURE_CUSTOM_TITLE); 
    	//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.lpcbypassaccount);
		if(woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE && woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE)
		{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar_big);
		}else
		{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		}
		
		Intent intent = getIntent();
		
		
		RegisterBroadcastReceiver();
		initWidget();
		if(GenieLPCmanage.openDNSUserName!=null){
			
			dNSMasqDeviceID = intent.getStringExtra("dNSMasqDeviceID");
			InitTitleView(false);
			setExistChildUserName(GenieLPCmanage.openDNSUserName);//当前登陆的用户
		}else{
//			GetDNSMasqDeviceID();//11
			String BypassuserNames = intent.getStringExtra("openDNSuserNames");
			dNSMasqDeviceID = intent.getStringExtra("dNSMasqDeviceID");
			openDNSuserNames = getString(BypassuserNames);
			InitTitleView(true);
			if(openDNSuserNames!=null){
				setNotExistChildUserName(openDNSuserNames);
			}
			
		}
		
		//注销LPC管理界面广播接收器
		GenieLPCmanage.getLPCInstance().UnRegisterBroadcastReceiver();
		
		
		
	}
	
	public void InitTitleView(final boolean status)
	{
		Button  back = (Button)findViewById(R.id.back);
		Button  about = (Button)findViewById(R.id.about);
		
		if(status){
			about.setText(R.string.refresh);
		}else{
			about.setText(R.string.logout);
		}
		
		
		TextView title = (TextView)findViewById(R.id.netgeartitle);
		

        title.setText(R.string.lpcbypassaccount);
		
		back.setBackgroundResource(R.drawable.title_bt_bj);
		about.setBackgroundResource(R.drawable.title_bt_bj);
		
		back.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GenieLPCBypass.this.onBackPressed();
			}
		});
		
		about.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(status){
					GetDNSMasqDeviceID();//1
				}else{
					//注销
					doReqDeviceIDForDeleteMac();
					//DeleteMacAddress();
				}
			}
		});
		
		back.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�     
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //锟斤拷为抬锟斤拷时锟斤拷图片      
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		

		
		about.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //锟斤拷锟轿拷锟斤拷锟绞憋拷谋锟斤拷锟酵计�     
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //锟斤拷为抬锟斤拷时锟斤拷图片      
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		
//	
	}
	
	private void initWidget(){
		lv_username_bypass = (ListView) findViewById(R.id.lv_username_bypass);
		lpc_bypass_status = (TextView) findViewById(R.id.lpc_bypass_status);
//		bt_lpc_bypass_logout = (Button) findViewById(R.id.bt_lpc_bypass_logout);
		linearLayout01 = (LinearLayout) findViewById(R.id.linearLayout01);
		linearLayout02 = (LinearLayout) findViewById(R.id.linearLayout02);
		lv_username_bypass.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(openDNSuserNames!=null){
					showLoginDialog(openDNSuserNames[arg2]);
				}
			}
		});
		
//		bt_lpc_bypass_logout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(GenieLPCmanage.openDNSUserName!=null){
//					
//				}
//			}
//		});
		
	}
	
	//获取默认的设备ID
	private void GetDNSMasqDeviceID()
	{
		
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
    	
		 m_loginpassword = GetLoginPassword();
		 
		 
		 GenieRequestInfo test = new GenieRequestInfo();
		 test.aRequestLable="GenieLPCBypass";
		 test.aSoapType = GenieGlobalDefines.ESoapRequestVerityUser;
		 test.aServer = "ParentalControl";
		 test.aMethod = "Authenticate";
		 test.aNeedwrap = false;
		 test.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
		 test.aNeedParser=false;
		 test.aTimeout = 20000;
		 test.aElement = new ArrayList<String>();

		 
		 if(!GenieRequest.m_SmartNetWork)
    	{
			 test.aElement.add("NewUsername");
			 test.aElement.add("admin");
			 test.aElement.add("NewPassword");
			 test.aElement.add(m_loginpassword);
    	}
		 
//		 else
//    	{
//    		if(null != GenieRequest.m_SmartInfo)
//    		{
//    		    String serial = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).serial;
//    		    if(serial != null)
//    		    {
//    		    	test.aElement.add("NewUsername");
//    		    	test.aElement.add(GetSmartNetworkRouterUsername(serial));
//    		    	test.aElement.add("NewPassword");
//    		    	test.aElement.add(GetSmartNetworkRouterPassword(serial));
//    		    }
//    		}
//    	}
    	
    	requestinfo.add(test);
    	
    	//查询设备ID
    	GenieRequestInfo test1 = new GenieRequestInfo();
    	test1.aRequestLable="GenieLPCBypass";
    	test1.aSoapType = GenieGlobalDefines.ESoapRequestDeviceID;
    	test1.aServer = "ParentalControl";
    	test1.aMethod = "GetDNSMasqDeviceID";
    	test1.aNeedwrap = true;
    	test1.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
    	test1.aNeedParser=true;
    	test1.aTimeout = 25000;
    	test1.aElement = new ArrayList<String>();
    	test1.aElement.add("NewMACAddress");
    	test1.aElement.add("default");
	  	requestinfo.add(test1);

		 
    	GenieRequestInfo test2 = new GenieRequestInfo();
    	test2.aRequestLable="GenieLPCBypass";
    	test2.aSoapType = GenieGlobalDefines.ESoapDNSMasqDeviceID;
    	test2.aServer = GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS;
    	test2.aMethod = GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceID;
    	test2.aNeedwrap = true;
    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
    	test2.aNeedParser=true;
    	test2.aTimeout = 20000;
    	test2.aElement = new ArrayList<String>();
    	test2.aElement.add("NewMACAddress");
    	test2.aElement.add("default");
    	
    	requestinfo.add(test2);

    	StopRequest();
    	m_SettingRequest = new GenieRequest(GenieLPCBypass.this,requestinfo);
    	//String title = getResources().getString(R.string.loading);
		
    	m_SettingRequest.SetProgressInfo(true, true);
    	m_SettingRequest.SetProgressText(null, null);
    	m_SettingRequest.Start();
	}
	
	//获取自己设备ID
	private void GetMyDNSMasqDeviceID()
	{
		
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		 
    	GenieRequestInfo test2 = new GenieRequestInfo();
    	test2.aRequestLable="GenieLPCBypass";
    	test2.aSoapType = GenieGlobalDefines.ESoapMyDNSMasqDeviceID;
    	test2.aServer = GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS;
    	test2.aMethod = GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceID;
    	test2.aNeedwrap = true;
    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
    	test2.aNeedParser=true;
    	test2.aTimeout = 20000;
    	test2.aElement = new ArrayList<String>();
    	test2.aElement.add("NewMACAddress");
    	test2.aElement.add(getMACAddress());
    	
    	requestinfo.add(test2);

    	StopRequest();
    	m_SettingRequest = new GenieRequest(GenieLPCBypass.this,requestinfo);
    	//String title = getResources().getString(R.string.loading);
		
    	m_SettingRequest.SetProgressInfo(true, true);
    	m_SettingRequest.SetProgressText(null, null);
    	m_SettingRequest.Start();
	}
	
	//getMACAddress
	public String getMACAddress(){
		WifiManager  WIFI = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = WIFI.getConnectionInfo();
		String mac = info.getMacAddress();
		GenieDebug.error("getMACAddress:", "MACAddress: "+mac);
		String macStr = mac.replaceAll(":", "");
		return macStr;
	}
	
	//获取自己设备绑定的用户
	private void GetUsersForChildMyDeviceID(String myDeviceId)
	{
		if(myDeviceId == null){
			return;
		}
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		 
    	GenieRequestInfo test2 = new GenieRequestInfo();
    	test2.aRequestLable="GenieLPCBypass";
    	test2.aOpenDNSType = GenieGlobalDefines.ESoapDeviceChildUserName;
    	
    	test2.aMethod = "device_child_username_get";
    	
//    	test2.aServer = GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS;
//    	test2.aMethod = GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceID;

	  	test2.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
    	test2.aNeedParser=true;
    	test2.aTimeout = 20000;
    	test2.aElement = new ArrayList<String>();
    	test2.aElement.add("api_key");
    	test2.aElement.add(GenieRequest.API_KEY);
    	test2.aElement.add("method");
    	test2.aElement.add("device_child_username_get");
    	test2.aElement.add("device_id");
    	test2.aElement.add(myDeviceId);
    	
    	requestinfo.add(test2);

    	StopRequest();
    	m_SettingRequest = new GenieRequest(GenieLPCBypass.this,requestinfo);
    	//String title = getResources().getString(R.string.loading);
		
    	m_SettingRequest.SetProgressInfo(true, true);
    	m_SettingRequest.SetProgressText(null, null);
    	m_SettingRequest.Start();
	}
	
	
	//获取设备所有的用户
	private void GetUsersForDeviceID(String deviceId)
	{
		if(deviceId == null){
			return;
		}
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		 
    	GenieRequestInfo test2 = new GenieRequestInfo();
    	test2.aRequestLable="GenieLPCBypass";
    	test2.aOpenDNSType = GenieGlobalDefines.ESoapDeviceUserName;
    	
    	test2.aMethod = "device_children_get";
    	
//    	test2.aServer = GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS;
//    	test2.aMethod = GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceID;

	  	test2.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
    	test2.aNeedParser=true;
    	test2.aTimeout = 20000;
    	test2.aElement = new ArrayList<String>();
    	test2.aElement.add("api_key");
    	test2.aElement.add(GenieRequest.API_KEY);
    	test2.aElement.add("method");
    	test2.aElement.add("device_children_get");
    	test2.aElement.add("parent_device_id");
    	test2.aElement.add(deviceId);
    	
    	requestinfo.add(test2);

    	StopRequest();
    	m_SettingRequest = new GenieRequest(GenieLPCBypass.this,requestinfo);
    	//String title = getResources().getString(R.string.loading);
    	String message = getResources().getString(R.string.getbypass);
		
    	m_SettingRequest.SetProgressInfo(true, true);
    	m_SettingRequest.SetProgressText(null, message);
    	m_SettingRequest.Start();
	}
	
	
	//登陆子账号
	private void loginBypassAccount(String DeviceID, String userName, String passWord)
	{
		if(DeviceID==null||userName == null||passWord==null){
			return;
		}
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
		
		m_loginpassword = GetLoginPassword();
		 
		 
		 GenieRequestInfo test = new GenieRequestInfo();
		 test.aRequestLable="GenieLPCBypass";
		 test.aSoapType = GenieGlobalDefines.ESoapRequestVerityUser;
		 test.aServer = "ParentalControl";
		 test.aMethod = "Authenticate";
		 test.aNeedwrap = false;
		 test.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
		 test.aNeedParser=false;
		 test.aTimeout = 20000;
		 test.aElement = new ArrayList<String>();

		 
		 if(!GenieRequest.m_SmartNetWork)
    	{
			 test.aElement.add("NewUsername");
			 test.aElement.add("admin");
			 test.aElement.add("NewPassword");
			 test.aElement.add(m_loginpassword);
    	}
		 
//		 else
//    	{
//    		if(null != GenieRequest.m_SmartInfo)
//    		{
//    		    String serial = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).serial;
//    		    if(serial != null)
//    		    {
//    		    	test.aElement.add("NewUsername");
//    		    	test.aElement.add(GetSmartNetworkRouterUsername(serial));
//    		    	test.aElement.add("NewPassword");
//    		    	test.aElement.add(GetSmartNetworkRouterPassword(serial));
//    		    }
//    		}
//    	}
    	
    	requestinfo.add(test);

		 
    	GenieRequestInfo test2 = new GenieRequestInfo();
    	test2.aRequestLable="GenieLPCBypass";
    	test2.aOpenDNSType = GenieGlobalDefines.ESoapLoginBypassAccount;
    	
    	test2.aMethod = "device_child_get";
    	
//    	test2.aServer = GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS;
//    	test2.aMethod = GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceID;

	  	test2.aRequestType=GenieGlobalDefines.RequestActionType.OpenDNS;
    	test2.aNeedParser=true;
    	test2.aTimeout = 20000;
    	test2.aElement = new ArrayList<String>();
    	test2.aElement.add("api_key");
    	test2.aElement.add(GenieRequest.API_KEY);
    	test2.aElement.add("method");
    	test2.aElement.add("device_child_get");
    	test2.aElement.add("parent_device_id");
    	test2.aElement.add(DeviceID);
    	test2.aElement.add("device_username");
    	test2.aElement.add(userName);
    	test2.aElement.add("device_password");
    	test2.aElement.add(passWord);
    	
    	requestinfo.add(test2);
    	cancelDialog();
    	StopRequest();
    	m_SettingRequest = new GenieRequest(GenieLPCBypass.this,requestinfo);
    	//String title = getResources().getString(R.string.loading);
    	String message = getResources().getString(R.string.loggingbypass);
		
    	m_SettingRequest.SetProgressInfo(true, true);
    	m_SettingRequest.SetProgressText(null, message);
    	m_SettingRequest.Start();
	}
	
	//
	private void SetDNSMasqDeviceID(String loginChildDeviceId)
	{
		
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		 
    	GenieRequestInfo test2 = new GenieRequestInfo();
    	test2.aRequestLable="GenieLPCBypass";
    	test2.aSoapType = GenieGlobalDefines.ESoapSetDNSMasqDeviceID;
    	test2.aServer = GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS;
    	test2.aMethod = GenieGlobalDefines.DICTIONARY_KEY_LPC_SetDeviceID;
    	test2.aNeedwrap = true;
    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
    	test2.aNeedParser=true;
    	test2.aTimeout = 20000;
    	test2.aElement = new ArrayList<String>();
    	test2.aElement.add("NewMACAddress");
    	test2.aElement.add(getMACAddress());
    	test2.aElement.add("NewDeviceID");
    	test2.aElement.add(loginChildDeviceId);
    	
    	requestinfo.add(test2);

    	StopRequest();
    	m_SettingRequest = new GenieRequest(GenieLPCBypass.this,requestinfo);
    	//String title = getResources().getString(R.string.loading);
		
    	m_SettingRequest.SetProgressInfo(true, true);
    	m_SettingRequest.SetProgressText(null, null);
    	m_SettingRequest.Start();
	}
	
	//注销本次登陆
	private void DeleteMacAddress()
	{
		
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
		
		m_loginpassword = GetLoginPassword();
		 
		 
		 GenieRequestInfo test = new GenieRequestInfo();
		 test.aRequestLable="GenieLPCBypass";
		 test.aSoapType = GenieGlobalDefines.ESoapRequestVerityUser;
		 test.aServer = "ParentalControl";
		 test.aMethod = "Authenticate";
		 test.aNeedwrap = false;
		 test.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
		 test.aNeedParser=false;
		 test.aTimeout = 20000;
		 test.aElement = new ArrayList<String>();

		 
		 if(!GenieRequest.m_SmartNetWork)
    	{
			 test.aElement.add("NewUsername");
			 test.aElement.add("admin");
			 test.aElement.add("NewPassword");
			 test.aElement.add(m_loginpassword);
    	}
		 
//		 else
//    	{
//    		if(null != GenieRequest.m_SmartInfo)
//    		{
//    		    String serial = GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).serial;
//    		    if(serial != null)
//    		    {
//    		    	test.aElement.add("NewUsername");
//    		    	test.aElement.add(GetSmartNetworkRouterUsername(serial));
//    		    	test.aElement.add("NewPassword");
//    		    	test.aElement.add(GetSmartNetworkRouterPassword(serial));
//    		    }
//    		}
//    	}
    	
    	requestinfo.add(test);
    	
    	
    	//查询设备ID
//    	GenieRequestInfo test1 = new GenieRequestInfo();
//    	test1.aRequestLable="GenieLPCBypass";
//    	test1.aSoapType = GenieGlobalDefines.ESoapRequestDeviceID;
//    	test1.aServer = "ParentalControl";
//    	test1.aMethod = "GetDNSMasqDeviceID";
//    	test1.aNeedwrap = true;
//    	test1.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
//    	test1.aNeedParser=true;
//    	test1.aTimeout = 25000;
//    	test1.aElement = new ArrayList<String>();
//    	test1.aElement.add("NewMACAddress");
//    	test1.aElement.add("default");
//	  	requestinfo.add(test1);

		 
    	GenieRequestInfo test2 = new GenieRequestInfo();
    	test2.aRequestLable="GenieLPCBypass";
    	test2.aSoapType = GenieGlobalDefines.ESoapDeleteMacAddress;
    	test2.aServer = GenieGlobalDefines.DICTIONARY_KEY_PC_STATUS;
    	test2.aMethod = GenieGlobalDefines.DICTIONARY_KEY_LPC_DeleteMACAddress;
    	test2.aNeedwrap = true;
    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
    	test2.aNeedParser=true;
    	test2.aTimeout = 20000;
    	test2.aElement = new ArrayList<String>();
    	test2.aElement.add("NewMACAddress");
    	test2.aElement.add(getMACAddress());
    	
    	requestinfo.add(test2);

    	StopRequest();
    	m_SettingRequest = new GenieRequest(GenieLPCBypass.this,requestinfo);
    	//String title = getResources().getString(R.string.loading);
    	String message = getResources().getString(R.string.exitbypass);
		
    	m_SettingRequest.SetProgressInfo(true, true);
    	m_SettingRequest.SetProgressText(null, message);
    	m_SettingRequest.Start();
	}
	
	/**
	 * 先获取设备ID判断是否切换路由，再注销
	 */
	private void doReqDeviceIDForDeleteMac(){
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
		
		m_loginpassword = GetLoginPassword();
		 
		 
		 GenieRequestInfo test = new GenieRequestInfo();
		 test.aRequestLable="GenieLPCBypass";
		 test.aSoapType = GenieGlobalDefines.ESoapRequestVerityUser;
		 test.aServer = "ParentalControl";
		 test.aMethod = "Authenticate";
		 test.aNeedwrap = false;
		 test.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
		 test.aNeedParser=false;
		 test.aTimeout = 20000;
		 test.aElement = new ArrayList<String>();

		 
		 if(!GenieRequest.m_SmartNetWork)
		 {
			 test.aElement.add("NewUsername");
			 test.aElement.add("admin");
			 test.aElement.add("NewPassword");
			 test.aElement.add(m_loginpassword);
		 }
		 

   	
		 requestinfo.add(test);
		
		//查询设备ID
    	GenieRequestInfo test1 = new GenieRequestInfo();
    	test1.aRequestLable="GenieLPCBypass";
    	test1.aActionLable="OnlyRequestDeviceIDForDeleteMac";
    	test1.aSoapType = GenieGlobalDefines.ESoapRequestDeviceID;
    	test1.aServer = "ParentalControl";
    	test1.aMethod = "GetDNSMasqDeviceID";
    	test1.aNeedwrap = true;
    	test1.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
    	test1.aNeedParser=true;
    	test1.aTimeout = 25000;
    	test1.aElement = new ArrayList<String>();
    	test1.aElement.add("NewMACAddress");
    	test1.aElement.add("default");
	  	requestinfo.add(test1);
	  	
	  	StopRequest();
    	m_SettingRequest = new GenieRequest(GenieLPCBypass.this,requestinfo);
    	String message = getResources().getString(R.string.wait);
		
    	m_SettingRequest.SetProgressInfo(true, true);
    	m_SettingRequest.SetProgressText(null, message);
    	m_SettingRequest.Start();
		
	}
	
	private void StopRequest()
    {
  	  if(null != m_SettingRequest)
  	  {
  		m_SettingRequest.Stop();
  		m_SettingRequest = null;
  	  }  	
    }
	
	public String GetLoginPassword()
	{
		GenieDebug.error("debug","GetLoginPassword ");
		SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SETTING_INFO, 0); 
		if(settings != null)
		{
		     String password = settings.getString(GenieGlobalDefines.PASSWORD, "password");

		     GenieDebug.error("debug","SaveLoginPassword settings != null password = "+password);
		     return password;
		}else
		{
			GenieDebug.error("debug","SaveLoginPassword settings == null return password  ");
			return "password";
		}
	}
	
	public static String[] getString(String str){
		if(str==null){
			return null;
		}
		List<String> userName = new ArrayList<String>();
		String split = "\",\"";
		String begin = "[\"";
		String end = "\"]";
		int length = str.length();
		String[] splitStr =str.split(split);
		for (int i = 0; i < splitStr.length; i++) {
			
			if(splitStr[i].indexOf(begin)!=-1){
				splitStr[i] = splitStr[i].substring(begin.length(), splitStr[i].length());
				if(splitStr[i].indexOf(end)!=-1){
					splitStr[i] = splitStr[i].substring(0, splitStr[i].length()-end.length());
				}
			}else if(splitStr[i].indexOf(end)!=-1){
				splitStr[i] = splitStr[i].substring(0, splitStr[i].length()-end.length());
			}else{
				splitStr[i] = splitStr[i];
			}
			GenieDebug.error("getString", splitStr[i]);
		}
		return splitStr;
	}
	
	private void setExistChildUserName(String userName){
		if(userName!=null&&lpc_bypass_status!=null&&linearLayout01!=null&&linearLayout02!=null){
//			bt_lpc_bypass_logout.setText("Logout");
			linearLayout01.setVisibility(View.VISIBLE);
			linearLayout02.setVisibility(View.GONE);
			String lpcbypassuser = getResources().getString(R.string.lpcbypassuser).toString();
			lpc_bypass_status.setText(lpcbypassuser+" "+userName);
		}
	}
	
	private void setNotExistChildUserName(String[] userNames){
		GenieLPCmanage.openDNSUserName = null;
		saveOpenDNSUserName(GenieLPCmanage.openDNSUserName);
		
		if(userNames!=null&&lv_username_bypass!=null&&linearLayout01!=null&&linearLayout02!=null){
			linearLayout01.setVisibility(View.GONE);
			linearLayout02.setVisibility(View.VISIBLE);
			lv_username_bypass.setAdapter(new UserAdapter(this, userNames));
		}
		
		
	}
	
	
	private void  showLoginDialog(final String userName){
		AlertDialog.Builder loginDialog = new AlertDialog.Builder(this);
		loginDialog.setTitle(R.string.login);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setPadding(10, 5, 10, 5);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		
		TextView tvUserName= new TextView(this);
		tvUserName.setPadding(0, 2, 0, 2);
		tvUserName.setTextSize(16);
		tvUserName.setText(R.string.login_username);
		EditText etUserName = new EditText(this);
		etUserName.setText(userName);
		etUserName.setEnabled(false);
		
		TextView tvPassword = new TextView(this);
		tvPassword.setPadding(0, 2, 0, 2);
		tvPassword.setTextSize(16);
		tvPassword.setText(R.string.login_password);
		final EditText etPassword = new EditText(this);
		etPassword.setSingleLine();
		etPassword. setTransformationMethod(PasswordTransformationMethod.getInstance());
		linearLayout.addView(tvUserName);
		linearLayout.addView(etUserName);
		linearLayout.addView(tvPassword);
		linearLayout.addView(etPassword);
		loginDialog.setView(linearLayout);
		
		loginDialog.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		
		loginDialog.setNegativeButton(R.string.login, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				cancelDialog();
				String password =  etPassword.getText().toString().trim();
//				if(password!=null&&!password.equals("")){
				GenieDebug.error("loginDialog=>", "dNSMasqDeviceID: "+dNSMasqDeviceID);
					loginBypassAccount(dNSMasqDeviceID, userName,password);
					BypassAccountName = userName;
//				}else{
//					Toast.makeText(GenieLPCBypass.this, "mimabunengweikong", 0).show();
//				}
				
			}
		});
		
//		dialog = 
			loginDialog.create().show();
//		dialog.show();
		
	}
	
	private void cancelDialog(){
		if(dialog!=null){
			dialog.cancel();
			dialog = null;
		}
	}
	
	

	private void UnRegisterBroadcastReceiver() {
		if (null != m_RequestReceiver) {
			unregisterReceiver(m_RequestReceiver);
			m_RequestReceiver = null;
		}
	}
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		UnRegisterBroadcastReceiver();
	}

	private void RegisterBroadcastReceiver() {
		UnRegisterBroadcastReceiver();

		m_RequestReceiver = new RequestReceiver();
		IntentFilter filter = new IntentFilter();// 鍒涘缓IntentFilter瀵硅薄
		filter.addAction(GenieGlobalDefines.REQUEST_ACTION_RET_BROADCAST);
		registerReceiver(m_RequestReceiver, filter);// 娉ㄥ唽Broadcast Receiver
	}

	
	
	private class RequestReceiver extends BroadcastReceiver {// 缁ф壙鑷狟roadcastReceiver鐨勫瓙绫�

		@Override
		public void onReceive(Context context, Intent intent) {// 閲嶅啓onReceive鏂规硶

			String actionlable = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_ACTION_LABLE);
			String lable = intent
					.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_LABLE);
			RequestActionType ActionType = (RequestActionType) intent
					.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_TYPE);
			RequestResultType aResultType = (RequestResultType) intent
					.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESULTTYPE);
			String aServer = intent
					.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SERVER);
			String aMethod = intent
					.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_METHOD);
			String aResponseCode = intent
					.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSECODE);
			int aHttpResponseCode = intent.getIntExtra(
					GenieGlobalDefines.REQUEST_ACTION_RET_HTTPRESPONSECODE, -2);
			int aHttpType = intent.getIntExtra(
					GenieGlobalDefines.REQUEST_ACTION_RET_HTTP_TYPE, -2);
			int aSoapType = intent.getIntExtra(
					GenieGlobalDefines.REQUEST_ACTION_RET_SOAP_TYPE, -2);
			int aOpenDNSType =  intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_OPENDNS_TYPE,-2);
			String aResponse = intent
					.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSE);

			GenieDebug.error("debug", "RequestReceiver onReceive lable ="
					+ lable);

			// if(aSoapType == GenieGlobalDefines.ESoapRequestGuestEnable
			// || aSoapType == GenieGlobalDefines.ESoapReqiestTrafficEnable)
			// {
			// aResponseCode = "401";
			// aResultType = RequestResultType.failed;
			// }

			if (lable != null && lable.equals("GenieLPCBypass")) {
				GenieDebug.error("debug", "RequestReceiver onReceive aServer ="
						+ aServer);
				GenieDebug.error("debug", "RequestReceiver onReceive aMethod ="
						+ aMethod);
				GenieDebug.error("debug",
						"RequestReceiver onReceive aResponseCode ="
								+ aResponseCode);
				GenieDebug.error("debug",
						"RequestReceiver onReceive aHttpResponseCode ="
								+ aHttpResponseCode);
				GenieDebug.error("debug",
						"RequestReceiver onReceive aResponse =" + aResponse);
				GenieDebug.error("debug",
						"RequestReceiver onReceive ActionType =" + ActionType);
				GenieDebug
						.error("debug",
								"RequestReceiver onReceive aResultType ="
										+ aResultType);
				GenieDebug
				.error("debug",
						"RequestReceiver onReceive aSoapType ="
								+ aSoapType);
				GenieDebug
				.error("debug",
						"RequestReceiver onReceive aOpenDNSType ="
								+ aOpenDNSType);

				if (null != ActionType && ActionType == RequestActionType.Soap) {
					if (null != aResultType) {
						if (aResultType == RequestResultType.Succes) {
							switch (aSoapType) {
							case GenieGlobalDefines.ESoapRequestDeviceID:{
								if(null!= actionlable && "OnlyRequestDeviceIDForDeleteMac".equals(actionlable)){
									String Deviceid = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_DEVICEID);
									//String dNSMasqDeviceID = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_NewDeviceID);
									if(!Deviceid.equals(dNSMasqDeviceID)){
										isChangeRouter=true;
										StopRequest();
										Toast.makeText(GenieLPCBypass.this, "Wifi has changed！", Toast.LENGTH_LONG).show();
										return;
									}else{
										isChangeRouter=false;
									}
									if(!isChangeRouter){
										DeleteMacAddress();
									}
								}else{
									
								}
								break;
							}
							case GenieGlobalDefines.ESoapDNSMasqDeviceID:
								
								String dNSMasqDeviceID = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_NewDeviceID);
								GenieDebug.error("debug",
										"RequestReceiver onReceive ESoapDNSMasqDeviceID ="
												+ dNSMasqDeviceID);
								
								GetUsersForDeviceID(dNSMasqDeviceID);//2

								break;
							case GenieGlobalDefines.ESoapMyDNSMasqDeviceID:
								
								String myDeviceID = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_MyNewDeviceID);
								GenieDebug.error("debug",
										"RequestReceiver onReceive ESoapMyDNSMasqDeviceID ="
												+ myDeviceID);
								GetUsersForChildMyDeviceID(myDeviceID);//4
								break;
							case GenieGlobalDefines.ESoapSetDNSMasqDeviceID:
								if(BypassAccountName!=null){
									GenieLPCmanage.openDNSUserName = BypassAccountName;
									//保存openDNSUserName
									saveOpenDNSUserName(GenieLPCmanage.openDNSUserName);
									
									StopRequest();
									GenieLPCBypass.this.finish();
								}
								break;
							case GenieGlobalDefines.ESoapDeleteMacAddress:
								GenieLPCmanage.openDNSUserName = null;
								saveOpenDNSUserName(GenieLPCmanage.openDNSUserName);
								
								StopRequest();
								GenieLPCBypass.this.finish();
								
								break;
							default:
								break;
							}
						} else {
							switch (aSoapType) {
							case GenieGlobalDefines.ESoapDNSMasqDeviceID:
								if(aResponseCode.equals("401")){
									showToast(R.string.NetWorkConnection);
								}
								break;
							case GenieGlobalDefines.ESoapMyDNSMasqDeviceID:
								break;
							case GenieGlobalDefines.ESoapSetDNSMasqDeviceID:
								showToast(R.string.loginfailed);
								break;
							case GenieGlobalDefines.ESoapDeleteMacAddress:
								showToast(R.string.logoutfailed);
								break;
							case GenieGlobalDefines.ESoapRequestVerityUser:
								StopRequest();
								showToast(R.string.NetWorkConnection);
								break;
							default:
								break;
							}
						}
					}
				}else if(null != ActionType && ActionType == RequestActionType.OpenDNS){
					if (null != aResultType) {
						if (aResultType == RequestResultType.Succes) {
							switch (aOpenDNSType) {
							case GenieGlobalDefines.ESoapDeviceChildUserName:
								
								String childDeviceIDUserName = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_ChildDeviceIDUserName);
								GenieDebug.error("debug",
										"RequestReceiver onReceive ESoapDeviceChildUserName ="
												+ childDeviceIDUserName);
								GenieLPCmanage.openDNSUserName = childDeviceIDUserName;
								StopRequest();
								GenieLPCBypass.this.finish();
								
								
								break;
							case GenieGlobalDefines.ESoapDeviceUserName:
								
								String deviceIDUserName = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_DeviceIDUserName);
								GenieDebug.error("debug",
										"RequestReceiver onReceive ESoapDeviceUserName ="
												+ deviceIDUserName);
								openDNSuserNames = getString(deviceIDUserName);
								
								GetMyDNSMasqDeviceID();//3
								break;
							case GenieGlobalDefines.ESoapLoginBypassAccount:
								
								String loginBypassAccount = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_LoginBypassAccount);
								GenieDebug.error("debug",
										"RequestReceiver onReceive ESoapLoginBypassAccount ="
												+ loginBypassAccount);
								SetDNSMasqDeviceID(loginBypassAccount);
								break;
							
							default:
								break;
							}
						} else {
							switch (aOpenDNSType) {
							case GenieGlobalDefines.ESoapDeviceChildUserName:
								String error = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR);
								GenieDebug.error("debug",
										"RequestReceiver onReceive ESoapDeviceChildUserName ="
												+ error);
								if(error.equals("4003")){
									//没有用户登陆
									if(openDNSuserNames!=null){
										setNotExistChildUserName(openDNSuserNames);
									}
								}
								break;
							case GenieGlobalDefines.ESoapDeviceUserName:
								String error1 = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR);
								GenieDebug.error("debug",
										"RequestReceiver onReceive ESoapDeviceUserName ="
												+ error1);
								if(error1.equals("4012")){
									//OpenDns中没有创建子账号
									showToast(R.string.NoCreateUserName);
								}
								break;
							case GenieGlobalDefines.ESoapLoginBypassAccount:
								String error2 = GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_OPENDNS_ERROR);
								GenieDebug.error("debug",
										"RequestReceiver onReceive ESoapLoginBypassAccount ="
												+ error2);
								if(error2.equals("3003")){
									//输入的密码错误
									showToast(R.string.loginBypassPasswordError);
									
								}
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
	
	
	public void showToast(int text) {
		Context context = getApplicationContext();
		String note = getResources().getString(text);
		Toast toast = Toast.makeText(this, note, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM, 0, 0);//Gravity.FILL_HORIZONTAL|
//		LinearLayout ll = new LinearLayout(context);
//		ll.setBackgroundColor(Color.BLACK);
//		ll.setOrientation(LinearLayout.VERTICAL);
//		TextView myTextView = new TextView(context);
//		myTextView.setTextColor(Color.RED);
//		myTextView.setText(note);
//		int lHeight = LinearLayout.LayoutParams.FILL_PARENT;
//		int lWidth = LinearLayout.LayoutParams.WRAP_CONTENT;
//		ll.addView(myTextView, new LinearLayout.LayoutParams(lHeight, lWidth));
//		ll.setPadding(40, 50, 0, 50);
//		toast.setView(ll);
		toast.show();
	}
	
	
	class UserAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		private String[] userNames;

		public UserAdapter(Context context,String[] userNames) {
			inflater = LayoutInflater.from(context);
			this.userNames = userNames;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return userNames.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return userNames[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View userLayout;
			ListViewWeight weight = new ListViewWeight();
			if(arg1 == null){
				userLayout = inflater.inflate(R.layout.bypassaccount_adapter, null);
			}else{
				userLayout = arg1;
			}
			weight.tvusername = (TextView) userLayout.findViewById(R.id.tv_username);
			
			weight.tvusername.setText(userNames[arg0]);
			return userLayout;
			
		}
		
		class ListViewWeight{
			TextView tvusername;
		}
		
	}
	
	/**
	 * 保存Bypass的用户名
	 * @param username
	 */
	private void saveOpenDNSUserName(String username){
		
		if(username!=null)
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_ChildDeviceIDUserName, username.trim());
		else
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_ChildDeviceIDUserName, username);
			
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		GenieLPCmanage.getLPCInstance().RegisterBroadcastReceiver();
	}

}
