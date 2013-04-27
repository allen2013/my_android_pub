package com.dragonflow;


import java.util.ArrayList;

import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;
import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GenieLoginDialog extends Dialog {

		private ImageView icon = null;
		private TextView  title = null;
		
		private TextView  waitinfo = null;
		private TextView  alartinfo = null;
		private TextView  notnetgearrouter = null;
		
		private TextView  bad_password = null;
		private Button	 login_ok = null;
		private Button	 login_cancel = null;
		
		private CheckBox	 login_box = null;
		
		private ProgressBar	 login_ProgressBar = null;
		
		private EditText   Login_username = null;
		
		private EditText   Login_password = null;
		private boolean   m_inputpassword = false;
		
		public int   m_notnetgearrouter = -1;
		
		private GenieRequest m_GetCurrentSeting = null;
		private GenieRequest m_LoginRequest = null;
		
		public interface OnBackPressed {
			
			void OnBack(GenieLoginDialog dialog);
		}
		
		public interface OnCanclePressed {
			
			void OnCancle(GenieLoginDialog dialog);
		}
		
		public interface OnLoginSucces {
			
			void OnSucces(GenieLoginDialog dialog);
		}
		
		private Context m_Context = null;
		OnBackPressed m_OnBackPressed = null;
		OnLoginSucces m_OnLoginSucces = null;
		
		OnCanclePressed m_OnCanclePressed = null;
		
		public GenieUserInfo userInfo = null;
		
		public GenieLoginDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			
			m_Context = context;
			
			userInfo = GenieShareAPI.ReadDataFromFile(context, GenieGlobalDefines.USER_INFO_FILENAME);
			
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.login_dialog); 

			
			m_OnBackPressed = null;
			icon = (ImageView)findViewById(R.id.login_icon);
			title = (TextView)findViewById(R.id.login_title);
			icon.setBackgroundResource(android.R.drawable.ic_dialog_alert);
			title.setText(R.string.login);
			
			login_box = (CheckBox)findViewById(R.id.remeberpassword);
			
			login_ok = (Button)findViewById(R.id.login_ok);
			login_cancel = (Button)findViewById(R.id.login_cancel);
			
			waitinfo = (TextView)findViewById(R.id.wait_text);
			
			notnetgearrouter = (TextView)findViewById(R.id.nonetgearrouter);
			
			alartinfo = (TextView)findViewById(R.id.alertinfo);
			bad_password = (TextView)findViewById(R.id.bad_password);
			
			login_ProgressBar = (ProgressBar)findViewById(R.id.progress); 
			
			Login_username = (EditText)findViewById(R.id.username);
			Login_password = (EditText)findViewById(R.id.password);
			
			m_loginpassword = GetLoginPassword();
			
			//userInfo.isSave = '0';  //test
				
			if(userInfo.isSave == '1')
			{
				login_box.setChecked(true);
				Login_password.setText(m_loginpassword);
			}
			else
			{
				login_box.setChecked(false);
				Login_password.setText("");
			}
			
			login_box.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(login_box.isChecked())
				{
					userInfo.isSave = '1';
				}else
				{
					userInfo.isSave = '0';
				}
			}
		});
			
    		if(Login_password.getText().toString().length() > 0)
    		{
    			m_inputpassword = true;
    		}else
    		{
    			m_inputpassword = false;
    		}
    		Login_password.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					EditText temp = (EditText)findViewById(R.id.password);
					if(temp.getText().toString().length() > 0)
					{
						m_inputpassword = true;	
					}else
					{
						m_inputpassword = false;
					}
					if(m_inputpassword )
					{
						GetLoginDialoglogin_ok().setEnabled(true);
					}else
					{
						GetLoginDialoglogin_ok().setEnabled(false);
					}
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
    		
    		
    		
    		if(m_inputpassword )
			{
				GetLoginDialoglogin_ok().setEnabled(true);
			}else
			{
				GetLoginDialoglogin_ok().setEnabled(false);
			}
    		
    		login_ok.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ShowLoginProgressBar(View.VISIBLE);
					ShowLoginWaitinfo(View.VISIBLE);
					ShowLoginBad_password(View.GONE);
					SetOkButtonEnabled(false);
					SetEditPassWordEnabled(false);
					GetCurrentSetting();
				}
			});
    		
    		login_cancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(m_OnCanclePressed != null)
					{
						m_OnCanclePressed.OnCancle(GenieLoginDialog.this);
					}
					GenieLoginDialog.this.dismiss();
				}
			});
    		
    		m_notnetgearrouter = -1;

		}
		

		
		private String m_loginpassword = null;
		
		public String GetLoginPassword()
		{
			GenieDebug.error("debug","settings GetLoginPassword ");
			SharedPreferences settings = m_Context.getSharedPreferences(GenieGlobalDefines.SETTING_INFO, 0); 
			if(settings != null)
			{
			     String password = settings.getString(GenieGlobalDefines.PASSWORD, "password");

			     GenieDebug.error("debug","settings GetLoginPassword settings != null password = "+password);
			     return password;
			}else
			{
				GenieDebug.error("debug","settings GetLoginPassword settings == null return password  ");
				return "password";
			}
		}
		
		public void SaveLoginPassword(String password)
		{
			GenieDebug.error("debug","settings SaveLoginPassword password = "+password);
			 SharedPreferences settings = m_Context.getSharedPreferences(GenieGlobalDefines.SETTING_INFO, 0);
		     if(null != settings)
		     {
		    	 GenieDebug.error("debug","settings SaveLoginPassword null != settings");
		    	 settings.edit().putString(GenieGlobalDefines.PASSWORD, password).commit();
		     }

		}
		
		@Override
		public void show() {
			// TODO Auto-generated method stub
			super.show();
			
			GenieDebug.error("debug", "GenieLoginDialog show()");
			
			RegisterBroadcastReceiver();
			
			if(IsConnectWIFI())
			{
				if(userInfo.isSave == '1')
				{
					ShowLoginProgressBar(View.VISIBLE);
					ShowLoginWaitinfo(View.VISIBLE);
					ShowLoginBad_password(View.GONE);
					SetOkButtonEnabled(false);
					SetEditPassWordEnabled(false);
					GetCurrentSetting();
				}else
				{
					ShowLoginProgressBar(View.INVISIBLE);
					ShowLoginWaitinfo(View.GONE);
					ShowLoginBad_password(View.GONE);
					SetOkButtonEnabled(true);
					SetEditPassWordEnabled(true);
				}
			}else
			{
				ShowLoginProgressBar(View.INVISIBLE);
				ShowLoginWaitinfo(View.GONE);
				ShowLoginBad_password(View.GONE);
				SetOkButtonEnabled(false);
				SetEditPassWordEnabled(false);
				ShowLoginAlartInfoVisible(View.VISIBLE,R.string.noconnectrouter);
			}
		}


		@Override
		public void dismiss() {
			// TODO Auto-generated method stub
			super.dismiss();
			
			if(m_LoginRequest != null)
			{
				m_LoginRequest.Stop();
				m_LoginRequest = null;
			}
			if(m_GetCurrentSeting != null)
			{
				m_GetCurrentSeting.Stop();
				m_GetCurrentSeting = null;
			}
			GenieDebug.error("debug", "GenieLoginDialog dismiss()");
			UnRegisterBroadcastReceiver();
		}



		public ImageView  GetLoginDialogIcon()
		{
			return icon ;
		}
		public TextView  GetLoginDialogTitle()
		{
			return title ;
		}
		
		public TextView  GetLoginDialogWaitinfo()
		{
			return waitinfo;
		}
		public TextView  GetLoginDialogAlartinfo()
		{
			return alartinfo ;
		}
		public TextView  GetLoginDialogbad_password()
		{
			return bad_password ;
		}
		
		
		private Button	 GetLoginDialoglogin_ok()
		{
			return login_ok;
		}
		public Button	 GetLoginDialogIconlogin_cancel()
		{
			return login_cancel;
		}
		
		public CheckBox	 GetLoginDialogCheckBox()
		{
			return login_box ;
		}
		
		public ProgressBar	 GetLoginDialogProgressBar()
		{
			return login_ProgressBar;
		}
		
		public EditText  GetLoginDialogUsername()
		{
			return Login_username;
		}
		
		public EditText   GetLoginDialogPassWord()
		{
			return Login_password;
		}

		private void ShowLoginWaitinfo(int  show)
		{
			
			if(null == waitinfo)
				return;
			
			waitinfo.setVisibility(show);
			
		}
		
		private void ShowLoginProgressBar(int  show)
		{			
			if(null == login_ProgressBar)
				return;
			
			login_ProgressBar.setVisibility(show);
			
		}
		
		public void ShowLoginBad_password(int  show)
		{
			if(null == bad_password)
				return;
			
			bad_password.setVisibility(show);
			
		}
		
		private void ShowLoginAlartNotConnectNETGEARRouter(int  show )
		{
			if(null == alartinfo || null == notnetgearrouter)
				return;
			
			alartinfo.setVisibility(View.GONE);
			
		
			
			 //SpannableString sp = new SpannableString(m_Context.getResources().getString(R.string.notnetgearrouter));
			
			 //sp.setSpan(new URLSpan("http://www.netgear.com/landing/en-us/netgear-genie-routers.aspx"), 
			//		 38, 47,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			notnetgearrouter.setVisibility(show);
			//alartinfo.setText(sp);
			//alartinfo.setText(Html.fromHtml(m_Context.getResources().getString(R.string.notnetgearrouter)));
			notnetgearrouter.setMovementMethod(LinkMovementMethod.getInstance());
		}
		
		private void ShowLoginAlartInfoVisible(int  show ,int id)
		{
			if(null == alartinfo)
				return;
			
			alartinfo.setVisibility(show);
			alartinfo.setText(id);
			if(null == notnetgearrouter)
				notnetgearrouter.setVisibility(View.GONE);
		}
		
		private void SetOkButtonEnabled(boolean enabled)
		{
			if(null == login_ok)
				return ;
			login_ok.setEnabled(enabled);
		}
		private void SetEditPassWordEnabled(boolean enabled)
		{
			if(null == Login_password)
				return ;
			Login_password.setEnabled(enabled);
		}
		
		
		
		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			if(null != m_OnBackPressed)
			{
				m_OnBackPressed.OnBack(this);
			}
		}


		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			// TODO Auto-generated method stub
			if(event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)
			{
				return true;
			}
			return super.dispatchKeyEvent(event);
		}
		
		
		public void SetOnBackPressed(OnBackPressed back)
		{
			m_OnBackPressed = back;
		}
		
		public void SetOnLoginSucces(OnLoginSucces action)
		{
			m_OnLoginSucces = action;
		}
		
		public void SetOnCanclePressed(OnCanclePressed action)
		{
			m_OnCanclePressed = action;
		}
		
		
		private boolean IsConnectWIFI()
	    {
	    	ConnectivityManager connectivityManager = (ConnectivityManager)m_Context.getSystemService( Context.CONNECTIVITY_SERVICE );
		    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo(); //WIFI
		   
		    //GenieDebug.error("debug","getTypeName="+activeNetInfo.getTypeName());
		    
		    if(activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI)
		    {
		    	return activeNetInfo.isConnected();
		    }else
		    {
		    	return false;
		    }
	    }
		
		private void GetCurrentSetting()
		{
			
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE,GenieSoap.KEY_NULL);
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION,GenieSoap.KEY_NULL);
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME,GenieSoap.KEY_NULL);
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS,GenieSoap.KEY_NULL);
			GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_SUPPORTED,GenieSoap.KEY_NULL);
			m_GetCurrentSeting = null;
			
			ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	    
	    	
	    	GenieRequestInfo currentsetting = new GenieRequestInfo();
	    	currentsetting.aRequestLable="GenieLoginDialog";
	    	currentsetting.aHttpType = GenieGlobalDefines.EHttpGetCurrentSetting;
	    	currentsetting.aRequestType=GenieGlobalDefines.RequestActionType.Http;
	    	currentsetting.aTimeout = 20000;
	    	requestinfo.add(currentsetting);
	    	
	    	m_GetCurrentSeting = new GenieRequest(this.m_Context,requestinfo);
	    	m_GetCurrentSeting.SetProgressInfo(false,false);
	    	m_GetCurrentSeting.Start();
		}
		
		public void AuthenticateUserAndPassword(String username ,String password)
		{
			GenieDebug.error("debug","AuthenticateUserAndPassword username = "+username);
			GenieDebug.error("debug","AuthenticateUserAndPassword password = "+password);
			
			ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	    	
			if(GenieRequest.m_First)
			{	
				String RouterMode =  GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
				if(RouterMode != null 
						&& (RouterMode.indexOf("WNDR4500") != -1
						|| RouterMode.indexOf("WNDR3400") != -1))
				{
					GenieRequest.m_RequestPort = 5000;
				}else
				{
					GenieRequest.m_RequestPort = 80;
				}
				
				GenieRequest.m_First = false;
			}
			
	    	GenieRequestInfo test0 = new GenieRequestInfo();
	    	test0.aRequestLable="GenieLoginDialog";
	    	test0.aSoapType = GenieGlobalDefines.checkAvailabelPort;
	    	test0.aServer = "DeviceInfo";
	    	test0.aMethod = "GetInfo";
	    	test0.aNeedwrap = false;
	    	test0.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	    	test0.aNeedParser=true;
	    	test0.aTimeout = 20000;
	    	test0.aElement = new ArrayList<String>();
	    	requestinfo.add(test0);
	    	
	    	GenieRequestInfo test1 = new GenieRequestInfo();
	    	test1.aRequestLable="GenieLoginDialog";
	    	test1.aSoapType = GenieGlobalDefines.ESoapRequestConfigFinish;
	    	test1.aServer = "DeviceConfig";
	    	test1.aMethod = "ConfigurationFinished";
	    	test1.aNeedwrap = false;
	    	test1.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	    	test1.aNeedParser=false;
	    	test1.aTimeout = 15000;
	    	test1.aElement = new ArrayList<String>();
	    	test1.aElement.add("NewStatus");
	    	test1.aElement.add("ChangesApplied");
	    	requestinfo.add(test1);
	    	
	    	GenieRequestInfo test2 = new GenieRequestInfo();
	    	test2.aRequestLable="GenieLoginDialog";
	    	test2.aSoapType = GenieGlobalDefines.ESoapRequestVerityUser;
	    	test2.aServer = "ParentalControl";
	    	test2.aMethod = "Authenticate";
	    	test2.aNeedwrap = false;
	    	test2.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	    	test2.aNeedParser=false;
	    	test2.aTimeout = 20000;
	    	test2.aElement = new ArrayList<String>();
	    	test2.aElement.add("NewUsername");
	    	test2.aElement.add(username);
	    	test2.aElement.add("NewPassword");
	    	test2.aElement.add(password);
	    	
	    	requestinfo.add(test2);

	    	m_LoginRequest = new GenieRequest(this.m_Context,requestinfo);
	    	m_LoginRequest.SetProgressInfo(false, false);
	    	m_LoginRequest.Start();
		}
		
	    private RequestReceiver m_RequestReceiver = null;
	    
		 private void UnRegisterBroadcastReceiver()
		 {
			 GenieDebug.error("debug", "GenieLoginDialog UnRegisterBroadcastReceiver");
			 if(null != m_RequestReceiver)
				{
				 m_Context.unregisterReceiver(m_RequestReceiver);
					m_RequestReceiver = null;
				}
		 }

		 private void RegisterBroadcastReceiver()
		 {
			 UnRegisterBroadcastReceiver();
			 
			 GenieDebug.error("debug", "GenieLoginDialog RegisterBroadcastReceiver");
				m_RequestReceiver = new RequestReceiver();
				IntentFilter filter = new IntentFilter();//鍒涘缓IntentFilter瀵硅薄
				filter.addAction(GenieGlobalDefines.REQUEST_ACTION_RET_BROADCAST);
				m_Context.registerReceiver(m_RequestReceiver, filter);//娉ㄥ唽Broadcast Receiver
		 }
		 
		 
		  private class RequestReceiver extends BroadcastReceiver{//缁ф壙鑷狟roadcastReceiver鐨勫瓙绫�


		         @Override
			        public void onReceive(Context context, Intent intent) {//閲嶅啓onReceive鏂规硶
		     
		     		String lable = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_LABLE);
		     		//RequestActionType ActionType = intent.getExtra(GenieGlobalDefines.REQUEST_ACTION_RET_TYPE);
		     		//RequestActionType aResultType = intent.getExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESULTTYPE);
		     		
		     		RequestActionType ActionType = (RequestActionType) intent.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_TYPE);
		     		RequestResultType aResultType = (RequestResultType) intent.getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESULTTYPE);
		     		
		     		String aServer = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SERVER);
		     		String aMethod = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_METHOD);
		     		String aResponseCode = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSECODE);
		     		int aHttpResponseCode = intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_HTTPRESPONSECODE,-2);
		     		String aResponse = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSE);
		     		int aHttpType =  intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_HTTP_TYPE,-2);
		     		int aSoapType =  intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SOAP_TYPE,-2);
		     		GenieDebug.error("debug", "GenieLoginDialog onReceive lable ="+lable);
	     		
		     		if(lable != null && lable.equals("GenieLoginDialog"))
		     		{
			     		GenieDebug.error("debug", "GenieLoginDialog onReceive aServer ="+aServer);
			     		GenieDebug.error("debug", "GenieLoginDialog onReceive aMethod ="+aMethod);
			     		GenieDebug.error("debug", "GenieLoginDialog onReceive aResponseCode ="+aResponseCode);
			     		GenieDebug.error("debug", "GenieLoginDialog onReceive aHttpResponseCode ="+aHttpResponseCode);
			     		GenieDebug.error("debug", "GenieLoginDialog onReceive aResponse ="+aResponse);
			     		GenieDebug.error("debug", "GenieLoginDialog onReceive ActionType ="+ActionType);
			     		GenieDebug.error("debug", "GenieLoginDialog onReceive aResultType ="+aResultType);
			     		
			     		
			     		if(ActionType != null && ActionType == RequestActionType.Http)
			     		{
			     			GenieDebug.error("debug", "GenieLoginDialog onReceive aHttpType ="+aHttpType);
			     			if(aHttpType == GenieGlobalDefines.EHttpGetCurrentSetting)
			     			{
			     				if(null != aResultType)
			     				{
			     					if(aResultType == RequestResultType.Succes)
				     				{
				     					String isgenie =  GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE);  //, GenieGlobalDefines.ROUTER_TYPE_OLD_NETGEAR);
					     				String mFV =  GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION);
					     				String mMN =  GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
					     				String InternetStatus =  GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS);
					     				String LpcSupport =  GenieSoap.dictionary.get(GenieGlobalDefines.DICTIONARY_KEY_LPC_SUPPORTED);
					     				
					     				GenieDebug.error("debug", "GenieLoginDialog CurrentSetting isgenie ="+isgenie);
					     				GenieDebug.error("debug", "GenieLoginDialog CurrentSetting mFV ="+mFV);
					     				GenieDebug.error("debug", "GenieLoginDialog CurrentSetting mMN ="+mMN);
					     				GenieDebug.error("debug", "GenieLoginDialog CurrentSetting InternetStatus ="+InternetStatus);
					     				GenieDebug.error("debug", "GenieLoginDialog CurrentSetting LpcSupport ="+LpcSupport);
						     			if(null != isgenie)	
						     			{
						     				if(isgenie.equals(GenieGlobalDefines.ROUTER_TYPE_NEW_NETGEAR) || 
						     					isgenie.equals(GenieGlobalDefines.ROUTER_TYPE_OLD_NETGEAR) )
						     				{
						     					m_notnetgearrouter = 1;
						     					if(null != Login_password)
						     						AuthenticateUserAndPassword("admin",Login_password.getText().toString());
						     				}else
						     				{
						     					m_notnetgearrouter = 0;
						     					
						     					ShowLoginProgressBar(View.INVISIBLE);
						     					ShowLoginWaitinfo(View.GONE);
						     					ShowLoginBad_password(View.GONE);
						     					SetOkButtonEnabled(false);
						     					SetEditPassWordEnabled(false);
						     					ShowLoginAlartNotConnectNETGEARRouter(View.VISIBLE);
						     					//ShowLoginAlartInfoVisible(View.VISIBLE,R.string.notnetgearrouter);
						     				}
						     			}else
						     			{
						     				m_notnetgearrouter = 0;
						     			}
				     				}else
				     				{
//				     					if(aResultType == RequestResultType.Exception 
//				     							&& aResponse != null 
//				     							&& (aResponse.indexOf("timed out") != -1))
//				     					{
//				     						ShowLoginProgressBar(View.INVISIBLE);
//					     					ShowLoginWaitinfo(View.GONE);
//					     					ShowLoginBad_password(View.GONE);
//					     					SetOkButtonEnabled(false);
//					     					SetEditPassWordEnabled(false);
//					     					ShowLoginAlartInfoVisible(View.VISIBLE,R.string.actiontimeout);
//				     					}else
//				     					{
				     						m_notnetgearrouter = 0;
					     					ShowLoginProgressBar(View.INVISIBLE);
					     					ShowLoginWaitinfo(View.GONE);
					     					ShowLoginBad_password(View.GONE);
					     					SetOkButtonEnabled(false);
					     					SetEditPassWordEnabled(false);
					     					ShowLoginAlartNotConnectNETGEARRouter(View.VISIBLE);
					     					//ShowLoginAlartInfoVisible(View.VISIBLE,R.string.notnetgearrouter);
				     					//}
				     				}
			     				}
			     			}
			     		}else if(ActionType != null && ActionType == RequestActionType.Soap)
			     		{
			     			GenieDebug.error("debug", "GenieLoginDialog onReceive aSoapType ="+aSoapType);
			     			
			     			if(aSoapType == GenieGlobalDefines.ESoapRequestVerityUser)
			     			{
			     				if(aResultType != null)
			     				{
				     				if(aResultType == RequestResultType.Succes && (null != aResponse && aResponse.indexOf("000") != -1))
				     				{
				     					if(null != Login_password)
				     						SaveLoginPassword(Login_password.getText().toString());
				     					if(null != m_OnLoginSucces)
				     						m_OnLoginSucces.OnSucces(GenieLoginDialog.this);
				     				}else
				     				{
				     					if(aResultType == RequestResultType.Exception 
				     							&& aResponse != null 
				     							&& (aResponse.indexOf("timed out") != -1))
				     					{
				     						ShowLoginProgressBar(View.INVISIBLE);
					     					ShowLoginWaitinfo(View.GONE);
					     					ShowLoginBad_password(View.GONE);
					     					SetOkButtonEnabled(false);
					     					SetEditPassWordEnabled(false);
					     					ShowLoginAlartInfoVisible(View.VISIBLE,R.string.actiontimeout);
				     					}else
				     					{
				     						ShowLoginProgressBar(View.INVISIBLE);
				     						ShowLoginWaitinfo(View.GONE);
				     						ShowLoginBad_password(View.VISIBLE);
				     						SetOkButtonEnabled(true);
				     						SetEditPassWordEnabled(true);
				     						ShowLoginAlartNotConnectNETGEARRouter(View.GONE);
				     						//ShowLoginAlartInfoVisible(View.GONE,R.string.notnetgearrouter);
				     					}
				     				}
			     				}
			     			}
			     		}
		     		}

		         }
		  }
		
		
	}
