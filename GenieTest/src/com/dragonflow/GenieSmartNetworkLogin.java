package com.dragonflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import com.dragonflow.GenieGlobalDefines.RequestActionType;
import com.dragonflow.GenieGlobalDefines.RequestResultType;
import com.dragonflow.genie.busi.process.TestTask;
import com.dragonflow.util.FileUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ViewFlipper;
import com.dragonflow.genie.ui.R;
import com.dragonflow.genie.ui.R.drawable;

public class GenieSmartNetworkLogin extends Activity {

	private ViewFlipper m_ViewFlipper = null;
	private LinearLayout m_Smartnetworklogin = null;
	private LinearLayout m_Smartlistview = null;
	private RelativeLayout m_Smartrouterloginview = null;
	
	private RelativeLayout m_localrouterlogin = null;
	
	private EditText  m_smartusernameview = null;
	private EditText  m_smartpasswordview = null;
	// Allen add for test login
	private EditText etInterval = null;
	private TestTask testService = null;
	private Button btnResetLog = null;
	// Allen add for test login end
	
	//private CheckBox  m_smartswitch = null;
	private RadioGroup m_radioswitch = null;
	private LinearLayout  m_smartswitchview = null;
	
	private EditText  m_localpassword = null;
	
	private GenieRequest m_Loginrequest = null;
	
	private int  activeIndex = 0;
	private int  clickid = -1;
	
	private int  m_sdkversion = -1;
	public int timeout=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //Allen add for test login:应用运行时，保持屏幕高亮，不锁屏
		Display d = getWindowManager().getDefaultDisplay();
		final int woindow_w = d.getWidth();
		final int woindow_h = d.getHeight();
		timeout=0;
		GenieDebug.error("onCreate", "onCreate --woindow_w == "+woindow_w);
    	
		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			setTheme(R.style.bigactivityTitlebarNoSearch);
		} else {
			setTheme(R.style.activityTitlebarNoSearch); 
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE
				| Window.FEATURE_CUSTOM_TITLE);	
    	
		setContentView(R.layout.smartrouterview);
		
		if (woindow_w >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE
				&& woindow_h >= GenieGlobalDefines.BIGACTIVITYTITLEBARNOSEARCHSIZE) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar_big);
		} else {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar);
		}
		
		
		activeIndex = getIntent().getIntExtra(
				GenieGlobalDefines.LIST_TYPE_INDEX, 0);
		clickid = getIntent().getIntExtra(
				GenieGlobalDefines.CLICK_ID, -1);
		

		
		
    	InitTitleView();
		InitView();

		  //Allen add for test login
		  etInterval = (EditText)m_localrouterlogin.findViewById(R.id.interval_et);	 
		  btnResetLog = (Button)m_localrouterlogin.findViewById(R.id.reset_log);
		  tvResult = (TextView)m_localrouterlogin.findViewById(R.id.test_result_tv);
		  initHandler();
		  btnResetLog.setOnClickListener(new View.OnClickListener() 
		  {
				@Override
				public void onClick(View v) 
				{
					TestTask.resetAll();
					tvResult.setText("login test result:");
				}
			});
		  //Allen add for test login end	
	}
	private View devicedetaildolg = null;
	
	private void savesmartnetworkurl(String url)
	{
		GenieRequest.SaveSmartNetworkUrl(this,url);
	}
	
	private void ShowConfigSmartNetworkDialog()
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		
		  devicedetaildolg = inflater.inflate(R.layout.setsmartnetworkurl,null);
		  
		  EditText smarturl = (EditText)devicedetaildolg.findViewById(R.id.smarturl);
		  smarturl.setText(GenieRequest.GetSmartNetworkUrl(this));
//		  smarturl.setEnabled(false);
		  
			AlertDialog.Builder dialog_wifiset =  new AlertDialog.Builder(this);
			
			//dialog_wifiset.setIcon(android.R.drawable.ic_dialog_alert);
			//dialog_wifiset.setTitle(" ");
			dialog_wifiset.setView(devicedetaildolg);
			dialog_wifiset.setPositiveButton(R.string.close,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					devicedetaildolg = null;
				}
			});
			dialog_wifiset.setNegativeButton(R.string.save,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(null != devicedetaildolg)
					{
						EditText smarturl = (EditText)devicedetaildolg.findViewById(R.id.smarturl);
						savesmartnetworkurl(smarturl.getText().toString());
						devicedetaildolg = null;
					}
				}
			});

			dialog_wifiset.show();
	}
	
	private void  SetTitleText()
	{
		
		if(null != m_ViewFlipper)
		{
			int id = m_ViewFlipper.getDisplayedChild();
			TextView title = (TextView) this.findViewById(R.id.netgeartitle);
			Button  back = (Button)this.findViewById(R.id.back);
			back.setBackgroundResource(R.drawable.title_bt_bj);
			
			
			Button  about = (Button)this.findViewById(R.id.about);		
			about.setBackgroundResource(R.drawable.title_bt_bj);
			about.setVisibility(View.GONE);
			
			
			switch(id)
			{
			case 0:
			case 1:	
				title.setText(R.string.login);
				back.setText(R.string.back);
				back.setOnClickListener( new OnClickListener() {
					
					@Override
					public void onClick(View v) 
					{
						onBack();
					}
				});
//				if(id == 1)
//				{
//					about.setVisibility(View.VISIBLE);
//					about.setText("test");					
//					about.setOnClickListener( new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							ShowConfigSmartNetworkDialog();
//						}
//					});
//				}
				break;
			case 2:	
				title.setText(R.string.s_mydevices);
				back.setText(R.string.logout);
				back.setOnClickListener( new OnClickListener() {
					
					@Override
					public void onClick(View v) 
					{
						//allen remark and add
						//EndRouterSession();
						onBack();
						//allen remark and add end
//						GenieRequest.m_SmartNetWork = false;
//						SetLoginResult(GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_FAILED);
					}
				});
//				about.setVisibility(View.VISIBLE);
//				about.setText(R.string.refresh);
//				about.setOnClickListener( new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) 
//					{
//						RefreshRouterList();
//					}
//				});
				
				break;
			case 3:	
				title.setText(R.string.s_routerlogin);
				back.setText(R.string.back);
				back.setOnClickListener( new OnClickListener() {
					
					@Override
					public void onClick(View v) 
					{
						onBack();
					}
				});
				break;	
			default:
				break;
			}
			
		}
	}
	
	private void onBack()
	{
		//Allen remark for login test
//		
//		if(null != m_ViewFlipper)
//		{
//			if(!m_ViewFlipper.isFlipping())
//		    {
//				int id = m_ViewFlipper.getDisplayedChild();
//				if(id == 0 || id == 1)
//				{
//					SetLoginResult(GenieGlobalDefines.LOCALLOGIN_RESULT_FAILED, null); // Allen modify for login test
//				}else if(id == 2)
//				{
//					if(activeIndex == GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_FAILED)
//					{
//						if(null != m_smartswitchview)
//						{
//						  m_smartswitchview.setVisibility(View.VISIBLE);
//						}
//						
//						  if(m_SmartNetworkloginRemember)
//						  {
//							  String user =  GetSmartNetworkUsername();
//							  String pass =  GetSmartNetworkPassword();
//							   
//							  if(null != user && null != pass)
//							  {
//								  m_smartusernameview.setText(user);
//								  m_smartpasswordview.setText(pass);
//							  }else
//							  {
//								  m_smartusernameview.setText("");
//								  m_smartpasswordview.setText("");
//							  }
//						  }else
//						  {
//							  m_smartusernameview.setText("");
//							  m_smartpasswordview.setText("");
//						  }
//						  
//						  m_ViewFlipper.setDisplayedChild(1);
//					}else
//					{
//						this.finish();
//					}
//				}else if(id == 3)
//				{
//					
//					m_ViewFlipper.setDisplayedChild(2);
//				}
//			   
//			}
//			
//			SetTitleText();
//		}

		
		
	}
	
	public void InitTitleView()
	{
		GenieDebug.error("debug","InitTitleView 0000");
		Button  back = (Button)this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.title_bt_bj);
		
		
		Button  about = (Button)this.findViewById(R.id.about);		
		about.setBackgroundResource(R.drawable.title_bt_bj);
		
		
		
		back.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				onBack();
			}
		});
		
		about.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		back.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		

		
		about.setOnTouchListener(new OnTouchListener(){      
            @Override     
            public boolean onTouch(View v, MotionEvent event) {      
                    if(event.getAction() == MotionEvent.ACTION_DOWN){      
                            //    
                            v.setBackgroundResource(R.drawable.title_bt_fj);
                    		
                    }else if(event.getAction() == MotionEvent.ACTION_UP){      
                            //
                            v.setBackgroundResource(R.drawable.title_bt_bj);
                    		
                    }      
                    return false;      
            }      
		});
		about.setVisibility(View.GONE);
		
		
	}
	
	private void InitView()
	{
		m_ViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);    
//		m_ViewFlipper.addView(LayoutInflater.from(this).inflate(R.layout.localrouterlogin,null));
//		m_ViewFlipper.addView(LayoutInflater.from(this).inflate(R.layout.smartnetworklogin,null));
//		m_ViewFlipper.addView(LayoutInflater.from(this).inflate(R.layout.smartrouterlist,null));
//		m_ViewFlipper.addView(LayoutInflater.from(this).inflate(R.layout.smartrouterlogin,null));
//		
		m_smartswitchview =   (LinearLayout)findViewById(R.id.smartswitchview);
		
		m_radioswitch = (RadioGroup)findViewById(R.id.radioswitch);
		m_radioswitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == R.id.smartswitch)
				{
					if(Build.VERSION.SDK_INT <  Build.VERSION_CODES.GINGERBREAD)
					{
						
						ShowLoginAlartInfoVisible(R.string.s_versionwarning);
						RadioButton local = (RadioButton)findViewById(R.id.localswitch);
						local.setChecked(true);
						return ;
					}else
					{
						 if(null != m_ViewFlipper)
						  {
							  if(!m_ViewFlipper.isFlipping() && m_ViewFlipper.getDisplayedChild() != 1)
							  {
								  m_ViewFlipper.setDisplayedChild(1);
								  SaveSmartNetworkSwitch(true);
							  }
						  }
					}
				}else if(checkedId == R.id.localswitch)
				{
					if(null != m_ViewFlipper)
					  {
						  if(!m_ViewFlipper.isFlipping()  && m_ViewFlipper.getDisplayedChild() != 0)
						  {
							  m_ViewFlipper.setDisplayedChild(0);
							  SaveSmartNetworkSwitch(false);
						  }
					  }
				}
				SetTitleText();
			}
		});


		m_Smartnetworklogin = (LinearLayout)findViewById(R.id.smartnetworklogin);
		m_Smartlistview = (LinearLayout)findViewById(R.id.smartlistview);
		m_Smartrouterloginview = (RelativeLayout)findViewById(R.id.Smartrouterloginview);
		m_localrouterlogin =   (RelativeLayout)findViewById(R.id.localrouterlogin);
		
		if(activeIndex != GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_SUCEESS)
		{
			RadioGroup sswitch =  (RadioGroup)findViewById(R.id.radioswitch);
			
			if(GenieGlobalDefines.GetSmartNetworkFlag(this) == 1)
			{
				sswitch.setVisibility(View.VISIBLE);
			}else
			{
				sswitch.setVisibility(View.GONE);
			}
			InitLocalRouterLogin();
			InitSmartNetworkLogin();
			
			if(GetSmartNetworkSwitch())
			{
				RadioButton smartswitch = (RadioButton)findViewById(R.id.smartswitch);
				smartswitch.setChecked(true);
				 
				if(!m_ViewFlipper.isFlipping())
				{
				   m_ViewFlipper.setDisplayedChild(1);
				   SetTitleText();
				}
			}else
			{
				RadioButton local = (RadioButton)findViewById(R.id.localswitch);
				local.setChecked(true);
				 
				if(!m_ViewFlipper.isFlipping())
				{
				   m_ViewFlipper.setDisplayedChild(0);
				   SetTitleText();
				}
			}

		}else
		{
			ShowSmartNetworkRouterListView();
			
		}
	}
	
	public GenieUserInfo userInfo = null;
	
	private void InitLocalRouterLogin()
	{
		if(null == m_localrouterlogin)
			return ;
		
		userInfo = GenieShareAPI.ReadDataFromFile(this, GenieGlobalDefines.USER_INFO_FILENAME);
		
		Button login_ok = (Button)m_localrouterlogin.findViewById(R.id.login_ok);
		Button login_cancel = (Button)m_localrouterlogin.findViewById(R.id.login_cancel);
		m_localpassword = (EditText)m_localrouterlogin.findViewById(R.id.password);
		
		
		 CheckBox rememberpassword = (CheckBox)m_localrouterlogin.findViewById(R.id.remeberpassword);
		 if(userInfo.isSave == '1')
			{
				rememberpassword.setChecked(true);
				String password = GetLoginPassword();
				m_localpassword.setText(password);
			}
			else
			{
				rememberpassword.setChecked(false);
				m_localpassword.setText("");
			}
		 
		  rememberpassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					userInfo.isSave = '1';
				}else
				{
					userInfo.isSave = '0';
				}
			}
		});
		
		  m_localpassword.setOnEditorActionListener(new OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					// TODO Auto-generated method stub
					GenieDebug.error("debug","actionId="+actionId);
					if(null != m_localrouterlogin)
					{
						InputMethodManager imm = (InputMethodManager)GenieSmartNetworkLogin.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);    
						
						GenieDebug.error("debug","null != m_Smartnetworklogin");
						Button login_ok = (Button)m_localrouterlogin.findViewById(R.id.login_ok);
						login_ok.setFocusable(true);
						login_ok.requestFocus();
						login_ok.setFocusableInTouchMode(true);
					}
					
					return false;
				}
			  });    
		
		
		
		login_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				GetCurrentSetting();
			}
		});
		
		login_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//SetLoginResult(GenieGlobalDefines.LOCALLOGIN_RESULT_FAILED, null); // Allen modify for login test
				TestTask.stopLoginTask();
			}
		});
		
		m_localpassword.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				GenieDebug.error("debug", "onTextChanged 0");
				if(null != m_localrouterlogin && null != m_localpassword )
				{
					GenieDebug.error("debug", "onTextChanged 1");
					int number = m_localpassword.getText().toString().length();
					Button login_ok = (Button)m_localrouterlogin.findViewById(R.id.login_ok);
					Button login_cancel = (Button)m_localrouterlogin.findViewById(R.id.login_cancel);
				
					if(number > 0)
					{
						login_ok.setEnabled(true);
					}else
					{
						login_ok.setEnabled(false);
					}
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
		
		int number = m_localpassword.getText().toString().length();
		if(number > 0)
		{
			login_ok.setEnabled(true);
		}else
		{
			login_ok.setEnabled(false);
		}
	}
	
	private boolean IsConnectWIFI()
    {
    	ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService( Context.CONNECTIVITY_SERVICE );
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
//		if(!IsConnectWIFI())
//		{
//			ShowLoginAlartInfoVisible(R.string.noconnectrouter);
//			return ;
//		}
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE,GenieSoap.KEY_NULL);
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION,GenieSoap.KEY_NULL);
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME,GenieSoap.KEY_NULL);
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS,GenieSoap.KEY_NULL);
		GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_LPC_SUPPORTED,GenieSoap.KEY_NULL);
		StopRequest();
		
		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();

		//Allen add for login test
		TestTask.initLoginTask(m_smartusernameview.getText().toString(),
				m_smartpasswordview.getText().toString(), 
				Integer.parseInt(etInterval.getText().toString()), mHandler);
		//Allen add for login test end	  
    	
    	GenieRequestInfo currentsetting = new GenieRequestInfo();
    	currentsetting.aRequestLable="GenieSmartNetworkLogin";
    	currentsetting.aHttpType = GenieGlobalDefines.EHttpGetCurrentSetting;
    	currentsetting.aRequestType=GenieGlobalDefines.RequestActionType.Http;
    	currentsetting.aTimeout = 20000;
    	requestinfo.add(currentsetting);
    	
    	m_Loginrequest = new GenieRequest(this,requestinfo);
    	m_Loginrequest.SetProgressInfo(true,true);
    	m_Loginrequest.Start();
	}
	
	public void AuthenticateUserAndPassword(String username ,String password)
	{
		GenieDebug.error("debug","AuthenticateUserAndPassword username = "+username);
		GenieDebug.error("debug","AuthenticateUserAndPassword password = "+password);
		
		StopRequest();

		ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
    	
		if(GenieRequest.m_First) //是首次访问，未认证用户名和密码
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
			GenieRequest.m_RequestPort = 5000;
		}
    	GenieRequestInfo test0 = new GenieRequestInfo();
    	test0.aRequestLable="GenieSmartNetworkLogin";
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
    	test1.aRequestLable="GenieSmartNetworkLogin";
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
    	test2.aRequestLable="GenieSmartNetworkLogin";
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

    	m_Loginrequest = new GenieRequest(this,requestinfo);
    	m_Loginrequest.SetProgressInfo(true,true);
    	m_Loginrequest.Start();
	}
	
	private boolean m_SmartNetworkloginRemember = true;
	
	private void InitSmartNetworkLogin()
	{
		if(m_Smartnetworklogin == null)
			return ;
		Button login_ok = (Button)m_Smartnetworklogin.findViewById(R.id.button_ok);
		  Button login_cancel = (Button)m_Smartnetworklogin.findViewById(R.id.button_cancel);
		  m_smartusernameview = (EditText)m_Smartnetworklogin.findViewById(R.id.smartusername);
		  m_smartpasswordview = (EditText)m_Smartnetworklogin.findViewById(R.id.smartpassword);	  
		  
		  m_SmartNetworkloginRemember = GetSmartNetworkRemember();
		  
		  CheckBox checkremeber = (CheckBox)m_Smartnetworklogin.findViewById(R.id.remember);
		  checkremeber.setChecked(m_SmartNetworkloginRemember);
		  checkremeber.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				m_SmartNetworkloginRemember = isChecked;
			}
		});
		  
		  if(m_SmartNetworkloginRemember)
		  {
			  String user =  GetSmartNetworkUsername();
			  String pass =  GetSmartNetworkPassword();
			  
			  
			  GenieDebug.error("debug","ShowSmartNetworkLoginView user="+user);
			  GenieDebug.error("debug","ShowSmartNetworkLoginView pass="+pass);
			  
			  if(null != user && null != pass)
			  {
				  m_smartusernameview.setText(user);
				  m_smartpasswordview.setText(pass);
			  }else
			  {
				  m_smartusernameview.setText("");
				  m_smartpasswordview.setText("");
			  }
		  }else
		  {
			  m_smartusernameview.setText("");
			  m_smartpasswordview.setText("");
		  }
				  
		  
		  
		  login_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(null != m_smartusernameview
						&& null != m_smartpasswordview)
				{
					SmartNetWorkInitAndAuth(m_smartusernameview.getText().toString(),
							m_smartpasswordview.getText().toString());
				}
			}
		});
		  
		  login_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//SetLoginResult(GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_FAILED, null); // Allen modify for login test
					TestTask.stopLoginTask();
				}
			});
		  
		  
		  m_smartusernameview.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					GenieDebug.error("debug", "onTextChanged 0");
					if(null != m_Smartnetworklogin && 
							null != m_smartusernameview &&
							null != m_smartpasswordview)
					{
						GenieDebug.error("debug", "onTextChanged 1");
						
						Button login_ok = (Button)m_Smartnetworklogin.findViewById(R.id.button_ok);
						Button login_cancel = (Button)m_Smartnetworklogin.findViewById(R.id.button_cancel);
						  
						  
						int number = m_smartusernameview.getText().toString().length();
						int number1 = m_smartpasswordview.getText().toString().length();
						
						if(number > 0 && number1 > 0)
						{
							login_ok.setEnabled(true);
						}else
						{
							login_ok.setEnabled(false);
						}
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
		  
		  m_smartpasswordview.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					GenieDebug.error("debug", "onTextChanged 0");
					if(null != m_Smartnetworklogin && 
							null != m_smartusernameview &&
							null != m_smartpasswordview)
					{
						GenieDebug.error("debug", "onTextChanged 1");
						
						Button login_ok = (Button)m_Smartnetworklogin.findViewById(R.id.button_ok);
						Button login_cancel = (Button)m_Smartnetworklogin.findViewById(R.id.button_cancel);
						  
						  
						int number = m_smartusernameview.getText().toString().length();
						int number1 = m_smartpasswordview.getText().toString().length();
						
						if(number > 0 && number1 > 0)
						{
							login_ok.setEnabled(true);
						}else
						{
							login_ok.setEnabled(false);
						}
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
		  
		  m_smartpasswordview.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				GenieDebug.error("debug","actionId="+actionId);
				if(null != m_Smartnetworklogin)
				{
					InputMethodManager imm = (InputMethodManager)GenieSmartNetworkLogin.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);    
					
					GenieDebug.error("debug","null != m_Smartnetworklogin");
					Button login_ok = (Button)m_Smartnetworklogin.findViewById(R.id.button_ok);
					login_ok.setFocusable(true);
					login_ok.requestFocus();
					login_ok.setFocusableInTouchMode(true);
				}
				
				return false;
			}
		  });    
			
			int number = m_smartusernameview.getText().toString().length();
			int number1 = m_smartpasswordview.getText().toString().length();
			if(number > 0 && number1 > 0)
			{
				login_ok.setEnabled(true);
			}else
			{
				login_ok.setEnabled(false);
			}
			
			
			TextView fogotpassword = (TextView)m_Smartnetworklogin.findViewById(R.id.fogotpassword);
			fogotpassword.setMovementMethod(LinkMovementMethod.getInstance());
			
			TextView signup = (TextView)m_Smartnetworklogin.findViewById(R.id.signup);
			signup.setMovementMethod(LinkMovementMethod.getInstance());
		  
		 
		
	}
	
	private void SetLoginResult(int result, String loginInfo)
	{
		StopRequest();
		
		// Allen add for login test
		if(result > 0)
		{
			TestTask.saveLoginResult(true, "login success"); //Allen add for login test
		}
		else
		{
			TestTask.saveLoginResult(false, "login fail:" + loginInfo);
		}
		
		//Allen add remark
//    	CloseAlertDialog();
//    	
//		this.setResult(result);
//		this.finish();
	}
	
	private boolean IsConnectInternet()
    {
    	ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService( Context.CONNECTIVITY_SERVICE );
	    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo(); //WIFI
	   
	    //GenieDebug.error("debug","getTypeName="+activeNetInfo.getTypeName());
	    
	    if(activeNetInfo != null && !activeNetInfo.isAvailable())
	    {
	    	return false;
	    }
	    
	    if(activeNetInfo != null && (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI
	    		||activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE))
	    {
	    	return activeNetInfo.isConnected();
	    }else
	    {
	    	return false;
	    }
    }
	
	public void SmartNetWorkInitAndAuth(String username,String password)
	  {
		  GenieDebug.error("debug","SmartNetWorkInitAndAuth username = "+username);
		  GenieDebug.error("debug","SmartNetWorkInitAndAuth password = "+password);

		  if(!IsConnectInternet())
		  {
			  GenieDebug.error("debug","SmartNetWorkInitAndAuth !IsConnectInternet()");
			  ShowLoginAlartInfoVisible(R.string.s_nointernet);
			  return ;
		  }
		  GenieDebug.error("debug","SmartNetWorkInitAndAuth IsConnectInternet()");
		  
		  StopRequest();
			//Allen add for login test
			TestTask.initLoginTask(m_smartusernameview.getText().toString(),
					m_smartpasswordview.getText().toString(), 
					Integer.parseInt(etInterval.getText().toString()), mHandler);
			//Allen add for login test end	  
		  ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	    	

	    	
			 GenieRequestInfo test = new GenieRequestInfo();
			 test.aRequestLable="GenieSmartNetworkLogin";
			 test.aSmartType = GenieGlobalDefines.ESMARTNETWORKAUTHENTICATE;
			 test.aServer = "/fcp/authenticate";
			 //test.aMethod = "Authenticate";
			 //test.aNeedwrap = false;
			 test.aRequestType=GenieGlobalDefines.RequestActionType.SmartNetWork;
			 //test.aNeedParser=false;
			 test.aTimeout = 20000;
			 test.aElement = new ArrayList<String>();	
			 test.aElement.add(username);
			 test.aElement.add(password);
			 
			 requestinfo.add(test);

			 m_Loginrequest = new GenieRequest(this,requestinfo);
			 m_Loginrequest.SetProgressInfo(true, true);
			 m_Loginrequest.Start();
	    	
	  }
	
	private void GetSmartRouterInfo()
	{
		StopRequest();
		  
		  ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	    	
	  	GenieRequestInfo request = new GenieRequestInfo();
	  	request.aRequestLable="GenieSmartNetworkLogin";
	  	request.aSoapType = GenieGlobalDefines.ESoapRequestRouterInfo;
	  	request.aServer = "DeviceInfo";
	  	request.aMethod = "GetInfo";
	  	request.aNeedwrap = false;
	  	request.aRequestType=GenieGlobalDefines.RequestActionType.Soap;
	  	request.aNeedParser=true;
	  	request.aTimeout = 25000;
	  	request.aElement = new ArrayList<String>();
	  	
	  	requestinfo.add(request);
    	
	  	m_Loginrequest = new GenieRequest(this,requestinfo);
	  	m_Loginrequest.SetProgressInfo(true, true);
	  	m_Loginrequest.Start();
	}
	
	
	private void RefreshRouterList()
	{
		
		StopRequest();
		  
		  ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	    	
		  	
	    	
		  GenieRequestInfo test = new GenieRequestInfo();
		  test.aRequestLable="GenieSmartNetworkLogin";
		  test.aSmartType = GenieGlobalDefines.ESMARTNETWORK_GETROUTERLIST;
		  //test.aServer = "/fcp/authenticate";
		  //test.aMethod = "Authenticate";
		  //test.aNeedwrap = false;
		  test.aRequestType=GenieGlobalDefines.RequestActionType.SmartNetWork;
		  //test.aNeedParser=false;
		  //test.aTimeout = 20000;
		 
		  requestinfo.add(test);

		  m_Loginrequest = new GenieRequest(this,requestinfo);
		  m_Loginrequest.SetProgressInfo(true, true);
		  m_Loginrequest.Start();
			 
	}
	
	
	private void EndRouterSession()
	  {
		  GenieDebug.error("debug","EndRouterSession ");
		  
		  	 if(GenieRequest.m_SmartInfo != null
		  			 && null != GenieRequest.m_SmartInfo.workcpid
		  			&& null != GenieRequest.m_SmartInfo.uiid
		  			&& null != GenieRequest.m_SmartInfo.domain
		  			&& null != GenieRequest.m_SmartInfo.sessionid)
			 {
				
		  
		  		StopRequest();
				  
				  ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
			    	
				  	
			    	
					 GenieRequestInfo test = new GenieRequestInfo();
					 test.aRequestLable="GenieSmartNetworkLogin";
					 test.aSmartType = GenieGlobalDefines.ESMARTNETWORK_EndRouterSession;
					 //test.aServer = "/fcp/authenticate";
					 //test.aMethod = "Authenticate";
					 //test.aNeedwrap = false;
					 test.aRequestType=GenieGlobalDefines.RequestActionType.SmartNetWork;
					 //test.aNeedParser=false;
					 //test.aTimeout = 20000;
					 
					 requestinfo.add(test);
		
					 m_Loginrequest = new GenieRequest(this,requestinfo);
					 m_Loginrequest.SetProgressInfo(true, true);
					 m_Loginrequest.Start();
		  }else
		  {
			  GenieRequest.m_SmartNetWork = false;
				SetLoginResult(GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_FAILED, null); // Allen modify for login test
				return ;

		  }
	  }
	
	private void SmartRouterStartSession(String username,String password)
	  {
		  GenieDebug.error("debug","SmartRouterStartSession username="+username);
		  GenieDebug.error("debug","SmartRouterStartSession password="+password);
		  
		  StopRequest();

			//Allen add for login test
			TestTask.initLoginTask(m_smartusernameview.getText().toString(),
					m_smartpasswordview.getText().toString(), 
					Integer.parseInt(etInterval.getText().toString()), mHandler);
			//Allen add for login test end	  
		  ArrayList<GenieRequestInfo> requestinfo = new ArrayList<GenieRequestInfo>();
	    	
		  	 if(GenieRequest.m_SmartInfo != null
		  			 && null != GenieRequest.m_SmartInfo.workcpid
		  			&& null != GenieRequest.m_SmartInfo.uiid
		  			&& null != GenieRequest.m_SmartInfo.domain
		  			&& null != GenieRequest.m_SmartInfo.sessionid)
			 {
		  		GenieRequestInfo request = new GenieRequestInfo();
		  		request.aRequestLable=" ";
		  		request.aSmartType = GenieGlobalDefines.ESMARTNETWORK_EndRouterSession;
				 //test.aServer = "/fcp/authenticate";
				 //test.aMethod = "Authenticate";
				 //test.aNeedwrap = false;
		  		request.aRequestType=GenieGlobalDefines.RequestActionType.SmartNetWork;
				 //test.aNeedParser=false;
				 //test.aTimeout = 20000;
				 
				 requestinfo.add(request);
			 }
		  	
	    	
			 GenieRequestInfo test = new GenieRequestInfo();
			 
			 
			 test.aRequestLable="GenieSmartNetworkLogin";
			 test.aSmartType = GenieGlobalDefines.ESMARTNETWORK_StartRouterSession;
			 //test.aServer = "/fcp/authenticate";
			 //test.aMethod = "Authenticate";
			 //test.aNeedwrap = false;
			 test.aRequestType=GenieGlobalDefines.RequestActionType.SmartNetWork;
			 //test.aNeedParser=false;
			 //test.aTimeout = 20000;
			 test.aElement = new ArrayList<String>();	
			 test.aElement.add(username);
			 test.aElement.add(password);
			 
			 requestinfo.add(test);

			 m_Loginrequest = new GenieRequest(this,requestinfo);
			 m_Loginrequest.SetProgressInfo(true, true);
			 m_Loginrequest.Start();
	  }
	  
	private boolean m_SmartNetworkRouterRemember = true;
	
	 private void ShowSmartRouterLoginView(GenieSmartRouterInfo routerinfo)
	  {
		  if(null == m_Smartrouterloginview || null == routerinfo )
			  return;
		  
		  
		  
		  TextView friendly_name = (TextView) m_Smartrouterloginview.findViewById(R.id.friendly_name);
		  TextView active = (TextView) m_Smartrouterloginview.findViewById(R.id.active);
         TextView serial = (TextView) m_Smartrouterloginview.findViewById(R.id.serial);
         ImageView icon = (ImageView) m_Smartrouterloginview.findViewById(R.id.router_icon);
         TextView model = (TextView) m_Smartrouterloginview.findViewById(R.id.model);
         
         EditText  username = (EditText)m_Smartrouterloginview.findViewById(R.id.smartrouterusername);
		  EditText  password = (EditText)m_Smartrouterloginview.findViewById(R.id.smartrouterpassword);
		  
		  
		  m_SmartNetworkRouterRemember = GetSmartNetworkRouterRemember(routerinfo.serial);
		  
		  CheckBox rememberpassword = (CheckBox)m_Smartrouterloginview.findViewById(R.id.remeberpassword);
		  rememberpassword.setChecked(m_SmartNetworkRouterRemember);
		  rememberpassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				m_SmartNetworkRouterRemember = isChecked;
			}
		});
		  
		  if(m_SmartNetworkRouterRemember)
		  {	  
		  
		      //String routeruser =  GetSmartNetworkRouterUsername(routerinfo.serial);
		      String routerpass =  GetSmartNetworkRouterPassword(routerinfo.serial);
		      
		     // GenieDebug.error("debug","ShowSmartRouterLoginView routeruser = "+routeruser);
		      GenieDebug.error("debug","ShowSmartRouterLoginView routerpass = "+routerpass);
		      
		      
		      if(null != routerpass)
		      {
		    	  //username.setText(routeruser);
		    	  password.setText(routerpass);
		      }else
		      {
		    	  //username.setText("");
		    	  password.setText("");
		      }
		  }else
		  {
			  //username.setText("");
	    	  password.setText("");
		  }
         
         friendly_name.setText(routerinfo.friendly_name);
         model.setText(routerinfo.model);
         //active.setText(routerinfo.active);
         //serial.setText(routerinfo.serial);
         
         if(routerinfo.active != null)
         {
         	if(routerinfo.active.equals("true"))
         		active.setText(getResources().getString(R.string.s_online));
         }
         if(routerinfo.serial != null)
         {
         	String ser = String.format("%s:%s",getResources().getString(R.string.s_serial),routerinfo.serial);
         	serial.setText(ser);
         }
        
         
         int routertype = GetRouterTypeID(routerinfo.model);
         
         
         if(routertype != -1)
         {
        	 Bitmap mIcon1 = BitmapFactory.decodeResource(getResources(), routertype);
        	 icon.setImageBitmap(mIcon1);
         }else
         {
        	 Bitmap mIcon1 = BitmapFactory.decodeResource(getResources(), R.drawable.gatewaydev);
        	 icon.setImageBitmap(mIcon1);
         }
         
         Button login_ok = (Button)m_Smartrouterloginview.findViewById(R.id.smartrouterlogin_ok);
		  Button login_cancel = (Button)m_Smartrouterloginview.findViewById(R.id.smartrouterlogin_cancel);
		  
		  login_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != m_Smartrouterloginview )
				{
					  
					EditText  username = (EditText)m_Smartrouterloginview.findViewById(R.id.smartrouterusername);
					EditText  password = (EditText)m_Smartrouterloginview.findViewById(R.id.smartrouterpassword);
					SmartRouterStartSession(username.getText().toString(),password.getText().toString());
					
				}
			}
		});
		  
		  login_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TestTask.stopLoginTask();
						if(null != m_ViewFlipper)
						  {
							if(!m_ViewFlipper.isFlipping())
							  {
								m_ViewFlipper.setDisplayedChild(2);	
								SetTitleText();
							  }
							 // m_ViewFlipper.showPrevious();
						  }	
				}
			});
		  
		  
		  username.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					GenieDebug.error("debug", "onTextChanged 0");
					if(null != m_Smartrouterloginview )
					{
						GenieDebug.error("debug", "onTextChanged 1");
						
						Button login_ok = (Button)m_Smartrouterloginview.findViewById(R.id.smartrouterlogin_ok);
						EditText  username = (EditText)m_Smartrouterloginview.findViewById(R.id.smartrouterusername);
						EditText  password = (EditText)m_Smartrouterloginview.findViewById(R.id.smartrouterpassword);
						   
						  
						int number = username.getText().toString().length();
						int number1 = password.getText().toString().length();
						
						if(number > 0 && number1 > 0)
						{
							login_ok.setEnabled(true);
						}else
						{
							login_ok.setEnabled(false);
						}
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
		  
		  password.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					GenieDebug.error("debug", "onTextChanged 0");
					if(null != m_Smartrouterloginview )
					{
						GenieDebug.error("debug", "onTextChanged 1");
						
						Button login_ok = (Button)m_Smartrouterloginview.findViewById(R.id.smartrouterlogin_ok);
						EditText  username = (EditText)m_Smartrouterloginview.findViewById(R.id.smartrouterusername);
						EditText  password = (EditText)m_Smartrouterloginview.findViewById(R.id.smartrouterpassword);
						   
						  
						int number = username.getText().toString().length();
						int number1 = password.getText().toString().length();
						
						if(number > 0 && number1 > 0)
						{
							login_ok.setEnabled(true);
						}else
						{
							login_ok.setEnabled(false);
						}
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
		  
		  
		  password.setOnEditorActionListener(new OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					// TODO Auto-generated method stub
					GenieDebug.error("debug","actionId="+actionId);
					if(null != m_Smartrouterloginview)
					{
						InputMethodManager imm = (InputMethodManager)GenieSmartNetworkLogin.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);    
						
						GenieDebug.error("debug","null != m_Smartnetworklogin");
						Button login_ok = (Button)m_Smartrouterloginview.findViewById(R.id.smartrouterlogin_ok);
						login_ok.setFocusable(true);
						login_ok.requestFocus();
						login_ok.setFocusableInTouchMode(true);
					}
					
					return false;
				}
			  });    
			
			int number = username.getText().toString().length();
			int number1 = password.getText().toString().length();
			if(number > 0 && number1 > 0)
			{
				login_ok.setEnabled(true);
			}else
			{
				login_ok.setEnabled(false);
			}
			
		  if(null != m_ViewFlipper)
		  {
			  if(!m_ViewFlipper.isFlipping())
			  {
				  m_ViewFlipper.setDisplayedChild(3);
				  SetTitleText();
			  }
			  //m_ViewFlipper.showNext();
		  }
		  
	  }
	 
	 private EfficientAdapter_smartrouter smartrouterlistItemAdapter = null;
	 	
	  public void ShowSmartNetworkRouterListView()
	  {
		 
		  if(null != m_smartswitchview)
		  {
			  m_smartswitchview.setVisibility(View.GONE);
		  }
		  
		  
		 
		  
		  SaveSmartNetworkInfo(GenieRequest.m_SmartInfo.username,GenieRequest.m_SmartInfo.password,m_SmartNetworkloginRemember);
		  
		  
		  ListView routerlist = (ListView)m_Smartlistview.findViewById(R.id.routerlist);
		  
		  smartrouterlistItemAdapter = new EfficientAdapter_smartrouter(this);
		  routerlist.setAdapter(smartrouterlistItemAdapter);
		  routerlist.setItemChecked(-1, true);
		  
		  
		  routerlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				if(GenieRequest.m_SmartInfo != null &&
	        			GenieRequest.m_SmartInfo.routerlist != null)
	        	{
					GenieSmartRouterInfo routerinfo = GenieRequest.m_SmartInfo.routerlist.get((int)arg3);
					if(routerinfo != null && routerinfo.active.equals("true"))
					{
						GenieRequest.m_SmartInfo.workcpid = routerinfo.cpid;
						ShowSmartRouterLoginView(routerinfo);
					}
	        	}
			}
		  });

		  if(m_ViewFlipper != null)
		  {
//			  m_ViewFlipper.showNext();
			  if(!m_ViewFlipper.isFlipping())
			  {
				  m_ViewFlipper.setDisplayedChild(2);
				  SetTitleText();
			  }
		  }
	  }
	  

	  private  class EfficientAdapter_smartrouter extends BaseAdapter {
	        private LayoutInflater mInflater;
	        private Bitmap mIcon1;
	        private Context m_context;
	        
	        private Bitmap file_video;
	        private Bitmap file_audio;
	        private Bitmap file_image;

	        public EfficientAdapter_smartrouter(Context context) {
	            // Cache the LayoutInflate to avoid asking for a new one each time.
	            mInflater = LayoutInflater.from(context);
	            m_context = context;

	            // Icons bound to the rows.
	            //mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gatewaydev);
	        }

	        /**
	         * The number of items in the list is determined by the number of speeches
	         * in our array.
	         *
	         * @see android.widget.ListAdapter#getCount()
	         */
	        public int getCount() {
	        	if(GenieRequest.m_SmartInfo != null &&
	        			GenieRequest.m_SmartInfo.routerlist != null)
	        	{
	        		GenieDebug.error("debug","EfficientAdapter_smartrouter GenieRequest.m_SmartInfo.routerlist.size() ="+GenieRequest.m_SmartInfo.routerlist.size());
	        		return GenieRequest.m_SmartInfo.routerlist.size();
	        	}else
	        	{
	        		return 0;
	        	}
	            //return m_listdata.length;
	        }

	        /**
	         * Since the data comes from an array, just returning the index is
	         * sufficent to get at the data. If we were using a more complex data
	         * structure, we would return whatever object represents one row in the
	         * list.
	         *
	         * @see android.widget.ListAdapter#getItem(int)
	         */
	        public Object getItem(int position) {
	            return position;
	            //return m_listdata.get(position);
	        }

	        /**
	         * Use the array index as a unique id.
	         *
	         * @see android.widget.ListAdapter#getItemId(int)
	         */
	        public long getItemId(int position) {
	            return position;
	        }
	        
	       

	        /**
	         * Make a view to hold each row.
	         *
	         * @see android.widget.ListAdapter#getView(int, android.view.View,
	         *      android.view.ViewGroup)
	         */
	        public View getView(int position, View convertView, ViewGroup parent) {
	            // A ViewHolder keeps references to children views to avoid unneccessary calls
	            // to findViewById() on each row.
	            ViewHolder holder;
	            
	            GenieDebug.error("debug","456789 getView 0");
	            
	            // When convertView is not null, we can reuse it directly, there is no need
	            // to reinflate it. We only inflate a new View when the convertView supplied
	            // by ListView is null.
	            //if (convertView == null) {
	            //	GenieDebug.error("debug","456789 getView 1");
	                convertView = mInflater.inflate(R.layout.list_item_smartrouter, null);

	                // Creates a ViewHolder and store references to the two children views
	                // we want to bind data to.
	                holder = new ViewHolder();
	                holder.friendly_name = (TextView) convertView.findViewById(R.id.friendly_name);
	                holder.active = (TextView) convertView.findViewById(R.id.active);
	                holder.serial = (TextView) convertView.findViewById(R.id.serial);
	                holder.icon = (ImageView) convertView.findViewById(R.id.router_icon);
	                holder.model = (TextView) convertView.findViewById(R.id.model);
	                
	                convertView.setTag(holder);
	            //} else {
	            //	GenieDebug.error("debug","456789 getView 2");
	                // Get the ViewHolder back to get fast access to the TextView
	                // and the ImageView.
	            //    holder = (ViewHolder) convertView.getTag();
	            //}
	            
	            //GenieDebug.error("debug", "View getView position= "+position);
	            //GenieDebug.error("debug", "View getView m_renderlistdata.get(position).m_select= "+m_renderlistdata.get(position).m_select);

	            // Bind the data efficiently with the holder.
	            
	            String active = null;
	            String friendly_name = null;
	            String model = null;
	            String serial = null;
	            
	            GenieDebug.error("debug","456789 position ="+position);
	            
	            if(GenieRequest.m_SmartInfo != null &&
	        			GenieRequest.m_SmartInfo.routerlist != null && 
	        			GenieRequest.m_SmartInfo.routerlist.get(position) != null)
	        	{
	            	GenieDebug.error("debug","456789 2position ="+position);
	            	
	            	active = GenieRequest.m_SmartInfo.routerlist.get(position).active;
		            friendly_name = GenieRequest.m_SmartInfo.routerlist.get(position).friendly_name;
		            model = GenieRequest.m_SmartInfo.routerlist.get(position).model;
		            serial = GenieRequest.m_SmartInfo.routerlist.get(position).serial;
	        	}
	            if(friendly_name != null )
	            {
	            	holder.friendly_name.setText(friendly_name);
	            }
	            if(model != null)
	            {
	            	holder.model.setText(model);
	            }
	            
	            if(active != null)
	            {
	            	if(active.equals("true"))
	            	{
	            		holder.active.setText(getResources().getString(R.string.s_online));
	            	}else
	            	{
	            		holder.active.setText(getResources().getString(R.string.s_offline));
	            	}
	            }
	            if(serial != null)
	            {
	            	String ser = String.format("%s:%s",getResources().getString(R.string.s_serial),serial);
	            	holder.serial.setText(ser);
	            }
	            
	            int routertype = GetRouterTypeID(model);
	            if(routertype != -1)
	            {
	            	mIcon1 = BitmapFactory.decodeResource(m_context.getResources(), routertype);
	            	
	            	if(active != null && active.equals("true"))
	            	{
	            		GenieDebug.error("debug","456789 active.equals(\"true\")");
	            		//holder.icon.setBackgroundDrawable(m_context.getResources().getDrawable(routertype));
		            	//holder.icon.getBackground().setAlpha(0);
	            		holder.icon.setImageBitmap(mIcon1);
	            	}else{
	            		GenieDebug.error("debug","456789 active.equals(\"false\")");
	            		holder.icon.setBackgroundDrawable(m_context.getResources().getDrawable(routertype));
		            	holder.icon.getBackground().setAlpha(100);
	            	}
	            }else
	            {
	            	mIcon1 = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.gatewaydev);
	            	holder.icon.setBackgroundResource(R.drawable.gatewaydev);
	            	if(active != null && active.equals("true"))
	            	{
	            		GenieDebug.error("debug","456789 -1 active.equals(\"true\")");
	            		//holder.icon.setBackgroundDrawable(m_context.getResources().getDrawable(R.drawable.gatewaydev));
	            		//holder.icon.getBackground().setAlpha(0);
	            		holder.icon.setImageBitmap(mIcon1);
	            	}else{
	            		GenieDebug.error("debug","456789 -1 active.equals(\"false\")");
	            		holder.icon.setBackgroundDrawable(m_context.getResources().getDrawable(R.drawable.gatewaydev));
	            		holder.icon.getBackground().setAlpha(100);
	            	}

	            }
	            
	            if(active != null)
	            {
	            	if(active.equals("true"))
	            	{
	            		holder.friendly_name.setTextColor(Color.BLACK);
		                holder.active.setTextColor(Color.BLACK);
		                holder.serial.setTextColor(Color.BLACK);		                
		                holder.model.setTextColor(Color.BLACK);
		                convertView.setFocusable(false);
		               // convertView.set
		                
	            	}else
	            	{
	            		holder.friendly_name.setTextColor(Color.GRAY);
		                holder.active.setTextColor(Color.GRAY);
		                holder.serial.setTextColor(Color.GRAY);		                
		                holder.model.setTextColor(Color.GRAY);
		                convertView.setFocusable(true);
	            	}
	            }
	            
	            return convertView;
	        }
	        
	        
	        
	        

	        class ViewHolder {
	        	ImageView icon;
	            TextView friendly_name;
	            TextView serial;
	            TextView active;
	            TextView model;	            
	        }
	    }
	  
	  private int GetRouterTypeID(String routertype )
    {
    	int ret = -1;
    	
    	if(routertype == null)
    		return ret;
    	
    	//routertype = "R6300";
    	
    	routertype = routertype.toUpperCase();
    	
    	//GenieDebug.error("debug","GetRouterTypeID routertype = "+routertype);
    	
    	for(int i = 0;i < m_Routertype.length;i++)
    	{
    		if(routertype.startsWith(m_Routertype[i]))
    		{
    			return image[i];
    		}
    	}
    	
    	return ret;
    }
    
    private String m_Routertype[] = {
			"CG3300",
			"CGD24G",
			"DG834GT",
			"DG834GV",
			"DG834G",
			"DG834N",
			"DG834PN",
			"DG834",
			"DGN1000_RN",
			"DGN2200M",
			"DGN2200",
			"DGN2000",
			"DGN3500",
			"DGNB2100",
			"DGND3300",
			"DGND3700",
			"DM111PSP",
			"DM111P",
			"JWNR2000T",
			"MBM621",
			"MBR624GU",
			"MBR1210-1BMCNS",
			"MBRN3000",
			"RP614",
			"WGR612",
			"WGR614L",
			"WGR614",
			"WGT624",
			"WNB2100",
			"WNDR37AV",
			"WNDR3300",
			"WNDR3400",
			"WNDR3700",
			"WNDR3800",
			"WNDR4000",
			"WNDRMAC",
			"WNR612",
			"WNR834B",
			"WNR834M",
			"WNR854T",
			"WNR1000",
			"WNR2000",
			"WNR2200",
			"WNR3500L",
			"WNR3500",
			"WNXR2000",
			"WPN824EXT",
			"WPN824N",
			"WPN824",
			"WNDR4500",
			"WNDR4700",
			"DGND4000",
			"WNR500",
			"JNR3000",
			"JNR3210",
			"JWNR2000",
			"R6300",
			"R6200",
	};
    
    private  int image[] = {

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
	
    private boolean GetSmartNetworkRouterRemember(String serial)
    {
  	  GenieDebug.error("debug","GetSmartNetworkRouterUsername ");
			SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0); 
			if(settings != null)
			{
			     boolean remember = settings.getBoolean(GenieGlobalDefines.SMARTROUTERREMEMBER+serial,true);
			     return remember;
			}else
			{
				return true;
			}
    }
    
	private String GetSmartNetworkRouterUsername(String serial)
    {
  	  GenieDebug.error("debug","GetSmartNetworkRouterUsername ");
			SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0); 
			if(settings != null)
			{
			     String username = settings.getString(GenieGlobalDefines.SMARTROUTERUSERRNAME+serial,null);
			     return username;
			}else
			{
				return null;
			}
    }
    private String GetSmartNetworkRouterPassword(String serial)
    {
  	  GenieDebug.error("debug","GetSmartNetworkRouterPassword ");
			SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0); 
			if(settings != null)
			{
			     String password = settings.getString(GenieGlobalDefines.SMARTROUTERPASSWORD+serial,null);
			     return password;
			}else
			{
				return null;
			}
    }
    private void SaveSmartNetworkRouterInfo(GenieSmartRouterInfo router,boolean remember)
    {
  	  if(router == null)
  		  return ;
  	  GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.active"+router.active);
  	  GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.cpid"+router.cpid);
  	  GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.friendly_name"+router.friendly_name);
  	  GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.model"+router.model);
  	  GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.owner"+router.owner);
  	  GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.password"+router.password);
  	  GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.username"+router.username);
  	  GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.serial"+router.serial);
  	  GenieDebug.error("debug", "SaveSmartNetworkRouterInfo router.type"+router.type);
  	  
  	  SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0);
	      if(null != settings)
	      {
	    	  settings.edit().putString(GenieGlobalDefines.SMARTROUTERUSERRNAME+router.serial, router.username).commit();
	    	  settings.edit().putString(GenieGlobalDefines.SMARTROUTERPASSWORD+router.serial, router.password).commit();
	    	  settings.edit().putBoolean(GenieGlobalDefines.SMARTROUTERREMEMBER+router.serial, remember).commit();
	      }

    }
    
    private boolean GetSmartNetworkRemember()
    {
  	  GenieDebug.error("debug","GetSmartNetworkUsername ");
			SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0); 
			if(settings != null)
			{
			     boolean remember = settings.getBoolean(GenieGlobalDefines.SMARTREMEMBER,true);
			     return remember;
			}else
			{
				return true;
			}
    }
    
    private String GetSmartNetworkUsername()
    {
  	  GenieDebug.error("debug","GetSmartNetworkUsername ");
			SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0); 
			if(settings != null)
			{
			     String username = settings.getString(GenieGlobalDefines.SMARTUSERRNAME,null);
			     return username;
			}else
			{
				return null;
			}
    }
    private String GetSmartNetworkPassword()
    {
  	  GenieDebug.error("debug","GetSmartNetworkPassword ");
			SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0); 
			if(settings != null)
			{
			     String password = settings.getString(GenieGlobalDefines.SMARTPASSWORD,null);
			     return password;
			}else
			{
				return null;
			}
    }
    private void SaveSmartNetworkInfo(String username,String password,boolean remember)
    {
  	  GenieDebug.error("debug", "SaveSmartNetworkInfo username="+username);
  	  GenieDebug.error("debug", "SaveSmartNetworkInfo password="+password);
  	   
  	  if(username == null || password == null)
  		  return ;
  	  
  	  SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0);
	      if(null != settings)
	      {
	    	  settings.edit().putString(GenieGlobalDefines.SMARTUSERRNAME,username).commit();
	    	  settings.edit().putString(GenieGlobalDefines.SMARTPASSWORD,password).commit();
	    	  settings.edit().putBoolean(GenieGlobalDefines.SMARTREMEMBER,remember).commit();
	      }

    }
    
    private boolean GetSmartNetworkSwitch()
    {
  	  GenieDebug.error("debug","GetSmartNetworkPassword ");
		SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0); 
		if(settings != null)
		{
		     boolean smartswitch = settings.getBoolean(GenieGlobalDefines.SMARTSWITCH,false);
		     return smartswitch;
		}else
		{
			return false;
		}
    }
    private void SaveSmartNetworkSwitch(boolean smartswitch)
    {
  	  GenieDebug.error("debug", "SaveSmartNetworkSwitch smartswitch="+smartswitch);
  	   
  	  
  	  SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0);
	      if(null != settings)
	      {
	    	  settings.edit().putBoolean(GenieGlobalDefines.SMARTSWITCH,smartswitch).commit();
	      }

    }
    
    public String GetLoginPassword()
	{
		GenieDebug.error("debug","settings GetLoginPassword ");
		SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SETTING_INFO, 0); 
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
		 SharedPreferences settings = getSharedPreferences(GenieGlobalDefines.SETTING_INFO, 0);
	     if(null != settings)
	     {
	    	 GenieDebug.error("debug","settings SaveLoginPassword null != settings");
	    	 settings.edit().putString(GenieGlobalDefines.PASSWORD, password).commit();
	     }

	}
    
    
	protected void onStart() {
		// TODO Auto-generated method stub
    	
		super.onStart();
					
		RegisterBroadcastReceiver();
	
	}
	
    public void onDestroy() {
    	super.onDestroy();
    	StopRequest();
    	CloseAlertDialog();
    	UnRegisterBroadcastReceiver();
    	
    }
    
    
    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
    	onBack();
	}

    private AlertDialog m_AlertDialog = null;
    
    private void CloseAlertDialog()
    {
    	if(m_AlertDialog != null)
    	{
    		if(m_AlertDialog.isShowing())
    			m_AlertDialog.dismiss();
    		
    		m_AlertDialog = null;
    	}
    }
    
    private void ShowLoginAlartNotConnectNETGEARRouter()
	{
    	CloseAlertDialog();
    	
    	LayoutInflater mInflater = LayoutInflater.from(GenieSmartNetworkLogin.this);
    	View view = mInflater.inflate(R.layout.notnetgearrouter, null);
    	
    	TextView  notnetgearrouter = (TextView)view.findViewById(R.id.nonetgearrouter);
		notnetgearrouter.setMovementMethod(LinkMovementMethod.getInstance());
    	
    	AlertDialog.Builder  dialog_wifiset =  new AlertDialog.Builder(GenieSmartNetworkLogin.this);
		
		dialog_wifiset.setIcon(R.drawable.icon);
		dialog_wifiset.setTitle(R.string.login);
		dialog_wifiset.setView(view);
		
		dialog_wifiset.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				m_AlertDialog = null;
			}
		});
		
		dialog_wifiset.setCancelable(false);
		
		m_AlertDialog = dialog_wifiset.create();
		m_AlertDialog.show();
		
	}
    
	private void ShowLoginAlartInfoVisible(int id)
	{
    	if(id < 0)
    		return ;
    	
    	CloseAlertDialog();
    	
    	AlertDialog.Builder  dialog_wifiset =  new AlertDialog.Builder(GenieSmartNetworkLogin.this);
		
		dialog_wifiset.setIcon(R.drawable.icon);
		dialog_wifiset.setTitle(R.string.login);
		dialog_wifiset.setMessage(id);
		dialog_wifiset.setPositiveButton(R.string.close,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				m_AlertDialog = null;
			}
		});
		
		
		dialog_wifiset.setCancelable(false);
		m_AlertDialog = dialog_wifiset.create();
		m_AlertDialog.show();
	}
    
    
    public void SaveUserinfo2File(String password)
	{
		
		GenieDebug.error("debug","SaveUserinfo2File password ="+password);
		if(userInfo.isSave == '1')
		{
			SaveLoginPassword(password);
			//GenieShareAPI.strncpy(userInfo.password, password.toCharArray(),0,password.length());
		}
		
		GenieShareAPI.WriteData2File(userInfo,this, GenieGlobalDefines.USER_INFO_FILENAME);
	}
    
    
    private void StopRequest()
    {
  	  if(null != m_Loginrequest)
  	  {
  		m_Loginrequest.Stop();
  		m_Loginrequest = null;
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
			IntentFilter filter = new IntentFilter();//閸掓稑缂揑ntentFilter鐎电钖�
			filter.addAction(GenieGlobalDefines.REQUEST_ACTION_RET_BROADCAST);
			registerReceiver(m_RequestReceiver, filter);//濞夈劌鍞紹roadcast Receiver
	 }

	private class RequestReceiver extends BroadcastReceiver
	{// 缂佈勫閼风嫙roadcastReceiver閻ㄥ嫬鐡欑猾锟�

		@Override
		public void onReceive(Context context, Intent intent)
		{// 闁插秴鍟搊nReceive閺傝纭�

			String lable = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_LABLE);
			RequestActionType ActionType = (RequestActionType) intent
			        .getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_TYPE);
			RequestResultType aResultType = (RequestResultType) intent
			        .getSerializableExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESULTTYPE);
			String aServer = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SERVER);
			String aMethod = intent.getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_METHOD);
			String aResponseCode = intent
			        .getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSECODE);
			int aHttpResponseCode = intent.getIntExtra(
			        GenieGlobalDefines.REQUEST_ACTION_RET_HTTPRESPONSECODE, -2);
			int aHttpType = intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_HTTP_TYPE, -2);
			int aSoapType = intent.getIntExtra(GenieGlobalDefines.REQUEST_ACTION_RET_SOAP_TYPE, -2);
			String aResponse = intent
			        .getStringExtra(GenieGlobalDefines.REQUEST_ACTION_RET_RESPONSE);

			GenieDebug.error("debug", "RequestReceiver onReceive lable =" + lable);
			// Allen add for test Login
			StringBuffer resultBuf = new StringBuffer("" + lable + "#" + ActionType + "#"
			        + aResultType + "#" + aServer + "#" + aMethod + "#" + aResponseCode + "#"
			        + aHttpResponseCode + "#" + aHttpType + "#" + aResponse);
			// Allen add for test login end

			if (lable != null && lable.equals("GenieSmartNetworkLogin")) //登陆
			{
				GenieDebug.error("debug", "RequestReceiver onReceive aServer =" + aServer);
				GenieDebug.error("debug", "RequestReceiver onReceive aMethod =" + aMethod);
				GenieDebug.error("debug", "RequestReceiver onReceive aResponseCode ="
				        + aResponseCode);
				GenieDebug.error("debug", "RequestReceiver onReceive aHttpResponseCode ="
				        + aHttpResponseCode);
				GenieDebug.error("debug", "RequestReceiver onReceive aResponse =" + aResponse);
				GenieDebug.error("debug", "RequestReceiver onReceive ActionType =" + ActionType);
				GenieDebug.error("debug", "RequestReceiver onReceive aResultType =" + aResultType);
				if (null != ActionType && ActionType == RequestActionType.Http) //http
				{
					GenieDebug.error("debug", "GenieLoginDialog onReceive aHttpType =" + aHttpType);
					resultBuf.append("->into http");
					if (aHttpType == GenieGlobalDefines.EHttpGetCurrentSetting) //获取默认值
					{
						resultBuf.append("->into getcurrentsetting");
						TestTask.setRouterName(aResponse); //Allen add for test login
						if (null != aResultType)
						{
							resultBuf.append("->into result type not null");
							if (aResultType == RequestResultType.Succes)
							{
								resultBuf.append("->into success");
								String isgenie = GenieSoap.dictionary
								        .get(GenieGlobalDefines.DICTIONARY_KEY_IS_GENIE); // ,
								                                                          // GenieGlobalDefines.ROUTER_TYPE_OLD_NETGEAR);
								String mFV = GenieSoap.dictionary
								        .get(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION);
								String mMN = GenieSoap.dictionary
								        .get(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME);
								String InternetStatus = GenieSoap.dictionary
								        .get(GenieGlobalDefines.DICTIONARY_KEY_INTERNET_STATUS);
								String LpcSupport = GenieSoap.dictionary
								        .get(GenieGlobalDefines.DICTIONARY_KEY_LPC_SUPPORTED);

								GenieDebug.error("debug",
								        "GenieLoginDialog CurrentSetting isgenie =" + isgenie);
								GenieDebug.error("debug", "GenieLoginDialog CurrentSetting mFV ="
								        + mFV);
								GenieDebug.error("debug", "GenieLoginDialog CurrentSetting mMN ="
								        + mMN);
								GenieDebug.error("debug",
								        "GenieLoginDialog CurrentSetting InternetStatus ="
								                + InternetStatus);
								GenieDebug
								        .error("debug",
								                "GenieLoginDialog CurrentSetting LpcSupport ="
								                        + LpcSupport);
								if (null != isgenie)
								{
									resultBuf.append("->into isgenie");
									if (isgenie.equals(GenieGlobalDefines.ROUTER_TYPE_NEW_NETGEAR)
									        || isgenie
									                .equals(GenieGlobalDefines.ROUTER_TYPE_OLD_NETGEAR))
									{
										resultBuf.append("->into router genie");

										if (null != m_localpassword)
										{
											// Allen add remark: after connnect
											// for login router
											AuthenticateUserAndPassword("admin", m_localpassword
											        .getText().toString());
										}
									}
									else
									{
										resultBuf.append("->into not router genie");
										ShowLoginAlartNotConnectNETGEARRouter();

									}
								}
								else
								{
									resultBuf.append("->into not genie");
									ShowLoginAlartNotConnectNETGEARRouter();
								}
							}
							else
							{
								resultBuf.append("->into faile");
								SetLoginResult(-1, resultBuf.toString());
								ShowLoginAlartNotConnectNETGEARRouter();
							}
						}
					}
				}
				else if (null != ActionType && ActionType == RequestActionType.Soap) //soap消息 
				{
					resultBuf.append("->into soap");
					if (aSoapType == GenieGlobalDefines.ESoapRequestVerityUser) //验证用户
					{
						resultBuf.append("->into verity user");
						if (aResultType != null)
						{
							resultBuf.append("->into result not null");
							if (aResultType == RequestResultType.Succes
							        && (null != aResponse && aResponse.indexOf("000") != -1)) //返回成功
							{
								resultBuf.append("->into success");
								if (null != m_localpassword) //保存密码
								{
									SaveUserinfo2File(m_localpassword.getText().toString());
								}
								GenieRequest.m_SmartNetWork = false;

								if (clickid == -1)
								{
									// SetLoginResult(GenieGlobalDefines.LOCALLOGIN_RESULT_SUCEESS);//allen
									// add remark succccccccccc
								}
								else
								{
									// SetLoginResult(clickid);//allen add
									// remark succccccccccc
								} // Allen add for login test
								SetLoginResult(1, "login success");
							}
							else //失败
							{
								resultBuf.append("->into faile");
								int ResponseCode = 0;
								try
								{
									ResponseCode = Integer.valueOf(aResponseCode);
								}
								catch (Exception e)
								{
									// TODO: handle exception
									e.printStackTrace();
								}
								if (ResponseCode == 401)
								{
									ShowLoginAlartInfoVisible(R.string.bad_password);
									resultBuf.append("#password is not right");
								}
								else
								{
									if (GenieRequest.m_RequestPort == 80)
									{
										GenieRequest.m_RequestPort = 5000;
									}
									else
									{
										GenieRequest.m_RequestPort = 80;
									}
									// 超时两次 重新 请求Session
									if (timeout == 1)
									{
										if (GenieMainView.logingeniemainview != null)
										{
											GenieMainView.logingeniemainview.Initdata();
											GenieRequest.m_RequestPort = 5000;
											timeout = 0;
										}
									}
									timeout += 1;
									ShowLoginAlartInfoVisible(R.string.actiontimeout);

									resultBuf.append("#time is out");
								}
								// Allen add for login test
								SetLoginResult(-1, resultBuf.toString());
							}
						}
					}
					else if (aSoapType == GenieGlobalDefines.ESoapRequestRouterInfo) //获取路由信息
					{
						resultBuf.append("->into router info");
						if (clickid == -1)
						{// SetLoginResult(GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_SUCEESS);
							SetLoginResult(1, resultBuf.toString() ); // Allen modify
																// for login
																// test
						}
						else
							// {/SetLoginResult(clickid);
							SetLoginResult(1, resultBuf.toString()); // Allen modify
																// for login
																// test
					}
				}
			}
			else if (null != ActionType && ActionType == RequestActionType.SmartNetWork) //联网~~
			{
				resultBuf.append("->into smartnetwork");

				int aSmartType = intent.getIntExtra(
				        GenieGlobalDefines.REQUEST_ACTION_RET_SMART_TYPE, -2);

				GenieDebug.error("debug", "aSmartType = " + aSmartType);

				switch (aSmartType)
				{
					case GenieGlobalDefines.ESMARTNETWORKAUTHENTICATE: //验证
						resultBuf.append("->into auth network");
						if (null != aResultType && aResultType != RequestResultType.Succes)
						{
							resultBuf.append("->into success");
							if (aResultType == RequestResultType.Unauthorized)
							{
								//ShowLoginAlartInfoVisible(R.string.s_invaliduserpass);
								resultBuf.append(R.string.s_invaliduserpass);
							}
							else
							{
								//ShowLoginAlartInfoVisible(R.string.s_network_error);
								resultBuf.append(R.string.s_network_error);
							}
							// Allen add for login test
							SetLoginResult(-1, resultBuf.toString());
						}
						break;
					case GenieGlobalDefines.ESMARTNETWORK_GETROUTERLIST: //获得路由列表
						resultBuf.append("->into network routerlist");
						int id = 0;
						if (null != m_ViewFlipper)
						{
							id = m_ViewFlipper.getDisplayedChild();
						}
						if (id != 2)
						{
							if (GenieRequest.m_SmartInfo != null
							        && GenieRequest.m_SmartInfo.routerlist != null
							        && GenieRequest.m_SmartInfo.routerlist.size() > 0)
							{
								ShowSmartNetworkRouterListView();
							}
							else
							{
								SaveSmartNetworkInfo(GenieRequest.m_SmartInfo.username,
								        GenieRequest.m_SmartInfo.password,
								        m_SmartNetworkloginRemember);
								ShowLoginAlartInfoVisible(R.string.s_smart_nodevice);
							}
						}
						else
						{
							if (null != aResultType && aResultType == RequestResultType.Succes)
							{
								if (GenieRequest.m_SmartInfo != null
								        && GenieRequest.m_SmartInfo.routerlist != null
								        && GenieRequest.m_SmartInfo.routerlist.size() > 0)
								{
									if (null != smartrouterlistItemAdapter)
									{
										smartrouterlistItemAdapter.notifyDataSetChanged();
									}
									// ShowSmartNetworkRouterListView();
								}
								else
								{
									// SaveSmartNetworkInfo(GenieRequest.m_SmartInfo.username,GenieRequest.m_SmartInfo.password,m_SmartNetworkloginRemember);
									ShowLoginAlartInfoVisible(R.string.s_smart_nodevice);
								}
							}
							else
							{
								ShowLoginAlartInfoVisible(R.string.s_network_error);
							}
						}
						break;
					case GenieGlobalDefines.ESMARTNETWORK_StartRouterSession: //路由缓存
						resultBuf.append("->into start router session");
						if (null != aResultType && aResultType == RequestResultType.Succes)
						{
							resultBuf.append("->into success");
							GenieRequest.m_SmartNetWork = true;

							SaveSmartNetworkRouterInfo(
							        GenieRequest.m_SmartInfo
							                .GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid),
							        m_SmartNetworkRouterRemember);
							// GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_MODE_NAME,GenieRequest.m_SmartInfo.GetWorkSmartRouterInfo(GenieRequest.m_SmartInfo.workcpid).model);
							// GenieSoap.dictionary.put(GenieGlobalDefines.DICTIONARY_KEY_FIRMWARE_VERSION,GenieGlobalDefines.NULL);

							GetSmartRouterInfo();
							// if(clickid == -1)
							// {
							// SetLoginResult(GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_SUCEESS);
							// }else
							// {
							// SetLoginResult(clickid);
							// }
						}
						else
						{
							resultBuf.append("->into faile");
							int errorcode = intent.getIntExtra(
							        GenieGlobalDefines.REQUEST_ACTION_RET_ERROR_CODE, -2);
							if (null != aResultType && aResultType == RequestResultType.error
							        && errorcode == 17)
							{
								GenieRequest.m_SmartNetWork = false;
								ShowLoginAlartInfoVisible(R.string.bad_password);
							}
							else
							{
								GenieRequest.m_SmartNetWork = false;
								ShowLoginAlartInfoVisible(R.string.s_network_error);
							}
						}
						break;
					case GenieGlobalDefines.ESMARTNETWORK_EndRouterSession: //结束缓存
						resultBuf.append("->into end session");
						if (GenieRequest.m_SmartInfo != null
						        && GenieRequest.m_SmartInfo.sessionid != null)
						{
							GenieRequest.m_SmartInfo.sessionid = null;
						}
						GenieRequest.m_SmartNetWork = false;
						// SetLoginResult(GenieGlobalDefines.SMARTNETWORKLOGIN_RESULT_FAILED);
						//SetLoginResult(-1, resultBuf.toString());
						break;
					default:
						break;
				}
			}
			//Allen add for login test : save procedure info
			TestTask.saveLoginInfo(resultBuf.toString());
		}

	}

	//Allen add for login test
	private static Handler mHandler;
	private TextView tvResult;

	private void initHandler()
	{ 
		if(mHandler == null)
		{
			mHandler = new Handler()
			{
				public void handleMessage(Message msg)
				{
					try
					{
						switch (msg.what)
						{
							case 0: 
							{
								tvResult.setText(TestTask.getLoginResult());
//								SmartNetWorkInitAndAuth(m_smartusernameview.getText().toString(),
//										m_smartpasswordview.getText().toString());
								GetCurrentSetting();
								break;
							} 
						}

						super.handleMessage(msg);
					}
					catch (Exception e)
					{
						GenieDebug.error("login fail~~:", e.getMessage());
					}
				}
			};
		}
	}
}
